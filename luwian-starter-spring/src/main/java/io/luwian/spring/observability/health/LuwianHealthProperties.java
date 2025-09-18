package io.luwian.spring.observability.health;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Toggle and tune built-in health checks exposed to Actuator. */
@ConfigurationProperties(prefix = "luwian.health")
public class LuwianHealthProperties {

    /** Enable Luwian health contributors. */
    private boolean enabled = true;

    /** Built-in: deadlock detection (liveness). */
    private boolean deadlock = true;

    /** Built-in: heap pressure (readiness). */
    private boolean heap = true;

    /** Minimum free heap bytes before marking degraded/down. Default: 64 MiB. */
    private long heapMinFreeBytes = 64L * 1024 * 1024;

    /** Minimum free ratio before degraded/down. Default: 5%. */
    private double heapMinFreeRatio = 0.05;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDeadlock() {
        return deadlock;
    }

    public void setDeadlock(boolean deadlock) {
        this.deadlock = deadlock;
    }

    public boolean isHeap() {
        return heap;
    }

    public void setHeap(boolean heap) {
        this.heap = heap;
    }

    public long getHeapMinFreeBytes() {
        return heapMinFreeBytes;
    }

    public void setHeapMinFreeBytes(long heapMinFreeBytes) {
        this.heapMinFreeBytes = heapMinFreeBytes;
    }

    public double getHeapMinFreeRatio() {
        return heapMinFreeRatio;
    }

    public void setHeapMinFreeRatio(double heapMinFreeRatio) {
        this.heapMinFreeRatio = heapMinFreeRatio;
    }
}
