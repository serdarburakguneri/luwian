package io.luwian.spring.corebridge;

import io.luwian.core.error.Problem;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Simple in-memory Problem & Builder implementation for Spring adapter use. */
public class ProblemBuilderImpl implements Problem.Builder {

    private URI type;
    private String title;
    private int status;
    private String detail;
    private URI instance;
    private final Map<String, Object> ext = new LinkedHashMap<>();

    @Override public Problem.Builder type(URI type) { this.type = type; return this; }
    @Override public Problem.Builder title(String title) { this.title = title; return this; }
    @Override public Problem.Builder status(int httpStatus) { this.status = httpStatus; return this; }
    @Override public Problem.Builder detail(String detail) { this.detail = detail; return this; }
    @Override public Problem.Builder instance(URI instance) { this.instance = instance; return this; }
    @Override public Problem.Builder put(String key, Object value) { this.ext.put(key, value); return this; }

    @Override public Problem build() {
        final URI t = this.type, inst = this.instance;
        final String ttl = this.title, det = this.detail;
        final int st = this.status;
        final Map<String, Object> exts = Collections.unmodifiableMap(new LinkedHashMap<>(ext));
        return new Problem() {
            @Override public URI type() { return t; }
            @Override public String title() { return ttl; }
            @Override public int status() { return st; }
            @Override public String detail() { return det; }
            @Override public URI instance() { return inst; }
            @Override public Map<String, Object> extensions() { return exts; }
        };
    }
}
