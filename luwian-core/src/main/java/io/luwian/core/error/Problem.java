package io.luwian.core.error;

import java.net.URI;
import java.util.Map;

/** Framework-agnostic RFC7807 Problem payload. */
public interface Problem {
    URI type();
    String title();
    int status();
    String detail();     
    URI instance();       
    Map<String,Object> extensions();

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
