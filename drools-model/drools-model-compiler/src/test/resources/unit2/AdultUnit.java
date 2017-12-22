package org.unit2;

import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.DataSource;

import org.model.*;

public class AdultUnit implements RuleUnit {
    private int adultAge = 0;
    private DataSource<Person> persons;

    public AdultUnit( ) { }

    public AdultUnit( DataSource<Person> persons ) {
        this.persons = persons;
    }

    public DataSource<Person> getPersons() {
        return persons;
    }

    rule HasManyToys {
        Child c = /persons#Child[toysNr > 5];
        do {
            System.out.println(c.getName());
        }
    }
}