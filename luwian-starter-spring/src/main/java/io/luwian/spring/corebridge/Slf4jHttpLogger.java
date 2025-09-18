package io.luwian.spring.corebridge;

import io.luwian.core.http.HttpExchange;
import io.luwian.core.logging.HttpLogger;
import io.luwian.core.logging.RedactionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;

/** Logs a single structured line via SLF4J; JSON if encoder is configured. */
public class Slf4jHttpLogger implements HttpLogger {

    private static final Logger log = LoggerFactory.getLogger(Slf4jHttpLogger.class);

    // Log event keys
    private static final String KEY_TIMESTAMP = "ts";
    private static final String KEY_DURATION_MS = "duration_ms";
    private static final String KEY_METHOD = "method";
    private static final String KEY_PATH = "path";
    private static final String KEY_QUERY = "query";
    private static final String KEY_STATUS = "status";
    private static final String KEY_REQ_HEADERS = "req_headers";
    private static final String KEY_RES_HEADERS = "res_headers";
    private static final String KEY_REQ_BODY = "req_body";
    private static final String KEY_RES_BODY = "res_body";

    // Log message
    private static final String LOG_HTTP_EVENT = "http {}";

    // Body truncation
    private static final int MAX_BODY_LENGTH = 4096;
    private static final String TRUNCATION_SUFFIX = "...(truncated)";

    private final RedactionPolicy redaction;

    public Slf4jHttpLogger(RedactionPolicy redaction) {
        this.redaction = redaction;
    }

    @Override
    public void log(Instant ts, long durationMs, HttpExchange.Request req, HttpExchange.Response res, Map<String, Object> ctx) {
        Map<String, Object> evt = new LinkedHashMap<>();
        evt.put(KEY_TIMESTAMP, ts.toString());
        evt.put(KEY_DURATION_MS, durationMs);
        evt.put(KEY_METHOD, req.method());
        evt.put(KEY_PATH, req.path());
        req.query().ifPresent(q -> evt.put(KEY_QUERY, q));
        evt.put(KEY_STATUS, res.status());
        evt.put(KEY_REQ_HEADERS, redaction.redactHeaders(req.headers()));
        evt.put(KEY_RES_HEADERS, redaction.redactHeaders(res.headers()));
        req.body().ifPresent(b -> evt.put(KEY_REQ_BODY, safeString(b)));
        res.body().ifPresent(b -> evt.put(KEY_RES_BODY, safeString(b)));
        if (ctx != null && !ctx.isEmpty()) evt.putAll(ctx);
        log.info(LOG_HTTP_EVENT, evt);
    }

    private static String safeString(byte[] bytes) {
        String s = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        return s.length() <= MAX_BODY_LENGTH ? s : s.substring(0, MAX_BODY_LENGTH) + TRUNCATION_SUFFIX;
    }
}
