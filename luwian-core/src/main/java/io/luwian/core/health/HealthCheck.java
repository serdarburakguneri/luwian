package io.luwian.core.health;

@FunctionalInterface
public interface HealthCheck {
    HealthReport check() throws Exception;

    /** Utility to time a check. */
    default HealthReport timed() {
        long t0 = System.nanoTime();
        try {
            HealthReport r = check();
            return new HealthReport.Builder(r.status())
                    .durationMs((System.nanoTime() - t0) / 1_000_000)
                    .build();
        } catch (Exception e) {
            return HealthReport.down()
                    .detail(
                            HealthConstants.DETAIL_ERROR,
                            e.getClass().getSimpleName() + ": " + e.getMessage())
                    .durationMs((System.nanoTime() - t0) / 1_000_000)
                    .build();
        }
    }
}
