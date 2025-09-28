package io.luwian.spring.observability.logging;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Properties for Luwian logging features. */
@ConfigurationProperties(prefix = "luwian.logging")
public class LuwianLoggingProperties {

    /** If true, emit JSON logs when encoder is present. */
    private boolean json = true;

    /** HTTP logging options. */
    private final Http http = new Http();

    public boolean isJson() {
        return json;
    }

    public void setJson(boolean json) {
        this.json = json;
    }

    public Http getHttp() {
        return http;
    }

    public static class Http {
        /** If true, include request/response bodies in HTTP logs. */
        private boolean body = false;

        public boolean isBody() {
            return body;
        }

        public void setBody(boolean body) {
            this.body = body;
        }
    }
}
