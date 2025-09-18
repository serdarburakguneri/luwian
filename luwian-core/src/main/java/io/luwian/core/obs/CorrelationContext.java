package io.luwian.core.obs;

import java.util.Optional;

/** Framework-agnostic correlation & tenancy context. */
public interface CorrelationContext {
    void setCorrelationId(String id);

    Optional<String> getCorrelationId();

    void setTenantId(String id);

    Optional<String> getTenantId();

    void clear();

    /** Bridge to logging MDC or equivalent. */
    interface MdcBridge {
        void put(String key, String value);

        void remove(String key);
    }
}
