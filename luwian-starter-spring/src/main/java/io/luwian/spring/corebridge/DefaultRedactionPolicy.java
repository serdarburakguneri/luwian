package io.luwian.spring.corebridge;

import io.luwian.core.logging.RedactionPolicy;

import java.nio.charset.StandardCharsets;
import java.util.*;

/** Minimal redaction: redact auth/cookie headers; pass body through. */
public class DefaultRedactionPolicy implements RedactionPolicy {

    // Headers to redact
    private static final String HEADER_AUTHORIZATION = "authorization";
    private static final String HEADER_COOKIE = "cookie";
    private static final String HEADER_SET_COOKIE = "set-cookie";
    private static final String HEADER_PROXY_AUTHORIZATION = "proxy-authorization";

    // Redaction placeholder
    private static final String REDACTED_VALUE = "***";

    private static final Set<String> REDACT = Set.of(
        HEADER_AUTHORIZATION, HEADER_COOKIE, HEADER_SET_COOKIE, HEADER_PROXY_AUTHORIZATION
    );

    @Override
    public Map<String, List<String>> redactHeaders(Map<String, List<String>> original) {
        Map<String, List<String>> out = new LinkedHashMap<>();
        for (var e : original.entrySet()) {
            String k = e.getKey();
            if (REDACT.contains(k.toLowerCase(Locale.ROOT))) {
                out.put(k, List.of(REDACTED_VALUE));
            } else {
                out.put(k, e.getValue());
            }
        }
        return out;
    }

    @Override
    public byte[] redactBody(byte[] original) {
        return original; // future: JSON field redaction
    }
}
