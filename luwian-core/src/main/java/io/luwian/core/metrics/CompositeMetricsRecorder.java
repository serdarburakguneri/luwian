package io.luwian.core.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Fan-out recorder to multiple delegates. */
public final class CompositeMetricsRecorder implements MetricsRecorder {

    private final List<MetricsRecorder> delegates;

    public CompositeMetricsRecorder(List<MetricsRecorder> delegates) {
        this.delegates = List.copyOf(delegates);
    }

    @Override
    public void increment(String counterName, Map<String, String> tags) {
        for (MetricsRecorder d : delegates) d.increment(counterName, tags);
    }

    @Override
    public void record(String timerName, long durationMs, Map<String, String> tags) {
        for (MetricsRecorder d : delegates) d.record(timerName, durationMs, tags);
    }

    @Override
    public TimerContext startTimer(String timerName, Map<String, String> tags) {
        List<TimerContext> ctxs = new ArrayList<>(delegates.size());
        for (MetricsRecorder d : delegates) ctxs.add(d.startTimer(timerName, tags));
        return () -> {
            for (TimerContext c : ctxs) c.close();
        };
    }
}
