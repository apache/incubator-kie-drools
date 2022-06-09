package org.drools.ruleunits.dsl;

import java.util.Arrays;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;

import static org.drools.model.Index.ConstraintType.EQUAL;

public class AccumulateUnit implements RuleUnitDefinition {

    private final DataStore<String> strings;
    private final DataStore<Integer> ints;

    public AccumulateUnit() {
        this(DataSource.createStore(), DataSource.createStore());
    }

    public AccumulateUnit(DataStore<String> strings, DataStore<Integer> ints) {
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
        rulesFactory.addRule()
                .from(strings)
                .filter(s -> s.substring(0, 1), EQUAL, "A")

                ;
    }

    public static void main(String[] args) {
        List<String> strings = Arrays.asList("A1", "A123", "B12", "ABCDEF");

        int result = strings.stream().filter(s -> s.substring(0,1).equals("A")).reduce(0, (a,s) -> a+s.length(), (a,b) -> a+b);
        System.out.println(result);
    }
}
