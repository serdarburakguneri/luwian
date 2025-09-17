package io.luwian.spring.observability.errors;

/** Policy for including stacktrace details in Problem+JSON. */
public enum IncludeStacktracePolicy {
    NEVER,
    ON_TRACE; // include details only when a correlation/trace is present
}
