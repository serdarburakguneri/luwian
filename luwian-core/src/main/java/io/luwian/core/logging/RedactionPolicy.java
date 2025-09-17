package io.luwian.core.logging;

import java.util.List;
import java.util.Map;

/** Decide how to redact headers/body fields before logging. */
public interface RedactionPolicy {
    Map<String, List<String>> redactHeaders(Map<String, List<String>> original);
    byte[] redactBody(byte[] original);
}
