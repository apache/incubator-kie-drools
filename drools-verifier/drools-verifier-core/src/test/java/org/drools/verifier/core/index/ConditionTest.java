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
package org.drools.verifier.core.index;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.ConditionSuperType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ConditionTest {

    private Condition<Integer> condition;

    @BeforeEach
    public void setUp() throws Exception {
        condition = new Condition<>(mock(Column.class),
                                  ConditionSuperType.FIELD_CONDITION,
                                  new Values<>(1),
                                  new AnalyzerConfigurationMock()) {

        };
    }

    @Test
    void valueSet() throws Exception {
        assertThat(condition.getValues()).hasSize(1).containsExactly(1);
    }

    @Test
    void changeValue() throws Exception {
        condition.setValue(new Values<>(2));

        assertThat(condition.getValues()).hasSize(1).containsExactly(2);
    }
}