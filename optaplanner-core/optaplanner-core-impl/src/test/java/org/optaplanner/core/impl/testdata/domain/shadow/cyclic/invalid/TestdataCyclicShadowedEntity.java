package org.optaplanner.core.impl.testdata.domain.shadow.cyclic.invalid;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.DummyVariableListener;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataCyclicShadowedEntity extends TestdataObject {

    public static EntityDescriptor<TestdataCyclicShadowedSolution> buildEntityDescriptor() {
        return TestdataCyclicShadowedSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataCyclicShadowedEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataCyclicShadowedSolution> buildVariableDescriptorForValue() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("value");
    }

    private TestdataValue value;
    private String rockShadow;
    private String paperShadow;
    private String scissorsShadow;

    public TestdataCyclicShadowedEntity() {
    }

    public TestdataCyclicShadowedEntity(String code) {
        super(code);
    }

    public TestdataCyclicShadowedEntity(String code, TestdataValue value) {
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

    @CustomShadowVariable(variableListenerClass = RockShadowUpdatingVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "scissorsShadow") })
    public String getRockShadow() {
        return rockShadow;
    }

    public void setRockShadow(String rockShadow) {
        this.rockShadow = rockShadow;
    }

    @CustomShadowVariable(variableListenerClass = PaperShadowUpdatingVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "rockShadow") })
    public String getPaperShadow() {
        return paperShadow;
    }

    public void setPaperShadow(String paperShadow) {
        this.paperShadow = paperShadow;
    }

    @CustomShadowVariable(variableListenerClass = ScissorsShadowUpdatingVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "paperShadow") })
    public String getScissorsShadow() {
        return scissorsShadow;
    }

    public void setScissorsShadow(String scissorsShadow) {
        this.scissorsShadow = scissorsShadow;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    // ************************************************************************
    // Static inner classes
    // ************************************************************************

    public static class RockShadowUpdatingVariableListener
            extends DummyVariableListener<TestdataCyclicShadowedSolution, TestdataCyclicShadowedEntity> {

        @Override
        public void afterEntityAdded(ScoreDirector<TestdataCyclicShadowedSolution> scoreDirector,
                TestdataCyclicShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        @Override
        public void afterVariableChanged(ScoreDirector<TestdataCyclicShadowedSolution> scoreDirector,
                TestdataCyclicShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        private void updateShadow(TestdataCyclicShadowedEntity entity,
                ScoreDirector<TestdataCyclicShadowedSolution> scoreDirector) {
            String scissors = entity.getScissorsShadow();
            scoreDirector.beforeVariableChanged(entity, "rockShadow");
            entity.setRockShadow("Rock beats (" + scissors + ")");
            scoreDirector.afterVariableChanged(entity, "rockShadow");
        }

    }

    public static class PaperShadowUpdatingVariableListener
            extends DummyVariableListener<TestdataCyclicShadowedSolution, TestdataCyclicShadowedEntity> {

        @Override
        public void afterEntityAdded(ScoreDirector<TestdataCyclicShadowedSolution> scoreDirector,
                TestdataCyclicShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        @Override
        public void afterVariableChanged(ScoreDirector<TestdataCyclicShadowedSolution> scoreDirector,
                TestdataCyclicShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        private void updateShadow(TestdataCyclicShadowedEntity entity,
                ScoreDirector<TestdataCyclicShadowedSolution> scoreDirector) {
            String rock = entity.getRockShadow();
            scoreDirector.beforeVariableChanged(entity, "paperShadow");
            entity.setPaperShadow("Paper beats (" + rock + ")");
            scoreDirector.afterVariableChanged(entity, "paperShadow");
        }

    }

    public static class ScissorsShadowUpdatingVariableListener
            extends DummyVariableListener<TestdataCyclicShadowedSolution, TestdataCyclicShadowedEntity> {

        @Override
        public void afterEntityAdded(ScoreDirector<TestdataCyclicShadowedSolution> scoreDirector,
                TestdataCyclicShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        @Override
        public void afterVariableChanged(ScoreDirector<TestdataCyclicShadowedSolution> scoreDirector,
                TestdataCyclicShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        private void updateShadow(TestdataCyclicShadowedEntity entity,
                ScoreDirector<TestdataCyclicShadowedSolution> scoreDirector) {
            String paper = entity.getPaperShadow();
            scoreDirector.beforeVariableChanged(entity, "scissorsShadow");
            entity.setScissorsShadow("Scissors beats (" + paper + ")");
            scoreDirector.afterVariableChanged(entity, "scissorsShadow");
        }

    }

}
