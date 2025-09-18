package io.luwian.spring.observability.health;

import io.luwian.core.health.CompositeHealthCheck;
import io.luwian.core.health.HealthConstants;
import io.luwian.core.health.HealthReport;
import io.luwian.core.health.builtin.HeapHealthCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/** Readiness: process is ready to serve (heap not under pressure, etc.). */
@Component
public class LuwianReadinessIndicator implements HealthIndicator {

    private final CompositeHealthCheck checks;

    @Autowired
    public LuwianReadinessIndicator(LuwianHealthProperties properties) {
        this.checks = new CompositeHealthCheck();
        if (properties.isHeap()) {
            checks.register(
                    HealthConstants.COMPONENT_HEAP,
                    new HeapHealthCheck(
                            properties.getHeapMinFreeBytes(), properties.getHeapMinFreeRatio()));
        }
    }

    @Override
    public Health health() {
        HealthReport r = checks.check();
        Health.Builder b;
        switch (r.status()) {
            case UP:
                b = Health.up();
                break;
            case DEGRADED:
                b = Health.status(HealthConstants.STATUS_DEGRADED);
                break;
            case DOWN:
                b = Health.down();
                break;
            default:
                b = Health.down();
                break;
        }
        return b.withDetails(r.details()).build();
    }
}
