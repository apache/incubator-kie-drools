/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.ruleunits.dsl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.dsl.domain.Cheese;
import org.drools.ruleunits.dsl.domain.Person;

import static org.drools.model.Index.ConstraintType.EQUAL;
import static org.drools.model.Index.ConstraintType.GREATER_THAN;
import static org.drools.model.Index.ConstraintType.LESS_THAN;

public class MultiJoinUnit implements RuleUnitDefinition {

    private final DataStore<String> strings;
    private final DataStore<Integer> ints;
    private final DataStore<Person> persons;
    private final DataStore<Cheese> cheeses;
    private final List<String> results = new ArrayList<>();

    public MultiJoinUnit() {
        this(DataSource.createStore(), DataSource.createStore(), DataSource.createStore(), DataSource.createStore());
    }

    public MultiJoinUnit(DataStore<String> strings, DataStore<Integer> ints, DataStore<Person> persons, DataStore<Cheese> cheeses) {
        this.strings = strings;
        this.ints = ints;
        this.persons = persons;
        this.cheeses = cheeses;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    public DataStore<Integer> getInts() {
        return ints;
    }

    public DataStore<Person> getPersons() {
        return persons;
    }

    public DataStore<Cheese> getCheeses() {
        return cheeses;
    }

    public List<String> getResults() {
        return results;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        // $s: /strings[ length > 5 ]
        // $i: /ints[ this > 5, this == $s.length ]
        // $p: /persons[ age > name.length, age == ($i + $s.length) * 2 + 4, age == $i + $s.length + 26 ]
        // $p: /cheeses[ price < $p.name.length + $i ]
        rulesFactory.rule("Complex multiple joins")
                    .on(strings)
                    .filter("length", s -> s.length(), GREATER_THAN, 5)
                    .join( rule -> rule.on(ints).filter(GREATER_THAN, 5) ) // alpha constraint
                    .filter(EQUAL, String::length) // beta constraint
                    .join( rule -> rule.on(persons).filter("age", Person::getAge, GREATER_THAN, "name", p -> p.getName().length()) )
                    .filter( (s, i, p) -> p.getAge() == ( i + s.length() ) * 2 + 4 )
                    .filter( "age", Person::getAge, EQUAL, (s, i) -> i + s.length() + 26 )
                    .on(cheeses)
                    .filter( "price", Cheese::getPrice, LESS_THAN, (s, i, p) -> p.getName().length() + i )
                    .execute(results, (r, s, i, p, c) -> r.add("Found " + p.getName() + " who eats " + c.getName())); // the consequence captures all the joined variables positionally
    }
}
