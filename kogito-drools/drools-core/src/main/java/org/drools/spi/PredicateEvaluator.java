package org.drools.spi;

import org.drools.FactHandle;
import org.drools.rule.Declaration;

public interface PredicateEvaluator {
    public boolean evaluate(Tuple tuple,
                            Object object,
                            FactHandle handle,
                            Declaration declaration,
                            Declaration[] requiredDeclarations);
}
