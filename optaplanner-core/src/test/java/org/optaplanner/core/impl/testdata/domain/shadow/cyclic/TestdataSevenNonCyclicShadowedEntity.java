/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.testdata.domain.shadow.cyclic;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListenerAdapter;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataSevenNonCyclicShadowedEntity extends TestdataObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataSevenNonCyclicShadowedSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataSevenNonCyclicShadowedEntity.class);
    }

    public static GenuineVariableDescriptor buildVariableDescriptorForValue() {
        SolutionDescriptor solutionDescriptor = TestdataSevenNonCyclicShadowedSolution.buildSolutionDescriptor();
        EntityDescriptor entityDescriptor = solutionDescriptor.findEntityDescriptorOrFail(TestdataSevenNonCyclicShadowedEntity.class);
        return entityDescriptor.getGenuineVariableDescriptor("value");
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

    @CustomShadowVariable(variableListenerClass = VariableListenerAdapter.class,
            sources = {@PlanningVariableReference(variableName = "secondShadow")})
    public String getThirdShadow() {
        return thirdShadow;
    }

    public void setThirdShadow(String thirdShadow) {
        this.thirdShadow = thirdShadow;
    }

    @CustomShadowVariable(variableListenerClass = VariableListenerAdapter.class,
            sources = {@PlanningVariableReference(variableName = "fourthShadow")})
    public String getFifthShadow() {
        return fifthShadow;
    }

    public void setFifthShadow(String fifthShadow) {
        this.fifthShadow = fifthShadow;
    }

    @CustomShadowVariable(variableListenerClass = VariableListenerAdapter.class,
            sources = {@PlanningVariableReference(variableName = "value")})
    public String getFirstShadow() {
        return firstShadow;
    }

    public void setFirstShadow(String firstShadow) {
        this.firstShadow = firstShadow;
    }

    @CustomShadowVariable(variableListenerClass = VariableListenerAdapter.class,
            sources = {@PlanningVariableReference(variableName = "thirdShadow")})
    public String getFourthShadow() {
        return fourthShadow;
    }

    public void setFourthShadow(String fourthShadow) {
        this.fourthShadow = fourthShadow;
    }

    @CustomShadowVariable(variableListenerClass = VariableListenerAdapter.class,
            sources = {@PlanningVariableReference(variableName = "firstShadow")})
    public String getSecondShadow() {
        return secondShadow;
    }

    public void setSecondShadow(String secondShadow) {
        this.secondShadow = secondShadow;
    }

    @CustomShadowVariable(variableListenerClass = VariableListenerAdapter.class,
            sources = {@PlanningVariableReference(variableName = "sixthShadow")})
    public String getSeventhShadow() {
        return seventhShadow;
    }

    public void setSeventhShadow(String seventhShadow) {
        this.seventhShadow = seventhShadow;
    }

    @CustomShadowVariable(variableListenerClass = VariableListenerAdapter.class,
            sources = {@PlanningVariableReference(variableName = "fifthShadow")})
    public String getSixthShadow() {
        return sixthShadow;
    }

    public void setSixthShadow(String sixthShadow) {
        this.sixthShadow = sixthShadow;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
