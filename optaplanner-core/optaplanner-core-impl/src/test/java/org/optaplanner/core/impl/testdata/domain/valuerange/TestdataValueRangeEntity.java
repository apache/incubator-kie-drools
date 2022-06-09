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
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataValueRangeEntity extends TestdataObject {

    public static EntityDescriptor<TestdataValueRangeSolution> buildEntityDescriptor() {
        return TestdataValueRangeSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataValueRangeEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataValueRangeSolution> buildVariableDescriptorForValue() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("value");
    }

    private Integer integerValue;
    private Long longValue;
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

    @PlanningVariable(valueRangeProviderRefs = "integerValueRange")
    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    @PlanningVariable(valueRangeProviderRefs = "longValueRange")
    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
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
