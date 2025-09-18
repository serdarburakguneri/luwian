package io.luwian.core.config;

/** Constants for configuration keys and default values. */
public final class ConfigConstants {

    private ConfigConstants() {}

    // Configuration Keys
    public static final String LOGGING_JSON_KEY = "luwian.logging.json";
    public static final String LOGGING_HTTP_BODY_KEY = "luwian.logging.http.body";
    public static final String LOGGING_TENANCY_HEADER_KEY = "luwian.logging.tenancy-header";
    public static final String ERROR_INCLUDE_STACKTRACE_KEY = "luwian.error.include-stacktrace";

    // Default Values
    public static final String DEFAULT_TENANCY_HEADER = "X-Tenant";
    public static final String STACKTRACE_NEVER_VALUE = "never";
    public static final String STACKTRACE_ON_TRACE_VALUE = "on-trace";
    public static final String TRUE_VALUE = "true";
}
