package org.optaplanner.core.impl.testdata.domain.shadow;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.ShadowVariable;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.DummyVariableListener;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataShadowedEntity extends TestdataObject {

    public static EntityDescriptor<TestdataShadowedSolution> buildEntityDescriptor() {
        return TestdataShadowedSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataShadowedEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataShadowedSolution> buildVariableDescriptorForValue() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("value");
    }

    private TestdataValue value;
    private String firstShadow;

    public TestdataShadowedEntity() {
    }

    public TestdataShadowedEntity(String code) {
        super(code);
    }

    public TestdataShadowedEntity(String code, TestdataValue value) {
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

    @ShadowVariable(variableListenerClass = FirstShadowUpdatingVariableListener.class, sourceVariableName = "value")
    public String getFirstShadow() {
        return firstShadow;
    }

    public void setFirstShadow(String firstShadow) {
        this.firstShadow = firstShadow;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    // ************************************************************************
    // Static inner classes
    // ************************************************************************

    public static class FirstShadowUpdatingVariableListener
            extends DummyVariableListener<TestdataShadowedSolution, TestdataShadowedEntity> {

        @Override
        public void afterEntityAdded(ScoreDirector<TestdataShadowedSolution> scoreDirector,
                TestdataShadowedEntity entity) {
            updateShadow(scoreDirector, entity);
        }

        @Override
        public void afterVariableChanged(ScoreDirector<TestdataShadowedSolution> scoreDirector,
                TestdataShadowedEntity entity) {
            updateShadow(scoreDirector, entity);
        }

        private void updateShadow(ScoreDirector<TestdataShadowedSolution> scoreDirector,
                TestdataShadowedEntity entity) {
            TestdataValue value = entity.getValue();
            scoreDirector.beforeVariableChanged(entity, "firstShadow");
            entity.setFirstShadow((value == null) ? null : value.getCode() + "/firstShadow");
            scoreDirector.afterVariableChanged(entity, "firstShadow");
        }

    }

}
