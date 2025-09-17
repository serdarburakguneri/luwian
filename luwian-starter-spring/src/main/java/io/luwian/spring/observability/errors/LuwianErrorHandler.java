package io.luwian.spring.observability.errors;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * RFC7807 Problem+JSON mapper.
 * Aligns with ERRORS.md and includes correlationId + errorCode.
 */
@ControllerAdvice
public class LuwianErrorHandler {

    // Error codes from ERRORS.md
    private static final String ERROR_CODE_VAL_001 = "LUW-VAL-001";
    private static final String ERROR_CODE_VAL_002 = "LUW-VAL-002";
    private static final String ERROR_CODE_NOT_001 = "LUW-NOT-001";
    private static final String ERROR_CODE_REQ_001 = "LUW-REQ-001";
    private static final String ERROR_CODE_STA_001 = "LUW-STA-001";
    private static final String ERROR_CODE_SEC_002 = "LUW-SEC-002";
    private static final String ERROR_CODE_MTH_001 = "LUW-MTH-001";
    private static final String ERROR_CODE_INT_000 = "LUW-INT-000";

    // Error types
    private static final String TYPE_VALIDATION_ERROR = "validation-error";
    private static final String TYPE_NOT_FOUND = "not-found";
    private static final String TYPE_BAD_REQUEST = "bad-request";
    private static final String TYPE_CONFLICT = "conflict";
    private static final String TYPE_FORBIDDEN = "forbidden";
    private static final String TYPE_METHOD_NOT_ALLOWED = "method-not-allowed";
    private static final String TYPE_INTERNAL_ERROR = "internal-error";

    // Titles
    private static final String TITLE_VALIDATION_FAILED = "Validation failed";
    private static final String TITLE_RESOURCE_NOT_FOUND = "Resource not found";
    private static final String TITLE_BAD_REQUEST = "Bad request";
    private static final String TITLE_INVALID_STATE = "Invalid state";
    private static final String TITLE_FORBIDDEN = "Forbidden";
    private static final String TITLE_METHOD_NOT_ALLOWED = "Method not allowed";
    private static final String TITLE_INTERNAL_SERVER_ERROR = "Internal server error";

    // Property keys
    private static final String PROP_CORRELATION_ID = "correlationId";
    private static final String PROP_ERROR_CODE = "errorCode";
    private static final String PROP_TIMESTAMP = "timestamp";
    private static final String PROP_VIOLATIONS = "violations";
    private static final String PROP_FIELD = "field";
    private static final String PROP_MESSAGE = "message";

    // Headers
    private static final String HEADER_CORRELATION_ID = "x-correlation-id";
    private static final String CONTENT_TYPE_PROBLEM_JSON = "application/problem+json";

    // Base URL for error types
    private static final String ERROR_BASE_URL = "https://errors.luwian.io/";

    // Default messages
    private static final String DEFAULT_ERROR_MESSAGE = "An error occurred.";
    private static final String DEFAULT_FIELD_MESSAGE = "invalid value";

    // Concise detail messages
    private static final String DETAIL_BAD_REQUEST = "Request body contains invalid or unsupported values.";
    private static final String DETAIL_NOT_FOUND = "Requested resource was not found.";
    private static final String DETAIL_FORBIDDEN = "You are not allowed to perform this operation.";
    private static final String DETAIL_CONFLICT = "Request cannot be completed due to resource state.";
    private static final String DETAIL_METHOD_NOT_ALLOWED = "HTTP method is not allowed for this endpoint.";
    private static final String DETAIL_INTERNAL_ERROR = "An unexpected error occurred.";

    // MDC keys
    private static final String MDC_CORRELATION_ID = "correlationId";

    private final LuwianErrorProperties props;

