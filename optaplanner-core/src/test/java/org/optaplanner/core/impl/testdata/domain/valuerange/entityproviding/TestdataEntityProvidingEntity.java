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

package org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding;

import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataEntityProvidingEntity extends TestdataObject {

    public static EntityDescriptor<TestdataEntityProvidingSolution> buildEntityDescriptor() {
        SolutionDescriptor<TestdataEntityProvidingSolution> solutionDescriptor = TestdataEntityProvidingSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataEntityProvidingEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataEntityProvidingSolution> buildVariableDescriptorForValue() {
        SolutionDescriptor<TestdataEntityProvidingSolution> solutionDescriptor = TestdataEntityProvidingSolution.buildSolutionDescriptor();
        EntityDescriptor<TestdataEntityProvidingSolution> entityDescriptor = solutionDescriptor.findEntityDescriptorOrFail(TestdataEntityProvidingEntity.class);
        return entityDescriptor.getGenuineVariableDescriptor("value");
    }

    private final List<TestdataValue> valueRange;

    private TestdataValue value;

    public TestdataEntityProvidingEntity(String code, List<TestdataValue> valueRange) {
        this(code, valueRange, null);
    }

    public TestdataEntityProvidingEntity(String code, List<TestdataValue> valueRange, TestdataValue value) {
        super(code);
        this.valueRange = valueRange;
        this.value = value;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange", nullable = true)
    public TestdataValue getValue() {
        return value;
    }

    public void setValue(TestdataValue value) {
        this.value = value;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @ValueRangeProvider(id = "valueRange")
    public List<TestdataValue> getValueRange() {
        return valueRange;
    }

}
