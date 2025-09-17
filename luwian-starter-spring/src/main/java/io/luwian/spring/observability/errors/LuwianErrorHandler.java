package io.luwian.spring.observability.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.util.Map;

/**
 * RFC7807 Problem+JSON mapper (skeleton).
 * See ERRORS.md for mappings and payload shape.
 */
@ControllerAdvice
public class LuwianErrorHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> onUnhandled(Exception ex) {
        Map<String, Object> problem = Map.of(
            "type", URI.create("about:blank").toString(),
            "title", "Internal Server Error",
            "status", HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
