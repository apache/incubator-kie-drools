package org.optaplanner.core.impl.testdata.domain.chained.rich;

import org.optaplanner.core.impl.testdata.domain.TestdataObject;

public class TestdataRichChainedAnchor extends TestdataObject implements TestdataRichChainedObject {

    // Shadow variables
    private TestdataRichChainedEntity nextEntity;

    public TestdataRichChainedAnchor() {
    }

    public TestdataRichChainedAnchor(String code) {
        super(code);
    }

    public TestdataRichChainedEntity getNextEntity() {
        return nextEntity;
    }

    public void setNextEntity(TestdataRichChainedEntity nextEntity) {
        this.nextEntity = nextEntity;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
