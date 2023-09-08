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
package org.drools.scenariosimulation.backend.runner.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueWrapperTest {

    @Test
    public void orElse() {
        assertThat(ValueWrapper.of(1).orElse(3)).isEqualTo((Integer) 1);
        assertThat(ValueWrapper.errorWithValidValue(null, null).orElse(3)).isEqualTo(3);
        assertThat(ValueWrapper.of(null).orElse(3)).isNull();
    }

    @Test
    public void orElseGet() {
        assertThat(ValueWrapper.of(1).orElseGet(() -> 3)).isEqualTo((Integer) 1);
        assertThat(ValueWrapper.errorWithValidValue(null, null).orElseGet(() -> 3)).isEqualTo(3);
        assertThat(ValueWrapper.of(null).orElseGet(() -> 3)).isNull();
    }
}