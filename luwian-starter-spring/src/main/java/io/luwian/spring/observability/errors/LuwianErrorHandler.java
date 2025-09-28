package io.luwian.spring.observability.errors;

import io.luwian.core.error.ErrorConstants;
import io.luwian.spring.corebridge.ProblemDetailFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Problem+JSON mapper backed by core ErrorCatalog and correlation context. */
@ControllerAdvice
public class LuwianErrorHandler {

    private final LuwianErrorProperties props;
    private final ProblemDetailFactory factory;

    public LuwianErrorHandler(LuwianErrorProperties props, ProblemDetailFactory factory) {
        this.props = props;
        this.factory = factory;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> onUnhandled(Exception ex, HttpServletRequest req) {
        ProblemDetail pd = factory.toProblemDetail(ex, req);
        boolean onTrace =
                props.getIncludeStacktrace()
                        == LuwianErrorProperties.IncludeStacktracePolicy.ON_TRACE;
        String detail = pd.getDetail();
        if (onTrace) {
            if (detail == null || detail.isBlank()) {
                pd.setDetail(ErrorConstants.GENERIC_ERROR_DETAIL);
            }
        } else {
            pd.setDetail(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, ErrorConstants.PROBLEM_JSON_CONTENT_TYPE);
        HttpStatus status = HttpStatus.resolve(pd.getStatus());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(pd, headers, status);
    }
}
