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

package org.drools.planner.core.testdata.domain.multivar;

import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.testdata.domain.TestdataObject;
import org.drools.planner.core.testdata.domain.TestdataValue;

import static org.mockito.Mockito.*;

@PlanningEntity
public class TestdataMultiVarEntity extends TestdataObject {

    public static PlanningEntityDescriptor buildEntityDescriptor() {
        return buildEntityDescriptor(mock(SolutionDescriptor.class));
    }

    public static PlanningEntityDescriptor buildEntityDescriptor(SolutionDescriptor solutionDescriptor) {
        PlanningEntityDescriptor entityDescriptor = new PlanningEntityDescriptor(
                solutionDescriptor, TestdataMultiVarEntity.class);
        entityDescriptor.processAnnotations();
        return entityDescriptor;
    }

    private TestdataValue primaryValue;
    private TestdataValue secondaryValue;

    private TestdataOtherValue nullableOtherValue;

    public TestdataMultiVarEntity() {
    }

    public TestdataMultiVarEntity(String code) {
        super(code);
    }

    public TestdataMultiVarEntity(String code, TestdataValue primaryValue, TestdataValue secondaryValue,
            TestdataOtherValue nullableOtherValue) {
        super(code);
        this.primaryValue = primaryValue;
        this.secondaryValue = secondaryValue;
        this.nullableOtherValue = nullableOtherValue;
    }

    @PlanningVariable
    @ValueRange(type = ValueRangeType.UNDEFINED)
    public TestdataValue getPrimaryValue() {
        return primaryValue;
    }

    public void setPrimaryValue(TestdataValue primaryValue) {
        this.primaryValue = primaryValue;
    }

    @PlanningVariable()
    @ValueRange(type = ValueRangeType.UNDEFINED)
    public TestdataValue getSecondaryValue() {
        return secondaryValue;
    }

    public void setSecondaryValue(TestdataValue secondaryValue) {
        this.secondaryValue = secondaryValue;
    }

    @PlanningVariable(nullable = true)
    @ValueRange(type = ValueRangeType.UNDEFINED)
    public TestdataOtherValue getNullableOtherValue() {
        return nullableOtherValue;
    }

    public void setNullableOtherValue(TestdataOtherValue nullableOtherValue) {
        this.nullableOtherValue = nullableOtherValue;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
