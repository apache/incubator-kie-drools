/**
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
package org.drools.core.base;

import org.drools.base.base.ValueType;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueTypeTest {

    @Test
    public void testIsBoolean() {
        assertThat(ValueType.BOOLEAN_TYPE.isBoolean()).isTrue();
        assertThat(ValueType.PBOOLEAN_TYPE.isBoolean()).isTrue();
    }

    @Test
    public void testIsNumber() {
        assertThat(ValueType.PBYTE_TYPE.isNumber()).isTrue();
        assertThat(ValueType.PSHORT_TYPE.isNumber()).isTrue();
        assertThat(ValueType.PINTEGER_TYPE.isNumber()).isTrue();
        assertThat(ValueType.PLONG_TYPE.isNumber()).isTrue();
        assertThat(ValueType.PFLOAT_TYPE.isNumber()).isTrue();
        assertThat(ValueType.PDOUBLE_TYPE.isNumber()).isTrue();
        assertThat(ValueType.BYTE_TYPE.isNumber()).isTrue();
        assertThat(ValueType.SHORT_TYPE.isNumber()).isTrue();
        assertThat(ValueType.INTEGER_TYPE.isNumber()).isTrue();
        assertThat(ValueType.LONG_TYPE.isNumber()).isTrue();
        assertThat(ValueType.FLOAT_TYPE.isNumber()).isTrue();
        assertThat(ValueType.DOUBLE_TYPE.isNumber()).isTrue();

    }

}
