package io.luwian.cli.commands;

import io.luwian.cli.util.TemplateEngine;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
        name = "new",
        description = "Create new Luwian resources",
        subcommands = {NewServiceCommand.Service.class})
public class NewServiceCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Specify a subcommand (e.g., 'luwian new service').");
    }

    @Command(name = "service", description = "Create a hello-only Spring Boot service")
    public static class Service implements Runnable {

        @Parameters(
                index = "0",
                paramLabel = "<name>",
                description = "Service name (folder & artifactId)")
        String name;

        @Option(
                names = "--pkg",
                required = true,
                description = "Base package, e.g., com.acme.orders")
        String pkg;

        @Option(
                names = "--http-port",
                defaultValue = "8080",
                description = "HTTP port (default: 8080)")
        int httpPort;

        @Option(
                names = "--dir",
                defaultValue = ".",
                description = "Target directory (default: current)")
        Path targetDir;

        @Option(names = "--force", description = "Overwrite existing files without prompt")
        boolean force;

        @Override
        public void run() {
            try {
                Path serviceRoot = targetDir.resolve(name);
                Files.createDirectories(serviceRoot);

                String pkgPath = pkg.replace('.', '/');
                Map<String, String> model = new HashMap<>();
                model.put("service", name);
                model.put("pkg", pkg);
                model.put("pkgPath", pkgPath);
                model.put("httpPort", Integer.toString(httpPort));

                System.out.println("Scaffolding service at: " + serviceRoot.toAbsolutePath());
                String index = "templates/new/service/spring/hello/_index.txt";
                String templateBase = "templates/new/service/spring/hello/";

                TemplateEngine engine = new TemplateEngine(force);
                engine.renderFromClasspath(index, templateBase, serviceRoot, model);

                System.out.println("Done. Next:");
                System.out.println("  cd " + name + " && mvn spring-boot:run");
                System.out.println("  -> http://localhost:" + httpPort + "/hello");
            } catch (IOException e) {
                throw new RuntimeException("Failed to scaffold service", e);
            }
        }
    }
}
