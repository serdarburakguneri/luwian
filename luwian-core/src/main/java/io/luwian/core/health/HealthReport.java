package io.luwian.core.health;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Result of a health check with optional details. */
public final class HealthReport {
    private final HealthStatus status;
    private final long durationMs;
    private final Map<String, Object> details;

    private HealthReport(HealthStatus status, long durationMs, Map<String, Object> details) {
        this.status = status;
        this.durationMs = durationMs;
        this.details = Collections.unmodifiableMap(new LinkedHashMap<>(details));
    }

    public HealthStatus status() {
        return status;
    }

    public long durationMs() {
        return durationMs;
    }

    public Map<String, Object> details() {
        return details;
    }

    public static Builder up() {
        return new Builder(HealthStatus.UP);
    }

    public static Builder degraded() {
        return new Builder(HealthStatus.DEGRADED);
    }

    public static Builder down() {
        return new Builder(HealthStatus.DOWN);
    }

    public static final class Builder {
        private final HealthStatus status;
        private final Map<String, Object> details = new LinkedHashMap<>();
        private long durationMs;

        public Builder(HealthStatus status) {
            this.status = status;
        }

        public Builder detail(String k, Object v) {
            details.put(k, v);
            return this;
        }

        public Builder durationMs(long ms) {
            this.durationMs = ms;
            return this;
        }

        public HealthReport build() {
            return new HealthReport(status, durationMs, details);
        }
    }
}
