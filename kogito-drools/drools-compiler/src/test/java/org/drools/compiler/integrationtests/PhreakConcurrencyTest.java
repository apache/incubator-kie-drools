package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.StockTick;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.SegmentMemory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;

@Ignore
public class PhreakConcurrencyTest extends CommonTestMethodBase {

    private Executor executor;

    private static RuleEngineOption wasRunningPhreak;

    @BeforeClass
    public static void setPhreak() {
        wasRunningPhreak = phreak;
        phreak = RuleEngineOption.PHREAK;
    }

    @AfterClass
    public static void unsetPhreak() {
        phreak = wasRunningPhreak;
    }

    @Before
    public void setUp() {
        executor = Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });
    }

    @Test
    public void testMultipleConcurrentEPs() {
        final boolean PARALLEL = true;
        final int EP_NR = 10;

        StringBuilder sb = new StringBuilder();

        sb.append("import org.drools.compiler.StockTick;\n");
        for (int i = 0; i < EP_NR; i++) {
            sb.append("global java.util.List results").append(i).append(";\n");
        }
        sb.append("declare StockTick\n" +
                  "    @role( event )\n" +
                  "end\n");
        for (int i = 0; i < EP_NR; i++) {
            sb.append("rule \"R" + i +"\"\n" +
                      "when\n" +
                      "    $name : String( this.startsWith(\"A\") )\n" +
                      "    $st : StockTick( company == $name, price > 10 ) from entry-point EP" + i +"\n" +
                      "then\n" +
                      "    results" + i +".add( $st );\n" +
                      "end\n");
        }

        KnowledgeBase kbase = loadKnowledgeBaseFromString(sb.toString());
        KieSession ksession = kbase.newStatefulKnowledgeSession();

        boolean success = true;
        if (PARALLEL) {

            CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);
            for (int i = 0; i < EP_NR; i++) {
                ecs.submit(new EPManipulator(ksession, i));
            }

            for (int i = 0; i < EP_NR; i++) {
                try {
                    success = ecs.take().get() && success;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        } else {

            for (int i = 0; i < EP_NR; i++) {
                try {
                    success = new EPManipulator(ksession, i).call() && success;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        }

        assertTrue(success);

        assertEquals(EP_NR, ksession.fireAllRules());

        for (int i = 0; i < EP_NR; i++) {
            assertEquals(1, ((List) ksession.getGlobal("results" + i)).size());
        }

        ksession.dispose();
    }

    public static class EPManipulator implements Callable<Boolean> {

        private final KieSession ksession;
        private final int index;

        public EPManipulator(KieSession ksession, int index) {
            this.ksession = ksession;
            this.index = index;
        }

        public Boolean call() throws Exception {
            List results = new ArrayList();
            ksession.setGlobal("results" + index, results);

            ksession.insert("ACME" + index);

            EntryPoint ep = ksession.getEntryPoint("EP" + index);

            for (int i = 0; i < 12; i++) {
                ep.insert(new StockTick(1, "ACME" + index, i - 50));
                ep.insert(new StockTick(2, "DROO" + index, i));
                ep.insert(new StockTick(3, "ACME" + index, i));
            }

            return true;
        }
    }

    @Test(timeout = 10000)
    public void testMultipleConcurrentEPs2() {
        String str = "global java.util.List results\n" +
                     "\n" +
                     "rule \"R0\" when\n" +
                     "    $s : String( ) from entry-point EP0\n" +
                     "    $i : Integer( toString().equals($s) ) from entry-point EP1\n" +
                     "    $l : Long( intValue() == $i ) from entry-point EP2\n" +
                     "then\n" +
                     "    results.add( $s );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"R1\" when\n" +
                     "    $s : String( ) from entry-point EP1\n" +
                     "    $i : Integer( toString().equals($s) ) from entry-point EP2\n" +
                     "    $l : Long( intValue() == $i ) from entry-point EP0\n" +
                     "then\n" +
                     "    results.add( $s );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"R2\" when\n" +
                     "    $s : String( ) from entry-point EP2\n" +
                     "    $i : Integer( toString().equals($s) ) from entry-point EP0\n" +
                     "    $l : Long( intValue() == $i ) from entry-point EP1\n" +
                     "then\n" +
                     "    results.add( $s );\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);

        boolean success = true;
        CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);
        for (int i = 0; i < 3; i++) {
            ecs.submit(new EPManipulator2(ksession, i));
        }

        for (int i = 0; i < 3; i++) {
            try {
                success = ecs.take().get() && success;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        assertTrue(success);

        ksession.fireAllRules();
        System.out.println(results);
        assertEquals(3, results.size());
        for (String s : results) {
            assertEquals("2", s);
        }
    }

    public static class EPManipulator2 implements Callable<Boolean> {

        private final KieSession ksession;
        private final int index;

        public EPManipulator2(KieSession ksession, int index) {
            this.ksession = ksession;
            this.index = index;
        }

        public Boolean call() throws Exception {
            EntryPoint ep = ksession.getEntryPoint("EP" + index);

            FactHandle[] fhs = new FactHandle[15];

            for (int j = 0; j < 3; j++) {
                for (int i = 0; i < 5; i++) {
                    fhs[i * 3] = ep.insert("" + i);
                    fhs[i * 3 + 1] = ep.insert(new Long(i));
                    fhs[i * 3 + 2] = ep.insert(new Integer(i));
                }

                for (int i = 0; i < 5; i++) {
                    if (i == index+j) continue;
                    ep.delete(fhs[i * 3]);
                    ep.delete(fhs[i * 3 + 1]);
                    ep.delete(fhs[i * 3 + 2]);
                }
            }

            return true;
        }
    }

    private KieSession getKieSessionWith3Segments() {
        String str = "global java.util.List results\n" +
                     "rule R1 when\n" +
                     "   String( this == \"1\") from entry-point EP1\n" +
                     "   String( this == \"2\") from entry-point EP2\n" +
                     "   String( this == \"3\") from entry-point EP3\n" +
                     "   String( this == \"4\") from entry-point EP4\n" +
                     "   String( this == \"5\") from entry-point EP5\n" +
                     "   String( this == \"6\") from entry-point EP6\n" +
                     "   String( this == \"7\") from entry-point EP7\n" +
                     "   String( this == \"8\") from entry-point EP8\n" +
                     "   String( this == \"9\") from entry-point EP9\n" +
                     "then\n" +
                     "   results.add(\"R1\");\n" +
                     "end\n" +
                     "\n" +
                     "rule R2 when\n" +
                     "   String( this == \"1\") from entry-point EP1\n" +
                     "   String( this == \"2\") from entry-point EP2\n" +
                     "   String( this == \"3\") from entry-point EP3\n" +
                     "   eval(true)\n" +
                     "   String( this == \"4\") from entry-point EP4\n" +
                     "   String( this == \"5\") from entry-point EP5\n" +
                     "   String( this == \"6\") from entry-point EP6\n" +
                     "   String( this == \"7\") from entry-point EP7\n" +
                     "   String( this == \"8\") from entry-point EP8\n" +
                     "   String( this == \"9\") from entry-point EP9\n" +
                     "then\n" +
                     "   results.add(\"R2\");\n" +
                     "end\n" +
                     "\n" +
                     "rule R3 when\n" +
                     "   String( this == \"1\") from entry-point EP1\n" +
                     "   String( this == \"2\") from entry-point EP2\n" +
                     "   String( this == \"3\") from entry-point EP3\n" +
                     "   eval(true)\n" +
                     "   String( this == \"4\") from entry-point EP4\n" +
                     "   String( this == \"5\") from entry-point EP5\n" +
                     "   String( this == \"6\") from entry-point EP6\n" +
                     "   eval(true)\n" +
                     "   String( this == \"7\") from entry-point EP7\n" +
                     "   String( this == \"8\") from entry-point EP8\n" +
                     "   String( this == \"9\") from entry-point EP9\n" +
                     "then\n" +
                     "   results.add(\"R3\");\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        return kbase.newStatefulKnowledgeSession();
    }

    @Test
    public void testMultipleConcurrentEPs3() {
        final KieSession ksession = getKieSessionWith3Segments();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);

        EPManipulator3[] epManipulators = new EPManipulator3[9];
        for (int i = 0; i < 9; i++) {
            epManipulators[i] = new EPManipulator3(ksession, i+1);
        }

        for (int deleteIndex = 0; deleteIndex < 11; deleteIndex++) {
            boolean success = true;
            CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);
            for (int i = 0; i < 9; i++) {
                ecs.submit(epManipulators[i].setDeleteIndex(deleteIndex % 10));
            }

            for (int i = 1; i < 10; i++) {
                try {
                    success = ecs.take().get() && success;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            assertTrue(success);

            new Thread () {
                public void run () {
                    ksession.fireUntilHalt();
                }
            }.start ();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            ksession.halt();

            if (deleteIndex % 10 == 0) {
                assertEquals(3, results.size());
                assertTrue(results.containsAll(asList("R1", "R2", "R3")));
            } else {
                if (!results.isEmpty()) {
                    fail("Results should be empty with deleteIndex = " + deleteIndex + "; got " + results.size() + " items");
                }
            }

            results.clear();
        }
    }

    public static class EPManipulator3 implements Callable<Boolean> {

        private static final Random RANDOM = new Random(0);

        private final KieSession ksession;
        private final int index;

        private int deleteIndex;

        private FactHandle fh = null;

        public EPManipulator3(KieSession ksession, int index) {
            this.ksession = ksession;
            this.index = index;
        }

        public Boolean call() throws Exception {
            EntryPoint ep = ksession.getEntryPoint("EP" + index);

            InternalWorkingMemory wm = ((NamedEntryPoint)ep).getInternalWorkingMemory();
            ObjectTypeNode otn = ((NamedEntryPoint)ep).getEntryPointNode().getObjectTypeNodes().values().iterator().next();
            AlphaNode alpha = (AlphaNode)otn.getSinkPropagator().getSinks()[0];
            BetaNode beta = (BetaNode)alpha.getSinkPropagator().getSinks()[0];
            BetaMemory memory = (BetaMemory) wm.getNodeMemory(beta);
            memory.getSegmentMemory();

            for (int i = 0; i < 100; i++) {
                Thread.sleep(RANDOM.nextInt(100));
                if (fh == null) {
                    fh = ep.insert("" + index);
                } else {
                    if ( RANDOM.nextInt(100) < 70 ) {
                        ep.delete(fh);
                        fh = null;
                    } else {
                        ep.update(fh, "" + index);
                    }
                }
            }

            if (index == deleteIndex) {
                if (fh != null) {
                    ep.delete(fh);
                    fh = null;
                }
            } else if (fh == null) {
                fh = ep.insert("" + index);
            }

            return true;
        }

        public EPManipulator3 setDeleteIndex(int deleteIndex) {
            this.deleteIndex = deleteIndex;
            return this;
        }
    }

    @Test
    public void testMultipleConcurrentEPs4() {
        final KieSession ksession = getKieSessionWith3Segments();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);

        EPManipulator4[] epManipulators = new EPManipulator4[9];
        CyclicBarrier barrier = new CyclicBarrier(9, new SegmentChecker(epManipulators));
        for (int i = 0; i < 9; i++) {
            epManipulators[i] = new EPManipulator4(ksession, i+1, barrier);
        }

        new Thread () {
            public void run () {
                ksession.fireUntilHalt();
            }
        }.start();

        for (int deleteIndex = 0; deleteIndex < 11; deleteIndex++) {
            boolean success = true;
            CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);
            for (int i = 0; i < 9; i++) {
                ecs.submit(epManipulators[i].setDeleteIndex(deleteIndex % 10));
            }

            for (int i = 0; i < 9; i++) {
                try {
                    success = ecs.take().get() && success;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            assertTrue(success);
        }

        ksession.halt();
    }

    public static class EPManipulator4 implements Callable<Boolean> {

        private static final Random RANDOM = new Random();

        private final KieSession ksession;
        private final int index;
        private final CyclicBarrier barrier;

        private int deleteIndex;

        private FactHandle fh = null;

        private final EntryPoint ep;

        public EPManipulator4(KieSession ksession, int index, CyclicBarrier barrier) {
            this.ksession = ksession;
            this.index = index;
            this.barrier = barrier;
            this.ep = ksession.getEntryPoint("EP" + index);
        }

        public Boolean call() throws Exception {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(RANDOM.nextInt(100));
                if (fh == null) {
                    fh = ep.insert("" + index);
                } else {
                    if ( RANDOM.nextInt(100) < 70 ) {
                        ep.delete(fh);
                        fh = null;
                    } else {
                        ep.update(fh, "" + index);
                    }
                }

                if (i % 10 == 9) {
                    if (!barrier.isBroken()) {
                        barrier.await();
                    } else {
                        fail("This is not a bug in phreak synchronization, but a for some reason the barrier is broken, run the test again");
                        return false;
                    }
                }
            }

            if (index == deleteIndex) {
                if (fh != null) {
                    ep.delete(fh);
                    fh = null;
                }
            } else if (fh == null) {
                fh = ep.insert("" + index);
            }

            return true;
        }

        public EPManipulator4 setDeleteIndex(int deleteIndex) {
            this.deleteIndex = deleteIndex;
            return this;
        }

        public boolean isInserted() {
            return fh != null;
        }

        public CyclicBarrier getBarrier() {
            return barrier;
        }

        public EntryPoint getEntryPoiny() {
            return ep;
        }
    }

    public static class SegmentChecker implements Runnable {

        private final EPManipulator4[] epManipulators;

        private PathMemory[] pathMemories;

        public SegmentChecker(EPManipulator4[] epManipulators) {
            this.epManipulators = epManipulators;
        }

        @Override
        public void run() {
            System.out.println("Sync point");
            if (pathMemories == null) {
                initPathMemories();
            }

            String s = "";
            for (int i = 0; i < epManipulators.length; i++) {
                if (epManipulators[i].isInserted()) {
                    s = "1" + s;
                } else {
                    s = "0" + s;
                }
            }
            System.out.println("Inserted facts mask = " + s);

            // P0 = s0 + s1
            SegmentMemory s0 = pathMemories[0].getSegmentMemories()[0];
            SegmentMemory s1 = pathMemories[0].getSegmentMemories()[1];

            // P1 = s0 + s2 + s3
            SegmentMemory s2 = pathMemories[1].getSegmentMemories()[1];
            SegmentMemory s3 = pathMemories[1].getSegmentMemories()[2];

            // P2 = s0 + s2 + s4
            SegmentMemory s4 = pathMemories[2].getSegmentMemories()[2];

            long s0Mask = 1; // InitialFact is always present
            long s1Mask = 0;
            long s2Mask = 0;
            long s3Mask = 0;
            long s4Mask = 0;

            if (epManipulators[0].isInserted()) {
                s0Mask |= 2;
            }
            if (epManipulators[1].isInserted()) {
                s0Mask |= 4;
            }
            if (epManipulators[2].isInserted()) {
                s0Mask |= 8;
            }
            if (epManipulators[3].isInserted()) {
                s1Mask |= 1;
                s2Mask |= 2;
            }
            if (epManipulators[4].isInserted()) {
                s1Mask |= 2;
                s2Mask |= 4;
            }
            if (epManipulators[5].isInserted()) {
                s1Mask |= 4;
                s2Mask |= 8;
            }
            if (epManipulators[6].isInserted()) {
                s1Mask |= 8;
                s3Mask |= 1;
                s4Mask |= 2;
            }
            if (epManipulators[7].isInserted()) {
                s1Mask |= 16;
                s3Mask |= 2;
                s4Mask |= 4;
            }
            if (epManipulators[8].isInserted()) {
                s1Mask |= 32;
                s3Mask |= 4;
                s4Mask |= 8;
            }

            assertEquals(s0Mask, s0.getLinkedNodeMask());
            assertEquals(s1Mask, s1.getLinkedNodeMask());
            assertEquals(s2Mask, s2.getLinkedNodeMask());
            assertEquals(s3Mask, s3.getLinkedNodeMask());
            assertEquals(s4Mask, s4.getLinkedNodeMask());

            long p0Mask = 0;
            long p1Mask = 0;
            long p2Mask = 0;

            if ((s0Mask & 15) == 15) {
                assertTrue(s0.isSegmentLinked());
                p0Mask |= 1;
                p1Mask |= 1;
                p2Mask |= 1;
            } else {
                assertFalse(s0.isSegmentLinked());
            }

            if ((s1Mask & 63) == 63) {
                assertTrue(s1.isSegmentLinked());
                p0Mask |= 2;
            } else {
                assertFalse(s1.isSegmentLinked());
            }

            if ((s2Mask & 14) == 14) {
                assertTrue(s2.isSegmentLinked());
                p1Mask |= 2;
                p2Mask |= 2;
            } else {
                assertFalse(s2.isSegmentLinked());
            }

            if ((s3Mask & 7) == 7) {
                assertTrue(s3.isSegmentLinked());
                p1Mask |= 4;
            } else {
                assertFalse(s3.isSegmentLinked());
            }

            if ((s4Mask & 14) == 14) {
                assertTrue(s4.isSegmentLinked());
                p2Mask |= 4;
            } else {
                assertFalse(s4.isSegmentLinked());
            }

            assertEquals(p0Mask == 3, pathMemories[0].isRuleLinked());
            assertEquals(p1Mask == 7, pathMemories[1].isRuleLinked());
            assertEquals(p2Mask == 7, pathMemories[2].isRuleLinked());
        }

        private void initPathMemories() {
            pathMemories = new PathMemory[3];
            NamedEntryPoint ep = (NamedEntryPoint)epManipulators[8].getEntryPoiny();
            InternalWorkingMemory wm = ((NamedEntryPoint)ep).getInternalWorkingMemory();
            ObjectTypeNode otn = ((NamedEntryPoint)ep).getEntryPointNode().getObjectTypeNodes().values().iterator().next();
            AlphaNode alpha = (AlphaNode)otn.getSinkPropagator().getSinks()[0];
            ObjectSink[] sinks = alpha.getSinkPropagator().getSinks();
            for (int i = 0; i < sinks.length; i++) {
                BetaNode beta = (BetaNode)sinks[i];
                RuleTerminalNode rtn = (RuleTerminalNode)beta.getSinkPropagator().getSinks()[0];
                pathMemories[i] =  ( PathMemory ) wm.getNodeMemory(rtn);
            }
        }
    }


    @Test
    public void testFactLeak() throws InterruptedException {
        for ( int i = 0; i < 100; i++ ) {
            // repeat this 100 times, to try and better detect thread issues
            doFactLeak();
            System.gc();
            Thread.sleep(200);
        }
    }

    public void doFactLeak() throws InterruptedException {
        //DROOLS-131
        String drl = "package org.drools.test; \n" +
                     "global " + ConcurrentLinkedQueue.class.getCanonicalName() + " list; \n" +
                     //"global " + AtomicInteger.class.getCanonicalName() + "counter; \n" +
                     "" +
                     "rule Intx when\n" +
                     " $x : Integer() from entry-point \"x\" \n" +
                     "then\n" +
                     " list.add( $x );" +
                     "end";
        int N = 1100;

        KnowledgeBase kb = loadKnowledgeBaseFromString( drl );
        final StatefulKnowledgeSession ks = kb.newStatefulKnowledgeSession();
        ConcurrentLinkedQueue list = new ConcurrentLinkedQueue<Integer>();
        AtomicInteger counter = new AtomicInteger(0);
        ks.setGlobal( "list", list );
        //ks.setGlobal( "counter", counter );

        new Thread () {
            public void run () {
                ks.fireUntilHalt();
            }
        }.start ();

        for ( int j = 0; j < N; j++ ) {
            ks.getEntryPoint( "x" ).insert( new Integer( j ) );
        }

        int count = 0;
        while ( list.size() != N && count++ != 1000) {
            Thread.sleep( 200 );
        }

        ks.halt();
        if ( list.size() != N ) {
            for ( int j = 0; j < N; j++ ) {
                if ( !list.contains( new Integer( j ) ) ) {
                    System.out.println( "missed: " + j );
                }
            }
        }

        assertEquals( N, list.size() );
    }
}