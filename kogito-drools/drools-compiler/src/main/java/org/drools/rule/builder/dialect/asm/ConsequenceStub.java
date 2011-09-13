package org.drools.rule.builder.dialect.asm;

import org.drools.spi.*;

public interface ConsequenceStub extends Consequence, InvokerStub {
    Boolean[] getNotPatterns();

    void setConsequence(Consequence consequence);
}
