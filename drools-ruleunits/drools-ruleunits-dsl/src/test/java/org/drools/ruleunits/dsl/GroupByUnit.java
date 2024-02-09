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

import java.util.HashMap;
import java.util.Map;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.SingletonStore;
import org.drools.ruleunits.dsl.domain.Person;

import static org.drools.model.Index.ConstraintType.GREATER_OR_EQUAL;
import static org.drools.model.functions.Function1.identity;
import static org.drools.ruleunits.dsl.Accumulators.collect;
import static org.drools.ruleunits.dsl.Accumulators.sum;

public class GroupByUnit implements RuleUnitDefinition {

    private final DataStore<Person> persons;
    private final SingletonStore<Integer> threshold;
    private final Map<String, Object> results = new HashMap<>();

    public GroupByUnit() {
        this(DataSource.createStore(), DataSource.createSingleton());
    }

    public GroupByUnit(DataStore<Person> persons, SingletonStore<Integer> threshold) {
        this.persons = persons;
        this.threshold = threshold;
    }

    public DataStore<Person> getPersons() {
        return persons;
    }

    public SingletonStore<Integer> getThreshold() {
        return threshold;
    }

    public Map<String, Object> getResults() {
        return results;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        // groupBy( $p: /persons[ age >= 18 ]; $p.substring(0, 1); sum($p.age) )
        rulesFactory.rule("Sum adults ages grouped by initial")
                    .groupBy( rule -> rule.on(persons).filter("age", Person::getAge, GREATER_OR_EQUAL, 18),
                              p -> p.getName().substring(0, 1), // grouping function
                              sum(Person::getAge) )
                    .execute(results, (r, initial, sum) -> r.put(initial, sum));

        // $i : /threshold
        // groupBy( $p: /persons[ age >= threshold ]; $p.substring(0, 1); sum($p.age) )
        rulesFactory.rule("Collect persons name with ages above threshold grouped by initial")
                .on(threshold)
                .groupBy( rule -> rule.on(persons).filter("age", Person::getAge, GREATER_OR_EQUAL, identity()),
                        p -> p.getName().substring(0, 1), // grouping function
                        collect(Person::getName) )
                .execute(results, (r, t, initial, sum) -> r.put(initial, sum));
    }
}
