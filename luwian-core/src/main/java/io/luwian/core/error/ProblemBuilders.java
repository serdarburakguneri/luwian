package io.luwian.core.error;

import java.net.URI;
import java.time.OffsetDateTime;

/** Utilities for creating Problems with conventional defaults. */
public final class ProblemBuilders {

    private ProblemBuilders() {}

    public static Problem.Builder of(URI type, String title, int status) {
        return new BasicProblem.Builder()
                .type(type)
                .title(title)
                .status(status)
                .put(ErrorConstants.TIMESTAMP_PROPERTY, OffsetDateTime.now().toString());
    }
}
