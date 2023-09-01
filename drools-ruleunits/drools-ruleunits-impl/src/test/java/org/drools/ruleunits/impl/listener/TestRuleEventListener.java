package org.drools.ruleunits.impl.listener;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.runtime.rule.Match;
import org.kie.internal.event.rule.RuleEventListener;

public class TestRuleEventListener implements RuleEventListener {

    private List<String> results = new ArrayList<>();

    public List<String> getResults() {
        return results;
    }

    public void onBeforeMatchFire(Match match) {
        results.add("onBeforeMatchFire : " + match.getRule().getName());
    }

    public void onAfterMatchFire(Match match) {
        results.add("onAfterMatchFire : " + match.getRule().getName());
    }
}
