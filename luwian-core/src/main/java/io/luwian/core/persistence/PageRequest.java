package io.luwian.core.persistence;

/** Simple page request (0-based). */
public record PageRequest(int page, int size) {
    public PageRequest {
        if (page < 0) throw new IllegalArgumentException("page < 0");
        if (size <= 0) throw new IllegalArgumentException("size <= 0");
    }
}
