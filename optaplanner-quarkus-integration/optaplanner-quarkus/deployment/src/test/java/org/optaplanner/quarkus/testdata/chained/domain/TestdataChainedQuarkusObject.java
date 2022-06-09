package org.optaplanner.quarkus.testdata.chained.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

@PlanningEntity
public interface TestdataChainedQuarkusObject {

    @InverseRelationShadowVariable(sourceVariableName = "previous")
    TestdataChainedQuarkusEntity getNext();

    void setNext(TestdataChainedQuarkusEntity next);

}
