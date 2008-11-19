package org.drools.runtime.rule;

public interface ConsequenceExceptionHandler {
    void handleException(Activation activation,
                         WorkingMemory workingMemory,
                         Exception exception);
}
