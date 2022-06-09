package org.optaplanner.core.impl.testdata.domain.shadow.corrupted;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.testdata.domain.DummyVariableListener;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataCorruptedShadowedEntity extends TestdataObject {

    public static EntityDescriptor<TestdataCorruptedShadowedSolution> buildEntityDescriptor() {
        return TestdataCorruptedShadowedSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataCorruptedShadowedEntity.class);
    }

    private TestdataValue value;
    private Integer count;

    public TestdataCorruptedShadowedEntity() {
    }

    public TestdataCorruptedShadowedEntity(String code) {
        super(code);
    }

    public TestdataCorruptedShadowedEntity(String code, TestdataValue value, int count) {
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

    @CustomShadowVariable(variableListenerClass = CountUpdatingVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "value") })
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    // ************************************************************************
    // Static inner classes
    // ************************************************************************

    public static class CountUpdatingVariableListener
            extends DummyVariableListener<TestdataCorruptedShadowedSolution, TestdataCorruptedShadowedEntity> {

        @Override
        public void afterEntityAdded(ScoreDirector<TestdataCorruptedShadowedSolution> scoreDirector,
                TestdataCorruptedShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        @Override
        public void afterVariableChanged(ScoreDirector<TestdataCorruptedShadowedSolution> scoreDirector,
                TestdataCorruptedShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        private void updateShadow(TestdataCorruptedShadowedEntity entity,
                ScoreDirector<TestdataCorruptedShadowedSolution> scoreDirector) {
            TestdataValue primaryValue = entity.getValue();
            Integer count;
            if (primaryValue == null) {
                count = null;
            } else {
                count = (entity.getCount() == null) ? 0 : entity.getCount();
                count++;
            }
            scoreDirector.beforeVariableChanged(entity, "count");
            entity.setCount(count);
            scoreDirector.afterVariableChanged(entity, "count");
        }

    }

}
