package io.luwian.spring.observability.errors;

/**
 * Constants for error handling configuration and messages.
 */
public final class ErrorConstants {

    private ErrorConstants() {
        // Utility class
    }

    // Error codes
    public static final String INTERNAL_ERROR_CODE = "LUW-INT-000";

    // Error messages
    public static final String INTERNAL_ERROR_TITLE = "Internal server error";
    public static final String INTERNAL_ERROR_DETAIL = "An unexpected error occurred.";
    public static final String GENERIC_ERROR_DETAIL = "An error occurred.";

    // URIs
    public static final String INTERNAL_ERROR_TYPE_URI = "https://errors.luwian.io/internal-error";

    // HTTP headers
    public static final String PROBLEM_JSON_CONTENT_TYPE = "application/problem+json";

    // Property keys
    public static final String TIMESTAMP_PROPERTY = "timestamp";
    public static final String ERROR_CODE_PROPERTY = "errorCode";
}
