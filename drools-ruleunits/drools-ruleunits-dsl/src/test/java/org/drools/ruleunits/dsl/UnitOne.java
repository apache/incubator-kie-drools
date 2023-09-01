package org.drools.ruleunits.dsl;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;

import static org.drools.model.Index.ConstraintType.GREATER_THAN;

public class UnitOne implements RuleUnitDefinition {

    private final DataStore<String> strings;
    private final DataStore<Integer> ints;

    public UnitOne() {
        this(DataSource.createStore(), DataSource.createStore());
    }

    public UnitOne(DataStore<String> strings, DataStore<Integer> ints) {
        this.strings = strings;
        this.ints = ints;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    public DataStore<Integer> getInts() {
        return ints;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        rulesFactory.rule()
                    .on(strings)
                    .filter("length", String::length, GREATER_THAN, 5)
                    .execute(ints, (i, s) -> i.add(s.length()));
    }
}
