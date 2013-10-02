package org.drools.integrationtests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.drools.ClockType;
import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.OrderEvent;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.Sensor;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.StockTick;
import org.drools.StockTickInterface;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.base.ClassObjectType;
import org.drools.base.evaluators.TimeIntervalParser;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.Scheduler;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.conf.AssertBehaviorOption;
import org.drools.conf.EventProcessingOption;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.definition.KnowledgePackage;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.event.rule.DebugWorkingMemoryEventListener;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.PackageDescr;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.rule.EntryPoint;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.runtime.Channel;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.conf.TimerJobFactoryOption;
import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.ObjectType;
import org.drools.time.SessionClock;
import org.drools.time.SessionPseudoClock;
import org.drools.time.impl.DurationTimer;
import org.drools.time.impl.PseudoClockScheduler;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CepEspTest extends CommonTestMethodBase {
    
    private RuleBase loadRuleBase( final Reader reader,
                                   final RuleBaseConfiguration conf ) throws IOException,
                                                                     DroolsParserException,
                                                                     Exception {
        final PackageBuilder builder = new PackageBuilder();
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            System.out.println( parser.getErrors() );
            fail( parser.getErrors().toString() );
        }
        // pre build the package
        builder.addPackage( packageDescr );
        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        RuleBase ruleBase = getRuleBase( conf );
        ruleBase.addPackage( pkg );
        // load up the rulebase
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        return ruleBase;
    }

    private KnowledgeBase loadKnowledgeBase( final Reader reader,
                                             final KnowledgeBaseConfiguration conf ) throws IOException,
                                                                                    ClassNotFoundException {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( reader ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        // add the packages to a rulebase
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( conf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        kbase = SerializationHelper.serializeObject( kbase );
        return kbase;
    }

    private KnowledgeBase loadKnowledgeBase( final String resource,
                                             final KnowledgeBaseConfiguration conf,
                                             final boolean serialize ) throws IOException,
                                                                      ClassNotFoundException {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( resource,
                                                            CepEspTest.class ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        // add the packages to a rulebase
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( conf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        if ( serialize ) {
            kbase = SerializationHelper.serializeObject( kbase );
        }
        return kbase;
    }
    
    protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) { 
        return kbase.newStatefulKnowledgeSession();
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase, KnowledgeSessionConfiguration conf) { 
        return kbase.newStatefulKnowledgeSession(conf, null);
    }

    @Test
    public void testComplexTimestamp() {
        String rule = "";
        rule += "package " + Message.class.getPackage().getName() + "\n" +
                "declare " + Message.class.getCanonicalName() + "\n" +
                 "   @role( event ) \n" +
                 "   @timestamp( getProperties().get( 'timestamp' )-1 ) \n" +
                 "   @duration( getProperties().get( 'duration' )+1 ) \n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( rule ) ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Message msg = new Message();
        Properties props = new Properties();
        props.put( "timestamp",
                   new Integer( 99 ) );
        props.put( "duration",
                   new Integer( 52 ) );
        msg.setProperties( props );

        EventFactHandle efh = (EventFactHandle) ksession.insert( msg );
        assertEquals( 98,
                      efh.getStartTimestamp() );
        assertEquals( 53,
                      efh.getDuration() );

    }

    @Test
    public void testEventAssertion() throws Exception {
        // read in the source
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_CEP_SimpleEventAssertion.drl" ) ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        StatefulKnowledgeSession session = createKnowledgeSession(kbase, conf);

        SessionPseudoClock clock = (SessionPseudoClock) session.<SessionClock>getSessionClock();

        final List results = new ArrayList();

        session.setGlobal( "results",
                           results );

        StockTickInterface tick1 = new StockTick( 1,
                                                  "DROO",
                                                  50,
                                                  10000 );
        StockTickInterface tick2 = new StockTick( 2,
                                                  "ACME",
                                                  10,
                                                  10010 );
        StockTickInterface tick3 = new StockTick( 3,
                                                  "ACME",
                                                  10,
                                                  10100 );
        StockTickInterface tick4 = new StockTick( 4,
                                                  "DROO",
                                                  50,
                                                  11000 );

        InternalFactHandle handle1 = (InternalFactHandle) session.insert( tick1 );
        clock.advanceTime( 10,
                           TimeUnit.SECONDS );
        InternalFactHandle handle2 = (InternalFactHandle) session.insert( tick2 );
        clock.advanceTime( 30,
                           TimeUnit.SECONDS );
        InternalFactHandle handle3 = (InternalFactHandle) session.insert( tick3 );
        clock.advanceTime( 20,
                           TimeUnit.SECONDS );
        InternalFactHandle handle4 = (InternalFactHandle) session.insert( tick4 );
        clock.advanceTime( 10,
                           TimeUnit.SECONDS );

        assertNotNull( handle1 );
        assertNotNull( handle2 );
        assertNotNull( handle3 );
        assertNotNull( handle4 );

        assertTrue( handle1.isEvent() );
        assertTrue( handle2.isEvent() );
        assertTrue( handle3.isEvent() );
        assertTrue( handle4.isEvent() );

        session.fireAllRules();

        assertEquals( 2,
                      ((List) session.getGlobal( "results" )).size() );

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPackageSerializationWithEvents() throws Exception {
        // read in the source
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_CEP_SimpleEventAssertion.drl" ) ),
                      ResourceType.DRL );

        // get the package
        Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();
        assertEquals( 2,
                      pkgs.size() );

        // serialize the package
        byte[] serializedPkg = DroolsStreamUtils.streamOut( pkgs );
        pkgs = (Collection<KnowledgePackage>) DroolsStreamUtils.streamIn( serializedPkg );

        // create the kbase
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( pkgs );

        // create the session
        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        StatefulKnowledgeSession session = createKnowledgeSession(kbase, conf);

        final List<StockTick> results = new ArrayList<StockTick>();

        session.setGlobal( "results",
                           results );

        StockTickInterface tick1 = new StockTick( 1,
                                                  "DROO",
                                                  50,
                                                  10000 );
        StockTickInterface tick2 = new StockTick( 2,
                                                  "ACME",
                                                  10,
                                                  10010 );

        InternalFactHandle handle1 = (InternalFactHandle) session.insert( tick1 );
        InternalFactHandle handle2 = (InternalFactHandle) session.insert( tick2 );

        assertNotNull( handle1 );
        assertNotNull( handle2 );

        assertTrue( handle1.isEvent() );
        assertTrue( handle2.isEvent() );

        session.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( tick2,
                      results.get( 0 ) );

    }

    @Test
    public void testEventAssertionWithDuration() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_SimpleEventAssertionWithDuration.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.PSEUDO_CLOCK );
        StatefulSession wm = ruleBase.newStatefulSession( conf,
                                                          null );

        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        StockTickInterface tick1 = new StockTick( 1,
                                                  "DROO",
                                                  50,
                                                  10000,
                                                  5 );
        StockTickInterface tick2 = new StockTick( 2,
                                                  "ACME",
                                                  10,
                                                  11000,
                                                  10 );
        StockTickInterface tick3 = new StockTick( 3,
                                                  "ACME",
                                                  10,
                                                  12000,
                                                  8 );
        StockTickInterface tick4 = new StockTick( 4,
                                                  "DROO",
                                                  50,
                                                  13000,
                                                  7 );

        InternalFactHandle handle1 = (InternalFactHandle) wm.insert( tick1 );
        InternalFactHandle handle2 = (InternalFactHandle) wm.insert( tick2 );
        InternalFactHandle handle3 = (InternalFactHandle) wm.insert( tick3 );
        InternalFactHandle handle4 = (InternalFactHandle) wm.insert( tick4 );

        assertNotNull( handle1 );
        assertNotNull( handle2 );
        assertNotNull( handle3 );
        assertNotNull( handle4 );

        assertTrue( handle1.isEvent() );
        assertTrue( handle2.isEvent() );
        assertTrue( handle3.isEvent() );
        assertTrue( handle4.isEvent() );

        EventFactHandle eh1 = (EventFactHandle) handle1;
        EventFactHandle eh2 = (EventFactHandle) handle2;
        EventFactHandle eh3 = (EventFactHandle) handle3;
        EventFactHandle eh4 = (EventFactHandle) handle4;

        assertEquals( tick1.getTime(),
                      eh1.getStartTimestamp() );
        assertEquals( tick2.getTime(),
                      eh2.getStartTimestamp() );
        assertEquals( tick3.getTime(),
                      eh3.getStartTimestamp() );
        assertEquals( tick4.getTime(),
                      eh4.getStartTimestamp() );

        assertEquals( tick1.getDuration(),
                      eh1.getDuration() );
        assertEquals( tick2.getDuration(),
                      eh2.getDuration() );
        assertEquals( tick3.getDuration(),
                      eh3.getDuration() );
        assertEquals( tick4.getDuration(),
                      eh4.getDuration() );

        wm.fireAllRules();

        assertEquals( 2,
                      results.size() );

    }

    @Test
    public void testEventAssertionWithDateTimestamp() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_SimpleEventAssertionWithDateTimestamp.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.PSEUDO_CLOCK );
        StatefulSession wm = ruleBase.newStatefulSession( conf,
                                                          null );

        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        StockTickInterface tick1 = new StockTick( 1,
                                                  "DROO",
                                                  50,
                                                  10000,
                                                  5 );
        StockTickInterface tick2 = new StockTick( 2,
                                                  "ACME",
                                                  10,
                                                  11000,
                                                  10 );
        StockTickInterface tick3 = new StockTick( 3,
                                                  "ACME",
                                                  10,
                                                  12000,
                                                  8 );
        StockTickInterface tick4 = new StockTick( 4,
                                                  "DROO",
                                                  50,
                                                  13000,
                                                  7 );

        InternalFactHandle handle1 = (InternalFactHandle) wm.insert( tick1 );
        InternalFactHandle handle2 = (InternalFactHandle) wm.insert( tick2 );
        InternalFactHandle handle3 = (InternalFactHandle) wm.insert( tick3 );
        InternalFactHandle handle4 = (InternalFactHandle) wm.insert( tick4 );

        assertNotNull( handle1 );
        assertNotNull( handle2 );
        assertNotNull( handle3 );
        assertNotNull( handle4 );

        assertTrue( handle1.isEvent() );
        assertTrue( handle2.isEvent() );
        assertTrue( handle3.isEvent() );
        assertTrue( handle4.isEvent() );

        EventFactHandle eh1 = (EventFactHandle) handle1;
        EventFactHandle eh2 = (EventFactHandle) handle2;
        EventFactHandle eh3 = (EventFactHandle) handle3;
        EventFactHandle eh4 = (EventFactHandle) handle4;

        assertEquals( tick1.getTime(),
                      eh1.getStartTimestamp() );
        assertEquals( tick2.getTime(),
                      eh2.getStartTimestamp() );
        assertEquals( tick3.getTime(),
                      eh3.getStartTimestamp() );
        assertEquals( tick4.getTime(),
                      eh4.getStartTimestamp() );

        wm.fireAllRules();

        assertEquals( 2,
                      results.size() );

    }

    @Test
    public void testEventExpiration() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_EventExpiration.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final InternalRuleBase internal = (InternalRuleBase) ruleBase;
        final TimeIntervalParser parser = new TimeIntervalParser();

        assertEquals( parser.parse( "1h30m" )[0].longValue(),
                      internal.getTypeDeclaration( StockTick.class ).getExpirationOffset() );
    }

    @Test
    public void testEventExpiration2() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_EventExpiration2.drl" ) );
        final RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setEventProcessingMode( EventProcessingOption.STREAM );
        final RuleBase ruleBase = loadRuleBase( reader,
                                                conf );

        final InternalRuleBase internal = (InternalRuleBase) ruleBase;
        final TimeIntervalParser parser = new TimeIntervalParser();

        Map<ObjectType, ObjectTypeNode> objectTypeNodes = internal.getRete().getObjectTypeNodes( EntryPoint.DEFAULT );
        ObjectTypeNode node = objectTypeNodes.get( new ClassObjectType( StockTick.class ) );

        assertNotNull( node );

        // the expiration policy @expires(10m) should override the temporal operator usage 
        assertEquals( parser.parse( "10m" )[0].longValue() + 1,
                      node.getExpirationOffset() );
    }

    @Test
    public void testEventExpiration3() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_EventExpiration3.drl" ) );
        final RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setEventProcessingMode( EventProcessingOption.STREAM );
        final RuleBase ruleBase = loadRuleBase( reader,
                                                conf );

        final InternalRuleBase internal = (InternalRuleBase) ruleBase;
        final TimeIntervalParser parser = new TimeIntervalParser();

        Map<ObjectType, ObjectTypeNode> objectTypeNodes = internal.getRete().getObjectTypeNodes( EntryPoint.DEFAULT );
        ObjectTypeNode node = objectTypeNodes.get( new ClassObjectType( StockTick.class ) );

        assertNotNull( node );

        // the expiration policy @expires(10m) should override the temporal operator usage 
        assertEquals( parser.parse( "10m" )[0].longValue() + 1,
                      node.getExpirationOffset() );
    }

    @Test
    public void testEventExpiration4() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_EventExpiration4.drl" ) );
        final KnowledgeBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( reader,
                                                       conf );

        final KnowledgeSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( "pseudo" ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, sconf);

        WorkingMemoryEntryPoint eventStream = ksession.getWorkingMemoryEntryPoint( "Event Stream" );

        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();

        final List results = new ArrayList();
        ksession.setGlobal( "results",
                            results );

        EventFactHandle handle1 = (EventFactHandle) eventStream.insert( new StockTick( 1,
                                                                                       "ACME",
                                                                                       50,
                                                                                       System.currentTimeMillis(),
                                                                                       3 ) );

        ksession.fireAllRules();

        clock.advanceTime( 11,
                           TimeUnit.SECONDS );
        /** clock.advance() will put the event expiration in the queue to be executed, 
            but it has to wait for a "thread" to do that
            so we fire rules again here to get that
            alternative could run fireUntilHalt() **/
        ksession.fireAllRules();

        assertTrue( results.size() == 1 );
        assertTrue( handle1.isExpired() );
        assertFalse( ksession.getFactHandles().contains( handle1 ) );
    }

    @Test
    public void testTimeRelationalOperators() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_TimeRelationalOperators.drl" ) );
        final RuleBaseConfiguration rbconf = new RuleBaseConfiguration();
        rbconf.setEventProcessingMode( EventProcessingOption.STREAM );
        final RuleBase ruleBase = loadRuleBase( reader,
                                                rbconf );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.PSEUDO_CLOCK );
        StatefulSession wm = ruleBase.newStatefulSession( conf,
                                                          null );

        final PseudoClockScheduler clock = (PseudoClockScheduler) wm.getSessionClock();

        clock.setStartupTime( 1000 );
        final List results_coincides = new ArrayList();
        final List results_before = new ArrayList();
        final List results_after = new ArrayList();
        final List results_meets = new ArrayList();
        final List results_met_by = new ArrayList();
        final List results_overlaps = new ArrayList();
        final List results_overlapped_by = new ArrayList();
        final List results_during = new ArrayList();
        final List results_includes = new ArrayList();
        final List results_starts = new ArrayList();
        final List results_started_by = new ArrayList();
        final List results_finishes = new ArrayList();
        final List results_finished_by = new ArrayList();

        wm.setGlobal( "results_coincides",
                      results_coincides );
        wm.setGlobal( "results_before",
                      results_before );
        wm.setGlobal( "results_after",
                      results_after );
        wm.setGlobal( "results_meets",
                      results_meets );
        wm.setGlobal( "results_met_by",
                      results_met_by );
        wm.setGlobal( "results_overlaps",
                      results_overlaps );
        wm.setGlobal( "results_overlapped_by",
                      results_overlapped_by );
        wm.setGlobal( "results_during",
                      results_during );
        wm.setGlobal( "results_includes",
                      results_includes );
        wm.setGlobal( "results_starts",
                      results_starts );
        wm.setGlobal( "results_started_by",
                      results_started_by );
        wm.setGlobal( "results_finishes",
                      results_finishes );
        wm.setGlobal( "results_finished_by",
                      results_finished_by );

        StockTickInterface tick1 = new StockTick( 1,
                                                  "DROO",
                                                  50,
                                                  System.currentTimeMillis(),
                                                  3 );
        StockTickInterface tick2 = new StockTick( 2,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  3 );
        StockTickInterface tick3 = new StockTick( 3,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  3 );
        StockTickInterface tick4 = new StockTick( 4,
                                                  "DROO",
                                                  50,
                                                  System.currentTimeMillis(),
                                                  5 );
        StockTickInterface tick5 = new StockTick( 5,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  5 );
        StockTickInterface tick6 = new StockTick( 6,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  3 );
        StockTickInterface tick7 = new StockTick( 7,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  5 );
        StockTickInterface tick8 = new StockTick( 8,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  3 );

        InternalFactHandle handle1 = (InternalFactHandle) wm.insert( tick1 );
        clock.advanceTime( 4,
                           TimeUnit.MILLISECONDS );
        InternalFactHandle handle2 = (InternalFactHandle) wm.insert( tick2 );
        clock.advanceTime( 4,
                           TimeUnit.MILLISECONDS );
        InternalFactHandle handle3 = (InternalFactHandle) wm.insert( tick3 );
        clock.advanceTime( 4,
                           TimeUnit.MILLISECONDS );
        InternalFactHandle handle4 = (InternalFactHandle) wm.insert( tick4 );
        InternalFactHandle handle5 = (InternalFactHandle) wm.insert( tick5 );
        clock.advanceTime( 1,
                           TimeUnit.MILLISECONDS );
        InternalFactHandle handle6 = (InternalFactHandle) wm.insert( tick6 );
        InternalFactHandle handle7 = (InternalFactHandle) wm.insert( tick7 );
        clock.advanceTime( 2,
                           TimeUnit.MILLISECONDS );
        InternalFactHandle handle8 = (InternalFactHandle) wm.insert( tick8 );

        assertNotNull( handle1 );
        assertNotNull( handle2 );
        assertNotNull( handle3 );
        assertNotNull( handle4 );
        assertNotNull( handle5 );
        assertNotNull( handle6 );
        assertNotNull( handle7 );
        assertNotNull( handle8 );

        assertTrue( handle1.isEvent() );
        assertTrue( handle2.isEvent() );
        assertTrue( handle3.isEvent() );
        assertTrue( handle4.isEvent() );
        assertTrue( handle6.isEvent() );
        assertTrue( handle7.isEvent() );
        assertTrue( handle8.isEvent() );

        // TODO: serialization needs to be fixed
        //wm = SerializationHelper.getSerialisedStatefulKnowledgeSession( wm, true );
        wm.fireAllRules();

        assertEquals( 1,
                      results_coincides.size() );
        assertEquals( tick5,
                      results_coincides.get( 0 ) );

        assertEquals( 1,
                      results_before.size() );
        assertEquals( tick2,
                      results_before.get( 0 ) );

        assertEquals( 1,
                      results_after.size() );
        assertEquals( tick3,
                      results_after.get( 0 ) );

        assertEquals( 1,
                      results_meets.size() );
        assertEquals( tick3,
                      results_meets.get( 0 ) );

        assertEquals( 1,
                      results_met_by.size() );
        assertEquals( tick2,
                      results_met_by.get( 0 ) );

        assertEquals( 1,
                      results_met_by.size() );
        assertEquals( tick2,
                      results_met_by.get( 0 ) );

        assertEquals( 1,
                      results_overlaps.size() );
        assertEquals( tick4,
                      results_overlaps.get( 0 ) );

        assertEquals( 1,
                      results_overlapped_by.size() );
        assertEquals( tick8,
                      results_overlapped_by.get( 0 ) );

        assertEquals( 1,
                      results_during.size() );
        assertEquals( tick6,
                      results_during.get( 0 ) );

        assertEquals( 1,
                      results_includes.size() );
        assertEquals( tick4,
                      results_includes.get( 0 ) );

        assertEquals( 1,
                      results_starts.size() );
        assertEquals( tick6,
                      results_starts.get( 0 ) );

        assertEquals( 1,
                      results_started_by.size() );
        assertEquals( tick7,
                      results_started_by.get( 0 ) );

        assertEquals( 1,
                      results_finishes.size() );
        assertEquals( tick8,
                      results_finishes.get( 0 ) );

        assertEquals( 1,
                      results_finished_by.size() );
        assertEquals( tick7,
                      results_finished_by.get( 0 ) );

    }

    @Test
    public void testBeforeOperator() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_BeforeOperator.drl" ) );
        final KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( reader,
                                                       kconf );

        KnowledgeSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, sconf);

        final PseudoClockScheduler clock = (PseudoClockScheduler) ksession.<SessionClock>getSessionClock();
        clock.setStartupTime( 1000 );

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        StockTickInterface tick1 = new StockTick( 1,
                                                  "DROO",
                                                  50,
                                                  System.currentTimeMillis(),
                                                  3 );
        StockTickInterface tick2 = new StockTick( 2,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  3 );
        StockTickInterface tick3 = new StockTick( 3,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  3 );
        StockTickInterface tick4 = new StockTick( 4,
                                                  "DROO",
                                                  50,
                                                  System.currentTimeMillis(),
                                                  5 );
        StockTickInterface tick5 = new StockTick( 5,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  5 );
        StockTickInterface tick6 = new StockTick( 6,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  3 );
        StockTickInterface tick7 = new StockTick( 7,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  5 );
        StockTickInterface tick8 = new StockTick( 8,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  3 );

        ksession.insert( tick1 );
        clock.advanceTime( 4,
                           TimeUnit.MILLISECONDS );
        ksession.insert( tick2 );
        clock.advanceTime( 4,
                           TimeUnit.MILLISECONDS );
        ksession.insert( tick3 );
        clock.advanceTime( 4,
                           TimeUnit.MILLISECONDS );
        ksession.insert( tick4 );
        ksession.insert( tick5 );
        clock.advanceTime( 1,
                           TimeUnit.MILLISECONDS );
        ksession.insert( tick6 );
        ksession.insert( tick7 );
        clock.advanceTime( 2,
                           TimeUnit.MILLISECONDS );
        ksession.insert( tick8 );

        ArgumentCaptor<ActivationCreatedEvent> arg = ArgumentCaptor.forClass( ActivationCreatedEvent.class );
        verify( ael ).activationCreated( arg.capture() );
        assertThat( arg.getValue().getActivation().getRule().getName(),
                    is( "before" ) );

        ksession.fireAllRules();

        verify( ael ).afterActivationFired( any( AfterActivationFiredEvent.class ) );
    }

    @Test
    public void testComplexOperator() throws Exception {
        // read in the source
        KnowledgeBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_ComplexOperator.drl" );

        KnowledgeSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase, sconf );

        final PseudoClockScheduler clock = (PseudoClockScheduler) ksession.<SessionClock>getSessionClock();
        clock.setStartupTime( 1000 );

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        StockTickInterface tick1 = new StockTick( 1,
                                                  "DROO",
                                                  50,
                                                  0,
                                                  3 );
        StockTickInterface tick2 = new StockTick( 2,
                                                  "ACME",
                                                  10,
                                                  4,
                                                  3 );
        StockTickInterface tick3 = new StockTick( 3,
                                                  "ACME",
                                                  10,
                                                  8,
                                                  3 );
        StockTickInterface tick4 = new StockTick( 4,
                                                  "DROO",
                                                  50,
                                                  12,
                                                  5 );
        StockTickInterface tick5 = new StockTick( 5,
                                                  "ACME",
                                                  10,
                                                  12,
                                                  5 );
        StockTickInterface tick6 = new StockTick( 6,
                                                  "ACME",
                                                  10,
                                                  13,
                                                  3 );
        StockTickInterface tick7 = new StockTick( 7,
                                                  "ACME",
                                                  10,
                                                  13,
                                                  5 );
        StockTickInterface tick8 = new StockTick( 8,
                                                  "ACME",
                                                  10,
                                                  15,
                                                  3 );

        ksession.insert( tick1 );
        ksession.insert( tick2 );
        ksession.insert( tick3 );
        ksession.insert( tick4 );
        ksession.insert( tick5 );
        ksession.insert( tick6 );
        ksession.insert( tick7 );
        ksession.insert( tick8 );

        ArgumentCaptor<ActivationCreatedEvent> arg = ArgumentCaptor.forClass( ActivationCreatedEvent.class );
        verify( ael ).activationCreated( arg.capture() );
        assertThat( arg.getValue().getActivation().getRule().getName(),
                is( "before" ) );

        ksession.fireAllRules();

        verify( ael ).afterActivationFired( any( AfterActivationFiredEvent.class ) );
    }

    @Test
    public void testMetByOperator() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_MetByOperator.drl" ) );
        final KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( reader,
                                                       kconf );

        KnowledgeSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, sconf);

        final PseudoClockScheduler clock = (PseudoClockScheduler) ksession.<PseudoClockScheduler>getSessionClock();
        clock.setStartupTime( 1000 );

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        StockTickInterface tick1 = new StockTick( 1,
                                                  "DROO",
                                                  50,
                                                  System.currentTimeMillis(),
                                                  3 );
        StockTickInterface tick2 = new StockTick( 2,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  3 );
        StockTickInterface tick3 = new StockTick( 3,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  3 );
        StockTickInterface tick4 = new StockTick( 4,
                                                  "DROO",
                                                  50,
                                                  System.currentTimeMillis(),
                                                  5 );
        StockTickInterface tick5 = new StockTick( 5,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  5 );
        StockTickInterface tick6 = new StockTick( 6,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  3 );
        StockTickInterface tick7 = new StockTick( 7,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  5 );
        StockTickInterface tick8 = new StockTick( 8,
                                                  "ACME",
                                                  10,
                                                  System.currentTimeMillis(),
                                                  3 );

        InternalFactHandle fh1 = (InternalFactHandle) ksession.insert( tick1 );
        clock.advanceTime( 4,
                           TimeUnit.MILLISECONDS );
        InternalFactHandle fh2 = (InternalFactHandle) ksession.insert( tick2 );
        clock.advanceTime( 4,
                           TimeUnit.MILLISECONDS );
        ksession.insert( tick3 );
        clock.advanceTime( 4,
                           TimeUnit.MILLISECONDS );
        ksession.insert( tick4 );
        ksession.insert( tick5 );
        clock.advanceTime( 1,
                           TimeUnit.MILLISECONDS );
        ksession.insert( tick6 );
        ksession.insert( tick7 );
        clock.advanceTime( 2,
                           TimeUnit.MILLISECONDS );
        ksession.insert( tick8 );

        ArgumentCaptor<ActivationCreatedEvent> arg = ArgumentCaptor.forClass( ActivationCreatedEvent.class );
        verify( ael ).activationCreated( arg.capture() );
        Activation activation = arg.getValue().getActivation();
        assertThat( activation.getRule().getName(),
                    is( "metby" ) );

        ksession.fireAllRules();

        ArgumentCaptor<AfterActivationFiredEvent> aaf = ArgumentCaptor.forClass( AfterActivationFiredEvent.class );
        verify( ael ).afterActivationFired( aaf.capture() );
        assertThat( (InternalFactHandle) aaf.getValue().getActivation().getFactHandles().toArray()[0],
                    is( fh2 ) );
    }

    @Test
    public void testAfterOnArbitraryDates() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_AfterOperatorDates.drl" ) );
        final RuleBaseConfiguration rbconf = new RuleBaseConfiguration();
        final RuleBase ruleBase = loadRuleBase( reader,
                                                rbconf );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.PSEUDO_CLOCK );
        StatefulSession wm = ruleBase.newStatefulSession( conf,
                                                          null );

        final List< ? > results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        StockTickInterface tick1 = new StockTick( 1,
                                                  "DROO",
                                                  50,
                                                  100000, // arbitrary timestamp
                                                  3 );
        StockTickInterface tick2 = new StockTick( 2,
                                                  "ACME",
                                                  10,
                                                  104000, // 4 seconds after DROO
                                                  3 );

        InternalFactHandle handle2 = (InternalFactHandle) wm.insert( tick2 );
        InternalFactHandle handle1 = (InternalFactHandle) wm.insert( tick1 );
        

        assertNotNull( handle1 );
        assertNotNull( handle2 );

        assertTrue( handle1.isEvent() );
        assertTrue( handle2.isEvent() );

        //        wm  = SerializationHelper.serializeObject(wm);
        wm.fireAllRules();

        assertEquals( 4,
                      results.size() );
        assertEquals( tick1,
                      results.get( 0 ) );
        assertEquals( tick2,
                      results.get( 1 ) );
        assertEquals( tick1,
                      results.get( 2 ) );
        assertEquals( tick2,
                      results.get( 3 ) );
    }

    @Test
    public void testBeforeOnArbitraryDates() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_BeforeOperatorDates.drl" ) );
        final RuleBaseConfiguration rbconf = new RuleBaseConfiguration();
        final RuleBase ruleBase = loadRuleBase( reader,
                                                rbconf );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.PSEUDO_CLOCK );
        StatefulSession wm = ruleBase.newStatefulSession( conf,
                                                          null );

        final List< ? > results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        StockTickInterface tick1 = new StockTick( 1,
                                                  "DROO",
                                                  50,
                                                  104000, // arbitrary timestamp
                                                  3 );
        StockTickInterface tick2 = new StockTick( 2,
                                                  "ACME",
                                                  10,
                                                  100000, // 4 seconds after DROO
                                                  3 );

        InternalFactHandle handle1 = (InternalFactHandle) wm.insert( tick1 );
        InternalFactHandle handle2 = (InternalFactHandle) wm.insert( tick2 );

        assertNotNull( handle1 );
        assertNotNull( handle2 );

        assertTrue( handle1.isEvent() );
        assertTrue( handle2.isEvent() );

        //        wm  = SerializationHelper.serializeObject(wm);
        wm.fireAllRules();

        assertEquals( 4,
                      results.size() );
        assertEquals( tick1,
                      results.get( 0 ) );
        assertEquals( tick2,
                      results.get( 1 ) );
        assertEquals( tick1,
                      results.get( 2 ) );
        assertEquals( tick2,
                      results.get( 3 ) );
    }

    @Test
    public void testCoincidesOnArbitraryDates() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_CoincidesOperatorDates.drl" ) );
        final RuleBaseConfiguration rbconf = new RuleBaseConfiguration();
        final RuleBase ruleBase = loadRuleBase( reader,
                                                rbconf );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.PSEUDO_CLOCK );
        StatefulSession wm = ruleBase.newStatefulSession( conf,
                                                          null );

        final List< ? > results = new ArrayList<Object>();

        wm.setGlobal( "results",
                      results );

        StockTickInterface tick1 = new StockTick( 1,
                                                  "DROO",
                                                  50,
                                                  100000, // arbitrary timestamp
                                                  3 );
        StockTickInterface tick2 = new StockTick( 2,
                                                  "ACME",
                                                  10,
                                                  100050, // 50 milliseconds after DROO
                                                  3 );

        InternalFactHandle handle1 = (InternalFactHandle) wm.insert( tick1 );
        InternalFactHandle handle2 = (InternalFactHandle) wm.insert( tick2 );

        assertNotNull( handle1 );
        assertNotNull( handle2 );

        assertTrue( handle1.isEvent() );
        assertTrue( handle2.isEvent() );

        //        wm  = SerializationHelper.serializeObject(wm);
        wm.fireAllRules();

        assertEquals( 4,
                      results.size() );
        assertEquals( tick1,
                      results.get( 0 ) );
        assertEquals( tick2,
                      results.get( 1 ) );
        assertEquals( tick1,
                      results.get( 2 ) );
        assertEquals( tick2,
                      results.get( 3 ) );
    }

    @Test
    public void testSimpleTimeWindow() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_SimpleTimeWindow.drl" ) );
        final RuleBaseConfiguration rbconf = new RuleBaseConfiguration();
        rbconf.setEventProcessingMode( EventProcessingOption.STREAM );
        final RuleBase ruleBase = loadRuleBase( reader,
                                                rbconf );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.PSEUDO_CLOCK );
        StatefulSession wm = ruleBase.newStatefulSession( conf,
                                                          null );

        List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        // how to initialize the clock?
        // how to configure the clock?
        SessionPseudoClock clock = (SessionPseudoClock) wm.getSessionClock();

        clock.advanceTime( 5,
                           TimeUnit.SECONDS ); // 5 seconds
        EventFactHandle handle1 = (EventFactHandle) wm.insert( new OrderEvent( "1",
                                                                               "customer A",
                                                                               70 ) );
        assertEquals( 5000,
                      handle1.getStartTimestamp() );
        assertEquals( 0,
                      handle1.getDuration() );

        //        wm  = SerializationHelper.getSerialisedStatefulSession( wm );
        //        results = (List) wm.getGlobal( "results" );
        //        clock = (SessionPseudoClock) wm.getSessionClock();

        wm.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( 70,
                      ((Number) results.get( 0 )).intValue() );

        // advance clock and assert new data
        clock.advanceTime( 10,
                           TimeUnit.SECONDS ); // 10 seconds
        EventFactHandle handle2 = (EventFactHandle) wm.insert( new OrderEvent( "2",
                                                                               "customer A",
                                                                               60 ) );
        assertEquals( 15000,
                      handle2.getStartTimestamp() );
        assertEquals( 0,
                      handle2.getDuration() );

        wm.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertEquals( 65,
                      ((Number) results.get( 1 )).intValue() );

        // advance clock and assert new data
        clock.advanceTime( 10,
                           TimeUnit.SECONDS ); // 10 seconds
        EventFactHandle handle3 = (EventFactHandle) wm.insert( new OrderEvent( "3",
                                                                               "customer A",
                                                                               50 ) );
        assertEquals( 25000,
                      handle3.getStartTimestamp() );
        assertEquals( 0,
                      handle3.getDuration() );

        wm.fireAllRules();

        assertEquals( 3,
                      results.size() );
        assertEquals( 60,
                      ((Number) results.get( 2 )).intValue() );

        // advance clock and assert new data
        clock.advanceTime( 10,
                           TimeUnit.SECONDS ); // 10 seconds
        EventFactHandle handle4 = (EventFactHandle) wm.insert( new OrderEvent( "4",
                                                                               "customer A",
                                                                               25 ) );
        assertEquals( 35000,
                      handle4.getStartTimestamp() );
        assertEquals( 0,
                      handle4.getDuration() );

        wm.fireAllRules();

        // first event should have expired, making average under the rule threshold, so no additional rule fire
        assertEquals( 3,
                      results.size() );

        // advance clock and assert new data
        clock.advanceTime( 10,
                           TimeUnit.SECONDS ); // 10 seconds
        EventFactHandle handle5 = (EventFactHandle) wm.insert( new OrderEvent( "5",
                                                                               "customer A",
                                                                               70 ) );
        assertEquals( 45000,
                      handle5.getStartTimestamp() );
        assertEquals( 0,
                      handle5.getDuration() );

        //        wm  = SerializationHelper.serializeObject(wm);
        wm.fireAllRules();

        // still under the threshold, so no fire
        assertEquals( 3,
                      results.size() );

        // advance clock and assert new data
        clock.advanceTime( 10,
                           TimeUnit.SECONDS ); // 10 seconds
        EventFactHandle handle6 = (EventFactHandle) wm.insert( new OrderEvent( "6",
                                                                               "customer A",
                                                                               115 ) );
        assertEquals( 55000,
                      handle6.getStartTimestamp() );
        assertEquals( 0,
                      handle6.getDuration() );

        wm.fireAllRules();

        assertEquals( 4,
                      results.size() );
        assertEquals( 70,
                      ((Number) results.get( 3 )).intValue() );

    }

    @Test
    public void testSimpleLengthWindow() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_SimpleLengthWindow.drl" ) );
        final RuleBaseConfiguration rbconf = new RuleBaseConfiguration();
        rbconf.setEventProcessingMode( EventProcessingOption.STREAM );
        final RuleBase ruleBase = loadRuleBase( reader,
                                                rbconf );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.REALTIME_CLOCK );
        StatefulSession wm = ruleBase.newStatefulSession( conf,
                                                          null );

        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        EventFactHandle handle1 = (EventFactHandle) wm.insert( new OrderEvent( "1",
                                                                               "customer A",
                                                                               70 ) );

        //        wm  = SerializationHelper.serializeObject(wm);
        wm.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( 70,
                      ((Number) results.get( 0 )).intValue() );

        // assert new data
        EventFactHandle handle2 = (EventFactHandle) wm.insert( new OrderEvent( "2",
                                                                               "customer A",
                                                                               60 ) );
        wm.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertEquals( 65,
                      ((Number) results.get( 1 )).intValue() );

        // assert new data
        EventFactHandle handle3 = (EventFactHandle) wm.insert( new OrderEvent( "3",
                                                                               "customer A",
                                                                               50 ) );
        wm.fireAllRules();

        assertEquals( 3,
                      results.size() );
        assertEquals( 60,
                      ((Number) results.get( 2 )).intValue() );

        // assert new data
        EventFactHandle handle4 = (EventFactHandle) wm.insert( new OrderEvent( "4",
                                                                               "customer A",
                                                                               25 ) );
        wm.fireAllRules();

        // first event should have expired, making average under the rule threshold, so no additional rule fire
        assertEquals( 3,
                      results.size() );

        // assert new data
        EventFactHandle handle5 = (EventFactHandle) wm.insert( new OrderEvent( "5",
                                                                               "customer A",
                                                                               70 ) );
        //        wm  = SerializationHelper.serializeObject(wm);
        wm.fireAllRules();

        // still under the threshold, so no fire
        assertEquals( 3,
                      results.size() );

        // assert new data
        EventFactHandle handle6 = (EventFactHandle) wm.insert( new OrderEvent( "6",
                                                                               "customer A",
                                                                               115 ) );
        wm.fireAllRules();

        assertEquals( 4,
                      results.size() );
        assertEquals( 70,
                      ((Number) results.get( 3 )).intValue() );

    }

    @Test
    public void testDelayingNot() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_DelayingNot.drl" ) );
        final RuleBaseConfiguration rbconf = new RuleBaseConfiguration();
        rbconf.setEventProcessingMode( EventProcessingOption.STREAM );
        final RuleBase ruleBase = loadRuleBase( reader,
                                                rbconf );

        final Rule rule = ruleBase.getPackage( "org.drools" ).getRule( "Delaying Not" );
        assertEquals( 10000,
                      ((DurationTimer) rule.getTimer()).getDuration() );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.PSEUDO_CLOCK );
        StatefulSession wm = ruleBase.newStatefulSession( conf,
                                                          null );

        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        SessionPseudoClock clock = (SessionPseudoClock) wm.getSessionClock();

        clock.advanceTime( 10,
                           TimeUnit.SECONDS );

        StockTickInterface st1O = new StockTick( 1,
                                                 "DROO",
                                                 100,
                                                 clock.getCurrentTime() );

        EventFactHandle st1 = (EventFactHandle) wm.insert( st1O );

        wm.fireAllRules();

        // should not fire, because it must wait 10 seconds
        assertEquals( 0,
                      results.size() );

        clock.advanceTime( 5,
                           TimeUnit.SECONDS );

        EventFactHandle st2 = (EventFactHandle) wm.insert( new StockTick( 1,
                                                                          "DROO",
                                                                          80,
                                                                          clock.getCurrentTime() ) );

        wm.fireAllRules();

        // should still not fire, because it must wait 5 more seconds, and st2 has lower price (80)
        assertEquals( 0,
                      results.size() );
        // assert new data
        wm.fireAllRules();

        clock.advanceTime( 6,
                           TimeUnit.SECONDS );

        wm.fireAllRules();

        // should fire, because waited for 10 seconds and no other event arrived with a price increase
        assertEquals( 1,
                      results.size() );

        assertEquals( st1O,
                      results.get( 0 ) );

    }

    @Test
    public void testDelayingNot2() throws Exception {
        String str = "package org.drools\n" +
                "declare A @role(event) symbol : String end\n" +
                "declare B @role(event) symbol : String end\n" +
                "rule Setup when\n" +
                "then\n" +
                "    insert( new A() );\n" +
                "end\n" +
                "rule X\n" +
                "when\n" +
                "    $a : A() and not( B( this after $a ) )\n" +
                "then\n" +
                "end\n";
        
        KnowledgeBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBase( new StringReader( str ), conf );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        // rule X should not be delayed as the delay would be infinite
        int rules = ksession.fireAllRules();
        assertEquals( 2, rules );
        
    }
    
    @Test
    public void testDelayingNotWithPreEpochClock() throws Exception {
        String str = "package org.drools\n" +
                "declare A @role(event) symbol : String end\n" +
                "declare B @role(event) symbol : String end\n" +
                "rule Setup when\n" +
                "then\n" +
                "    insert( new A() );\n" +
                "end\n" +
                "rule X\n" +
                "when\n" +
                "    $a : A() and not( B( this after $a ) )\n" +
                "then\n" +
                "end\n";
        
        KnowledgeBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBase( new StringReader( str ), conf );

        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, ksconf);
        
        // Getting a pre-epoch date (i.e., before 1970) 
        Calendar ts = Calendar.getInstance();
        ts.set( 1900, 1, 1 );
        
        // Initializing the clock to that date
        SessionPseudoClock clock = ksession.getSessionClock();
        clock.advanceTime( ts.getTimeInMillis(), TimeUnit.MILLISECONDS );
        
        // rule X should not be delayed as the delay would be infinite
        int rules = ksession.fireAllRules();
        assertEquals( 2, rules );
        
    }
    
    //    @Test @Ignore
    //    public void testTransactionCorrelation() throws Exception {
    //        // read in the source
    //        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_TransactionCorrelation.drl" ) );
    //        final RuleBase ruleBase = loadRuleBase( reader );
    //
    //        final WorkingMemory wm = ruleBase.newStatefulSession();
    //        final List results = new ArrayList();
    //
    //        wm.setGlobal( "results",
    //                      results );
    //
    //
    //    }

    @Test
    public void testIdleTime() throws Exception {
        // read in the source
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_CEP_SimpleEventAssertion.drl" ) ),
                      ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        StatefulKnowledgeSession session = createKnowledgeSession(kbase, conf);
        InternalWorkingMemory iwm = ((StatefulKnowledgeSessionImpl) session).session;

        SessionPseudoClock clock = (SessionPseudoClock) session.<SessionClock>getSessionClock();

        final List results = new ArrayList();

        session.setGlobal( "results",
                           results );

        StockTickInterface tick1 = new StockTick( 1,
                                                  "DROO",
                                                  50,
                                                  10000 );
        StockTickInterface tick2 = new StockTick( 2,
                                                  "ACME",
                                                  10,
                                                  10010 );
        StockTickInterface tick3 = new StockTick( 3,
                                                  "ACME",
                                                  10,
                                                  10100 );
        StockTickInterface tick4 = new StockTick( 4,
                                                  "DROO",
                                                  50,
                                                  11000 );

        assertEquals( 0,
                      iwm.getIdleTime() );
        InternalFactHandle handle1 = (InternalFactHandle) session.insert( tick1 );
        clock.advanceTime( 10,
                           TimeUnit.SECONDS );
        assertEquals( 10000,
                      iwm.getIdleTime() );
        InternalFactHandle handle2 = (InternalFactHandle) session.insert( tick2 );
        assertEquals( 0,
                      iwm.getIdleTime() );
        clock.advanceTime( 15,
                           TimeUnit.SECONDS );
        assertEquals( 15000,
                      iwm.getIdleTime() );
        clock.advanceTime( 15,
                           TimeUnit.SECONDS );
        assertEquals( 30000,
                      iwm.getIdleTime() );
        InternalFactHandle handle3 = (InternalFactHandle) session.insert( tick3 );
        assertEquals( 0,
                      iwm.getIdleTime() );
        clock.advanceTime( 20,
                           TimeUnit.SECONDS );
        InternalFactHandle handle4 = (InternalFactHandle) session.insert( tick4 );
        clock.advanceTime( 10,
                           TimeUnit.SECONDS );

        assertNotNull( handle1 );
        assertNotNull( handle2 );
        assertNotNull( handle3 );
        assertNotNull( handle4 );

        assertTrue( handle1.isEvent() );
        assertTrue( handle2.isEvent() );
        assertTrue( handle3.isEvent() );
        assertTrue( handle4.isEvent() );

        assertEquals( 10000,
                      iwm.getIdleTime() );
        session.fireAllRules();
        assertEquals( 0,
                      iwm.getIdleTime() );

        assertEquals( 2,
                      ((List) session.getGlobal( "results" )).size() );

    }

    @Test
    public void testIdleTimeAndTimeToNextJob() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_SimpleTimeWindow.drl" ) );
        final RuleBaseConfiguration rbconf = new RuleBaseConfiguration();
        rbconf.setEventProcessingMode( EventProcessingOption.STREAM );
        final RuleBase ruleBase = loadRuleBase( reader,
                                                rbconf );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.PSEUDO_CLOCK );
        StatefulSession wm = ruleBase.newStatefulSession( conf,
                                                          null );
        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( wm );
        File testTmpDir = new File( "target/test-tmp/" );
        testTmpDir.mkdirs();
        logger.setFileName( "target/test-tmp/testIdleTimeAndTimeToNextJob-audit" );

        try {
            List results = new ArrayList();

            wm.setGlobal( "results",
                          results );
            InternalWorkingMemory iwm = (InternalWorkingMemory) wm;

            // how to initialize the clock?
            // how to configure the clock?
            SessionPseudoClock clock = (SessionPseudoClock) wm.getSessionClock();
            clock.advanceTime( 5,
                               TimeUnit.SECONDS ); // 5 seconds

            // there is no next job, so returns -1
            assertEquals( -1,
                          iwm.getTimeToNextJob() );
            wm.insert( new OrderEvent( "1",
                                       "customer A",
                                       70 ) );
            assertEquals( 0,
                          iwm.getIdleTime() );
            // now, there is a next job in 30 seconds: expire the event
            assertEquals( 30000,
                          iwm.getTimeToNextJob() );

            wm.fireAllRules();
            assertEquals( 1,
                          results.size() );
            assertEquals( 70,
                          ((Number) results.get( 0 )).intValue() );

            // advance clock and assert new data
            clock.advanceTime( 10,
                               TimeUnit.SECONDS ); // 10 seconds
            // next job is in 20 seconds: expire the event
            assertEquals( 20000,
                          iwm.getTimeToNextJob() );

            wm.insert( new OrderEvent( "2",
                                       "customer A",
                                       60 ) );
            wm.fireAllRules();

            assertEquals( 2,
                          results.size() );
            assertEquals( 65,
                          ((Number) results.get( 1 )).intValue() );

            // advance clock and assert new data
            clock.advanceTime( 10,
                               TimeUnit.SECONDS ); // 10 seconds
            // next job is in 10 seconds: expire the event
            assertEquals( 10000,
                          iwm.getTimeToNextJob() );

            wm.insert( new OrderEvent( "3",
                                       "customer A",
                                       50 ) );
            wm.fireAllRules();
            assertEquals( 3,
                          results.size() );
            assertEquals( 60,
                          ((Number) results.get( 2 )).intValue() );

            // advance clock and assert new data
            clock.advanceTime( 10,
                               TimeUnit.SECONDS ); // 10 seconds
            // advancing clock time will cause events to expire
            assertEquals( 0,
                          iwm.getIdleTime() );
            // next job is in 10 seconds: expire another event
            //assertEquals( 10000, iwm.getTimeToNextJob());

            wm.insert( new OrderEvent( "4",
                                       "customer A",
                                       25 ) );
            wm.fireAllRules();

            // first event should have expired, making average under the rule threshold, so no additional rule fire
            assertEquals( 3,
                          results.size() );

            // advance clock and assert new data
            clock.advanceTime( 10,
                               TimeUnit.SECONDS ); // 10 seconds

            wm.insert( new OrderEvent( "5",
                                       "customer A",
                                       70 ) );
            assertEquals( 0,
                          iwm.getIdleTime() );

            //        wm  = SerializationHelper.serializeObject(wm);
            wm.fireAllRules();

            // still under the threshold, so no fire
            assertEquals( 3,
                          results.size() );
        } finally {
            logger.writeToDisk();
        }
    }

    @Test
    public void testCollectWithWindows() throws Exception {
        final KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( "test_CEP_CollectWithWindows.drl",
                                                       kbconf,
                                                       true );

        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, ksconf);
        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( ksession );
        File testTmpDir = new File( "target/test-tmp/" );
        testTmpDir.mkdirs();
        logger.setFileName( "target/test-tmp/testCollectWithWindows-audit" );

        List<Number> timeResults = new ArrayList<Number>();
        List<Number> lengthResults = new ArrayList<Number>();

        ksession.setGlobal( "timeResults",
                            timeResults );
        ksession.setGlobal( "lengthResults",
                            lengthResults );

        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();

        try {
            // First interaction
            clock.advanceTime( 5,
                               TimeUnit.SECONDS ); // 5 seconds
            ksession.insert( new OrderEvent( "1",
                                             "customer A",
                                             70 ) );

            ksession.fireAllRules();

            assertEquals( 1,
                          timeResults.size() );
            assertEquals( 1,
                          timeResults.get( 0 ).intValue() );
            assertEquals( 1,
                          lengthResults.size() );
            assertEquals( 1,
                          lengthResults.get( 0 ).intValue() );

            // Second interaction: advance clock and assert new data
            clock.advanceTime( 10,
                               TimeUnit.SECONDS ); // 10 seconds
            ksession.insert( new OrderEvent( "2",
                                             "customer A",
                                             60 ) );
            ksession.fireAllRules();

            assertEquals( 2,
                          timeResults.size() );
            assertEquals( 2,
                          timeResults.get( 1 ).intValue() );
            assertEquals( 2,
                          lengthResults.size() );
            assertEquals( 2,
                          lengthResults.get( 1 ).intValue() );

            // Third interaction: advance clock and assert new data
            clock.advanceTime( 10,
                               TimeUnit.SECONDS ); // 10 seconds
            ksession.insert( new OrderEvent( "3",
                                             "customer A",
                                             50 ) );
            ksession.fireAllRules();

            assertEquals( 3,
                          timeResults.size() );
            assertEquals( 3,
                          timeResults.get( 2 ).intValue() );
            assertEquals( 3,
                          lengthResults.size() );
            assertEquals( 3,
                          lengthResults.get( 2 ).intValue() );

            // Fourth interaction: advance clock and assert new data
            clock.advanceTime( 10,
                               TimeUnit.SECONDS ); // 10 seconds
            ksession.insert( new OrderEvent( "4",
                                             "customer A",
                                             25 ) );
            ksession.fireAllRules();

            // first event should have expired now
            assertEquals( 4,
                          timeResults.size() );
            assertEquals( 3,
                          timeResults.get( 3 ).intValue() );
            assertEquals( 4,
                          lengthResults.size() );
            assertEquals( 3,
                          lengthResults.get( 3 ).intValue() );

            // Fifth interaction: advance clock and assert new data
            clock.advanceTime( 5,
                               TimeUnit.SECONDS ); // 10 seconds
            ksession.insert( new OrderEvent( "5",
                                             "customer A",
                                             70 ) );
            ksession.fireAllRules();

            assertEquals( 5,
                          timeResults.size() );
            assertEquals( 4,
                          timeResults.get( 4 ).intValue() );
            assertEquals( 5,
                          lengthResults.size() );
            assertEquals( 3,
                          lengthResults.get( 4 ).intValue() );
        } finally {
            logger.writeToDisk();
        }

    }

    @Test
    public void testPseudoSchedulerRemoveJobTest() {
        String str = "import org.drools.integrationtests.CepEspTest.A\n";
        str += "declare A\n";
        str += "    @role( event )\n";
        str += "end\n";
        str += "rule A\n";
        str += "when\n";
        str += "   $a : A()\n";
        str += "   not A(this after [1s,10s] $a)\n";
        str += "then\n";
        str += "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( str ) ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );

        KnowledgeSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( "pseudo" ) );
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( config );
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession( sessionConfig,
                                                                                       KnowledgeBaseFactory.newEnvironment() );
        PseudoClockScheduler pseudoClock = ksession.<PseudoClockScheduler>getSessionClock();

        FactHandle h = ksession.insert( new A() );
        ksession.retract( h );
    }

    public static class A
        implements
        Serializable {
    }

    public static class Message {
        private Properties properties;

        public Properties getProperties() {
            return properties;
        }

        public void setProperties( Properties properties ) {
            this.properties = properties;
        }
    }

    @Test
    public void testStreamModeNoSerialization() throws IOException,
                                               ClassNotFoundException {
        final KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase1 = loadKnowledgeBase( "test_CEP_StreamMode.drl",
                                                        kbconf,
                                                        false );

        KnowledgeBase kbase2 = (KnowledgeBase) DroolsStreamUtils.streamIn( DroolsStreamUtils.streamOut( kbase1 ),
                                                                           null );

        final StatefulKnowledgeSession ksession1 = kbase1.newStatefulKnowledgeSession();
        AgendaEventListener ael1 = mock( AgendaEventListener.class );
        ksession1.addEventListener( ael1 );

        final StatefulKnowledgeSession ksession2 = kbase2.newStatefulKnowledgeSession();
        AgendaEventListener ael2 = mock( AgendaEventListener.class );
        ksession2.addEventListener( ael2 );

        // -------------
        // first, check the non-serialized session
        // -------------
        ksession1.insert( new Sensor( 10,
                                      10 ) );
        ksession1.fireAllRules();

        ArgumentCaptor<AfterActivationFiredEvent> aafe1 = ArgumentCaptor.forClass( AfterActivationFiredEvent.class );
        verify( ael1,
                times( 1 ) ).afterActivationFired( aafe1.capture() );
        List<AfterActivationFiredEvent> events1 = aafe1.getAllValues();
        assertThat( events1.get( 0 ).getActivation().getDeclarationValue( "$avg" ),
                    is( (Object) 10 ) );

        ksession1.insert( new Sensor( 20,
                                      20 ) );
        ksession1.fireAllRules();
        verify( ael1,
                times( 2 ) ).afterActivationFired( aafe1.capture() );
        assertThat( events1.get( 1 ).getActivation().getDeclarationValue( "$avg" ),
                    is( (Object) 15 ) );
        ksession1.insert( new Sensor( 30,
                                      30 ) );
        ksession1.fireAllRules();
        verify( ael1,
                times( 3 ) ).afterActivationFired( aafe1.capture() );
        assertThat( events1.get( 2 ).getActivation().getDeclarationValue( "$avg" ),
                    is( (Object) 25 ) );

        ksession1.dispose();

        // -------------
        // now we check the serialized session
        // -------------
        ArgumentCaptor<AfterActivationFiredEvent> aafe2 = ArgumentCaptor.forClass( AfterActivationFiredEvent.class );

        ksession2.insert( new Sensor( 10,
                                      10 ) );
        ksession2.fireAllRules();
        verify( ael2,
                times( 1 ) ).afterActivationFired( aafe2.capture() );
        List<AfterActivationFiredEvent> events2 = aafe2.getAllValues();
        assertThat( events2.get( 0 ).getActivation().getDeclarationValue( "$avg" ),
                    is( (Object) 10 ) );

        ksession2.insert( new Sensor( 20,
                                      20 ) );
        ksession2.fireAllRules();
        verify( ael2,
                times( 2 ) ).afterActivationFired( aafe2.capture() );
        assertThat( events2.get( 1 ).getActivation().getDeclarationValue( "$avg" ),
                    is( (Object) 15 ) );

        ksession2.insert( new Sensor( 30,
                                      30 ) );
        ksession2.fireAllRules();
        verify( ael2,
                times( 3 ) ).afterActivationFired( aafe2.capture() );
        assertThat( events2.get( 2 ).getActivation().getDeclarationValue( "$avg" ),
                    is( (Object) 25 ) );
        ksession2.dispose();
    }

    @Test
    public void testIdentityAssertBehaviorOnEntryPoints() throws IOException,
                                                         ClassNotFoundException {
        StockTickInterface st1 = new StockTick( 1,
                                                "RHT",
                                                10,
                                                10 );
        StockTickInterface st2 = new StockTick( 1,
                                                "RHT",
                                                10,
                                                10 );
        StockTickInterface st3 = new StockTick( 2,
                                                "RHT",
                                                15,
                                                20 );

        final KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        kbconf.setOption( AssertBehaviorOption.IDENTITY );
        final KnowledgeBase kbase1 = loadKnowledgeBase( "test_CEP_AssertBehaviorOnEntryPoints.drl",
                                                        kbconf,
                                                        true );

        final StatefulKnowledgeSession ksession = kbase1.newStatefulKnowledgeSession();
        AgendaEventListener ael1 = mock( AgendaEventListener.class );
        ksession.addEventListener( ael1 );
        WorkingMemoryEntryPoint ep1 = ksession.getWorkingMemoryEntryPoint( "stocktick stream" );

        FactHandle fh1 = ep1.insert( st1 );
        FactHandle fh1_2 = ep1.insert( st1 );
        FactHandle fh2 = ep1.insert( st2 );
        FactHandle fh3 = ep1.insert( st3 );

        assertSame( fh1,
                    fh1_2 );
        assertNotSame( fh1,
                       fh2 );
        assertNotSame( fh1,
                       fh3 );
        assertNotSame( fh2,
                       fh3 );

        ksession.fireAllRules();
        // must have fired 3 times, one for each event identity
        verify( ael1,
                times( 3 ) ).afterActivationFired( any( AfterActivationFiredEvent.class ) );

        ksession.dispose();
    }

    @Test
    public void testEqualityAssertBehaviorOnEntryPoints() throws IOException,
                                                         ClassNotFoundException {
        StockTickInterface st1 = new StockTick( 1,
                                                "RHT",
                                                10,
                                                10 );
        StockTickInterface st2 = new StockTick( 1,
                                                "RHT",
                                                10,
                                                10 );
        StockTickInterface st3 = new StockTick( 2,
                                                "RHT",
                                                15,
                                                20 );

        final KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        kbconf.setOption( AssertBehaviorOption.EQUALITY );
        final KnowledgeBase kbase1 = loadKnowledgeBase( "test_CEP_AssertBehaviorOnEntryPoints.drl",
                                                        kbconf,
                                                        true );

        final StatefulKnowledgeSession ksession1 = kbase1.newStatefulKnowledgeSession();
        AgendaEventListener ael1 = mock( AgendaEventListener.class );
        ksession1.addEventListener( ael1 );
        WorkingMemoryEntryPoint ep1 = ksession1.getWorkingMemoryEntryPoint( "stocktick stream" );

        FactHandle fh1 = ep1.insert( st1 );
        FactHandle fh1_2 = ep1.insert( st1 );
        FactHandle fh2 = ep1.insert( st2 );
        FactHandle fh3 = ep1.insert( st3 );

        assertSame( fh1,
                    fh1_2 );
        assertSame( fh1,
                    fh2 );
        assertNotSame( fh1,
                       fh3 );

        ksession1.fireAllRules();
        // must have fired 2 times, one for each event equality
        verify( ael1,
                times( 2 ) ).afterActivationFired( any( AfterActivationFiredEvent.class ) );

        ksession1.dispose();
    }

    @Test
    public void testEventDeclarationForInterfaces() throws Exception {
        // read in the source
        final KnowledgeBase kbase = loadKnowledgeBase( "test_CEP_EventInterfaces.drl",
                                                       null,
                                                       true );

        StatefulKnowledgeSession session = createKnowledgeSession(kbase);

        StockTickInterface tick1 = new StockTick( 1,
                                                  "DROO",
                                                  50,
                                                  10000 );
        StockTickInterface tick2 = new StockTick( 2,
                                                  "ACME",
                                                  10,
                                                  10010 );
        StockTickInterface tick3 = new StockTick( 3,
                                                  "ACME",
                                                  10,
                                                  10100 );
        StockTickInterface tick4 = new StockTick( 4,
                                                  "DROO",
                                                  50,
                                                  11000 );

        InternalFactHandle handle1 = (InternalFactHandle) session.insert( tick1 );
        InternalFactHandle handle2 = (InternalFactHandle) session.insert( tick2 );
        InternalFactHandle handle3 = (InternalFactHandle) session.insert( tick3 );
        InternalFactHandle handle4 = (InternalFactHandle) session.insert( tick4 );

        assertTrue( handle1.isEvent() );
        assertTrue( handle2.isEvent() );
        assertTrue( handle3.isEvent() );
        assertTrue( handle4.isEvent() );
    }

    @Test
    public void testTemporalOperators() throws Exception {
        // read in the source
        final RuleBaseConfiguration kbconf = new RuleBaseConfiguration();
        kbconf.setEventProcessingMode( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBase( "test_CEP_TemporalOperators.drl",
                                                 kbconf,
                                                 true );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        ksession.insert( new StockTick( 1,
                                        "A",
                                        10,
                                        1000 ) );
    }

    @Test
    public void testTemporalOperators2() throws Exception {
        // read in the source
        final RuleBaseConfiguration kbconf = new RuleBaseConfiguration();
        kbconf.setEventProcessingMode( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBase( "test_CEP_TemporalOperators2.drl",
                                                 kbconf,
                                                 true );

        KnowledgeSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, sconf);
        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();

        WorkingMemoryEntryPoint ep = ksession.getWorkingMemoryEntryPoint( "X" );

        clock.advanceTime( 1000,
                           TimeUnit.SECONDS );
        ep.insert( new StockTick( 1,
                                  "A",
                                  10,
                                  clock.getCurrentTime() ) );
        clock.advanceTime( 8,
                           TimeUnit.SECONDS );
        ep.insert( new StockTick( 2,
                                  "B",
                                  10,
                                  clock.getCurrentTime() ) );
        clock.advanceTime( 8,
                           TimeUnit.SECONDS );
        ep.insert( new StockTick( 3,
                                  "B",
                                  10,
                                  clock.getCurrentTime() ) );
        clock.advanceTime( 8,
                           TimeUnit.SECONDS );
        int rules = ksession.fireAllRules();
        assertEquals( 2,
                      rules );
    }

    @Test
    public void testTemporalOperatorsInfinity() throws Exception {
        // read in the source
        final RuleBaseConfiguration kbconf = new RuleBaseConfiguration();
        kbconf.setEventProcessingMode( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBase( "test_CEP_TemporalOperators3.drl",
                                                 kbconf,
                                                 true );

        KnowledgeSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( sconf,
                                                                               null );
        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();

        WorkingMemoryEntryPoint ep = ksession.getWorkingMemoryEntryPoint( "X" );

        clock.advanceTime( 1000,
                           TimeUnit.SECONDS );
        ep.insert( new StockTick( 1,
                                  "A",
                                  10,
                                  clock.getCurrentTime() ) );
        clock.advanceTime( 8,
                           TimeUnit.SECONDS );
        ep.insert( new StockTick( 2,
                                  "B",
                                  10,
                                  clock.getCurrentTime() ) );
        clock.advanceTime( 8,
                           TimeUnit.SECONDS );
        ep.insert( new StockTick( 3,
                                  "B",
                                  10,
                                  clock.getCurrentTime() ) );
        clock.advanceTime( 8,
                           TimeUnit.SECONDS );
        int rules = ksession.fireAllRules();
        assertEquals( 3,
                      rules );
    }

    @Test
    public void testMultipleSlidingWindows() throws IOException,
                                            ClassNotFoundException {
        String str = "declare A\n" +
                     "    @role( event )\n" +
                     "    id : int\n" +
                     "end\n" +
                     "declare B\n" +
                     "    @role( event )\n" +
                     "    id : int\n" +
                     "end\n" +
                     "rule launch\n" +
                     "when\n" +
                     "then\n" +
                     "    insert( new A( 1 ) );\n" +
                     "    insert( new A( 2 ) );\n" +
                     "    insert( new B( 1 ) );\n" +
                     "    insert( new A( 3 ) );\n" +
                     "    insert( new B( 2 ) );\n" +
                     "end\n" +
                     "rule \"ab\"\n" +
                     "when\n" +
                     "    A( $a : id ) over window:length( 1 )\n" +
                     "    B( $b : id ) over window:length( 1 )\n" +
                     "then\n" +
                     "    //System.out.println(\"AB: ( \"+$a+\", \"+$b+\" )\");\n" +
                     "end\n" +
                     "rule \"ba\"\n" +
                     "when\n" +
                     "    B( $b : id ) over window:length( 1 )\n" +
                     "    A( $a : id ) over window:length( 1 )\n" +
                     "then\n" +
                     "    //System.out.println(\"BA: ( \"+$b+\", \"+$a+\" )\");\n" +
                     "end";

        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBase( new StringReader( str ),
                                                 config );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        ksession.fireAllRules();

        ArgumentCaptor<AfterActivationFiredEvent> captor = ArgumentCaptor.forClass( AfterActivationFiredEvent.class );
        verify( ael,
                times( 7 ) ).afterActivationFired( captor.capture() );

        List<AfterActivationFiredEvent> values = captor.getAllValues();
        // first rule
        Activation act = values.get( 0 ).getActivation();
        assertThat( act.getRule().getName(),
                    is( "launch" ) );

        // second rule
        act = values.get( 1 ).getActivation();
        assertThat( act.getRule().getName(),
                    is( "ba" ) );
        assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                    is( 3 ) );
        assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                    is( 2 ) );

        // third rule
        act = values.get( 2 ).getActivation();
        assertThat( act.getRule().getName(),
                    is( "ab" ) );
        assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                    is( 3 ) );
        assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                    is( 2 ) );

        // fourth rule
        act = values.get( 3 ).getActivation();
        assertThat( act.getRule().getName(),
                    is( "ba" ) );
        assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                    is( 3 ) );
        assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                    is( 1 ) );

        // fifth rule
        act = values.get( 4 ).getActivation();
        assertThat( act.getRule().getName(),
                    is( "ab" ) );
        assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                    is( 3 ) );
        assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                    is( 1 ) );

        // sixth rule
        act = values.get( 5 ).getActivation();
        assertThat( act.getRule().getName(),
                    is( "ba" ) );
        assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                    is( 2 ) );
        assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                    is( 1 ) );

        // seventh rule
        act = values.get( 6 ).getActivation();
        assertThat( act.getRule().getName(),
                    is( "ab" ) );
        assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                    is( 2 ) );
        assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                    is( 1 ) );

    }

    @Test
    public void testCloudModeExpiration() throws IOException,
                                            ClassNotFoundException,
                                         InstantiationException,
                                         IllegalAccessException,
                                         InterruptedException {
        String str = "package org.drools.cloud\n" +
                     "import org.drools.*\n" +
                     "declare Event\n" +
                     "        @role ( event )\n" +
                     "        name : String\n" +
                     "        value : Object\n" +
                     "end\n" +
                     "declare AnotherEvent\n" +
                     "        @role ( event )\n" +
                     "        message : String\n" +
                     "        type : String\n" +
                     "end\n" +
                     "declare StockTick\n" +
                     "        @role ( event )\n" +
                     "end\n" +
                     "rule \"two events\"\n" +
                     "    when\n" +
                     "        Event( value != null ) from entry-point X\n" +
                     "        StockTick( company != null ) from entry-point X\n" +
                     "    then\n" +
                     "end";

        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.CLOUD );
        KnowledgeBase kbase = loadKnowledgeBase( new StringReader( str ),
                                                 config );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        WorkingMemoryEntryPoint ep = ksession.getWorkingMemoryEntryPoint( "X" );

        ep.insert( new StockTick( 1,
                                  "RHT",
                                  10,
                                  1000 ) );
        int rulesFired = ksession.fireAllRules();
        assertEquals( 0,
                      rulesFired );

        org.drools.definition.type.FactType event = kbase.getFactType( "org.drools.cloud",
                                                                       "Event" );
        Object e1 = event.newInstance();
        event.set( e1,
                   "name",
                   "someKey" );
        event.set( e1,
                   "value",
                   "someValue" );

        ep.insert( e1 );
        rulesFired = ksession.fireAllRules();
        assertEquals( 1,
                      rulesFired );

        // let some time be spent
        Thread.currentThread().sleep( 1000 );

        // check both events are still in memory as we are running in CLOUD mode
        assertEquals( 2,
                      ep.getFactCount() );
    }

    @Test
    public void testSalienceWithEventsPseudoClock() throws IOException,
                                                   ClassNotFoundException {
        String str = "package org.drools\n" +
                     "declare StockTick\n" +
                     "        @role ( event )\n" +
                     "end\n" +
                     "rule R1 salience 1000\n" +
                     "    when\n" +
                     "        $s1 : StockTick( company == 'RHT' )\n" +
                     "        $s2 : StockTick( company == 'ACME', this after[0s,1m] $s1 )\n" +
                     "    then\n" +
                     "end\n" +
                     "rule R2 salience 1000\n" +
                     "    when\n" +
                     "        $s1 : StockTick( company == 'RHT' )\n" +
                     "        not StockTick( company == 'ACME', this after[0s,1m] $s1 )\n" +
                     "    then\n" +
                     "end\n" +
                     "rule R3 salience 100\n" +
                     "    when\n" +
                     "        $s2 : StockTick( company == 'ACME' )\n" +
                     "    then\n" +
                     "end\n";

        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBase( new StringReader( str ),
                                                 config );
        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( ksconf,
                                                                               null );

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        clock.advanceTime( 1000000,
                           TimeUnit.MILLISECONDS );

        ksession.insert( new StockTick( 1,
                                        "RHT",
                                        10,
                                        1000 ) );
        clock.advanceTime( 5,
                           TimeUnit.SECONDS );
        ksession.insert( new StockTick( 2,
                                        "RHT",
                                        10,
                                        1000 ) );
        clock.advanceTime( 5,
                           TimeUnit.SECONDS );
        ksession.insert( new StockTick( 3,
                                        "RHT",
                                        10,
                                        1000 ) );
        clock.advanceTime( 5,
                           TimeUnit.SECONDS );
        ksession.insert( new StockTick( 4,
                                        "ACME",
                                        10,
                                        1000 ) );
        clock.advanceTime( 5,
                           TimeUnit.SECONDS );
        int rulesFired = ksession.fireAllRules();
        assertEquals( 4,
                      rulesFired );

        ArgumentCaptor<AfterActivationFiredEvent> captor = ArgumentCaptor.forClass( AfterActivationFiredEvent.class );
        verify( ael,
                times( 4 ) ).afterActivationFired( captor.capture() );
        List<AfterActivationFiredEvent> aafe = captor.getAllValues();

        Assert.assertThat( aafe.get( 0 ).getActivation().getRule().getName(),
                           is( "R1" ) );
        Assert.assertThat( aafe.get( 1 ).getActivation().getRule().getName(),
                           is( "R1" ) );
        Assert.assertThat( aafe.get( 2 ).getActivation().getRule().getName(),
                           is( "R1" ) );
        Assert.assertThat( aafe.get( 3 ).getActivation().getRule().getName(),
                           is( "R3" ) );
    }

    @Test
    public void testSalienceWithEventsRealtimeClock() throws IOException,
                                                     ClassNotFoundException, InterruptedException {
        String str = "package org.drools\n" +
                     "declare StockTick\n" +
                     "        @role ( event )\n" +
                     "end\n" +
                     "rule R1 salience 1000\n" +
                     "    when\n" +
                     "        $s1 : StockTick( company == 'RHT' )\n" +
                     "        $s2 : StockTick( company == 'ACME', this after[0s,1m] $s1 )\n" +
                     "    then\n" +
                     "end\n" +
                     "rule R2 salience 1000\n" +
                     "    when\n" +
                     "        $s1 : StockTick( company == 'RHT' )\n" +
                     "        not StockTick( company == 'ACME', this after[0s,1m] $s1 )\n" +
                     "    then\n" +
                     "end\n" +
                     "rule R3 salience 100\n" +
                     "    when\n" +
                     "        $s2 : StockTick( company == 'ACME' )\n" +
                     "    then\n" +
                     "end\n";

        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBase( new StringReader( str ),
                                                 config );
        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( ClockType.REALTIME_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( ksconf,
                                                                               null );

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        ksession.insert( new StockTick( 1,
                                        "RHT",
                                        10,
                                        1000 ) );
        ksession.insert( new StockTick( 2,
                                        "RHT",
                                        10,
                                        1000 ) );
        ksession.insert( new StockTick( 3,
                                        "RHT",
                                        10,
                                        1000 ) );
        // sleep for 2 secs
        Thread.currentThread().sleep( 2000 );
        ksession.insert( new StockTick( 4,
                                        "ACME",
                                        10,
                                        1000 ) );
        // sleep for 1 sec
        Thread.currentThread().sleep( 1000 );
        int rulesFired = ksession.fireAllRules();
        assertEquals( 4,
                      rulesFired );

        ArgumentCaptor<AfterActivationFiredEvent> captor = ArgumentCaptor.forClass( AfterActivationFiredEvent.class );
        verify( ael,
                times( 4 ) ).afterActivationFired( captor.capture() );
        List<AfterActivationFiredEvent> aafe = captor.getAllValues();

        Assert.assertThat( aafe.get( 0 ).getActivation().getRule().getName(),
                           is( "R1" ) );
        Assert.assertThat( aafe.get( 1 ).getActivation().getRule().getName(),
                           is( "R1" ) );
        Assert.assertThat( aafe.get( 2 ).getActivation().getRule().getName(),
                           is( "R1" ) );
        Assert.assertThat( aafe.get( 3 ).getActivation().getRule().getName(),
                           is( "R3" ) );
    }

    @Test
    public void testExpireEventOnEndTimestamp() throws Exception {
        // DROOLS-40
        String str =
                "package org.drools;\n" +
                "\n" +
                "import org.drools.StockTick;\n" +
                "\n" +
                "global java.util.List resultsAfter;\n" +
                "\n" +
                "declare StockTick\n" +
                "    @role( event )\n" +
                "    @duration( duration )\n" +
                "end\n" +
                "\n" +
                "rule \"after[60,80]\"\n" +
                "when\n" +
                "$a : StockTick( company == \"DROO\" )\n" +
                "$b : StockTick( company == \"ACME\", this after[60,80] $a )\n" +
                "then\n" +
                "       resultsAfter.add( $b );\n" +
                "end";

        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = loadKnowledgeBaseFromString(config, str);

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(conf, null);

        PseudoClockScheduler clock = (PseudoClockScheduler) ksession.getSessionClock();

        List<StockTick> resultsAfter = new ArrayList<StockTick>();
        ksession.setGlobal("resultsAfter", resultsAfter);

        // inserting new StockTick with duration 30 at time 0 => rule
        // after[60,80] should fire when ACME lasts at 100-120
        ksession.insert(new StockTick(1, "DROO", 0, 0, 30));

        clock.advanceTime(100, TimeUnit.MILLISECONDS);

        ksession.insert(new StockTick(2, "ACME", 0, 0, 20));

        ksession.fireAllRules();

        assertEquals(1, resultsAfter.size());
    }


    @Test
    public void testSlidingWindowsAccumulateExternalJoin() throws Exception {
        // DROOLS-106
        // The logic may not be optimal, but was used to detect a WM corruption
        String str =
                "package testing2;\n" +
                        "\n" +
                        "import java.util.*;\n" +
                        "import org.drools.StockTick;\n" +
                        "" +
                        "global List list;\n" +
                        "" +
                        "declare StockTick\n" +
                        "    @role( event )\n" +
                        "    @duration( duration )\n" +
                        "end\n" +
                        "\n" +
                        "rule test\n" +
                        "when\n" +
                        " $primary : StockTick( $name : company ) over window:length(1)\n" +
                        " accumulate ( " +
                        "           $tick : StockTick( company == $name ) , " +
                        "               $num : count( $tick ) )\n" +

                        "then\n" +
                        "   System.out.println(\"Found name: \" + $primary + \" with \" +$num );\n" +
                        "   list.add( $num.intValue() ); \n" +
                        "end\n" +
                        ""
                        ;

        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = loadKnowledgeBaseFromString(config, str);

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(conf, null);

        int seq = 0;
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( new StockTick( seq++, "AAA", 10.0, 10L ) );
        ksession.fireAllRules();
        assertEquals( list, Arrays.asList( 1 ) );

        ksession.insert( new StockTick( seq++, "AAA", 15.0, 10L ) );
        ksession.fireAllRules();
        assertEquals( list, Arrays.asList( 1, 2 ) );

        ksession.insert( new StockTick( seq++, "CCC", 10.0, 10L ) );
        ksession.fireAllRules();
        assertEquals( list, Arrays.asList( 1, 2, 1 ) );

        System.out.println(" ___________________________________- ");

        ksession.insert( new StockTick( seq++, "DDD", 13.0, 20L ) );
        ksession.fireAllRules();
        assertEquals( list, Arrays.asList( 1, 2, 1, 1 ) );

        ksession.insert( new StockTick( seq++, "AAA", 11.0, 20L ) );
        ksession.fireAllRules();
        assertEquals( list, Arrays.asList( 1, 2, 1, 1, 3 ) );

        // NPE Here
        ksession.fireAllRules();

    }




    @Test
    public void testTimeAndLengthWindowConflict() throws Exception {
        String drl = "package org.drools;\n" +
                "\n" +
                "import java.util.List\n" +
                "\n" +
                "global List timeResults;\n" +
                "global List lengthResults;\n" +
                "\n" +
                "declare OrderEvent\n" +
                "    @role( event )\n" +
                "end\n" +
                "\n" +
                "rule \"collect with time window\"\n" +
                "when\n" +
                "    $list : List( empty == false ) from collect(\n" +
                "              $o : OrderEvent() over window:time(30s) )\n" +
                "then\n" +
                "    timeResults.add( $list.size() );\n" +
                "end\n" +
                "\n" +
                "rule \"collect with length window\"\n" +
                "when\n" +
                "    accumulate (\n" +
                "              $o : OrderEvent( $tot : total ) over window:length(3)," +
                "              $avg : average( $tot ) )\n" +
                "then\n" +
                "    lengthResults.add( $avg );\n" +
                "end\n";

        final KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBaseFromString( kbconf,  drl );

        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, ksconf);

        List<Number> timeResults = new ArrayList<Number>();
        List<Number> lengthResults = new ArrayList<Number>();

        ksession.setGlobal( "timeResults",
                timeResults );
        ksession.setGlobal( "lengthResults",
                lengthResults );

        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();

            clock.advanceTime( 5, TimeUnit.SECONDS ); // 5 seconds
        ksession.insert( new OrderEvent( "1", "customer A", 70 ) );
        ksession.fireAllRules();
        System.out.println( lengthResults );
        assertTrue( lengthResults.contains( 70.0 ) );

        clock.advanceTime( 10, TimeUnit.SECONDS ); // 10 seconds
        ksession.insert( new OrderEvent( "2", "customer A", 60 ) );
        ksession.fireAllRules();
        System.out.println( lengthResults );
        assertTrue( lengthResults.contains( 65.0 ) );

        // Third interaction: advance clock and assert new data
        clock.advanceTime( 10, TimeUnit.SECONDS ); // 10 seconds
        ksession.insert( new OrderEvent( "3", "customer A", 50 ) );
        ksession.fireAllRules();
        System.out.println( lengthResults );
        assertTrue( lengthResults.contains( 60.0 ) );

        // Fourth interaction: advance clock and assert new data
        clock.advanceTime( 60, TimeUnit.SECONDS ); // 60 seconds
        ksession.insert( new OrderEvent( "4", "customer A", 25 ) );
        ksession.fireAllRules();
        System.out.println( lengthResults );
//        assertTrue( lengthResults.contains( 45 ) );

    }

    @Test
    public void testTimeWindowWithPastEvents() throws Exception {
        String drl = "package org.drools;\n" +
                "\n" +
                "import java.util.List\n" +
                "\n" +
                "global List timeResults;\n" +
                "\n" +
                "declare StockTick\n" +
                "    @role( event )\n" +
                "    @timestamp( time ) \n" +
                "end\n" +
                "\n" +
                "rule \"collect with time window\"\n" +
                "when\n" +
                "    accumulate(\n" +
                "              $o : StockTick() over window:time(10ms)," +
                "              $tot : count( $o );" +
                "              $tot > 0 )\n" +
                "then\n" +
                "    System.out.println( $tot ); \n" +
                "    timeResults.add( $tot );\n" +
                "end\n";

        final KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBaseFromString( kbconf,  drl );

        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, ksconf);

        List<Number> timeResults = new ArrayList<Number>();

        ksession.setGlobal( "timeResults",
                timeResults );
        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();

        int count = 0;
        StockTick tick1 = new StockTick( count++, "X", 0.0, 1 );
        StockTick tick2 = new StockTick( count++, "X", 0.0, 3 );
        StockTick tick3 = new StockTick( count++, "X", 0.0, 7 );
        StockTick tick4 = new StockTick( count++, "X", 0.0, 9 );
        StockTick tick5 = new StockTick( count++, "X", 0.0, 15 );

        clock.advanceTime( 30, TimeUnit.MILLISECONDS );

        ksession.insert( tick1 );
        ksession.insert( tick2 );
        ksession.insert( tick3 );
        ksession.insert( tick4 );
        ksession.insert( tick5 );

        ksession.fireAllRules();
        assertTrue( timeResults.isEmpty() );

        clock.advanceTime( 0, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertTrue( timeResults.isEmpty() );

        clock.advanceTime( 3, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertTrue( timeResults.isEmpty() );

        clock.advanceTime( 10, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertTrue( timeResults.isEmpty() );

    }

    @Test
    public void testTimeStampOnNonExistingField() throws Exception {
        // BZ-985942
        String drl = "package org.drools.compiler;\n" +
                     "\n" +
                     "declare StockTick\n" +
                     " @role( event )\n" +
                     " @timestamp( nonExistingField ) \n" +
                     "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( );
        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()),
                      ResourceType.DRL );

        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    @Ignore()
    public void testLeakingActivationsWithDetachedExpiredNonCancelling() throws Exception {
        // JBRULES-3558
        String drl = "package org.drools;\n" +
                "\n" +
                "import java.util.List\n" +
                "\n" +
                "global List list; \n" +
                "" +
                "declare Motion\n" +
                "  @role( event )\n" +
                "  @expires( 1ms )\n" +
                "  @timestamp( timestamp )\n" +
                "  timestamp : long\n" +
                "end\n" +
                "\n" +
                "declare Recording\n" +
                "end\n" +
                "\n" +
                "" +
                "rule Init \n" +
                "salience 1000\n" +
                "when\n" +
                "   $l : Long() \n" +
                "then\n" +
                "   System.out.println( \" Insert motion \" + $l );\n" +
                "   insert( new Motion( $l ) ); \n" +
                "end\n" +
                "" +
                "rule \"StartRecording\"\n" +
                "  when\n" +
                "    $mot : Motion()\n" +
                "    not Recording()\n" +
                "  then\n" +
                "    list.add( $mot ); \n " +
                "    System.out.println(\"Recording started\");\n" +
                "    insert(new Recording());\n" +
                "end\n";

        final KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBaseFromString( kbconf,  drl );
        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( ClockType.REALTIME_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, ksconf);

        List<Number> list = new ArrayList<Number>();

        ksession.setGlobal( "list", list );

        ksession.insert( new Long( 1000 ) );
        ksession.insert( new Long( 1001 ) );
        ksession.insert( new Long( 1002 ) );

        Thread.sleep( 2000 );

        ksession.fireAllRules();
        assertEquals( 1, list.size() );

    }




    public static class ProbeEvent {
        private int value = 1;
        public int getValue() { return value; }
        public ProbeEvent(int value) { this.value = value; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ProbeEvent that = (ProbeEvent) o;

            if (value != that.value) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public String toString() {
            return "ProbeEvent{" +
                    "value=" + value +
                    '}';
        }
    }
    public static class ProbeCounter {
        private long total = 0;
        public void setTotal(long total) { this.total = total; }
        public long getTotal() { return total; }
        public void addValue () { total += 1; }
    }

    @Test
    public void testExpirationAtHighRates() throws InterruptedException {
        String drl = "package drools5fusioneval\n" +
                     "" +
                     "global java.util.List list; \n" +
                "" +
                "import org.drools.integrationtests.CepEspTest.ProbeEvent;\n" +
                "import org.drools.integrationtests.CepEspTest.ProbeCounter;\n" +
                "\n" +
                "declare ProbeEvent\n" +
                "    @role (event)\n" +
                "    @expires(1ms)\n" +
                "end\n" +
                "\n" +
                "rule \"Probe rule\"\n" +
                "when\n" +
                "    $pe : ProbeEvent () from entry-point ep01\n" +
                "    $pc : ProbeCounter ()\n" +
                "then\n" +
                "   list.add( $pe.getValue() ); \n" +
                "    $pc.addValue ();\n" +
                "end";

        KnowledgeBaseConfiguration kbconfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconfig.setOption (EventProcessingOption.STREAM);

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbconfig);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder ();
        kbuilder.add (ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            System.err.println (kbuilder.getErrors().toString());
        }
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList( );
        session.setGlobal( "list", list );
        WorkingMemoryEntryPoint ep01 = session.getWorkingMemoryEntryPoint("ep01");


        new Thread () {
            public void run () {
                session.fireUntilHalt();
            }
        }.start ();

        int eventLimit = 5000;

        ProbeCounter pc = new ProbeCounter ();
        long myTotal = 0;

        try {
            FactHandle pch = session.insert(pc);
            for ( int i = 0; i < eventLimit; i++ ) {
                ep01.insert ( new ProbeEvent ( i ) );
                myTotal++;
            }

            Thread.sleep( 2000 );
        } catch ( Throwable t ) {
            fail( t.getMessage() );
        }

        assertEquals( eventLimit, myTotal );
        assertEquals( eventLimit, list.size() );
        assertEquals( 0, session.getWorkingMemoryEntryPoint( "ep01" ).getObjects().size() );
    }


    @Test
    public void AfterOperatorInCEPQueryTest() {

        String drl = "package org.drools;\n" +
                     "\n" +
                     "declare StockTick\n" +
                     "    @role( event )\n" +
                     "end\n" +
                     "\n" +
                     "query EventsBeforeNineSeconds\n" +
                     "   $event : StockTick() from entry-point EStream\n" +
                     "   $result : StockTick ( this after [0s, 9s] $event) from entry-point EventStream\n" +
                     "end\n" +
                     "\n" +
                     "query EventsBeforeNineteenSeconds\n" +
                     "   $event : StockTick() from entry-point EStream\n" +
                     "   $result : StockTick ( this after [0s, 19s] $event) from entry-point EventStream\n" +
                     "end\n" +
                     "\n" +
                     "query EventsBeforeHundredSeconds\n" +
                     "   $event : StockTick() from entry-point EStream\n" +
                     "   $result : StockTick ( this after [0s, 100s] $event) from entry-point EventStream\n" +
                     "end\n";

        final KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBaseFromString( kbconf,  drl );

        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, ksconf);

        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        WorkingMemoryEntryPoint ePoint = ksession.getWorkingMemoryEntryPoint( "EStream" );
        WorkingMemoryEntryPoint entryPoint = ksession.getWorkingMemoryEntryPoint( "EventStream" );


        ePoint.insert(new StockTick(0L, "zero", 0.0, 0));

        entryPoint.insert(new StockTick(1L, "one", 0.0, 0));

        clock.advanceTime( 10, TimeUnit.SECONDS );

        entryPoint.insert(new StockTick(2L, "two",0.0,  0));

        clock.advanceTime( 10, TimeUnit.SECONDS );

        entryPoint.insert(new StockTick(3L, "three", 0.0, 0));

        QueryResults results = ksession.getQueryResults("EventsBeforeNineSeconds");

        assertEquals( 1, results.size());

        results = ksession.getQueryResults("EventsBeforeNineteenSeconds");

        assertEquals( 2, results.size() );

        results = ksession.getQueryResults("EventsBeforeHundredSeconds");

        assertEquals( 3, results.size() );

        ksession.dispose();
    }


    @Test
    public void testFromWithEvents() {
        String drl = "\n" +
                     "\n" +
                     "package org.drools.test\n" +
                     "global java.util.List list; \n" +
                     "\n" +
                     "declare MyEvent\n" +
                     "@role(event)\n" +
                     "@timestamp( stamp )\n" +
                     "id : int\n" +
                     "stamp : long\n" +
                     "end\n" +
                     "\n" +
                     "declare MyBean\n" +
                     "id : int\n" +
                     "event : MyEvent\n" +
                     "end\n" +
                     "\n" +
                     "rule \"Init\"\n" +
                     "when\n" +
                     "then\n" +
                     "MyEvent ev = new MyEvent( 1, 1000 );\n" +
                     "MyBean bin = new MyBean( 99, ev );\n" +
                     "MyEvent ev2 = new MyEvent( 2, 2000 );\n" +
                     "\n" +
                     "drools.getWorkingMemory().getWorkingMemoryEntryPoint( \"X\" ).insert( ev2 );\n" +
                     "insert( bin );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"Check\"\n" +
                     "when\n" +
                     "$e2 : MyEvent( id == 2 ) from entry-point \"X\" \n" +
                     "$b1 : MyBean( id == 99, $ev : event )\n" +
                     "MyEvent( this before $e2 ) from $ev\n" +
                     "then\n" +
                     "System.out.println( \"Success\" );\n" +
                     "list.add( 1 ); \n" +
                     "end\n";
        KnowledgeBase kb = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ks = kb.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList( 1 );
        ks.setGlobal( "list", list );
        ks.fireAllRules();
        assertEquals( Arrays.asList( 1 ), list );

    }


    @Test
    public void testDeserializationWithTrackableTimerJob() throws InterruptedException {
        String drl = "package org.drools.test;\n" +
                     "import org.drools.StockTick; \n" +
                     "global java.util.List list;\n" +
                     "\n" +
                     "declare StockTick\n" +
                     "  @role( event )\n" +
                     "  @expires( 1s )\n" +
                     "end\n" +
                     "\n" +
                     "rule \"One\"\n" +
                     "when\n" +
                     "  StockTick( $id : seq, company == \"AAA\" ) over window:time( 1s )\n" +
                     "then\n" +
                     "  list.add( $id ); \n" +
                     "end\n" +
                     "\n" +
                     "rule \"Two\"\n" +
                     "when\n" +
                     "  StockTick( $id : seq, company == \"BBB\" ) \n" +
                     "then\n" +
                     "  System.out.println( $id ); \n" +
                     "  list.add( $id );\n" +
                     "end";
        final KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );

        KnowledgeSessionConfiguration knowledgeSessionConfiguration = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        knowledgeSessionConfiguration.setOption( TimerJobFactoryOption.get( "trackable" ) );

        KnowledgeBase kb = loadKnowledgeBaseFromString( kbconf, drl );
        StatefulKnowledgeSession ks = kb.newStatefulKnowledgeSession( knowledgeSessionConfiguration, null );

        ks.insert( new StockTick( 2, "BBB", 1.0, 0 ) );
        Thread.sleep( 1100 );

        try {
            ks = SerializationHelper.getSerialisedStatefulKnowledgeSession( ks, true, false );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        ks.addEventListener( new DebugAgendaEventListener(  ) );

        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.fireAllRules();

        ks.insert( new StockTick( 3, "BBB", 1.0, 0 ) );
        ks.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( Arrays.asList( 2L, 3L ), list );


    }


    @Test
    public void testWindowExpireActionDeserialization() throws InterruptedException {
        String drl = "package org.drools.test;\n" +
                     "import org.drools.StockTick; \n" +
                     "global java.util.List list; \n" +
                     "\n" +
                     "declare StockTick\n" +
                     "  @role( event )\n" +
                     "end\n" +
                     "\n" +
                     "rule \"One\"\n" +
                     "when\n" +
                     "  StockTick( $id : seq, company == \"BBB\" ) over window:time( 1s )\n" +
                     "then\n" +
                     "  list.add( $id );\n" +
                     "end\n" +
                     "\n" +
                     "";
        final KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kb = loadKnowledgeBaseFromString( kbconf, drl );
        StatefulKnowledgeSession ks = kb.newStatefulKnowledgeSession( );

        ks.insert( new StockTick( 2, "BBB", 1.0, 0 ) );
        Thread.sleep( 1200 );

        try {
            ks = SerializationHelper.getSerialisedStatefulKnowledgeSession( ks, true, false );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.fireAllRules();

        ks.insert( new StockTick( 3, "BBB", 1.0, 0 ) );
        ks.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( Arrays.asList( 3L ), list );


    }

    @Test
    public void testDuplicateFiring1() throws InterruptedException {

        String drl = "package org.test;\n" +
                     "import org.drools.StockTick;\n " +
                     "" +
                     "global java.util.List list \n" +
                     "" +
                     "declare StockTick @role(event) end \n" +
                     "" +
                     "rule \"slidingTimeCount\"\n" +
                     "when\n" +
                     "\t$n: Number ( intValue > 0 ) from accumulate ( $e: StockTick() over window:time(300ms) from entry-point SensorEventStream, count($e))\n" +
                     "then\n" +
                     "  list.add( $n ); \n" +
                     "  System.out.println( \"Events in last 3 seconds: \" + $n );\n" +
                     "end" +
                     "" +
                     "\n" +
                     "rule \"timerRuleAfterAllEvents\"\n" +
                     "        timer ( int: 2s )\n" +
                     "when\n" +
                     "        $room : String( )\n" +
                     "then\n" +
                     "  list.add( -1 ); \n" +
                     "  System.out.println(\"2sec after room was modified\");\n" +
                     "end " +
                     "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        // Check the builder for errors
        if (kbuilder.hasErrors()) {
            fail( kbuilder.getErrors().toString() );
        }

        //configure knowledge base
        KnowledgeBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(baseConfig);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //init session clock
        KnowledgeSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get("realtime") );
        //init stateful knowledge session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(sessionConfig, null);
        ArrayList list = new ArrayList(  );
        ksession.setGlobal( "list", list );

        //entry point for sensor events
        WorkingMemoryEntryPoint sensorEventStream = ksession.getWorkingMemoryEntryPoint( "SensorEventStream" );

        ksession.insert( "Go" );
        System.out.println("1. fireAllRules()");

        //insert events
        for(int i=2;i<8;i++){
            StockTick event = new StockTick( (i-1), "XXX", 1.0, 0 );
            sensorEventStream.insert( event );

            System.out.println(i + ". fireAllRules()");
            ksession.fireAllRules();

            Thread.sleep(105);
        }

        //let thread sleep for another 1m to see if dereffered rules fire (timers, (not) after rules)
        Thread.sleep(100*40*1);

        assertEquals( Arrays.asList( 1L, 2L, 3L, 3L, 3L, 3L, -1 ), list );

        ksession.dispose();
    }

    @Test
    public void testDuplicateFiring2() throws InterruptedException {

        String drl = "package org.test;\n" +
                     "import org.drools.StockTick;\n " +
                     "" +
                     "global java.util.List list \n" +
                     "" +
                     "declare StockTick @role(event) end \n" +
                     "" +
                     "rule Tick when $s : StockTick() then System.out.println( $s ); end \n" +
                     "" +
                     "rule \"slidingTimeCount\"\n" +
                     "when\n" +
                     "\t$n: Number ( intValue > 0 ) from accumulate ( $e: StockTick() over window:time(3s), count($e))\n" +
                     "then\n" +
                     "  list.add( $n ); \n" +
                     "  System.out.println( \"Events in last 3 seconds: \" + $n );\n" +
                     "end" +
                     "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);

        // Check the builder for errors
        if (kbuilder.hasErrors()) {
            fail( kbuilder.getErrors().toString() );
        }

        //configure knowledge base
        KnowledgeBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.CLOUD );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(baseConfig);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //init session clock
        KnowledgeSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get("pseudo") );
        //init stateful knowledge session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(sessionConfig, null);
        SessionPseudoClock clock = ksession.getSessionClock();
        ArrayList list = new ArrayList(  );
        ksession.setGlobal( "list", list );


        //insert events
        for(int i=1;i<3;i++){
            StockTick event = new StockTick( (i-1), "XXX", 1.0, 0 );
            clock.advanceTime( 1001, TimeUnit.MILLISECONDS );
            ksession.insert( event );

            System.out.println(i + ". rule invocation");
            ksession.fireAllRules();
        }

        clock.advanceTime( 3001, TimeUnit.MILLISECONDS );
        StockTick event = new StockTick( 3, "XXX", 1.0, 0 );
        System.out.println("3. rule invocation");
        ksession.insert( event );
        ksession.fireAllRules();

        clock.advanceTime( 3001, TimeUnit.MILLISECONDS );
        StockTick event2 = new StockTick( 3, "XXX", 1.0, 0 );
        System.out.println("4. rule invocation");
        ksession.insert( event2 );
        ksession.fireAllRules();

        ksession.dispose();

        assertEquals( Arrays.asList( 1L, 2L, 1L, 1L ), list );
    }


    @Test
    public void testPastEventExipration() throws InterruptedException {
        //DROOLS-257
        String drl = "package org.test;\n" +
                     "import org.drools.StockTick;\n " +
                     "" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare StockTick @role(event) @timestamp( time ) @expires( 200ms ) end \n" +
                     "" +
                     "rule \"slidingTimeCount\"\n" +
                     "when\n" +
                     "  accumulate ( $e: StockTick() over window:length(10), $n : count($e) )\n" +
                     "then\n" +
                     "  list.add( $n ); \n" +
                     "  System.out.println( \"Events in last 3 seconds: \" + $n );\n" +
                     "end" +
                     "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get("realtime") );
        //init stateful knowledge session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( sessionConfig, null );
        ArrayList list = new ArrayList(  );
        ksession.setGlobal( "list", list );

        long now = new Date().getTime();

        StockTick event1 = new StockTick( 1, "XXX", 1.0, now );
        StockTick event2 = new StockTick( 2, "XXX", 1.0, now + 240 );
        StockTick event3 = new StockTick( 2, "XXX", 1.0, now + 380 );
        StockTick event4 = new StockTick( 2, "XXX", 1.0, now + 500 );

        ksession.insert( event1 );
        ksession.insert( event2 );
        ksession.insert( event3 );
        ksession.insert( event4 );

        Thread.sleep( 220 );

        ksession.fireAllRules();

        Thread.sleep( 400 );

        ksession.fireAllRules();

        assertEquals( Arrays.asList( 3L, 1L ), list );
    }



    public static class Event {
        private int type;
        private int value;
        private long time;

        public Event( int type, int value, long time ) {
            this.type = type;
            this.value = value;
            this.time = time;
        }

        public int getType() {
            return type;
        }

        public void setType( int type ) {
            this.type = type;
        }

        public int getValue() {
            return value;
        }

        public void setValue( int value ) {
            this.value = value;
        }

        public long getTime() {
            return time;
        }

        public void setTime( long time ) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "Event{" +
                   "type=" + type +
                   ", value=" + value +
                   ", time=" + ( ( time % 10000 ) )+
                   '}';
        }
    }

    @Test
    public void testLastEvent() throws InterruptedException {
        String drl = "\n" +
                     "import org.drools.integrationtests.CepEspTest.Event; \n" +
                     "import org.drools.time.SessionClock; \n" +
                     "import java.util.Date; \n" +
                     "global java.util.List list; \n" +
                     "" +
                     "declare Event \n" +
                     "  @role( event )\n" +
                     "  @timestamp( time ) \n" +
                     "  @expires( 10000000 ) \n" +
                     "end \n" +
                     "" +
                     "" +
                     "rule \"inform about E1\"\n" +
                     "when\n" +
                     "  String() \n" +
                     "  $event1 : Event( type == 1 )\n" +
                     "    //there is an event (T2) with value 0 between 0,2m after doorClosed\n" +
                     "  $event2: Event( type == 2, value == 1, this after [0, 1200ms] $event1, $timestamp : time )\n" +
                     "    //there is no newer event (T2) within the timeframe\n" +
                     "  not Event( type == 2, this after [0, 1200ms] $event1, time > $timestamp ) \n" +
                     "then\n" +
                     "  System.out.println( \"E1 valid \" );\n" +
                     "  list.add( (new Date().getTime() % 10000 ) ); \n " +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.REALTIME_CLOCK.getId() ) );
        //init stateful knowledge session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( sessionConfig, null );
        ArrayList list = new ArrayList(  );
        ksession.setGlobal( "list", list );
        ksession.addEventListener( new DebugAgendaEventListener(  ) );

        long t0 = new Date().getTime() % 10000;
        System.out.println( "Insert e1 at " + t0 );
        ksession.insert( new Event( 1, -1, new Date().getTime() ) );
        Thread.sleep( 600 );
        ksession.fireAllRules();
        ksession.insert( new Event( 2, 0, new Date().getTime() ) );
        Thread.sleep( 100 );
        ksession.fireAllRules();
        ksession.insert( new Event( 2, 0, new Date().getTime() ) );
        Thread.sleep( 300 );
        ksession.fireAllRules();
        ksession.insert( new Event( 2, 0, new Date().getTime() ) );
        Thread.sleep( 100 );
        ksession.fireAllRules();

        ksession.insert( "go" );
        ksession.insert( new Event( 2, 1, new Date().getTime() ) );
        Thread.sleep( 100 );
        ksession.fireAllRules();
        Thread.sleep( 100 );
        ksession.fireAllRules();
        ksession.insert( new Event( 2, 0, new Date().getTime() ) );

        Thread.sleep( 1000 );
        ksession.fireAllRules();

        assertFalse( list.isEmpty() );
        assertEquals( 1, list.size() );
        Long time = (Long) list.get( 0 ) - t0;

        System.out.print( time );
        assertTrue( time > 1000 && time < 1500 );

        ksession.dispose();

    }



    @Test
    public void testEventTimestamp() {
        // DROOLS-268
        String drl = "\n" +
                     "import org.drools.integrationtests.CepEspTest.Event; \n" +
                     "global java.util.List list; \n" +
                     "global org.drools.time.SessionPseudoClock clock; \n" +
                     "" +
                     "declare Event \n" +
                     " @role( event )\n" +
                     " @timestamp( time ) \n" +
                     " @expires( 10000000 ) \n" +
                     "end \n" +
                     "" +
                     "" +
                     "rule \"inform about E1\"\n" +
                     "when\n" +
                     " $event1 : Event( type == 1 )\n" +
                     " //there is an event (T2) with value 0 between 0,2m after doorClosed\n" +
                     " $event2: Event( type == 2, value == 1, this after [0, 1200ms] $event1, $timestamp : time )\n" +
                     " //there is no newer event (T2) within the timeframe\n" +
                     " not Event( type == 2, this after [0, 1200ms] $event1, time > $timestamp ) \n" +
                     "then\n" +
                     " list.add( clock.getCurrentTime() ); \n " +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get(ClockType.PSEUDO_CLOCK.getId()) );

        //init stateful knowledge session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( sessionConfig, null );
        ArrayList list = new ArrayList( );
        ksession.setGlobal( "list", list );

        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        ksession.setGlobal( "clock", clock );

        ksession.insert( new Event( 1, -1, clock.getCurrentTime() ) ); // 0
        clock.advanceTime(600, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();
        ksession.insert( new Event( 2, 0, clock.getCurrentTime() ) ); // 600
        clock.advanceTime(100, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();
        ksession.insert( new Event( 2, 0, clock.getCurrentTime() ) ); // 700
        clock.advanceTime(300, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();
        ksession.insert( new Event( 2, 0, clock.getCurrentTime() ) ); // 1000
        clock.advanceTime(100, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();
        ksession.insert( new Event( 2, 1, clock.getCurrentTime() ) ); // 1100
        clock.advanceTime(100, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();
        clock.advanceTime(100, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();
        ksession.insert( new Event( 2, 0, clock.getCurrentTime() ) ); // 1300

        clock.advanceTime(1000, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();

        assertFalse( list.isEmpty() );
        assertEquals( 1, list.size() );
        Long time = (Long) list.get( 0 );

        assertTrue( time > 1000 && time < 1500 );

        ksession.dispose();
    }

    @Test
    public void testEventTimestamp2() {
        // DROOLS-268
        String drl = "\n" +
                     "import org.drools.integrationtests.CepEspTest.Event; \n" +
                     "global java.util.List list; \n" +
                     "global org.drools.time.SessionPseudoClock clock; \n" +
                     "" +
                     "declare Event \n" +
                     " @role( event )\n" +
                     " @timestamp( time ) \n" +
                     " @expires( 10000000 ) \n" +
                     "end \n" +
                     "" +
                     "" +
                     "rule \"inform about E1\"\n" +
                     "when\n" +
                     " $event1 : Event( type == 1 )\n" +
                     " $event2: Event( type == 2 )\n" +
                     " not Event( type == 3, this after [0, 1000ms] $event1 ) \n" +
                     "then\n" +
                     " list.add( clock.getCurrentTime() ); \n " +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        //init stateful knowledge session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( sessionConfig, null );
        ArrayList list = new ArrayList( );
        ksession.setGlobal( "list", list );

        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        ksession.setGlobal( "clock", clock );

        ksession.insert( new Event( 1, 0, clock.getCurrentTime() ) );
        clock.advanceTime(600, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();

        ksession.insert( new Event( 2, 0, clock.getCurrentTime() ) );
        clock.advanceTime(600, TimeUnit.MILLISECONDS);
        ksession.insert( new Event( 3, 0, clock.getCurrentTime() ) );
        ksession.fireAllRules();

        assertFalse( list.isEmpty() );
        assertEquals( 1, list.size() );
        long time = (Long) list.get( 0 );

        assertTrue( time >= 1000 );

        ksession.dispose();
    }
}

