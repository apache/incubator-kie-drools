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

package org.optaplanner.core.impl.testdata.domain.reinitialize;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataReinitializeEntity extends TestdataObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataReinitializeSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataReinitializeEntity.class);
    }

    private TestdataValue value;
    private boolean initialized;

    public TestdataReinitializeEntity() {
    }

    public TestdataReinitializeEntity(String code) {
        super(code);
    }

    public TestdataReinitializeEntity(String code, TestdataValue value) {
        this(code);
        this.value = value;
    }

    public TestdataReinitializeEntity(String code, TestdataValue value, boolean initialized) {
        this(code, value);
        this.initialized = initialized;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange",
            reinitializeVariableEntityFilter = TestdataReinitializeEntityFilter.class)
    public TestdataValue getValue() {
        return value;
    }

    public void setValue(TestdataValue value) {
        this.value = value;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
