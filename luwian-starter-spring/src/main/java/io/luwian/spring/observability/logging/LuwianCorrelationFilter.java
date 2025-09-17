package io.luwian.spring.observability.logging;

import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Extracts/creates correlationId and places it into MDC.
 * Writes x-correlation-id on response.
 */
public class LuwianCorrelationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // TODO: derive or generate correlationId, set MDC keys, set response header.
        filterChain.doFilter(request, response);
    }
}
