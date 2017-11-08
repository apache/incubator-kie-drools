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

package org.drools.compiler.integrationtests;


import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(Parameterized.class)
public class MultithreadedSubnetworkTest extends CommonTestMethodBase {

    protected final String drl;

    @Parameters(name = "DRL={0}")
    public static List<String[]> getTestParameter() {
        return Arrays.asList(
            new String[] {"sharedSubnetworkRule", sharedSubnetworkRule},
            new String[] {"noSharingSubnetworkRule", noSharingSubnetworkRule},
            new String[] {"notSubnetworkRule", notSubnetworkRule},
            new String[] {"existsSubnetworkRule", existsSubnetworkRule});
    }

    public MultithreadedSubnetworkTest(String drlName, final String drl) {
        this.drl = drl;
    }

    final static String sharedSubnetworkRule =
        "import " + AtomicInteger.class.getCanonicalName() + ";\n" +
            "rule R1y when\n" +
            "    AtomicInteger() \n" +
            "    Number() from accumulate ( AtomicInteger() and $s : String( this == \"test_1\" ) ; count($s) )" +
            "    Long()\n" +
            "then\n" +
            "    System.out.println(\"R1y\");" +
            "end\n" +
            "\n" +
            "rule R1x when\n" +
            "    AtomicInteger( get() == 1 ) \n" +
            "    Number() from accumulate ( AtomicInteger() and $s : String( this == \"test_1\" ) ; count($s) )\n" +
            "then\n" +
            "    System.out.println(\"R1x\");" +
            "end\n" +
            "" +
            "rule R2 when\n" +
            "    $i : AtomicInteger( get() < 3 )\n" +
            "then\n" +
            "    System.out.println(\"R2\");" +
            "    $i.incrementAndGet();" +
            "    update($i);" +
            "end\n";

    final static String noSharingSubnetworkRule =
        "import " + AtomicInteger.class.getCanonicalName() + ";\n" +
            "rule R1y when\n" +
            "    AtomicInteger() \n" +
            "    Number() from accumulate ( AtomicInteger() and $s : String( this == \"test_1\" ) ; count($s) )" +
            "    Long()\n" +
            "then\n" +
            "    System.out.println(\"R1y\");" +
            "end\n" +
            "\n" +
            "rule R1x when\n" +
            "    AtomicInteger() \n" +
            "    Number() from accumulate ( $i : AtomicInteger( get() == 1) and String( this == \"test_2\" ) ; count($i) )\n" +
            "then\n" +
            "    System.out.println(\"R1x\");" +
            "end\n" +
            "" +
            "rule R2 when\n" +
            "    $i : AtomicInteger( get() < 3 )\n" +
            "then\n" +
            "    System.out.println(\"R2\");" +
            "    $i.incrementAndGet();" +
            "    update($i);" +
            "end\n";

    final static String notSubnetworkRule =
        "import " + AtomicInteger.class.getCanonicalName() + ";\n" +
            "rule R1 when\n" +
            "    AtomicInteger() \n" +
            "    not(AtomicInteger( get() == 1 ) and String( this == \"test_1\" )) \n" +
            "then\n" +
            "    System.out.println(\"R1\");" +
            "end\n" +
            "\n" +
            "rule R2 when\n" +
            "    AtomicInteger() \n" +
            "    String( this != \"test_2\" ) \n" +
            "    not(AtomicInteger( get() == 1 ) and String( this == \"test_1\" )) \n" +
            "then\n" +
            "    System.out.println(\"R2\");" +
            "end\n";

    final static String existsSubnetworkRule =
        "import " + AtomicInteger.class.getCanonicalName() + ";\n" +
            "rule R1 when\n" +
            "    AtomicInteger() \n" +
            "    exists(AtomicInteger( get() == 1 ) and String( this == \"test_1\" )) \n" +
            "then\n" +
            "    System.out.println(\"R1\");" +
            "end\n" +
            "\n" +
            "rule R2 when\n" +
            "    AtomicInteger() \n" +
            "    String( this != \"test_2\" ) \n" +
            "    exists(AtomicInteger( get() == 1 ) and String( this == \"test_1\" )) \n" +
            "then\n" +
            "    System.out.println(\"R2\");" +
            "end\n";

    @Test(timeout = 10000)
    public void testConcurrentInsertionsFewObjectsManyThreads() throws InterruptedException {
        testConcurrentInsertions(drl, 1, 1000, false, false);
    }

    @Test(timeout = 10000)
    public void testConcurrentInsertionsManyObjectsFewThreads() throws InterruptedException {
        testConcurrentInsertions(drl, 500, 4, false, false);
    }

    @Test(timeout = 10000)
    public void testConcurrentInsertionsManyObjectsSingleThread() throws InterruptedException {
        testConcurrentInsertions(drl, 1000, 1, false, false);
    }

    @Test(timeout = 10000)
    public void testConcurrentInsertionsNewSessionEachThread() throws InterruptedException {
        testConcurrentInsertions(drl, 10, 1000, true, false);
    }

    @Test(timeout = 10000)
    public void testConcurrentInsertionsNewSessionEachThreadUpdate() throws InterruptedException {
        testConcurrentInsertions(drl, 10, 1000, true, true);
    }

    private void testConcurrentInsertions(final String drl, final int objectCount, final int threadCount,
                                          final boolean newSessionForEachThread, final boolean updateFacts) throws InterruptedException {

        final KieBase kieBase = new KieHelper().addContent(drl, ResourceType.DRL).build();

        ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });

        KieSession ksession = null;
        try {
            Callable<Boolean>[] tasks = new Callable[threadCount];
            if (newSessionForEachThread) {
                for (int i = 0; i < threadCount; i++) {
                    tasks[i] = getTask(objectCount, kieBase, updateFacts);
                }
            } else {
                ksession = kieBase.newKieSession();
                for (int i = 0; i < threadCount; i++) {
                    tasks[i] = getTask(objectCount, ksession, false, updateFacts);
                }
            }

            CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);
            for (Callable<Boolean> task : tasks) {
                ecs.submit(task);
            }

            int successCounter = 0;
            for (int i = 0; i < threadCount; i++) {
                try {
                    if (ecs.take().get()) {
                        successCounter++;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            assertEquals(threadCount, successCounter);
            if (ksession != null) {
                ksession.dispose();
            }
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
    }

    private Callable<Boolean> getTask(final int objectCount, final KieBase kieBase, final boolean updateFacts) {
        return getTask(objectCount, kieBase.newKieSession(), true, updateFacts);
    }

    private Callable<Boolean> getTask(
        final int objectCount,
        final KieSession ksession,
        final boolean disposeSession,
        final boolean updateFacts) {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                try {
                    for (int j = 0; j < 10; j++) {
                        FactHandle[] facts = new FactHandle[objectCount];
                        FactHandle[] stringFacts = new FactHandle[objectCount];
                        for (int i = 0; i < objectCount; i++) {
                            facts[i] = ksession.insert(new AtomicInteger(i));
                            stringFacts[i] = ksession.insert("test_" + i);
                        }
                        if (updateFacts) {
                            for (int i = 0; i < objectCount; i++) {
                                ksession.update(facts[i], new AtomicInteger(-i));
                                ksession.update(stringFacts[i], "updated_test_" + i);
                            }
                        }
                        for (int i = 0; i < objectCount; i++) {
                            ksession.delete(facts[i]);
                            ksession.delete(stringFacts[i]);
                        }
                        ksession.fireAllRules();
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    if (disposeSession) {
                        ksession.dispose();
                    }
                }
            }
        };
    }
}
