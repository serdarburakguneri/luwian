package io.luwian.core.error;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultErrorCatalogTest {

    @Test
    void resolvesFallbackWhenUnknown() {
        var cat = new DefaultErrorCatalog();
        var ec = cat.resolve(new RuntimeException("x")).orElseThrow();
        assertThat(ec.code()).isEqualTo("LUW-INT-000");
        assertThat(ec.httpStatus()).isEqualTo(500);
    }
}
