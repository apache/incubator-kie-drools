package org.drools.ruleunits.dsl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;

import static org.drools.model.Index.ConstraintType.GREATER_THAN;

public class UnitTwo implements RuleUnitDefinition {

    private final DataStore<Integer> ints;
    private final List<String> results = new ArrayList<>();

    public UnitTwo() {
        this(DataSource.createStore());
    }

    public UnitTwo(DataStore<Integer> ints) {
        this.ints = ints;
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
                    .on(ints)
                    .filter(GREATER_THAN, 5)
                    .execute(results, (r, i) -> r.add("Found " + i));


    }
}
