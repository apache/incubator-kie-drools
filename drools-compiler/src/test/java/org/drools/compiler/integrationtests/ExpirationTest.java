/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.ClassObjectFilter;
import org.drools.core.ClockType;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;
import static org.kie.api.definition.type.Expires.Policy.TIME_SOFT;

public class ExpirationTest {

    @Test
    public void testAlpha() {
        String drl = "import " + A.class.getCanonicalName() + "\n" +
                     "declare A @role( event ) @expires(11ms) end\n" +
                     "global java.util.concurrent.atomic.AtomicInteger counter;\n" +
                     "rule R0 when\n" +
                     "  $a: A( $Aid: id > 0 )\n" +
                     "then\n" +
                     "  System.out.println(\"[\" + $a + \"]\");" +
                     "  counter.incrementAndGet();\n" +
                     "end";

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieBase kbase = helper.build( EventProcessingOption.STREAM );
        KieSession ksession = kbase.newKieSession( sessionConfig, null );

        PseudoClockScheduler sessionClock = ksession.getSessionClock();

        AtomicInteger counter = new AtomicInteger( 0 );
        ksession.setGlobal( "counter", counter );

        ksession.insert( new A(1) );

        sessionClock.advanceTime( 10, TimeUnit.MILLISECONDS );
        ksession.insert( new A(2) );

        sessionClock.advanceTime( 10, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals(2, counter.get());
    }

    @Test
    public void testBeta() {
        // DROOLS-1329
        String drl = "import " + A.class.getCanonicalName() + "\n" +
                     "import " + B.class.getCanonicalName() + "\n" +
                     "declare A @role( event ) @expires(11ms) end\n" +
                     "declare B @role( event ) @expires(11ms) end\n" +
                     "global java.util.concurrent.atomic.AtomicInteger counter;\n" +
                     "rule R0 when\n" +
                     "  $a: A( $Aid: id > 0 )\n" +
                     "  $b: B( ($Bid: id <= $Aid) && (id > ($Aid - 1 )))\n" +
                     "then\n" +
                     "  System.out.println(\"[\" + $a + \",\" + $b + \"]\");" +
                     "  counter.incrementAndGet();\n" +
                     "end";

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieBase kbase = helper.build( EventProcessingOption.STREAM );
        KieSession ksession = kbase.newKieSession( sessionConfig, null );

        PseudoClockScheduler sessionClock = ksession.getSessionClock();

        AtomicInteger counter = new AtomicInteger( 0 );
        ksession.setGlobal( "counter", counter );

        ksession.insert( new A(1) );
        ksession.insert( new B(1) );

        sessionClock.advanceTime( 10, TimeUnit.MILLISECONDS );
        ksession.insert( new A(2) );
        ksession.insert( new B(2) );

        sessionClock.advanceTime( 10, TimeUnit.MILLISECONDS );

        ksession.fireAllRules();
        assertEquals(2, counter.get());
    }

    @Test
    public void testBetaRightExpired() {
        // DROOLS-1329
        String drl = "import " + A.class.getCanonicalName() + "\n" +
                     "import " + B.class.getCanonicalName() + "\n" +
                     "declare A @role( event ) @expires(11ms) end\n" +
                     "declare B @role( event ) @expires(11ms) end\n" +
                     "global java.util.concurrent.atomic.AtomicInteger counter;\n" +
                     "rule R0 when\n" +
                     "  $a: A( $Aid: id > 0 )\n" +
                     "  $b: B( id == $Aid )\n" +
                     "then\n" +
                     "  System.out.println(\"[\" + $a + \",\" + $b + \"]\");" +
                     "  counter.incrementAndGet();\n" +
                     "end";

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieBase kbase = helper.build( EventProcessingOption.STREAM );
        KieSession ksession = kbase.newKieSession( sessionConfig, null );

        PseudoClockScheduler sessionClock = ksession.getSessionClock();

        AtomicInteger counter = new AtomicInteger( 0 );
        ksession.setGlobal( "counter", counter );

        ksession.insert( new A(1) );

        sessionClock.advanceTime( 20, TimeUnit.MILLISECONDS );
        ksession.insert( new B(1) );

        ksession.fireAllRules();
        assertEquals(0, counter.get());
    }

    @Test
    public void testBetaLeftExpired() {
        // DROOLS-1329
        String drl = "import " + A.class.getCanonicalName() + "\n" +
                     "import " + B.class.getCanonicalName() + "\n" +
                     "declare A @role( event ) @expires(11ms) end\n" +
                     "declare B @role( event ) @expires(11ms) end\n" +
                     "global java.util.concurrent.atomic.AtomicInteger counter;\n" +
                     "rule R0 when\n" +
                     "  $a: A( $Aid: id > 0 )\n" +
                     "  $b: B( id == $Aid )\n" +
                     "then\n" +
                     "  System.out.println(\"[\" + $a + \",\" + $b + \"]\");" +
                     "  counter.incrementAndGet();\n" +
                     "end";

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieBase kbase = helper.build( EventProcessingOption.STREAM );
        KieSession ksession = kbase.newKieSession( sessionConfig, null );

        PseudoClockScheduler sessionClock = ksession.getSessionClock();

        AtomicInteger counter = new AtomicInteger( 0 );
        ksession.setGlobal( "counter", counter );

        ksession.insert( new B(1) );

        sessionClock.advanceTime( 20, TimeUnit.MILLISECONDS );
        ksession.insert( new A(1) );

        ksession.fireAllRules();
        assertEquals(0, counter.get());
    }

    @Test
    public void testBetaLeftExpired2() {
        // DROOLS-1329
        String drl = "import " + A.class.getCanonicalName() + "\n" +
                     "import " + B.class.getCanonicalName() + "\n" +
                     "import " + C.class.getCanonicalName() + "\n" +
                     "declare A @role( event ) @expires(31ms) end\n" +
                     "declare B @role( event ) @expires(11ms) end\n" +
                     "declare C @role( event ) @expires(31ms) end\n" +
                     "global java.util.concurrent.atomic.AtomicInteger counter;\n" +
                     "rule R0 when\n" +
                     "  $a: A( $Aid: id > 0 )\n" +
                     "  $b: B( $Bid: id == $Aid )\n" +
                     "  $c: C( id == $Bid )\n" +
                     "then\n" +
                     "  System.out.println(\"[\" + $a + \",\" + $b + \",\" + $c + \"]\");" +
                     "  counter.incrementAndGet();\n" +
                     "end";

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieBase kbase = helper.build( EventProcessingOption.STREAM );
        KieSession ksession = kbase.newKieSession( sessionConfig, null );

        PseudoClockScheduler sessionClock = ksession.getSessionClock();

        AtomicInteger counter = new AtomicInteger( 0 );
        ksession.setGlobal( "counter", counter );

        ksession.insert( new A(1) );
        ksession.insert( new B(1) );

        sessionClock.advanceTime( 20, TimeUnit.MILLISECONDS );
        ksession.insert( new C(1) );

        ksession.fireAllRules();
        assertEquals(0, counter.get());
    }

    public class A {
        private final int id;

        public A( int id ) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "A(" + id + ")";
        }
    }

