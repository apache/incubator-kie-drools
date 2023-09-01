package org.drools.ruleunits.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

public class OOPathTestUnit implements RuleUnitData {
    private final DataStore<String> strings;
    private final DataStore<Integer> ints;
    private final List<String> results = new ArrayList<>();

    public OOPathTestUnit() {
        this(DataSource.createStore(), DataSource.createStore());
    }

    public OOPathTestUnit(DataStore<String> strings, DataStore<Integer> ints) {
        this.strings = strings;
        this.ints = ints;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    public DataStore<Integer> getInts() {
        return ints;
    }

    public List<String> getResults() {
        return results;
    }
}
