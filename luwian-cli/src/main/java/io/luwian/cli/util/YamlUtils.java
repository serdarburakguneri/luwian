package io.luwian.cli.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** YAML kv parser for .luwian.yml (format: key: value). */
public final class YamlUtils {
    private YamlUtils() {}

    public static Map<String, String> readSimpleKeyValues(Path path) throws IOException {
        Map<String, String> m = new LinkedHashMap<>();
        for (String line : Files.readAllLines(path)) {
            String s = line.trim();
            if (s.isEmpty() || s.startsWith("#")) continue;
            int idx = s.indexOf(':');
            if (idx > 0) {
                String k = s.substring(0, idx).trim();
                String v = s.substring(idx + 1).trim();
                if (v.startsWith("\"") && v.endsWith("\"") && v.length() >= 2) {
                    v = v.substring(1, v.length() - 1);
                }
                m.put(k, v);
            }
        }
        return m;
    }
}
