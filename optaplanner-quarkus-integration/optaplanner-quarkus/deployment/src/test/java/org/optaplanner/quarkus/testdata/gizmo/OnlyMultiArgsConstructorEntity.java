package org.optaplanner.quarkus.testdata.gizmo;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class OnlyMultiArgsConstructorEntity extends PrivateNoArgsConstructorEntity {
    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    String anotherValue;

    public OnlyMultiArgsConstructorEntity(String id) {
        super(id);
    }
}
