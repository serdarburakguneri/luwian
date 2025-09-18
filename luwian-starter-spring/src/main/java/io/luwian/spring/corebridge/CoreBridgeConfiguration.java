package io.luwian.spring.corebridge;

import io.luwian.core.logging.HttpLogger;
import io.luwian.core.logging.RedactionPolicy;
import io.luwian.core.obs.CorrelationContext;
import io.luwian.core.error.ErrorCatalog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Wires Spring ↔ Luwian Core adapters when luwian-core is present. */
@Configuration
@ConditionalOnClass(name = "io.luwian.core.error.Problem")
public class CoreBridgeConfiguration {

    @Bean
    public ErrorCatalog luwianSpringErrorCatalog() {
        return new SpringErrorCatalog();
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
        return new SpringCorrelationContext();
    }

    @Bean
    public ProblemDetailFactory luwianProblemDetailFactory(ErrorCatalog catalog, CorrelationContext correlationContext) {
        return new ProblemDetailFactory(catalog, correlationContext);
    }
}
