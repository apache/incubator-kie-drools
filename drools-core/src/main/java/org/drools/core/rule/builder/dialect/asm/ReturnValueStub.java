package org.drools.core.rule.builder.dialect.asm;

import org.drools.core.spi.ReturnValueExpression;

public interface ReturnValueStub extends ReturnValueExpression, InvokerStub {

    void setReturnValue(ReturnValueExpression returnValue);
}
