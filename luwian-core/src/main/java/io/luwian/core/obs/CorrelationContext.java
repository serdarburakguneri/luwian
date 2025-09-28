package io.luwian.core.obs;

import java.util.Optional;

/** Correlation & tenancy context. */
public interface CorrelationContext {
    void setCorrelationId(String id);

    Optional<String> getCorrelationId();

    void setTenantId(String id);

    Optional<String> getTenantId();

    /** Optional end-user identifier. Default no-op to retain binary compatibility. */
    default void setUserId(String id) {}

    /** Optional end-user identifier. Default empty. */
    default Optional<String> getUserId() {
        return Optional.empty();
    }

    void clear();

    /** Bridge to logging MDC or equivalent. */
    interface MdcBridge {
        void put(String key, String value);

        void remove(String key);
    }
}
