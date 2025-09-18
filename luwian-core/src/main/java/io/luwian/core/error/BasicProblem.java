package io.luwian.core.error;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Minimal immutable RFC7807 Problem implementation with builder. */
public final class BasicProblem implements Problem {

    private final URI type;
    private final String title;
    private final int status;
    private final String detail;
    private final URI instance;
    private final Map<String, Object> extensions;

    private BasicProblem(
            URI type,
            String title,
            int status,
            String detail,
            URI instance,
            Map<String, Object> ext) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.extensions = Collections.unmodifiableMap(new LinkedHashMap<>(ext));
    }

    @Override
    public URI type() {
        return type;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public int status() {
        return status;
    }

    @Override
    public String detail() {
        return detail;
    }

    @Override
    public URI instance() {
        return instance;
    }

    @Override
    public Map<String, Object> extensions() {
        return extensions;
    }

    /** Builder implementation for BasicProblem. */
    public static final class Builder implements Problem.Builder {
        private URI type;
        private String title;
        private int status;
        private String detail;
        private URI instance;
        private final Map<String, Object> ext = new LinkedHashMap<>();

        @Override
        public Builder type(URI type) {
            this.type = type;
            return this;
        }

        @Override
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        @Override
        public Builder status(int httpStatus) {
            this.status = httpStatus;
            return this;
        }

        @Override
        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        @Override
        public Builder instance(URI instance) {
            this.instance = instance;
            return this;
        }

        @Override
        public Builder put(String key, Object value) {
            this.ext.put(key, value);
            return this;
        }

        @Override
        public Problem build() {
            return new BasicProblem(type, title, status, detail, instance, ext);
        }
    }
}
