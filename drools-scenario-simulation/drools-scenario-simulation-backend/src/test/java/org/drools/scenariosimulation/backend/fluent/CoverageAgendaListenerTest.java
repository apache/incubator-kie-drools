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