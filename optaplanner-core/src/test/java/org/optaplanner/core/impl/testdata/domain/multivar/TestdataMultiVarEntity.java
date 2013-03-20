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

package org.optaplanner.core.impl.testdata.domain.multivar;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.api.domain.value.ValueRangeType;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataMultiVarEntity extends TestdataObject {

    public static PlanningEntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataMultiVarSolution.buildSolutionDescriptor();
        return solutionDescriptor.getPlanningEntityDescriptor(TestdataMultiVarEntity.class);
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
    @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "valueList")
    public TestdataValue getPrimaryValue() {
        return primaryValue;
    }

    public void setPrimaryValue(TestdataValue primaryValue) {
        this.primaryValue = primaryValue;
    }

    @PlanningVariable
    @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "valueList")
    public TestdataValue getSecondaryValue() {
        return secondaryValue;
    }

    public void setSecondaryValue(TestdataValue secondaryValue) {
        this.secondaryValue = secondaryValue;
    }

    @PlanningVariable(nullable = true)
    @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "otherValueList")
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
