/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests.concurrency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.drools.compiler.integrationtests.facts.BeanA;
import org.drools.compiler.integrationtests.facts.BeanB;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import static org.assertj.core.api.Assertions.*;

/**
 * Test each thread having it's own separate KieBase and KieSession.
 */
public class ConcurrentBasesParallelTest extends AbstractConcurrentTest {

    private static final Integer NUMBER_OF_THREADS = 10;

    static Stream<Arguments> parameters() {
        return Stream.of(new Parameters(true, true),
                         new Parameters(true, false),
                         new Parameters(false, false),
                         new Parameters(false, true))
                .map(Arguments::arguments);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testOneOfAllFactsMatches(Parameters params) throws InterruptedException {
        final int numberOfObjects = 100;

        final TestExecutor exec = counter -> {
            final String rule = "import " + BeanA.class.getCanonicalName() + ";\n" +
                    "rule Rule_" + counter + " " +
                    "when " +
                    "    BeanA( seed == " + counter + ") " +
                    "then " +
                    "end";

            final KieBase base = getKieBase(params, rule);
            final KieSession session = base.newKieSession();
            try {
                for (int i = 0; i < numberOfObjects; i++) {
                    session.insert(new BeanA(i));
                }
                assertThat(session.fireAllRules()).isEqualTo(1);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testNoFactMatches(Parameters params) throws InterruptedException {
        final TestExecutor exec = counter -> {
            final String rule = "import " + BeanA.class.getCanonicalName() + ";\n" +
                    "rule Rule_" + counter + " " +
                    "when " +
                    "    BeanA( seed == " + counter + ") " +
                    "then " +
                    "end";

            final KieBase base = getKieBase(params, rule);
            final KieSession session = base.newKieSession();

            try {
                for (int i = 0; i < NUMBER_OF_THREADS; i++) {
                    if (i != counter) {
                        session.insert(new BeanA(i));
                    }
                }
                assertThat(session.fireAllRules()).isEqualTo(0);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testFireAndGlobalSeparation(Parameters params) throws InterruptedException {
        final TestExecutor exec = counter -> {
            final String rule = "import " + BeanA.class.getCanonicalName() + ";\n" +
                    "global " + AtomicInteger.class.getCanonicalName() + " result;\n" +
                    "rule Rule_" + counter + " " +
                    "when " +
                    "    BeanA()" +
                    "then " +
                    "    result.set(" + counter + ");" +
                    "end";

            final KieBase base = getKieBase(params, rule);
            final KieSession session = base.newKieSession();

            try {
                session.insert(new BeanA());
                final AtomicInteger r = new AtomicInteger(0);
                session.setGlobal("result", r);
                assertThat(session.fireAllRules()).isEqualTo(1);
                assertThat(r.get()).isEqualTo(counter);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testFireAndGlobalSeparation2(Parameters params) throws InterruptedException {
        final String ruleTemplate = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "import " + BeanB.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule ${ruleName} " +
                "when " +
                "${className}()" +
                "then " +
                "    list.add(\"${className}\");" +
                "end";

        final TestExecutor exec = counter -> {

            final String className = (counter % 2 == 0) ? "BeanA" : "BeanB";
            final String ruleName = "Rule_" + className + "_" + counter;
            final String rule = ruleTemplate.replace("${ruleName}", ruleName).replace("${className}", className);

            final KieBase base = getKieBase(params, rule);
            final KieSession session = base.newKieSession();

            try {
                session.insert(new BeanA());
                session.insert(new BeanB());
                final List<String> list = new ArrayList<>();
                session.setGlobal("list", list);
                final int rulesFired = session.fireAllRules();
                assertThat(list).hasSize(1);
                assertThat(list.get(0)).isEqualTo(className);
                assertThat(rulesFired).isEqualTo(1);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testNonMatchingFact(Parameters params) throws InterruptedException {
        final String ruleTemplate = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "import " + BeanB.class.getCanonicalName() + ";\n" +
                "rule ${ruleName} " +
                "when " +
                "${className}()" +
                "then " +
                "end";

        final TestExecutor exec = counter -> {
            final String className = (counter % 2 == 0) ? "BeanA" : "BeanB";
            final String ruleName = "Rule_" + className + "_" + counter;
            final String rule = ruleTemplate.replace("${ruleName}", ruleName).replace("${className}", className);

            final KieBase base = getKieBase(params, rule);
            final KieSession session = base.newKieSession();

            try {
                if (counter % 2 == 0) {
                    session.insert(new BeanB());
                } else {
                    session.insert(new BeanA());
                }
                assertThat(session.fireAllRules()).isEqualTo(0);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testMatchingFact(Parameters params) throws InterruptedException {
        final String ruleTemplate = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "import " + BeanB.class.getCanonicalName() + ";\n" +
                "rule ${ruleName} " +
                "when " +
                "${className}()" +
                "then " +
                "end";

        final TestExecutor exec = counter -> {
            final String className = (counter % 2 == 0) ? "BeanA" : "BeanB";
            final String ruleName = "Rule_" + className + "_" + counter;
            final String rule = ruleTemplate.replace("${ruleName}", ruleName).replace("${className}", className);

            final KieBase base = getKieBase(params, rule);
            final KieSession session = base.newKieSession();

            try {
                if (counter % 2 == 0) {
                    session.insert(new BeanA());
                } else {
                    session.insert(new BeanB());
                }
                assertThat(session.fireAllRules()).isEqualTo(1);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testNot(Parameters params) throws InterruptedException {
        final String ruleTemplate = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "import " + BeanB.class.getCanonicalName() + ";\n" +
                "rule ${ruleName} " +
                "when " +
                "    not ${className}()" +
                "then " +
                "end";

        final TestExecutor exec = counter -> {

            final String className = (counter % 2 == 0) ? "BeanA" : "BeanB";
            final String ruleName = "Rule_" + className + "_" + counter;
            final String rule = ruleTemplate.replace("${ruleName}", ruleName).replace("${className}", className);

            final KieBase base = getKieBase(params, rule);
            final KieSession session = base.newKieSession();

            try {
                if (counter % 2 == 0) {
                    session.insert(new BeanA());
                } else {
                    session.insert(new BeanB());
                }
                assertThat(session.fireAllRules()).isEqualTo(0);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testExists(Parameters params) throws InterruptedException {
        final String ruleTemplate = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "import " + BeanB.class.getCanonicalName() + ";\n" +
                "rule ${ruleName} " +
                "when " +
                "    exists ${className}()" +
                "then " +
                "end";

        final TestExecutor exec = counter -> {

            final String className = (counter % 2 == 0) ? "BeanA" : "BeanB";
            final String ruleName = "Rule_" + className + "_" + counter;
            final String rule = ruleTemplate.replace("${ruleName}", ruleName).replace("${className}", className);

            final KieBase base = getKieBase(params, rule);
            final KieSession session = base.newKieSession();

            try {
                if (counter % 2 == 0) {
                    session.insert(new BeanA());
                } else {
                    session.insert(new BeanB());
                }
                assertThat(session.fireAllRules()).isEqualTo(1);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testSubnetwork(Parameters params) throws InterruptedException {
        final String ruleTemplate = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "import " + BeanB.class.getCanonicalName() + ";\n" +
                "rule ${ruleName} " +
                "when " +
                "    $bean : ${className}() \n" +
                "then " +
                "end";

        final String subnetworkRuleTemplate = "rule Rule_subnetwork " +
                "when " +
                "    $bean : ${className}() \n" +
                "    Number( doubleValue > 0) from" +
                "       accumulate ( BeanA() and $s : String(), count($s) )" +
                "then " +
                "end";

        final TestExecutor exec = counter -> {

            final String className = (counter % 2 == 0) ? "BeanA" : "BeanB";
            final String ruleName = "Rule_" + className + "_" + counter;
            final String rule = ruleTemplate.replace("${ruleName}", ruleName).replace("${className}", className);
            final String subnetworkRule = subnetworkRuleTemplate.replace("${className}", className);

            final KieBase base = getKieBase(params, rule, subnetworkRule);
            final KieSession session = base.newKieSession();

            try {
                session.insert("test");
                if (counter % 2 == 0) {
                    session.insert(new BeanA());
                    assertThat(session.fireAllRules()).isEqualTo(2);
                } else {
                    session.insert(new BeanB());
                    assertThat(session.fireAllRules()).isEqualTo(1);
                }
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testAccumulatesMatchOnlyBeanA(Parameters params) throws InterruptedException {
        final String ruleA = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "rule RuleA " +
                "when " +
                "    $n : Number( doubleValue == 1 ) from accumulate($bean : BeanA(), count($bean)) " +
                "then " +
                "end";

        final String ruleB = "import " + BeanB.class.getCanonicalName() + ";\n" +
                "rule RuleB " +
                "when " +
                "    $n : Number( doubleValue == 1 ) from accumulate($bean : BeanB(), count($bean)) " +
                "then " +
                "end";

        final TestExecutor exec = counter -> {

            final KieBase base = getKieBase(params, ruleA, ruleB);
            final KieSession session = base.newKieSession();

            try {
                if (counter % 2 == 0) {
                    session.insert(new BeanA(counter));
                    assertThat(session.fireAllRules()).isEqualTo(1);
                } else {
                    assertThat(session.fireAllRules()).isEqualTo(0);
                }
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testAccumulatesMatchBoth(Parameters params) throws InterruptedException {
        final String ruleA = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "rule RuleA " +
                "when " +
                "    $n : Number( doubleValue == 1 ) from accumulate($bean : BeanA(), count($bean)) " +
                "then " +
                "end";

        final String ruleB = "import " + BeanB.class.getCanonicalName() + ";\n" +
                "rule RuleB " +
                "when " +
                "    $n : Number( doubleValue == 1 ) from accumulate($bean : BeanB(), count($bean)) " +
                "then " +
                "end";

        final TestExecutor exec = counter -> {
            final KieBase base = getKieBase(params, ruleA, ruleB);
            final KieSession session = base.newKieSession();

            try {
                session.insert(new BeanA());
                session.insert(new BeanB());
                assertThat(session.fireAllRules()).isEqualTo(2);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testAccumulatesMatchOnlyOne(Parameters params) throws InterruptedException {
        final String ruleA = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "rule RuleA " +
                "when " +
                "    $n : Number( doubleValue == 1 ) from accumulate($bean : BeanA(), count($bean)) " +
                "then " +
                "end";

        final String ruleB = "import " + BeanB.class.getCanonicalName() + ";\n" +
                "rule RuleB " +
                "when " +
                "    $n : Number( doubleValue == 1 ) from accumulate($bean : BeanB(), count($bean)) " +
                "then " +
                "end";

        final TestExecutor exec = counter -> {
            final KieBase base = getKieBase(params, ruleA, ruleB);
            final KieSession session = base.newKieSession();

            try {
                if (counter % 2 == 0) {
                    session.insert(new BeanA(counter));
                } else {
                    session.insert(new BeanB(counter));
                }
                assertThat(session.fireAllRules()).isEqualTo(1);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testNotsMatchOnlyOne(Parameters params) throws InterruptedException {
        final String ruleA = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "rule RuleNotA " +
                "when " +
                "    not BeanA() " +
                "then " +
                "end";

        final String ruleB = "import " + BeanB.class.getCanonicalName() + ";\n" +
                "rule RuleNotB " +
                "when " +
                "    not BeanB() " +
                "then " +
                "end";

        final TestExecutor exec = counter -> {
            final KieBase base = getKieBase(params, ruleA, ruleB);
            final KieSession session = base.newKieSession();

            try {
                if (counter % 2 == 0) {
                    session.insert(new BeanA(counter));
                } else {
                    session.insert(new BeanB(counter));
                }
                assertThat(session.fireAllRules()).isEqualTo(1);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testNotsMatchBoth(Parameters params) throws InterruptedException {
        final String ruleA = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "rule RuleNotA " +
                "when " +
                "    not BeanA() " +
                "then " +
                "end";

        final String ruleB = "import " + BeanB.class.getCanonicalName() + ";\n" +
                "rule RuleNotB " +
                "when " +
                "    not BeanB() " +
                "then " +
                "end";

        final TestExecutor exec = counter -> {
            final KieBase base = getKieBase(params, ruleA, ruleB);
            final KieSession session = base.newKieSession();

            try {
                if (counter % 2 == 0) {
                    session.insert(new BeanA(counter));
                    session.insert(new BeanB(counter));
                    assertThat(session.fireAllRules()).isEqualTo(0);
                } else {
                    assertThat(session.fireAllRules()).isEqualTo(2);
                }
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testFunctions(Parameters params) throws InterruptedException {
        final String rule = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "global java.util.List list;" +
                "rule Rule " +
                "when " +
                "    BeanA() " +
                "then " +
                "    addToList(list);" +
                "end";

        final TestExecutor exec = counter -> {
            final String function = "import java.util.List;\n" +
                    "function void addToList(List list) { \n" +
                    "    list.add( \"" + counter + "\" );\n" +
                    "}\n";

            final KieBase base = getKieBase(params, rule, function);
            final KieSession session = base.newKieSession();

            try {
                session.insert(new BeanA());
                final List<String> list = new ArrayList<>();
                session.setGlobal("list", list);
                final int rulesFired = session.fireAllRules();
                assertThat(list).hasSize(1);
                assertThat(list.get(0)).isEqualTo("" + counter);
                assertThat(rulesFired).isEqualTo(1);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testFunctions2(Parameters params) throws InterruptedException {
        final int objectCount = 100;

        final String rule = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "global java.util.List list;" +
                "rule Rule " +
                "when " +
                "    BeanA() " +
                "then " +
                "    addToList(list);" +
                "end";

        final String functionTemplate = "import java.util.List;\n" +
                "function void addToList(List list) { \n" +
                "    list.add( \"${identifier}\" );\n" +
                "}\n";

        final TestExecutor exec = counter -> {

            final String identifier = (counter % 2 == 0) ? "even" : "odd";
            final String otherIdentifier = (counter % 2 == 0) ? "odd" : "even";
            final String functionRule = functionTemplate.replace("${identifier}", identifier);

            final KieBase base = getKieBase(params, rule, functionRule);
            final KieSession session = base.newKieSession();

            try {
                final List<String> list = new ArrayList<>();
                session.setGlobal("list", list);
                int rulesFired = 0;
                for (int i = 0; i < objectCount; i++) {
                    session.insert(new BeanA(i));
                    rulesFired += session.fireAllRules();
                }

                assertThat(list).hasSize(objectCount);
                assertThat(list).contains(identifier);
                assertThat(list).doesNotContain(otherIdentifier);
                assertThat(rulesFired).isEqualTo(objectCount);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testQueries(Parameters params) throws InterruptedException {
        final int numberOfObjects = 100;

        final TestExecutor exec = counter -> {
            final String query = "import " + BeanA.class.getCanonicalName() + ";\n" +
                    "query Query " +
                    "    bean : BeanA( seed == " + counter + " ) " +
                    "end";

            final KieBase base = getKieBase(params, query);
            final KieSession session = base.newKieSession();

            try {
                final BeanA bean = new BeanA(counter);
                session.insert(bean);
                for (int i = 0; i < numberOfObjects; i++) {
                    if (i != counter) {
                        session.insert(new BeanA(i));
                    }
                }
                final QueryResults results = session.getQueryResults("Query");
                assertThat(results).hasSize(1);
                for (final QueryResultsRow row : results) {
                    assertThat(row.get("bean")).isEqualTo(bean);
                }
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedConcurrentBasesParallelTest
    public void testQueries2(Parameters params) throws InterruptedException {
        final int numberOfObjects = 100;

        final String queryTemplate = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "query Query " +
                "    bean : BeanA( seed == ${seed} ) " +
                "end";

        final TestExecutor exec = counter -> {
            final int seed = counter % 2;
            final String seedString = "" + seed;
            final String queryDrl = queryTemplate.replace("${seed}", seedString);
            final KieBase base = getKieBase(params, queryDrl);
            final KieSession session = base.newKieSession();

            try {
                for (int i = 0; i < numberOfObjects; i++) {
                    session.insert(new BeanA(seed));
                }
                final QueryResults results = session.getQueryResults("Query");
                assertThat(results).hasSize(numberOfObjects);
                for (final QueryResultsRow row : results) {
                    final BeanA bean = (BeanA) row.get("bean");
                    assertThat(bean.getSeed()).isEqualTo(seed);
                }
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ParameterizedTest
    @MethodSource("parameters")
    public @interface ParameterizedConcurrentBasesParallelTest {

    }
}
