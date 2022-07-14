package org.drools.ruleunits.dsl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.SingletonStore;

import static org.drools.model.Index.ConstraintType.EQUAL;
import static org.drools.model.Index.ConstraintType.GREATER_THAN;
import static org.drools.ruleunits.dsl.Accumulators.avg;
import static org.drools.ruleunits.dsl.Accumulators.sum;

public class AccumulateUnit implements RuleUnitDefinition {

    private final DataStore<String> strings;
    private final SingletonStore<Integer> threshold;
    private final List<String> results = new ArrayList<>();


    public AccumulateUnit() {
        this(DataSource.createStore(), DataSource.createSingleton());
    }

    public AccumulateUnit(DataStore<String> strings, SingletonStore<Integer> threshold) {
        this.strings = strings;
        this.threshold = threshold;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    public List<String> getResults() {
        return results;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        RuleFactory ruleFactory1 = rulesFactory.addRule();
        ruleFactory1.accumulate(
                ruleFactory1.from(strings).filter(s -> s.substring(0, 1), EQUAL, "A"),
                sum(String::length)
            )
            .execute(results, (r, sum) -> r.add("Sum of length of Strings starting with A is " + sum));

        RuleFactory ruleFactory2 = rulesFactory.addRule();
        ruleFactory2.from(threshold)
                .join( ruleFactory2.accumulate(
                                ruleFactory2.from(strings).filter(s -> s.substring(0, 1), EQUAL, "A"),
                                avg(String::length)
                     )).filter(GREATER_THAN, Integer::doubleValue)
                .execute(results, (r, t, avg) -> r.add("Average length of Strings longer than threshold " + t + " is " + avg));
    }
}
