package io.luwian.cli.commands;

import io.luwian.cli.util.TemplateUtils;
import io.luwian.cli.util.YamlUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
        name = "add",
        description = "Add features to an existing service",
        subcommands = {AddHandlerCommand.Handler.class})
public class AddHandlerCommand {

    @Command(
            name = "handler",
            description = "Generate a REST handler (controller and optional DTOs/tests)")
    public static class Handler implements Runnable {

        @Parameters(
                index = "0",
                paramLabel = "Name",
                description = "Logical name, e.g. Order or Checkout")
        String name;

        @Option(
                names = {"--pkg"},
                required = true,
                description = "Base package, e.g. com.acme.shop")
        String pkg;

        @Option(
                names = {"--path"},
                required = true,
                description = "HTTP path, e.g. /api/orders")
        String path;

        @Option(
                names = {"--method"},
                defaultValue = "GET",
                description = "HTTP method (GET, POST, PUT, DELETE, PATCH)")
        String method;

        @Option(
                names = {"--produces"},
                defaultValue = "application/json",
                description = "Produces MIME type")
        String produces;

        @Option(
                names = {"--consumes"},
                defaultValue = "application/json",
                description = "Consumes MIME type")
        String consumes;

        @Option(
                names = {"--status"},
                defaultValue = "200",
                description = "HTTP status code")
        int status;

        @Option(
                names = {"--dto-in"},
                description = "Request DTO simple name, e.g. CreateOrderRequest")
        String dtoIn;

        @Option(
                names = {"--dto-out"},
                description = "Response DTO simple name, e.g. OrderResponse")
        String dtoOut;

        @Option(
                names = {"--with-test"},
                defaultValue = "true",
                description = "Generate a MockMvc slice test")
        boolean withTest;

        @Option(
                names = {"--timed"},
                defaultValue = "false",
                description = "Annotate handler method with @Timed")
        boolean timed;

        @Option(
                names = {"--arch"},
                defaultValue = "inherit",
                description = "Architecture: inherit|layered|hex")
        String arch;

        @Option(
                names = {"--dry-run"},
                defaultValue = "false",
                description = "Print planned changes, write nothing")
        boolean dryRun;

        @Option(
                names = {"--force"},
                defaultValue = "false",
                description = "Overwrite existing files")
        boolean force;

        @Option(
                names = {"--root"},
                description = "Project root (defaults to current directory)")
        Path root;

