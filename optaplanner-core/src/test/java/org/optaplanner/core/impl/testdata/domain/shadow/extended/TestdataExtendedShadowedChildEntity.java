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
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListenerAdapter;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataExtendedShadowedChildEntity extends TestdataExtendedShadowedParentEntity {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataExtendedShadowedSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataExtendedShadowedChildEntity.class);
    }

    private String secondShadow;

    public TestdataExtendedShadowedChildEntity() {
    }

    public TestdataExtendedShadowedChildEntity(String code) {
        super(code);
    }

    public TestdataExtendedShadowedChildEntity(String code, TestdataValue value) {
        super(code, value);
    }

    @CustomShadowVariable(variableListenerClass = SecondShadowUpdatingVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "firstShadow") })
    public String getSecondShadow() {
        return secondShadow;
    }

    public void setSecondShadow(String secondShadow) {
        this.secondShadow = secondShadow;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    // ************************************************************************
    // Static inner classes
    // ************************************************************************

    public static class SecondShadowUpdatingVariableListener
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
            String firstShadow = entity.getFirstShadow();
            if (entity instanceof TestdataExtendedShadowedChildEntity) {
                TestdataExtendedShadowedChildEntity childEntity = (TestdataExtendedShadowedChildEntity) entity;
                scoreDirector.beforeVariableChanged(childEntity, "secondShadow");
                childEntity.setSecondShadow((firstShadow == null) ? null : firstShadow + "/secondShadow");
                scoreDirector.afterVariableChanged(childEntity, "secondShadow");
            }
        }

    }

}
