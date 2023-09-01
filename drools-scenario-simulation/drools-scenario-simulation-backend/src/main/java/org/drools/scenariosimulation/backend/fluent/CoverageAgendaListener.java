package org.drools.scenariosimulation.backend.fluent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.internal.definition.rule.InternalRule;

import static org.drools.scenariosimulation.backend.fluent.RuleScenarioExecutableBuilder.prettyFullyQualifiedName;

public class CoverageAgendaListener extends DefaultAgendaEventListener {

    protected Map<String, Integer> ruleExecuted = new HashMap<>();
    protected List<String> auditsMessages = new ArrayList<>();

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent beforeMatchFiredEvent) {
        InternalRule rule = (InternalRule) beforeMatchFiredEvent.getMatch().getRule();
        String ruleKey = prettyFullyQualifiedName(rule);
        ruleExecuted.compute(ruleKey, (r, counter) -> counter == null ? 1 : counter + 1);
        auditsMessages.add(ruleKey);
    }

    public Map<String, Integer> getRuleExecuted() {
        return Collections.unmodifiableMap(ruleExecuted);
    }

    public List<String> getAuditsMessages() {
        return Collections.unmodifiableList(auditsMessages);
    }
}
