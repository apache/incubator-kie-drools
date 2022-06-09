package org.optaplanner.spring.boot.autoconfigure.chained.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

@PlanningEntity
public class TestdataChainedSpringEntity implements TestdataChainedSpringObject {

    @PlanningVariable(valueRangeProviderRefs = { "chainedAnchorRange",
            "chainedEntityRange" }, graphType = PlanningVariableGraphType.CHAINED)
    private TestdataChainedSpringObject previous;

    private TestdataChainedSpringEntity next;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public TestdataChainedSpringObject getPrevious() {
        return previous;
    }

    public void setPrevious(TestdataChainedSpringObject previous) {
        this.previous = previous;
    }

    @Override
    public TestdataChainedSpringEntity getNext() {
        return next;
    }

    @Override
    public void setNext(TestdataChainedSpringEntity next) {
        this.next = next;
    }

}
