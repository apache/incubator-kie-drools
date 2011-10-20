package org.drools.rule.builder.dialect.asm;

import org.drools.spi.EvalExpression;

public interface EvalStub extends EvalExpression, InvokerStub {
    void setEval(EvalExpression predicate);
}
