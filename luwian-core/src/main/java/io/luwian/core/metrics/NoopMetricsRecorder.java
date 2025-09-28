package io.luwian.core.metrics;

import java.util.Map;

/** No-op implementation used when no metrics backend is configured. */
public final class NoopMetricsRecorder implements MetricsRecorder {

    public static final NoopMetricsRecorder INSTANCE = new NoopMetricsRecorder();

    private NoopMetricsRecorder() {}

    @Override
    public void increment(String counterName, Map<String, String> tags) {}

    @Override
    public void record(String timerName, long durationMs, Map<String, String> tags) {}

    @Override
    public TimerContext startTimer(String timerName, Map<String, String> tags) {
        return () -> {};
    }
}
