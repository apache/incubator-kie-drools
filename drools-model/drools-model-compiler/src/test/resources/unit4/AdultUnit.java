package org.unit4;

import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.DataSource;

import org.model.*;

public class AdultUnit implements RuleUnit {
    private DataSource<Person> persons;

    public AdultUnit( ) { }

    public AdultUnit( DataSource<Person> persons ) {
        this.persons = persons;
    }

    public DataSource<Person> getPersons() {
        return persons;
    }

    rule ParentOfHasManyToys {
        Child c = /persons#Child[toysNr > 5];
        Person p = /persons[age < c.age || name.length > c.name.length];
        do {
            System.out.println(c.getName());
        }
    }
}