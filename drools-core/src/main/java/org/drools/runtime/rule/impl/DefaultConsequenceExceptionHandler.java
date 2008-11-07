package org.drools.runtime.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.ConsequenceExceptionHandler;
import org.drools.runtime.rule.WorkingMemory;

public class DefaultConsequenceExceptionHandler implements ConsequenceExceptionHandler, Externalizable {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void handleException(Activation activation,
                                WorkingMemory workingMemory,
                                Exception exception) {
        throw new org.drools.runtime.rule.ConsequenceException( exception, activation.getRule() );
    }

}
