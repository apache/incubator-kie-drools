package org.drools.runtime.rule;

import java.io.Externalizable;

public interface ConsequenceExceptionHandler extends Externalizable {
    void handleException(Activation activation, WorkingMemory workingMemory, Exception exception);
}
