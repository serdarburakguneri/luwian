package io.luwian.spring.observability.logging;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Extracts/creates correlationId, tenantId, and (if present) W3C trace headers.
 * Writes x-correlation-id on response and enriches MDC for all logs.
 */
public class LuwianCorrelationFilter extends OncePerRequestFilter {

    // MDC keys
    public static final String MDC_CORRELATION_ID = "correlationId";
    public static final String MDC_TENANT_ID = "tenantId";
    public static final String MDC_USER_ID = "userId";
    public static final String MDC_TRACE_ID = "traceId";
    public static final String MDC_SPAN_ID = "spanId";

    // HTTP headers
    private static final String HEADER_CORRELATION_ID = "x-correlation-id";
    private static final String HEADER_TRACEPARENT = "traceparent";

    // Traceparent parsing
    private static final String TRACEPARENT_SEPARATOR = "-";
    private static final int TRACEPARENT_MIN_PARTS = 4;
    private static final int TRACEPARENT_TRACE_ID_INDEX = 1;
    private static final int TRACEPARENT_SPAN_ID_INDEX = 2;

    private final LuwianLoggingProperties props;

    public LuwianCorrelationFilter(LuwianLoggingProperties props) {
        this.props = props;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String incomingCid = header(request, HEADER_CORRELATION_ID);
        String traceparent = header(request, HEADER_TRACEPARENT);
        String tenant = header(request, props.getTenancyHeader());

        String correlationId = (incomingCid != null && !incomingCid.isBlank()) ? incomingCid : UUID.randomUUID().toString();

        String traceId = null, spanId = null;
        if (traceparent != null && traceparent.split(TRACEPARENT_SEPARATOR).length >= TRACEPARENT_MIN_PARTS) {
            String[] parts = traceparent.split(TRACEPARENT_SEPARATOR);
            traceId = parts[TRACEPARENT_TRACE_ID_INDEX];
            spanId = parts[TRACEPARENT_SPAN_ID_INDEX];
        }

        try {
            put(MDC_CORRELATION_ID, correlationId);
            if (tenant != null) put(MDC_TENANT_ID, tenant);
            if (traceId != null) put(MDC_TRACE_ID, traceId);
            if (spanId != null) put(MDC_SPAN_ID, spanId);

            response.setHeader(HEADER_CORRELATION_ID, correlationId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_CORRELATION_ID);
            MDC.remove(MDC_TENANT_ID);
            MDC.remove(MDC_USER_ID);
            MDC.remove(MDC_TRACE_ID);
            MDC.remove(MDC_SPAN_ID);
        }
    }

    private static String header(HttpServletRequest req, String name) {
        String v = req.getHeader(name);
        return (v == null || v.isBlank()) ? null : v;
    }

    private static void put(String key, String value) {
        if (value != null && !value.isBlank()) {
            MDC.put(key, value);
        }
    }
}
