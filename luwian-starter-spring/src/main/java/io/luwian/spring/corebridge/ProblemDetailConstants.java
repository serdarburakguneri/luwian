package io.luwian.spring.corebridge;

/** Constants for ProblemDetail creation and error handling. */
public final class ProblemDetailConstants {

    private ProblemDetailConstants() {} // Utility class

    // Error Base URL
    public static final String ERROR_BASE_URL = "https://errors.luwian.io";

    // ProblemDetail Properties
    public static final String TIMESTAMP_PROPERTY = "timestamp";
    public static final String ERROR_CODE_PROPERTY = "errorCode";
    public static final String CORRELATION_ID_PROPERTY = "correlationId";

    // Error Detail Messages
    public static final String BAD_REQUEST_DETAIL =
            "Request body contains invalid or unsupported values.";
    public static final String NOT_FOUND_DETAIL = "Requested resource was not found.";
    public static final String FORBIDDEN_DETAIL = "You are not allowed to perform this operation.";
    public static final String CONFLICT_DETAIL =
            "Request cannot be completed due to resource state.";
    public static final String METHOD_NOT_ALLOWED_DETAIL =
            "HTTP method is not allowed for this endpoint.";
    public static final String INTERNAL_ERROR_DETAIL = "An unexpected error occurred.";

    // HTTP Status Codes
    public static final int BAD_REQUEST_STATUS = 400;
    public static final int FORBIDDEN_STATUS = 403;
    public static final int NOT_FOUND_STATUS = 404;
    public static final int CONFLICT_STATUS = 409;
    public static final int METHOD_NOT_ALLOWED_STATUS = 405;
}
