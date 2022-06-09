package org.optaplanner.quarkus.testdata.interfaceentity.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public interface TestdataInterfaceEntity {

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    Integer getValue();

    void setValue(Integer value);
}
