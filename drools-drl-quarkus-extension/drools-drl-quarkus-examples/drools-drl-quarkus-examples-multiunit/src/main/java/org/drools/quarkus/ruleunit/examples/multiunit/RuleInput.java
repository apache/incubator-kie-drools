package org.drools.quarkus.ruleunit.examples.multiunit;

import org.drools.ruleunits.api.RuleUnitData;

public class RuleInput implements RuleUnitData {

    private final String text;

    public RuleInput(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
