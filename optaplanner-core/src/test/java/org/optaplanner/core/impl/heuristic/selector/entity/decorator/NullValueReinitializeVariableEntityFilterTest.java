/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.nullable.TestdataNullableEntity;

public class NullValueReinitializeVariableEntityFilterTest {

    @Test
    public void accept() {
        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        NullValueReinitializeVariableEntityFilter filter = new NullValueReinitializeVariableEntityFilter(variableDescriptor);
        assertThat(filter.accept(null, new TestdataEntity("a", new TestdataValue()))).isFalse();
        assertThat(filter.accept(null, new TestdataEntity("b", null))).isTrue();
    }

    @Test
    public void acceptWithNullableEntity() {
        EntityDescriptor entityDescriptor = TestdataNullableEntity.buildEntityDescriptor();
        GenuineVariableDescriptor variableDescriptor = entityDescriptor.getGenuineVariableDescriptor("value");
        NullValueReinitializeVariableEntityFilter filter = new NullValueReinitializeVariableEntityFilter(variableDescriptor);
        assertThat(filter.accept(null, new TestdataNullableEntity("a", new TestdataValue()))).isFalse();
        assertThat(filter.accept(null, new TestdataNullableEntity("b", null))).isTrue();
    }

}
