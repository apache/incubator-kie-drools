package org.drools.testcoverage.common.listener;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderListener extends DefaultAgendaEventListener {

    private List<String> rulesFired = new ArrayList<String>();

    @Override
    public void afterMatchFired(final AfterMatchFiredEvent event) {
        rulesFired.add(event.getMatch().getRule().getName());
    }

    public int size() {
        return rulesFired.size();
    }

    public String get(final int index) {
        return rulesFired.get(index);
    }
}