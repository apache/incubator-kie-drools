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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CoverageAgendaListenerTest extends AbstractRuleCoverageTest {

    @Test
    public void beforeMatchFired() {
        Map<String, Integer> rulesToNumberOfTimes = new HashMap<>();
        rulesToNumberOfTimes.put("rule1", 2);
        rulesToNumberOfTimes.put("rule2", 2);
        rulesToNumberOfTimes.put("rule3", 1);

        CoverageAgendaListener coverageAgendaListener = createCoverageAgendaListenerWithData(rulesToNumberOfTimes);

        Map<String, Integer> ruleExecuted = coverageAgendaListener.getRuleExecuted();
        assertEquals((Integer) 2, ruleExecuted.get("rule1"));
        assertEquals((Integer) 2, ruleExecuted.get("rule2"));
        assertEquals((Integer) 1, ruleExecuted.get("rule3"));
        assertNull(ruleExecuted.get("rule4"));
    }
}