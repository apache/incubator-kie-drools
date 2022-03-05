/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.variable.descriptor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testdata.domain.nullable.TestdataNullableEntity;

class GenuineVariableDescriptorTest {

    @Test
    void isReinitializable() {
        GenuineVariableDescriptor<?> variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        assertThat(variableDescriptor.isReinitializable(new TestdataEntity("a", new TestdataValue()))).isFalse();
        assertThat(variableDescriptor.isReinitializable(new TestdataEntity("b", null))).isTrue();
    }

    @Test
    void isReinitializable_nullable() {
        GenuineVariableDescriptor<?> variableDescriptor = TestdataNullableEntity.buildVariableDescriptorForValue();
        assertThat(variableDescriptor.isReinitializable(new TestdataNullableEntity("a", new TestdataValue()))).isFalse();
        assertThat(variableDescriptor.isReinitializable(new TestdataNullableEntity("b", null))).isTrue();
    }

    @Test
    void isReinitializable_list() {
        GenuineVariableDescriptor<?> variableDescriptor = TestdataListEntity.buildVariableDescriptorForValueList();
        assertThat(variableDescriptor.isReinitializable(new TestdataListEntity("a", new TestdataListValue()))).isFalse();
        assertThat(variableDescriptor.isReinitializable(new TestdataListEntity("b", new ArrayList<>()))).isFalse();
    }
}
