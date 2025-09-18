package io.luwian.spring.observability.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON logging helper: - If logstash-logback-encoder is on classpath and luwian.logging.json=true,
 * log a startup hint so users know JSON logging is active or explain how to enable it.
 *
 * <p>We intentionally do not force a logging config because Spring resolves logging setup before
 * auto-configuration. Users keep full control of logback-spring.xml.
 */
public class LuwianJsonLoggingConfig {

    private static final Logger log = LoggerFactory.getLogger(LuwianJsonLoggingConfig.class);

    // Class names
    private static final String LOGSTASH_ENCODER_CLASS =
            "net.logstash.logback.encoder.LogstashEncoder";

    // Log messages
    private static final String MSG_JSON_SUPPORTED =
            "Luwian: JSON logging is supported (logstash encoder detected). "
                    + "Ensure your logback-spring.xml uses LogstashEncoder for JSON output.";
    private static final String MSG_JSON_MISSING =
            "Luwian: JSON logging requested, but Logstash encoder not found. "
                    + "Add dependency: 'net.logstash.logback:logstash-logback-encoder'.";

    public LuwianJsonLoggingConfig() {
        boolean logstashPresent = isPresent(LOGSTASH_ENCODER_CLASS);
        if (logstashPresent) {
            log.info(MSG_JSON_SUPPORTED);
        } else {
            log.info(MSG_JSON_MISSING);
        }
    }

    private static boolean isPresent(String fqcn) {
        try {
            Class.forName(fqcn);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
