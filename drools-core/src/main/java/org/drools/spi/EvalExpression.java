package org.drools.spi;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;

public interface EvalExpression extends Invokeable {
    public boolean evaluate(Tuple tuple,
                            Declaration[] requiredDeclarations, 
                            WorkingMemory workingMemory);
}
