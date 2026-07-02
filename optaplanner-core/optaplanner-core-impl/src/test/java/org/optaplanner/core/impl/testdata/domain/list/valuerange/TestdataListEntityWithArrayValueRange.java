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

package org.optaplanner.core.impl.testdata.domain.list.valuerange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataListEntityWithArrayValueRange extends TestdataObject {

    public static EntityDescriptor<TestdataListSolutionWithArrayValueRange> buildEntityDescriptor() {
        return TestdataListSolutionWithArrayValueRange.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataListEntityWithArrayValueRange.class);
    }

    public static GenuineVariableDescriptor<TestdataListSolutionWithArrayValueRange> buildVariableDescriptorForValueList() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("valueList");
    }

    @PlanningListVariable(valueRangeProviderRefs = "arrayValueRange")
    private final List<TestdataValue> valueList;

    public TestdataListEntityWithArrayValueRange(String code, List<TestdataValue> valueList) {
        super(code);
        this.valueList = valueList;
    }

    public TestdataListEntityWithArrayValueRange(String code, TestdataValue... values) {
        this(code, new ArrayList<>(Arrays.asList(values)));
    }

    public List<TestdataValue> getValueList() {
        return valueList;
    }
}
