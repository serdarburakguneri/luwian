package io.luwian.core.metrics;

import java.util.Map;

/** Metrics recorder. */
public interface MetricsRecorder {

    void increment(String counterName, Map<String, String> tags);

    void record(String timerName, long durationMs, Map<String, String> tags);

    TimerContext startTimer(String timerName, Map<String, String> tags);

    interface TimerContext extends AutoCloseable {
        @Override
        void close();
    }
}
