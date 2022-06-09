package org.optaplanner.core.impl.testdata.domain.clone.lookup;

import org.optaplanner.core.api.domain.lookup.PlanningId;

public class TestdataObjectIntegerId {

    @PlanningId
    private final Integer id;

    public TestdataObjectIntegerId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

}
