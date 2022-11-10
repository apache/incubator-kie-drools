package org.optaplanner.core.impl.testdata.domain.chained.multientity;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataChainedBrownEntity extends TestdataObject implements TestdataChainedMultiEntityChainElement {

    private TestdataChainedMultiEntityChainElement previousChainElement;

    public TestdataChainedBrownEntity() {
    }

    public TestdataChainedBrownEntity(String code) {
        super(code);
    }

    @PlanningVariable(
            valueRangeProviderRefs = { "brownRange", "anchorRange" },
            graphType = PlanningVariableGraphType.CHAINED)
    public TestdataChainedMultiEntityChainElement getPreviousChainElement() {
        return previousChainElement;
    }

    public void setPreviousChainElement(TestdataChainedMultiEntityChainElement previousChainElement) {
        this.previousChainElement = previousChainElement;
    }
}
