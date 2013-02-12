/*
 * Copyright 2013 JBoss Inc
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

package org.drools.planner.core.testdata.domain.setbased;

import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.value.ValueRange;
import org.drools.planner.api.domain.value.ValueRangeType;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.testdata.domain.TestdataObject;
import org.drools.planner.core.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataSetBasedEntity extends TestdataObject {

    public static PlanningEntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataSetBasedSolution.buildSolutionDescriptor();
        return solutionDescriptor.getPlanningEntityDescriptor(TestdataSetBasedEntity.class);
    }

    private TestdataValue value;

    public TestdataSetBasedEntity() {
    }

    public TestdataSetBasedEntity(String code) {
        super(code);
    }

    public TestdataSetBasedEntity(String code, TestdataValue value) {
        this(code);
        this.value = value;
    }

    @PlanningVariable
    @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "valueSet")
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
