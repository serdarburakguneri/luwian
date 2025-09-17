# Luwian — Config Namespace & Stack Mapping (v1)

All user-facing configuration keys live under the `luwian.*` namespace. Starters translate these into Spring/Quarkus specifics.

## Keys & Defaults

| Luwian key                        | Type    | Default     | Description |
|---|---|---|---|
| `luwian.logging.json`             | boolean | `true`      | Emit JSON logs. |
| `luwian.logging.http.body`        | boolean | `false`     | Include HTTP body in logs (PII risk). |
| `luwian.metrics.enabled`          | boolean | `true`      | Enable Micrometer / Prometheus endpoint. |
| `luwian.tracing.enabled`          | boolean | `true`      | Enable OpenTelemetry tracing. |
| `luwian.tenancy.header`           | string  | `X-Tenant`  | Header used to resolve tenant. |
| `luwian.error.include-stacktrace` | enum    | `on-trace`  | `never` or `on-trace`. |

## Per-Stack Mapping

| Luwian key                        | Spring Boot mapping                                  | Quarkus mapping                                  |
|---|---|---|
| `luwian.logging.json`             | Enable JSON encoder (Logback config via starter)     | `quarkus.log.console.json=true`                  |
| `luwian.logging.http.body`        | Flag read by Luwian HTTP logging filter              | Flag read by Luwian HTTP logging filter          |
| `luwian.metrics.enabled`          | Actuator + Micrometer enabled                        | `quarkus.micrometer.enabled=true`                |
| `luwian.tracing.enabled`          | OTel auto-config (env: `OTEL_EXPORTER_OTLP_*`)       | OTel auto-config (env)                           |
| `luwian.tenancy.header`           | `LuwianTenantFilter.header`                          | `LuwianTenantFilter.header`                      |
| `luwian.error.include-stacktrace` | Controls problem handler detail inclusion            | Controls problem mapper detail inclusion         |

## Global Metric Tags
Injected by starters:
- `service`: artifactId or `LUWIAN_SERVICE` env.
- `env`: from `ENV` or `SPRING_PROFILES_ACTIVE` / `quarkus.profile`.
- `version`: from build info (git commit / app version if available).

## HTTP Metrics
We rely on framework defaults and add use-case metrics via annotation:
- HTTP server metrics: Actuator/Micrometer (Spring) / Quarkus Micrometer binder.
- Use-case annotation (planned): `@Observed("usecase.name")` →
  - Timer: `luwian.usecase.duration{name="<usecase>"}`
  - Counter: `luwian.usecase.errors{name="<usecase>", exception="<FQCN>"}`

## Tracing
- W3C Trace Context (`traceparent`, `tracestate`).
- Export controlled by environment variables (OTLP by default).

## Security
- `luwian.security.*` namespace reserved for future OIDC/JWT starter.
