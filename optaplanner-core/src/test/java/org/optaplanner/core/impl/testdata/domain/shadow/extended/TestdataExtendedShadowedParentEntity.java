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

package org.optaplanner.core.impl.testdata.domain.shadow.extended;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListenerAdapter;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataExtendedShadowedParentEntity extends TestdataObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataExtendedShadowedSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataExtendedShadowedParentEntity.class);
    }

    public static GenuineVariableDescriptor buildVariableDescriptorForValue() {
        SolutionDescriptor solutionDescriptor = TestdataExtendedShadowedSolution.buildSolutionDescriptor();
        EntityDescriptor entityDescriptor = solutionDescriptor
                .findEntityDescriptorOrFail(TestdataExtendedShadowedParentEntity.class);
        return entityDescriptor.getGenuineVariableDescriptor("value");
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
            extends VariableListenerAdapter<TestdataExtendedShadowedParentEntity> {

        @Override
        public void afterEntityAdded(ScoreDirector scoreDirector, TestdataExtendedShadowedParentEntity entity) {
            updateShadow(scoreDirector, entity);
        }

        @Override
        public void afterVariableChanged(ScoreDirector scoreDirector, TestdataExtendedShadowedParentEntity entity) {
            updateShadow(scoreDirector, entity);
        }

        private void updateShadow(ScoreDirector scoreDirector, TestdataExtendedShadowedParentEntity entity) {
            TestdataValue value = entity.getValue();
            scoreDirector.beforeVariableChanged(entity, "firstShadow");
            entity.setFirstShadow((value == null) ? null : value.getCode() + "/firstShadow");
            scoreDirector.afterVariableChanged(entity, "firstShadow");
        }

    }

    public static class ThirdShadowUpdatingVariableListener
            extends VariableListenerAdapter<TestdataExtendedShadowedChildEntity> {

        @Override
        public void afterEntityAdded(ScoreDirector scoreDirector, TestdataExtendedShadowedChildEntity entity) {
            updateShadow(scoreDirector, entity);
        }

        @Override
        public void afterVariableChanged(ScoreDirector scoreDirector, TestdataExtendedShadowedChildEntity entity) {
            updateShadow(scoreDirector, entity);
        }

        private void updateShadow(ScoreDirector scoreDirector, TestdataExtendedShadowedChildEntity entity) {
            String secondShadow = entity.getSecondShadow();
            scoreDirector.beforeVariableChanged(entity, "thirdShadow");
            entity.setThirdShadow((secondShadow == null) ? null : secondShadow + "/thirdShadow");
            scoreDirector.afterVariableChanged(entity, "thirdShadow");
        }

    }

}
