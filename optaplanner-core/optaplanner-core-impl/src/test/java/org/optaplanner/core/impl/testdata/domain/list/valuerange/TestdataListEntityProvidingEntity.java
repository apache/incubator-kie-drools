package org.optaplanner.core.impl.testdata.domain.list.valuerange;

import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;

@PlanningEntity
public class TestdataListEntityProvidingEntity extends TestdataObject {

    @ValueRangeProvider(id = "valueRange")
    private final List<TestdataListValue> valueRange;
    @PlanningListVariable(valueRangeProviderRefs = "valueRange")
    private List<TestdataListValue> valueList;

    public TestdataListEntityProvidingEntity(List<TestdataListValue> valueRange) {
        this.valueRange = valueRange;
    }

    public List<TestdataListValue> getValueRange() {
        return valueRange;
    }

    public List<TestdataListValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<TestdataListValue> valueList) {
        this.valueList = valueList;
    }
}
