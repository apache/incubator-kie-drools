/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.testdata.domain.clone.deepcloning;

import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.cloner.DeepPlanningClone;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataDeepCloningEntity extends TestdataObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataDeepCloningSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataDeepCloningEntity.class);
    }

    public static GenuineVariableDescriptor buildVariableDescriptorForValue() {
        SolutionDescriptor solutionDescriptor = TestdataDeepCloningSolution.buildSolutionDescriptor();
        EntityDescriptor entityDescriptor = solutionDescriptor.findEntityDescriptorOrFail(TestdataDeepCloningEntity.class);
        return entityDescriptor.getGenuineVariableDescriptor("value");
    }

    private TestdataValue value;
    private List<String> shadowVariableList;
    private Map<String, String> shadowVariableMap;

    public TestdataDeepCloningEntity() {
    }

    public TestdataDeepCloningEntity(String code) {
        super(code);
    }

    public TestdataDeepCloningEntity(String code, TestdataValue value) {
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

    @DeepPlanningClone
    @CustomShadowVariable(sources = {@PlanningVariableReference(variableName = "value")},
            variableListenerClass = DummyVariableListener.class)
    public List<String> getShadowVariableList() {
        return shadowVariableList;
    }

    public void setShadowVariableList(List<String> shadowVariableList) {
        this.shadowVariableList = shadowVariableList;
    }

    @DeepPlanningClone
    @CustomShadowVariable(sources = {@PlanningVariableReference(variableName = "value")},
            variableListenerClass = DummyVariableListener.class)
    public Map<String, String> getShadowVariableMap() {
        return shadowVariableMap;
    }

    public void setShadowVariableMap(Map<String, String> shadowVariableMap) {
        this.shadowVariableMap = shadowVariableMap;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public static class DummyVariableListener implements VariableListener<TestdataDeepCloningEntity> {

        @Override
        public void beforeEntityAdded(ScoreDirector scoreDirector, TestdataDeepCloningEntity testdataDeepCloningEntity) {
            // Do nothing
        }

        @Override
        public void afterEntityAdded(ScoreDirector scoreDirector, TestdataDeepCloningEntity testdataDeepCloningEntity) {
            // Do nothing
        }

        @Override
        public void beforeVariableChanged(ScoreDirector scoreDirector, TestdataDeepCloningEntity testdataDeepCloningEntity) {
            // Do nothing
        }

        @Override
        public void afterVariableChanged(ScoreDirector scoreDirector, TestdataDeepCloningEntity testdataDeepCloningEntity) {
            // Do nothing
        }

        @Override
        public void beforeEntityRemoved(ScoreDirector scoreDirector, TestdataDeepCloningEntity testdataDeepCloningEntity) {
            // Do nothing
        }

        @Override
        public void afterEntityRemoved(ScoreDirector scoreDirector, TestdataDeepCloningEntity testdataDeepCloningEntity) {
            // Do nothing
        }

    }

}
