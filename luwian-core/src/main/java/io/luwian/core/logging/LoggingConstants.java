package io.luwian.core.logging;

/** Constants for logging configuration and field names. */
public final class LoggingConstants {

    private LoggingConstants() {}

    // HTTP Logging Field Names
    public static final String TIMESTAMP_FIELD = "ts";
    public static final String DURATION_MS_FIELD = "duration_ms";
    public static final String METHOD_FIELD = "method";
    public static final String PATH_FIELD = "path";
    public static final String QUERY_FIELD = "query";
    public static final String STATUS_FIELD = "status";
    public static final String REQ_HEADERS_FIELD = "req_headers";
    public static final String RES_HEADERS_FIELD = "res_headers";
    public static final String REQ_BODY_FIELD = "req_body";
    public static final String RES_BODY_FIELD = "res_body";

    // HTTP Logging Message
    public static final String HTTP_LOG_MESSAGE = "http {}";

    // Redaction
    public static final String REDACTED_VALUE = "***";
    public static final String TRUNCATED_SUFFIX = "...(truncated)";

    // Sensitive Headers
    public static final String AUTHORIZATION_HEADER = "authorization";
    public static final String COOKIE_HEADER = "cookie";
    public static final String SET_COOKIE_HEADER = "set-cookie";
    public static final String PROXY_AUTHORIZATION_HEADER = "proxy-authorization";

    // Body Truncation
    public static final int MAX_BODY_LENGTH = 4096;
}
