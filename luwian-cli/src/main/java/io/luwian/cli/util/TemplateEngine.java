package io.luwian.cli.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.text.StringSubstitutor;

/**
 * - Reads an index file from classpath that lists template file paths (relative to classpath root)
 * - For each file, loads the resource and performs {{var}} replacement
 * - Writes to the target directory mirroring the source structure
 *
 * Supported placeholder syntax: {{var}} (implemented on top of StringSubstitutor with ${var})
 */
public class TemplateEngine {

    private final boolean force;

    public TemplateEngine(boolean force) {
        this.force = force;
    }

    public void renderFromClasspath(String indexResource, String templateBase, Path targetRoot, Map<String, String> model) throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream in = cl.getResourceAsStream(indexResource)) {
            if (in == null) throw new IOException("Missing resource index: " + indexResource);
            List<String> paths = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .lines().filter(s -> !s.isBlank() && !s.startsWith("#"))
                    .collect(Collectors.toList());
            for (String rel : paths) {
                String templatePath = templateBase + rel;
                String content = readResourceAsString(templatePath);
                String rendered = substitute(content, model);
                
                String renderedPath = substitute(rel, model);                
               
                if (renderedPath.endsWith(".mustache")) {
                    renderedPath = renderedPath.substring(0, renderedPath.length() - 9);
                }

                Path out = targetRoot.resolve(renderedPath);
                Files.createDirectories(out.getParent());
                if (Files.exists(out) && !force) {
                    throw new IOException("File exists (use --force to overwrite): " + out);
                }
                Files.writeString(out, rendered, StandardCharsets.UTF_8);
                System.out.println("  + " + targetRoot.relativize(out));
            }
        }
    }

    private String readResourceAsString(String path) throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream in = cl.getResourceAsStream(path)) {
            if (in == null) throw new IOException("Missing template: " + path);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String substitute(String text, Map<String, String> model) {       
        String normalized = text.replace("{{", "${").replace("}}", "}");
        Map<String, String> m = new HashMap<>(model);       
        return new StringSubstitutor(m).replace(normalized);
    }
}
