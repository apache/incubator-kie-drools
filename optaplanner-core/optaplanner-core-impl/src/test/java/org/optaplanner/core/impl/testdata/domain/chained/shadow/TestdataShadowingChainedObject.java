package org.optaplanner.core.impl.testdata.domain.chained.shadow;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

@PlanningEntity
public interface TestdataShadowingChainedObject {

    /**
     * @return sometimes null
     */
    @InverseRelationShadowVariable(sourceVariableName = "chainedObject")
    TestdataShadowingChainedEntity getNextEntity();

    void setNextEntity(TestdataShadowingChainedEntity nextEntity);

}
