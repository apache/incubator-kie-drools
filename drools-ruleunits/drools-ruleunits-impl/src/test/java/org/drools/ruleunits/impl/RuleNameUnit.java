package org.drools.ruleunits.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

public class RuleNameUnit implements RuleUnitData {
    private final DataStore<String> strings;
    private final List<String> results = new ArrayList<>();

    public RuleNameUnit() {
        this(DataSource.createStore());
    }

    public RuleNameUnit(DataStore<String> strings) {
        this.strings = strings;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    public List<String> getResults() {
        return results;
    }
}
