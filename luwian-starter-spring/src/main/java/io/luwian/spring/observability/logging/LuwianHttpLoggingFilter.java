package io.luwian.spring.observability.logging;

import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Logs HTTP request/response line, duration, and status.
 * Body logging is controlled by luwian.logging.http.body.
 */
public class LuwianHttpLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // TODO: log method/path/status/duration. Respect body flag and redact sensitive headers.
        filterChain.doFilter(request, response);
    }
}
