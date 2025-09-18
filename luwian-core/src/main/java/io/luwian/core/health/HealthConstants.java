package io.luwian.core.health;

/** Constants for health check implementations. */
public final class HealthConstants {

    private HealthConstants() {
        // Utility class
    }

    // Health check component names
    public static final String COMPONENT_DEADLOCK = "deadlock";
    public static final String COMPONENT_HEAP = "heap";
    public static final String COMPONENT_COMPONENTS = "components";

    // Health check detail keys
    public static final String DETAIL_ERROR = "error";
    public static final String DETAIL_STATUS = "status";
    public static final String DETAIL_DURATION_MS = "durationMs";
    public static final String DETAIL_DETAILS = "details";
    public static final String DETAIL_DEADLOCKED_THREADS = "deadlockedThreads";
    public static final String DETAIL_FREE_BYTES = "freeBytes";
    public static final String DETAIL_MAX_BYTES = "maxBytes";
    public static final String DETAIL_TOTAL_BYTES = "totalBytes";
    public static final String DETAIL_FREE_RATIO = "freeRatio";

    // Health status names
    public static final String STATUS_DEGRADED = "DEGRADED";
}
