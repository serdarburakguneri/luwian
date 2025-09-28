package io.luwian.examples.spring;

import io.luwian.core.metrics.MetricsNaming;
import io.luwian.core.metrics.MetricsRecorder;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UseCaseService {

    private final MetricsRecorder metrics;

    public UseCaseService(MetricsRecorder metrics) {
        this.metrics = metrics;
    }

    public String doWork() {
        try (var t =
                metrics.startTimer(
                        MetricsNaming.USECASE_DURATION,
                        Map.of(MetricsNaming.TAG_USECASE, "demo"))) {
            return "ok";
        } catch (RuntimeException e) {
            metrics.increment(
                    MetricsNaming.USECASE_ERRORS,
                    Map.of(
                            MetricsNaming.TAG_USECASE,
                            "demo",
                            MetricsNaming.TAG_EXCEPTION,
                            e.getClass().getName()));
            throw e;
        }
    }
}
