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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.drools.compiler.integrationtests.facts.BeanA;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.*;

public class SharedSessionParallelTest extends AbstractConcurrentTest {

    static Stream<Arguments> parameters() {
        return Stream.of(new Parameters(true, true),
                         new Parameters(true, false),
                         new Parameters(false, false),
                         new Parameters(false, true))
                .map(Arguments::arguments);
    }

    @ParameterizedSharedSessionParallelTest
    public void testNoExceptions(Parameters params) throws InterruptedException {
        final String drl = "rule R1 when String() then end";

        final int repetitions = 100;
        final int numberOfObjects = 1000;
        final int countOfThreads = 100;

        for (int i = 0; i < repetitions; i++) {

            final KieSession kieSession = getKieBase(params, drl).newKieSession();

            parallelTest(countOfThreads, counter -> {
                try {
                    for (int j = 0; j < numberOfObjects; j++) {
                        kieSession.insert("test_" + numberOfObjects);
                    }
                    kieSession.fireAllRules();
                    return true;
                } catch (final Exception ex) {
                    throw new RuntimeException(ex);
                }
            });

            kieSession.dispose();
        }
    }

    @ParameterizedSharedSessionParallelTest
    public void testCheckOneThreadOnly(Parameters params) throws InterruptedException {
        final int threadCount = 100;
        final List<String> list = Collections.synchronizedList(new ArrayList<>());

        final String drl = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R1 " +
                "when " +
                "    BeanA($n : seed) " +
                "then " +
                "    list.add(\"\" + $n);" +
                "end";

        final KieSession kieSession = getKieBase(params, drl).newKieSession();
        final CountDownLatch latch = new CountDownLatch(threadCount);

        final TestExecutor exec = counter -> {
            kieSession.setGlobal("list", list);
            kieSession.insert(new BeanA(counter));
            latch.countDown();

            if (counter == 0) {
                try {
                    latch.await();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }
                return kieSession.fireAllRules() == threadCount;
            }
            return true;
        };

        parallelTest(threadCount, exec);
        kieSession.dispose();

        assertThat(list).hasSize(threadCount);
        for (int i = 0; i < threadCount; i++) {
            assertThat(list).contains("" + i);
        }
    }

    @ParameterizedSharedSessionParallelTest
    public void testCorrectFirings(Parameters params) throws InterruptedException {
        final int threadCount = 100;

        final String drl = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "global java.util.List globalList;\n" +
                "rule R1 " +
                "when " +
                "    BeanA($n : seed) " +
                "then " +
                "    globalList.add(\"\" + $n);" +
                "end";

        final KieSession kieSession = getKieBase(params, drl).newKieSession();

        final List<String> list = Collections.synchronizedList(new ArrayList<>());

        final TestExecutor exec = counter -> {
            kieSession.setGlobal("globalList", list);
            kieSession.insert(new BeanA(counter));
            kieSession.fireAllRules();
            return true;
        };

        parallelTest(threadCount, exec);
        kieSession.dispose();
        checkList(threadCount, list);
    }

    @ParameterizedSharedSessionParallelTest
    public void testCorrectFirings2(Parameters params) throws InterruptedException {
        final int threadCount = 100;

        final String drl = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R1 " +
                "when " +
                "    BeanA($n : seed, seed == 0) " +
                "then " +
                "    list.add(\"\" + $n);" +
                "end";

        final KieSession kieSession = getKieBase(params, drl).newKieSession();
        final List<String> list = Collections.synchronizedList(new ArrayList<>());

        final TestExecutor exec = counter -> {
            kieSession.setGlobal("list", list);
            kieSession.insert(new BeanA(counter % 2));
            kieSession.fireAllRules();
            return true;
        };

        parallelTest(threadCount, exec);
        kieSession.dispose();
        assertThat(list).contains("" + 0);
        assertThat(list).doesNotContain("" + 1);
        final int expectedListSize = ((threadCount - 1) / 2) + 1;
        assertThat(list).hasSize(expectedListSize);
    }

