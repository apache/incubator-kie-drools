package org.drools.ruleunits.dsl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.impl.NamedRuleUnitData;

import static org.drools.model.Index.ConstraintType.EQUAL;

public class NamedHelloWorldUnit implements RuleUnitDefinition,
                                            NamedRuleUnitData {

    private final DataStore<String> strings;
    private final List<String> results = new ArrayList<>();

    private final String expectedMessage;

    public NamedHelloWorldUnit(String expectedMessage) {
        this.strings = DataSource.createStore();
        this.expectedMessage = expectedMessage;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    public List<String> getResults() {
        return results;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        // /strings[ this == "Hello World" ]
        rulesFactory.rule()
                .on(strings)
                .filter(EQUAL, expectedMessage) // when no extractor is provided "this" is implicit
                .execute(results, r -> r.add("it worked!")); // the consequence can ignore the matched facts
    }

    @Override
    public String getUnitName() {
        return getClass().getCanonicalName() + "#" + expectedMessage;
    }
}
