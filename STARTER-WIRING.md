# Luwian — Starter Wiring Plan (Spring-only, v1)

This document defines how **Luwian conventions** are wired into a Spring Boot app via `luwian-starter-spring`. It is a **design contract** (no implementation yet). Quarkus wiring will be added later.

## Scope
- Spring Boot 3.3+
- Java 21
- Dependencies: `spring-boot-starter-web`, `spring-boot-starter-actuator`, `micrometer-core` (and the chosen registry), optional OpenTelemetry

---

## Packages & Classes

```
io.luwian.spring.autoconfigure
  └─ LuwianAutoConfiguration                 # @AutoConfiguration entry point (already present)

io.luwian.spring.observability.logging
  ├─ LuwianJsonLoggingConfig                 # Configures Logback JSON encoder (if enabled)
  ├─ LuwianCorrelationFilter                 # Extracts/creates correlationId → MDC
  └─ LuwianHttpLoggingFilter                 # Request/response logging (body optional)

io.luwian.spring.observability.errors
  └─ LuwianProblemHandler                    # @ControllerAdvice → Problem+JSON mapper

io.luwian.spring.observability.metrics
  └─ LuwianMetricsConfig                     # MeterRegistryCustomizer with global tags

io.luwian.spring.observability.tracing
  └─ LuwianTracingConfig                     # Enables OTel auto-config if enabled

io.luwian.spring.annotations                 # (future) @Observed, @ErrorHandled, @Idempotent
```

---

## Configuration Keys

All Luwian keys live under the `luwian.*` namespace and are defined in **CONFIG.md**. Starters **read these** and translate into Spring behaviors.

| Key                                  | Default    | Effect (Spring) |
|--------------------------------------|------------|------------------|
| `luwian.logging.json`                | `true`     | Switch Logback to JSON encoder. |
| `luwian.logging.http.body`           | `false`    | Include request/response body in HTTP logs (PII risk). |
| `luwian.metrics.enabled`             | `true`     | Enable Micrometer and expose Prometheus via Actuator. |
| `luwian.tracing.enabled`             | `true`     | Respect OpenTelemetry auto-config if libs present. |
| `luwian.tenancy.header`              | `X-Tenant` | Header name resolved by `LuwianCorrelationFilter` (also sets MDC key). |
| `luwian.error.include-stacktrace`    | `on-trace` | Include stacktrace details **only** when a trace/correlation is active. |

**Global tags** injected into metrics: `service`, `env`, `version`.

---

## Components

### 1) `LuwianJsonLoggingConfig`
- Responsibility: configure JSON logging output when `luwian.logging.json=true`.
- Approach: configure Logback (via a small `logback-spring.xml` fragment or programmatic config).
- JSON fields (minimum): `ts`, `level`, `logger`, `message`, `traceId`, `spanId`, `correlationId`, `tenantId`, `userId`, `fields`.

### 2) `LuwianCorrelationFilter`
- Type: `OncePerRequestFilter`.
- Order: high precedence (runs before other logging/metrics filters).
- Reads headers: `traceparent` (W3C), `x-correlation-id`, and `luwian.tenancy.header` (default `X-Tenant`).
- Strategy:
  - If `traceparent` exists → derive `traceId` (if accessible) and set `correlationId` if absent.
  - Else: generate a new `correlationId` (UUID).
  - Put values in MDC: `correlationId`, `tenantId`, `userId` (if available), and pass-through `traceId`/`spanId` if present.
- Response header: always write `x-correlation-id`.

### 3) `LuwianHttpLoggingFilter`
- Type: `OncePerRequestFilter`.
- Behavior: logs request line, method, path, status, duration; includes body only if `luwian.logging.http.body=true`.
- Privacy: redact common sensitive headers (`authorization`, `cookie`, etc.).
- Fields: `method`, `path`, `query`, `status`, `duration_ms`, plus MDC fields.

### 4) `LuwianProblemHandler`
- Type: `@ControllerAdvice` with `@ExceptionHandler` methods.
- Maps exception classes to Problem+JSON payloads defined in **ERRORS.md**.
- Always sets `Content-Type: application/problem+json`.
- Includes `correlationId` in body and header.
- Stacktrace:
  - `luwian.error.include-stacktrace=never` → don't include details.
  - `on-trace` (default) → include concise `detail` only when a correlation/trace is present.

### 5) `LuwianMetricsConfig`
- Provides a `MeterRegistryCustomizer<?>` that injects `service`, `env`, `version` tags globally.
- Ensures HTTP metrics are exposed via Actuator (`/actuator/prometheus`); no custom binder needed for MVP.

### 6) `LuwianTracingConfig`
- If `luwian.tracing.enabled=true` and OTel auto-config classes are on classpath, make no-op bean just to confirm enablement and document requirements.
- Respect environment variables (`OTEL_EXPORTER_OTLP_*`, `OTEL_RESOURCE_ATTRIBUTES`).

---

## AutoConfiguration Contract

`io.luwian.spring.autoconfigure.LuwianAutoConfiguration`
- Conditions:
  - Active for web apps only (`@ConditionalOnWebApplication`).
  - Beans registered conditionally on properties:
    - `@ConditionalOnProperty(value="luwian.logging.json", havingValue="true", matchIfMissing=true)` → `LuwianJsonLoggingConfig`
    - `@ConditionalOnProperty("luwian.metrics.enabled")` → `LuwianMetricsConfig`
    - `@ConditionalOnProperty("luwian.tracing.enabled")` → `LuwianTracingConfig`
- Filter order:
  1. `LuwianCorrelationFilter`
  2. `LuwianHttpLoggingFilter`
- Expose Problem handler unconditionally (fallback safety).

---

## Endpoints & Exposure (Spring defaults)

- Health: `/actuator/health` (include liveness/readiness probes).
- Info: `/actuator/info` (populate `version` from build info if available).
- Metrics: `/actuator/prometheus` (Prometheus scrape).
- No business endpoints in the starter.


