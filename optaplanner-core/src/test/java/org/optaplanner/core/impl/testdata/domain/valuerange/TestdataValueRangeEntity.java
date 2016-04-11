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

package org.optaplanner.core.impl.testdata.domain.valuerange;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataValueRangeEntity extends TestdataObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataValueRangeSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataValueRangeEntity.class);
    }

    public static GenuineVariableDescriptor buildVariableDescriptorForValue() {
        SolutionDescriptor solutionDescriptor = TestdataValueRangeSolution.buildSolutionDescriptor();
        EntityDescriptor entityDescriptor = solutionDescriptor.findEntityDescriptorOrFail(TestdataValueRangeEntity.class);
        return entityDescriptor.getGenuineVariableDescriptor("value");
    }

    private int intValue;
    private long longValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private LocalDate localDateValue;
    private LocalTime localTimeValue;
    private LocalDateTime localDateTimeValue;
    private Year yearValue;

    public TestdataValueRangeEntity() {
    }

    public TestdataValueRangeEntity(String code) {
        super(code);
    }

    @PlanningVariable(valueRangeProviderRefs = "intValueRange")
    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    @PlanningVariable(valueRangeProviderRefs = "longValueRange")
    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    @PlanningVariable(valueRangeProviderRefs = "bigIntegerValueRange")
    public BigInteger getBigIntegerValue() {
        return bigIntegerValue;
    }

    public void setBigIntegerValue(BigInteger bigIntegerValue) {
        this.bigIntegerValue = bigIntegerValue;
    }

    @PlanningVariable(valueRangeProviderRefs = "bigDecimalValueRange")
    public BigDecimal getBigDecimalValue() {
        return bigDecimalValue;
    }

    public void setBigDecimalValue(BigDecimal bigDecimalValue) {
        this.bigDecimalValue = bigDecimalValue;
    }

    @PlanningVariable(valueRangeProviderRefs = "localDateValueRange")
    public LocalDate getLocalDateValue() {
        return localDateValue;
    }

    public void setLocalDateValue(LocalDate localDateValue) {
        this.localDateValue = localDateValue;
    }

    @PlanningVariable(valueRangeProviderRefs = "localTimeValueRange")
    public LocalTime getLocalTimeValue() {
        return localTimeValue;
    }

    public void setLocalTimeValue(LocalTime localTimeValue) {
        this.localTimeValue = localTimeValue;
    }

    @PlanningVariable(valueRangeProviderRefs = "localDateTimeValueRange")
    public LocalDateTime getLocalDateTimeValue() {
        return localDateTimeValue;
    }

    public void setLocalDateTimeValue(LocalDateTime localDateTimeValue) {
        this.localDateTimeValue = localDateTimeValue;
    }

    @PlanningVariable(valueRangeProviderRefs = "yearValueRange")
    public Year getYearValue() {
        return yearValue;
    }

    public void setYearValue(Year yearValue) {
        this.yearValue = yearValue;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
