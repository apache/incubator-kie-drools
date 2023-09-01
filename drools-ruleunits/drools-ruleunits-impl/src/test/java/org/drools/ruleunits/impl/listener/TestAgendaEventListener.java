package org.drools.ruleunits.impl.listener;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCreatedEvent;

public class TestAgendaEventListener extends DefaultAgendaEventListener {

    private List<String> results = new ArrayList<>();

    public List<String> getResults() {
        return results;
    }

    @Override
    public void matchCreated(MatchCreatedEvent event) {
        results.add("matchCreated : " + event.getMatch().getRule().getName());
    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        results.add("beforeMatchFired : " + event.getMatch().getRule().getName());
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        results.add("afterMatchFired : " + event.getMatch().getRule().getName());
    }

}
