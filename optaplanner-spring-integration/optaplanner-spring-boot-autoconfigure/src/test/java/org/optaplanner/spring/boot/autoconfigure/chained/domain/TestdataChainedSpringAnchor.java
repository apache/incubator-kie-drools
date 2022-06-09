package org.optaplanner.spring.boot.autoconfigure.chained.domain;

public class TestdataChainedSpringAnchor implements TestdataChainedSpringObject {

    private TestdataChainedSpringEntity next;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public TestdataChainedSpringEntity getNext() {
        return next;
    }

    @Override
    public void setNext(TestdataChainedSpringEntity next) {
        this.next = next;
    }

}
