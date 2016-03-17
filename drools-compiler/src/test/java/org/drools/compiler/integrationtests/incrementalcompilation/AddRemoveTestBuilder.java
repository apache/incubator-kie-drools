/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests.incrementalcompilation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddRemoveTestBuilder {
    private final List<TestOperation> testPlan = new ArrayList<TestOperation>();

    public AddRemoveTestBuilder addOperation(final TestOperationType operationType, final Object operationParameter) {
        testPlan.add(new TestOperation(operationType, operationParameter));
        return this;
    }

    public AddRemoveTestBuilder addOperation(final TestOperationType operationType) {
        testPlan.add(new TestOperation(operationType, null));
        return this;
    }

    public List<TestOperation> build() {
        return testPlan;
    }

    public void clear() {
        testPlan.clear();
    }

    public static List<List<TestOperation>> getTestPlan(final String rule1, final String rule2,
            final String rule1Name, final String rule2Name, final Object[] facts) {
        final List<List<TestOperation>> testPlan = new ArrayList<List<TestOperation>>();

        // Session with rules -> Insert facts -> Fire -> Check results -> Remove rule(s) -> Fire -> Check results
        testPlan.addAll(createInsertFactsFireRulesRemoveRulesTestPlan(rule1, rule2, rule1Name, rule2Name, facts));
        // Same with reverted rules
        testPlan.addAll(createInsertFactsFireRulesRemoveRulesTestPlan(rule2, rule1, rule2Name, rule1Name, facts));

        // Session with rules -> Fire -> Check results -> Insert facts -> Fire -> Check results -> Remove rule(s) -> Fire -> Check results
        testPlan.addAll(createFireRulesInsertFactsFireRulesRemoveRulesTestPlan(rule1, rule2, rule1Name, rule2Name, facts));
        // Same with reverted rules
        testPlan.addAll(createFireRulesInsertFactsFireRulesRemoveRulesTestPlan(rule2, rule1, rule2Name, rule1Name, facts));

        // Session with rules -> Insert facts -> Remove rule(s) -> Fire -> Check results -> Remove rule(s) -> Fire -> Check results
        testPlan.addAll(createInsertFactsRemoveRulesFireRulesRemoveRulesTestPlan(rule1, rule2, rule1Name, rule2Name, facts));
        // Same with reverted rules
        testPlan.addAll(createInsertFactsRemoveRulesFireRulesRemoveRulesTestPlan(rule2, rule1, rule2Name, rule1Name, facts));

        // Session with rules -> Insert facts -> Fire -> Check results -> Remove rule(s) -> Fire -> Reinsert rules -> Check results
        testPlan.addAll(createInsertFactsFireRulesRemoveRulesReinsertRulesTestPlan(rule1, rule2, rule1Name, rule2Name, facts));
        // Same with reverted rules
        testPlan.addAll(createInsertFactsFireRulesRemoveRulesReinsertRulesTestPlan(rule2, rule1, rule2Name, rule1Name, facts));

        return testPlan;
    }

    public static List<List<TestOperation>> createInsertFactsFireRulesRemoveRulesTestPlan(final String rule1,
            final String rule2, final String rule1Name, final String rule2Name, final Object[] facts) {
        final List<List<TestOperation>> testPlan = new ArrayList<List<TestOperation>>();
        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
                .addOperation(TestOperationType.INSERT_FACTS, facts)
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule1Name, rule2Name})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule1Name, rule2Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});
        testPlan.add(builder.build());

        builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
                .addOperation(TestOperationType.INSERT_FACTS, facts)
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule1Name, rule2Name})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule1Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule2Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});
        testPlan.add(builder.build());

        builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
                .addOperation(TestOperationType.INSERT_FACTS, facts)
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule1Name, rule2Name})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule2Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule1Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});
        testPlan.add(builder.build());
        return testPlan;
    }

    public static List<List<TestOperation>> createInsertFactsRemoveRulesFireRulesRemoveRulesTestPlan(final String rule1,
            final String rule2, final String rule1Name, final String rule2Name, final Object[] facts) {
        final List<List<TestOperation>> testPlan = new ArrayList<List<TestOperation>>();
        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
                .addOperation(TestOperationType.INSERT_FACTS, facts)
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule1Name, rule2Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});
        testPlan.add(builder.build());

        builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
                .addOperation(TestOperationType.INSERT_FACTS, facts)
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule2Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule1Name})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule1Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});
        testPlan.add(builder.build());

        builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
                .addOperation(TestOperationType.INSERT_FACTS, facts)
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule1Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule2Name})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule2Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});
        testPlan.add(builder.build());

        return testPlan;
    }

    public static List<List<TestOperation>> createFireRulesInsertFactsFireRulesRemoveRulesTestPlan(final String rule1,
            final String rule2, final String rule1Name, final String rule2Name, final Object[] facts) {
        final List<List<TestOperation>> testPlan = new ArrayList<List<TestOperation>>();
        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
                .addOperation(TestOperationType.INSERT_FACTS, facts)
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule1Name, rule2Name})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule1Name, rule2Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});
        testPlan.add(builder.build());

        builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
                .addOperation(TestOperationType.INSERT_FACTS, facts)
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule1Name, rule2Name})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule1Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule2Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});
        testPlan.add(builder.build());

        builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
                .addOperation(TestOperationType.INSERT_FACTS, facts)
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule1Name, rule2Name})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule2Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule1Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});
        testPlan.add(builder.build());

        return testPlan;
    }

    public static List<List<TestOperation>> createInsertFactsFireRulesRemoveRulesReinsertRulesTestPlan(final String rule1,
            final String rule2, final String rule1Name, final String rule2Name, final Object[] facts) {
        final List<List<TestOperation>> testPlan = new ArrayList<List<TestOperation>>();
        AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
                .addOperation(TestOperationType.INSERT_FACTS, facts)
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule1Name, rule2Name})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule1Name, rule2Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
                .addOperation(TestOperationType.ADD_RULES, new String[]{rule1, rule2})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule1Name, rule2Name});
        testPlan.add(builder.build());

        builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
                .addOperation(TestOperationType.INSERT_FACTS, facts)
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule1Name, rule2Name})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule1Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule2Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
                .addOperation(TestOperationType.ADD_RULES, new String[]{rule1})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule1Name})
                .addOperation(TestOperationType.ADD_RULES, new String[]{rule2})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule2Name})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule1Name, rule2Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});
        testPlan.add(builder.build());

        builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
                .addOperation(TestOperationType.INSERT_FACTS, facts)
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule1Name, rule2Name})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule1Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{rule2Name})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{})
                .addOperation(TestOperationType.ADD_RULES_REINSERT_OLD, new String[]{rule1})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule1Name})
                .addOperation(TestOperationType.ADD_RULES_REINSERT_OLD, new String[]{rule2})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{rule1Name, rule2Name});
        testPlan.add(builder.build());
        return testPlan;
    }

    public static List<TestOperation> createInsertFactsRemoveFireTestPlan(final String rule1, final String rule2,
            final Object[] facts) {
        final AddRemoveTestBuilder builder = new AddRemoveTestBuilder();
        builder.addOperation(TestOperationType.CREATE_SESSION, new String[]{rule1, rule2})
                .addOperation(TestOperationType.INSERT_FACTS, facts)
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{"R2"})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{"R1"})
                .addOperation(TestOperationType.REMOVE_RULES, new String[]{"R1"})
                .addOperation(TestOperationType.FIRE_RULES)
                .addOperation(TestOperationType.CHECK_RESULTS, new String[]{});
        return builder.build();
    }

    public static Object[] getDefaultFacts() {
        return new Object[]{1, 2, "1"};
    }
}
