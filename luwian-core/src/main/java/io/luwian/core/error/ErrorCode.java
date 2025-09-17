package io.luwian.core.error;

/** Catalog row: binds a stable code to HTTP status/title/type path. */
public record ErrorCode(String code, int httpStatus, String title, String typePath) {
    public String typeUri(String base) { return base.endsWith("/") ? base + typePath : base + "/" + typePath; }
}
