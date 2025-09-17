package io.luwian.spring.observability.logging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Logs HTTP request/response at INFO with duration and redacted headers.
 * Body logging is optional (luwian.logging.http.body=true).
 */
public class LuwianHttpLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LuwianHttpLoggingFilter.class);

    // Redacted headers for security
    private static final Set<String> REDACT_HEADERS = Set.of(
            "authorization", "cookie", "set-cookie", "proxy-authorization"
    );

    // Log event keys
    private static final String KEY_TIMESTAMP = "ts";
    private static final String KEY_METHOD = "method";
    private static final String KEY_PATH = "path";
    private static final String KEY_QUERY = "query";
    private static final String KEY_SCHEME = "scheme";
    private static final String KEY_HOST = "host";
    private static final String KEY_STATUS = "status";
    private static final String KEY_DURATION_MS = "duration_ms";
    private static final String KEY_CORRELATION_ID = "correlationId";
    private static final String KEY_REQ_HEADERS = "req_headers";
    private static final String KEY_RES_HEADERS = "res_headers";
    private static final String KEY_REQ_BODY = "req_body";
    private static final String KEY_RES_BODY = "res_body";

    // HTTP headers
    private static final String HEADER_HOST = "host";
    private static final String HEADER_CORRELATION_ID = "x-correlation-id";

    // Log messages
    private static final String LOG_HTTP_EVENT = "http {}";

    // Body truncation
    private static final int MAX_BODY_LENGTH = 4096;
    private static final String TRUNCATION_SUFFIX = "...(truncated)";

    // Redaction placeholder
    private static final String REDACTED_VALUE = "***";

    private final LuwianLoggingProperties props;

    public LuwianHttpLoggingFilter(LuwianLoggingProperties props) {
        this.props = props;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        boolean logBody = props.getHttp().isBody();

        HttpServletRequest req = request;
        HttpServletResponse res = response;

        if (logBody) {
            req = new ContentCachingRequestWrapper(request);
            res = new ContentCachingResponseWrapper(response);
        }

        long start = System.nanoTime();
        String method = req.getMethod();
        String path = req.getRequestURI();
        String query = Optional.ofNullable(req.getQueryString()).orElse("");
        String scheme = req.getScheme();
        String host = Optional.ofNullable(req.getHeader(HEADER_HOST)).orElse("");
        String correlationId = Optional.ofNullable(req.getHeader(HEADER_CORRELATION_ID)).orElse("");

        try {
            chain.doFilter(req, res);
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            int status = (res instanceof ContentCachingResponseWrapper)
                    ? ((ContentCachingResponseWrapper) res).getStatus()
                    : response.getStatus();

            Map<String, Object> evt = new LinkedHashMap<>();
            evt.put(KEY_TIMESTAMP, Instant.now().toString());
            evt.put(KEY_METHOD, method);
            evt.put(KEY_PATH, path);
            if (!query.isEmpty()) evt.put(KEY_QUERY, query);
            evt.put(KEY_SCHEME, scheme);
            evt.put(KEY_HOST, host);
            evt.put(KEY_STATUS, status);
            evt.put(KEY_DURATION_MS, durationMs);
            if (!correlationId.isEmpty()) evt.put(KEY_CORRELATION_ID, correlationId);

            evt.put(KEY_REQ_HEADERS, redactHeaders(Collections.list(req.getHeaderNames()), req));
            evt.put(KEY_RES_HEADERS, redactHeaders(response.getHeaderNames(), response));

            if (logBody) {
                String reqBody = readCachedRequestBody(req);
                String resBody = readCachedResponseBody(res);
                if (!reqBody.isBlank()) evt.put(KEY_REQ_BODY, truncate(reqBody, MAX_BODY_LENGTH));
                if (!resBody.isBlank()) evt.put(KEY_RES_BODY, truncate(resBody, MAX_BODY_LENGTH));
                if (res instanceof ContentCachingResponseWrapper ccrw) {
                    ccrw.copyBodyToResponse();
                }
            }

            log.info(LOG_HTTP_EVENT, evt);
        }
    }

    private Map<String, Object> redactHeaders(Collection<String> names, HttpServletRequest req) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (String n : names) {
            String key = n.toLowerCase(Locale.ROOT);
            if (REDACT_HEADERS.contains(key)) {
                m.put(n, REDACTED_VALUE);
            } else {
                m.put(n, Collections.list(req.getHeaders(n)));
            }
        }
        return m;
    }

    private Map<String, Object> redactHeaders(Collection<String> names, HttpServletResponse res) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (String n : names) {
            String key = n.toLowerCase(Locale.ROOT);
            if (REDACT_HEADERS.contains(key)) {
                m.put(n, REDACTED_VALUE);
            } else {
                m.put(n, new ArrayList<>(res.getHeaders(n)));
            }
        }
        return m;
    }

    private static String readCachedRequestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper ccrw) {
            byte[] buf = ccrw.getContentAsByteArray();
            return new String(buf, StandardCharsets.UTF_8);
        }
        return "";
    }

    private static String readCachedResponseBody(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper ccrw) {
            byte[] buf = ccrw.getContentAsByteArray();
            return new String(buf, StandardCharsets.UTF_8);
        }
        return "";
    }

    private static String truncate(String s, int max) {
        return (s.length() <= max) ? s : s.substring(0, max) + TRUNCATION_SUFFIX;
    }
}
