package org.drools.ruleunits.dsl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;

import static org.drools.model.functions.Function1.identity;
import static org.drools.ruleunits.dsl.Accumulators.sum;

public class SumAccumulateUnit implements RuleUnitDefinition {
    private final DataStore<Integer> integers;
    private List<Integer> results = new ArrayList<>();

    public SumAccumulateUnit() {
        this(DataSource.createStore());
    }

    public SumAccumulateUnit(DataStore<Integer> integers) {
        this.integers = integers;
    }

    public DataStore<Integer> getIntegers() {
        return integers;
    }

    public List<Integer> getResults() {
        return results;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        rulesFactory.rule("Sum of Integers")
                .accumulate( rule -> rule.on(integers), sum(identity()) )
                .execute(results, List::add);
    }
}
