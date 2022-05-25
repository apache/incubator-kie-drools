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

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;

import static org.drools.model.Index.ConstraintType.EQUAL;
import static org.drools.model.Index.ConstraintType.GREATER_THAN;
import static org.drools.model.Index.ConstraintType.LESS_THAN;

public class HelloWorld implements RuleUnitDefinition {

    private final DataStore<String> strings;
    private final DataStore<Integer> ints;
    private final List<String> results = new ArrayList<>();

    public HelloWorld() {
        this(DataSource.createStore(), DataSource.createStore());
    }

    public HelloWorld(DataStore<String> strings, DataStore<Integer> ints) {
        this.strings = strings;
        this.ints = ints;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    public DataStore<Integer> getInts() {
        return ints;
    }

    public List<String> getResults() {
        return results;
    }

    @Override
    public void defineRules(RulesContext rulesContext) {
        rulesContext.addRule()
                .from(strings, String.class) // it is mandatory to also pass the datasource class only when the datasource is null (which should also be avoided)
                .filter(EQUAL, "Hello World") // when no extractor is provided "this" is implicit
                .execute(results, r -> r.add("it worked!")); // the consequence can ignore the matched facts

        rulesContext.addRule()
                .from(strings) // since the datasource has been already initialized its class can be inferred without the need of explicitly passing it
                .filter(s -> s.length(), GREATER_THAN, 5) // when no property name is provided it's impossible to generate indexes and property reactivity
                .execute(results, (r, s) -> r.add("it also worked with " + s.toUpperCase())); // this consequence also uses the matched fact

        rulesContext.addRule("MyRule") // it is possible to optionally set a name for the rule
                .from(strings)
                .filter("length", s -> s.length(), LESS_THAN, 5) // providing the name of the property used in the constraint allows index and property reactivity generation
                .execute(results, r -> r.add("this shouldn't fire"));

        rulesContext.addRule()
                .from(strings)
                .filter("length", s -> s.length() > 5) // it is also possible to use a plain lambda predicate, but in this case no index can be generated
                .join(ints)
                .filter(GREATER_THAN, 5) // a filter with a fixed right value is a alpha constraint
                .filter(EQUAL, String::length) // a filter with a right extractor is a beta constraint
                .execute(results, (r, s, i) -> r.add("String '" + s + "' is " + i + " characters long")); // the consequence captures all the joined variables positionally
    }
}
