package io.luwian.core.error;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/** Default exception → LUW error code mapping. */
public class DefaultErrorCatalog implements ErrorCatalog {

    private static final Map<Class<? extends Throwable>, ErrorCode> MAP =
            Map.ofEntries(
                    Map.entry(
                            NoSuchElementException.class,
                            new ErrorCode(
                                    ErrorConstants.NOT_FOUND_CODE,
                                    ErrorConstants.NOT_FOUND_STATUS,
                                    ErrorConstants.NOT_FOUND_TITLE,
                                    ErrorConstants.NOT_FOUND_TYPE_PATH)),
                    Map.entry(
                            IllegalArgumentException.class,
                            new ErrorCode(
                                    ErrorConstants.BAD_REQUEST_CODE,
                                    ErrorConstants.BAD_REQUEST_STATUS,
                                    ErrorConstants.BAD_REQUEST_TITLE,
                                    ErrorConstants.BAD_REQUEST_TYPE_PATH)),
                    Map.entry(
                            IllegalStateException.class,
                            new ErrorCode(
                                    ErrorConstants.INVALID_STATE_CODE,
                                    ErrorConstants.CONFLICT_STATUS,
                                    ErrorConstants.INVALID_STATE_TITLE,
                                    ErrorConstants.CONFLICT_TYPE_PATH)),
                    Map.entry(
                            SecurityException.class,
                            new ErrorCode(
                                    ErrorConstants.FORBIDDEN_CODE,
                                    ErrorConstants.FORBIDDEN_STATUS,
                                    ErrorConstants.FORBIDDEN_TITLE,
                                    ErrorConstants.FORBIDDEN_TYPE_PATH)),
                    Map.entry(
                            AccessDeniedException.class,
                            new ErrorCode(
                                    ErrorConstants.ACCESS_DENIED_CODE,
                                    ErrorConstants.FORBIDDEN_STATUS,
                                    ErrorConstants.FORBIDDEN_TITLE,
                                    ErrorConstants.FORBIDDEN_TYPE_PATH)),
                    Map.entry(
                            UnsupportedOperationException.class,
                            new ErrorCode(
                                    ErrorConstants.METHOD_NOT_ALLOWED_CODE,
                                    ErrorConstants.METHOD_NOT_ALLOWED_STATUS,
                                    ErrorConstants.METHOD_NOT_ALLOWED_TITLE,
                                    ErrorConstants.METHOD_NOT_ALLOWED_TYPE_PATH)));

    private static final Map<Class<? extends Throwable>, ErrorCode> REFLECTIVE = buildReflective();

    private static Map<Class<? extends Throwable>, ErrorCode> buildReflective() {
        Map<Class<? extends Throwable>, ErrorCode> m = new HashMap<>();
        // jakarta.validation.ConstraintViolationException → 400 validation-error (VAL-001)
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> c =
                    (Class<? extends Throwable>)
                            Class.forName("jakarta.validation.ConstraintViolationException");
            m.put(
                    c,
                    new ErrorCode(
                            "LUW-VAL-001",
                            ErrorConstants.BAD_REQUEST_STATUS,
                            "Validation failed",
                            "validation-error"));
        } catch (ClassNotFoundException ignored) {
        }

        // org.springframework.web.bind.MethodArgumentNotValidException → 400 validation-error
        // (VAL-002)
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> c =
                    (Class<? extends Throwable>)
                            Class.forName(
                                    "org.springframework.web.bind.MethodArgumentNotValidException");
            m.put(
                    c,
                    new ErrorCode(
                            "LUW-VAL-002",
                            ErrorConstants.BAD_REQUEST_STATUS,
                            "Validation failed",
                            "validation-error"));
        } catch (ClassNotFoundException ignored) {
        }
        return m;
    }

    @Override
    public Optional<ErrorCode> resolve(Throwable error) {
        Class<?> c = error.getClass();
        while (c != null && Throwable.class.isAssignableFrom(c)) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> throwableClass = (Class<? extends Throwable>) c;
            var ec = MAP.get(throwableClass);
            if (ec == null) ec = REFLECTIVE.get(throwableClass);
            if (ec != null) return Optional.of(ec);
            c = c.getSuperclass();
        }
        return Optional.of(
                new ErrorCode(
                        ErrorConstants.INTERNAL_ERROR_CODE,
                        ErrorConstants.INTERNAL_ERROR_STATUS,
                        ErrorConstants.INTERNAL_ERROR_TITLE,
                        ErrorConstants.INTERNAL_ERROR_TYPE_PATH));
    }
}
