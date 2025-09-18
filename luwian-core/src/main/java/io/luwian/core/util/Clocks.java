package io.luwian.core.util;

import java.time.Clock;
import java.time.ZoneOffset;

/** UTC clock helper. */
public final class Clocks {
    private Clocks() {}

    public static Clock utc() {
        return Clock.system(ZoneOffset.UTC);
    }
}
