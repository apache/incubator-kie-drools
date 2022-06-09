package org.optaplanner.quarkus.testdata.gizmo;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class PrivateNoArgsConstructorEntity {
    @PlanningId
    final String id;

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    String value;

    private PrivateNoArgsConstructorEntity() {
        id = null;
    }

    public PrivateNoArgsConstructorEntity(String id) {
        this.id = id;
    }
}
