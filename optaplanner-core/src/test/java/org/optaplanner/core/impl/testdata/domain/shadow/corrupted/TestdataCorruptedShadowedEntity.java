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

package org.optaplanner.core.impl.testdata.domain.shadow.corrupted;

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
public class TestdataCorruptedShadowedEntity extends TestdataObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataCorruptedShadowedSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataCorruptedShadowedEntity.class);
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

    public static class CountUpdatingVariableListener extends VariableListenerAdapter<TestdataCorruptedShadowedEntity> {

        @Override
        public void afterEntityAdded(ScoreDirector scoreDirector, TestdataCorruptedShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        @Override
        public void afterVariableChanged(ScoreDirector scoreDirector, TestdataCorruptedShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        private void updateShadow(TestdataCorruptedShadowedEntity entity, ScoreDirector scoreDirector) {
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
