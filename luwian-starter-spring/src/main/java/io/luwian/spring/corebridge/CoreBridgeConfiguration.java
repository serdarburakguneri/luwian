package io.luwian.spring.corebridge;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.luwian.core.error.DefaultErrorCatalog;
import io.luwian.core.error.ErrorCatalog;
import io.luwian.core.logging.DefaultRedactionPolicy;
import io.luwian.core.logging.HttpLogger;
import io.luwian.core.logging.RedactionPolicy;
import io.luwian.core.logging.Slf4jHttpLogger;
import io.luwian.core.obs.CorrelationContext;
import io.luwian.core.obs.ThreadLocalCorrelationContext;

/** Wires Spring ↔ Luwian Core adapters when luwian-core is present. */
@Configuration
@ConditionalOnClass(name = "io.luwian.core.error.Problem")
public class CoreBridgeConfiguration {

    @Bean
    public ErrorCatalog luwianErrorCatalog() {
        return new DefaultErrorCatalog();
    }

    @Bean
    public RedactionPolicy luwianRedactionPolicy() {
        return new DefaultRedactionPolicy();
    }

    @Bean
    public HttpLogger luwianHttpLogger(RedactionPolicy redactionPolicy) {
        return new Slf4jHttpLogger(redactionPolicy);
    }

    @Bean
    public CorrelationContext luwianCorrelationContext() {
        // Bridge core correlation context with SLF4J MDC
        return new ThreadLocalCorrelationContext(new CorrelationContext.MdcBridge() {
            @Override public void put(String key, String value) { MDC.put(key, value); }
            @Override public void remove(String key) { MDC.remove(key); }
        });
    }

    @Bean
    public ProblemDetailFactory luwianProblemDetailFactory(ErrorCatalog catalog, CorrelationContext correlationContext) {
        return new ProblemDetailFactory(catalog, correlationContext);
    }
}