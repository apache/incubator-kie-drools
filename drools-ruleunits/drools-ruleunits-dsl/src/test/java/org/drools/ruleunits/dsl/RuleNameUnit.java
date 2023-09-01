package org.drools.ruleunits.dsl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;

public class RuleNameUnit implements RuleUnitDefinition {

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

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        // /strings
        rulesFactory.rule("HelloWorld")
                    .on(strings)
                    .execute(results, r -> r.add("HelloWorld"));

        // /strings
        rulesFactory.rule("GoodByeWorld")
                    .on(strings)
                    .execute(results, r -> r.add("GoodByeWorld"));
    }
}
