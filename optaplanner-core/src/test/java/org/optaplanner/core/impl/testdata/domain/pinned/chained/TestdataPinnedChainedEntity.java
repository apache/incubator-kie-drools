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

package org.optaplanner.core.impl.testdata.domain.pinned.chained;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedObject;

@PlanningEntity(pinningFilter = TestdataChainedEntityPinningFilter.class)
public class TestdataPinnedChainedEntity extends TestdataObject implements TestdataChainedObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataPinnedChainedSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataPinnedChainedEntity.class);
    }

    public static GenuineVariableDescriptor buildVariableDescriptorForChainedObject() {
        SolutionDescriptor solutionDescriptor = TestdataPinnedChainedSolution.buildSolutionDescriptor();
        EntityDescriptor entityDescriptor = solutionDescriptor.findEntityDescriptorOrFail(TestdataPinnedChainedEntity.class);
        return entityDescriptor.getGenuineVariableDescriptor("chainedObject");
    }

    private TestdataChainedObject chainedObject;
    private boolean pinned;

    public TestdataPinnedChainedEntity() {
    }

    public TestdataPinnedChainedEntity(String code) {
        super(code);
    }

    public TestdataPinnedChainedEntity(String code, TestdataChainedObject chainedObject) {
        this(code);
        this.chainedObject = chainedObject;
    }

    public TestdataPinnedChainedEntity(String code, TestdataChainedObject chainedObject, boolean pinned) {
        this(code, chainedObject);
        this.pinned = pinned;
    }

    @PlanningVariable(valueRangeProviderRefs = { "chainedAnchorRange",
            "chainedEntityRange" }, graphType = PlanningVariableGraphType.CHAINED)
    public TestdataChainedObject getChainedObject() {
        return chainedObject;
    }

    public void setChainedObject(TestdataChainedObject chainedObject) {
        this.chainedObject = chainedObject;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
