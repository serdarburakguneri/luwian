package io.luwian.core.logging;

import io.luwian.core.http.HttpExchange;
import java.time.Instant;
import java.util.Map;

/** Contract to log a structured HTTP event. Implementations may use SLF4J or send elsewhere. */
public interface HttpLogger {
    void log(
            Instant timestamp,
            long durationMs,
            HttpExchange.Request req,
            HttpExchange.Response res,
            Map<String, Object> context);
}
