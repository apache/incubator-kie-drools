/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream.testdata;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataLavishEntity extends TestdataObject {

    public static EntityDescriptor<TestdataLavishSolution> buildEntityDescriptor() {
        SolutionDescriptor<TestdataLavishSolution> solutionDescriptor = TestdataLavishSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataLavishEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataLavishSolution> buildVariableDescriptorForValue() {
        SolutionDescriptor<TestdataLavishSolution> solutionDescriptor = TestdataLavishSolution.buildSolutionDescriptor();
        EntityDescriptor<TestdataLavishSolution> entityDescriptor = solutionDescriptor.findEntityDescriptorOrFail(TestdataLavishEntity.class);
        return entityDescriptor.getGenuineVariableDescriptor("value");
    }

    private TestdataLavishEntityGroup entityGroup;
    private TestdataLavishValue value;

    public TestdataLavishEntity() {
    }

    public TestdataLavishEntity(String code, TestdataLavishEntityGroup entityGroup) {
        super(code);
        this.entityGroup = entityGroup;
    }

    public TestdataLavishEntity(String code, TestdataLavishEntityGroup entityGroup, TestdataLavishValue value) {
        this(code, entityGroup);
        this.value = value;
    }

    public TestdataLavishEntityGroup getEntityGroup() {
        return entityGroup;
    }

    public void setEntityGroup(TestdataLavishEntityGroup entityGroup) {
        this.entityGroup = entityGroup;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public TestdataLavishValue getValue() {
        return value;
    }

    public void setValue(TestdataLavishValue value) {
        this.value = value;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
