package io.luwian.spring.corebridge;

import io.luwian.core.error.BasicProblem;
import io.luwian.core.error.ErrorCatalog;
import io.luwian.core.error.ErrorCode;
import io.luwian.core.error.ErrorConstants;
import io.luwian.core.error.Problem;
import io.luwian.core.obs.CorrelationContext;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

/** Converts exceptions to Spring ProblemDetail via core catalog + builder. */
public class ProblemDetailFactory {

    private final ErrorCatalog catalog;
    private final CorrelationContext correlation;

    public ProblemDetailFactory(ErrorCatalog catalog, CorrelationContext correlation) {
        this.catalog = catalog;
        this.correlation = correlation;
    }

    public ProblemDetail toProblemDetail(Throwable t, HttpServletRequest req) {
        ErrorCode code =
                catalog.resolve(t)
                        .orElseGet(
                                () ->
                                        new ErrorCode(
                                                ErrorConstants.INTERNAL_ERROR_CODE,
                                                ErrorConstants.INTERNAL_ERROR_STATUS,
                                                ErrorConstants.INTERNAL_ERROR_TITLE,
                                                ErrorConstants.INTERNAL_ERROR_TYPE_PATH));
        Problem p =
                new BasicProblem.Builder()
                        .type(URI.create(code.typeUri(ErrorConstants.ERROR_BASE_URL)))
                        .title(code.title())
                        .status(code.httpStatus())
                        .detail(conciseDetail(code.httpStatus()))
                        .instance(URI.create(req.getRequestURI()))
                        .put(ErrorConstants.TIMESTAMP_PROPERTY, OffsetDateTime.now().toString())
                        .put(ErrorConstants.ERROR_CODE_PROPERTY, code.code())
                        .build();

        ProblemDetail pd =
                ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(p.status()), p.detail());
        pd.setTitle(p.title());
        pd.setType(p.type());
        pd.setInstance(p.instance());
        p.extensions().forEach(pd::setProperty);
        correlation
                .getCorrelationId()
                .ifPresent(cid -> pd.setProperty(ErrorConstants.CORRELATION_ID_PROPERTY, cid));
        return pd;
    }

    private static String conciseDetail(int status) {
        return switch (status) {
            case ErrorConstants.BAD_REQUEST_STATUS -> ErrorConstants.BAD_REQUEST_DETAIL;
            case ErrorConstants.NOT_FOUND_STATUS -> ErrorConstants.NOT_FOUND_DETAIL;
            case ErrorConstants.FORBIDDEN_STATUS -> ErrorConstants.FORBIDDEN_DETAIL;
            case ErrorConstants.CONFLICT_STATUS -> ErrorConstants.CONFLICT_DETAIL;
            case ErrorConstants.METHOD_NOT_ALLOWED_STATUS ->
                    ErrorConstants.METHOD_NOT_ALLOWED_DETAIL;
            default -> ErrorConstants.INTERNAL_ERROR_DETAIL;
        };
    }
}
