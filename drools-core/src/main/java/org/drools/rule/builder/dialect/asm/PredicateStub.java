package org.drools.rule.builder.dialect.asm;

import org.drools.spi.PredicateExpression;

public interface PredicateStub extends PredicateExpression, InvokerStub {
    String[] getLocalDeclarationTypes();

    void setPredicate(PredicateExpression predicate);
}
