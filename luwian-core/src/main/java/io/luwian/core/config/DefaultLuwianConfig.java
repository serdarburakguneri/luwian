package io.luwian.core.config;

import java.util.Map;
import java.util.Optional;

/** Simple map-backed LuwianConfig implementation. */
public class DefaultLuwianConfig implements LuwianConfig {

    private final Map<String, String> map;

    public DefaultLuwianConfig(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public boolean loggingJson() {
        return bool(ConfigConstants.LOGGING_JSON_KEY, true);
    }

    @Override
    public boolean loggingHttpBody() {
        return bool(ConfigConstants.LOGGING_HTTP_BODY_KEY, false);
    }

    @Override
    public String tenancyHeader() {
        return map.getOrDefault(
                ConfigConstants.LOGGING_TENANCY_HEADER_KEY, ConfigConstants.DEFAULT_TENANCY_HEADER);
    }

    @Override
    public StacktracePolicy errorIncludeStacktrace() {
        return ConfigConstants.STACKTRACE_NEVER_VALUE.equalsIgnoreCase(
                        map.getOrDefault(
                                ConfigConstants.ERROR_INCLUDE_STACKTRACE_KEY,
                                ConfigConstants.STACKTRACE_ON_TRACE_VALUE))
                ? StacktracePolicy.NEVER
                : StacktracePolicy.ON_TRACE;
    }

    private boolean bool(String key, boolean def) {
        return Optional.ofNullable(map.get(key))
                .map(v -> ConfigConstants.TRUE_VALUE.equalsIgnoreCase(v))
                .orElse(def);
    }
}
