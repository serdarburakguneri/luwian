package io.luwian.core.logging;

import java.time.Instant;
import java.util.Map;

import io.luwian.core.http.HttpExchange;

/** Contract to log a structured HTTP event. Implementations may use SLF4J or send elsewhere. */
public interface HttpLogger {
    void log(Instant timestamp, long durationMs, HttpExchange.Request req, HttpExchange.Response res, Map<String,Object> context);
}
