package org.optaplanner.core.impl.testdata.domain.chained.next;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

@PlanningEntity
public interface TestdataNextAndChainedObject {

    /**
     * @return sometimes null
     */
    @InverseRelationShadowVariable(sourceVariableName = "chainedObject")
    TestdataNextAndChainedEntity getNextEntity();
    void setNextEntity(TestdataNextAndChainedEntity nextEntity);

}
