package io.luwian.spring.observability.logging;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Luwian logging configuration. */
@ConfigurationProperties(prefix = "luwian.logging")
public class LuwianLoggingProperties {

    /**
     * When true, prefer JSON log output (requires Logback + logstash encoder on classpath).
     * Note: actual logging config file is still user-controlled; we only detect & guide.
     */
    private boolean json = true;

    private final Http http = new Http();

    /** Name of the tenant header to read (also used by correlation filter). */
    private String tenancyHeader = "X-Tenant";

    public boolean isJson() { return json; }
    public void setJson(boolean json) { this.json = json; }

    public Http getHttp() { return http; }

    public String getTenancyHeader() { return tenancyHeader; }
    public void setTenancyHeader(String tenancyHeader) { this.tenancyHeader = tenancyHeader; }

    public static class Http {
        private boolean body = false;
        public boolean isBody() { return body; }
        public void setBody(boolean body) { this.body = body; }
    }
}
