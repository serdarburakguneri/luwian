package io.luwian.spring.observability.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;

/**
 * Adds global tags (service, env, version) to all metrics.
 */
public class LuwianMetricsConfig implements MeterRegistryCustomizer<MeterRegistry> {

    @Override
    public void customize(MeterRegistry registry) {
        // TODO: add common tags from env/build info.
    }
}
