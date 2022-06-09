package org.optaplanner.core.impl.testdata.domain.shadow.cyclic;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.DummyVariableListener;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataSevenNonCyclicShadowedEntity extends TestdataObject {

    public static EntityDescriptor<TestdataSevenNonCyclicShadowedSolution> buildEntityDescriptor() {
        return TestdataSevenNonCyclicShadowedSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataSevenNonCyclicShadowedEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataSevenNonCyclicShadowedSolution> buildVariableDescriptorForValue() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("value");
    }

    private TestdataValue value;
    // Intentionally out of order
    private String thirdShadow;
    private String fifthShadow;
    private String firstShadow;
    private String fourthShadow;
    private String secondShadow;
    private String seventhShadow;
    private String sixthShadow;

    public TestdataSevenNonCyclicShadowedEntity() {
    }

    public TestdataSevenNonCyclicShadowedEntity(String code) {
        super(code);
    }

    public TestdataSevenNonCyclicShadowedEntity(String code, TestdataValue value) {
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

    @CustomShadowVariable(variableListenerClass = DummyVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "secondShadow") })
    public String getThirdShadow() {
        return thirdShadow;
    }

    public void setThirdShadow(String thirdShadow) {
        this.thirdShadow = thirdShadow;
    }

    @CustomShadowVariable(variableListenerClass = DummyVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "fourthShadow") })
    public String getFifthShadow() {
        return fifthShadow;
    }

    public void setFifthShadow(String fifthShadow) {
        this.fifthShadow = fifthShadow;
    }

    @CustomShadowVariable(variableListenerClass = DummyVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "value") })
    public String getFirstShadow() {
        return firstShadow;
    }

    public void setFirstShadow(String firstShadow) {
        this.firstShadow = firstShadow;
    }

    @CustomShadowVariable(variableListenerClass = DummyVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "thirdShadow") })
    public String getFourthShadow() {
        return fourthShadow;
    }

    public void setFourthShadow(String fourthShadow) {
        this.fourthShadow = fourthShadow;
    }

    @CustomShadowVariable(variableListenerClass = DummyVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "firstShadow") })
    public String getSecondShadow() {
        return secondShadow;
    }

    public void setSecondShadow(String secondShadow) {
        this.secondShadow = secondShadow;
    }

    @CustomShadowVariable(variableListenerClass = DummyVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "sixthShadow") })
    public String getSeventhShadow() {
        return seventhShadow;
    }

    public void setSeventhShadow(String seventhShadow) {
        this.seventhShadow = seventhShadow;
    }

    @CustomShadowVariable(variableListenerClass = DummyVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "fifthShadow") })
    public String getSixthShadow() {
        return sixthShadow;
    }

    public void setSixthShadow(String sixthShadow) {
        this.sixthShadow = sixthShadow;
    }

}
