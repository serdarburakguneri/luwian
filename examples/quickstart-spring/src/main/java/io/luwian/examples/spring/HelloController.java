package io.luwian.examples.spring;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example controller demonstrating Luwian features: - Error handling with core ErrorConstants -
 * Correlation context - Metrics collection - JSON logging
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from Luwian (Spring scaffold)!");
        response.put("timestamp", java.time.Instant.now().toString());
        response.put("version", "1.0.0");
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "quickstart-spring");
        return response;
    }

    @GetMapping("/error/not-found")
    public ResponseEntity<String> triggerNotFound() {
        throw new NoSuchElementException(
                "Resource not found - this will be handled by Luwian error handling");
    }

    @GetMapping("/error/bad-request")
    public ResponseEntity<String> triggerBadRequest() {
        throw new IllegalArgumentException(
                "Invalid request parameter - this will be handled by Luwian error handling");
    }

    @GetMapping("/error/internal")
    public ResponseEntity<String> triggerInternalError() {
        throw new RuntimeException(
                "Internal server error - this will be handled by Luwian error handling");
    }

    @GetMapping("/metrics-demo")
    public Map<String, Object> metricsDemo() {
        // This endpoint will be automatically timed by Luwian metrics
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint demonstrates Luwian metrics collection");
        response.put("note", "Check /actuator/prometheus for metrics");
        return response;
    }

    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> request) {
        // This demonstrates request/response logging
        Map<String, Object> response = new HashMap<>();
        response.put("echo", request);
        response.put("timestamp", java.time.Instant.now().toString());
        return response;
    }
}
