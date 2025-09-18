package io.luwian.core.error;

/** Constants for error handling and error codes. */
public final class ErrorConstants {

    private ErrorConstants() {}

    // Error Codes
    public static final String INTERNAL_ERROR_CODE = "LUW-INT-000";
    public static final String NOT_FOUND_CODE = "LUW-NOT-001";
    public static final String BAD_REQUEST_CODE = "LUW-REQ-001";
    public static final String INVALID_STATE_CODE = "LUW-STA-001";
    public static final String FORBIDDEN_CODE = "LUW-SEC-001";
    public static final String ACCESS_DENIED_CODE = "LUW-SEC-002";
    public static final String METHOD_NOT_ALLOWED_CODE = "LUW-MTH-001";

    // Error Titles
    public static final String INTERNAL_ERROR_TITLE = "Internal server error";
    public static final String NOT_FOUND_TITLE = "Resource not found";
    public static final String BAD_REQUEST_TITLE = "Bad request";
    public static final String INVALID_STATE_TITLE = "Invalid state";
    public static final String FORBIDDEN_TITLE = "Forbidden";
    public static final String METHOD_NOT_ALLOWED_TITLE = "Method not allowed";

    // Error Type Paths
    public static final String NOT_FOUND_TYPE_PATH = "not-found";
    public static final String BAD_REQUEST_TYPE_PATH = "bad-request";
    public static final String CONFLICT_TYPE_PATH = "conflict";
    public static final String FORBIDDEN_TYPE_PATH = "forbidden";
    public static final String METHOD_NOT_ALLOWED_TYPE_PATH = "method-not-allowed";
    public static final String INTERNAL_ERROR_TYPE_PATH = "internal-error";

    // HTTP Status Codes
    public static final int BAD_REQUEST_STATUS = 400;
    public static final int FORBIDDEN_STATUS = 403;
    public static final int NOT_FOUND_STATUS = 404;
    public static final int METHOD_NOT_ALLOWED_STATUS = 405;
    public static final int CONFLICT_STATUS = 409;
    public static final int INTERNAL_ERROR_STATUS = 500;

    // Error Details
    public static final String INTERNAL_ERROR_DETAIL = "An unexpected error occurred.";
    public static final String GENERIC_ERROR_DETAIL = "An error occurred.";

    // URIs
    public static final String INTERNAL_ERROR_TYPE_URI = "https://errors.luwian.io/internal-error";

    // HTTP Headers
    public static final String PROBLEM_JSON_CONTENT_TYPE = "application/problem+json";

    // Property Keys
    public static final String TIMESTAMP_PROPERTY = "timestamp";
    public static final String ERROR_CODE_PROPERTY = "errorCode";
    public static final String CORRELATION_ID_PROPERTY = "correlationId";

    // Error Base URL
    public static final String ERROR_BASE_URL = "https://errors.luwian.io";

    // Error Detail Messages
    public static final String BAD_REQUEST_DETAIL =
            "Request body contains invalid or unsupported values.";
    public static final String NOT_FOUND_DETAIL = "Requested resource was not found.";
    public static final String FORBIDDEN_DETAIL = "You are not allowed to perform this operation.";
    public static final String CONFLICT_DETAIL =
            "Request cannot be completed due to resource state.";
    public static final String METHOD_NOT_ALLOWED_DETAIL =
            "HTTP method is not allowed for this endpoint.";
}
