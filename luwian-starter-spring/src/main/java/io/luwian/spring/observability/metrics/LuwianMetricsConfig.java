package io.luwian.spring.observability.metrics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Applies global tags to all Micrometer meters (service, environment, version).
 */
@Component
public class LuwianMetricsConfig {

    private final LuwianMetricsProperties props;
    private final String springAppName;

    public LuwianMetricsConfig(
            LuwianMetricsProperties props,
            @Value("${spring.application.name:}") String springAppName
    ) {
        this.props = props;
        this.springAppName = springAppName;
    }

    public void configure(MeterRegistry registry) {
        String service = (props.getService() == null || props.getService().isBlank())
                ? (springAppName == null || springAppName.isBlank() ? MetricsConstants.DEFAULT_SERVICE_NAME : springAppName)
                : props.getService();

        registry.config().commonTags(
                MetricsConstants.SERVICE_TAG, service,
                MetricsConstants.ENVIRONMENT_TAG, props.getEnvironment(),
                MetricsConstants.VERSION_TAG, props.getVersion()
        );
    }
}
