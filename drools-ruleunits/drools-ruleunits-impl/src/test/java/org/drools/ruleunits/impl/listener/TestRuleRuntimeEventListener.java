package org.drools.ruleunits.impl.listener;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectInsertedEvent;

public class TestRuleRuntimeEventListener extends DefaultRuleRuntimeEventListener {

    private List<String> results = new ArrayList<>();

    public List<String> getResults() {
        return results;
    }

    public void objectInserted(ObjectInsertedEvent event) {
        results.add("objectInserted : " + event.getObject().toString());
    }
}
