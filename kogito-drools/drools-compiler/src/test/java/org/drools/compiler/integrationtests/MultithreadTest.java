/*
 * Copyright 2008 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on Feb 5, 2008
 */

package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.StockTick;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.TimedRuleExectionOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * This is a test case for multi-thred issues
 */
public class MultithreadTest extends CommonTestMethodBase {

    @Test(timeout = 10000)
    public void testConcurrentInsertionsFewObjectsManyThreads() {
        final String drl = "import org.drools.compiler.integrationtests.MultithreadTest.Bean\n" +
                "\n" +
                "rule \"R\"\n" +
                "when\n" +
                "    $a : Bean( seed != 1 )\n" +
                "then\n" +
                "end";
        testConcurrentInsertions(drl, 1, 1000, false, false);
    }

    @Test(timeout = 10000)
    public void testConcurrentInsertionsManyObjectsFewThreads() {
        final String drl = "import org.drools.compiler.integrationtests.MultithreadTest.Bean\n" +
                "\n" +
                "rule \"R\"\n" +
                "when\n" +
                "    $a : Bean( seed != 1 )\n" +
                "then\n" +
                "end";
        testConcurrentInsertions(drl, 1000, 4, false, false);
    }

    @Test(timeout = 10000)
    public void testConcurrentInsertionsNewSessionEachThreadObjectTypeNode() {
        final String drl = "import org.drools.compiler.integrationtests.MultithreadTest.Bean\n" +
                " query existsBeanSeed5More() \n" +
                "     Bean( seed > 5 ) \n" +
                " end \n" +
                "\n" +
                "rule \"R\"\n" +
                "when\n" +
                "    $a: Bean( seed != 1 )\n" +
                "    existsBeanSeed5More() \n" +
                "then\n" +
                "end \n" +
                "rule \"R2\"\n" +
                "when\n" +
                "    $a: Bean( seed != 1 )\n" +
                "then\n" +
                "end\n";
        testConcurrentInsertions(drl, 10, 1000, true, true);
    }

    @Test(timeout = 10000)
    public void testConcurrentInsertionsNewSessionEachThread() {
        final String drl = "import org.drools.compiler.integrationtests.MultithreadTest.Bean\n" +
                " query existsBeanSeed5More() \n" +
                "     Bean( seed > 5 ) \n" +
                " end \n" +
                "\n" +
                "rule \"R\"\n" +
                "when\n" +
                "    $a: Bean( seed != 1 )\n" +
                "    $b: Bean( seed != 2 )\n" +
                "    existsBeanSeed5More() \n" +
                "then\n" +
                "end \n" +
                "rule \"R2\"\n" +
                "when\n" +
                "    $a: Bean( seed != 1 )\n" +
                "    $b: Bean( seed != 2 )\n" +
                "then\n" +
                "end\n" +
                "rule \"R3\"\n" +
                "when\n" +
                "    $a: Bean( seed != 3 )\n" +
                "    $b: Bean( seed != 4 )\n" +
                "    $c: Bean( seed != 5 )\n" +
                "    $d: Bean( seed != 6 )\n" +
                "    $e: Bean( seed != 7 )\n" +
                "then\n" +
                "end";
        testConcurrentInsertions(drl, 10, 1000, true, false);
    }

