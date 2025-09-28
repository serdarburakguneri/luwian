package io.luwian.core.scheduler;

/** A periodic or cron-based task. */
public interface SchedulerTask extends Runnable {
    String name();

    String schedule(); // e.g., cron or fixed-rate descriptor
}
