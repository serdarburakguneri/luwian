package io.luwian.core.obs;

import java.util.Optional;

/** Immutable snapshot of correlation-related fields carried across threads. */
public record RequestContext(String correlationId, String tenantId, String userId) {

    public static RequestContext from(CorrelationContext ctx) {
        return new RequestContext(
                ctx.getCorrelationId().orElse(null),
                ctx.getTenantId().orElse(null),
                ctx.getUserId().orElse(null));
    }

    public void applyTo(CorrelationContext ctx) {
        if (correlationId != null && !correlationId.isBlank()) ctx.setCorrelationId(correlationId);
        if (tenantId != null && !tenantId.isBlank()) ctx.setTenantId(tenantId);
        if (userId != null && !userId.isBlank()) ctx.setUserId(userId);
    }

    public Optional<String> correlationIdOpt() {
        return Optional.ofNullable(correlationId);
    }

    public Optional<String> tenantIdOpt() {
        return Optional.ofNullable(tenantId);
    }

    public Optional<String> userIdOpt() {
        return Optional.ofNullable(userId);
    }
}
