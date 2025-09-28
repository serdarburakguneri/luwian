package io.luwian.core.error;

import java.util.List;
import java.util.Optional;

/** Chains multiple catalogs and returns the first non-empty mapping. */
public final class CompositeErrorCatalog implements ErrorCatalog {

    private final List<ErrorCatalog> catalogs;

    public CompositeErrorCatalog(List<ErrorCatalog> catalogs) {
        this.catalogs = List.copyOf(catalogs);
    }

    @Override
    public Optional<ErrorCode> resolve(Throwable error) {
        for (ErrorCatalog c : catalogs) {
            Optional<ErrorCode> r = c.resolve(error);
            if (r.isPresent()) return r;
        }
        return Optional.empty();
    }
}
