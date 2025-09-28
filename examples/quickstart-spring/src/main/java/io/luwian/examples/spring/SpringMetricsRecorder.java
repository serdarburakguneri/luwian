package io.luwian.examples.spring;

import io.luwian.core.metrics.MetricsRecorder;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class SpringMetricsRecorder implements MetricsRecorder {

    private final MeterRegistry registry;

    public SpringMetricsRecorder(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void increment(String counterName, Map<String, String> tags) {
        registry.counter(counterName, toArray(tags)).increment();
    }

    @Override
    public void record(String timerName, long durationMs, Map<String, String> tags) {
        registry.timer(timerName, toArray(tags)).record(durationMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public TimerContext startTimer(String timerName, Map<String, String> tags) {
        long start = System.nanoTime();
        return () ->
                registry.timer(timerName, toArray(tags))
                        .record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    }

    private static String[] toArray(Map<String, String> tags) {
        return tags.entrySet().stream()
                .flatMap(e -> java.util.stream.Stream.of(e.getKey(), e.getValue()))
                .toArray(String[]::new);
    }
}
