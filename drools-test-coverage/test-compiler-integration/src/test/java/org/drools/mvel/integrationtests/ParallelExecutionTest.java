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

import org.drools.base.base.ClassObjectType;
import org.drools.base.rule.EntryPointId;
import org.drools.core.ClockType;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.reteoo.CompositePartitionAwareObjectSinkAdapter;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.compiler.util.debug.DebugList;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.conf.ParallelExecutionOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ParallelExecutionTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ParallelExecutionTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test(timeout = 40000L)
    public void test() {
        StringBuilder sb = new StringBuilder( 400 );
        sb.append( "global java.util.List list;\n" );
        for (int i = 0; i < 10; i++) {
            sb.append( getRule( i, "" ) );
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );

        EntryPointNode epn = ((InternalKnowledgeBase) kbase).getRete().getEntryPointNode(EntryPointId.DEFAULT);
        ObjectTypeNode otn = epn.getObjectTypeNodes().get( new ClassObjectType( Integer.class ) );
        assertThat(((CompositePartitionAwareObjectSinkAdapter) otn.getObjectSinkPropagator()).isHashed()).isTrue();

        KieSession ksession = kbase.newKieSession();
        assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

        List<Integer> list = new DebugList<Integer>();
        ksession.setGlobal( "list", list );

        for (int i = 0; i < 10; i++) {
            ksession.insert( i );
            ksession.insert( "" + i );
        }

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(10);
    }

    @Test(timeout = 40000L)
    public void testWithInsertions() {
        StringBuilder sb = new StringBuilder( 4000 );
        sb.append( "global java.util.List list;\n" );
        int ruleNr = 200;

        for (int i = 0; i < ruleNr; i++) {
            sb.append( getRule( i, "insert( $i + 10 );\ninsert( \"\" + ($i + 10) );\n" ) );
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );
        KieSession ksession = kbase.newKieSession();

        assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

        List<Integer> list = new DebugList<Integer>();
        ksession.setGlobal( "list", list );

        for (int i = 0; i < 10; i++) {
            ksession.insert( i );
            ksession.insert( "" + i );
        }

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(ruleNr);
    }

    @Test(timeout = 40000L)
    public void testWithDeletes() {
        StringBuilder sb = new StringBuilder( 400 );
        sb.append( "global java.util.List list;\n" );
        for (int i = 1; i < 11; i++) {
            sb.append( getRule( i, "delete( $i );\n" ) );
        }
        for (int i = 1; i < 11; i++) {
            sb.append( getNotRule( i ) );
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );
        KieSession ksession = kbase.newKieSession();

        assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

        List<Integer> list = new DebugList<Integer>();
        ksession.setGlobal( "list", list );

        for (int i = 1; i < 11; i++) {
            ksession.insert( i );
            ksession.insert( "" + i );
        }

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(20);
    }

    @Test(timeout = 40000L)
    public void testWithAsyncInsertions() {
        StringBuilder sb = new StringBuilder( 4000 );
        sb.append( "global java.util.List list;\n" );
        int ruleNr = 200;

        for (int i = 0; i < ruleNr; i++) {
            sb.append( getRule( i, "insertAsync( $i + 10 );\ninsertAsync( \"\" + ($i + 10) );\n" ) );
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );
        KieSession ksession = kbase.newKieSession();

        assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

        StatefulKnowledgeSessionImpl session = (StatefulKnowledgeSessionImpl) ksession;

        List<Integer> list = new DebugList<Integer>();
        ksession.setGlobal( "list", list );

        for (int i = 0; i < 10; i++) {
            session.insertAsync( i );
            session.insertAsync( "" + i );
        }

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(ruleNr);
    }

    private String getRule(int i, String rhs) {
        return getRule( i, rhs, "" );
    }

    private String getRule(int i, String rhs, String attributes) {
        return  "rule R" + i + " " + attributes + "when\n" +
                "    $i : Integer( intValue == " + i + " )" +
                "    String( toString == $i.toString )\n" +
                "then\n" +
                "    list.add($i);\n" +
                rhs +
                "end\n";
    }

    private String getNotRule(int i) {
        return  "rule Rnot" + i + " when\n" +
                "    String( toString == \"" + i + "\" )\n" +
                "    not Integer( intValue == " + i + " )" +
                "then\n" +
                "    list.add(" + -i + ");\n" +
                "end\n";
    }

    @Test(timeout = 40000L)
    public void testFireUntilHalt() {
        StringBuilder sb = new StringBuilder( 400 );
        sb.append( "global java.util.List list;\n" );
        for (int i = 0; i < 10; i++) {
            sb.append( getRule( i, "" ) );
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );
        KieSession ksession = kbase.newKieSession();

        assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

        CountDownLatch done = new CountDownLatch(1);

        DebugList<Integer> list = new DebugList<Integer>();
        list.onItemAdded = ( l -> { if (l.size() == 10) {
            ksession.halt();
            done.countDown();
        }} );
        ksession.setGlobal( "list", list );

        new Thread(ksession::fireUntilHalt).start();
        try {
            for (int i = 0; i < 10; i++) {
                ksession.insert( i );
                ksession.insert( "" + i );
            }

            try {
                done.await();
            } catch (InterruptedException e) {
                throw new RuntimeException( e );
            }

            assertThat(list.size()).isEqualTo(10);
        } finally {
            ksession.halt();
            ksession.dispose();
        }
    }

    @Test(timeout = 40000L)
    @Ignore("this test is failing on Jenkins but not locally, we need to figure out why")
    public void testFireUntilHalt2() {
        int rulesNr = 4;
        int factsNr = 1;
        int fireNr = rulesNr * factsNr;

        String drl = "import " + A.class.getCanonicalName() + ";\n" +
                     "import " + B.class.getCanonicalName() + ";\n" +
                     "global java.util.concurrent.atomic.AtomicInteger counter\n" +
                     "global java.util.concurrent.CountDownLatch done\n" +
                     "global java.util.List list;\n";

        for (int i = 0; i < rulesNr; i++) {
            drl += getFireUntilHaltRule(fireNr, i);
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, drl);
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );

        for (int loop = 0; loop < 10; loop++) {
            System.out.println("Starting loop " + loop);
            KieSession ksession = kbase.newKieSession();
            assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

            CountDownLatch done = new CountDownLatch( 1 );
            ksession.setGlobal( "done", done );

            AtomicInteger counter = new AtomicInteger( 0 );
            ksession.setGlobal( "counter", counter );

            List<String> list = new DebugList<String>();
            ksession.setGlobal( "list", list );

            new Thread(ksession::fireUntilHalt).start();
            try {
                A a = new A( rulesNr + 1 );
                ksession.insert( a );

                for ( int i = 0; i < factsNr; i++ ) {
                    ksession.insert( new B( rulesNr + i + 3 ) );
                }

                try {
                    done.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException( e );
                }

                assertThat(counter.get()).isEqualTo(fireNr);
            } finally {
                ksession.halt();
                ksession.dispose();
            }

            System.out.println("Loop " + loop + " terminated");
        }
    }

    private String getFireUntilHaltRule(int fireNr, int i) {
        return  "rule R" + i + " when\n" +
                "  A( $a : value > " + i + ")\n" +
                "  B( $b : value > $a )\n" +
                "then\n" +
                "  list.add( drools.getRule().getName() );" +
                "  if (counter.incrementAndGet() == " + fireNr + " ) {\n" +
                "    drools.halt();\n" +
                "    done.countDown();\n" +
                "  }\n" +
                "end\n";
    }

    public static class A {
        private int value;

        public A( int value ) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue( int value ) {
            this.value = value;
        }
    }

    public static class B {
        private int value;

        public B( int value ) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue( int value ) {
            this.value = value;
        }
    }

    @Test(timeout = 40000L)
    public void testFireUntilHaltWithAsyncInsert() {
        StringBuilder sb = new StringBuilder( 400 );
        sb.append( "global java.util.List list;\n" );
        for (int i = 0; i < 10; i++) {
            sb.append( getRule( i, "" ) );
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );
        KieSession ksession = kbase.newKieSession();

        assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

        StatefulKnowledgeSessionImpl session = (StatefulKnowledgeSessionImpl) ksession;

        CountDownLatch done = new CountDownLatch(1);

        DebugList<Integer> list = new DebugList<Integer>();
        list.onItemAdded = ( l -> { if (l.size() == 10) {
            ksession.halt();
            done.countDown();
        }} );
        ksession.setGlobal( "list", list );

        new Thread(ksession::fireUntilHalt).start();
        try {
            for (int i = 0; i < 10; i++) {
                session.insertAsync( i );
                session.insertAsync( "" + i );
            }

            try {
                done.await();
            } catch (InterruptedException e) {
                throw new RuntimeException( e );
            }

            assertThat(list.size()).isEqualTo(10);
        } finally {
            ksession.halt();
            ksession.dispose();
        }
    }

    @Test(timeout = 40000L)
    public void testDisableParallelismOnSinglePartition() {
        String drl =
                "rule R1 when\n" +
                "    $i : Integer( this == 4 )" +
                "    String( length > $i )\n" +
                "then end \n" +
                "rule R2 when\n" +
                "    $i : Integer( this == 4 )" +
                "    String( length == $i )\n" +
                "then end \n" +
                "rule R3 when\n" +
                "    $i : Integer( this == 4 )" +
                "    String( length < $i )\n" +
                "then end \n";

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, drl);
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );
        KieSession ksession = kbase.newKieSession();

        InternalWorkingMemory session = (InternalWorkingMemory) ksession;

        // since there is only one partition the multithread evaluation should be disabled and run with the DefaultAgenda
        assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isFalse();
    }

    @Test(timeout = 40000L)
    public void testEventsExpiration() {
        StringBuilder sb = new StringBuilder( 400 );
        sb.append( "global java.util.List list;\n" );
        sb.append( "import " + MyEvent.class.getCanonicalName() + ";\n" );
        sb.append( "declare MyEvent @role( event ) @expires( 20ms ) @timestamp( timestamp ) end\n" );
        for (int i = 0; i < 10; i++) {
            sb.append( getRuleWithEvent( i ) );
        }

        KieSessionConfiguration sessionConfig = RuleBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieBaseTestConfiguration streamConfig = TestParametersUtil.getStreamInstanceOf(kieBaseTestConfiguration);
        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", streamConfig, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, streamConfig, ParallelExecutionOption.FULLY_PARALLEL );
        KieSession ksession = kbase.newKieSession(sessionConfig, null);

        assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

        PseudoClockScheduler sessionClock = ksession.getSessionClock();
        sessionClock.setStartupTime(0);

        List<Integer> list = new DebugList<Integer>();
        ksession.setGlobal( "list", list );

        for (int i = 0; i < 10; i++) {
            ksession.insert( new MyEvent( i, i*2L ) );
        }

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(10);
        assertThat(ksession.getFactCount()).isEqualTo(10L);

        sessionClock.advanceTime( 29, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertThat(ksession.getFactCount()).isEqualTo(5L);

        sessionClock.advanceTime( 12, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertThat(ksession.getFactCount()).isEqualTo(0L);
    }

    @Test(timeout = 40000L)
    public void testImmediateEventsExpiration() {
        StringBuilder sb = new StringBuilder( 400 );
        sb.append( "global java.util.List list;\n" );
        sb.append( "import " + MyEvent.class.getCanonicalName() + ";\n" );
        sb.append( "declare MyEvent @role( event ) @expires( 1ms ) @timestamp( timestamp ) end\n" );
        for (int i = 0; i < 10; i++) {
            sb.append( getRuleWithEvent( i ) );
        }

        KieSessionConfiguration sessionConfig = RuleBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieBaseTestConfiguration streamConfig = TestParametersUtil.getStreamInstanceOf(kieBaseTestConfiguration);
        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", streamConfig, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, streamConfig, ParallelExecutionOption.FULLY_PARALLEL );
        KieSession ksession = kbase.newKieSession(sessionConfig, null);

        PseudoClockScheduler sessionClock = ksession.getSessionClock();
        sessionClock.setStartupTime(0);

        assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

        List<Integer> list = new DebugList<>();
        ksession.setGlobal( "list", list );

        int eventNr = 10;
        for (int i = 0; i < eventNr; i++) {
            ksession.insert( new MyEvent( i, i ) );
            sessionClock.advanceTime(1, TimeUnit.MILLISECONDS);
        }
        sessionClock.advanceTime(1, TimeUnit.MILLISECONDS);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(eventNr);

        // check that all events have been expired
        assertThat(ksession.getFactHandles()).isEmpty();
    }

     public static class MyEvent {
        private final int id;
        private final long timestamp;

        public MyEvent( int id, long timestamp ) {
            this.id = id;
            this.timestamp = timestamp;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "MyEvent[" + id + "]";
        }
    }

    private String getRuleWithEvent(int i) {
        return  "rule R" + i + " when\n" +
                "    $i : MyEvent( id == " + i + " )" +
                "then\n" +
                "    list.add($i);\n" +
                "end\n";
    }

    private String getRuleWithEventForExpiration(int i) {
        return  "rule R" + i + " when\n" +
                "    $i : MyEvent( id == " + i + " )\n" +
                "then\n" +
                "    list.add($i);\n" +
                "    insert(" + i + ");\n" +
                "end\n" +
                "rule R" + i + "not when\n" +
                "    Integer( this == " + i + " )\n" +
                "    not MyEvent( id == " + i + " )\n" +
                "then\n" +
                "    list.add(" + i + ");\n" +
                "end\n";
    }

    @Test(timeout = 40000L)
    public void testFireUntilHaltWithExpiration() {
        StringBuilder sb = new StringBuilder( 400 );
        sb.append( "global java.util.List list;\n" );
        sb.append( "import " + MyEvent.class.getCanonicalName() + ";\n" );
        sb.append( "declare MyEvent @role( event ) @expires( 20ms ) @timestamp( timestamp ) end\n" );
        for (int i = 0; i < 5; i++) {
            sb.append( getRuleWithEventForExpiration( i ) );
        }

        // See DROOLS-6352 : To avoid bias in CompositePartitionAwareObjectSinkAdapter.partitionedPropagators by artificial repetition of rule pattern
        sb.append("rule R_ex1\n when MyEvent(id == 100)\n then\n end\n");

        for (int i = 5; i < 10; i++) {
            sb.append( getRuleWithEventForExpiration( i ) );
        }

        KieSessionConfiguration sessionConfig = RuleBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieBaseTestConfiguration streamConfig = TestParametersUtil.getStreamInstanceOf(kieBaseTestConfiguration);
        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", streamConfig, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, streamConfig, ParallelExecutionOption.FULLY_PARALLEL );
        KieSession ksession = kbase.newKieSession(sessionConfig, null);

        assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

        PseudoClockScheduler sessionClock = ksession.getSessionClock();
        sessionClock.setStartupTime(0);

        DebugList<Integer> list = new DebugList<Integer>();
        CountDownLatch done1 = new CountDownLatch(1);
        list.onItemAdded = ( l -> { if (l.size() == 10) {
            done1.countDown();
        }} );
        ksession.setGlobal( "list", list );

        for (int i = 0; i < 10; i++) {
            ksession.insert( new MyEvent( i, i*2L ) );
        }

        new Thread(ksession::fireUntilHalt).start();
        try {
            try {
                done1.await();
            } catch (InterruptedException e) {
                throw new RuntimeException( e );
            }

            assertThat(list.size()).isEqualTo(10);
            list.clear();

            CountDownLatch done2 = new CountDownLatch(1);
            list.onItemAdded = ( l -> { if (l.size() == 5) {
                done2.countDown();
            }} );

            ksession.insert( 1 );
            sessionClock.advanceTime( 29, TimeUnit.MILLISECONDS );

            try {
                done2.await();
            } catch (InterruptedException e) {
                throw new RuntimeException( e );
            }

            assertThat(list.size()).isEqualTo(5);
            list.clear();

            CountDownLatch done3 = new CountDownLatch(1);
            list.onItemAdded = ( l -> { if (l.size() == 5) {
                done3.countDown();
            }} );

            sessionClock.advanceTime( 12, TimeUnit.MILLISECONDS );
            try {
                done3.await();
            } catch (InterruptedException e) {
                throw new RuntimeException( e );
            }

            assertThat(list.size()).isEqualTo(5);
        } finally {
            ksession.halt();
            ksession.dispose();
        }
    }

    @Test(timeout = 40000L)
    @Ignore("this test is failing on Jenkins but not locally, we need to figure out why")
    public void testFireUntilHaltWithExpiration2() throws InterruptedException {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                "import " + B.class.getCanonicalName() + "\n" +
                "declare A @role( event ) @expires(11ms) end\n" +
                "declare B @role( event ) @expires(11ms) end\n" +
                "global java.util.concurrent.atomic.AtomicInteger counter;\n" +
                "global java.util.concurrent.CountDownLatch fireLatch;\n" +
                "rule R0 when\n" +
                "  $A: A( $Aid : value > 0 )\n" +
                "  $B: B( ($Bid: value <= $Aid) && (value > ($Aid - 1 )))\n" +
                "then\n" +
                "  counter.incrementAndGet();\n" +
                "  fireLatch.countDown();" +
                "end\n" +
                "rule R1 when\n" +
                "  $A: A( $Aid: value > 1 )\n" +
                "  $B: B( ($Bid: value <= $Aid) && (value > ($Aid - 1 )))\n" +
                "then\n" +
                "  counter.incrementAndGet();\n" +
                "  fireLatch.countDown();" +
                "end\n" +
                "rule R2 when\n" +
                "  $A: A( $Aid: value > 2 )\n" +
                "  $B: B( ($Bid: value <= $Aid) && (value > ($Aid - 1 )))\n" +
                "then\n" +
                "  counter.incrementAndGet();\n" +
                "  fireLatch.countDown();" +
                "end\n" +
                "rule R3 when\n" +
                "  $A: A( $Aid: value > 3 )\n" +
                "  $B: B( ($Bid: value <= $Aid) && (value > ($Aid - 1 )))\n" +
                "then\n" +
                "  counter.incrementAndGet();\n" +
                "  fireLatch.countDown();" +
                "end";

        KieSessionConfiguration sessionConfig = RuleBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieBaseTestConfiguration streamConfig = TestParametersUtil.getStreamInstanceOf(kieBaseTestConfiguration);
        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", streamConfig, drl);
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, streamConfig, ParallelExecutionOption.FULLY_PARALLEL );
        KieSession ksession = kbase.newKieSession(sessionConfig, null);

        try {
            assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

            PseudoClockScheduler sessionClock = ksession.getSessionClock();
            sessionClock.setStartupTime( 0 );

            AtomicInteger counter = new AtomicInteger( 0 );
            ksession.setGlobal( "counter", counter );

            new Thread( () -> ksession.fireUntilHalt() ).start();

            int eventsNr = 5;
            final CountDownLatch fireLatch = new CountDownLatch(eventsNr * 4);
            ksession.setGlobal("fireLatch", fireLatch);
            for ( int i = 0; i < eventsNr; i++ ) {
                ksession.insert( new A( i + 4 ) );
                ksession.insert( new B( i + 4 ) );
                sessionClock.advanceTime( 10, TimeUnit.MILLISECONDS );
            }

            fireLatch.await();
            assertThat(counter.get()).isEqualTo(eventsNr * 4);
        } finally {
            ksession.halt();
            ksession.dispose();
        }
    }

    @Test(timeout = 40000L)
    public void testWithUpdates() {
        StringBuilder sb = new StringBuilder( 400 );
        sb.append( "global java.util.List list;\n" );
        for (int i = 0; i < 10; i++) {
            sb.append( getRule( i, "" ) );
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );
        KieSession ksession = kbase.newKieSession();

        assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

        List<Integer> list = new DebugList<Integer>();
        ksession.setGlobal( "list", list );

        FactHandle[] fhs = new FactHandle[10];

        for (int i = 0; i < 10; i++) {
            fhs[i] = ksession.insert( i );
            ksession.insert( "" + i );
        }

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(10);

        list.clear();

        for (int i = 0; i < 10; i++) {
            ksession.update( fhs[i], i );
        }

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(10);
    }

    @Test(timeout = 40000L)
    public void testDisableParallelismWithAgendaGroups() {
        StringBuilder sb = new StringBuilder( 400 );
        sb.append( "global java.util.List list;\n" );
        sb.append( "rule first\n" +
                   "when\n" +
                   "then\n" +
                   "    drools.getKnowledgeRuntime().getAgenda().getAgendaGroup(\"agenda\").setFocus();\n" +
                   "end\n" );
        for (int i = 0; i < 10; i++) {
            sb.append( getRule( i, "", "agenda-group \"agenda\"" ) );
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );

        KieSession ksession = kbase.newKieSession();

        // multithread evaluation is not allowed when using agenda-groups
        assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isFalse();

        List<Integer> list = new DebugList<Integer>();
        ksession.setGlobal( "list", list );

        for (int i = 0; i < 10; i++) {
            ksession.insert( i );
            ksession.insert( "" + i );
        }

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(10);
    }

    @Test(timeout = 40000L)
    public void testDisableParallelismWithSalience() {
        StringBuilder sb = new StringBuilder( 400 );
        sb.append( "global java.util.List list;\n" );
        for (int i = 0; i < 10; i++) {
            sb.append( getRule( i, "", "salience " + i ) );
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );

        KieSession ksession = kbase.newKieSession();

        // multithread evaluation is not allowed when using salience
        assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isFalse();

        List<Integer> list = new DebugList<Integer>();
        ksession.setGlobal( "list", list );

        for (int i = 0; i < 10; i++) {
            ksession.insert( i );
            ksession.insert( "" + i );
        }

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(10);
        assertThat(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)).isEqualTo(list);
    }

    @Test(timeout = 40000L)
    public void testMultipleParallelKieSessionsWithInsertions() throws InterruptedException, ExecutionException, TimeoutException {
        final int NUMBER_OF_PARALLEL_SESSIONS = 5;

        /* Create KIE base */
        StringBuilder sb = new StringBuilder();
        sb.append("global java.util.List list;\n");
        final int ruleNr = 200;

        for (int i = 0; i < ruleNr; i++) {
            sb.append(getRule(i, "insert( $i + 10 );\ninsert( \"\" + ($i + 10) );\n"));
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kBase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );

        /* Create parallel tasks */
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PARALLEL_SESSIONS; i++) {
            tasks.add(getMultipleParallelKieSessionsWithInsertionsCallable(kBase, ruleNr));
        }

        runTasksInParallel(tasks);
    }

    private Callable<Void> getMultipleParallelKieSessionsWithInsertionsCallable(KieBase kBase, int ruleNr) {
        return new Callable<Void>() {
            @Override public Void call() {
                KieSession ksession = kBase.newKieSession();
                assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

                List<Integer> list = new DebugList<Integer>();
                ksession.setGlobal( "list", list );

                insertFacts(ksession, 10);

                ksession.fireAllRules();
                assertThat(list.size()).isEqualTo(ruleNr);

                return null;
            }
        };
    }

    @Test(timeout = 40000L)
    public void testMultipleParallelKieSessionsWithUpdates() throws InterruptedException, ExecutionException, TimeoutException {
        final int NUMBER_OF_PARALLEL_SESSIONS = 5;

        /* Create KIE base */
        StringBuilder sb = new StringBuilder( 400 );
        sb.append( "global java.util.List list;\n" );
        for (int i = 0; i < 10; i++) {
            sb.append( getRule( i, "" ) );
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kBase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );

        /* Create parallel tasks */
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PARALLEL_SESSIONS; i++) {
            tasks.add(getMultipleParallelKieSessionsWithUpdatesCallable(kBase));
        }

        /* Run tasks in parallel */
        runTasksInParallel(tasks);
    }

    private Callable<Void> getMultipleParallelKieSessionsWithUpdatesCallable(KieBase kBase) {
        return new Callable<Void>() {
            @Override public Void call() {
                KieSession ksession = kBase.newKieSession();
                assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).as("Parallel agenda has to be enabled").isTrue();

                List<Integer> list = new DebugList<Integer>();
                ksession.setGlobal( "list", list );

                FactHandle[] fhs = new FactHandle[10];
                fhs = insertFacts(ksession, 10);

                ksession.fireAllRules();
                assertThat(list.size()).isEqualTo(10);

                list.clear();

                for (int i = 0; i < 10; i++) {
                    ksession.update( fhs[i], i );
                }

                ksession.fireAllRules();
                assertThat(list.size()).isEqualTo(10);

                return null;
            }
        };
    }

    @Test(timeout = 40000L)
    public void testMultipleParallelKieSessionsWithDeletes() throws InterruptedException, ExecutionException, TimeoutException {
        final int NUMBER_OF_PARALLEL_SESSIONS = 5;

        /* Create KIE base */
        StringBuilder sb = new StringBuilder(400);
        sb.append("global java.util.List list;\n");
        for (int i = 1; i < 11; i++) {
            sb.append(getRule(i, "delete( $i );\n"));
        }
        for (int i = 1; i < 11; i++) {
            sb.append(getNotRule(i));
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PARALLEL_SESSIONS; i++) {
            tasks.add(getMultipleParallelKieSessionsWithDeletesCallable(kbase));
        }

        /* Run tasks in parallel */
        runTasksInParallel(tasks);
    }

    private Callable<Void> getMultipleParallelKieSessionsWithDeletesCallable(KieBase kbase) {
        return new Callable<Void>() {
            @Override public Void call() {
                KieSession ksession = kbase.newKieSession();
                assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

                List<Integer> list = new DebugList<Integer>();
                ksession.setGlobal( "list", list );

                insertFacts(ksession, 11);
                ksession.fireAllRules();

                assertThat(ksession.getObjects()).isEmpty();
                assertThat(list.size()).isEqualTo(20);

                return null;
            }
        };
    }

    @Test(timeout = 40000L)
    public void testMultipleParallelKieSessionsFireUntilHalt() throws InterruptedException, ExecutionException, TimeoutException {
        final int NUMBER_OF_PARALLEL_SESSIONS = 5;

        /* Create KIE base */
        StringBuilder sb = new StringBuilder(400);
        sb.append("global java.util.List list;\n");
        for (int i = 0; i < 10; i++) {
            sb.append(getRule(i, ""));
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PARALLEL_SESSIONS; i++) {
            tasks.add(getMultipleParallelKieSessionsFireUntilHaltCallable(kbase, false));
        }

        /* Run tasks in parallel */
        runTasksInParallel(tasks);
    }

    private Callable<Void> getMultipleParallelKieSessionsFireUntilHaltCallable(KieBase kBase, boolean asyncInsert) {
        return () -> {
            KieSession ksession = kBase.newKieSession();
            assertThat(((InternalWorkingMemory) ksession).getAgenda().isParallelAgenda()).isTrue();

            CountDownLatch done = new CountDownLatch(1);

            DebugList<Integer> list = new DebugList<Integer>();
            list.onItemAdded = (l -> {
                if (l.size() == 10) {
                    ksession.halt();
                    done.countDown();
                }
            });
            ksession.setGlobal("list", list);

            new Thread(ksession::fireUntilHalt).start();
            if (asyncInsert) {
                StatefulKnowledgeSessionImpl session = (StatefulKnowledgeSessionImpl) ksession;
                for (int i = 0; i < 10; i++) {
                    session.insertAsync(i);
                    session.insertAsync("" + String.valueOf(i));
                }
            } else {
                insertFacts(ksession, 10);
            }

            try {
                done.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            assertThat(list.size()).isEqualTo(10);

            return null;
        };
    }

    @Test(timeout = 40000L)
    public void testMultipleParallelKieSessionsFireUntilHaltWithAsyncInsert() throws InterruptedException, ExecutionException, TimeoutException {
        final int NUMBER_OF_PARALLEL_SESSIONS = 5;

        /* Create KIE base */
        StringBuilder sb = new StringBuilder(400);
        sb.append("global java.util.List list;\n");
        for (int i = 0; i < 10; i++) {
            sb.append(getRule(i, ""));
        }

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("test", kieBaseTestConfiguration, sb.toString());
        final KieBase kbase = KieBaseUtil.newKieBaseFromKieModuleWithAdditionalOptions(kieModule, kieBaseTestConfiguration, ParallelExecutionOption.FULLY_PARALLEL );

        /* Create parallel tasks */
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PARALLEL_SESSIONS; i++) {
            tasks.add(getMultipleParallelKieSessionsFireUntilHaltCallable(kbase, true));
        }

        /* Run tasks in parallel */
        runTasksInParallel(tasks);
    }

    private FactHandle[] insertFacts(KieSession ksession, int n) {
        FactHandle[] fhs = new FactHandle[n];
        for (int i = 0; i < n; i++) {
            fhs[i] = ksession.insert(i);
            ksession.insert(String.valueOf(i));
        }

        return fhs;
    }

    private void runTasksInParallel(List<Callable<Void>> tasks) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(tasks.size());
        try {
            List<Future<Void>> futures = executorService.invokeAll(tasks);
            assertThat(futures.size()).isEqualTo(tasks.size());
        } finally {
            executorService.shutdownNow();
        }
    }
}
