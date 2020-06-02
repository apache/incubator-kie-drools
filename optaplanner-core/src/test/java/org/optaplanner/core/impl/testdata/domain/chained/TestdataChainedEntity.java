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

package org.optaplanner.core.impl.testdata.domain.chained;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataChainedEntity extends TestdataObject implements TestdataChainedObject {

    public static EntityDescriptor<TestdataChainedSolution> buildEntityDescriptor() {
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor = TestdataChainedSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataChainedEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataChainedSolution> buildVariableDescriptorForChainedObject() {
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor = TestdataChainedSolution.buildSolutionDescriptor();
        EntityDescriptor<TestdataChainedSolution> entityDescriptor = solutionDescriptor
                .findEntityDescriptorOrFail(TestdataChainedEntity.class);
        return entityDescriptor.getGenuineVariableDescriptor("chainedObject");
    }

    public static GenuineVariableDescriptor<TestdataChainedSolution> buildVariableDescriptorForUnchainedValue() {
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor = TestdataChainedSolution.buildSolutionDescriptor();
        EntityDescriptor<TestdataChainedSolution> entityDescriptor = solutionDescriptor
                .findEntityDescriptorOrFail(TestdataChainedEntity.class);
        return entityDescriptor.getGenuineVariableDescriptor("unchainedValue");
    }

    private TestdataChainedObject chainedObject;
    private TestdataValue unchainedValue;

    public TestdataChainedEntity() {
    }

    public TestdataChainedEntity(String code) {
        super(code);
    }

    public TestdataChainedEntity(String code, TestdataChainedObject chainedObject) {
        this(code);
        this.chainedObject = chainedObject;
    }

    @PlanningVariable(valueRangeProviderRefs = { "chainedAnchorRange",
            "chainedEntityRange" }, graphType = PlanningVariableGraphType.CHAINED)
    public TestdataChainedObject getChainedObject() {
        return chainedObject;
    }

    public void setChainedObject(TestdataChainedObject chainedObject) {
        this.chainedObject = chainedObject;
    }

    @PlanningVariable(valueRangeProviderRefs = { "unchainedRange" })
    public TestdataValue getUnchainedValue() {
        return unchainedValue;
    }

    public void setUnchainedValue(TestdataValue unchainedValue) {
        this.unchainedValue = unchainedValue;
    }

    public void getUnchainedObject(TestdataChainedObject chainedObject) {
        this.chainedObject = chainedObject;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
