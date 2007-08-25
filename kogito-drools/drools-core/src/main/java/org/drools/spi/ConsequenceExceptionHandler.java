package org.drools.spi;

import java.io.Serializable;

import org.drools.WorkingMemory;

/**
 * Care should be taken when implementing this class. Swallowing of consequence can be dangerous
 * if the exception occured during a WorkingMemory action, thus leaving the integrity of the 
 * WorkingMemory invalid.
 *
 */
public interface ConsequenceExceptionHandler extends Serializable {
    void handleException(Activation activation, WorkingMemory workingMemory, Exception exception);
}   
