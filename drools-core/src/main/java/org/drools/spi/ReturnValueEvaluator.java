package org.drools.spi;

import org.drools.rule.Declaration;

public interface ReturnValueEvaluator {
    public Object evaluate(Tuple tuple,
                           Declaration[] requiredDeclarations);
}
