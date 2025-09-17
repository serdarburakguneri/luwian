package io.luwian.core.config;

import java.util.Optional;

/** Read-only config facade; starters map platform config to this. */
public interface LuwianConfig {
    boolean loggingJson();
    boolean loggingHttpBody();
    String tenancyHeader();
    StacktracePolicy errorIncludeStacktrace();

    enum StacktracePolicy { NEVER, ON_TRACE }

    interface Source {
        Optional<String> get(String key);
        default boolean getBool(String key, boolean def) {
            return get(key).map(v -> "true".equalsIgnoreCase(v)).orElse(def);
        }
    }
}
