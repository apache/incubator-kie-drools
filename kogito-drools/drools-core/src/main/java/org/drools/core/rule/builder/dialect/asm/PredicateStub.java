package org.drools.core.rule.builder.dialect.asm;

import org.drools.core.spi.PredicateExpression;

public interface PredicateStub extends PredicateExpression, InvokerStub {

    void setPredicate(PredicateExpression predicate);
}
