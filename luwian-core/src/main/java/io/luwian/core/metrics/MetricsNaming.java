package io.luwian.core.metrics;

/** Naming conventions for metrics IDs and tags. */
public final class MetricsNaming {
    private MetricsNaming() {}

    // Timers
    public static final String USECASE_DURATION = "luwian.usecase.duration";

    // Counters
    public static final String USECASE_ERRORS = "luwian.usecase.errors";

    // Common tags
    public static final String TAG_USECASE = "name";
    public static final String TAG_EXCEPTION = "exception";
}
