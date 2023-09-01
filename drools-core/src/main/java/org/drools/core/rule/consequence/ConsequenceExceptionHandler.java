package org.drools.core.rule.consequence;

import org.drools.core.WorkingMemory;


/**
 * Care should be taken when implementing this class. Swallowing of consequence can be dangerous
 * if the exception occured during a WorkingMemory action, thus leaving the integrity of the
 * WorkingMemory invalid.
 */
public interface ConsequenceExceptionHandler {
    void handleException(InternalMatch internalMatch, WorkingMemory workingMemory, Exception exception);
}
