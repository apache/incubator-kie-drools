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

import org.drools.core.ClockType;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.utils.KieHelper;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

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
}
