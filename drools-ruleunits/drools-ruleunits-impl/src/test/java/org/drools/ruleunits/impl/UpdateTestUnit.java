package org.drools.ruleunits.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.impl.domain.Person;

public class UpdateTestUnit implements RuleUnitData {
    private final List<String> results = new ArrayList<>();
    private final DataStore<Person> persons;

    public UpdateTestUnit() {
        this(DataSource.createStore());
    }

    public UpdateTestUnit(DataStore<Person> persons) {
        this.persons = persons;
    }

    public DataStore<Person> getPersons() {
        return persons;
    }

    public List<String> getResults() {
        return results;
    }
}
