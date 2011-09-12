package org.drools.rule.builder.dialect.asm;

import org.drools.spi.*;

public interface ConsequenceStub extends Consequence, ConsequenceDataProvider {
    String getConsequenceClassName();
    String[] getPackageImports();

    void setConsequence(Consequence consequence);
}
