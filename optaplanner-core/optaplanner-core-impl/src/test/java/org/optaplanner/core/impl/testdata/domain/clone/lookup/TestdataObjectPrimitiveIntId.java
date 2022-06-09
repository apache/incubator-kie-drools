package org.optaplanner.core.impl.testdata.domain.clone.lookup;

import org.optaplanner.core.api.domain.lookup.PlanningId;

public class TestdataObjectPrimitiveIntId {

    @PlanningId
    private final int id;

    public TestdataObjectPrimitiveIntId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

}
