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
                        .type(URI.create(code.typeUri(ProblemDetailConstants.ERROR_BASE_URL)))
                        .title(code.title())
                        .status(code.httpStatus())
                        .detail(conciseDetail(code.httpStatus()))
                        .instance(URI.create(req.getRequestURI()))
                        .put(
                                ProblemDetailConstants.TIMESTAMP_PROPERTY,
                                OffsetDateTime.now().toString())
                        .put(ProblemDetailConstants.ERROR_CODE_PROPERTY, code.code())
                        .build();

        ProblemDetail pd =
                ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(p.status()), p.detail());
        pd.setTitle(p.title());
        pd.setType(p.type());
        pd.setInstance(p.instance());
        p.extensions().forEach(pd::setProperty);
        correlation
                .getCorrelationId()
                .ifPresent(
                        cid -> pd.setProperty(ProblemDetailConstants.CORRELATION_ID_PROPERTY, cid));
        return pd;
    }

    private static String conciseDetail(int status) {
        return switch (status) {
            case ProblemDetailConstants.BAD_REQUEST_STATUS ->
                    ProblemDetailConstants.BAD_REQUEST_DETAIL;
            case ProblemDetailConstants.NOT_FOUND_STATUS -> ProblemDetailConstants.NOT_FOUND_DETAIL;
            case ProblemDetailConstants.FORBIDDEN_STATUS -> ProblemDetailConstants.FORBIDDEN_DETAIL;
            case ProblemDetailConstants.CONFLICT_STATUS -> ProblemDetailConstants.CONFLICT_DETAIL;
            case ProblemDetailConstants.METHOD_NOT_ALLOWED_STATUS ->
                    ProblemDetailConstants.METHOD_NOT_ALLOWED_DETAIL;
            default -> ProblemDetailConstants.INTERNAL_ERROR_DETAIL;
        };
    }
}
