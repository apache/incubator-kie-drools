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

package org.optaplanner.core.impl.testdata.domain.reflect.generic;

import java.util.Map;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataGenericEntity<T> extends TestdataObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataGenericSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataGenericEntity.class);
    }

    public static GenuineVariableDescriptor buildVariableDescriptorForValue() {
        SolutionDescriptor solutionDescriptor = TestdataGenericSolution.buildSolutionDescriptor();
        EntityDescriptor entityDescriptor = solutionDescriptor.findEntityDescriptorOrFail(TestdataGenericEntity.class);
        return entityDescriptor.getGenuineVariableDescriptor("value");
    }

    private TestdataGenericValue<T> value;
    private TestdataGenericValue<Map<T, TestdataGenericValue<T>>> complexGenericValue;

    public TestdataGenericEntity() {
    }

    public TestdataGenericEntity(String code) {
        super(code);
    }

    public TestdataGenericEntity(String code, TestdataGenericValue value) {
        this(code);
        this.value = value;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public TestdataGenericValue<T> getValue() {
        return value;
    }

    public void setValue(TestdataGenericValue<T> value) {
        this.value = value;
    }

    @PlanningVariable(valueRangeProviderRefs = "complexGenericValueRange")
    public TestdataGenericValue<Map<T, TestdataGenericValue<T>>> getComplexGenericValue() {
        return complexGenericValue;
    }

    public void setComplexGenericValue(TestdataGenericValue<Map<T, TestdataGenericValue<T>>> complexGenericValue) {
        this.complexGenericValue = complexGenericValue;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
