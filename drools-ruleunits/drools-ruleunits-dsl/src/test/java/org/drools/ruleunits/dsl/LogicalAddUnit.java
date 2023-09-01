package org.drools.ruleunits.dsl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;

public class LogicalAddUnit implements RuleUnitDefinition {

    private final DataStore<String> strings;
    private final DataStore<Integer> ints;
    private final List<String> results = new ArrayList<>();

    public LogicalAddUnit() {
        this(DataSource.createStore(), DataSource.createStore());
    }

    public LogicalAddUnit(DataStore<String> strings, DataStore<Integer> ints) {
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

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        rulesFactory.rule()
                    .on(strings)
                    // executeOnDataStore is required when using methods specific of a DataStore like update or addLogical
                    .executeOnDataStore(ints, (i, s) -> i.addLogical(s.length()));

        rulesFactory.rule()
                .exists( rule -> rule.on(ints) )
                .execute( results, r -> r.add("exists") );

        rulesFactory.rule()
                .not( rule -> rule.on(ints) )
                .execute( results, r -> r.add("not exists") );

    }
}
