package io.luwian.core.obs;

import java.util.Optional;

/** Default CorrelationContext backed by ThreadLocal; MdcBridge optional. */
public class ThreadLocalCorrelationContext implements CorrelationContext {

    private final ThreadLocal<String> cid = new ThreadLocal<>();
    private final ThreadLocal<String> tenant = new ThreadLocal<>();
    private final MdcBridge mdc;

    public ThreadLocalCorrelationContext() { this(null); }

    public ThreadLocalCorrelationContext(MdcBridge mdcBridge) {
        this.mdc = mdcBridge;
    }

    @Override public void setCorrelationId(String id) {
        cid.set(id);
        if (mdc != null && id != null && !id.isBlank()) mdc.put(CorrelationConstants.CORRELATION_ID_KEY, id);
    }

    @Override public Optional<String> getCorrelationId() { return Optional.ofNullable(cid.get()); }

    @Override public void setTenantId(String id) {
        tenant.set(id);
        if (mdc != null && id != null && !id.isBlank()) mdc.put(CorrelationConstants.TENANT_ID_KEY, id);
    }

    @Override public Optional<String> getTenantId() { return Optional.ofNullable(tenant.get()); }

    @Override public void clear() {
        if (mdc != null) {
            mdc.remove(CorrelationConstants.CORRELATION_ID_KEY);
            mdc.remove(CorrelationConstants.TENANT_ID_KEY);
        }
        cid.remove();
        tenant.remove();
    }
}