    @ParameterizedSharedSessionParallelTest
    public void testLongRunningRule(Parameters params) throws InterruptedException {
        final int threadCount = 100;
        final int seed = threadCount + 200;
        final int objectCount = 1000;

        final String longRunningDrl = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule longRunning " +
                "when " +
                "    $bean : BeanA($n : seed, seed > " + threadCount + ") " +
                "then " +
                "    modify($bean) { setSeed($n-1) };" +
                "    list.add(\"\" + $bean.getSeed());" +
                "end";

        final String listDrl = "global java.util.List list2;\n" +
                "rule listRule " +
                "when " +
                "    BeanA($n : seed, seed < " + threadCount + ") " +
                "then " +
                "    list2.add(\"\" + $n);" +
                "end";

        final KieSession kieSession = getKieBase(params, longRunningDrl, listDrl).newKieSession();

        final CyclicBarrier barrier = new CyclicBarrier(threadCount);
        final List<String> list = Collections.synchronizedList(new ArrayList<>());
        final List<String> list2 = Collections.synchronizedList(new ArrayList<>());

        final TestExecutor exec = counter -> {
            try {
                if (counter == 0) {
                    kieSession.setGlobal("list", list);
                    kieSession.setGlobal("list2", list2);
                    kieSession.insert(new BeanA(seed));
                    barrier.await();
                    kieSession.fireAllRules();
                    return true;
                } else {
                    barrier.await();
                    for (int i = 0; i < objectCount; i++) {
                        kieSession.insert(new BeanA(counter));
                    }
                    kieSession.fireAllRules();
                    return true;
                }
            } catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
        };

        parallelTest(threadCount, exec);
        kieSession.dispose();
        checkList(threadCount, seed, list);
        checkList(1, threadCount, list2, (threadCount - 1) * objectCount);
    }

    @ParameterizedSharedSessionParallelTest
    public void testLongRunningRule2(Parameters params) throws InterruptedException {
        final int threadCount = 100;
        final int seed = 1000;

        final String waitingRule = "rule waitingRule " +
                "when " +
                "    String( this == \"wait\" ) " +
                "then " +
                "end";

        final String longRunningDrl = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule longRunning " +
                "when " +
                "    $bean : BeanA($n : seed, seed > 0 ) " +
                "then " +
                "    modify($bean) { setSeed($n-1) };" +
                "    list.add(\"\" + $bean.getSeed());" +
                "end";

        final KieSession kieSession = getKieBase(params, longRunningDrl, waitingRule).newKieSession();

        final CyclicBarrier barrier = new CyclicBarrier(threadCount);
        final List<String> list = Collections.synchronizedList(new ArrayList<>());

        final TestExecutor exec = counter -> {
            try {
                if (counter == 0) {
                    kieSession.setGlobal("list", list);
                    kieSession.insert("wait");
                    kieSession.insert(new BeanA(seed));
                    barrier.await();
                    kieSession.fireAllRules();
                    return true;
                } else {
                    barrier.await();
                    kieSession.insert(new BeanA(seed));
                    kieSession.fireAllRules();
                    return true;
                }
            } catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
        };

        parallelTest(threadCount, exec);
        kieSession.dispose();
        checkList(0, seed, list, seed * threadCount);
    }

    @ParameterizedSharedSessionParallelTest
    public void testLongRunningRule3(Parameters params) throws InterruptedException {
        final int threadCount = 10;
        final int seed = threadCount + 50;
        final int objectCount = 1000;

        final String longRunningDrl = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule longRunning " +
                "when " +
                "    $bean : BeanA($n : seed, seed > " + threadCount + ") " +
                "then " +
                "    modify($bean) { setSeed($n-1) };" +
                "    list.add(\"\" + $bean.getSeed());" +
                "end";

        final String listDrl = "global java.util.List list2;\n" +
                "rule listRule " +
                "when " +
                "    BeanA($n : seed, seed < " + threadCount + ") " +
                "then " +
                "    list2.add(\"\" + $n);" +
                "end";

        final KieSession kieSession = getKieBase(params, longRunningDrl, listDrl).newKieSession();

        final CyclicBarrier barrier = new CyclicBarrier(threadCount);
        final List<String> list = Collections.synchronizedList(new ArrayList<>());
        final List<String> list2 = Collections.synchronizedList(new ArrayList<>());

        final TestExecutor exec = counter -> {
            try {
                if (counter % 2 == 0) {
                    kieSession.setGlobal("list", list);
                    kieSession.setGlobal("list2", list2);
                    kieSession.insert(new BeanA(seed));
                    barrier.await();
                    kieSession.fireAllRules();
                    return true;
                } else {
                    barrier.await();
                    for (int i = 0; i < objectCount; i++) {
                        kieSession.insert(new BeanA(counter));
                    }
                    kieSession.fireAllRules();
                    return true;
                }
            } catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
        };

        parallelTest(threadCount, exec);
        kieSession.dispose();

        final int listExpectedSize = (threadCount / 2 + threadCount % 2) * (seed - threadCount);
        final int list2ExpectedSize = threadCount / 2 * objectCount;
        for (int i = 0; i < threadCount; i++) {
            if (i % 2 == 1) {
                assertThat(list2).contains("" + i);
            }
        }
        assertThat(list).hasSize(listExpectedSize);
        assertThat(list2).hasSize(list2ExpectedSize);
    }

