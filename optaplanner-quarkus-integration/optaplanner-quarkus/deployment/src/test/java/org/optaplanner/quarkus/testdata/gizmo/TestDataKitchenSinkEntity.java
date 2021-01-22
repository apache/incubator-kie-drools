package org.optaplanner.quarkus.testdata.gizmo;

import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/*
 *  Should have one of every annotation, even annotations that
 *  don't make sense on an entity, to make sure everything works
 *  a-ok.
 */
@PlanningEntity
public class TestDataKitchenSinkEntity {

    private Integer intVariable;

    @PlanningVariable(valueRangeProviderRefs = { "names" })
    private String stringVariable;

    @PlanningPin
    private boolean isPinned;

    @PlanningVariable(valueRangeProviderRefs = { "ints" })
    private Integer getIntVariable() {
        return intVariable;
    }

    private void setIntVariable(Integer val) {
        intVariable = val;
    }

    public Integer testGetIntVariable() {
        return intVariable;
    }

    public String testGetStringVariable() {
        return stringVariable;
    }

    @ValueRangeProvider(id = "ints")
    private List<Integer> myIntValueRange() {
        return Collections.singletonList(1);
    }

    @ValueRangeProvider(id = "names")
    public List<String> myStringValueRange() {
        return Collections.singletonList("A");
    }

}
