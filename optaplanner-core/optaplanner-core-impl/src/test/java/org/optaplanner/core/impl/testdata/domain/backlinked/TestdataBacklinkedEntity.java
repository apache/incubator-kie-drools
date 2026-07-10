/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.testdata.domain.backlinked;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataBacklinkedEntity extends TestdataObject {

    public static EntityDescriptor<TestdataBacklinkedSolution> buildEntityDescriptor() {
        return TestdataBacklinkedSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataBacklinkedEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataBacklinkedSolution> buildVariableDescriptorForValue() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("value");
    }

    private TestdataBacklinkedSolution solution;
    private TestdataValue value;

    public TestdataBacklinkedEntity() {
    }

    public TestdataBacklinkedEntity(TestdataBacklinkedSolution solution, String code) {
        this(solution, code, null);
    }

    public TestdataBacklinkedEntity(TestdataBacklinkedSolution solution, String code, TestdataValue value) {
        super(code);
        this.solution = solution;
        this.value = value;
    }

    public TestdataBacklinkedSolution getSolution() {
        return solution;
    }

    public void setSolution(TestdataBacklinkedSolution solution) {
        this.solution = solution;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public TestdataValue getValue() {
        return value;
    }

    public void setValue(TestdataValue value) {
        this.value = value;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
