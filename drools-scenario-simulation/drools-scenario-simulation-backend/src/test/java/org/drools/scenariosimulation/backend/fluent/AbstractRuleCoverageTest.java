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