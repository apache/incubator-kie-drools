package org.kie.kogito.rules;

import org.drools.core.ruleunit.impl.ListDataStore;

public class HelloWorld implements RuleUnitData {

    private final DataStore<String> strings = new ListDataStore<>();

    public DataStore<String> getStrings() {
        return strings;
    }
}
