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
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.Index.ConstraintType.EQUAL;
import static org.drools.model.Index.ConstraintType.GREATER_THAN;
import static org.drools.model.Index.ConstraintType.LESS_THAN;

public class SyntheticRuleUnitsTest {

    @Test
    public void testHelloWorld() {
        DataStore<String> strings = DataSource.createStore();
        DataStore<Integer> ints = DataSource.createStore();
        List<String> results = new ArrayList<>();

        SyntheticRuleUnit unit = SyntheticRuleUnitBuilder.build("HelloWorld")
                .registerDataSource("strings", strings, String.class)
                .registerDataSource("ints", ints, Integer.class)
                .registerGlobal("results", results)
                .defineRules(rulesFactory -> {
                    // /strings[ this == "Hello World" ]
                    rulesFactory.rule()
                            .on(strings)
                            .filter(EQUAL, "Hello World") // when no extractor is provided "this" is implicit
                            .execute(results, r -> r.add("it worked!")); // the consequence can ignore the matched facts

                    // /strings[ length > 5 ]
                    rulesFactory.rule()
                            .on(strings) // since the datasource has been already initialized its class can be inferred without the need of explicitly passing it
                            .filter(s -> s.length(), GREATER_THAN, 5) // when no property name is provided it's impossible to generate indexes and property reactivity
                            .execute(results, (r, s) -> r.add("it also worked with " + s.toUpperCase())); // this consequence also uses the matched fact

                    // /strings[ length < 5 ]
                    rulesFactory.rule("MyRule") // it is possible to optionally set a name for the rule
                            .on(strings)
                            .filter("length", s -> s.length(), LESS_THAN, 5) // providing the name of the property used in the constraint allows index and property reactivity generation
                            .execute(results, r -> r.add("this shouldn't fire"));

                    // $s: /strings[ length > 5 ]
                    // /ints[ this > 5, this == $s.length ]
                    rulesFactory.rule()
                            .on(strings)
                            .filter("length", s -> s.length() > 5) // it is also possible to use a plain lambda predicate, but in this case no index can be generated
                            .join(
                                    rule -> rule.on(ints) // creates a new pattern ...
                                            .filter(GREATER_THAN, 5) // ... add an alpha filter to it
                            ) // ... and join it with the former one
                            .filter(EQUAL, String::length) // this filter is applied to the result of the join, so it is a beta constraint
                            .execute(results, (r, s, i) -> r.add("String '" + s + "' is " + i + " characters long")); // the consequence captures all the joined variables positionally
                });

        RuleUnitInstance<SyntheticRuleUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit);

        strings.add("Hello World");

        assertThat(unitInstance.fire()).isEqualTo(2);
        assertThat(results).containsExactlyInAnyOrder("it worked!", "it also worked with HELLO WORLD");

        results.clear();
        ints.add(11);
        assertThat(unitInstance.fire()).isEqualTo(1);
        assertThat(results).containsExactly("String 'Hello World' is 11 characters long");

        unitInstance.close();
    }

    @Test
    public void testSeparatedUnitCreation() {
        SyntheticRuleUnit unit = createSyntheticRuleUnit();

        RuleUnitInstance<SyntheticRuleUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit);

        unit.getDataStore("strings", String.class).add("Hello World");

        assertThat(unitInstance.fire()).isEqualTo(2);
        assertThat(unit.getGlobal("results", List.class)).containsExactlyInAnyOrder("it worked!", "it also worked with HELLO WORLD");

        unit.getGlobal("results", List.class).clear();
        unit.getDataStore("ints", Integer.class).add(11);
        assertThat(unitInstance.fire()).isEqualTo(1);
        assertThat(unit.getGlobal("results", List.class)).containsExactly("String 'Hello World' is 11 characters long");

        unitInstance.close();
    }

    @Test
    public void syntheticRuleUnitDefinitionReuse() {
        // DROOLS-7363
        SyntheticRuleUnit unit = createSyntheticRuleUnit();
        RuleUnitInstance<SyntheticRuleUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit);

        unit.getDataStore("strings", String.class).add("Hello World");

        assertThat(unitInstance.fire()).isEqualTo(2);
        assertThat(unit.getGlobal("results", List.class)).containsExactlyInAnyOrder("it worked!", "it also worked with HELLO WORLD");

        SyntheticRuleUnit unit2 = createSyntheticRuleUnit();
        RuleUnitInstance<SyntheticRuleUnit> unitInstance2 = RuleUnitProvider.get().createRuleUnitInstance(unit2);

        unit2.getDataStore("strings", String.class).add("Hello World");
        unitInstance2.fire();
        unit2.getGlobal("results", List.class).clear();

        unit2.getDataStore("ints", Integer.class).add(11);
        assertThat(unitInstance2.fire()).isEqualTo(1);
        assertThat(unit2.getGlobal("results", List.class)).containsExactly("String 'Hello World' is 11 characters long");

        unitInstance.close();
        unitInstance2.close();
    }

    private SyntheticRuleUnit createSyntheticRuleUnit() {
        DataStore<String> strings = DataSource.createStore();
        DataStore<Integer> ints = DataSource.createStore();
        List<String> results = new ArrayList<>();

        return SyntheticRuleUnitBuilder.build("HelloWorld")
                .registerDataSource("strings", strings, String.class)
                .registerDataSource("ints", ints, Integer.class)
                .registerGlobal("results", results)
                .defineRules(rulesFactory -> {
                    // /strings[ this == "Hello World" ]
                    rulesFactory.rule()
                            .on(strings)
                            .filter(EQUAL, "Hello World") // when no extractor is provided "this" is implicit
                            .execute(results, r -> r.add("it worked!")); // the consequence can ignore the matched facts

                    // /strings[ length > 5 ]
                    rulesFactory.rule()
                            .on(strings) // since the datasource has been already initialized its class can be inferred without the need of explicitly passing it
                            .filter(s -> s.length(), GREATER_THAN, 5) // when no property name is provided it's impossible to generate indexes and property reactivity
                            .execute(results, (r, s) -> r.add("it also worked with " + s.toUpperCase())); // this consequence also uses the matched fact

                    // /strings[ length < 5 ]
                    rulesFactory.rule("MyRule") // it is possible to optionally set a name for the rule
                            .on(strings)
                            .filter("length", s -> s.length(), LESS_THAN, 5) // providing the name of the property used in the constraint allows index and property reactivity generation
                            .execute(results, r -> r.add("this shouldn't fire"));

                    // $s: /strings[ length > 5 ]
                    // /ints[ this > 5, this == $s.length ]
                    rulesFactory.rule()
                            .on(strings)
                            .filter("length", s -> s.length() > 5) // it is also possible to use a plain lambda predicate, but in this case no index can be generated
                            .join(
                                    rule -> rule.on(ints) // creates a new pattern ...
                                            .filter(GREATER_THAN, 5) // ... add an alpha filter to it
                            ) // ... and join it with the former one
                            .filter(EQUAL, String::length) // this filter is applied to the result of the join, so it is a beta constraint
                            .execute(results, (r, s, i) -> r.add("String '" + s + "' is " + i + " characters long")); // the consequence captures all the joined variables positionally
                });
    }
}
