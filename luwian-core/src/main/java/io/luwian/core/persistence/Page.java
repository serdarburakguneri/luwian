package io.luwian.core.persistence;

import java.util.List;

/** Simple page result. */
public record Page<T>(List<T> content, long totalElements, int totalPages, int page, int size) {}
