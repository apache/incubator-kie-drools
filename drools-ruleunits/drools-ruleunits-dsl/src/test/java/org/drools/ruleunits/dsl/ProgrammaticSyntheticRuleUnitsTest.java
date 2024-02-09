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
import java.util.function.Consumer;

import org.drools.model.Index;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// this test simulates YaRD internal behaviour; if breaking, coordinate changes to YaRD impl2.
public class ProgrammaticSyntheticRuleUnitsTest {

    @Test
    public void test() {
        DataStore<String> strings = DataSource.createStore();
        DataStore<Integer> ints = DataSource.createStore();
        List<String> results = new ArrayList<>();

        SyntheticRuleUnitBuilder unitBuilder = SyntheticRuleUnitBuilder.build("ProgTestUnit");
        unitBuilder.registerDataSource("strings", strings, String.class);
        unitBuilder.registerDataSource("ints", ints, Integer.class);
        unitBuilder.registerGlobal("results", results);

        List<RuleDefinition> rules = parseRules(strings, ints);

        Consumer<RulesFactory> rulesDefinition = createRulesDefinition(rules, results);

        SyntheticRuleUnit unit = unitBuilder.defineRules(rulesDefinition);

        RuleUnitInstance<SyntheticRuleUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit);

        strings.add("Hi Universe");
        strings.add("Hello World");
        ints.add(7);

        assertThat(unitInstance.fire()).isEqualTo(1);
        assertThat(results).containsExactlyInAnyOrder("R2");

    }

    private Consumer<RulesFactory> createRulesDefinition(List<RuleDefinition> rules, List<String> results) {
        return rulesFactory -> {
            int i = 0;
            for (RuleDefinition rule : rules) {
                addRule(rulesFactory.rule(rule.ruleName), rule, results);
            }
        };
    }

    private void addRule(RuleFactory ruleFactory, RuleDefinition rule, List<String> results) {
        for (Constraint constraint : rule.constraints) {
            ruleFactory.on(constraint.dataStore).filter(constraint.operator, constraint.value);
        }

        ruleFactory.execute(results, r -> r.add(rule.ruleName));
    }

    private List<RuleDefinition> parseRules(DataStore<String> strings, DataStore<Integer> ints) {
        List<RuleDefinition> rules = new ArrayList<>();

        // /strings[ this == "Hi Universe" ]
        // /ints[ this <= 5 ]
        Constraint<String> r1c1 = new Constraint<>(strings, Index.ConstraintType.EQUAL, "Hi Universe");
        Constraint<Integer> r1c2 = new Constraint<>(ints, Index.ConstraintType.LESS_OR_EQUAL, 5);
        rules.add(new RuleDefinition("R1", r1c1, r1c2));

        // /strings[ this == "Hello World" ]
        // /ints[ this > 5 ]
        Constraint<String> r2c1 = new Constraint<>(strings, Index.ConstraintType.EQUAL, "Hello World");
        Constraint<Integer> r2c2 = new Constraint<>(ints, Index.ConstraintType.GREATER_THAN, 5);
        rules.add(new RuleDefinition("R2", r2c1, r2c2));

        return rules;
    }

    private static class Constraint<T> {
        private final DataStore<T> dataStore;
        private final Index.ConstraintType operator;
        private final T value;

        private Constraint(DataStore<T> dataStore, Index.ConstraintType operator, T value) {
            this.dataStore = dataStore;
            this.operator = operator;
            this.value = value;
        }
    }

    private static class RuleDefinition {
        private final String ruleName;

        private final Constraint[] constraints;

        private RuleDefinition(String ruleName, Constraint... constraints) {
            this.ruleName = ruleName;
            this.constraints = constraints;
        }
    }
}
