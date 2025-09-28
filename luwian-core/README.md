# luwian-core

Cross-cutting abstractions and conventions shared by Spring and Quarkus starters. Framework-agnostic.

## Packages

- `io.luwian.core.error`: Problem model and exception→code mapping via `ErrorCatalog`.
- `io.luwian.core.logging`: Structured logging (HTTP logging, redaction policy).
- `io.luwian.core.obs`: Correlation and tenancy context with MDC bridge.
- `io.luwian.core.http`: Minimal HTTP exchange model for logging.
- `io.luwian.core.health`: Health checks and reports (deadlock, heap builtins).
- `io.luwian.core.config`: Core config model.

## Usage (examples)

Create a correlation context and set correlation id:

```java
CorrelationContext ctx = new ThreadLocalCorrelationContext();
ctx.setCorrelationId("123e4567-...");
```

Log an HTTP exchange (starter provides a servlet adapter):

```java
HttpLogger logger = new Slf4jHttpLogger(new DefaultRedactionPolicy());
logger.log(Instant.now(), 42L, req, res, Map.of("correlationId", ctx.getCorrelationId().orElse("")));
```

Map an exception to a canonical error code:

```java
ErrorCatalog catalog = new DefaultErrorCatalog();
ErrorCode code = catalog.resolve(new IllegalArgumentException()).orElseThrow();
```


