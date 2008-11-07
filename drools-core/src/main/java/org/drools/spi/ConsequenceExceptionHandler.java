package org.drools.spi;

import org.drools.spi.Activation;
import org.drools.WorkingMemory;


/**
 * Care should be taken when implementing this class. Swallowing of consequence can be dangerous
 * if the exception occured during a WorkingMemory action, thus leaving the integrity of the
 * WorkingMemory invalid.
 *
 */
public interface ConsequenceExceptionHandler {
    void handleException(Activation activation, WorkingMemory workingMemory, Exception exception);
}
