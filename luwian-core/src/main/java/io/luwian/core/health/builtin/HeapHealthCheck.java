package io.luwian.core.health.builtin;

import io.luwian.core.health.HealthCheck;
import io.luwian.core.health.HealthConstants;
import io.luwian.core.health.HealthReport;
import io.luwian.core.health.HealthStatus;

/** Simple heap pressure check with absolute and ratio thresholds. */
public class HeapHealthCheck implements HealthCheck {

    private final long minFreeBytes;
    private final double minFreeRatio;

    public HeapHealthCheck(long minFreeBytes, double minFreeRatio) {
        this.minFreeBytes = Math.max(0, minFreeBytes);
        this.minFreeRatio = Math.max(0.0, Math.min(1.0, minFreeRatio));
    }

    @Override
    public HealthReport check() {
        long t0 = System.nanoTime();
        long free = Runtime.getRuntime().freeMemory();
        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        double ratio = max > 0 ? (double) free / (double) max : 1.0;
        long dur = (System.nanoTime() - t0) / 1_000_000;

        HealthStatus s = HealthStatus.UP;
        if (free < minFreeBytes || ratio < minFreeRatio) {
            s = (ratio < minFreeRatio / 2.0) ? HealthStatus.DOWN : HealthStatus.DEGRADED;
        }
        return new HealthReport.Builder(s)
                .detail(HealthConstants.DETAIL_FREE_BYTES, free)
                .detail(HealthConstants.DETAIL_MAX_BYTES, max)
                .detail(HealthConstants.DETAIL_TOTAL_BYTES, total)
                .detail(HealthConstants.DETAIL_FREE_RATIO, ratio)
                .durationMs(dur)
                .build();
    }
}
