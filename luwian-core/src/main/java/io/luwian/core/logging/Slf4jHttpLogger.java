package io.luwian.core.logging;

import io.luwian.core.http.HttpExchange;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Structured HTTP logger via SLF4J; emits JSON if encoder configured. */
public class Slf4jHttpLogger implements HttpLogger {

    private static final Logger log = LoggerFactory.getLogger(Slf4jHttpLogger.class);
    private final RedactionPolicy redaction;

    public Slf4jHttpLogger(RedactionPolicy redaction) {
        this.redaction = redaction;
    }

    @Override
    public void log(
            Instant ts,
            long durationMs,
            HttpExchange.Request req,
            HttpExchange.Response res,
            Map<String, Object> ctx) {
        Map<String, Object> evt = new LinkedHashMap<>();
        evt.put(LoggingConstants.TIMESTAMP_FIELD, ts.toString());
        evt.put(LoggingConstants.DURATION_MS_FIELD, durationMs);
        evt.put(LoggingConstants.METHOD_FIELD, req.method());
        evt.put(LoggingConstants.PATH_FIELD, req.path());
        req.query().ifPresent(q -> evt.put(LoggingConstants.QUERY_FIELD, q));
        evt.put(LoggingConstants.STATUS_FIELD, res.status());
        evt.put(LoggingConstants.REQ_HEADERS_FIELD, redaction.redactHeaders(req.headers()));
        evt.put(LoggingConstants.RES_HEADERS_FIELD, redaction.redactHeaders(res.headers()));
        req.body().ifPresent(b -> evt.put(LoggingConstants.REQ_BODY_FIELD, safe(b)));
        res.body().ifPresent(b -> evt.put(LoggingConstants.RES_BODY_FIELD, safe(b)));
        if (ctx != null && !ctx.isEmpty()) evt.putAll(ctx);
        log.info(LoggingConstants.HTTP_LOG_MESSAGE, evt);
    }

    private static String safe(byte[] bytes) {
        String s = new String(bytes, StandardCharsets.UTF_8);
        return s.length() <= LoggingConstants.MAX_BODY_LENGTH
                ? s
                : s.substring(0, LoggingConstants.MAX_BODY_LENGTH)
                        + LoggingConstants.TRUNCATED_SUFFIX;
    }
}
