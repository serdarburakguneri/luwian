package io.luwian.cli.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class TemplateUtils {
    private TemplateUtils() {}

    public static String loadClasspath(String path) throws IOException {
        var in = TemplateUtils.class.getClassLoader().getResourceAsStream(path);
        if (in == null) throw new IOException("Template not found on classpath: " + path);
        try (in;
                var r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line).append('\n');
            return sb.toString();
        }
    }

    public static String render(String template, Map<String, String> vars) {
        String out = template;
        for (var e : vars.entrySet()) {
            out = out.replace("{{" + e.getKey() + "}}", e.getValue());
        }
        return out;
    }
}
