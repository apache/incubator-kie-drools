/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scenariosimulation.api.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FactMappingValueTest {

    @Test
    public void emptyFactMappingValue() {
        assertThatThrownBy(() -> new FactMappingValue(null, ExpressionIdentifier.DESCRIPTION, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("FactIdentifier has to be not null");

        assertThatThrownBy(() -> new FactMappingValue(FactIdentifier.DESCRIPTION, null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ExpressionIdentifier has to be not null");
    }
}