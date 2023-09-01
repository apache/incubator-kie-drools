package org.drools.ruleunits.impl;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

public class NotTestUnit implements RuleUnitData {
    private final DataStore<String> strings;

    public NotTestUnit() {
        this(DataSource.createStore());
    }

    public NotTestUnit(DataStore<String> strings) {
        this.strings = strings;
    }

    public DataStore<String> getStrings() {
        return strings;
    }
}
