package io.luwian.core.error;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class BasicProblemTest {

    @Test
    void buildsProblem() {
        Problem p = new BasicProblem.Builder()
                .type(URI.create("https://errors/boom"))
                .title("Boom")
                .status(500)
                .detail("x")
                .instance(URI.create("/t"))
                .put("k","v")
                .build();

        assertThat(p.title()).isEqualTo("Boom");
        assertThat(p.status()).isEqualTo(500);
        assertThat(p.extensions()).containsEntry("k","v");
    }
}
