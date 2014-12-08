package org.optaplanner.core.impl.testdata.domain.chained.next;

import org.optaplanner.core.impl.testdata.domain.TestdataObject;

public class TestdataNextAndChainedAnchor extends TestdataObject implements TestdataNextAndChainedObject {

    // Shadow variables
    private TestdataNextAndChainedEntity nextEntity;

    public TestdataNextAndChainedAnchor() {
    }

    public TestdataNextAndChainedAnchor(String code) {
        super(code);
    }

    public TestdataNextAndChainedEntity getNextEntity() {
        return nextEntity;
    }

    public void setNextEntity(TestdataNextAndChainedEntity nextEntity) {
        this.nextEntity = nextEntity;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
