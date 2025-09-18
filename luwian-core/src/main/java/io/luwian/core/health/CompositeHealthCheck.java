package io.luwian.core.health;

import java.util.LinkedHashMap;
import java.util.Map;

/** Runs multiple checks and aggregates status (DOWN wins, then DEGRADED). */
public class CompositeHealthCheck implements HealthCheck {

    private final Map<String, HealthCheck> checks = new LinkedHashMap<>();

    public CompositeHealthCheck register(String name, HealthCheck check) {
        checks.put(name, check);
        return this;
    }

    @Override
    public HealthReport check() {
        HealthStatus agg = HealthStatus.UP;
        Map<String, Object> details = new LinkedHashMap<>();
        long totalMs = 0;

        for (var e : checks.entrySet()) {
            HealthReport r = e.getValue().timed();
            details.put(
                    e.getKey(),
                    Map.of(
                            HealthConstants.DETAIL_STATUS, r.status().name(),
                            HealthConstants.DETAIL_DURATION_MS, r.durationMs(),
                            HealthConstants.DETAIL_DETAILS, r.details()));
            totalMs += r.durationMs();
            if (r.status() == HealthStatus.DOWN) agg = HealthStatus.DOWN;
            else if (r.status() == HealthStatus.DEGRADED && agg == HealthStatus.UP)
                agg = HealthStatus.DEGRADED;
        }
        return new HealthReport.Builder(agg)
                .detail(HealthConstants.COMPONENT_COMPONENTS, details)
                .durationMs(totalMs)
                .build();
    }
}
