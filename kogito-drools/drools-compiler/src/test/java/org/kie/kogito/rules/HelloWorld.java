package org.kie.kogito.rules;

import org.drools.core.ruleunit.impl.ListDataStore;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnitMemory;

public class HelloWorld implements RuleUnitMemory {

    private final DataStore<String> strings = new ListDataStore<>();

    public DataStore<String> getStrings() {
        return strings;
    }
}
