package org.drools.spi;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;

public interface ReturnValueExpression extends Invokeable {
    public Object evaluate(Tuple tuple,
                           Declaration[] requiredDeclarations,
                           WorkingMemory workingMemory);
}
