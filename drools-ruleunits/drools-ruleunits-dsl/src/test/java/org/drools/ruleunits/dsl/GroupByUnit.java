/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.ruleunits.dsl;

import java.util.HashMap;
import java.util.Map;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.dsl.domain.Person;

import static org.drools.model.Index.ConstraintType.GREATER_OR_EQUAL;
import static org.drools.ruleunits.dsl.Accumulators.sum;

public class GroupByUnit implements RuleUnitDefinition {

    private final DataStore<Person> persons;
    private final DataStore<Integer> ints;
    private final Map<String, Integer> results = new HashMap<>();

    public GroupByUnit() {
        this(DataSource.createStore(), DataSource.createStore());
    }

    public GroupByUnit(DataStore<Person> persons, DataStore<Integer> ints) {
        this.persons = persons;
        this.ints = ints;
    }

    public DataStore<Person> getPersons() {
        return persons;
    }

    public DataStore<Integer> getInts() {
        return ints;
    }

    public Map<String, Integer> getResults() {
        return results;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        // groupBy( $p: /persons[ age >= 18 ]; $p.substring(0, 1); sum($p.age) )
        rulesFactory.addRule("Sum adults age grouped by initial")
                    .groupBy( rule -> rule.from(persons).filter("age", Person::getAge, GREATER_OR_EQUAL, 18),
                              p -> p.getName().substring(0, 1), // grouping function
                              sum(Person::getAge) )
                    .execute(results, (r, initial, sum) -> r.put(initial, sum));
    }
}
