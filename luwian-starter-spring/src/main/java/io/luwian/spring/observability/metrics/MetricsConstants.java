package io.luwian.spring.observability.metrics;

/** Constants for metrics configuration and tag names. */
public final class MetricsConstants {

private MetricsConstants() {} // Utility class

    // Tag Names
    public static final String SERVICE_TAG = "service";
    public static final String ENVIRONMENT_TAG = "env";
    public static final String VERSION_TAG = "version";

    // Default Values
    public static final String DEFAULT_SERVICE_NAME = "luwian-app";
}
