package io.luwian.core.obs;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/** Helpers to propagate RequestContext across async boundaries. */
public final class ContextPropagators {

    private ContextPropagators() {}

    public static Runnable wrap(Runnable delegate, CorrelationContext ctx) {
        Objects.requireNonNull(delegate, "delegate");
        Objects.requireNonNull(ctx, "ctx");
        RequestContext snapshot = RequestContext.from(ctx);
        return () -> {
            snapshot.applyTo(ctx);
            try {
                delegate.run();
            } finally {
                ctx.clear();
            }
        };
    }

    public static <V> Callable<V> wrap(Callable<V> delegate, CorrelationContext ctx) {
        Objects.requireNonNull(delegate, "delegate");
        Objects.requireNonNull(ctx, "ctx");
        RequestContext snapshot = RequestContext.from(ctx);
        return () -> {
            snapshot.applyTo(ctx);
            try {
                return delegate.call();
            } finally {
                ctx.clear();
            }
        };
    }

    public static Executor wrappingExecutor(Executor delegate, CorrelationContext ctx) {
        Objects.requireNonNull(delegate, "delegate");
        Objects.requireNonNull(ctx, "ctx");
        return command -> delegate.execute(wrap(command, ctx));
    }
}