    private void testConcurrentInsertions(final String drl, final int objectCount, final int threadCount,
            final boolean newSessionForEachThread, final boolean updateFacts) {

        final KieBase kieBase = new KieHelper().addContent(drl, ResourceType.DRL).build();

        Executor executor = Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });

        KieSession ksession = null;
        Callable<Boolean>[] tasks = new Callable[threadCount];
        if (newSessionForEachThread) {
            for (int i = 0; i < threadCount; i++) {
                tasks[i] = getTask( objectCount, kieBase, updateFacts );
            }
        } else {
            ksession = kieBase.newKieSession();
            for (int i = 0; i < threadCount; i++) {
                tasks[i] = getTask( objectCount, ksession, false , updateFacts );
            }
        }

        CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);
        for (Callable<Boolean> task : tasks) {
            ecs.submit( task );
        }

        int successCounter = 0;
        for (int i = 0; i < threadCount; i++) {
            try {
                if ( ecs.take().get() ) {
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
    }

    private Callable<Boolean> getTask( final int objectCount, final KieBase kieBase, final boolean updateFacts) {
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
                        for (int i = 0; i < objectCount; i++) {
                            facts[i] = ksession.insert(new Bean(i));
                        }
                        if (updateFacts) {
                            for (int i = 0; i < objectCount; i++) {
                                ksession.update(facts[i], new Bean(-i));
                            }
                        }
                        for (FactHandle fact : facts) {
                            ksession.delete(fact);
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

    public static class Bean {

        private final int seed;
        private final String threadName;

        public Bean(int seed) {
            this.seed = seed;
            threadName = Thread.currentThread().getName();
        }

        public int getSeed() {
            return seed;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Bean)) return false;
            return seed == ((Bean)other).seed && threadName.equals( ((Bean)other).threadName );
        }

        @Override
        public int hashCode() {
            return 29 * seed + 31 * threadName.hashCode();
        }

        @Override
        public String toString() {
            return "Bean #" + seed + " created by " + threadName;
        }
    }
/*/
    public static class Bean {

        private final int seed;

        public Bean(int seed) {
            this.seed = seed;
        }

        public int getSeed() {
            return seed;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Bean)) return false;
            return seed == ((Bean)other).seed;
        }

        @Override
        public int hashCode() {
            return seed;
        }

        @Override
        public String toString() {
            return "Bean #" + seed;
        }
    }
*/
    @Test(timeout = 1000000)
    public void testSlidingTimeWindows() {
        String str = "package org.drools\n" +
                     "global java.util.List list; \n" +
                     "import org.drools.compiler.StockTick; \n" +
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

        final List<Exception> errors = new ArrayList<Exception>(  );

        KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption(EventProcessingOption.STREAM);
        kbconf.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kbconf, str );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        final EntryPoint ep = ksession.getEntryPoint( "X" );
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Executor executor = Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });

        final int RUN_TIME = 5000; // runs for 10 seconds
        final int THREAD_NR = 2;

        CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);
        ecs.submit(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                try {
                    ksession.fireUntilHalt();
                    return true;
                } catch (Exception e) {
                    errors.add( e );
                    e.printStackTrace();
                    return false;
                }
            }

        });
        for (int i = 0; i < THREAD_NR; i++) {
            ecs.submit(new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    try {
                        final String s = Thread.currentThread().getName();
                        long endTS = System.currentTimeMillis() + RUN_TIME;
                        int j = 0;
                        long lastTimeInserted = -1;
                        while( System.currentTimeMillis() < endTS ) {
                            final long currentTimeInMillis = System.currentTimeMillis();
                            if ( currentTimeInMillis > lastTimeInserted ) {
                                lastTimeInserted = currentTimeInMillis;
                                ep.insert( new StockTick( j++, s, 0.0, 0 ) );
                            }
                        }
                        return true;
                    } catch ( Exception e ) {
                        errors.add( e );
                        e.printStackTrace();
                        return false;
                    }
                }
            });
        }

        boolean success = true;
        for (int i = 0; i < THREAD_NR; i++) {
            try {
                success = ecs.take().get() && success;
            } catch (Exception e) {
                errors.add( e );
            }
        }
        ksession.halt();
        try {
            success = ecs.take().get() && success;
        } catch (Exception e) {
            errors.add( e );
        }

        for ( Exception e : errors ) {
            e.printStackTrace();
        }
        assertTrue( errors.isEmpty() );
        assertTrue( success );

        ksession.dispose();
    }


    @Test( timeout = 10000 )
    public void testClassLoaderRace() throws InterruptedException {

        String drl = "package org.drools.integrationtests;\n" +
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString(drl);
        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        Thread t = new Thread() {
            public void run()
            { session.fireUntilHalt(); }

        };
        t.start();

        session.fireAllRules();


        for ( int j = 0; j < 100; j++ ) {
            session.insert( j );
        }

        try {
            Thread.sleep( 1000 );
            System.out.println( "Halting .." );
            session.halt();
        } catch ( Exception e ) {
            fail( e.getMessage() );
        }
    }



    public static class IntEvent {
        private int data;
        public IntEvent( int j ) { data = j; }
        public int getData() { return data; }
        public void setData( int data ) { this.data = data; }
    }

    public class Server {
        public int currentTemp;
        public double avgTemp;
        public String hostname;
        public int readingCount;

        public Server( String hiwaesdk ) { hostname = hiwaesdk; }

        public String toString() {
            return "Server{" +
                   "currentTemp=" + currentTemp +
                   ", avgTemp=" + avgTemp +
                   ", hostname='" + hostname + '\'' +
                   '}';
        }
    }

    @Test(timeout = 5000)
    public void testRaceOnAccumulateNodeSimple() throws InterruptedException {

        String drl = "package org.drools.integrationtests;\n" +
                     "" +
                     "import org.drools.compiler.integrationtests.MultithreadTest.IntEvent; \n" +
                     "import org.drools.compiler.integrationtests.MultithreadTest.Server; \n" +
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
                     " $avg := Number( ) from accumulate ( " +
                     "      IntEvent ( $temp : data ) over window:length(10) from entry-point ep01; " +
                     "      average ($temp)\n" +
                     "  )\n" +
                     "then\n" +
                     "  $s.avgTemp = $avg.intValue();\n" +
                     "  System.out.println( $avg );\n" +
                     "end\n" +
                     "\n";

        KieBaseConfiguration kbconfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconfig.setOption(EventProcessingOption.STREAM);

        KnowledgeBase kbase = loadKnowledgeBaseFromString(kbconfig, drl);

        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        EntryPoint ep01 = session.getEntryPoint("ep01");

        Runner t = new Runner(session);
        t.start();

        Thread.sleep( 1000 );

        Server hiwaesdk = new Server ("hiwaesdk");
        session.insert( hiwaesdk );
        long LIMIT = 20;

        for ( long i = LIMIT; i > 0; i-- ) {
            ep01.insert ( new IntEvent ( (int) i ) ); //Thread.sleep (0x1); }
            if ( i % 1000 == 0 ) {
                System.out.println( i );
            }
        }

        try {
            Thread.sleep( 1000 );
            System.out.println( "Halting .." );
            session.halt();
        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

        if (t.getError() != null) {
            fail(t.getError().getMessage());
        }
    }



    public static class MyFact {
        Date timestamp = new Date();
        String id = UUID.randomUUID().toString();

        public MyFact() {}

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
    }



    @Test @Ignore
    public void testConcurrencyWithChronThreads() throws InterruptedException {

        String drl = "package it.intext.drools.fusion.bug;\n" +
                     "\n" +
                     "import org.drools.compiler.integrationtests.MultithreadTest.MyFact;\n" +
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

        KieBaseConfiguration kbconfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconfig.setOption(EventProcessingOption.STREAM);

        KnowledgeBase kbase = loadKnowledgeBaseFromString(kbconfig, drl);

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get("REALTIME"));
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(conf,null);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        Runner t = new Runner(ksession);
        t.start();

        final int FACTS_PER_POLL = 1000;
        final int POLL_INTERVAL = 500;

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(
                new Runnable() {

                    public void run() {
                        for ( int j = 0; j < FACTS_PER_POLL; j++ ) {
                            ksession.insert( new MyFact() );
                        }
                    }
                },
                0,
                POLL_INTERVAL,
                TimeUnit.MILLISECONDS );


        Thread.sleep( 10200 );

        executor.shutdownNow();
        ksession.halt();
        t.join();

        if (t.getError() != null) {
            fail(t.getError().getMessage());
        }

        System.out.println( "Final size " + ksession.getObjects().size() );
        //assertEquals( 2000, ksession.getObjects().size() );

        ksession.dispose();
    }

    public static class Runner extends Thread {

        private final StatefulKnowledgeSession ksession;
        private Throwable error;

        public Runner(StatefulKnowledgeSession ksession) {
            this.ksession = ksession;
        }

        @Override
        public void run() {
            try {
                ksession.fireUntilHalt();
            } catch (Throwable t) {
                error = t;
                throw new RuntimeException(t);
            }
        }

        public Throwable getError() {
            return error;
        }
    }



    @Test( timeout = 5000 )
    public void testConcurrentQueries() {
        // DROOLS-175
        StringBuilder drl = new StringBuilder(  );
        drl.append( "package org.drools.test;\n" +
                    "" +
                    "query foo( ) \n" +
                    "   Object() from new Object() \n" +
                    "end\n" +
                    "" +
                    "rule XYZ when then end \n"
        );

        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newByteArrayResource( drl.toString().getBytes() ), ResourceType.DRL );
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Executor executor = Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });

        final int THREAD_NR = 5;

        CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);
        for (int i = 0; i < THREAD_NR; i++) {
            ecs.submit(new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    boolean succ = false;
                    try {
                        QueryResults res = ksession.getQueryResults( "foo", Variable.v );
                        succ = (res.size() == 1);
                        return succ;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return succ;
                    }
                }
            });
        }

        boolean success = true;
        for (int i = 0; i < THREAD_NR; i++) {
            try {
                success = ecs.take().get() && success;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        assertTrue(success);
        ksession.dispose();
    }

    @Test
    public void testConcurrentDelete() {
        String drl =
                "import " + SlowBean.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $sb1: SlowBean() \n" +
                "  $sb2: SlowBean( id > $sb1.id ) \n" +
                "then " +
                "  System.out.println($sb2 + \" > \"+ $sb1);" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        final int BEAN_NR = 4;
        for (int step = 0; step < 2 ; step++) {
            FactHandle[] fhs = new FactHandle[BEAN_NR];
            for (int i = 0; i < BEAN_NR; i++) {
                fhs[i] = ksession.insert(new SlowBean(i + step * BEAN_NR));
            }

            final CyclicBarrier barrier = new CyclicBarrier(2);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ksession.fireAllRules();
                    try {
                        barrier.await();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            try {
                Thread.sleep(15L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < BEAN_NR; i++) {
                if (i % 2 == 1) ksession.delete(fhs[i]);
            }

            try {
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("Done step " + step);
        }
    }

    public class SlowBean {
        private final int id;

        public SlowBean(int id) {
            this.id = id;
        }

        public int getId() {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return id;
        }

        @Override
        public String toString() {
            return "" + id;
        }
    }

    @Test(timeout = 10000)
    public void testConcurrentFireAndDispose() throws InterruptedException {
        // DROOLS-1103
        String drl = "rule R no-loop timer( int: 1s )\n" +
                     "when\n" +
                     "    String()\n" +
                     "then\n" +
                     "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieBase kbase = helper.build( EventProcessingOption.STREAM );
        KieSessionConfiguration ksconf = KieServices.Factory.get().newKieSessionConfiguration();
        ksconf.setOption( TimedRuleExectionOption.YES );
        final KieSession ksession = kbase.newKieSession(ksconf, null);

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException _e) {
                }
                ksession.dispose();
            }
        }.start();

        try {
            int i = 0;
            while (true) {
                ksession.insert("" + i++);
                ksession.fireAllRules();
            }
        } catch (IllegalStateException e) {
            // java.lang.IllegalStateException: Illegal method call. This session was previously disposed.
            // ignore and exit
        } catch (java.util.concurrent.RejectedExecutionException e) {
            e.printStackTrace();
            fail( "java.util.concurrent.RejectedExecutionException should not happen" );
        }
    }

    @Test(timeout = 10000)
    public void testFireUntilHaltAndDispose() throws InterruptedException {
        // DROOLS-1103
        String drl = "rule R no-loop timer( int: 1s )\n" +
                     "when\n" +
                     "    String()\n" +
                     "then\n" +
                     "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieBase kbase = helper.build( EventProcessingOption.STREAM );
        KieSessionConfiguration ksconf = KieServices.Factory.get().newKieSessionConfiguration();
        ksconf.setOption( TimedRuleExectionOption.YES );
        final KieSession ksession = kbase.newKieSession(ksconf, null);

        new Thread() {
            @Override
            public void run() {
                ksession.fireUntilHalt();
            }
        }.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // do nothing
        }

        ksession.insert("xxx");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // do nothing
        }

        ksession.dispose();
    }
}
