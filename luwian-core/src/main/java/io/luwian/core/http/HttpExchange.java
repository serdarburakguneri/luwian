package io.luwian.core.http;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Minimal HTTP abstraction to keep logging logic portable. */
public interface HttpExchange {
    interface Request {
        String method();
        String path();
        Optional<String> query();
        Map<String, List<String>> headers();
        Optional<byte[]> body(); 
    }
    interface Response {
        int status();
        Map<String, List<String>> headers();
        Optional<byte[]> body(); 
    }
}
