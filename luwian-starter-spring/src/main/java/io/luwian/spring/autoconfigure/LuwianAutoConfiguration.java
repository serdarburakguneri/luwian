package io.luwian.spring.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.luwian.spring.observability.logging.LuwianJsonLoggingConfig;
import io.luwian.spring.observability.metrics.LuwianMetricsConfig;
import io.luwian.spring.observability.tracing.LuwianTracingConfig;
import io.luwian.spring.observability.errors.LuwianErrorHandler;
import io.luwian.spring.observability.errors.LuwianErrorProperties;
import io.luwian.spring.corebridge.CoreBridgeConfiguration;

/**
 * Spring Boot AutoConfiguration entry for Luwian.
 */
@AutoConfiguration
@ConditionalOnWebApplication
@Import(CoreBridgeConfiguration.class) 
public class LuwianAutoConfiguration {

    // --- Error handling ---
    @Bean
    LuwianErrorProperties luwianErrorProperties() {
        return new LuwianErrorProperties();
    }

    @Bean
    LuwianErrorHandler luwianErrorHandler(LuwianErrorProperties props) {
        return new LuwianErrorHandler(props);
    }

    // --- Logging JSON config (conditional) ---
    @Bean
    @ConditionalOnProperty(value = "luwian.logging.json", havingValue = "true", matchIfMissing = true)
    LuwianJsonLoggingConfig luwianJsonLoggingConfig() {
        return new LuwianJsonLoggingConfig();
    }


    // --- Metrics (conditional) ---
    @Bean
    @ConditionalOnProperty(value = "luwian.metrics.enabled", havingValue = "true", matchIfMissing = true)
    LuwianMetricsConfig luwianMetricsConfig() {
        return new LuwianMetricsConfig();
    }

    // --- Tracing (conditional) ---
    @Bean
    @ConditionalOnProperty(value = "luwian.tracing.enabled", havingValue = "true", matchIfMissing = true)
    LuwianTracingConfig luwianTracingConfig() {
        return new LuwianTracingConfig();
    }
}