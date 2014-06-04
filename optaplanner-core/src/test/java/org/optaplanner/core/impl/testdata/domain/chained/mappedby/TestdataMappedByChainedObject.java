package org.optaplanner.core.impl.testdata.domain.chained.mappedby;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

@PlanningEntity
public interface TestdataMappedByChainedObject {

    /**
     * @return sometimes null
     */
    @InverseRelationShadowVariable(sourceVariableName = "chainedObject")
    TestdataMappedByChainedEntity getNextEntity();
    void setNextEntity(TestdataMappedByChainedEntity nextEntity);

}
