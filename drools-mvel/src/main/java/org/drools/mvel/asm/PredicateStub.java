package org.drools.mvel.asm;

import org.drools.base.rule.accessor.PredicateExpression;

public interface PredicateStub extends PredicateExpression, InvokerStub {

    void setPredicate(PredicateExpression predicate);
}
