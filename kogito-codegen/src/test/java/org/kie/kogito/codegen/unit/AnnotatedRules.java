package org.kie.kogito.codegen.unit;

import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnitData;
import org.kie.kogito.rules.units.annotations.When;

public class AnnotatedRules implements RuleUnitData {

    private final DataStore<Person> persons = DataSource.createStore();

    public void adult(@When("/persons[ age >= 18 ]") Person person) {
        System.out.println(person);
    }

    public DataStore<Person> getPersons() {
        return persons;
    }

    public AnnotatedRules getUnit() {
        return this;
    }
}
