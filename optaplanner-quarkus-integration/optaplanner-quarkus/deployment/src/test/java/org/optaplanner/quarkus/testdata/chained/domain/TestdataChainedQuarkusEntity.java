package org.optaplanner.quarkus.testdata.chained.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

@PlanningEntity
public class TestdataChainedQuarkusEntity implements TestdataChainedQuarkusObject {

    @PlanningVariable(valueRangeProviderRefs = { "chainedAnchorRange",
            "chainedEntityRange" }, graphType = PlanningVariableGraphType.CHAINED)
    private TestdataChainedQuarkusObject previous;

    private TestdataChainedQuarkusEntity next;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public TestdataChainedQuarkusObject getPrevious() {
        return previous;
    }

    public void setPrevious(TestdataChainedQuarkusObject previous) {
        this.previous = previous;
    }

    @Override
    public TestdataChainedQuarkusEntity getNext() {
        return next;
    }

    @Override
    public void setNext(TestdataChainedQuarkusEntity next) {
        this.next = next;
    }

}
