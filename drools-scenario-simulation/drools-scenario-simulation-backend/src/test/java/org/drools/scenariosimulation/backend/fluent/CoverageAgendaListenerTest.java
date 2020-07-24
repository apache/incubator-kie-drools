/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.scenariosimulation.backend.fluent;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kie.api.event.rule.BeforeMatchFiredEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CoverageAgendaListenerTest extends AbstractRuleCoverageTest {

    private final static String RULE_NAME = "rule1";

    @Test
    public void beforeMatchFired() {
        CoverageAgendaListener coverageAgendaListener = new CoverageAgendaListener();
        assertTrue(coverageAgendaListener.getRuleExecuted().isEmpty());
        assertTrue(coverageAgendaListener.getAuditsMessages().isEmpty());
        BeforeMatchFiredEvent beforeMatchFiredEvent = createBeforeMatchFiredEventMock(RULE_NAME);
        coverageAgendaListener.beforeMatchFired(beforeMatchFiredEvent);
        Map<String, Integer> ruleExecuted = coverageAgendaListener.getRuleExecuted();
        assertEquals(1, ruleExecuted.size());
        assertEquals((Integer) 1, ruleExecuted.get(RULE_NAME));
        List<String> auditMessages = coverageAgendaListener.getAuditsMessages();
        assertEquals(1, auditMessages.size());
        assertEquals(RULE_NAME, auditMessages.get(0));
    }
}