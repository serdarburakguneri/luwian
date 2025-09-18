package io.luwian.core.health.builtin;

import io.luwian.core.health.HealthCheck;
import io.luwian.core.health.HealthConstants;
import io.luwian.core.health.HealthReport;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;

/** Flags DOWN if any threads are deadlocked. */
public class DeadlockHealthCheck implements HealthCheck {
    @Override
    public HealthReport check() {
        long t0 = System.nanoTime();
        ThreadMXBean mx = ManagementFactory.getThreadMXBean();
        long[] ids = mx.findDeadlockedThreads();
        long dur = (System.nanoTime() - t0) / 1_000_000;
        if (ids == null || ids.length == 0) {
            return HealthReport.up().durationMs(dur).build();
        }
        ThreadInfo[] info = mx.getThreadInfo(ids, Integer.MAX_VALUE);
        return HealthReport.down()
                .detail(
                        HealthConstants.DETAIL_DEADLOCKED_THREADS,
                        Arrays.stream(info).map(ThreadInfo::getThreadName).toList())
                .durationMs(dur)
                .build();
    }
}
