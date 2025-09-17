package io.luwian.spring.observability.errors;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configuration properties for Luwian error handling. */
@ConfigurationProperties(prefix = "luwian.error")
public class LuwianErrorProperties {

    /**
     * Controls whether stacktrace-like details are included in Problem+JSON.
     * Values: never | on-trace (default on-trace)
     */
    private IncludeStacktracePolicy includeStacktrace = IncludeStacktracePolicy.ON_TRACE;

    public IncludeStacktracePolicy getIncludeStacktrace() {
        return includeStacktrace;
    }

    public void setIncludeStacktrace(IncludeStacktracePolicy includeStacktrace) {
        this.includeStacktrace = includeStacktrace;
    }
}
