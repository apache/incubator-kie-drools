package org.drools.ruleunits.dsl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;

import static org.drools.model.Index.ConstraintType.EQUAL;

public class SelfJoinUnit implements RuleUnitDefinition {

    private final DataStore<String> strings;
    private final List<String> results = new ArrayList<>();

    public SelfJoinUnit() {
        this(DataSource.createStore());
    }

    public SelfJoinUnit(DataStore<String> strings) {
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
        rulesFactory.rule("Self join")
                .on(strings)
                .on(strings)
                .filter(s -> s.substring(0,1), EQUAL, s -> s.substring(1,2))
                .execute(results, (r, s1, s2) -> r.add("Found '" + s1 + "' and '" + s2 + "'"));
    }
}
