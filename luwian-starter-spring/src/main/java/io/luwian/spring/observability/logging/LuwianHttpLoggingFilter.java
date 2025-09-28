package io.luwian.spring.observability.logging;

import io.luwian.core.http.HttpExchange;
import io.luwian.core.logging.HttpLogger;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

/** Structured HTTP request/response logging. */
public class LuwianHttpLoggingFilter extends OncePerRequestFilter {

    private final HttpLogger httpLogger;
    private final boolean includeBody;

    public LuwianHttpLoggingFilter(HttpLogger httpLogger, boolean includeBody) {
        this.httpLogger = httpLogger;
        this.includeBody = includeBody;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        Instant start = Instant.now();

        CachingRequestWrapper wrappedReq = new CachingRequestWrapper(request, includeBody);
        CachingResponseWrapper wrappedRes = new CachingResponseWrapper(response, includeBody);

        try {
            filterChain.doFilter(wrappedReq, wrappedRes);
        } finally {
            long durationMs =
                    Math.max(0, java.time.Duration.between(start, Instant.now()).toMillis());

            HttpExchange.Request reqEvt = new ServletReqAdapter(wrappedReq, includeBody);
            HttpExchange.Response resEvt = new ServletResAdapter(wrappedRes, includeBody);
            Map<String, Object> ctx = new LinkedHashMap<>();
            httpLogger.log(start, durationMs, reqEvt, resEvt, ctx);

            wrappedRes.copyBodyToResponse();
        }
    }

    // --- Servlet wrappers & adapters ---

    private static class CachingRequestWrapper extends HttpServletRequestWrapper {
        private final byte[] cachedBody;

        CachingRequestWrapper(HttpServletRequest request, boolean cache) throws IOException {
            super(request);
            if (!cache) {
                this.cachedBody = new byte[0];
                return;
            }
            try (InputStream is = request.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                is.transferTo(bos);
                this.cachedBody = bos.toByteArray();
            }
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream bais = new ByteArrayInputStream(cachedBody);
            return new ServletInputStream() {
                @Override
                public int read() {
                    return bais.read();
                }

                @Override
                public boolean isFinished() {
                    return bais.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(jakarta.servlet.ReadListener readListener) {}
            };
        }

        byte[] body() {
            return cachedBody;
        }
    }

    private static class CachingResponseWrapper extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        private ServletOutputStream servletOutputStream;
        private final boolean capture;

        CachingResponseWrapper(HttpServletResponse response, boolean capture) {
            super(response);
            this.capture = capture;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (!capture) return super.getOutputStream();
            if (servletOutputStream == null) {
                servletOutputStream =
                        new ServletOutputStream() {
                            @Override
                            public boolean isReady() {
                                return true;
                            }

                            @Override
                            public void setWriteListener(WriteListener writeListener) {}

                            @Override
                            public void write(int b) throws IOException {
                                bos.write(b);
                            }
                        };
            }
            return servletOutputStream;
        }

        byte[] body() {
            return bos.toByteArray();
        }

        void copyBodyToResponse() throws IOException {
            if (!capture) return;
            byte[] bytes = bos.toByteArray();
            if (bytes.length > 0) {
                super.getOutputStream().write(bytes);
            }
        }
    }

    private static class ServletReqAdapter implements HttpExchange.Request {
        private final HttpServletRequest req;
        private final boolean includeBody;

        ServletReqAdapter(HttpServletRequest req, boolean includeBody) {
            this.req = req;
            this.includeBody = includeBody;
        }

        @Override
        public String method() {
            return req.getMethod();
        }

        @Override
        public String path() {
            return req.getRequestURI();
        }

        @Override
        public Optional<String> query() {
            return Optional.ofNullable(req.getQueryString());
        }

        @Override
        public Map<String, List<String>> headers() {
            return headersToMap(req);
        }

        @Override
        public Optional<byte[]> body() {
            if (!includeBody) return Optional.empty();
            if (req instanceof CachingRequestWrapper crw) return Optional.ofNullable(crw.body());
            return Optional.empty();
        }
    }

    private static class ServletResAdapter implements HttpExchange.Response {
        private final CachingResponseWrapper res;
        private final boolean includeBody;

        ServletResAdapter(CachingResponseWrapper res, boolean includeBody) {
            this.res = res;
            this.includeBody = includeBody;
        }

        @Override
        public int status() {
            return res.getStatus();
        }

        @Override
        public Map<String, List<String>> headers() {
            return res.getHeaderNames().stream()
                    .collect(
                            Collectors.toMap(
                                    h -> h.toLowerCase(), h -> List.copyOf(res.getHeaders(h))));
        }

        @Override
        public Optional<byte[]> body() {
            return includeBody ? Optional.of(res.body()) : Optional.empty();
        }
    }

    private static Map<String, List<String>> headersToMap(HttpServletRequest req) {
        Map<String, List<String>> out = new LinkedHashMap<>();
        for (Enumeration<String> names = req.getHeaderNames(); names.hasMoreElements(); ) {
            String name = names.nextElement();
            out.put(name.toLowerCase(), java.util.Collections.list(req.getHeaders(name)));
        }
        return out;
    }
}
