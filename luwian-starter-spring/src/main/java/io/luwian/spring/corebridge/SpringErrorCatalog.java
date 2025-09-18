package io.luwian.spring.corebridge;

import io.luwian.core.error.ErrorCatalog;
import io.luwian.core.error.ErrorCode;

import java.nio.file.AccessDeniedException;
import java.util.*;

/** Spring-flavored default catalog mapping common exceptions to LUW codes. */
public class SpringErrorCatalog implements ErrorCatalog {

    // Error codes
    private static final String ERROR_CODE_VAL_001 = "LUW-VAL-001";
    private static final String ERROR_CODE_VAL_002 = "LUW-VAL-002";
    private static final String ERROR_CODE_NOT_001 = "LUW-NOT-001";
    private static final String ERROR_CODE_REQ_001 = "LUW-REQ-001";
    private static final String ERROR_CODE_STA_001 = "LUW-STA-001";
    private static final String ERROR_CODE_SEC_001 = "LUW-SEC-001";
    private static final String ERROR_CODE_SEC_002 = "LUW-SEC-002";
    private static final String ERROR_CODE_MTH_001 = "LUW-MTH-001";
    private static final String ERROR_CODE_INT_000 = "LUW-INT-000";

    // Error titles
    private static final String TITLE_VALIDATION_FAILED = "Validation failed";
    private static final String TITLE_RESOURCE_NOT_FOUND = "Resource not found";
    private static final String TITLE_BAD_REQUEST = "Bad request";
    private static final String TITLE_INVALID_STATE = "Invalid state";
    private static final String TITLE_FORBIDDEN = "Forbidden";
    private static final String TITLE_METHOD_NOT_ALLOWED = "Method not allowed";
    private static final String TITLE_INTERNAL_SERVER_ERROR = "Internal server error";

    // Error type paths
    private static final String TYPE_VALIDATION_ERROR = "validation-error";
    private static final String TYPE_NOT_FOUND = "not-found";
    private static final String TYPE_BAD_REQUEST = "bad-request";
    private static final String TYPE_CONFLICT = "conflict";
    private static final String TYPE_FORBIDDEN = "forbidden";
    private static final String TYPE_METHOD_NOT_ALLOWED = "method-not-allowed";
    private static final String TYPE_INTERNAL_ERROR = "internal-error";

    // HTTP status codes
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_NOT_FOUND = 404;
    private static final int STATUS_FORBIDDEN = 403;
    private static final int STATUS_CONFLICT = 409;
    private static final int STATUS_METHOD_NOT_ALLOWED = 405;
    private static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    private static final Map<Class<? extends Throwable>, ErrorCode> MAP = Map.ofEntries(
        entry(jakarta.validation.ConstraintViolationException.class, new ErrorCode(ERROR_CODE_VAL_001, STATUS_BAD_REQUEST, TITLE_VALIDATION_FAILED, TYPE_VALIDATION_ERROR)),
        entry(org.springframework.web.bind.MethodArgumentNotValidException.class, new ErrorCode(ERROR_CODE_VAL_002, STATUS_BAD_REQUEST, TITLE_VALIDATION_FAILED, TYPE_VALIDATION_ERROR)),
        entry(java.util.NoSuchElementException.class, new ErrorCode(ERROR_CODE_NOT_001, STATUS_NOT_FOUND, TITLE_RESOURCE_NOT_FOUND, TYPE_NOT_FOUND)),
        entry(IllegalArgumentException.class, new ErrorCode(ERROR_CODE_REQ_001, STATUS_BAD_REQUEST, TITLE_BAD_REQUEST, TYPE_BAD_REQUEST)),
        entry(IllegalStateException.class, new ErrorCode(ERROR_CODE_STA_001, STATUS_CONFLICT, TITLE_INVALID_STATE, TYPE_CONFLICT)),
        entry(SecurityException.class, new ErrorCode(ERROR_CODE_SEC_001, STATUS_FORBIDDEN, TITLE_FORBIDDEN, TYPE_FORBIDDEN)),
        entry(AccessDeniedException.class, new ErrorCode(ERROR_CODE_SEC_002, STATUS_FORBIDDEN, TITLE_FORBIDDEN, TYPE_FORBIDDEN)),
        entry(UnsupportedOperationException.class, new ErrorCode(ERROR_CODE_MTH_001, STATUS_METHOD_NOT_ALLOWED, TITLE_METHOD_NOT_ALLOWED, TYPE_METHOD_NOT_ALLOWED))
    );

    @Override
    public Optional<ErrorCode> resolve(Throwable error) {
        // Exact match, then first superclass match
        Class<?> c = error.getClass();
        while (c != null && Throwable.class.isAssignableFrom(c)) {
            var ec = MAP.get(c);
            if (ec != null) return Optional.of(ec);
            c = c.getSuperclass();
        }
        return Optional.of(new ErrorCode(ERROR_CODE_INT_000, STATUS_INTERNAL_SERVER_ERROR, TITLE_INTERNAL_SERVER_ERROR, TYPE_INTERNAL_ERROR));
    }

    private static <K, V> Map.Entry<K, V> entry(K k, V v) { return Map.entry(k, v); }
}
