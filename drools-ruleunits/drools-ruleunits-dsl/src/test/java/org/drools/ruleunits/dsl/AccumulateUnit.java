package org.drools.ruleunits.dsl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.SingletonStore;

import static org.drools.model.Index.ConstraintType.EQUAL;
import static org.drools.model.Index.ConstraintType.GREATER_THAN;
import static org.drools.model.Index.ConstraintType.NOT_EQUAL;
import static org.drools.model.functions.Function1.identity;
import static org.drools.ruleunits.dsl.Accumulators.avg;
import static org.drools.ruleunits.dsl.Accumulators.max;
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

    public SingletonStore<Integer> getThreshold() {
        return threshold;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        RuleFactory ruleFactory1 = rulesFactory.addRule();
        ruleFactory1.accumulate(
                ruleFactory1.from(strings).filter(s -> s.substring(0, 1), EQUAL, "A"),
                sum(String::length)
            )
            .execute(results, (r, sum) -> r.add("Sum of length of Strings starting with A is " + sum));

        rulesFactory.addRule()
                .from(strings).filter(s -> s.substring(0, 1), NOT_EQUAL, "A")
                .accumulate(max(String::length))
                .execute(results, (r, max) -> r.add("Max length of Strings not starting with A is " + max));

        rulesFactory.addRule().from(threshold)
                .join(strings)
                .filter(String::length, GREATER_THAN, identity())
                .accumulate(avg(String::length))
                .execute(results, (r, t, avg) -> r.add("Average length of Strings longer than threshold " + t + " is " + avg));
    }
}
