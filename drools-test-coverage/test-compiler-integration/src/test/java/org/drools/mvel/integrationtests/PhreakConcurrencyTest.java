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
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.reteoo.BetaMemory;
import org.drools.kiesession.entrypoints.NamedEntryPoint;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.mvel.compiler.StockTick;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Ignore
public class PhreakConcurrencyTest extends CommonTestMethodBase {

    // This test already fails with standard-drl (probably not maintained) so not enhanced for exec-model

    @Test
    public void testMultipleConcurrentEPs() {
        final boolean PARALLEL = true;
        final int EP_NR = 10;

        StringBuilder sb = new StringBuilder();

        sb.append("import org.drools.mvel.compiler.StockTick;\n");
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

        KieBase kbase = loadKnowledgeBaseFromString(sb.toString());
        KieSession ksession = kbase.newKieSession();

        boolean success = true;
        if (PARALLEL) {
            final ExecutorService executor = Executors.newFixedThreadPool(EP_NR, r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            });

            try {
                CompletionService<Boolean> ecs = new ExecutorCompletionService<>(executor);
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
            } finally {
                executor.shutdownNow();
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

        assertThat(success).isTrue();
        assertThat(ksession.fireAllRules()).isEqualTo(EP_NR);

        for (int i = 0; i < EP_NR; i++) {
            assertThat(((List) ksession.getGlobal("results" + i)).size()).isEqualTo(1);
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

        KieBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newKieSession();

        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        boolean success = true;
        final ExecutorService executor = Executors.newFixedThreadPool(3, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        try {
            CompletionService<Boolean> ecs = new ExecutorCompletionService<>(executor);
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

            assertThat(success).isTrue();

            ksession.fireAllRules();
            System.out.println(results);
            assertThat(results.size()).isEqualTo(3);
            for (String s : results) {
                assertThat(s).isEqualTo("2");
            }
        } finally {
            executor.shutdownNow();
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

        KieBase kbase = loadKnowledgeBaseFromString(str);
        return kbase.newKieSession();
    }

    @Test
    public void testMultipleConcurrentEPs3() {
        final KieSession ksession = getKieSessionWith3Segments();

        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        EPManipulator3[] epManipulators = new EPManipulator3[9];
        for (int i = 0; i < 9; i++) {
            epManipulators[i] = new EPManipulator3(ksession, i+1);
        }

        for (int deleteIndex = 0; deleteIndex < 11; deleteIndex++) {
            boolean success = true;
            final ExecutorService executor = Executors.newFixedThreadPool(9, r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            });

            try {
                CompletionService<Boolean> ecs = new ExecutorCompletionService<>(executor);
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

                assertThat(success).isTrue();

                new Thread(ksession::fireUntilHalt).start ();
                try {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } finally {
                    ksession.halt();

                    if (deleteIndex % 10 == 0) {
                        assertThat(results.size()).isEqualTo(3);
                        assertThat(results.containsAll(asList("R1", "R2", "R3"))).isTrue();
                    } else {
                        if (!results.isEmpty()) {
                            fail("Results should be empty with deleteIndex = " + deleteIndex + "; got " + results.size() + " items");
                        }
                    }

                    results.clear();
                }
            } finally {
                executor.shutdownNow();
            }
        }

        ksession.dispose();
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

            ReteEvaluator reteEvaluator = ((NamedEntryPoint)ep).getReteEvaluator();
            ObjectTypeNode otn = ((NamedEntryPoint)ep).getEntryPointNode().getObjectTypeNodes().values().iterator().next();
            AlphaNode alpha = (AlphaNode)otn.getObjectSinkPropagator().getSinks()[0];
            BetaNode       beta   = (BetaNode)alpha.getObjectSinkPropagator().getSinks()[0];
            BetaMemory memory = (BetaMemory) reteEvaluator.getNodeMemory(beta);
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

        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        EPManipulator4[] epManipulators = new EPManipulator4[9];
        CyclicBarrier barrier = new CyclicBarrier(9, new SegmentChecker(epManipulators));
        for (int i = 0; i < 9; i++) {
            epManipulators[i] = new EPManipulator4(ksession, i+1, barrier);
        }

        new Thread(ksession::fireUntilHalt).start();
        try {
            for (int deleteIndex = 0; deleteIndex < 11; deleteIndex++) {
                boolean success = true;
                final ExecutorService executor = Executors.newFixedThreadPool(9, r -> {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                });

                try {
                    CompletionService<Boolean> ecs = new ExecutorCompletionService<>(executor);
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

                    assertThat(success).isTrue();
                } finally {
                    executor.shutdownNow();
                }
            }
        } finally {
            ksession.halt();
            ksession.dispose();
        }
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

            assertThat(s0.getLinkedNodeMask()).isEqualTo(s0Mask);
            assertThat(s1.getLinkedNodeMask()).isEqualTo(s1Mask);
            assertThat(s2.getLinkedNodeMask()).isEqualTo(s2Mask);
            assertThat(s3.getLinkedNodeMask()).isEqualTo(s3Mask);
            assertThat(s4.getLinkedNodeMask()).isEqualTo(s4Mask);

            long p0Mask = 0;
            long p1Mask = 0;
            long p2Mask = 0;

            if ((s0Mask & 15) == 15) {
                assertThat(s0.isSegmentLinked()).isTrue();
                p0Mask |= 1;
                p1Mask |= 1;
                p2Mask |= 1;
            } else {
                assertThat(s0.isSegmentLinked()).isFalse();
            }

            if ((s1Mask & 63) == 63) {
                assertThat(s1.isSegmentLinked()).isTrue();
                p0Mask |= 2;
            } else {
                assertThat(s1.isSegmentLinked()).isFalse();
            }

            if ((s2Mask & 14) == 14) {
                assertThat(s2.isSegmentLinked()).isTrue();
                p1Mask |= 2;
                p2Mask |= 2;
            } else {
                assertThat(s2.isSegmentLinked()).isFalse();
            }

            if ((s3Mask & 7) == 7) {
                assertThat(s3.isSegmentLinked()).isTrue();
                p1Mask |= 4;
            } else {
                assertThat(s3.isSegmentLinked()).isFalse();
            }

            if ((s4Mask & 14) == 14) {
                assertThat(s4.isSegmentLinked()).isTrue();
                p2Mask |= 4;
            } else {
                assertThat(s4.isSegmentLinked()).isFalse();
            }

            assertThat(pathMemories[0].isRuleLinked()).isEqualTo(p0Mask == 3);
            assertThat(pathMemories[1].isRuleLinked()).isEqualTo(p1Mask == 7);
            assertThat(pathMemories[2].isRuleLinked()).isEqualTo(p2Mask == 7);
        }

        private void initPathMemories() {
            pathMemories = new PathMemory[3];
            NamedEntryPoint ep = (NamedEntryPoint)epManipulators[8].getEntryPoiny();
            ReteEvaluator reteEvaluator = ep.getReteEvaluator();
            ObjectTypeNode otn = ep.getEntryPointNode().getObjectTypeNodes().values().iterator().next();
            AlphaNode alpha = (AlphaNode)otn.getObjectSinkPropagator().getSinks()[0];
            ObjectSink[] sinks = alpha.getObjectSinkPropagator().getSinks();
            for (int i = 0; i < sinks.length; i++) {
                BetaNode beta = (BetaNode)sinks[i];
                RuleTerminalNode rtn = (RuleTerminalNode)beta.getSinkPropagator().getSinks()[0];
                pathMemories[i] = reteEvaluator.getNodeMemory(rtn);
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

        KieBase kb = loadKnowledgeBaseFromString( drl );
        final KieSession ks = kb.newKieSession();
        ConcurrentLinkedQueue list = new ConcurrentLinkedQueue<Integer>();
        AtomicInteger counter = new AtomicInteger(0);
        ks.setGlobal( "list", list );
        //ks.setGlobal( "counter", counter );

        new Thread(ks::fireUntilHalt).start();
        try {
            for ( int j = 0; j < N; j++ ) {
                ks.getEntryPoint( "x" ).insert( new Integer( j ) );
            }

            int count = 0;
            while ( list.size() != N && count++ != 1000) {
                Thread.sleep( 200 );
            }
        } finally {
            ks.halt();

            if ( list.size() != N ) {
                for ( int j = 0; j < N; j++ ) {
                    if ( !list.contains( new Integer( j ) ) ) {
                        System.out.println( "missed: " + j );
                    }
                }
            }

            assertThat(list.size()).isEqualTo(N);

            ks.dispose();
        }
    }
}