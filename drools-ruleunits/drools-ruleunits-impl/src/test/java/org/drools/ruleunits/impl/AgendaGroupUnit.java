package org.drools.ruleunits.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

public class AgendaGroupUnit implements RuleUnitData {
    private final DataStore<String> strings;
    private final List<String> results = new ArrayList<>();

    public AgendaGroupUnit() {
        this(DataSource.createStore());
    }

    public AgendaGroupUnit(DataStore<String> strings) {
        this.strings = strings;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    public List<String> getResults() {
        return results;
    }
}
