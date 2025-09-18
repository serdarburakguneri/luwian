package io.luwian.spring.observability.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

class LuwianMetricsConfigTest {

    @Test
    void commonTagsAreApplied() {
        var props = new LuwianMetricsProperties();
        props.setService("svc");
        props.setEnvironment("dev");
        props.setVersion("1.2.3");

        var cfg = new LuwianMetricsConfig(props, "ignored");
        var reg = new SimpleMeterRegistry();
        cfg.customize(reg);

        reg.counter("test.counter").increment();
        var m = reg.get("test.counter").counter();
        assertThat(m.getId().getTags())
                .extracting(t -> t.getKey() + "=" + t.getValue())
                .contains("service=svc", "env=dev", "version=1.2.3");
    }
}
