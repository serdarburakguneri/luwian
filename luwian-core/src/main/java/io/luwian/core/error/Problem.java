package io.luwian.core.error;

import java.net.URI;
import java.util.Map;

/** RFC 7807 Problem abstraction with an extensible payload. */
public interface Problem {
    URI type();

    String title();

    int status();

    String detail();

    URI instance();

    Map<String, Object> extensions();

    /** Fluent builder for Problems. */
    interface Builder {
        Builder type(URI type);

        Builder title(String title);

        Builder status(int httpStatus);

        Builder detail(String detail);

        Builder instance(URI instance);

        Builder put(String key, Object value);

        Problem build();
    }
}
