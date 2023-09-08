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
package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.base.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.base.rule.EntryPointId;
import org.drools.base.base.ObjectType;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.StockTick;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.base.rule.TypeDeclaration.NEVER_EXPIRES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests related to the stream support features
 */
@RunWith(Parameterized.class)
public class StreamsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public StreamsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true);
    }

    @Test(timeout = 10000)
    public void testEventAssertion() {
        final String drl = "package org.drools.compiler\n" +
                "\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List results;\n" +
                "\n" +
                "declare StockTick\n" +
                "    @role( event )\n" +
                "end\n" +
                "\n" +
                "rule \"Test entry point\"\n" +
                "when\n" +
                "    $st : StockTick( company == \"ACME\", price > 10 ) from entry-point StockStream\n" +
                "then\n" +
                "    results.add( $st );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession(KieSessionTestConfiguration.STATEFUL_PSEUDO.getKieSessionConfiguration(), null);
        try {
            final List results = new ArrayList();
            session.setGlobal("results", results);

            final StockTick tick1 = new StockTick(1, "DROO", 50, System.currentTimeMillis());
            final StockTick tick2 = new StockTick(2, "ACME", 10, System.currentTimeMillis());
            final StockTick tick3 = new StockTick(3, "ACME", 10, System.currentTimeMillis());
            final StockTick tick4 = new StockTick(4, "DROO", 50, System.currentTimeMillis());

            final InternalFactHandle handle1 = (InternalFactHandle) session.insert(tick1);
            final InternalFactHandle handle2 = (InternalFactHandle) session.insert(tick2);
            final InternalFactHandle handle3 = (InternalFactHandle) session.insert(tick3);
            final InternalFactHandle handle4 = (InternalFactHandle) session.insert(tick4);

            assertThat(handle1).isNotNull();
            assertThat(handle2).isNotNull();
            assertThat(handle3).isNotNull();
            assertThat(handle4).isNotNull();

            assertThat(handle1.isEvent()).isTrue();
            assertThat(handle2.isEvent()).isTrue();
            assertThat(handle3.isEvent()).isTrue();
            assertThat(handle4.isEvent()).isTrue();

            session.fireAllRules();

            assertThat(results.size()).isEqualTo(0);

            final StockTick tick5 = new StockTick(5, "DROO", 50, System.currentTimeMillis());
            final StockTick tick6 = new StockTick(6, "ACME", 10, System.currentTimeMillis());
            final StockTick tick7 = new StockTick(7, "ACME", 15, System.currentTimeMillis());
            final StockTick tick8 = new StockTick(8, "DROO", 50, System.currentTimeMillis());

            final EntryPoint entry = session.getEntryPoint("StockStream");

            final InternalFactHandle handle5 = (InternalFactHandle) entry.insert(tick5);
            final InternalFactHandle handle6 = (InternalFactHandle) entry.insert(tick6);
            final InternalFactHandle handle7 = (InternalFactHandle) entry.insert(tick7);
            final InternalFactHandle handle8 = (InternalFactHandle) entry.insert(tick8);

            assertThat(handle5).isNotNull();
            assertThat(handle6).isNotNull();
            assertThat(handle7).isNotNull();
            assertThat(handle8).isNotNull();

            assertThat(handle5.isEvent()).isTrue();
            assertThat(handle6.isEvent()).isTrue();
            assertThat(handle7.isEvent()).isTrue();
            assertThat(handle8.isEvent()).isTrue();

            session.fireAllRules();

            assertThat(results.size()).isEqualTo(1);
            assertThat(results.get(0)).isSameAs(tick7);
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testEntryPointReference() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("stream-test", kieBaseTestConfiguration,
                                                                           "org/drools/compiler/integrationtests/test_EntryPointReference.drl");
        final KieSession session = kbase.newKieSession();
        try {
            final List<StockTick> results = new ArrayList<>();
            session.setGlobal("results", results);

            final StockTick tick5 = new StockTick(5, "DROO", 50, System.currentTimeMillis());
            final StockTick tick6 = new StockTick(6, "ACME", 10, System.currentTimeMillis());
            final StockTick tick7 = new StockTick(7, "ACME", 30, System.currentTimeMillis());
            final StockTick tick8 = new StockTick(8, "DROO", 50, System.currentTimeMillis());

            final EntryPoint entry = session.getEntryPoint("stream1");

            final InternalFactHandle handle5 = (InternalFactHandle) entry.insert(tick5);
            final InternalFactHandle handle6 = (InternalFactHandle) entry.insert(tick6);
            final InternalFactHandle handle7 = (InternalFactHandle) entry.insert(tick7);
            final InternalFactHandle handle8 = (InternalFactHandle) entry.insert(tick8);

            assertThat(handle5).isNotNull();
            assertThat(handle6).isNotNull();
            assertThat(handle7).isNotNull();
            assertThat(handle8).isNotNull();

            assertThat(handle5.isEvent()).isTrue();
            assertThat(handle6.isEvent()).isTrue();
            assertThat(handle7.isEvent()).isTrue();
            assertThat(handle8.isEvent()).isTrue();

            session.fireAllRules();

            assertThat(results.size()).isEqualTo(1);
            assertThat(results.get(0)).isSameAs(tick7);
        } finally {
            session.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testModifyRetracOnEntryPointFacts() {

        final String drl = "package org.drools.compiler\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "declare StockTick\n" +
                "    @role( event )\n" +
                "end\n" +
                "\n" +
                "rule \"Test entry point 1\"\n" +
                "when\n" +
                "    $st : StockTick( company == \"ACME\", price > 10 && < 100 ) from entry-point \"stream1\"\n" +
                "then\n" +
                "    results.add( Double.valueOf( $st.getPrice() ) );\n" +
                "    modify( $st ) { setPrice( 110 ) }\n" +
                "end\n" +
                "\n" +
                "rule \"Test entry point 2\"\n" +
                "when\n" +
                "    $st : StockTick( company == \"ACME\", price > 100 ) from entry-point \"stream1\"\n" +
                "then\n" +
                "    results.add( Double.valueOf( $st.getPrice() ) );\n" +
                "    delete( $st );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List<? extends Number> results = new ArrayList<>();
            session.setGlobal("results", results);

            final StockTick tick5 = new StockTick(5, "DROO", 50, System.currentTimeMillis());
            final StockTick tick6 = new StockTick(6, "ACME", 10, System.currentTimeMillis());
            final StockTick tick7 = new StockTick(7, "ACME", 30, System.currentTimeMillis());
            final StockTick tick8 = new StockTick(8, "DROO", 50, System.currentTimeMillis());

            final EntryPoint entry = session.getEntryPoint("stream1");

            final InternalFactHandle handle5 = (InternalFactHandle) entry.insert(tick5);
            final InternalFactHandle handle6 = (InternalFactHandle) entry.insert(tick6);
            final InternalFactHandle handle7 = (InternalFactHandle) entry.insert(tick7);
            final InternalFactHandle handle8 = (InternalFactHandle) entry.insert(tick8);

            assertThat(handle5).isNotNull();
            assertThat(handle6).isNotNull();
            assertThat(handle7).isNotNull();
            assertThat(handle8).isNotNull();

            assertThat(handle5.isEvent()).isTrue();
            assertThat(handle6.isEvent()).isTrue();
            assertThat(handle7.isEvent()).isTrue();
            assertThat(handle8.isEvent()).isTrue();

            session.fireAllRules();

            assertThat(results.size()).isEqualTo(2);
            assertThat(results.get(0).intValue()).isEqualTo(30);
            assertThat(results.get(1).intValue()).isEqualTo(110);

            // the 3 non-matched facts continue to exist in the entry point
            assertThat(entry.getObjects().size()).isEqualTo(3);
            // but no fact was inserted into the main session
            assertThat(session.getObjects().size()).isEqualTo(0);
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testModifyOnEntryPointFacts() {
        final String drl = "package org.drools.compiler\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "declare StockTick\n" +
                "        @role ( event )\n" +
                "end\n" +
                "rule R1 salience 100\n" +
                "    when\n" +
                "        $s1 : StockTick( company == 'RHT', price == 10 ) from entry-point ep1\n" +
                "    then\n" +
                "        StockTick s = $s1;\n" +
                "        modify( s ) { setPrice( 50 ) };\n" +
                "end\n" +
                "rule R2 salience 90\n" +
                "    when\n" +
                "        $s1 : StockTick( company == 'RHT', price == 10 ) from entry-point ep2\n" +
                "    then\n" +
                "        StockTick s = $s1;\n" +
                "        modify( s ) { setPrice( 50 ) };\n" +
                "end\n" +
                "rule R3 salience 80\n" +
                "    when\n" +
                "        $s1 : StockTick( company == 'RHT', price == 10 ) from entry-point ep3\n" +
                "    then\n" +
                "        StockTick s = $s1;\n" +
                "        modify( s ) { setPrice( 50 ) };\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final org.kie.api.event.rule.AgendaEventListener ael = mock(org.kie.api.event.rule.AgendaEventListener.class);
            ksession.addEventListener(ael);

            final EntryPoint ep1 = ksession.getEntryPoint("ep1");
            final EntryPoint ep2 = ksession.getEntryPoint("ep2");
            final EntryPoint ep3 = ksession.getEntryPoint("ep3");

            ep1.insert(new StockTick(1, "RHT", 10, 1000));
            ep2.insert(new StockTick(1, "RHT", 10, 1000));
            ep3.insert(new StockTick(1, "RHT", 10, 1000));
            final int rulesFired = ksession.fireAllRules();
            assertThat(rulesFired).isEqualTo(3);

            final ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
            verify(ael, times(3)).afterMatchFired(captor.capture());
            final List<org.kie.api.event.rule.AfterMatchFiredEvent> aafe = captor.getAllValues();

            assertThat(aafe.get(0).getMatch().getRule().getName()).isEqualTo("R1");
            assertThat(aafe.get(1).getMatch().getRule().getName()).isEqualTo("R2");
            assertThat(aafe.get(2).getMatch().getRule().getName()).isEqualTo("R3");
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testEntryPointWithAccumulateAndMVEL() {
        final String drl = "package org.drools.compiler\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "rule R1 dialect 'mvel'\n" +
                "    when\n" +
                "        $n : Number() from accumulate( \n" +
                "                 StockTick() from entry-point ep1,\n" +
                "                 count(1))" +
                "    then\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final org.kie.api.event.rule.AgendaEventListener ael = mock(org.kie.api.event.rule.AgendaEventListener.class);
            ksession.addEventListener(ael);

            final EntryPoint ep1 = ksession.getEntryPoint("ep1");

            ep1.insert(new StockTick(1, "RHT", 10, 1000));
            final int rulesFired = ksession.fireAllRules();
            assertThat(rulesFired).isEqualTo(1);

            final ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
            verify(ael, times(1)).afterMatchFired(captor.capture());
            final List<org.kie.api.event.rule.AfterMatchFiredEvent> aafe = captor.getAllValues();

            assertThat(aafe.get(0).getMatch().getRule().getName()).isEqualTo("R1");
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testGetEntryPointList() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("stream-test", kieBaseTestConfiguration,
                                                                           "org/drools/compiler/integrationtests/test_EntryPointReference.drl");
        final KieSession session = kbase.newKieSession();
        try {
            final EntryPoint def = session.getEntryPoint(EntryPointId.DEFAULT.getEntryPointId());
            final EntryPoint s1 = session.getEntryPoint("stream1");
            final EntryPoint s2 = session.getEntryPoint("stream2");
            final EntryPoint s3 = session.getEntryPoint("stream3");
            final Collection<? extends EntryPoint> eps = session.getEntryPoints();

            assertThat(eps.size()).isEqualTo(4);
            assertThat(eps.contains(def)).isTrue();
            assertThat(eps.contains(s1)).isTrue();
            assertThat(eps.contains(s2)).isTrue();
            assertThat(eps.contains(s3)).isTrue();
        } finally {
            session.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testEventDoesNotExpireIfNotInPattern() {
        final String drl = "package org.drools.compiler\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "declare StockTick\n" +
                "    @role( event )\n" +
                "    @expires( 1s )\n" +
                "end\n" +
                "\n" +
                "rule X\n" +
                "when\n" +
                "    eval( true )\n" +
                "then \n" +
                "    // no-op\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession(KieSessionTestConfiguration.STATEFUL_PSEUDO.getKieSessionConfiguration(), null);
        try {
            final RuleRuntimeEventListener wml = mock(RuleRuntimeEventListener.class);
            ksession.addEventListener(wml);

            final PseudoClockScheduler clock = ksession.getSessionClock();

            final StockTick st1 = new StockTick(1, "RHT", 100, 1000);
            final StockTick st2 = new StockTick(2, "RHT", 100, 1000);

            ksession.insert(st1);
            ksession.insert(st2);

            verify(wml, times(2)).objectInserted(any(org.kie.api.event.rule.ObjectInsertedEvent.class));
            assertThat(ksession.getObjects()).hasSize(2);
            assertThat((Collection<Object>) ksession.getObjects()).contains(st1, st2);

            ksession.fireAllRules();

            clock.advanceTime(3, TimeUnit.SECONDS);
            ksession.fireAllRules();

            assertThat(ksession.getObjects()).hasSize(0);
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testEventExpirationSetToZero() {
        final String drl = "package org.drools.compiler\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "declare StockTick\n" +
                "    @role( event )\n" +
                "    @expires( 0 )\n" +
                "end\n" +
                "\n" +
                "rule X\n" +
                "when\n" +
                "    StockTick()\n" +
                "then \n" +
                "    // no-op\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession(KieSessionTestConfiguration.STATEFUL_PSEUDO.getKieSessionConfiguration(), null);
        try {
            final RuleRuntimeEventListener wml = mock(RuleRuntimeEventListener.class);
            ksession.addEventListener(wml);
            final AgendaEventListener ael = mock(AgendaEventListener.class);
            ksession.addEventListener(ael);

            final PseudoClockScheduler clock = ksession.getSessionClock();

            final StockTick st1 = new StockTick(1, "RHT", 100, 1000);
            final StockTick st2 = new StockTick(2, "RHT", 100, 1000);

            ksession.insert(st1);
            ksession.insert(st2);

            assertThat(ksession.fireAllRules()).isEqualTo(2);

            verify(wml, times(2)).objectInserted(any(org.kie.api.event.rule.ObjectInsertedEvent.class));
            verify(ael, times(2)).matchCreated(any(MatchCreatedEvent.class));
            assertThat(ksession.getObjects()).hasSize(2);
            assertThat((Collection<Object>) ksession.getObjects()).contains(st1, st2);

            clock.advanceTime(3, TimeUnit.SECONDS);
            ksession.fireAllRules();

            assertThat(ksession.getObjects()).hasSize(0);
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testEventExpirationValue() {
        final String drl1 = "package org.drools.pkg1\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "declare StockTick\n" +
                "    @role(event)\n" +
                "end\n" +
                "rule X\n" +
                "when\n" +
                "    StockTick()\n" +
                "then\n" +
                "end\n";
        final String drl2 = "package org.drools.pkg2\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "declare StockTick\n" +
                "    @role(event)\n" +
                "end\n" +
                "rule X\n" +
                "when\n" +
                "    StockTick()\n" +
                "then\n" +
                "end\n";

        final InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl(
                "stream-test", kieBaseTestConfiguration, drl1, drl2);
        final List<ObjectTypeNode> otns = kbase.getRete().getObjectTypeNodes();
        final ObjectType stot = new ClassObjectType(StockTick.class);
        for (final ObjectTypeNode otn : otns) {
            if (otn.getObjectType().isAssignableFrom(stot)) {
                assertThat(otn.getExpirationOffset()).isEqualTo(NEVER_EXPIRES);
            }
        }
    }

    @Test(timeout = 10000)
    public void testDeclaredEntryPoint() {
        final String drl = "package org.jboss.qa.brms.declaredep\n" +
                "declare entry-point UnusedEntryPoint\n" +
                "end\n" +
                "rule HelloWorld\n" +
                "    when\n" +
                "        String( ) from entry-point UsedEntryPoint\n" +
                "    then\n" +
                "        // consequences\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            assertThat(ksession.getEntryPoint("UsedEntryPoint")).isNotNull();
            assertThat(ksession.getEntryPoint("UnusedEntryPoint")).isNotNull();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testWindowDeclaration() {
        final String drl = "package org.drools.compiler\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "declare StockTick\n" +
                "    @role(event)\n" +
                "end\n" +
                "declare window RedHatTicks\n" +
                "    StockTick( company == 'RHT' )\n" +
                "               over window:length(5)\n" +
                "               from entry-point ticks\n" +
                "end\n" +
                "rule X\n" +
                "when\n" +
                "    accumulate( $s : StockTick( price > 20 ) from window RedHatTicks,\n" +
                "                $sum : sum( $s.getPrice() ),\n" +
                "                $cnt : count( $s ) )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final AgendaEventListener ael = mock(AgendaEventListener.class);
            ksession.addEventListener(ael);

            final EntryPoint ep = ksession.getEntryPoint("ticks");
            ep.insert(new StockTick(1, "ACME", 20, 1000)); // not in the window
            ep.insert(new StockTick(2, "RHT", 20, 1000)); // not > 20
            ep.insert(new StockTick(3, "RHT", 30, 1000));
            ep.insert(new StockTick(4, "ACME", 30, 1000)); // not in the window
            ep.insert(new StockTick(5, "RHT", 25, 1000));
            ep.insert(new StockTick(6, "ACME", 10, 1000)); // not in the window
            ep.insert(new StockTick(7, "RHT", 10, 1000)); // not > 20
            ep.insert(new StockTick(8, "RHT", 40, 1000));

            ksession.fireAllRules();

            final ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
            verify(ael, times(1)).afterMatchFired(captor.capture());

            final AfterMatchFiredEvent aafe = captor.getValue();
            assertThat(((Number) aafe.getMatch().getDeclarationValue("$sum")).intValue()).isEqualTo(95);
            assertThat(((Number) aafe.getMatch().getDeclarationValue("$cnt")).intValue()).isEqualTo(3);
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testWindowDeclaration2() {
        final String drl = "package org.drools.compiler\n" +
                "declare Double\n" +
                "    @role(event)\n" +
                "end\n" +
                "declare window Streem\n" +
                "    Double() over window:length( 10 ) from entry-point data\n" +
                "end\n" +
                "rule \"See\"\n" +
                "when\n" +
                "    $sum : Double() from accumulate (\n" +
                "        $d: Double()\n" +
                "            from window Streem,\n" +
                "        sum( $d )\n" +
                "    )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final AgendaEventListener ael = mock(AgendaEventListener.class);
            ksession.addEventListener(ael);

            final EntryPoint ep = ksession.getEntryPoint("data");
            ep.insert(10d);
            ep.insert(11d);
            ep.insert(12d);

            ksession.fireAllRules();

            final ArgumentCaptor<AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
            verify(ael, times(1)).afterMatchFired(captor.capture());

            final AfterMatchFiredEvent aafe = captor.getValue();
            assertThat(((Number) aafe.getMatch().getDeclarationValue("$sum")).intValue()).isEqualTo(33);
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testMultipleWindows() {
        final String drl = "package org.drools.compiler\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "declare StockTick\n" +
                "    @role(event)\n" +
                "end\n" +
                "rule FaultsCoincide\n" +
                "when\n" +
                "   f1 : StockTick( company == \"RHT\" ) over window:length( 1 )\n" +
                "   f2 : StockTick( company == \"JBW\" ) over window:length( 1 )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final AgendaEventListener ael = mock(AgendaEventListener.class);
            ksession.addEventListener(ael);

            final StockTick st1 = new StockTick(1, "RHT", 10, 1000);
            ksession.insert(st1);
            final StockTick st2 = new StockTick(2, "JBW", 10, 1000);
            ksession.insert(st2);

            ksession.fireAllRules();

            final ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
            verify(ael, times(1)).afterMatchFired(captor.capture());

            final AfterMatchFiredEvent aafe = captor.getValue();
            assertThat(aafe.getMatch().getDeclarationValue("f1")).isEqualTo(st1);
            assertThat(aafe.getMatch().getDeclarationValue("f2")).isEqualTo(st2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testWindowWithEntryPointCompilationError() {
        final String drl = "import " + Cheese.class.getCanonicalName() + ";\n" +
                "declare window X\n" +
                "   Cheese( type == \"gorgonzola\" ) over window:time(1m) from entry-point Z\n" +
                "end\n" +
                "rule R when\n" +
                "   $c : Cheese( price < 100 ) from window X\n" +
                "then\n" +
                "   System.out.println($c);\n" +
                "end\n";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        assertThat(kieBuilder.getResults().getMessages())
                .withFailMessage("Should have raised a compilation error as Cheese is not declared as an event.")
                .isNotEmpty();
    }

    @Test(timeout = 10000)
    public void testAtomicActivationFiring() throws Exception {
        // JBRULES-3383
        final String drl = "package org.drools.compiler.test\n" +
                "declare Event\n" +
                "   @role(event)\n" +
                "   name : String\n" +
                "end\n" +
                "declare Monitor\n" +
                "   @role(event)\n" +
                "   event : Event\n" +
                "   name : String\n" +
                "end\n" +
                "\n" +
                "rule \"start monitoring\"\n" +
                "when\n" +
                "    $e : Event( $in : name )\n" +
                "    not Monitor( name == $in )\n" +
                "then\n" +
                "    Monitor m = new Monitor( $e, $in );\n" +
                "    insert( m );\n" +
                "end\n" +
                "\n" +
                "rule \"stop monitoring\"\n" +
                "timer( int: 1s )\n" +
                "when\n" +
                "    $m : Monitor( $in : name )\n" +
                "    $e : Event( name == $in )\n" +
                "then\n" +
                "    retract( $m );\n" +
                "    retract( $m.getEvent() );\n" +
                "end\n" +
                "rule \"halt\"\n" +
                "salience -1\n" +
                "when\n" +
                "    not Event( )\n" +
                "then\n" +
                "    drools.halt();\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.addEventListener(new org.kie.api.event.rule.DebugAgendaEventListener());

            final FactType eventType = kbase.getFactType("org.drools.compiler.test", "Event");

            final Object event = eventType.newInstance();
            eventType.set(event, "name", "myName");
            ksession.insert(event);

            ksession.fireUntilHalt();
        } finally {
            ksession.dispose();
        }
    }
}
