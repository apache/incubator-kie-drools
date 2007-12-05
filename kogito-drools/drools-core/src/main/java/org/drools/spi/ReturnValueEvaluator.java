package org.drools.spi;

import org.drools.WorkingMemory;

public interface ReturnValueEvaluator {
    Object evaluate(WorkingMemory workingMemory) throws Exception;
}
