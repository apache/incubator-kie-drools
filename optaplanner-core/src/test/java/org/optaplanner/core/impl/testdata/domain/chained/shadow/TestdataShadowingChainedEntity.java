/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.testdata.domain.chained.shadow;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataShadowingChainedEntity extends TestdataObject implements TestdataShadowingChainedObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataShadowingChainedSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataShadowingChainedEntity.class);
    }

    private TestdataShadowingChainedObject chainedObject;

    // Shadow variables
    private TestdataShadowingChainedEntity nextEntity;
    private TestdataShadowingChainedAnchor anchor;

    public TestdataShadowingChainedEntity() {
    }

    public TestdataShadowingChainedEntity(String code) {
        super(code);
    }

    public TestdataShadowingChainedEntity(String code, TestdataShadowingChainedObject chainedObject) {
        this(code);
        this.chainedObject = chainedObject;
    }

    @PlanningVariable(valueRangeProviderRefs = {"chainedAnchorRange", "chainedEntityRange"},
            graphType = PlanningVariableGraphType.CHAINED)
    public TestdataShadowingChainedObject getChainedObject() {
        return chainedObject;
    }

    public void setChainedObject(TestdataShadowingChainedObject chainedObject) {
        this.chainedObject = chainedObject;
    }

    @Override
    public TestdataShadowingChainedEntity getNextEntity() {
        return nextEntity;
    }

    @Override
    public void setNextEntity(TestdataShadowingChainedEntity nextEntity) {
        this.nextEntity = nextEntity;
    }

    @AnchorShadowVariable(sourceVariableName = "chainedObject")
    public TestdataShadowingChainedAnchor getAnchor() {
        return anchor;
    }

    public void setAnchor(TestdataShadowingChainedAnchor anchor) {
        this.anchor = anchor;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
