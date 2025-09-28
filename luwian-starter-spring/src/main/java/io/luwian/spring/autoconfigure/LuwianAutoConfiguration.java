package io.luwian.spring.autoconfigure;

import io.luwian.core.logging.HttpLogger;
import io.luwian.core.obs.CorrelationContext;
import io.luwian.spring.corebridge.CoreBridgeConfiguration;
import io.luwian.spring.observability.errors.LuwianErrorHandler;
import io.luwian.spring.observability.errors.LuwianErrorProperties;
import io.luwian.spring.observability.health.LuwianHealthProperties;
import io.luwian.spring.observability.logging.LuwianCorrelationFilter;
import io.luwian.spring.observability.logging.LuwianHttpLoggingFilter;
import io.luwian.spring.observability.logging.LuwianJsonLoggingConfig;
import io.luwian.spring.observability.logging.LuwianLoggingProperties;
import io.luwian.spring.observability.metrics.LuwianMetricsConfig;
import io.luwian.spring.observability.metrics.LuwianMetricsProperties;
import io.luwian.spring.observability.tracing.LuwianTracingConfig;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/** Spring Boot AutoConfiguration entry for Luwian. */
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties({
    LuwianErrorProperties.class,
    LuwianMetricsProperties.class,
    LuwianHealthProperties.class,
    LuwianLoggingProperties.class
})
@Import(CoreBridgeConfiguration.class)
public class LuwianAutoConfiguration {

    // --- Logging (JSON hint) ---
    @Bean
    @ConditionalOnProperty(
            value = "luwian.logging.json",
            havingValue = "true",
            matchIfMissing = true)
    LuwianJsonLoggingConfig luwianJsonLoggingConfig() {
        return new LuwianJsonLoggingConfig();
    }

    // --- Error handler ---
    @Bean
    LuwianErrorHandler luwianErrorHandler(
            LuwianErrorProperties props, io.luwian.spring.corebridge.ProblemDetailFactory factory) {
        return new LuwianErrorHandler(props, factory);
    }

    // --- Metrics (common tags) ---
    @Bean
    @ConditionalOnProperty(
            value = "luwian.metrics.enabled",
            havingValue = "true",
            matchIfMissing = true)
    LuwianMetricsConfig luwianMetricsConfig(
            LuwianMetricsProperties props, @Value("${spring.application.name:}") String appName) {
        return new LuwianMetricsConfig(props, appName);
    }

    @Bean
    @ConditionalOnProperty(
            value = "luwian.metrics.enabled",
            havingValue = "true",
            matchIfMissing = true)
    InitializingBean luwianMetricsCommonTagsInitializer(
            LuwianMetricsConfig config, MeterRegistry registry) {
        return () -> config.configure(registry);
    }

    // --- Optional: AOP TimedAspect support if Micrometer AOP & AOP infra are present ---
    @Bean
    @ConditionalOnClass(
            name = {
                "io.micrometer.core.aop.TimedAspect",
                "org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator"
            })
    @ConditionalOnProperty(
            value = "luwian.metrics.aop-timed",
            havingValue = "true",
            matchIfMissing = true)
    io.micrometer.core.aop.TimedAspect luwianTimedAspect(
            io.micrometer.core.instrument.MeterRegistry registry) {
        return new io.micrometer.core.aop.TimedAspect(registry);
    }

    // --- Health: liveness/readiness contributors are auto-registered via @Component ---

    // --- Tracing toggle placeholder ---
    @Bean
    @ConditionalOnProperty(
            value = "luwian.tracing.enabled",
            havingValue = "true",
            matchIfMissing = true)
    LuwianTracingConfig luwianTracingConfig() {
        return new LuwianTracingConfig();
    }

    // --- Filters ---
    @Bean
    @ConditionalOnProperty(
            value = "luwian.logging.http.enabled",
            havingValue = "true",
            matchIfMissing = true)
    LuwianHttpLoggingFilter luwianHttpLoggingFilter(
            HttpLogger httpLogger, LuwianLoggingProperties loggingProps) {
        boolean includeBody = loggingProps.getHttp().isBody();
        return new LuwianHttpLoggingFilter(httpLogger, includeBody);
    }

    @Bean
    LuwianCorrelationFilter luwianCorrelationFilter(
            CorrelationContext correlationContext,
            @Value("${luwian.tenancy.header:X-Tenant}") String tenantHeader) {
        return new LuwianCorrelationFilter(correlationContext, tenantHeader);
    }
}
