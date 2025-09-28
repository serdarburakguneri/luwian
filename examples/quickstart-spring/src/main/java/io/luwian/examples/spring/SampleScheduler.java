package io.luwian.examples.spring;

import io.luwian.core.scheduler.SchedulerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SampleScheduler implements SchedulerTask {

    private static final Logger log = LoggerFactory.getLogger(SampleScheduler.class);

    @Override
    public String name() {
        return "sample.scheduler";
    }

    @Override
    public String schedule() {
        return "fixedRate:5000";
    }

    @Override
    public void run() {
        log.info("Scheduled task executed: {}", name());
    }

    @Scheduled(fixedRate = 5000)
    public void tick() {
        run();
    }
}
