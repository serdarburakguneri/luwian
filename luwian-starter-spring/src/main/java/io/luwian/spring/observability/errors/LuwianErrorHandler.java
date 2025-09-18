package io.luwian.spring.observability.errors;

import io.luwian.core.error.ErrorConstants;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Minimal Problem+JSON mapper. Delegates detail policy to LuwianErrorProperties. */
@ControllerAdvice
public class LuwianErrorHandler {

    private final LuwianErrorProperties props;

    public LuwianErrorHandler(LuwianErrorProperties props) {
        this.props = props;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> onUnhandled(Exception ex, HttpServletRequest req) {
        ProblemDetail pd =
                ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, conciseDetail());
        pd.setTitle(ErrorConstants.INTERNAL_ERROR_TITLE);
        pd.setType(URI.create(ErrorConstants.INTERNAL_ERROR_TYPE_URI));
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty(ErrorConstants.TIMESTAMP_PROPERTY, OffsetDateTime.now().toString());
        pd.setProperty(ErrorConstants.ERROR_CODE_PROPERTY, ErrorConstants.INTERNAL_ERROR_CODE);

        if (props.getIncludeStacktrace()
                == LuwianErrorProperties.IncludeStacktracePolicy.ON_TRACE) {
            pd.setDetail(
                    Optional.ofNullable(ex.getMessage())
                            .orElse(ErrorConstants.GENERIC_ERROR_DETAIL));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, ErrorConstants.PROBLEM_JSON_CONTENT_TYPE);

        return new ResponseEntity<>(pd, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static String conciseDetail() {
        return ErrorConstants.INTERNAL_ERROR_DETAIL;
    }
}