        @Override
        public void run() {
            try {
                Path projectRoot =
                        root != null
                                ? root.toAbsolutePath()
                                : Paths.get(".").toAbsolutePath().normalize();
                String resolvedArch = resolveArch(projectRoot, arch);
                validateHttpMethod(method);

                String pkgPath = pkg.replace('.', '/');
                String namePascal = capitalize(name);
                String controllerName = namePascal + "Controller";

                Map<String, String> vars = new LinkedHashMap<>();
                vars.put("pkg", pkg);
                vars.put("Name", namePascal);
                vars.put("name", lowerFirst(namePascal));
                vars.put("controllerName", controllerName);
                vars.put("path", path);
                vars.put("method", method.toUpperCase(Locale.ROOT));
                vars.put("produces", produces);
                vars.put("consumes", consumes);
                vars.put("status", Integer.toString(status));
                vars.put("dtoIn", nvl(dtoIn, "Void"));
                vars.put("dtoOut", nvl(dtoOut, "HelloResponse"));
                vars.put("date", LocalDateTime.now().toString());
                vars.put("timedAnnotation", timed ? "@io.micrometer.core.annotation.Timed" : "");
                vars.put("timedImport", timed ? "import io.micrometer.core.annotation.Timed;" : "");

                // layout locations by arch
                Path javaMain = findMainJava(projectRoot);
                Path testJava = findTestJava(projectRoot);
                Path baseDir;
                List<PlannedFile> files = new ArrayList<>();

                if ("layered".equals(resolvedArch)) {
                    baseDir = javaMain.resolve(pkgPath);
                    Path webDir = baseDir.resolve("web");
                    Path dtoDir = webDir.resolve("dto");
                    Path svcDir = baseDir.resolve("service");

                    files.add(
                            new PlannedFile(
                                    webDir.resolve(controllerName + ".java"),
                                    "templates/add/handler/layered/Controller.java.mustache",
                                    vars));

                    if (dtoOut != null) {
                        files.add(
                                new PlannedFile(
                                        dtoDir.resolve(vars.get("dtoOut") + ".java"),
                                        "templates/add/handler/layered/DtoOut.java.mustache",
                                        vars));
                    }
                    if (dtoIn != null) {
                        files.add(
                                new PlannedFile(
                                        dtoDir.resolve(vars.get("dtoIn") + ".java"),
                                        "templates/add/handler/layered/DtoIn.java.mustache",
                                        vars));
                    }
                    files.add(
                            new PlannedFile(
                                    svcDir.resolve(namePascal + "Service.java"),
                                    "templates/add/handler/layered/Service.java.mustache",
                                    vars));

                    if (withTest) {
                        Path testPkg = testJava.resolve(pkgPath).resolve("web");
                        files.add(
                                new PlannedFile(
                                        testPkg.resolve(controllerName + "Test.java"),
                                        "templates/add/handler/layered/ControllerTest.java.mustache",
                                        vars));
                    }
                } else if ("hex".equals(resolvedArch)) {
                    baseDir = javaMain.resolve(pkgPath);
                    Path portsIn = baseDir.resolve("ports/in");
                    Path app = baseDir.resolve("app");
                    Path adaptersWeb = baseDir.resolve("adapters/in/web");
                    Path dtoDir = adaptersWeb.resolve("dto");

                    files.add(
                            new PlannedFile(
                                    portsIn.resolve(namePascal + "UseCase.java"),
                                    "templates/add/handler/hex/UseCase.java.mustache",
                                    vars));
                    files.add(
                            new PlannedFile(
                                    app.resolve(namePascal + "Service.java"),
                                    "templates/add/handler/hex/AppService.java.mustache",
                                    vars));
                    files.add(
                            new PlannedFile(
                                    adaptersWeb.resolve(controllerName + ".java"),
                                    "templates/add/handler/hex/Controller.java.mustache",
                                    vars));

                    if (dtoOut != null) {
                        files.add(
                                new PlannedFile(
                                        dtoDir.resolve(vars.get("dtoOut") + ".java"),
                                        "templates/add/handler/hex/DtoOut.java.mustache",
                                        vars));
                    }
                    if (dtoIn != null) {
                        files.add(
                                new PlannedFile(
                                        dtoDir.resolve(vars.get("dtoIn") + ".java"),
                                        "templates/add/handler/hex/DtoIn.java.mustache",
                                        vars));
                    }
                    if (withTest) {
                        Path testPkg = testJava.resolve(pkgPath).resolve("adapters/in/web");
                        files.add(
                                new PlannedFile(
                                        testPkg.resolve(controllerName + "Test.java"),
                                        "templates/add/handler/hex/ControllerTest.java.mustache",
                                        vars));
                    }
                } else {
                    throw new IllegalArgumentException("Unsupported arch: " + resolvedArch);
                }

                // Dry-run display
                if (dryRun) {
                    System.out.println("Would create/update files:");
                    for (PlannedFile f : files) {
                        System.out.println(" - " + projectRoot.relativize(f.target));
                    }
                    return;
                }

                // Write files
                for (PlannedFile f : files) {
                    writeFromTemplate(projectRoot, f, force);
                }

                System.out.printf(
                        "✅ Added handler '%s' (%s) at %s%n", namePascal, resolvedArch, path);

            } catch (IOException | IllegalArgumentException | IllegalStateException e) {
                System.err.println("❌ " + e.getMessage());
                if (Boolean.getBoolean("luwian.cli.debug")) {
                    e.printStackTrace();
                }
            }
        }

        private static String resolveArch(Path root, String archOpt) throws IOException {
            if (!"inherit".equalsIgnoreCase(archOpt)) return archOpt.toLowerCase(Locale.ROOT);
            Path cfg = root.resolve(".luwian.yml");
            if (Files.exists(cfg)) {
                Map<String, String> map = YamlUtils.readSimpleKeyValues(cfg);
                String a = map.getOrDefault("arch", "layered");
                return a.toLowerCase(Locale.ROOT);
            }
            return "layered";
        }

        private static void writeFromTemplate(Path root, PlannedFile pf, boolean force)
                throws IOException {
            if (Files.exists(pf.target) && !force) {
                throw new IOException(
                        "File exists: "
                                + root.relativize(pf.target)
                                + " (use --force to overwrite)");
            }
            Files.createDirectories(pf.target.getParent());
            String tpl = TemplateUtils.loadClasspath(pf.templatePath);
            String rendered = TemplateUtils.render(tpl, pf.vars);
            Files.writeString(pf.target, rendered);
        }

        private static Path findMainJava(Path root) {
            Path p = root.resolve("src/main/java");
            if (Files.exists(p)) return p;
            throw new IllegalStateException("Cannot find src/main/java under " + root);
        }

        private static Path findTestJava(Path root) {
            Path p = root.resolve("src/test/java");
            if (Files.exists(p)) return p;
            throw new IllegalStateException("Cannot find src/test/java under " + root);
        }

        private static void validateHttpMethod(String m) {
            Set<String> ok = Set.of("GET", "POST", "PUT", "DELETE", "PATCH");
            if (!ok.contains(m.toUpperCase(Locale.ROOT))) {
                throw new IllegalArgumentException("Unsupported method: " + m);
            }
        }

        private static String nvl(String v, String def) {
            return (v == null || v.isBlank()) ? def : v;
        }

        private static String capitalize(String s) {
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }

        private static String lowerFirst(String s) {
            return s.substring(0, 1).toLowerCase() + s.substring(1);
        }

        private static class PlannedFile {
            final Path target;
            final String templatePath;
            final Map<String, String> vars;

            PlannedFile(Path target, String templatePath, Map<String, String> vars) {
                this.target = target;
                this.templatePath = templatePath;
                this.vars = vars;
            }
        }
    }
}
