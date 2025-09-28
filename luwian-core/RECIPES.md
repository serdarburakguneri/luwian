# Core Recipes

Minimal examples to wire core primitives in starters or apps.

- Correlation propagation across threads: `ContextPropagators.wrap(...)`
- Use-case metric timing:

```java
try (var t = metrics.startTimer(MetricsNaming.USECASE_DURATION, Map.of("name","orders.create"))) {
  service.createOrder(cmd);
} catch (Exception e) {
  metrics.increment(MetricsNaming.USECASE_ERRORS, Map.of("name","orders.create","exception", e.getClass().getName()));
  throw e;
}
```

- Outbox enqueue within UnitOfWork:

```java
uow.commit(); // after saving domain changes and outbox record
```
