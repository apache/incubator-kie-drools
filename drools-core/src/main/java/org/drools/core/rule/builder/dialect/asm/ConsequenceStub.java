package org.drools.core.rule.builder.dialect.asm;

import org.drools.core.spi.Consequence;

public interface ConsequenceStub extends Consequence, InvokerStub {
    Boolean[] getNotPatterns();

    void setConsequence(Consequence consequence);
}
