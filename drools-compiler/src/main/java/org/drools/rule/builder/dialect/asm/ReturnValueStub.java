package org.drools.rule.builder.dialect.asm;

import org.drools.spi.*;

public interface ReturnValueStub extends ReturnValueExpression, InvokerStub {

    void setReturnValue(ReturnValueExpression returnValue);
}
