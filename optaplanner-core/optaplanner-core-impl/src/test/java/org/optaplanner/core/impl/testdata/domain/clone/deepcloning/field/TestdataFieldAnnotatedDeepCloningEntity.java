package org.optaplanner.core.impl.testdata.domain.clone.deepcloning.field;

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
public class TestdataFieldAnnotatedDeepCloningEntity extends TestdataObject {

    public static EntityDescriptor<TestdataFieldAnnotatedDeepCloningSolution> buildEntityDescriptor() {
        return TestdataFieldAnnotatedDeepCloningSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataFieldAnnotatedDeepCloningEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataFieldAnnotatedDeepCloningSolution> buildVariableDescriptorForValue() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("value");
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    private TestdataValue value;
    @DeepPlanningClone
    @CustomShadowVariable(sources = {
            @PlanningVariableReference(variableName = "value") }, variableListenerClass = DummyVariableListener.class)
    private List<String> shadowVariableList;
    @DeepPlanningClone
    @CustomShadowVariable(sources = {
            @PlanningVariableReference(variableName = "value") }, variableListenerClass = DummyVariableListener.class)
    private Map<String, String> shadowVariableMap;

    public TestdataFieldAnnotatedDeepCloningEntity() {
    }

    public TestdataFieldAnnotatedDeepCloningEntity(String code) {
        super(code);
    }

    public TestdataFieldAnnotatedDeepCloningEntity(String code, TestdataValue value) {
        this(code);
        this.value = value;
    }

    public TestdataValue getValue() {
        return value;
    }

    public void setValue(TestdataValue value) {
        this.value = value;
    }

    public List<String> getShadowVariableList() {
        return shadowVariableList;
    }

    public void setShadowVariableList(List<String> shadowVariableList) {
        this.shadowVariableList = shadowVariableList;
    }

    public Map<String, String> getShadowVariableMap() {
        return shadowVariableMap;
    }

    public void setShadowVariableMap(Map<String, String> shadowVariableMap) {
        this.shadowVariableMap = shadowVariableMap;
    }

}
