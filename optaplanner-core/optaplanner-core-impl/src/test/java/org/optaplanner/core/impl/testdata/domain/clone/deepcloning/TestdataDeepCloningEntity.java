package org.optaplanner.core.impl.testdata.domain.clone.deepcloning;

import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.cloner.DeepPlanningClone;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.DummyVariableListener;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataDeepCloningEntity extends TestdataObject {

    public static EntityDescriptor<TestdataDeepCloningSolution> buildEntityDescriptor() {
        return TestdataDeepCloningSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataDeepCloningEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataDeepCloningSolution> buildVariableDescriptorForValue() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("value");
    }

    private TestdataValue value;
    private List<String> shadowVariableList;
    private Map<String, String> shadowVariableMap;

    public TestdataDeepCloningEntity() {
    }

    public TestdataDeepCloningEntity(String code) {
        super(code);
    }

    public TestdataDeepCloningEntity(String code, TestdataValue value) {
        this(code);
        this.value = value;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public TestdataValue getValue() {
        return value;
    }

    public void setValue(TestdataValue value) {
        this.value = value;
    }

    @DeepPlanningClone
    @CustomShadowVariable(sources = {
            @PlanningVariableReference(variableName = "value") }, variableListenerClass = DummyVariableListener.class)
    public List<String> getShadowVariableList() {
        return shadowVariableList;
    }

    public void setShadowVariableList(List<String> shadowVariableList) {
        this.shadowVariableList = shadowVariableList;
    }

    @DeepPlanningClone
    @CustomShadowVariable(sources = {
            @PlanningVariableReference(variableName = "value") }, variableListenerClass = DummyVariableListener.class)
    public Map<String, String> getShadowVariableMap() {
        return shadowVariableMap;
    }

    public void setShadowVariableMap(Map<String, String> shadowVariableMap) {
        this.shadowVariableMap = shadowVariableMap;
    }

}
