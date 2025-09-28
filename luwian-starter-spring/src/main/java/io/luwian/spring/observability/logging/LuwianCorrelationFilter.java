package io.luwian.spring.observability.logging;

import io.luwian.core.obs.CorrelationContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

/** Establishes correlation and tenant context for each request. */
public class LuwianCorrelationFilter extends OncePerRequestFilter {

    private final CorrelationContext correlationContext;
    private final String tenantHeader;

    public LuwianCorrelationFilter(CorrelationContext correlationContext, String tenantHeader) {
        this.correlationContext = correlationContext;
        this.tenantHeader = tenantHeader;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String cid = headerFirst(request, "x-correlation-id");
            if (cid == null || cid.isBlank()) {
                cid = UUID.randomUUID().toString();
            }
            correlationContext.setCorrelationId(cid);
            response.setHeader("x-correlation-id", cid);

            String tenantId = headerFirst(request, tenantHeader);
            if (tenantId != null && !tenantId.isBlank()) {
                correlationContext.setTenantId(tenantId);
            }

            filterChain.doFilter(request, response);
        } finally {
            correlationContext.clear();
        }
    }

    private static String headerFirst(HttpServletRequest req, String name) {
        var vals = req.getHeaders(name);
        return vals != null && vals.hasMoreElements() ? vals.nextElement() : null;
    }
}
