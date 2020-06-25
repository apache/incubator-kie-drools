/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.testdata.domain.shadow.manytomany;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListenerAdapter;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataManyToManyShadowedEntity extends TestdataObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataManyToManyShadowedSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataManyToManyShadowedEntity.class);
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
            extends VariableListenerAdapter<TestdataManyToManyShadowedEntity> {

        @Override
        public void afterEntityAdded(ScoreDirector scoreDirector, TestdataManyToManyShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        @Override
        public void afterVariableChanged(ScoreDirector scoreDirector, TestdataManyToManyShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        private void updateShadow(TestdataManyToManyShadowedEntity entity, ScoreDirector scoreDirector) {
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
