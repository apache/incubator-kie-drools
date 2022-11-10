package org.optaplanner.core.impl.testdata.domain.chained.multientity;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataChainedGreenEntity extends TestdataObject implements TestdataChainedMultiEntityChainElement {

    private TestdataChainedMultiEntityChainElement previousChainElement;

    public TestdataChainedGreenEntity() {
    }

    public TestdataChainedGreenEntity(String code) {
        super(code);
    }

    @PlanningVariable(
            valueRangeProviderRefs = { "greenRange", "anchorRange" },
            graphType = PlanningVariableGraphType.CHAINED)
    public TestdataChainedMultiEntityChainElement getPreviousChainElement() {
        return previousChainElement;
    }

    public void setPreviousChainElement(TestdataChainedMultiEntityChainElement previousChainElement) {
        this.previousChainElement = previousChainElement;
    }
}
