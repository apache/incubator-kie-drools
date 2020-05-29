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

package org.optaplanner.core.impl.testdata.domain.pinned.extended;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataPinnedEntity;

@PlanningEntity(pinningFilter = TestdataExtendedPinningFilter.class)
public class TestdataExtendedPinnedEntity extends TestdataPinnedEntity {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataExtendedPinnedSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataExtendedPinnedEntity.class);
    }

    private TestdataValue subValue;
    private boolean closed;
    private boolean pinnedByBoss;

    public TestdataExtendedPinnedEntity() {
    }

    public TestdataExtendedPinnedEntity(String code) {
        super(code);
    }

    public TestdataExtendedPinnedEntity(String code, TestdataValue value, TestdataValue subValue) {
        super(code, value);
        this.subValue = subValue;
    }

    public TestdataExtendedPinnedEntity(String code, TestdataValue value, boolean locked, boolean pinned,
            TestdataValue subValue, boolean closed, boolean pinnedByBoss) {
        super(code, value, locked, pinned);
        this.subValue = subValue;
        this.closed = closed;
        this.pinnedByBoss = pinnedByBoss;
    }

    @PlanningVariable(valueRangeProviderRefs = "subValueRange")
    public TestdataValue getSubValue() {
        return subValue;
    }

    public void setSubValue(TestdataValue subValue) {
        this.subValue = subValue;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @PlanningPin
    public boolean isPinnedByBoss() {
        return pinnedByBoss;
    }

    public void setPinnedByBoss(boolean pinnedByBoss) {
        this.pinnedByBoss = pinnedByBoss;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
