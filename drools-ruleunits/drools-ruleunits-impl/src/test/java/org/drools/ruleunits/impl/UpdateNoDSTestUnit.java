package org.drools.ruleunits.impl;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.impl.domain.Person;

import java.util.ArrayList;
import java.util.List;

public class UpdateNoDSTestUnit implements RuleUnitData {
    private final List<String> results = new ArrayList<>();
    private final DataStore<Person> persons;

    public UpdateNoDSTestUnit() {
        this(DataSource.createStore());
    }

    public UpdateNoDSTestUnit(DataStore<Person> persons) {
        this.persons = persons;
    }

    public DataStore<Person> getPersons() {
        return persons;
    }

    public List<String> getResults() {
        return results;
    }
}
