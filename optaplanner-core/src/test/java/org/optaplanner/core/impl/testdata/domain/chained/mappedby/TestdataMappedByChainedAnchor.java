package org.optaplanner.core.impl.testdata.domain.chained.mappedby;

import org.optaplanner.core.impl.testdata.domain.TestdataObject;

public class TestdataMappedByChainedAnchor extends TestdataObject implements TestdataMappedByChainedObject {

    // Shadow variables
    private TestdataMappedByChainedEntity nextEntity;

    public TestdataMappedByChainedAnchor() {
    }

    public TestdataMappedByChainedAnchor(String code) {
        super(code);
    }

    public TestdataMappedByChainedEntity getNextEntity() {
        return nextEntity;
    }

    public void setNextEntity(TestdataMappedByChainedEntity nextEntity) {
        this.nextEntity = nextEntity;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
