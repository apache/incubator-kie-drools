package org.drools.model;

import org.drools.model.functions.Function1;

public interface Binding {
    Variable getBoundVariable();
    Function1 getBindingFunction();
    Variable getInputVariable();
    Variable[] getInputVariables();
    String[] getReactOn();
    String[] getWatchedProps();
    Object eval(Object... args);
}
