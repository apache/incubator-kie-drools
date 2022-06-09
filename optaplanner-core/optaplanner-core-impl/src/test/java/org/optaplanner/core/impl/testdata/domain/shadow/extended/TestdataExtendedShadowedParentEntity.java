package org.optaplanner.core.impl.testdata.domain.shadow.extended;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.DummyVariableListener;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataExtendedShadowedParentEntity extends TestdataObject {

    public static EntityDescriptor<TestdataExtendedShadowedSolution> buildEntityDescriptor() {
        return TestdataExtendedShadowedSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataExtendedShadowedParentEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataExtendedShadowedSolution> buildVariableDescriptorForValue() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("value");
    }

    private TestdataValue value;
    private String firstShadow;
    private String thirdShadow;

    public TestdataExtendedShadowedParentEntity() {
    }

    public TestdataExtendedShadowedParentEntity(String code) {
        super(code);
    }

    public TestdataExtendedShadowedParentEntity(String code, TestdataValue value) {
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

    @CustomShadowVariable(variableListenerClass = FirstShadowUpdatingVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "value") })
    public String getFirstShadow() {
        return firstShadow;
    }

    public void setFirstShadow(String firstShadow) {
        this.firstShadow = firstShadow;
    }

    @CustomShadowVariable(variableListenerClass = ThirdShadowUpdatingVariableListener.class,
            sources = { @PlanningVariableReference(entityClass = TestdataExtendedShadowedChildEntity.class,
                    variableName = "secondShadow") })
    public String getThirdShadow() {
        return thirdShadow;
    }

    public void setThirdShadow(String thirdShadow) {
        this.thirdShadow = thirdShadow;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    // ************************************************************************
    // Static inner classes
    // ************************************************************************

    public static class FirstShadowUpdatingVariableListener
            extends DummyVariableListener<TestdataExtendedShadowedSolution, TestdataExtendedShadowedParentEntity> {

        @Override
        public void afterEntityAdded(ScoreDirector<TestdataExtendedShadowedSolution> scoreDirector,
                TestdataExtendedShadowedParentEntity entity) {
            updateShadow(scoreDirector, entity);
        }

        @Override
        public void afterVariableChanged(ScoreDirector<TestdataExtendedShadowedSolution> scoreDirector,
                TestdataExtendedShadowedParentEntity entity) {
            updateShadow(scoreDirector, entity);
        }

        private void updateShadow(ScoreDirector<TestdataExtendedShadowedSolution> scoreDirector,
                TestdataExtendedShadowedParentEntity entity) {
            TestdataValue value = entity.getValue();
            scoreDirector.beforeVariableChanged(entity, "firstShadow");
            entity.setFirstShadow((value == null) ? null : value.getCode() + "/firstShadow");
            scoreDirector.afterVariableChanged(entity, "firstShadow");
        }

    }

    public static class ThirdShadowUpdatingVariableListener
            implements VariableListener<TestdataExtendedShadowedSolution, TestdataExtendedShadowedChildEntity> {

        @Override
        public void beforeEntityAdded(ScoreDirector<TestdataExtendedShadowedSolution> scoreDirector,
                TestdataExtendedShadowedChildEntity testdataExtendedShadowedChildEntity) {
            // Do nothing.
        }

        @Override
        public void afterEntityAdded(ScoreDirector<TestdataExtendedShadowedSolution> scoreDirector,
                TestdataExtendedShadowedChildEntity entity) {
            updateShadow(scoreDirector, entity);
        }

        @Override
        public void beforeVariableChanged(ScoreDirector<TestdataExtendedShadowedSolution> scoreDirector,
                TestdataExtendedShadowedChildEntity testdataExtendedShadowedChildEntity) {
            // Do nothing.
        }

        @Override
        public void afterVariableChanged(ScoreDirector<TestdataExtendedShadowedSolution> scoreDirector,
                TestdataExtendedShadowedChildEntity entity) {
            updateShadow(scoreDirector, entity);
        }

        @Override
        public void beforeEntityRemoved(ScoreDirector<TestdataExtendedShadowedSolution> scoreDirector,
                TestdataExtendedShadowedChildEntity testdataExtendedShadowedChildEntity) {
            // Do nothing.
        }

        @Override
        public void afterEntityRemoved(ScoreDirector<TestdataExtendedShadowedSolution> scoreDirector,
                TestdataExtendedShadowedChildEntity testdataExtendedShadowedChildEntity) {
            // Do nothing.
        }

        private void updateShadow(ScoreDirector<TestdataExtendedShadowedSolution> scoreDirector,
                TestdataExtendedShadowedChildEntity entity) {
            String secondShadow = entity.getSecondShadow();
            scoreDirector.beforeVariableChanged(entity, "thirdShadow");
            entity.setThirdShadow((secondShadow == null) ? null : secondShadow + "/thirdShadow");
            scoreDirector.afterVariableChanged(entity, "thirdShadow");
        }

    }

}
