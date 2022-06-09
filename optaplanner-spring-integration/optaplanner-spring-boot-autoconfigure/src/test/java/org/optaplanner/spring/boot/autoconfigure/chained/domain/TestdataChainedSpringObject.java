package org.optaplanner.spring.boot.autoconfigure.chained.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

@PlanningEntity
public interface TestdataChainedSpringObject {

    @InverseRelationShadowVariable(sourceVariableName = "previous")
    TestdataChainedSpringEntity getNext();

    void setNext(TestdataChainedSpringEntity next);

}
