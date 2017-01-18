package org.kie.dmn.feel.lang.impl;

import java.util.Map;

/**
 * Execution Frame interface represents a
 * stack frame on the FEEL runtime
 */
public interface ExecutionFrame {

    Object getValue(String symbol);

    boolean isDefined(String symbol);

    void setValue(String symbol, Object value);

    Map<String, Object> getAllValues();
}
