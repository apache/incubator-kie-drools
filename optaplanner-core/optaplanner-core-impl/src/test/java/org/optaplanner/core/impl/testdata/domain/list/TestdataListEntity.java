/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.testdata.domain.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataListEntity extends TestdataObject {

    public static EntityDescriptor<TestdataListSolution> buildEntityDescriptor() {
        return TestdataListSolution.buildSolutionDescriptor().findEntityDescriptorOrFail(TestdataListEntity.class);
    }

    public static ListVariableDescriptor<TestdataListSolution> buildVariableDescriptorForValueList() {
        return (ListVariableDescriptor<TestdataListSolution>) buildEntityDescriptor().getGenuineVariableDescriptor("valueList");
    }

    public static TestdataListEntity createWithValues(String code, TestdataListValue... values) {
        // Set up shadow variables to preserve consistency.
        return new TestdataListEntity(code, values).setUpShadowVariables();
    }

    TestdataListEntity setUpShadowVariables() {
        valueList.forEach(testdataListValue -> {
            testdataListValue.setEntity(this);
            testdataListValue.setIndex(valueList.indexOf(testdataListValue));
        });
        return this;
    }

    @PlanningListVariable(valueRangeProviderRefs = "valueRange")
    private List<TestdataListValue> valueList;

    public TestdataListEntity() {
    }

    public TestdataListEntity(String code, List<TestdataListValue> valueList) {
        super(code);
        this.valueList = valueList;
    }

    public TestdataListEntity(String code, TestdataListValue... values) {
        this(code, new ArrayList<>(Arrays.asList(values)));
    }

    public List<TestdataListValue> getValueList() {
        return valueList;
    }
}
