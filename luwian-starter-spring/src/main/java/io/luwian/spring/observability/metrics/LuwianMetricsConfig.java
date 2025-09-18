package io.luwian.spring.observability.metrics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Applies global tags to all Micrometer meters (service, environment, version).
 */
public class LuwianMetricsConfig implements MeterRegistryCustomizer<MeterRegistry> {

    private final LuwianMetricsProperties props;
    private final String springAppName;

    public LuwianMetricsConfig(
            LuwianMetricsProperties props,
            @Value("${spring.application.name:}") String springAppName
    ) {
        this.props = props;
        this.springAppName = springAppName;
    }

    @Override
    public void customize(MeterRegistry registry) {
        String service = (props.getService() == null || props.getService().isBlank())
                ? (springAppName == null || springAppName.isBlank() ? "luwian-app" : springAppName)
                : props.getService();

        registry.config().commonTags(
                "service", service,
                "env", props.getEnvironment(),
                "version", props.getVersion()
        );
    }
}
