package org.drools.mvel.asm;

import org.drools.base.rule.accessor.EvalExpression;

public interface EvalStub extends EvalExpression, InvokerStub {
    void setEval(EvalExpression predicate);
}
