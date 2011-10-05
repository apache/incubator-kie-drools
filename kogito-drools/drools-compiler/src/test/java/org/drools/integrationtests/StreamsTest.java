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
package org.drools.integrationtests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.internal.matchers.IsCollectionContaining.hasItems;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mvel2.MVEL;

import static org.junit.Assert.*;

import org.drools.ClockType;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.StockTick;
import org.drools.StockTickInterface;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.DroolsParserException;
import org.drools.conf.EventProcessingOption;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.io.ResourceFactory;
import org.drools.rule.EntryPoint;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.time.impl.PseudoClockScheduler;

/**
 * Tests related to the stream support features
 */
public class StreamsTest {

    private KnowledgeBase loadKnowledgeBase( final String fileName ) throws IOException,
                                                                    DroolsParserException,
                                                                    Exception {
        return loadKnowledgeBase( fileName,
                                  KnowledgeBaseFactory.newKnowledgeBaseConfiguration() );
    }

    private KnowledgeBase loadKnowledgeBase( final String fileName,
                                             KnowledgeBaseConfiguration kconf ) throws IOException,
                                                                               DroolsParserException,
                                                                               Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( fileName,
                                                            getClass() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
            return null;
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kconf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        return SerializationHelper.serializeObject( kbase );
    }

    private KnowledgeBase loadKnowledgeBaseFromString( String... drlContentStrings ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for ( String drlContentString : drlContentStrings ) {
            kbuilder.add( ResourceFactory.newByteArrayResource( drlContentString.getBytes() ),
                          ResourceType.DRL );
        }

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

    @Test
    public void testEventAssertion() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_EntryPoint.drl" );
        //final RuleBase ruleBase = loadRuleBase( reader );

        KnowledgeSessionConfiguration conf = new SessionConfiguration();
        ((SessionConfiguration) conf).setClockType( ClockType.PSEUDO_CLOCK );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession( conf,
                                                                              null );

        final List results = new ArrayList();

        session.setGlobal( "results",
                           results );

        StockTickInterface tick1 = new StockTick( 1,
                                                  "DROO",
                                                  50,
                                                  System.currentTimeMillis() );
        StockTickInterface tick2 = new StockTick( 2,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis() );
        StockTickInterface tick3 = new StockTick( 3,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis() );
        StockTickInterface tick4 = new StockTick( 4,
                                                  "DROO",
                                                  50,
                                                  System.currentTimeMillis() );

        InternalFactHandle handle1 = (InternalFactHandle) session.insert( tick1 );
        InternalFactHandle handle2 = (InternalFactHandle) session.insert( tick2 );
        InternalFactHandle handle3 = (InternalFactHandle) session.insert( tick3 );
        InternalFactHandle handle4 = (InternalFactHandle) session.insert( tick4 );

        assertNotNull( handle1 );
        assertNotNull( handle2 );
        assertNotNull( handle3 );
        assertNotNull( handle4 );

        assertTrue( handle1.isEvent() );
        assertTrue( handle2.isEvent() );
        assertTrue( handle3.isEvent() );
        assertTrue( handle4.isEvent() );

        session.fireAllRules();

        assertEquals( 0,
                      results.size() );

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
                                                  15,
                                                  System.currentTimeMillis() );
        StockTickInterface tick8 = new StockTick( 8,
                                                  "DROO",
                                                  50,
                                                  System.currentTimeMillis() );

        WorkingMemoryEntryPoint entry = session.getWorkingMemoryEntryPoint( "StockStream" );

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

