package org.drools.spi;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;

public interface PredicateExpression
    extends
    Invoker {
    public boolean evaluate(Tuple tuple,
                            FactHandle factHandle,
                            Declaration declaration,
                            Declaration[] requiredDeclarations,
                            WorkingMemory workingMemory) throws Exception;
}
