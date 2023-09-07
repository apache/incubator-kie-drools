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

package org.optaplanner.core.impl.testdata.domain.list.mixed;

import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataMixedVariablesEntity extends TestdataObject {

    public static EntityDescriptor<TestdataMixedVariablesSolution> buildEntityDescriptor() {
        return TestdataMixedVariablesSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataMixedVariablesEntity.class);
    }

    public static ListVariableDescriptor<TestdataMixedVariablesSolution> buildVariableDescriptorForValueList() {
        return (ListVariableDescriptor<TestdataMixedVariablesSolution>) buildEntityDescriptor()
                .getGenuineVariableDescriptor("valueList");
    }

    @PlanningListVariable(valueRangeProviderRefs = "valueRange")
    private final List<TestdataValue> valueList;
    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    private TestdataValue value;

    public TestdataMixedVariablesEntity(String code, List<TestdataValue> valueList, TestdataValue value) {
        super(code);
        this.valueList = valueList;
        this.value = value;
    }

    public List<TestdataValue> getValueList() {
        return valueList;
    }

    public TestdataValue getValue() {
        return value;
    }
}
