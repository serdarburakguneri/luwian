package io.luwian.spring.observability.health;

import io.luwian.core.health.CompositeHealthCheck;
import io.luwian.core.health.HealthConstants;
import io.luwian.core.health.HealthReport;
import io.luwian.core.health.builtin.DeadlockHealthCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/** Liveness: process is healthy (no deadlocks, etc.). */
@Component
public class LuwianLivenessIndicator implements HealthIndicator {

    private final CompositeHealthCheck checks;

    @Autowired
    public LuwianLivenessIndicator(LuwianHealthProperties properties) {
        this.checks = new CompositeHealthCheck();
        if (properties.isDeadlock()) {
            checks.register(HealthConstants.COMPONENT_DEADLOCK, new DeadlockHealthCheck());
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