    @ParameterizedSharedSessionParallelTest
    public void testCountdownBean(Parameters params) throws InterruptedException {
        final int threadCount = 100;
        final int seed = 1000;

        final String drl = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule countdown " +
                "when " +
                "    $bean : BeanA($n : seed, seed >  0 ) " +
                "then " +
                "    modify($bean) { setSeed($n-1) };" +
                "    list.add(\"\" + $bean.getSeed());" +
                "end";

        final KieSession kieSession = getKieBase(params, drl).newKieSession();
        final CyclicBarrier barrier = new CyclicBarrier(threadCount);
        final List<String> list = Collections.synchronizedList(new ArrayList<>());
        final BeanA bean = new BeanA(seed);

        final TestExecutor exec = counter -> {
            try {
                if (counter == 0) {
                    kieSession.setGlobal("list", list);
                    kieSession.insert(bean);
                }
                barrier.await();
                kieSession.fireAllRules();
                return true;
            } catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
        };

        parallelTest(threadCount, exec);
        kieSession.dispose();
        checkList(seed, list);
        assertThat(bean).hasFieldOrPropertyWithValue("seed", 0);
    }

    @ParameterizedSharedSessionParallelTest
    public void testCountdownBean2(Parameters params) throws InterruptedException {
        final int threadCount = 100;
        final int seed = 1000;

        final String drl = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule countdown " +
                "when " +
                "    $bean : BeanA($n : seed, seed >  0 ) " +
                "then " +
                "    modify($bean) { setSeed($n-1) };" +
                "    list.add(\"\" + $bean.getSeed());" +
                "end";

        final KieSession kieSession = getKieBase(params, drl).newKieSession();
        final List<String> list = Collections.synchronizedList(new ArrayList<>());
        final BeanA[] beans = new BeanA[threadCount];

        final TestExecutor exec = counter -> {
            final BeanA bean = new BeanA(seed);
            beans[counter] = bean;
            try {
                kieSession.setGlobal("list", list);
                kieSession.insert(bean);
                kieSession.fireAllRules();
                return true;
            } catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
        };

        parallelTest(threadCount, exec);
        kieSession.dispose();

        checkList(0, seed, list, seed * threadCount);
        for (final BeanA bean : beans) {
            assertThat(bean).hasFieldOrPropertyWithValue("seed", 0);
        }
    }

    @ParameterizedSharedSessionParallelTest
    public void testOneRulePerThread(Parameters params) throws InterruptedException {
        final int threadCount = 1000;

        final String[] drls = new String[threadCount];
        for (int i = 0; i < threadCount; i++) {
            drls[i] = "import " + BeanA.class.getCanonicalName() + ";\n" +
                    "global java.util.List list;\n" +
                    "rule R" + i + " " +
                    "when " +
                    "    $bean : BeanA( seed == " + i + " ) " +
                    "then " +
                    "    list.add(\"" + i + "\");" +
                    "end";
        }

        final KieSession kieSession = getKieBase(params, drls).newKieSession();
        final List<String> list = Collections.synchronizedList(new ArrayList<>());

        final TestExecutor exec = counter -> {
            kieSession.setGlobal("list", list);
            kieSession.insert(new BeanA(counter));
            kieSession.fireAllRules();
            return true;
        };

        parallelTest(threadCount, exec);
        kieSession.dispose();
        checkList(threadCount, list);
    }

    private void checkList(final int end, final List list) {
        checkList(0, end, list);
    }

    private void checkList(final int start, final int end, final List list) {
        final int expectedSize = end - start;
        checkList(start, end, list, expectedSize);
    }

    private void checkList(final int start, final int end, final List list, final int expectedSize) {
        assertThat(list).hasSize(expectedSize);
        for (int i = start; i < end; i++) {
            assertThat(list).contains("" + i);
        }
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ParameterizedTest
    @MethodSource("parameters")
    public @interface ParameterizedSharedSessionParallelTest {

    }
}
