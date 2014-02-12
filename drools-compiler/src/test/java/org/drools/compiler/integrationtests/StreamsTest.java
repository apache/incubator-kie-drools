/*
 * Copyright 2007 JBoss Inc
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
 * Created on Dec 14, 2007
 */
package org.drools.compiler.integrationtests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.ClockType;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.SessionConfiguration;
import org.drools.compiler.StockTick;
import org.drools.compiler.StockTickInterface;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.ObjectType;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.internal.KnowledgeBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.time.SessionClock;
import org.mockito.ArgumentCaptor;

/**
 * Tests related to the stream support features
 */
public class StreamsTest extends CommonTestMethodBase {

    private KnowledgeBase loadKnowledgeBase( final String fileName ) throws IOException,
            DroolsParserException,
            Exception {
        return loadKnowledgeBase( fileName,
                                  KnowledgeBaseFactory.newKnowledgeBaseConfiguration() );
    }

    private KnowledgeBase loadKnowledgeBase( final String fileName,
            KieBaseConfiguration kconf ) throws IOException,
            DroolsParserException,
            Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(fileName,
                                                          getClass()),
                     ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            System.out.println( kbuilder.getErrors() );
            return null;
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        return SerializationHelper.serializeObject(kbase);
        //return kbase;
    }

    @Test(timeout=10000)
    public void testEventAssertion() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase("test_EntryPoint.drl");
        //final RuleBase ruleBase = loadRuleBase( reader );

        KieSessionConfiguration conf = new SessionConfiguration();
        ( (SessionConfiguration) conf ).setClockType( ClockType.PSEUDO_CLOCK );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession( conf,
                                                                              null );

        final List results = new ArrayList();

        session.setGlobal("results",
                          results);

        StockTickInterface tick1 = new StockTick(1,
                                                 "DROO",
                                                 50,
                                                 System.currentTimeMillis());
        StockTickInterface tick2 = new StockTick(2,
                                                 "ACME",
                                                 10,
                                                 System.currentTimeMillis());
        StockTickInterface tick3 = new StockTick(3,
                                                 "ACME",
                                                 10,
                                                 System.currentTimeMillis());
        StockTickInterface tick4 = new StockTick(4,
                                                 "DROO",
                                                 50,
                                                 System.currentTimeMillis());

        InternalFactHandle handle1 = (InternalFactHandle) session.insert(tick1);
        InternalFactHandle handle2 = (InternalFactHandle) session.insert(tick2);
        InternalFactHandle handle3 = (InternalFactHandle) session.insert(tick3);
        InternalFactHandle handle4 = (InternalFactHandle) session.insert(tick4);

        assertNotNull(handle1);
        assertNotNull(handle2);
        assertNotNull(handle3);
        assertNotNull(handle4);

        assertTrue(handle1.isEvent());
        assertTrue(handle2.isEvent());
        assertTrue(handle3.isEvent());
        assertTrue(handle4.isEvent());

        session.fireAllRules();

        assertEquals(0,
                     results.size());

        StockTickInterface tick5 = new StockTick(5,
                                                 "DROO",
                                                 50,
                                                 System.currentTimeMillis());
        StockTickInterface tick6 = new StockTick(6,
                                                 "ACME",
                                                 10,
                                                 System.currentTimeMillis());
        StockTickInterface tick7 = new StockTick(7,
                                                 "ACME",
                                                 15,
                                                 System.currentTimeMillis());
        StockTickInterface tick8 = new StockTick(8,
                                                 "DROO",
                                                 50,
                                                 System.currentTimeMillis());

        EntryPoint entry = session.getEntryPoint("StockStream");

        InternalFactHandle handle5 = (InternalFactHandle) entry.insert(tick5);
        InternalFactHandle handle6 = (InternalFactHandle) entry.insert(tick6);
        InternalFactHandle handle7 = (InternalFactHandle) entry.insert(tick7);
        InternalFactHandle handle8 = (InternalFactHandle) entry.insert(tick8);

        assertNotNull(handle5);
        assertNotNull(handle6);
        assertNotNull(handle7);
        assertNotNull(handle8);

        assertTrue(handle5.isEvent());
        assertTrue(handle6.isEvent());
        assertTrue(handle7.isEvent());
        assertTrue(handle8.isEvent());

        session.fireAllRules();

        assertEquals(1,
                     results.size());
        assertSame(tick7,
                   results.get(0));

    }

    @Test//(timeout=10000)
    public void testEntryPointReference() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase("test_EntryPointReference.drl");
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        final List<StockTick> results = new ArrayList<StockTick>();
        session.setGlobal("results",
                          results);

        StockTickInterface tick5 = new StockTick(5,
                                                 "DROO",
                                                 50,
                                                 System.currentTimeMillis());
        StockTickInterface tick6 = new StockTick(6,
                                                 "ACME",
                                                 10,
                                                 System.currentTimeMillis());
        StockTickInterface tick7 = new StockTick(7,
                                                 "ACME",
                                                 30,
                                                 System.currentTimeMillis());
        StockTickInterface tick8 = new StockTick(8,
                                                 "DROO",
                                                 50,
                                                 System.currentTimeMillis());

        EntryPoint entry = session.getEntryPoint("stream1");

        InternalFactHandle handle5 = (InternalFactHandle) entry.insert(tick5);
        InternalFactHandle handle6 = (InternalFactHandle) entry.insert(tick6);
        InternalFactHandle handle7 = (InternalFactHandle) entry.insert(tick7);
        InternalFactHandle handle8 = (InternalFactHandle) entry.insert(tick8);

        assertNotNull(handle5);
        assertNotNull(handle6);
        assertNotNull(handle7);
        assertNotNull(handle8);

        assertTrue(handle5.isEvent());
        assertTrue(handle6.isEvent());
        assertTrue(handle7.isEvent());
        assertTrue(handle8.isEvent());

        session.fireAllRules();

        assertEquals(1,
                     results.size());
        assertSame(tick7,
                   results.get(0));

    }

    @Test(timeout=10000)
    public void testModifyRetracOnEntryPointFacts() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase("test_modifyRetractEntryPoint.drl");
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        final List<? extends Number> results = new ArrayList<Number>();
        session.setGlobal( "results",
                           results );

        StockTickInterface tick5 = new StockTick( 5,
                                                  "DROO",
                                                  50,
                                                  System.currentTimeMillis() );
        StockTickInterface tick6 = new StockTick( 6,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis() );
        StockTickInterface tick7 = new StockTick( 7,
                                                  "ACME",
                                                  30,
                                                  System.currentTimeMillis() );
        StockTickInterface tick8 = new StockTick( 8,
                                                  "DROO",
                                                  50,
                                                  System.currentTimeMillis() );

        EntryPoint entry = session.getEntryPoint( "stream1" );

       InternalFactHandle handle5 = (InternalFactHandle) entry.insert( tick5 );
        InternalFactHandle handle6 = (InternalFactHandle) entry.insert( tick6 );
        InternalFactHandle handle7 = (InternalFactHandle) entry.insert( tick7 );
        InternalFactHandle handle8 = (InternalFactHandle) entry.insert( tick8 );

        assertNotNull( handle5 );
        assertNotNull( handle6 );
        assertNotNull( handle7 );
        assertNotNull( handle8 );

        assertTrue( handle5.isEvent() );
        assertTrue( handle6.isEvent() );
        assertTrue( handle7.isEvent() );
        assertTrue( handle8.isEvent() );

        session.fireAllRules();

        System.out.println(results);
        assertEquals( 2,
                      results.size() );
        assertEquals( 30,
                      ( (Number) results.get( 0 ) ).intValue() );
        assertEquals( 110,
                      ( (Number) results.get( 1 ) ).intValue() );

        // the 3 non-matched facts continue to exist in the entry point
        assertEquals(3,
                     entry.getObjects().size());
        // but no fact was inserted into the main session
        assertEquals(0,
                session.getObjects().size());

    }

    @Test //(timeout=10000)
    public void testModifyOnEntryPointFacts() throws Exception {
        String str = "package org.drools.compiler\n" +
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

        // read in the source
        KnowledgeBase kbase = loadKnowledgeBaseFromString( (KieBaseConfiguration)null, str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        org.kie.api.event.rule.AgendaEventListener ael = mock(org.kie.api.event.rule.AgendaEventListener.class);
        ksession.addEventListener(ael);

        EntryPoint ep1 = ksession.getEntryPoint("ep1");
        EntryPoint ep2 = ksession.getEntryPoint("ep2");
        EntryPoint ep3 = ksession.getEntryPoint("ep3");

        ep1.insert(new StockTick(1,
                                 "RHT",
                                 10,
                                 1000));
        ep2.insert(new StockTick(1,
                                 "RHT",
                                 10,
                                 1000));
        ep3.insert(new StockTick(1,
                                 "RHT",
                                 10,
                                 1000));
        int rulesFired = ksession.fireAllRules();
        assertEquals(3,
                     rulesFired);

        ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
        verify(ael,
               times(3)).afterMatchFired(captor.capture());
        List<org.kie.api.event.rule.AfterMatchFiredEvent> aafe = captor.getAllValues();

        Assert.assertThat(aafe.get(0).getMatch().getRule().getName(),
                          is("R1"));
        Assert.assertThat(aafe.get(1).getMatch().getRule().getName(),
                          is("R2"));
        Assert.assertThat(aafe.get(2).getMatch().getRule().getName(),
                          is("R3"));
    }

    @Test(timeout=10000)
    public void testEntryPointWithAccumulateAndMVEL() throws Exception {
        String str = "package org.drools.compiler\n" +
                "rule R1 dialect 'mvel'\n" +
                "    when\n" +
                "        $n : Number() from accumulate( \n" +
                "                 StockTick() from entry-point ep1,\n" +
                "                 count(1))" +
                "    then\n" +
                "end\n";

        // read in the source
        KnowledgeBase kbase = loadKnowledgeBaseFromString( (KieBaseConfiguration)null, str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        org.kie.api.event.rule.AgendaEventListener ael = mock(org.kie.api.event.rule.AgendaEventListener.class);
        ksession.addEventListener(ael);

        EntryPoint ep1 = ksession.getEntryPoint("ep1");

        ep1.insert(new StockTick(1,
                "RHT",
                10,
                1000));
        int rulesFired = ksession.fireAllRules();
        assertEquals(1,
                rulesFired);

        ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
        verify(ael,
                times(1)).afterMatchFired(captor.capture());
        List<org.kie.api.event.rule.AfterMatchFiredEvent> aafe = captor.getAllValues();

        Assert.assertThat(aafe.get(0).getMatch().getRule().getName(),
                is("R1"));
    }
    
    @Test(timeout=10000)
    public void testGetEntryPointList() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase("test_EntryPointReference.drl");
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        EntryPoint def = session.getEntryPoint(EntryPointId.DEFAULT.getEntryPointId());
        EntryPoint s1 = session.getEntryPoint("stream1");
        EntryPoint s2 = session.getEntryPoint( "stream2" );
        EntryPoint s3 = session.getEntryPoint( "stream3" );
        Collection<? extends EntryPoint> eps = session.getEntryPoints();

        assertEquals( 4,
                      eps.size() );
        assertTrue(eps.contains(def));
        assertTrue(eps.contains(s1));
        assertTrue(eps.contains(s2));
        assertTrue(eps.contains(s3));
    }

    @Test(timeout=10000)
    public void testEventDoesNotExpireIfNotInPattern() throws Exception {
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = loadKnowledgeBase("test_EventExpiration.drl",
                kconf);

        KieSessionConfiguration ksessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksessionConfig.setOption(ClockTypeOption.get("pseudo"));
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(ksessionConfig,
                null);

        RuleRuntimeEventListener wml = mock(RuleRuntimeEventListener.class);
        ksession.addEventListener(wml);

        PseudoClockScheduler clock = (PseudoClockScheduler) ksession.<SessionClock> getSessionClock();

        final StockTickInterface st1 = new StockTick(1,
                                                     "RHT",
                                                     100,
                                                     1000);
        final StockTickInterface st2 = new StockTick(2,
                                                     "RHT",
                                                     100,
                                                     1000);

        ksession.insert(st1);
        ksession.insert(st2);

        verify(wml,
               times(2)).objectInserted(any(org.kie.api.event.rule.ObjectInsertedEvent.class));
        assertThat(ksession.getObjects().size(),
                   equalTo(2));
        assertThat((Collection<Object>) ksession.getObjects(),
                   hasItems((Object) st1,
                            st2));

        ksession.fireAllRules();

        clock.advanceTime(3,
                          TimeUnit.SECONDS);
        ksession.fireAllRules();

        assertThat(ksession.getObjects().size(),
                equalTo(0));
    }

    @Test(timeout=10000)
    public void testEventExpirationSetToZero() throws Exception {
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = loadKnowledgeBase("test_EventExpirationSetToZero.drl",
                                                kconf);

        KieSessionConfiguration ksessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksessionConfig.setOption(ClockTypeOption.get("pseudo"));
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(ksessionConfig,
                                                                              null);

        RuleRuntimeEventListener wml = mock(RuleRuntimeEventListener.class);
        ksession.addEventListener(wml);
        AgendaEventListener ael = mock(AgendaEventListener.class);
        ksession.addEventListener(ael);

        PseudoClockScheduler clock = (PseudoClockScheduler) ksession.<SessionClock> getSessionClock();

        final StockTickInterface st1 = new StockTick(1,
                                                     "RHT",
                                                     100,
                                                     1000);
        final StockTickInterface st2 = new StockTick(2,
                                                     "RHT",
                                                     100,
                                                     1000);

        ksession.insert(st1);
        ksession.insert(st2);

        assertThat(ksession.fireAllRules(),
                   equalTo(2));

        verify(wml,
               times(2)).objectInserted(any(org.kie.api.event.rule.ObjectInsertedEvent.class));
        verify(ael,
               times(2)).matchCreated(any(MatchCreatedEvent.class));
        assertThat(ksession.getObjects().size(),
                   equalTo(2));
        assertThat((Collection<Object>) ksession.getObjects(),
                   hasItems((Object) st1,
                            st2));

        clock.advanceTime(3,
                          TimeUnit.SECONDS);
        ksession.fireAllRules();

        assertThat(ksession.getObjects().size(),
                   equalTo(0));
    }

    @Test(timeout=10000)
    public void testEventExpirationValue() throws Exception {
        String drl1 = "package org.drools.pkg1\n" +
                      "import org.drools.compiler.StockTick\n" +
                      "declare StockTick\n" +
                      "    @role(event)\n" +
                      "end\n" +
                      "rule X\n" +
                      "when\n" +
                      "    StockTick()\n" +
                      "then\n" +
                      "end\n";
        String drl2 = "package org.drools.pkg2\n" +
                      "import org.drools.compiler.StockTick\n" +
                      "declare StockTick\n" +
                      "    @role(event)\n" +
                      "end\n" +
                      "rule X\n" +
                      "when\n" +
                      "    StockTick()\n" +
                      "then\n" +
                      "end\n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(drl1.getBytes()),
                     ResourceType.DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(drl2.getBytes()),
                     ResourceType.DRL);
        assertFalse(kbuilder.getErrors().toString(),
                    kbuilder.hasErrors());
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        List<ObjectTypeNode> otns = ( (KnowledgeBaseImpl) kbase ).getRete().getObjectTypeNodes();
        ObjectType stot = new ClassObjectType( StockTick.class );
        for (ObjectTypeNode otn : otns) {
            if (otn.getObjectType().isAssignableFrom( stot )) {
                assertEquals( -1,
                              otn.getExpirationOffset() );
            }
        }
    }

    @Test(timeout=10000)
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
        
        KnowledgeBase kbase = loadKnowledgeBaseFromString( (KieBaseConfiguration)null, drl );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        assertNotNull(ksession.getEntryPoint("UsedEntryPoint"));
        assertNotNull(ksession.getEntryPoint("UnusedEntryPoint"));

        ksession.dispose();
    }

    public void testWindowDeclaration() throws Exception {
        String drl = "package org.drools.compiler\n" +
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
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = loadKnowledgeBaseFromString(kconf,
                                                          drl);

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        AgendaEventListener ael = mock(AgendaEventListener.class);
        ksession.addEventListener(ael);

        EntryPoint ep = ksession.getEntryPoint("ticks");
        ep.insert(new StockTick(1, "ACME", 20, 1000)); // not in the window
        ep.insert(new StockTick(2, "RHT", 20, 1000)); // not > 20
        ep.insert(new StockTick(3, "RHT", 30, 1000));
        ep.insert(new StockTick(4, "ACME", 30, 1000)); // not in the window
        ep.insert(new StockTick(5, "RHT", 25, 1000));
        ep.insert(new StockTick(6, "ACME", 10, 1000)); // not in the window
        ep.insert(new StockTick(7, "RHT", 10, 1000)); // not > 20
        ep.insert(new StockTick(8, "RHT", 40, 1000));

        ksession.fireAllRules();

        ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
        verify(ael,
               times(1)).afterMatchFired(captor.capture());

        AfterMatchFiredEvent aafe = captor.getValue();
        Assert.assertThat(((Number) aafe.getMatch().getDeclarationValue("$sum")).intValue(),
                          is(95));
        Assert.assertThat(((Number) aafe.getMatch().getDeclarationValue("$cnt")).intValue(),
                          is(3));

    }

    @Test(timeout=10000)
    public void testWindowDeclaration2() throws Exception {
        String drl = "package org.drools.compiler\n" +
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
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = loadKnowledgeBaseFromString(kconf,
                                                          drl);

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        AgendaEventListener ael = mock(AgendaEventListener.class);
        ksession.addEventListener(ael);

        EntryPoint ep = ksession.getEntryPoint("data");
        ep.insert(Double.valueOf( 10 )); 
        ep.insert(Double.valueOf( 11 )); 
        ep.insert(Double.valueOf( 12 )); 

        ksession.fireAllRules();

        ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
        verify(ael,
               times(1)).afterMatchFired(captor.capture());

        AfterMatchFiredEvent aafe = captor.getValue();
        Assert.assertThat(((Number) aafe.getMatch().getDeclarationValue("$sum")).intValue(),
                          is(33));
    }
    
    @Test (timeout=10000)
    public void testMultipleWindows() throws Exception {
        String drl = "package org.drools.compiler\n" +
                     "declare StockTick\n" + 
                     "    @role(event)\n" + 
                     "end\n" + 
                     "rule FaultsCoincide\n" + 
                     "when\n" + 
                     "   f1 : StockTick( company == \"RHT\" ) over window:length( 1 )\n" + 
                     "   f2 : StockTick( company == \"JBW\" ) over window:length( 1 )\n" + 
                     "then\n" + 
                     "end";
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = loadKnowledgeBaseFromString(kconf,
                                                          drl);

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        AgendaEventListener ael = mock(AgendaEventListener.class);
        ksession.addEventListener(ael);
        
        StockTick st1 = new StockTick(1, "RHT", 10, 1000);
        ksession.insert( st1 );
        StockTick st2 = new StockTick(2, "JBW", 10, 1000);
        ksession.insert( st2 );
        
        ksession.fireAllRules();

        ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
        verify(ael,
               times(1)).afterMatchFired(captor.capture());

        AfterMatchFiredEvent aafe = captor.getValue();
        Assert.assertThat( (StockTick) aafe.getMatch().getDeclarationValue("f1"),
                           is(st1));
        Assert.assertThat( (StockTick) aafe.getMatch().getDeclarationValue("f2"),
                           is(st2));
    }

    @Test(timeout=10000)
    public void testWindowWithEntryPointCompilationError() {
        String str = "import org.drools.compiler.Cheese;\n" +
                "declare window X\n" +
                "   Cheese( type == \"gorgonzola\" ) over window:time(1m) from entry-point Z\n" +
                "end\n" +
                "rule R when\n" +
                "   $c : Cheese( price < 100 ) from window X\n" +
                "then\n" +
                "   System.out.println($c);\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()),
                      ResourceType.DRL );
        
        assertTrue( "Should have raised a compilation error as Cheese is not declared as an event.",
                    kbuilder.hasErrors() );
    }
    
    
    @Test(timeout=10000)
    public void testAtomicActivationFiring() throws Exception {
        // JBRULES-3383
        String str = "package org.drools.compiler.test\n" +
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

        KieBaseConfiguration kBaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kBaseConfig.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = loadKnowledgeBaseFromString(kBaseConfig, str);
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        
        ksession.addEventListener(new org.kie.api.event.rule.DebugAgendaEventListener());

        FactType eventType = kbase.getFactType("org.drools.compiler.test", "Event");

        Object event = eventType.newInstance();
        eventType.set(event, "name", "myName");
        ksession.insert( event );

        ksession.fireUntilHalt();
    }
}
