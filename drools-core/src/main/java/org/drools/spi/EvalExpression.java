package org.drools.spi;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;

public interface EvalExpression
    extends
    Invoker {
    public boolean evaluate(Tuple tuple,
                            Declaration[] requiredDeclarations,
                            WorkingMemory workingMemory) throws Exception;
}
