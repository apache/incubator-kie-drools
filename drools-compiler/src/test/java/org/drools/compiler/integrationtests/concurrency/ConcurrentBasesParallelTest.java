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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
import org.drools.compiler.integrationtests.facts.BeanA;
import org.drools.compiler.integrationtests.facts.BeanB;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//@RunWith(Parameterized.class)
public class ConcurrentBasesParallelTest extends AbstractConcurrentTest {

    @Parameterized.Parameters(name = "Enforced jitting={0}, Serialize KieBase={1}")
    public static List<Boolean[]> getTestParameters() {
        return Arrays.asList(
                new Boolean[] {false, false},
                new Boolean[] {false, true},
                new Boolean[] {true, false},
                new Boolean[] {true, true});
    }

    private static final Integer NUMBER_OF_THREADS = 10;

//    public ConcurrentBasesParallelTest(final boolean enforcedJitting, final boolean serializeKieBase) {
//        super(enforcedJitting, serializeKieBase, false, false);
//    }

    public ConcurrentBasesParallelTest() {
        super(false, false, false, false);
    }

    @Test
    public void testOneOfAllFactsMatches() throws InterruptedException {
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
                return session.fireAllRules() == 1;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testNoFactMatches() throws InterruptedException {
        final TestExecutor exec = counter -> {
            final String rule = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "rule Rule_" + counter + " " +
                "when " +
                "    BeanA( seed == " + counter + ") " +
                "then " +
                "end";

            final StringBuilder builder = new StringBuilder();
            builder.append("COUNTER: " + counter + "\n");
            builder.append(rule);
            builder.append("\n");
            final KieBase base = getKieBase(rule);
            final KieSession session = base.newKieSession();

            try {
                for (int i = 0; i < 2; i++) {
                    if (i != counter) {
                        builder.append("BeanA: " + i + "\n");
                        session.insert(new BeanA(i));
                    }
                }
                builder.append("Objects: " + session.getObjects().size() + "\n");
                int fireCount = session.fireAllRules();
                builder.append("kieBaseHashCode: " + base.hashCode() + "\n");
                builder.append("sessionHashCode: " + session.hashCode() + "\n");
                builder.append("fireCount: " + fireCount + "\n");
                builder.append("--------------\n");
                System.out.println(builder.toString());
                return fireCount == 0;
            } finally {
                session.dispose();
            }
        };

        parallelTest(2, exec);
    }

    @Test
    public void testDifferentRuleset3() throws InterruptedException {
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
                assertEquals(1, session.fireAllRules());
                assertEquals(counter, r.get());
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testDifferentRuleset4() throws InterruptedException {
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
                assertEquals(1, list.size());
                assertEquals(className, list.get(0));
                return rulesFired == 1;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testDifferentRuleset5() throws InterruptedException {
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
                final int rulesFired = session.fireAllRules();
                assertEquals(0, rulesFired);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testDifferentRuleset6() throws InterruptedException {
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
                final int rulesFired = session.fireAllRules();
                assertEquals(1, rulesFired);
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testDifferentRulesetNot() throws InterruptedException {
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
                final int rulesFired = session.fireAllRules();
                assertEquals(0, rulesFired);
                return true;
            } finally {
                session.dispose();
            }

        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testDifferentRulesetExists() throws InterruptedException {
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
                final int rulesFired = session.fireAllRules();
                assertEquals(1, rulesFired);
                return true;
            } finally {
                session.dispose();
            }

        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testDifferentRulesetSharedSubnetwork() throws InterruptedException {
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
                    assertEquals(2, session.fireAllRules());
                } else {
                    session.insert(new BeanB());
                    assertEquals(1, session.fireAllRules());
                }
                return true;
            } finally {
                session.dispose();
            }

        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testSameRuleset1() throws InterruptedException {
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
                    return session.fireAllRules() == 1;
                } else {
                    return session.fireAllRules() == 0;
                }
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testSameRuleset2() throws InterruptedException {
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
                return session.fireAllRules() == 2;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testSameRuleset3() throws InterruptedException {
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
                return session.fireAllRules() == 1;
            } finally {
                session.dispose();
            }

        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testSameRuleset4() throws InterruptedException {
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
                return session.fireAllRules() == 1;
            } finally {
                session.dispose();
            }

        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testSameRuleset5() throws InterruptedException {
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
                    return session.fireAllRules() == 0;
                } else {
                    return session.fireAllRules() == 2;
                }
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testFunctions() throws InterruptedException {
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
                assertEquals(1, list.size());
                assertEquals(""+counter, list.get(0));
                return rulesFired == 1;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testFunctions2() throws InterruptedException {
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
                assertEquals(objectCount, list.size());
                assertTrue(list.contains(identifier));
                assertTrue(!list.contains(otherIdentifier));
                return rulesFired == objectCount;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testQueries() throws InterruptedException {
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
                assertEquals(1, results.size());
                for (final QueryResultsRow row : results) {
                    assertEquals(bean, row.get("bean"));
                }
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }

    @Test
    public void testQueries2() throws InterruptedException {
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
                assertEquals(numberOfObjects, results.size());
                for (final QueryResultsRow row : results) {
                    final BeanA bean = (BeanA) row.get("bean");
                    assertEquals(seed, bean.getSeed());
                }
                return true;
            } finally {
                session.dispose();
            }
        };

        parallelTest(NUMBER_OF_THREADS, exec);
    }
}
