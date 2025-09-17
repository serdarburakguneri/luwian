package io.luwian.spring.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.luwian.spring.observability.errors.LuwianErrorHandler;
import io.luwian.spring.observability.errors.LuwianErrorProperties;
import io.luwian.spring.observability.logging.LuwianCorrelationFilter;
import io.luwian.spring.observability.logging.LuwianHttpLoggingFilter;
import io.luwian.spring.observability.logging.LuwianJsonLoggingConfig;
import io.luwian.spring.observability.logging.LuwianLoggingProperties;
import io.luwian.spring.observability.metrics.LuwianMetricsConfig;
import io.luwian.spring.observability.tracing.LuwianTracingConfig;

/**
 * Spring Boot AutoConfiguration entry for Luwian.
 */
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties({ LuwianErrorProperties.class, LuwianLoggingProperties.class })
public class LuwianAutoConfiguration {

    // --- Logging JSON config (conditional) ---
    @Bean
    @ConditionalOnProperty(value = "luwian.logging.json", havingValue = "true", matchIfMissing = true)
    LuwianJsonLoggingConfig luwianJsonLoggingConfig() {
        return new LuwianJsonLoggingConfig();
    }

    // --- Correlation & HTTP logging filters ---
    @Bean
    LuwianCorrelationFilter luwianCorrelationFilter(LuwianLoggingProperties props) {
        return new LuwianCorrelationFilter(props);
    }

    @Bean
    LuwianHttpLoggingFilter luwianHttpLoggingFilter(LuwianLoggingProperties props) {
        return new LuwianHttpLoggingFilter(props);
    }

    // --- Error handler (always present) ---
    @Bean
    LuwianErrorHandler luwianErrorHandler(LuwianErrorProperties props) {
        return new LuwianErrorHandler(props);
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