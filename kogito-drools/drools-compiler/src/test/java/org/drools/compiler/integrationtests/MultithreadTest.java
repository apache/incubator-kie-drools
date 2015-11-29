/*
 * Copyright 2008 JBoss Inc
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
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
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
        testConcurrentInsertions(1, 1000);
    }

    @Test(timeout = 10000)
    public void testConcurrentInsertionsManyObjectsFewThreads() {
        testConcurrentInsertions(1000, 4);
    }

    private void testConcurrentInsertions(final int objectCount, final int threadCount) {
        String str = "import org.drools.compiler.integrationtests.MultithreadTest.Bean\n" +
                     "\n" +
                     "rule \"R\"\n" +
                     "when\n" +
                     "    $a : Bean( seed != 1 )\n" +
                     "then\n" +
                     "end";

        final KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL).build().newKieSession();

        Executor executor = Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });

        Callable<Boolean>[] tasks = new Callable[threadCount];
        for (int i = 0; i < threadCount; i++) {
            tasks[i] = getTask( objectCount, ksession );
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
        ksession.dispose();
    }

    private Callable<Boolean> getTask( final int objectCount, final KieSession ksession ) {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                try {
                    FactHandle[] facts = new FactHandle[objectCount];
                    for (int i = 0; i < objectCount; i++) {
                        facts[i] = ksession.insert(new Bean(i));
                    }
                    ksession.fireAllRules();
                    for (FactHandle fact : facts) {
                        ksession.delete(fact);
                    }
                    ksession.fireAllRules();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
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


    // FIXME
//
//    public void testRuleBaseConcurrentCompilation() {
//        final int THREAD_COUNT = 30;
//        try {
//            boolean success = true;
//            final PackageBuilder builder = new PackageBuilder();
//            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MultithreadRulebaseSharing.drl" ) ) );
//            RuleBase ruleBase = RuleBaseFactory.newRuleBase();
//            ruleBase.addPackage( builder.getPackage() );
//            ruleBase = SerializationHelper.serializeObject( ruleBase );
//            final Thread[] t = new Thread[THREAD_COUNT];
//            final RulebaseRunner[] r = new RulebaseRunner[THREAD_COUNT];
//            for ( int i = 0; i < t.length; i++ ) {
//                r[i] = new RulebaseRunner( i,
//                                           ruleBase );
//                t[i] = new Thread( r[i],
//                                   "thread-" + i );
//                t[i].start();
//            }
//            for ( int i = 0; i < t.length; i++ ) {
//                t[i].join();
//                if ( r[i].getStatus() == RulebaseRunner.Status.FAIL ) {
//                    success = false;
//                }
//            }
//            if ( !success ) {
//                fail( "Multithread test failed. Look at the stack traces for details. " );
//            }
//        } catch ( Exception e ) {
//            e.printStackTrace();
//            fail( "Should not raise any exception: " + e.getMessage() );
//        }
//    }
//
//    public static class RulebaseRunner
//        implements
//        Runnable {
//
//        private static final int ITERATIONS = 300;
//        private final int        id;
//        private final RuleBase   rulebase;
//        private Status           status;
//
//        public RulebaseRunner(final int id,
//                              final RuleBase rulebase) {
//            this.id = id;
//            this.rulebase = rulebase;
//            this.status = Status.SUCCESS;
//        }
//
//        public void run() {
//            try {
//                StatefulSession session2 = this.rulebase.newStatefulSession();
//
//                for ( int k = 0; k < ITERATIONS; k++ ) {
//                    GrandParent gp = new GrandParent( "bob" );
//                    Parent parent = new Parent( "mark" );
//                    parent.setGrandParent( gp );
//
//                    Child child = new Child( "mike" );
//                    child.setParent( parent );
//
//                    session2.insert( gp );
//                    session2.insert( parent );
//                    session2.insert( child );
//                }
//
//                session2.fireAllRules();
//                session2.dispose();
//
//            } catch ( Exception e ) {
//                this.status = Status.FAIL;
//                System.out.println( Thread.currentThread().getName() + " failed: " + e.getMessage() );
//                e.printStackTrace();
//            }
//        }
//
//        public static enum Status {
//            SUCCESS, FAIL
//        }
//
//        /**
//         * @return the id
//         */
//        public int getId() {
//            return id;
//        }
//
//        /**
//         * @return the status
//         */
//        public Status getStatus() {
//            return status;
//        }
//
//    }
//
//    public void testExpectedFires() {
//        try {
//            final PackageBuilder packageBuilder = new PackageBuilder();
//            packageBuilder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MultithreadFiringCheck.drl" ) ) );
//            final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
//            ruleBase.addPackage( packageBuilder.getPackage() );
//            final Queue errorList = new ConcurrentLinkedQueue();
//            final Thread t[] = new Thread[50];
//            for ( int i = 0; i < t.length; i++ ) {
//                final int count = i;
//                t[i] = new Thread( new Runnable() {
//                    public void run() {
//                        try {
//                            final int iterations = count * 15 + 3000;
//                            final List results = new ArrayList();
//                            final StatefulSession session2 = ruleBase.newStatefulSession();
//                            session2.setGlobal( "results",
//                                                results );
//                            session2.insert( new Integer( -1 ) );
//                            for ( int k = 0; k < iterations; k++ ) {
//                                session2.insert( new Integer( k ) );
//                                if ( k + 1 != session2.getAgenda().agendaSize() ) {
//                                    errorList.add( "THREAD-" + count + " ERROR: expected agenda size=" + (k + 1) + " but was " + session2.getAgenda().agendaSize() );
//                                }
//                            }
//                            session2.fireAllRules();
//                            session2.dispose();
//                            if ( results.size() != iterations ) {
//                                errorList.add( "THREAD-" + count + " ERROR: expected fire count=" + iterations + " but was " + results.size() );
//                            }
//                        } catch ( Exception e ) {
//                            errorList.add( "THREAD-" + count + " EXCEPTION: " + e.getMessage() );
//                            e.printStackTrace();
//                        }
//                    }
//                } );
//                t[i].start();
//            }
//            for ( int i = 0; i < t.length; i++ ) {
//                t[i].join();
//            }
//            assertTrue( "Errors during execution: " + errorList.toString(),
//                        errorList.isEmpty() );
//        } catch ( Exception e ) {
//            e.printStackTrace();
//            fail( "No exception should have been raised: " + e.getMessage() );
//        }
//    }
//
//    public void testMultithreadDateStringConstraints() {
//        try {
//            final int THREAD_COUNT = 10;
//            final PackageBuilder packageBuilder = new PackageBuilder();
//            packageBuilder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MultithreadDateStringConstraints.drl" ) ) );
//            final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
//            ruleBase.addPackage( packageBuilder.getPackage() );
//            final Vector errors = new Vector();
//
//            final Thread t[] = new Thread[THREAD_COUNT];
//            for ( int j = 0; j < 10; j++ ) {
//                for ( int i = 0; i < t.length; i++ ) {
//                    t[i] = new Thread() {
//                        public void run() {
//                            try {
//                                final int ITERATIONS = 300;
//                                StatefulSession session = ruleBase.newStatefulSession();
//                                List results = new ArrayList();
//                                session.setGlobal( "results",
//                                                   results );
//                                for ( int k = 0; k < ITERATIONS; k++ ) {
//                                    session.insert( new Order() );
//                                }
//                                session.fireAllRules();
//                                session.dispose();
//                                if ( results.size() != ITERATIONS ) {
//                                    errors.add( "Rules did not fired correctly. Expected: " + ITERATIONS + ". Actual: " + results.size() );
//                                }
//                            } catch ( Exception ex ) {
//                                ex.printStackTrace();
//                                errors.add( ex );
//                            }
//                        }
//
//                    };
//                    t[i].start();
//                }
//                for ( int i = 0; i < t.length; i++ ) {
//                    t[i].join();
//                }
//            }
//            if ( !errors.isEmpty() ) {
//                fail( " Errors occured during execution " );
//            }
//        } catch ( Exception e ) {
//            e.printStackTrace();
//            fail( "Should not raise exception" );
//        }
//    }
//
//    class Runner
//        implements
//        Runnable {
//        private final long             TIME_SPAN;
//        private final StatelessSession session;
//        private final AtomicInteger    count;
//
//        public Runner(long BASE_TIME,
//                      StatelessSession session,
//                      final AtomicInteger count) {
//            this.TIME_SPAN = BASE_TIME;
//            this.session = session;
//            this.count = count;
//        }
//
//        public void run() {
//            //System.out.println( Thread.currentThread().getName() + " starting..." );
//            try {
//                count.incrementAndGet();
//                long time = System.currentTimeMillis();
//                while ( (System.currentTimeMillis() - time) < TIME_SPAN ) {
//                    //System.out.println( Thread.currentThread().getName() + ": added package at " + (System.currentTimeMillis() - time) );
//                    for ( int j = 0; j < 100; j++ ) {
//                        session.execute( getFacts() );
//                    }
//                    //System.out.println( Thread.currentThread().getName() + ": executed rules at " + (System.currentTimeMillis() - time) );
//                }
//            } catch ( Exception ex ) {
//                ex.printStackTrace();
//            }
//            if ( count.decrementAndGet() == 0 ) {
//                synchronized ( MultithreadTest.this ) {
//                    MultithreadTest.this.notifyAll();
//                }
//            }
//            //System.out.println( Thread.currentThread().getName() + " exiting..." );
//        }
//
//        private Cheese[] getFacts() {
//            final int SIZE = 100;
//            Cheese[] facts = new Cheese[SIZE];
//
//            for ( int i = 0; i < facts.length; i++ ) {
//                facts[i] = new Cheese();
//                facts[i].setPrice( i );
//                facts[i].setOldPrice( i );
//            }
//            return facts;
//        }
//    }
//
//    public void testSharedPackagesThreadDeadLock() throws Exception {
//        final int THREADS = Integer.parseInt( System.getProperty( "test.threads",
//                                                                  "10" ) );
//        final long BASE_TIME = Integer.parseInt( System.getProperty( "test.time",
//                                                                     "15" ) ) * 1000;
//
//        final AtomicInteger count = new AtomicInteger( 0 );
//
//        final Package[] pkgs = buildPackages();
//        for ( int i = 0; i < THREADS; i++ ) {
//            RuleBase ruleBase = createRuleBase( pkgs );
//            StatelessSession session = createSession( ruleBase );
//            new Thread( new Runner( BASE_TIME,
//                                    session,
//                                    count ) ).start();
//        }
//        synchronized ( this ) {
//            wait();
//        }
//    }
//
//    private RuleBase createRuleBase(Package[] pkgs) {
//        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
//        for ( Package pkg : pkgs ) {
//            ruleBase.addPackage( pkg );
//        }
//        return ruleBase;
//    }
//
//    private StatelessSession createSession(RuleBase ruleBase) {
//        StatelessSession session = ruleBase.newStatelessSession();
//        return session;
//    }
//
//    private Package[] buildPackages() throws Exception {
//        final String KEY = "REPLACE";
//        final int SIZE = 100;
//        final Package[] pkgs = new Package[SIZE];
//        final String DRL = "package org.drools\n" + "    no-loop true\n" + "    dialect \"java\"\n" + "rule \"" + KEY + "\"\n" + "salience 1\n" + "when\n" + "    $fact:Cheese(price == " + KEY + ", oldPrice not in (11,5))\n" + // thread-lock
//                           "then\n" + "    //$fact.excludeProduct(" + KEY + ", 1, null, null);\n" + "end\n";
//        System.out.print( "Building " + pkgs.length + " packages" );
//        for ( int i = 0; i < pkgs.length; i++ ) {
//            pkgs[i] = getPackage( DRL.replaceAll( KEY,
//                                                  Integer.toString( i ) ) );
//            System.out.print( "." );
//        }
//        System.out.println();
//        return pkgs;
//    }
//
//    private static Package getPackage(String drl) throws Exception {
//        PackageBuilder pkgBuilder = new PackageBuilder();
//        pkgBuilder.addPackageFromDrl( new StringReader( drl ) );
//        if ( pkgBuilder.hasErrors() ) {
//            StringBuilder sb = new StringBuilder();
//            for ( Object obj : pkgBuilder.getErrors() ) {
//                if ( sb.length() > 0 ) {
//                    sb.append( '\n' );
//                }
//                sb.append( obj );
//            }
//            throw new DroolsParserException( sb.toString() );
//        }
//        return pkgBuilder.getPackage();
//    }
//
//    public void testEventExpiration() {
//        String rule =
//            "package org.drools\n" +
//            "declare StockTick @role(event) @expires(0s) end\n" +
//            "rule test no-loop true\n" +
//            "when\n" +
//            "   $f : StockTick() from entry-point EntryPoint\n" +
//            "then\n" +
//            "   //System.out.println($f);\n" +
//            "end";
//
//        final StatefulKnowledgeSession session;
//        final WorkingMemoryEntryPoint entryPoint;
//
//        KnowledgeBaseConfiguration kbaseConf = KnowledgeBaseFactory
//            .newKnowledgeBaseConfiguration();
//        kbaseConf.setOption(EventProcessingOption.STREAM);
//
//        KnowledgeBuilder builder = KnowledgeBuilderFactory
//            .newKnowledgeBuilder();
//
//        builder.add(ResourceFactory.newReaderResource(new StringReader(rule)),
//            ResourceType.DRL);
//
//        if (builder.hasErrors()) {
//            throw new RuntimeException(builder.getErrors().toString());
//        }
//
//        final KnowledgeBase knowledgeBase = KnowledgeBaseFactory
//            .newKnowledgeBase(kbaseConf);
//
//        knowledgeBase.addKnowledgePackages(builder.getKnowledgePackages());
//
//        session = knowledgeBase.newStatefulKnowledgeSession();
//        WorkingMemoryEventListener wmel = Mockito.mock( WorkingMemoryEventListener.class );
//        session.addEventListener( wmel );
//
//        entryPoint = session
//            .getWorkingMemoryEntryPoint("EntryPoint");
//
//        new Thread(new Runnable() {
//            public void run() {
//                session.fireUntilHalt();
//            }
//        }).start();
//
//        for (int x = 0; x < 10000; x++) {
//            entryPoint.insert(new StockTick(x, "RHT", 10, 10+x));
//            Thread.yield();
//        }
//
//        session.halt();
//        session.fireAllRules();
//
//        // facts are being expired
//        verify( wmel, atLeastOnce() ).objectRetracted( any( ObjectRetractedEvent.class ) );
//    }


}
