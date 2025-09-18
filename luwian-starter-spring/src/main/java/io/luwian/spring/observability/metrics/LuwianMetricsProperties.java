package io.luwian.spring.observability.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Luwian metrics configuration. */
@ConfigurationProperties(prefix = "luwian.metrics")
public class LuwianMetricsProperties {

    /** Enable Micrometer customizations (common tags, etc). Default true. */
    private boolean enabled = true;

    /** Service name tag (defaults to spring.application.name if empty). */
    private String service;

    /** Environment tag (e.g., dev/stage/prod). */
    private String environment = "dev";

    /** Version tag (e.g., from build info). */
    private String version = "unknown";

    /** If true and AOP present, registers TimedAspect for @Timed. */
    private boolean aopTimed = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isAopTimed() {
        return aopTimed;
    }

    public void setAopTimed(boolean aopTimed) {
        this.aopTimed = aopTimed;
    }
}
