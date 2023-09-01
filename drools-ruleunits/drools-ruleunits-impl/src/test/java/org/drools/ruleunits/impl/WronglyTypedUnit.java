package org.drools.ruleunits.impl;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

public class WronglyTypedUnit implements RuleUnitData  {

    private final DataStore<Integer> strings;

    public WronglyTypedUnit() {
        this.strings = DataSource.createStore();
    }

    public DataStore<Integer> getStrings() {
        return strings;
    }
}
