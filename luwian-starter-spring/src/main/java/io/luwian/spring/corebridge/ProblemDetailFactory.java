package io.luwian.spring.corebridge;

import io.luwian.core.error.ErrorCatalog;
import io.luwian.core.error.ErrorCode;
import io.luwian.core.error.Problem;
import io.luwian.core.obs.CorrelationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

/** Converts exceptions to Spring ProblemDetail via core catalog + builder. */
public class ProblemDetailFactory {

    // Error base URL
    private static final String ERROR_BASE_URL = "https://errors.luwian.io";

    // Property keys
    private static final String PROP_TIMESTAMP = "timestamp";
    private static final String PROP_ERROR_CODE = "errorCode";
    private static final String PROP_CORRELATION_ID = "correlationId";

    // Detail messages
    private static final String DETAIL_BAD_REQUEST = "Request body contains invalid or unsupported values.";
    private static final String DETAIL_NOT_FOUND = "Requested resource was not found.";
    private static final String DETAIL_FORBIDDEN = "You are not allowed to perform this operation.";
    private static final String DETAIL_CONFLICT = "Request cannot be completed due to resource state.";
    private static final String DETAIL_METHOD_NOT_ALLOWED = "HTTP method is not allowed for this endpoint.";
    private static final String DETAIL_INTERNAL_ERROR = "An unexpected error occurred.";

    // HTTP status codes
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_NOT_FOUND = 404;
    private static final int STATUS_FORBIDDEN = 403;
    private static final int STATUS_CONFLICT = 409;
    private static final int STATUS_METHOD_NOT_ALLOWED = 405;

    private final ErrorCatalog catalog;
    private final CorrelationContext correlation;

    public ProblemDetailFactory(ErrorCatalog catalog, CorrelationContext correlation) {
        this.catalog = catalog;
        this.correlation = correlation;
    }

    public ProblemDetail toProblemDetail(Throwable t, HttpServletRequest req) {
        ErrorCode code = catalog.resolve(t).orElseGet(
                () -> new ErrorCode("LUW-INT-000", 500, "Internal server error", "internal-error")
        );
        Problem p = new ProblemBuilderImpl()
                .type(URI.create(code.typeUri(ERROR_BASE_URL)))
                .title(code.title())
                .status(code.httpStatus())
                .detail(conciseDetail(code.httpStatus()))
                .instance(URI.create(req.getRequestURI()))
                .put(PROP_TIMESTAMP, OffsetDateTime.now().toString())
                .put(PROP_ERROR_CODE, code.code())
                .build();

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(p.status()), p.detail());
        pd.setTitle(p.title());
        pd.setType(p.type());
        pd.setInstance(p.instance());
        p.extensions().forEach(pd::setProperty);
        correlation.getCorrelationId().ifPresent(cid -> pd.setProperty(PROP_CORRELATION_ID, cid));
        return pd;
    }

    private static String conciseDetail(int status) {
        return switch (status) {
            case STATUS_BAD_REQUEST -> DETAIL_BAD_REQUEST;
            case STATUS_NOT_FOUND -> DETAIL_NOT_FOUND;
            case STATUS_FORBIDDEN -> DETAIL_FORBIDDEN;
            case STATUS_CONFLICT -> DETAIL_CONFLICT;
            case STATUS_METHOD_NOT_ALLOWED -> DETAIL_METHOD_NOT_ALLOWED;
            default -> DETAIL_INTERNAL_ERROR;
        };
    }
}
