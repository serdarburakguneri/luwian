package io.luwian.spring.observability.errors;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

class LuwianErrorHandlerTest {

    @Test
    void fallbackProducesProblemJson() {
        var props = new LuwianErrorProperties();
        props.setIncludeStacktrace(IncludeStacktracePolicy.NEVER);
        var handler = new LuwianErrorHandler(props);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getRequestURI()).thenReturn("/demo");

        ResponseEntity<ProblemDetail> resp = handler.onUnhandled(new RuntimeException("boom"), req);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getHeaders().getFirst("Content-Type")).isEqualTo("application/problem+json");
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getProperties()).containsKey("errorCode");
    }
}
