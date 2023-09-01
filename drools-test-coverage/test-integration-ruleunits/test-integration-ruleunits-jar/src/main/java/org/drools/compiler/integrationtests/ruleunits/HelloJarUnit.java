package org.drools.compiler.integrationtests.ruleunits;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

public class HelloJarUnit implements RuleUnitData {
    private final DataStore<String> strings;
    private final List<String> results = new ArrayList<>();

    public HelloJarUnit() {
        this(DataSource.createStore());
    }

    public HelloJarUnit(DataStore<String> strings) {
        this.strings = strings;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    public List<String> getResults() {
        return results;
    }
}
