package io.luwian.core.logging;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Simple header redaction policy for sensitive headers. */
public class DefaultRedactionPolicy implements RedactionPolicy {

    private static final Set<String> REDACT = Set.of(
        LoggingConstants.AUTHORIZATION_HEADER, 
        LoggingConstants.COOKIE_HEADER, 
        LoggingConstants.SET_COOKIE_HEADER, 
        LoggingConstants.PROXY_AUTHORIZATION_HEADER
    );

    @Override
    public Map<String, List<String>> redactHeaders(Map<String, List<String>> original) {
        Map<String, List<String>> out = new LinkedHashMap<>();
        for (var e : original.entrySet()) {
            String key = e.getKey();
            if (REDACT.contains(key.toLowerCase(Locale.ROOT))) {
                out.put(key, List.of(LoggingConstants.REDACTED_VALUE));
            } else {
                out.put(key, e.getValue());
            }
        }
        return out;
    }

    @Override
    public byte[] redactBody(byte[] original) {
        return original;
    }
}
