package io.luwian.spring.corebridge;

import io.luwian.core.obs.CorrelationContext;
import org.slf4j.MDC;

import java.util.Optional;

/** MDC-backed correlation/tenancy storage. */
public class SpringCorrelationContext implements CorrelationContext {

    // MDC keys
    private static final String MDC_CORRELATION_ID = "correlationId";
    private static final String MDC_TENANT_ID = "tenantId";

    @Override public void setCorrelationId(String id) { if (id != null && !id.isBlank()) MDC.put(MDC_CORRELATION_ID, id); }
    @Override public Optional<String> getCorrelationId() { return Optional.ofNullable(MDC.get(MDC_CORRELATION_ID)); }

    @Override public void setTenantId(String id) { if (id != null && !id.isBlank()) MDC.put(MDC_TENANT_ID, id); }
    @Override public Optional<String> getTenantId() { return Optional.ofNullable(MDC.get(MDC_TENANT_ID)); }

    @Override public void clear() {
        MDC.remove(MDC_CORRELATION_ID);
        MDC.remove(MDC_TENANT_ID);
    }
}