    public class B {
        private final int id;

        public B( int id ) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "B(" + id + ")";
        }
    }

    public class C {
        private final int id;

        public C( int id ) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "C(" + id + ")";
        }
    }


    @Role(Role.Type.EVENT)
    @Expires( "10s" )
    public static class ExpiringEventA { }

    @Role(Role.Type.EVENT)
    @Expires( value = "30s", policy = TIME_SOFT )
    public static class ExpiringEventB { }

    @Role(Role.Type.EVENT)
    @Expires( value = "30s", policy = TIME_SOFT )
    public static class ExpiringEventC { }

    @Test
    public void testSoftExpiration() {
        // DROOLS-1483
        String drl = "import " + ExpiringEventA.class.getCanonicalName() + "\n" +
                     "import " + ExpiringEventB.class.getCanonicalName() + "\n" +
                     "import " + ExpiringEventC.class.getCanonicalName() + "\n" +
                     "rule Ra when\n" +
                     "  $e : ExpiringEventA() over window:time(20s)\n" +
                     "then end\n " +
                     "rule Rb when\n" +
                     "  $e : ExpiringEventB() over window:time(20s)\n" +
                     "then end\n " +
                     "rule Rc when\n" +
                     "  $e : ExpiringEventC()\n" +
                     "then end\n";

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieBase kbase = helper.build( EventProcessingOption.STREAM );
        KieSession ksession = kbase.newKieSession( sessionConfig, null );

        PseudoClockScheduler clock = ksession.getSessionClock();

        ksession.insert( new ExpiringEventA() );
        ksession.insert( new ExpiringEventB() );
        ksession.insert( new ExpiringEventC() );
        ksession.fireAllRules();

        clock.advanceTime( 5, TimeUnit.SECONDS );
        ksession.fireAllRules();

        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( ExpiringEventA.class ) ).size() );
        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( ExpiringEventB.class ) ).size() );
        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( ExpiringEventC.class ) ).size() );

        clock.advanceTime( 10, TimeUnit.SECONDS );
        ksession.fireAllRules();

        // t=15 -> hard expiration of A
        assertEquals( 0, ksession.getObjects( new ClassObjectFilter( ExpiringEventA.class ) ).size() );
        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( ExpiringEventB.class ) ).size() );
        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( ExpiringEventC.class ) ).size() );

        clock.advanceTime( 10, TimeUnit.SECONDS );
        ksession.fireAllRules();

        // t=25 -> implicit expiration of B
        assertEquals( 0, ksession.getObjects( new ClassObjectFilter( ExpiringEventA.class ) ).size() );
        assertEquals( 0, ksession.getObjects( new ClassObjectFilter( ExpiringEventB.class ) ).size() );
        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( ExpiringEventC.class ) ).size() );

        clock.advanceTime( 10, TimeUnit.SECONDS );
        ksession.fireAllRules();

        // t=35 -> soft expiration of C
        assertEquals( 0, ksession.getObjects( new ClassObjectFilter( ExpiringEventA.class ) ).size() );
        assertEquals( 0, ksession.getObjects( new ClassObjectFilter( ExpiringEventB.class ) ).size() );
        assertEquals( 0, ksession.getObjects( new ClassObjectFilter( ExpiringEventC.class ) ).size() );
    }

    @Test
    public void testSoftExpirationWithDeclaration() {
        // DROOLS-1483
        String drl = "import " + A.class.getCanonicalName() + "\n" +
                     "import " + B.class.getCanonicalName() + "\n" +
                     "import " + C.class.getCanonicalName() + "\n" +
                     "declare A @role( event ) @expires(10s) end\n" +
                     "declare B @role( event ) @expires(value = 30s, policy = TIME_SOFT) end\n" +
                     "declare C @role( event ) @expires(value = 30s, policy = TIME_SOFT) end\n" +
                     "rule Ra when\n" +
                     "  $e : A() over window:time(20s)\n" +
                     "then end\n " +
                     "rule Rb when\n" +
                     "  $e : B() over window:time(20s)\n" +
                     "then end\n " +
                     "rule Rc when\n" +
                     "  $e : C()\n" +
                     "then end\n";

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieBase kbase = helper.build( EventProcessingOption.STREAM );
        KieSession ksession = kbase.newKieSession( sessionConfig, null );

        PseudoClockScheduler clock = ksession.getSessionClock();

        ksession.insert( new A(1) );
        ksession.insert( new B(2) );
        ksession.insert( new C(3) );
        ksession.fireAllRules();

        clock.advanceTime( 5, TimeUnit.SECONDS );
        ksession.fireAllRules();

        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( A.class ) ).size() );
        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( B.class ) ).size() );
        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( C.class ) ).size() );

        clock.advanceTime( 10, TimeUnit.SECONDS );
        ksession.fireAllRules();

        // t=15 -> hard expiration of A
        assertEquals( 0, ksession.getObjects( new ClassObjectFilter( A.class ) ).size() );
        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( B.class ) ).size() );
        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( C.class ) ).size() );

        clock.advanceTime( 10, TimeUnit.SECONDS );
        ksession.fireAllRules();

        // t=25 -> implicit expiration of B
        assertEquals( 0, ksession.getObjects( new ClassObjectFilter( A.class ) ).size() );
        assertEquals( 0, ksession.getObjects( new ClassObjectFilter( B.class ) ).size() );
        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( C.class ) ).size() );

        clock.advanceTime( 10, TimeUnit.SECONDS );
        ksession.fireAllRules();

        // t=35 -> soft expiration of C
        assertEquals( 0, ksession.getObjects( new ClassObjectFilter( A.class ) ).size() );
        assertEquals( 0, ksession.getObjects( new ClassObjectFilter( B.class ) ).size() );
        assertEquals( 0, ksession.getObjects( new ClassObjectFilter( C.class ) ).size() );
    }
}
