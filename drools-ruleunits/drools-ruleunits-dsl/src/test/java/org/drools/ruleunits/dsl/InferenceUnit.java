package org.drools.ruleunits.dsl;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.dsl.domain.Person;

import static org.drools.model.Index.ConstraintType.EQUAL;
import static org.drools.model.Index.ConstraintType.GREATER_OR_EQUAL;
import static org.drools.model.Index.ConstraintType.GREATER_THAN;

public class InferenceUnit implements RuleUnitDefinition {

    private final DataStore<String> strings;
    private final DataStore<Person> persons;

    public InferenceUnit() {
        this(DataSource.createStore(), DataSource.createStore());
    }

    public InferenceUnit(DataStore<String> strings, DataStore<Person> persons) {
        this.strings = strings;
        this.persons = persons;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    public DataStore<Person> getPersons() {
        return persons;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        rulesFactory.rule()
                .on(strings)
                .filter(s -> s.length(), GREATER_THAN, 4)
                .executeOnDataStore(persons, (ps, s) -> ps.add(new Person(s, 17)));

        rulesFactory.rule()
                .on(persons)
                .filter("name", p -> p.getName() , EQUAL, "Mario")
                // executeOnDataStore is required when using methods specific of a DataStore like update or addLogical
                .executeOnDataStore(persons, (ps, p) -> {
                    p.setAge(p.getAge()+1);
                    ps.update(p, "age"); // property reactivity update
                });

        rulesFactory.rule()
                .on(persons)
                .filter("age", p -> p.getAge() , GREATER_OR_EQUAL, 18)
                .executeOnDataStore(strings, s -> s.add("ok"));
    }
}
