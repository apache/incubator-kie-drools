package org.drools.mvel.asm;

import org.drools.base.rule.accessor.ReturnValueExpression;

public interface ReturnValueStub extends ReturnValueExpression, InvokerStub {

    void setReturnValue(ReturnValueExpression returnValue);
}
