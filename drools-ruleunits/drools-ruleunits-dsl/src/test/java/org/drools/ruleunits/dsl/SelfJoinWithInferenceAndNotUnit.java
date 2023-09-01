package org.drools.ruleunits.dsl;

import org.drools.model.functions.Function1;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;

import static org.drools.model.Index.ConstraintType.EQUAL;
import static org.drools.model.Index.ConstraintType.NOT_EQUAL;

public class SelfJoinWithInferenceAndNotUnit implements RuleUnitDefinition {

    private final DataStore<String> strings;
    private final DataStore<String> alerts;

    public SelfJoinWithInferenceAndNotUnit() {
        this(DataSource.createStore(), DataSource.createStore());
    }

    public SelfJoinWithInferenceAndNotUnit(DataStore<String> strings, DataStore<String> alerts) {
        this.strings = strings;
        this.alerts = alerts;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        rulesFactory.rule("Self join")
                .on(strings)
                .on(strings)
                .filter(NOT_EQUAL, Function1.identity())
                .filter(s -> s.substring(0,1), EQUAL, s -> s.substring(0,1))
                .not( rule -> rule.on(alerts) )
                .execute(alerts, (a, s1, s2) -> a.add("Found String with same initial '" + s1 + "' and '" + s2 + "'"));
    }
}
