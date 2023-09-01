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
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.ActionSuperType;
import org.drools.verifier.core.index.model.Column;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ActionTest {

    private Action action;

    @BeforeEach
    public void setUp() throws Exception {
        action = new Action(mock(Column.class),
                            ActionSuperType.FIELD_ACTION,
                            new Values<>(true),
                            new AnalyzerConfigurationMock()) {
        };
    }

    @Test
    void valueSet() throws Exception {
        assertThat(action.getValues()).hasSize(1).containsExactly(true);
    }

    @Test
    void changeValue() throws Exception {
        action.setValue(new Values<>(false));

        assertThat(action.getValues()).hasSize(1).containsExactly(false);
    }
}