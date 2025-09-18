package io.luwian.spring.corebridge;

import io.luwian.core.http.HttpExchange;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;

public final class ServletHttpExchangeAdapters {

    private ServletHttpExchangeAdapters() {}

    public static HttpExchange.Request request(HttpServletRequest r, boolean withBody, byte[] body) {
        final Map<String, List<String>> headers = new LinkedHashMap<>();
        for (var e = r.getHeaderNames(); e.hasMoreElements(); ) {
            String name = e.nextElement();
            headers.put(name, Collections.list(r.getHeaders(name)));
        }
        final String q = r.getQueryString();
        final Optional<byte[]> bodyOpt = withBody && body != null ? Optional.of(body) : Optional.empty();
        return new HttpExchange.Request() {
            @Override public String method() { return r.getMethod(); }
            @Override public String path() { return r.getRequestURI(); }
            @Override public Optional<String> query() { return Optional.ofNullable(q); }
            @Override public Map<String, List<String>> headers() { return headers; }
            @Override public Optional<byte[]> body() { return bodyOpt; }
        };
    }

    public static HttpExchange.Response response(HttpServletResponse r, boolean withBody, byte[] body) {
        final Map<String, List<String>> headers = new LinkedHashMap<>();
        for (String name : r.getHeaderNames()) {
            headers.put(name, new ArrayList<>(r.getHeaders(name)));
        }
        final Optional<byte[]> bodyOpt = withBody && body != null ? Optional.of(body) : Optional.empty();
        return new HttpExchange.Response() {
            @Override public int status() { return r.getStatus(); }
            @Override public Map<String, List<String>> headers() { return headers; }
            @Override public Optional<byte[]> body() { return bodyOpt; }
        };
    }
}
