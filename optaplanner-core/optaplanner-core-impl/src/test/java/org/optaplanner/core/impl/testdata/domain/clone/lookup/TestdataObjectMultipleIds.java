package org.optaplanner.core.impl.testdata.domain.clone.lookup;

import org.optaplanner.core.api.domain.lookup.PlanningId;

public class TestdataObjectMultipleIds {

    @PlanningId
    private final Integer id;
    @PlanningId
    private final String name;
    @PlanningId
    private final Boolean bool;

    public TestdataObjectMultipleIds() {
        this.id = 0;
        this.name = "";
        this.bool = false;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getBool() {
        return bool;
    }

}
