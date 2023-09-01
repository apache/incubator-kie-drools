package org.drools.mvel.asm;

import org.drools.base.rule.consequence.Consequence;

public interface ConsequenceStub extends Consequence, InvokerStub {
    Boolean[] getNotPatterns();

    void setConsequence(Consequence consequence);
}
