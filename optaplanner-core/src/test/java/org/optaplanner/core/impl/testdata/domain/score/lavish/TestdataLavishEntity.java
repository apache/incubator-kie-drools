/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.testdata.domain.score.lavish;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataLavishEntity extends TestdataObject {

    public static EntityDescriptor<TestdataLavishSolution> buildEntityDescriptor() {
        SolutionDescriptor<TestdataLavishSolution> solutionDescriptor = TestdataLavishSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataLavishEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataLavishSolution> buildVariableDescriptorForValue() {
        SolutionDescriptor<TestdataLavishSolution> solutionDescriptor = TestdataLavishSolution.buildSolutionDescriptor();
        EntityDescriptor<TestdataLavishSolution> entityDescriptor = solutionDescriptor.findEntityDescriptorOrFail(TestdataLavishEntity.class);
        return entityDescriptor.getGenuineVariableDescriptor("value");
    }

    private TestdataLavishEntityGroup entityGroup;
    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    private TestdataLavishValue value;

    private String stringProperty = "";
    private Integer integerProperty = 1;
    private Long longProperty = 1L;
    private BigInteger bigIntegerProperty = BigInteger.ONE;
    private BigDecimal bigDecimalProperty = BigDecimal.ONE;

    public TestdataLavishEntity() {
    }

    public TestdataLavishEntity(String code, TestdataLavishEntityGroup entityGroup) {
        this(code, entityGroup, null);
    }

    public TestdataLavishEntity(String code, TestdataLavishEntityGroup entityGroup, TestdataLavishValue value) {
        super(code);
        this.entityGroup = entityGroup;
        this.value = value;
    }

    // ************************************************************************
    // Getter/setters
    // ************************************************************************

    public TestdataLavishEntityGroup getEntityGroup() {
        return entityGroup;
    }

    public void setEntityGroup(TestdataLavishEntityGroup entityGroup) {
        this.entityGroup = entityGroup;
    }

    public TestdataLavishValue getValue() {
        return value;
    }

    public void setValue(TestdataLavishValue value) {
        this.value = value;
    }

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public Integer getIntegerProperty() {
        return integerProperty;
    }

    public void setIntegerProperty(Integer integerProperty) {
        this.integerProperty = integerProperty;
    }

    public Long getLongProperty() {
        return longProperty;
    }

    public void setLongProperty(Long longProperty) {
        this.longProperty = longProperty;
    }

    public BigInteger getBigIntegerProperty() {
        return bigIntegerProperty;
    }

    public void setBigIntegerProperty(BigInteger bigIntegerProperty) {
        this.bigIntegerProperty = bigIntegerProperty;
    }

    public BigDecimal getBigDecimalProperty() {
        return bigDecimalProperty;
    }

    public void setBigDecimalProperty(BigDecimal bigDecimalProperty) {
        this.bigDecimalProperty = bigDecimalProperty;
    }

}
