package io.luwian.core.util;

import java.util.UUID;

/** UUID generator. */
public final class IdGenerator {
    private IdGenerator() {}

    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
