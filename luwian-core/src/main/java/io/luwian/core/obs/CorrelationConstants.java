package io.luwian.core.obs;

/** Constants for correlation and tenancy context. */
public final class CorrelationConstants {

    private CorrelationConstants() {}

    // MDC Keys
    public static final String CORRELATION_ID_KEY = "correlationId";
    public static final String TENANT_ID_KEY = "tenantId";
}
