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

import java.util.Map;
import java.util.stream.IntStream;

import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.definition.rule.InternalRule;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractRuleCoverageTest {

    protected CoverageAgendaListener createCoverageAgendaListenerWithData(Map<String, Integer> rulesToNumberOfTimes) {
        CoverageAgendaListener coverageAgendaListener = new CoverageAgendaListener();
        for (Map.Entry<String, Integer> ruleToNumberOfTimes : rulesToNumberOfTimes.entrySet()) {
            BeforeMatchFiredEvent beforeMatchFiredEventMock = createBeforeMatchFiredEventMock(ruleToNumberOfTimes.getKey());
            IntStream.range(0, ruleToNumberOfTimes.getValue()).forEach(i -> coverageAgendaListener.beforeMatchFired(beforeMatchFiredEventMock));
        }
        return coverageAgendaListener;
    }

    protected BeforeMatchFiredEvent createBeforeMatchFiredEventMock(String ruleName) {
        BeforeMatchFiredEvent eventMock = mock(BeforeMatchFiredEvent.class);
        Match matchMock = mock(Match.class);
        InternalRule ruleMock = mock(InternalRule.class);
        when(ruleMock.getName()).thenReturn(ruleName);
        when(ruleMock.getPackageName()).thenReturn("");
        when(matchMock.getRule()).thenReturn(ruleMock);
        when(eventMock.getMatch()).thenReturn(matchMock);
        return eventMock;
    }
}