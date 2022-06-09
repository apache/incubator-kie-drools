package org.optaplanner.core.impl.testdata.domain.shadow.manytomany;

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
public class TestdataManyToManyShadowedEntity extends TestdataObject {

    public static EntityDescriptor<TestdataManyToManyShadowedSolution> buildEntityDescriptor() {
        return TestdataManyToManyShadowedSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataManyToManyShadowedEntity.class);
    }

    private TestdataValue primaryValue;
    private TestdataValue secondaryValue;
    private String composedCode;
    private String reverseComposedCode;

    public TestdataManyToManyShadowedEntity() {
    }

    public TestdataManyToManyShadowedEntity(String code) {
        super(code);
    }

    public TestdataManyToManyShadowedEntity(String code, TestdataValue primaryValue, TestdataValue secondaryValue) {
        this(code);
        this.primaryValue = primaryValue;
        this.secondaryValue = secondaryValue;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public TestdataValue getPrimaryValue() {
        return primaryValue;
    }

    public void setPrimaryValue(TestdataValue primaryValue) {
        this.primaryValue = primaryValue;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public TestdataValue getSecondaryValue() {
        return secondaryValue;
    }

    public void setSecondaryValue(TestdataValue secondaryValue) {
        this.secondaryValue = secondaryValue;
    }

    @CustomShadowVariable(variableListenerClass = ComposedValuesUpdatingVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "primaryValue"),
            @PlanningVariableReference(variableName = "secondaryValue") })
    public String getComposedCode() {
        return composedCode;
    }

    public void setComposedCode(String composedCode) {
        this.composedCode = composedCode;
    }

    @CustomShadowVariable(variableListenerRef = @PlanningVariableReference(variableName = "composedCode"))
    public String getReverseComposedCode() {
        return reverseComposedCode;
    }

    public void setReverseComposedCode(String reverseComposedCode) {
        this.reverseComposedCode = reverseComposedCode;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    // ************************************************************************
    // Static inner classes
    // ************************************************************************

    public static class ComposedValuesUpdatingVariableListener
            extends DummyVariableListener<TestdataManyToManyShadowedSolution, TestdataManyToManyShadowedEntity> {

        @Override
        public void afterEntityAdded(ScoreDirector<TestdataManyToManyShadowedSolution> scoreDirector,
                TestdataManyToManyShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        @Override
        public void afterVariableChanged(ScoreDirector<TestdataManyToManyShadowedSolution> scoreDirector,
                TestdataManyToManyShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        private void updateShadow(TestdataManyToManyShadowedEntity entity,
                ScoreDirector<TestdataManyToManyShadowedSolution> scoreDirector) {
            TestdataValue primaryValue = entity.getPrimaryValue();
            TestdataValue secondaryValue = entity.getSecondaryValue();
            String composedValue;
            String reverseComposedValue;
            if (primaryValue == null || secondaryValue == null) {
                composedValue = null;
                reverseComposedValue = null;
            } else {
                composedValue = primaryValue.getCode() + "-" + secondaryValue.getCode();
                reverseComposedValue = secondaryValue.getCode() + "-" + primaryValue.getCode();
            }
            scoreDirector.beforeVariableChanged(entity, "composedCode");
            entity.setComposedCode(composedValue);
            scoreDirector.afterVariableChanged(entity, "composedCode");
            scoreDirector.beforeVariableChanged(entity, "reverseComposedCode");
            entity.setReverseComposedCode(reverseComposedValue);
            scoreDirector.afterVariableChanged(entity, "reverseComposedCode");
        }

    }

}
