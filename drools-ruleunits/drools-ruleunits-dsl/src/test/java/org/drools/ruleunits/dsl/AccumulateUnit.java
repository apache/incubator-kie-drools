package org.drools.ruleunits.dsl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;

import static org.drools.model.Index.ConstraintType.EQUAL;
import static org.drools.ruleunits.dsl.Accumulators.sum;

public class AccumulateUnit implements RuleUnitDefinition {

    private final DataStore<String> strings;
    private final List<String> results = new ArrayList<>();

    public AccumulateUnit() {
        this(DataSource.createStore());
    }

    public AccumulateUnit(DataStore<String> strings) {
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
        RuleFactory accRuleFactory = rulesFactory.addRule();
        accRuleFactory.accumulate(
                accRuleFactory.from(strings).filter(s -> s.substring(0, 1), EQUAL, "A"),
                sum(String::length)
            )
            .execute(results, (r, sum) -> r.add("Sum of length of Strings starting with A is " + sum)); ;
    }
}