    public LuwianErrorHandler(LuwianErrorProperties props) {
        this.props = Objects.requireNonNull(props, "props");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> onConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, TITLE_VALIDATION_FAILED,
                ERROR_CODE_VAL_001, TYPE_VALIDATION_ERROR, req);
        pd.setProperty(PROP_VIOLATIONS, ex.getConstraintViolations().stream()
                .map(v -> Map.of(PROP_FIELD, fieldName(v), PROP_MESSAGE, v.getMessage()))
                .collect(Collectors.toList()));
        includeDetail(pd, ex);
        return withHeaders(pd);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> onMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, TITLE_VALIDATION_FAILED,
                ERROR_CODE_VAL_002, TYPE_VALIDATION_ERROR, req);
        List<Map<String, Object>> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.<String, Object>of(PROP_FIELD, fe.getField(), PROP_MESSAGE, message(fe)))
                .collect(Collectors.toList());
        pd.setProperty(PROP_VIOLATIONS, violations);
        includeDetail(pd, ex);
        return withHeaders(pd);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> onNotFound(NoSuchElementException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.NOT_FOUND, TITLE_RESOURCE_NOT_FOUND,
                ERROR_CODE_NOT_001, TYPE_NOT_FOUND, req);
        includeDetail(pd, ex);
        return withHeaders(pd);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> onBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, TITLE_BAD_REQUEST,
                ERROR_CODE_REQ_001, TYPE_BAD_REQUEST, req);
        includeDetail(pd, ex);
        return withHeaders(pd);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> onInvalidState(IllegalStateException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.CONFLICT, TITLE_INVALID_STATE,
                ERROR_CODE_STA_001, TYPE_CONFLICT, req);
        includeDetail(pd, ex);
        return withHeaders(pd);
    }

    @ExceptionHandler(java.nio.file.AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> onAccessDenied(java.nio.file.AccessDeniedException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.FORBIDDEN, TITLE_FORBIDDEN,
                ERROR_CODE_SEC_002, TYPE_FORBIDDEN, req);
        includeDetail(pd, ex);
        return withHeaders(pd);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ProblemDetail> onMethodNotAllowed(UnsupportedOperationException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.METHOD_NOT_ALLOWED, TITLE_METHOD_NOT_ALLOWED,
                ERROR_CODE_MTH_001, TYPE_METHOD_NOT_ALLOWED, req);
        includeDetail(pd, ex);
        return withHeaders(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> onUnhandled(Exception ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.INTERNAL_SERVER_ERROR, TITLE_INTERNAL_SERVER_ERROR,
                ERROR_CODE_INT_000, TYPE_INTERNAL_ERROR, req);
        includeDetail(pd, ex);
        return withHeaders(pd);
    }

    private ResponseEntity<ProblemDetail> withHeaders(ProblemDetail pd) {
        HttpHeaders headers = new HttpHeaders();
        String correlationId = String.valueOf(pd.getProperties().get(PROP_CORRELATION_ID));
        if (correlationId != null && !correlationId.equals("null")) {
            headers.add(HEADER_CORRELATION_ID, correlationId);
        }
        headers.add(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_PROBLEM_JSON);
        return new ResponseEntity<>(pd, headers, HttpStatus.valueOf(pd.getStatus()));
    }

    private ProblemDetail problem(HttpStatus status, String title, String errorCode, String typePath, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, conciseDetail(status));
        pd.setTitle(title);
        pd.setType(URI.create(ERROR_BASE_URL + typePath));
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty(PROP_TIMESTAMP, OffsetDateTime.now().toString());
        pd.setProperty(PROP_ERROR_CODE, errorCode);
        String correlationId = correlationId();
        if (correlationId != null) {
            pd.setProperty(PROP_CORRELATION_ID, correlationId);
        }
        return pd;
    }

    private void includeDetail(ProblemDetail pd, Exception ex) {
        if (props.getIncludeStacktrace() == IncludeStacktracePolicy.ON_TRACE) {
            String cid = String.valueOf(pd.getProperties().get(PROP_CORRELATION_ID));
            if (cid != null && !cid.equals("null")) {
                pd.setDetail(Optional.ofNullable(ex.getMessage()).orElse(DEFAULT_ERROR_MESSAGE));
            }
        }
    }

    private static String fieldName(ConstraintViolation<?> v) {
        String path = v.getPropertyPath() != null ? v.getPropertyPath().toString() : "";
        int idx = path.lastIndexOf('.');
        return idx >= 0 ? path.substring(idx + 1) : path;
    }

    private static String message(FieldError fe) {
        return Optional.ofNullable(fe.getDefaultMessage()).orElse(DEFAULT_FIELD_MESSAGE);
    }

    private static String conciseDetail(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> DETAIL_BAD_REQUEST;
            case NOT_FOUND -> DETAIL_NOT_FOUND;
            case FORBIDDEN -> DETAIL_FORBIDDEN;
            case CONFLICT -> DETAIL_CONFLICT;
            case METHOD_NOT_ALLOWED -> DETAIL_METHOD_NOT_ALLOWED;
            default -> DETAIL_INTERNAL_ERROR;
        };
    }

    private static String correlationId() {
        String cid = MDC.get(MDC_CORRELATION_ID);
        return (cid == null || cid.isBlank()) ? null : cid;
    }
}
