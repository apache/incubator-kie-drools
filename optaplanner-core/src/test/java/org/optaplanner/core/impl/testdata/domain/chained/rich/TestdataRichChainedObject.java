package org.optaplanner.core.impl.testdata.domain.chained.rich;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

@PlanningEntity
public interface TestdataRichChainedObject {

    /**
     * @return sometimes null
     */
    @InverseRelationShadowVariable(sourceVariableName = "chainedObject")
    TestdataRichChainedEntity getNextEntity();
    void setNextEntity(TestdataRichChainedEntity nextEntity);

}
