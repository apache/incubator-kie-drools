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

package org.optaplanner.quarkus.testdata.chained.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

@PlanningEntity
public class TestdataChainedQuarkusEntity implements TestdataChainedQuarkusObject {

    @PlanningVariable(valueRangeProviderRefs = { "chainedAnchorRange",
            "chainedEntityRange" }, graphType = PlanningVariableGraphType.CHAINED)
    private TestdataChainedQuarkusObject previous;

    private TestdataChainedQuarkusEntity next;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public TestdataChainedQuarkusObject getPrevious() {
        return previous;
    }

    public void setPrevious(TestdataChainedQuarkusObject previous) {
        this.previous = previous;
    }

    @Override
    public TestdataChainedQuarkusEntity getNext() {
        return next;
    }

    @Override
    public void setNext(TestdataChainedQuarkusEntity next) {
        this.next = next;
    }

}
