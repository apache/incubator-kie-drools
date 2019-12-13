package org.kie.kogito.rules;

import org.kie.kogito.rules.units.ListDataStore;

public class HelloWorld implements RuleUnitData {

    private final DataStore<String> strings = new ListDataStore<>();

    public DataStore<String> getStrings() {
        return strings;
    }
}