        assertEquals( 1,
                      results.size() );
        assertSame( tick7,
                    results.get( 0 ) );

    }

    @Test
    public void testEntryPointReference() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_EntryPointReference.drl" );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        final List<StockTick> results = new ArrayList<StockTick>();
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

        WorkingMemoryEntryPoint entry = session.getWorkingMemoryEntryPoint( "stream1" );

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

        assertEquals( 1,
                      results.size() );
        assertSame( tick7,
                    results.get( 0 ) );

    }

    @Test
    public void testModifyRetracOnEntryPointFacts() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_modifyRetractEntryPoint.drl" );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        final List< ? extends Number> results = new ArrayList<Number>();
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

        WorkingMemoryEntryPoint entry = session.getWorkingMemoryEntryPoint( "stream1" );

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

        System.out.println( results );
        assertEquals( 2,
                      results.size() );
        assertEquals( 30,
                      ((Number) results.get( 0 )).intValue() );
        assertEquals( 110,
                      ((Number) results.get( 1 )).intValue() );

        // the 3 non-matched facts continue to exist in the entry point
        assertEquals( 3,
                      entry.getObjects().size() );
        // but no fact was inserted into the main session
        assertEquals( 0,
                      session.getObjects().size() );

    }

    @Test
    public void testModifyOnEntryPointFacts() throws Exception {
        String str = "package org.drools\n" +
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
        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        org.drools.event.rule.AgendaEventListener ael = mock( org.drools.event.rule.AgendaEventListener.class );
        ksession.addEventListener( ael );
        
        WorkingMemoryEntryPoint ep1 = ksession.getWorkingMemoryEntryPoint( "ep1" );
        WorkingMemoryEntryPoint ep2 = ksession.getWorkingMemoryEntryPoint( "ep2" );
        WorkingMemoryEntryPoint ep3 = ksession.getWorkingMemoryEntryPoint( "ep3" );

        ep1.insert( new StockTick( 1, "RHT", 10, 1000 ) );
        ep2.insert( new StockTick( 1, "RHT", 10, 1000 ) );
        ep3.insert( new StockTick( 1, "RHT", 10, 1000 ) );
        int rulesFired = ksession.fireAllRules();
        assertEquals( 3,
                      rulesFired );

        ArgumentCaptor<org.drools.event.rule.AfterActivationFiredEvent> captor = ArgumentCaptor.forClass( org.drools.event.rule.AfterActivationFiredEvent.class );
        verify( ael,
                times( 3 ) ).afterActivationFired( captor.capture() );
        List<org.drools.event.rule.AfterActivationFiredEvent> aafe = captor.getAllValues();

        Assert.assertThat( aafe.get( 0 ).getActivation().getRule().getName(),
                           is( "R1" ) );
        Assert.assertThat( aafe.get( 1 ).getActivation().getRule().getName(),
                           is( "R2" ) );
        Assert.assertThat( aafe.get( 2 ).getActivation().getRule().getName(),
                           is( "R3" ) );
    }

    @Test
    public void testGetEntryPointList() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_EntryPointReference.drl" );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        WorkingMemoryEntryPoint def = session.getWorkingMemoryEntryPoint( EntryPoint.DEFAULT.getEntryPointId() );
        WorkingMemoryEntryPoint s1 = session.getWorkingMemoryEntryPoint( "stream1" );
        WorkingMemoryEntryPoint s2 = session.getWorkingMemoryEntryPoint( "stream2" );
        WorkingMemoryEntryPoint s3 = session.getWorkingMemoryEntryPoint( "stream3" );
        Collection< ? extends WorkingMemoryEntryPoint> eps = session.getWorkingMemoryEntryPoints();

        assertEquals( 4,
                      eps.size() );
        assertTrue( eps.contains( def ) );
        assertTrue( eps.contains( s1 ) );
        assertTrue( eps.contains( s2 ) );
        assertTrue( eps.contains( s3 ) );
    }

    @Test
    public void testEventDoesNotExpireIfNotInPattern() throws Exception {
        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBase( "test_EventExpiration.drl",
                                                 kconf );

        KnowledgeSessionConfiguration ksessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksessionConfig.setOption( ClockTypeOption.get( "pseudo" ) );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( ksessionConfig,
                                                                               null );

        WorkingMemoryEventListener wml = mock( WorkingMemoryEventListener.class );
        ksession.addEventListener( wml );

        PseudoClockScheduler clock = ksession.getSessionClock();

        final StockTickInterface st1 = new StockTick( 1,
                                                      "RHT",
                                                      100,
                                                      1000 );
        final StockTickInterface st2 = new StockTick( 2,
                                                      "RHT",
                                                      100,
                                                      1000 );

        ksession.insert( st1 );
        ksession.insert( st2 );

        verify( wml,
                times( 2 ) ).objectInserted( any( org.drools.event.rule.ObjectInsertedEvent.class ) );
        assertThat( ksession.getObjects().size(),
                    equalTo( 2 ) );
        assertThat( ksession.getObjects(),
                    hasItems( (Object) st1,
                              st2 ) );

        ksession.fireAllRules();

        clock.advanceTime( 3,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();

        assertThat( ksession.getObjects().size(),
                    equalTo( 0 ) );
    }

    @Test
    public void testEventExpirationSetToZero() throws Exception {
        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBase( "test_EventExpirationSetToZero.drl",
                                                 kconf );

        KnowledgeSessionConfiguration ksessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksessionConfig.setOption( ClockTypeOption.get( "pseudo" ) );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( ksessionConfig,
                                                                               null );

        WorkingMemoryEventListener wml = mock( WorkingMemoryEventListener.class );
        ksession.addEventListener( wml );
        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        PseudoClockScheduler clock = ksession.getSessionClock();

        final StockTickInterface st1 = new StockTick( 1,
                                                      "RHT",
                                                      100,
                                                      1000 );
        final StockTickInterface st2 = new StockTick( 2,
                                                      "RHT",
                                                      100,
                                                      1000 );

        ksession.insert( st1 );
        ksession.insert( st2 );

        verify( wml,
                times( 2 ) ).objectInserted( any( org.drools.event.rule.ObjectInsertedEvent.class ) );
        verify( ael,
                times( 2 ) ).activationCreated( any( ActivationCreatedEvent.class ) );
        assertThat( ksession.getObjects().size(),
                    equalTo( 2 ) );
        assertThat( ksession.getObjects(),
                    hasItems( (Object) st1,
                              st2 ) );

        int fired = ksession.fireAllRules();

        assertThat( fired,
                    equalTo( 2 ) );

        clock.advanceTime( 3,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();

        assertThat( ksession.getObjects().size(),
                    equalTo( 0 ) );
    }
}
