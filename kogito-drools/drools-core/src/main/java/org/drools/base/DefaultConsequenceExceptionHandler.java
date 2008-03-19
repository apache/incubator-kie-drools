package org.drools.base;

import org.drools.WorkingMemory;
import org.drools.spi.Activation;
import org.drools.spi.ConsequenceException;
import org.drools.spi.ConsequenceExceptionHandler;

import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

public class DefaultConsequenceExceptionHandler implements ConsequenceExceptionHandler, Externalizable {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void handleException(Activation activation,
                                WorkingMemory workingMemory,
                                Exception exception) {
        throw new ConsequenceException( exception,
                                        activation.getRule() );
    }

}
