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
package org.drools.mvel.integrationtests.concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.drools.mvel.integrationtests.facts.BeanA;
import org.drools.mvel.integrationtests.facts.BeanB;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;


/**
 * Test each thread having it's own separate KieBase and KieSession.
 */
public class ConcurrentBasesParallelTest extends AbstractConcurrentTest {

    public static Stream<Arguments> parameters() {
        List<Boolean> baseParams = Arrays.asList(false, true);

        Collection<KieBaseTestConfiguration> kbParams = TestParametersUtil2.getKieBaseCloudConfigurations(true);
        // combine
        List<Arguments> params = new ArrayList<>();
        for (Boolean baseParam : baseParams) {
            for (KieBaseTestConfiguration kbParam : kbParams) {
                if (baseParam && kbParam.isExecutableModel()) {
                    // jitting & exec-model test is not required
                } else {
                    params.add(arguments(baseParam, kbParam));
                }
            }
        }
        return params.stream();
    }

    private static final Integer NUMBER_OF_THREADS = 10;

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testOneOfAllFactsMatches(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
        final int numberOfObjects = 100;

        final TestExecutor exec = counter -> {
            final String rule = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "rule Rule_" + counter + " " +
                "when " +
                "    BeanA( seed == " + counter + ") " +
                "then " +
                "end";

            final KieBase base = getKieBase(rule);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testNoFactMatches(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
        final TestExecutor exec = counter -> {
            final String rule = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "rule Rule_" + counter + " " +
                "when " +
                "    BeanA( seed == " + counter + ") " +
                "then " +
                "end";

            final KieBase base = getKieBase(rule);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testFireAndGlobalSeparation(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
        final TestExecutor exec = counter -> {
            final String rule = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "global " + AtomicInteger.class.getCanonicalName() + " result;\n" +
                "rule Rule_" + counter + " " +
                "when " +
                "    BeanA()" +
                "then " +
                "    result.set(" + counter + ");" +
                "end";

            final KieBase base = getKieBase(rule);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testFireAndGlobalSeparation2(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
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

            final KieBase base = getKieBase(rule);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testNonMatchingFact(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
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

            final KieBase base = getKieBase(rule);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testMatchingFact(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
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

            final KieBase base = getKieBase(rule);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testNot(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
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

            final KieBase base = getKieBase(rule);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testExists(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
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

            final KieBase base = getKieBase(rule);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testSubnetwork(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
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

            final KieBase base = getKieBase(rule, subnetworkRule);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testAccumulatesMatchOnlyBeanA(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
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

            final KieBase base = getKieBase(ruleA, ruleB);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testAccumulatesMatchBoth(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
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
            final KieBase base = getKieBase(ruleA, ruleB);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testAccumulatesMatchOnlyOne(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
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
            final KieBase base = getKieBase(ruleA, ruleB);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testNotsMatchOnlyOne(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
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
            final KieBase base = getKieBase(ruleA, ruleB);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testNotsMatchBoth(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
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
            final KieBase base = getKieBase(ruleA, ruleB);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testFunctions(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
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

            final KieBase base = getKieBase(rule, function);
            final KieSession session = base.newKieSession();

            try {
                session.insert(new BeanA());
                final List <String> list = new ArrayList<>();
                session.setGlobal("list", list);
                final int rulesFired = session.fireAllRules();
                assertThat(list).hasSize(1);
                assertThat(list.get(0)).isEqualTo(""+counter);
                assertThat(rulesFired).isEqualTo(1);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testFunctions2(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
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

            final String identifier = (counter%2 == 0) ? "even" : "odd";
            final String otherIdentifier = (counter%2 == 0) ? "odd" : "even";
            final String functionRule = functionTemplate.replace("${identifier}", identifier);

            final KieBase base = getKieBase(rule, functionRule);
            final KieSession session = base.newKieSession();

            try {
                final List <String> list = new ArrayList<>();
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testQueries(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
        final int numberOfObjects = 100;

        final TestExecutor exec = counter -> {
            final String query = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "query Query " +
                "    bean : BeanA( seed == "+ counter +" ) " +
                "end";

            final KieBase base = getKieBase(query);
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

    @ParameterizedTest(name = "Enforced jitting={0}, KieBase type={1}")
    @MethodSource("parameters")
    @Timeout(40000)
    public void testQueries2(boolean enforcedJitting, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {
        initTest(enforcedJitting, false, false, false, kieBaseTestConfiguration);
        final int numberOfObjects = 100;

        final String queryTemplate = "import " + BeanA.class.getCanonicalName() + ";\n" +
            "query Query " +
            "    bean : BeanA( seed == ${seed} ) " +
            "end";

        final TestExecutor exec = counter -> {
            final int seed = counter % 2;
            final String seedString = "" + seed;
            final String queryDrl = queryTemplate.replace("${seed}", seedString);
            final KieBase base = getKieBase(queryDrl);
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
}
