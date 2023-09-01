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
package org.drools.scenariosimulation.backend.fluent;


import org.junit.Test;
import org.kie.api.event.rule.BeforeMatchFiredEvent;

import static org.assertj.core.api.Assertions.assertThat;

public class CoverageAgendaListenerTest extends AbstractRuleCoverageTest {

    private final static String RULE_NAME = "rule1";

    
    @Test
    public void constructor() {
        CoverageAgendaListener coverageAgendaListener = new CoverageAgendaListener();

        assertThat(coverageAgendaListener.getRuleExecuted()).isEmpty();
        assertThat(coverageAgendaListener.getAuditsMessages()).isEmpty();
    }
    @Test
    public void beforeMatchFired() {
        CoverageAgendaListener coverageAgendaListener = new CoverageAgendaListener();
        
        BeforeMatchFiredEvent beforeMatchFiredEvent = createBeforeMatchFiredEventMock(RULE_NAME);
        
        coverageAgendaListener.beforeMatchFired(beforeMatchFiredEvent);
        
        assertThat(coverageAgendaListener.getRuleExecuted()).hasSize(1).containsEntry(RULE_NAME, 1);
        assertThat(coverageAgendaListener.getAuditsMessages()).hasSize(1).containsExactly(RULE_NAME);
    }
}