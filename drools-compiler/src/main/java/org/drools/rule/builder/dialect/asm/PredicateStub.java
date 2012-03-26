package org.drools.rule.builder.dialect.asm;

import org.drools.spi.*;

public interface PredicateStub extends PredicateExpression, InvokerStub {

    void setPredicate(PredicateExpression predicate);
}
