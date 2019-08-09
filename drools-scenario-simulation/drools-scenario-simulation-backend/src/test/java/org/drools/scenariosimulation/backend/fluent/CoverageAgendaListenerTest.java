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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CoverageAgendaListenerTest extends AbstractRuleCoverageTest {

    @Test
    public void beforeMatchFired() {
        Map<String, Integer> rulesToNumberOfTimes = new LinkedHashMap<>();
        rulesToNumberOfTimes.put("rule1", 2);
        rulesToNumberOfTimes.put("rule2", 2);
        rulesToNumberOfTimes.put("rule3", 1);

        CoverageAgendaListener coverageAgendaListener = createCoverageAgendaListenerWithData(rulesToNumberOfTimes);

        Map<String, Integer> ruleExecuted = coverageAgendaListener.getRuleExecuted();
        assertEquals((Integer) 2, ruleExecuted.get("rule1"));
        assertEquals((Integer) 2, ruleExecuted.get("rule2"));
        assertEquals((Integer) 1, ruleExecuted.get("rule3"));
        assertNull(ruleExecuted.get("rule4"));

        List<String> auditMessages = coverageAgendaListener.getAuditsMessages();
        assertEquals(auditMessages.get(0), coverageAgendaListener.generateAuditMessage("rule1", 1));
        assertEquals(auditMessages.get(1), coverageAgendaListener.generateAuditMessage("rule1", 2));
        assertEquals(auditMessages.get(2), coverageAgendaListener.generateAuditMessage("rule2", 1));
        assertEquals(auditMessages.get(3), coverageAgendaListener.generateAuditMessage("rule2", 2));
        assertEquals(auditMessages.get(4), coverageAgendaListener.generateAuditMessage("rule3", 1));
        assertTrue(auditMessages.size() == 5);
    }
}