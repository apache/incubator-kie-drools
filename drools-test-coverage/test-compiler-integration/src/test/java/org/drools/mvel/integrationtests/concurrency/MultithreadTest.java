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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.drools.core.impl.RuleBaseFactory;
import org.drools.mvel.compiler.StockTick;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.TimedRuleExecutionOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.internal.conf.ConstraintJittingThresholdOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * This is a test case for multi-thred issues
 */
@RunWith(Parameterized.class)
public class MultithreadTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MultithreadTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private static final Logger LOG = LoggerFactory.getLogger(MultithreadTest.class);

    @Test(timeout = 2000000)
    public void testSlidingTimeWindows() {
        final String str = "package org.drools\n" +
                "global java.util.List list; \n" +
                "import " + StockTick.class.getCanonicalName() + "; \n" +
                "" +
                "declare StockTick @role(event) end\n" +
                "" +
                "rule R\n" +
                "when\n" +
                " accumulate( $st : StockTick() over window:time(400ms)\n" +
                "             from entry-point X,\n" +
                "             $c : count(1) )" +
                "then\n" +
                "   list.add( $c ); \n" +
                "end \n";

        final List<Exception> errors = new ArrayList<>();

        KieBaseTestConfiguration streamConfig = TestParametersUtil.getStreamInstanceOf(kieBaseTestConfiguration);
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", streamConfig, str);

        final KieSession ksession = kbase.newKieSession();
        final EntryPoint ep = ksession.getEntryPoint("X");
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final int THREAD_NR = 2;
        final ExecutorService executor = Executors.newFixedThreadPool(THREAD_NR, r -> {
            final Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        try {
            final CompletionService<Boolean> ecs = new ExecutorCompletionService<>(executor);
            ecs.submit(() -> {
                try {
                    ksession.fireUntilHalt();
                    return true;
                } catch (final Exception e) {
                    errors.add(e);
                    e.printStackTrace();
                    return false;
                }
            });

            final int RUN_TIME = 5000; // runs for 10 seconds
            boolean success = false;
            try {
                for (int i = 0; i < THREAD_NR; i++) {
                    ecs.submit(() -> {
                        try {
                            final String s = Thread.currentThread().getName();
                            final long endTS = System.currentTimeMillis() + RUN_TIME;
                            int j = 0;
                            long lastTimeInserted = -1;
                            while (System.currentTimeMillis() < endTS) {
                                final long currentTimeInMillis = System.currentTimeMillis();
                                if (currentTimeInMillis > lastTimeInserted) {
                                    lastTimeInserted = currentTimeInMillis;
                                    ep.insert(new StockTick(j++, s, 0.0, 0));
                                }
                            }
                            return true;
                        } catch (final Exception e) {
                            errors.add(e);
                            e.printStackTrace();
                            return false;
                        }
                    });
                }

                success = true;
                for (int i = 0; i < THREAD_NR; i++) {
                    try {
                        success = ecs.take().get() && success;
                    } catch (final Exception e) {
                        errors.add(e);
                    }
                }
            } finally {
                ksession.halt();

                try {
                    success = ecs.take().get() && success;
                } catch (final Exception e) {
                    errors.add(e);
                }

                for (final Exception e : errors) {
                    e.printStackTrace();
                }

                assertThat(errors).isEmpty();
                assertThat(success).isTrue();

                ksession.dispose();
            }
        } finally {
            executor.shutdownNow();
        }
    }

    @Test(timeout = 20000)
    public void testClassLoaderRace() throws InterruptedException {

        final String drl = "package org.drools.integrationtests;\n" +
                "" +
                "rule \"average temperature\"\n" +
                "when\n" +
                " $avg := Number( ) from accumulate ( " +
                "      $x : Integer ( ); " +
                "      average ($x) )\n" +
                "then\n" +
                "  System.out.println( $avg );\n" +
                "end\n" +
                "\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();

        final Thread t = new Thread(session::fireUntilHalt);
        t.start();
        try {
            session.fireAllRules();

            for (int j = 0; j < 100; j++) {
                session.insert(j);
            }
            Thread.sleep(1000);
        } finally {
            session.halt();
            session.dispose();
        }
    }

    public static class IntEvent {

        private int data;

        public IntEvent(final int j) {
            data = j;
        }

        public int getData() {
            return data;
        }

        public void setData(final int data) {
            this.data = data;
        }
    }

    public class Server {

        public int currentTemp;
        public double avgTemp;
        public String hostname;
        public int readingCount;

        public Server(final String hiwaesdk) {
            hostname = hiwaesdk;
        }

        public String toString() {
            return "Server{" +
                    "currentTemp=" + currentTemp +
                    ", avgTemp=" + avgTemp +
                    ", hostname='" + hostname + '\'' +
                    '}';
        }
    }

    @Test(timeout = 20000)
    public void testRaceOnAccumulateNodeSimple() throws InterruptedException {

        final String drl = "package org.drools.integrationtests;\n" +
                "" +
                "import " + Server.class.getCanonicalName() + ";\n" +
                "import " + IntEvent.class.getCanonicalName() + ";\n" +
                "" +
                "declare IntEvent\n" +
                "  @role ( event )\n" +
                "  @expires( 15s )\n" +
                "end\n" +
                "\n" +
                "" +
                "rule \"average temperature\"\n" +
                "when\n" +
                "  $s : Server (hostname == \"hiwaesdk\")\n" +
                " $avg := Number( this != null ) from accumulate ( " +
                "      IntEvent ( $temp : data ) over window:length(10) from entry-point ep01; " +
                "      average ($temp)\n" +
                "  )\n" +
                "then\n" +
                "  $s.avgTemp = $avg.intValue();\n" +
                "  System.out.println( $avg );\n" +
                "end\n" +
                "\n";

        KieBaseTestConfiguration streamConfig = TestParametersUtil.getStreamInstanceOf(kieBaseTestConfiguration);
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", streamConfig, drl);


        final KieSession session = kbase.newKieSession();
        final EntryPoint ep01 = session.getEntryPoint("ep01");

        final Runner t = new Runner(session);
        t.start();
        try {
            Thread.sleep(1000);

            final Server hiwaesdk = new Server("hiwaesdk");
            session.insert(hiwaesdk);
            final long LIMIT = 20;

            for (long i = LIMIT; i > 0; i--) {
                ep01.insert(new IntEvent((int) i)); //Thread.sleep (0x1); }
                if (i % 1000 == 0) {
                    System.out.println(i);
                }
            }
            Thread.sleep(1000);
        } finally {
            session.halt();
            session.dispose();
        }

        if (t.getError() != null) {
            fail(t.getError().getMessage());
        }
    }

    public static class MyFact {

        Date timestamp = new Date();
        String id = UUID.randomUUID().toString();

        public MyFact() {
        }

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }
    }

    @Test
    @Ignore
    public void testConcurrencyWithChronThreads() throws InterruptedException {

        final String drl = "package it.intext.drools.fusion.bug;\n" +
                "\n" +
                "import " + MyFact.class.getCanonicalName() + ";\n " +
                " global java.util.List list; \n" +
                "\n" +
                "declare MyFact\n" +
                "\t@role( event )\n" +
                "\t@expires( 1s )\n" +
                "end\n" +
                "\n" +
                "rule \"Dummy\"\n" +
                "timer( cron: 0/1 * * * * ? )\n" +
                "when\n" +
                "  Number( $count : intValue ) from accumulate( MyFact( ) over window:time(1s); sum(1) )\n" +
                "then\n" +
                "    System.out.println($count+\" myfact(s) seen in the last 1 seconds\");\n" +
                "    list.add( $count ); \n" +
                "end";

        KieBaseTestConfiguration streamConfig = TestParametersUtil.getStreamInstanceOf(kieBaseTestConfiguration);
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", streamConfig, drl);

        final KieSessionConfiguration conf = RuleBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption(ClockTypeOption.REALTIME);
        final KieSession ksession = kbase.newKieSession(conf, null);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        final Runner t = new Runner(ksession);
        t.start();
        try {
            final int FACTS_PER_POLL = 1000;
            final int POLL_INTERVAL = 500;

            final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            try {
                executor.scheduleAtFixedRate(
                        () -> {
                            for (int j = 0; j < FACTS_PER_POLL; j++) {
                                ksession.insert(new MyFact());
                            }
                        },
                        0,
                        POLL_INTERVAL,
                        TimeUnit.MILLISECONDS);

                Thread.sleep(10200);
            } finally {
                executor.shutdownNow();
            }
        } finally {
            ksession.halt();
            ksession.dispose();
        }

        t.join();

        if (t.getError() != null) {
            fail(t.getError().getMessage());
        }

        System.out.println("Final size " + ksession.getObjects().size());

        ksession.dispose();
    }

    public static class Runner extends Thread {

        private final KieSession ksession;
        private Throwable error;

        public Runner(final KieSession ksession) {
            this.ksession = ksession;
        }

        @Override
        public void run() {
            try {
                ksession.fireUntilHalt();
            } catch (final Throwable t) {
                error = t;
                throw new RuntimeException(t);
            }
        }

        public Throwable getError() {
            return error;
        }
    }

    @Test(timeout = 20000)
    public void testConcurrentQueries() {
        // DROOLS-175
        final StringBuilder drl = new StringBuilder();
        drl.append("package org.drools.test;\n" +
                           "" +
                           "query foo( ) \n" +
                           "   Object() from new Object() \n" +
                           "end\n" +
                           "" +
                           "rule XYZ when then end \n"
        );

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl.toString());

        final KieSession ksession = kbase.newKieSession();

        final int THREAD_NR = 5;
        final ExecutorService executor = Executors.newFixedThreadPool(THREAD_NR, r -> {
            final Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        try {
            final CompletionService<Boolean> ecs = new ExecutorCompletionService<>(executor);
            for (int i = 0; i < THREAD_NR; i++) {
                ecs.submit(() -> {
                    boolean succ = false;
                    try {
                        final QueryResults res = ksession.getQueryResults("foo");
                        succ = (res.size() == 1);
                        return succ;
                    } catch (final Exception e) {
                        e.printStackTrace();
                        return succ;
                    }
                });
            }

            boolean success = true;
            for (int i = 0; i < THREAD_NR; i++) {
                try {
                    success = ecs.take().get() && success;
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }

            assertThat(success).isTrue();
            ksession.dispose();
        } finally {
            executor.shutdownNow();
        }
    }

    @Test(timeout = 40000)
    public void testConcurrentDelete() {
        final String drl =
                "import " + SlowBean.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        "  $sb1: SlowBean() \n" +
                        "  $sb2: SlowBean( id > $sb1.id ) \n" +
                        "then " +
                        "  System.out.println($sb2 + \" > \"+ $sb1);" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final int BEAN_NR = 4;
        for (int step = 0; step < 2; step++) {
            final FactHandle[] fhs = new FactHandle[BEAN_NR];
            for (int i = 0; i < BEAN_NR; i++) {
                fhs[i] = ksession.insert(new SlowBean(i + step * BEAN_NR));
            }

            final CyclicBarrier barrier = new CyclicBarrier(2);
            new Thread(() -> {
                ksession.fireAllRules();
                try {
                    barrier.await();
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();

            try {
                Thread.sleep(15L);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < BEAN_NR; i++) {
                if (i % 2 == 1) {
                    ksession.delete(fhs[i]);
                }
            }

            try {
                barrier.await();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("Done step " + step);
        }
    }

    public class SlowBean {

        private final int id;

        public SlowBean(final int id) {
            this.id = id;
        }

        public int getId() {
            try {
                Thread.sleep(10L);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
            return id;
        }

        @Override
        public String toString() {
            return "" + id;
        }
    }

    @Test(timeout = 20000)
    public void testConcurrentFireAndDispose() throws InterruptedException {
        // DROOLS-1103
        final String drl = "rule R no-loop timer( int: 1s )\n" +
                "when\n" +
                "    String()\n" +
                "then\n" +
                "end";

        KieBaseTestConfiguration streamConfig = TestParametersUtil.getStreamInstanceOf(kieBaseTestConfiguration);
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", streamConfig, drl);

        final KieSessionConfiguration ksconf = KieServices.Factory.get().newKieSessionConfiguration();
        ksconf.setOption(TimedRuleExecutionOption.YES);
        final KieSession ksession = kbase.newKieSession(ksconf, null);

        final Thread t1 = new Thread() {
            @Override
            public void run() {
                LOG.info("before: sleep, dispose().");
                try {
                    Thread.sleep(100);
                } catch (final InterruptedException _e) {
                }
                LOG.info("before: dispose().");
                ksession.dispose();
                LOG.info("after: dispose().");
            }
        };
        t1.setDaemon(true);
        t1.start();

        try {
            int i = 0;
            LOG.info("before: while.");
            while (true) {
                ksession.insert("" + i++);
                ksession.fireAllRules();
            }
        } catch (final IllegalStateException e) {
            // java.lang.IllegalStateException: Illegal method call. This session was previously disposed.
            // ignore and exit
            LOG.info("after: while.");
        } catch (final java.util.concurrent.RejectedExecutionException e) {
            e.printStackTrace();
            fail("java.util.concurrent.RejectedExecutionException should not happen");
        }
        LOG.info("last line of test.");
    }

    @Test(timeout = 20000)
    public void testFireUntilHaltAndDispose() throws InterruptedException {
        // DROOLS-1103
        final String drl = "rule R no-loop timer( int: 1s )\n" +
                "when\n" +
                "    String()\n" +
                "then\n" +
                "end";

        KieBaseTestConfiguration streamConfig = TestParametersUtil.getStreamInstanceOf(kieBaseTestConfiguration);
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", streamConfig, drl);

        final KieSessionConfiguration ksconf = KieServices.Factory.get().newKieSessionConfiguration();
        ksconf.setOption(TimedRuleExecutionOption.YES);
        final KieSession ksession = kbase.newKieSession(ksconf, null);

        new Thread(ksession::fireUntilHalt).start();
        try {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                // do nothing
            }

            ksession.insert("xxx");

            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                // do nothing
            }
        } finally {
            ksession.dispose();
            ksession.halt();
        }
    }

    @Test(timeout = 40000)
    public void testJittingShortComparison() {
        // DROOLS-1633
        final String drl =
                "import " + BeanA.class.getCanonicalName() + "\n;" +
                        "global java.util.List list;" +
                        "rule R when\n" +
                        "  $a1: BeanA($sv1 : shortValue)\n" +
                        "  $b2: BeanA(shortValue != $sv1)\n" +
                        "then\n" +
                        "  list.add(\"FIRED\");\n" +
                        "end";

        final List<String> list = Collections.synchronizedList(new ArrayList());
        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, drl);
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ConstraintJittingThresholdOption.get(0));

        final int threadNr = 1000;
        final Thread[] threads = new Thread[threadNr];
        for (int i = 0; i < threadNr; i++) {
            threads[i] = new Thread(new SessionRunner(kbase, list));
        }

        for (int i = 0; i < threadNr; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threadNr; i++) {
            try {
                threads[i].join();
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        assertThat(list).hasSize(0);
    }

    public static class SessionRunner implements Runnable {

        private final KieSession ksession;

        public SessionRunner(final KieBase kbase, final List<String> list) {
            ksession = kbase.newKieSession();
            ksession.setGlobal("list", list);
            ksession.insert(new BeanA());
        }

        public void run() {
            ksession.fireAllRules();
        }
    }

    public static class BeanA {

        public Short getShortValue() {
            return 769;
        }
    }
}
