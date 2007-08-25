package org.drools.base;

import org.drools.WorkingMemory;
import org.drools.spi.Activation;
import org.drools.spi.ConsequenceException;
import org.drools.spi.ConsequenceExceptionHandler;

public class DefaultConsequenceExceptionHandler implements ConsequenceExceptionHandler {

    public void handleException(Activation activation,
                                WorkingMemory workingMemory,
                                Exception exception) {
        exception.printStackTrace();
        throw new ConsequenceException( exception,
                                        activation.getRule() );
    }

}
