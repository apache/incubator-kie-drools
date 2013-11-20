package org.optaplanner.core.api.score.holder;

import java.util.Collections;
import java.util.List;

import org.drools.core.common.AgendaItem;
import org.drools.core.common.AgendaItemImpl;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.kie.api.runtime.rule.RuleRuntime;

import static org.mockito.Mockito.*;

public abstract class AbstractScoreHolderTest {

    protected RuleContext createRuleContext(String ruleName) {
        RuleContext kcontext = mock(RuleContext.class);
        AgendaItem agendaItem = new AgendaItemImpl() {
            @Override
            public List<Object> getObjects() {
                return Collections.emptyList();
            }
        };
        when(kcontext.getMatch()).thenReturn(agendaItem);
        Rule rule = mock(Rule.class);
        when(rule.getPackageName()).thenReturn(getClass().getPackage().getName());
        when(rule.getName()).thenReturn(ruleName);
        when(kcontext.getRule()).thenReturn(rule);
        return kcontext;
    }

    protected void callUnMatch(RuleContext ruleContext) {
        AgendaItem agendaItem = (AgendaItem) ruleContext.getMatch();
        agendaItem.getActivationUnMatchListener().unMatch(mock(RuleRuntime.class), agendaItem);
    }

}
