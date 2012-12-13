package org.drools.integrationtests;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

import org.drools.ClockType;
import org.drools.CommonTestMethodBase;
import org.drools.OrderEvent;
import org.drools.RuleBaseConfiguration;
import org.drools.Sensor;
import org.drools.StockTick;
import org.drools.StockTickInterface;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.base.ClassObjectType;
import org.drools.base.evaluators.TimeIntervalParser;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;
import org.drools.spi.ObjectType;
import org.drools.time.SessionPseudoClock;
import org.drools.time.impl.DurationTimer;
import org.drools.time.impl.PseudoClockScheduler;
import org.junit.Assert;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KieBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.conf.EqualityBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.definition.KnowledgePackage;
import org.kie.event.rule.MatchCreatedEvent;
import org.kie.event.rule.AfterMatchFiredEvent;
import org.kie.event.rule.AgendaEventListener;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.KieSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.conf.ClockTypeOption;
import org.kie.runtime.rule.FactHandle;
import org.kie.runtime.rule.Match;
import org.kie.runtime.rule.SessionEntryPoint;
import org.kie.time.SessionClock;
import org.mockito.ArgumentCaptor;

public class CepEspTest extends CommonTestMethodBase {
    
    @Test
    public void testComplexTimestamp() {
        String rule = "";
        rule += "package " + Message.class.getPackage().getName() + "\n" +
                "declare " + Message.class.getCanonicalName() + "\n" +
                 "   @role( event ) \n" +
                 "   @timestamp( getProperties().get( 'timestamp' )-1 ) \n" +
                 "   @duration( getProperties().get( 'duration' )+1 ) \n" +
                "end\n";
        
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
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
        KnowledgeBase kbase = loadKnowledgeBase( "test_CEP_SimpleEventAssertion.drl" );
        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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
        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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
        KnowledgeBase kbase = loadKnowledgeBase( "test_CEP_SimpleEventAssertionWithDuration.drl" );
        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession session = createKnowledgeSession(kbase, conf);

        final List results = new ArrayList();

        session.setGlobal( "results",
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

        session.fireAllRules();

        assertEquals( 2,
                      results.size() );

    }

    @Test
    public void testEventAssertionWithDateTimestamp() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "test_CEP_SimpleEventAssertionWithDateTimestamp.drl" );
        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession session = createKnowledgeSession(kbase, conf);

        final List results = new ArrayList();

        session.setGlobal( "results",
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

        session.fireAllRules();

        assertEquals( 2,
                      results.size() );

    }

    @Test
    public void testEventExpiration() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "test_CEP_EventExpiration.drl" );

        // read in the source
        TypeDeclaration factType = ((InternalRuleBase)((KnowledgeBaseImpl)kbase).ruleBase).getTypeDeclaration( StockTick.class );
        final TimeIntervalParser parser = new TimeIntervalParser();

        assertEquals( parser.parse( "1h30m" )[0].longValue(),
                      factType.getExpirationOffset() );
    }

    @Test
    public void testEventExpiration2() throws Exception {
        // read in the source
        KieBaseConfiguration kbc = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbc.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBase( kbc, "test_CEP_EventExpiration2.drl" );

        final InternalRuleBase internal = (InternalRuleBase) ((KnowledgeBaseImpl)kbase).ruleBase;
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
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_EventExpiration3.drl" );
        
        final InternalRuleBase internal = (InternalRuleBase) ((KnowledgeBaseImpl)kbase).ruleBase;
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
        final KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_EventExpiration4.drl" );

        final KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( "pseudo" ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, sconf);

        SessionEntryPoint eventStream = ksession.getWorkingMemoryEntryPoint( "Event Stream" );

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
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_TimeRelationalOperators.drl" );
        
        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase, sconf );
        
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

        //        wm  = SerializationHelper.serializeObject(wm);
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
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_BeforeOperator.drl" );
        
        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase, sconf );

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

        ArgumentCaptor<MatchCreatedEvent> arg = ArgumentCaptor.forClass( MatchCreatedEvent.class );
        verify( ael ).matchCreated(arg.capture());
        assertThat( arg.getValue().getMatch().getRule().getName(),
                    is( "before" ) );

        ksession.fireAllRules();

        verify( ael ).afterMatchFired(any(AfterMatchFiredEvent.class));
    }

    @Test
    public void testMetByOperator() throws Exception {
        // read in the source
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_MetByOperator.drl" );
        
        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase, sconf );

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

        ArgumentCaptor<MatchCreatedEvent> arg = ArgumentCaptor.forClass( MatchCreatedEvent.class );
        verify( ael ).matchCreated(arg.capture());
        Match activation = arg.getValue().getMatch();
        assertThat( activation.getRule().getName(),
                    is( "metby" ) );

        ksession.fireAllRules();

        ArgumentCaptor<AfterMatchFiredEvent> aaf = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );
        verify( ael ).afterMatchFired(aaf.capture());
        assertThat( (InternalFactHandle) aaf.getValue().getMatch().getFactHandles().toArray()[0],
                    is( fh2 ) );
    }

    @Test
    public void testAfterOnArbitraryDates() throws Exception {
        // read in the source
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_AfterOperatorDates.drl" );
        
        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase, sconf );

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
    public void testBeforeOnArbitraryDates() throws Exception {
        // read in the source
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_BeforeOperatorDates.drl" );
        
        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase, sconf );

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
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_CoincidesOperatorDates.drl" );
        
        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase, sconf );

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
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_SimpleTimeWindow.drl" );
        
        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase, sconf );

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
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_SimpleLengthWindow.drl" );
        
        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase, sconf );

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
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_DelayingNot.drl" );
        
        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase, sconf );

        final Rule rule = (Rule) kbase.getRule( "org.drools", "Delaying Not" );
        assertEquals( 10000,
                      ((DurationTimer) rule.getTimer()).getDuration() );

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
        
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBaseFromString( conf, str );
        
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
        
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( conf, str );

        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_SimpleTimeWindow.drl" );
        
        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase, sconf );

        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( wm );
        File testTmpDir = new File( "target/test-tmp/" );
        testTmpDir.mkdirs();
        logger.setFileName( "target/test-tmp/testIdleTimeAndTimeToNextJob-audit" );

        try {
            List results = new ArrayList();

            wm.setGlobal( "results",
                          results );
            InternalWorkingMemory iwm = (InternalWorkingMemory) ((StatefulKnowledgeSessionImpl)wm).session;

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
        final KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( kbconf, "test_CEP_CollectWithWindows.drl" );

        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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
        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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
        final KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase1 = loadKnowledgeBase( kbconf, "test_CEP_StreamMode.drl" );

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

        ArgumentCaptor<AfterMatchFiredEvent> aafe1 = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );
        verify( ael1,
                times( 1 ) ).afterMatchFired(aafe1.capture());
        List<AfterMatchFiredEvent> events1 = aafe1.getAllValues();
        assertThat( events1.get( 0 ).getMatch().getDeclarationValue( "$avg" ),
                    is( (Object) 10 ) );

        ksession1.insert( new Sensor( 20,
                                      20 ) );
        ksession1.fireAllRules();
        verify( ael1,
                times( 2 ) ).afterMatchFired(aafe1.capture());
        assertThat( events1.get( 1 ).getMatch().getDeclarationValue( "$avg" ),
                    is( (Object) 15 ) );
        ksession1.insert( new Sensor( 30,
                                      30 ) );
        ksession1.fireAllRules();
        verify( ael1,
                times( 3 ) ).afterMatchFired(aafe1.capture());
        assertThat( events1.get( 2 ).getMatch().getDeclarationValue( "$avg" ),
                    is( (Object) 25 ) );

        ksession1.dispose();

        // -------------
        // now we check the serialized session
        // -------------
        ArgumentCaptor<AfterMatchFiredEvent> aafe2 = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );

        ksession2.insert( new Sensor( 10,
                                      10 ) );
        ksession2.fireAllRules();
        verify( ael2,
                times( 1 ) ).afterMatchFired(aafe2.capture());
        List<AfterMatchFiredEvent> events2 = aafe2.getAllValues();
        assertThat( events2.get( 0 ).getMatch().getDeclarationValue( "$avg" ),
                    is( (Object) 10 ) );

        ksession2.insert( new Sensor( 20,
                                      20 ) );
        ksession2.fireAllRules();
        verify( ael2,
                times( 2 ) ).afterMatchFired(aafe2.capture());
        assertThat( events2.get( 1 ).getMatch().getDeclarationValue( "$avg" ),
                    is( (Object) 15 ) );

        ksession2.insert( new Sensor( 30,
                                      30 ) );
        ksession2.fireAllRules();
        verify( ael2,
                times( 3 ) ).afterMatchFired(aafe2.capture());
        assertThat( events2.get( 2 ).getMatch().getDeclarationValue( "$avg" ),
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

        final KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        kbconf.setOption( EqualityBehaviorOption.IDENTITY );
        final KnowledgeBase kbase1 = loadKnowledgeBase( kbconf, "test_CEP_AssertBehaviorOnEntryPoints.drl" );

        final StatefulKnowledgeSession ksession = kbase1.newStatefulKnowledgeSession();
        AgendaEventListener ael1 = mock( AgendaEventListener.class );
        ksession.addEventListener( ael1 );
        SessionEntryPoint ep1 = ksession.getWorkingMemoryEntryPoint( "stocktick stream" );

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
                times( 3 ) ).afterMatchFired(any(AfterMatchFiredEvent.class));

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

        final KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        kbconf.setOption( EqualityBehaviorOption.EQUALITY );
        final KnowledgeBase kbase1 = loadKnowledgeBase( kbconf, "test_CEP_AssertBehaviorOnEntryPoints.drl" );

        final StatefulKnowledgeSession ksession1 = kbase1.newStatefulKnowledgeSession();
        AgendaEventListener ael1 = mock( AgendaEventListener.class );
        ksession1.addEventListener( ael1 );
        SessionEntryPoint ep1 = ksession1.getWorkingMemoryEntryPoint( "stocktick stream" );

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
                times( 2 ) ).afterMatchFired(any(AfterMatchFiredEvent.class));

        ksession1.dispose();
    }

    @Test
    public void testEventDeclarationForInterfaces() throws Exception {
        // read in the source
        final KnowledgeBase kbase = loadKnowledgeBase( "test_CEP_EventInterfaces.drl" );

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
        KnowledgeBase kbase = loadKnowledgeBase( kbconf, "test_CEP_TemporalOperators.drl" );

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
        KnowledgeBase kbase = loadKnowledgeBase( kbconf, "test_CEP_TemporalOperators2.drl" );

        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, sconf);
        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();

        SessionEntryPoint ep = ksession.getWorkingMemoryEntryPoint( "X" );

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
        KnowledgeBase kbase = loadKnowledgeBase( kbconf, "test_CEP_TemporalOperators3.drl" );

        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( sconf,
                                                                               null );
        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();

        SessionEntryPoint ep = ksession.getWorkingMemoryEntryPoint( "X" );

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

        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( config, str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        ksession.fireAllRules();

        ArgumentCaptor<AfterMatchFiredEvent> captor = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );
        verify( ael,
                times( 7 ) ).afterMatchFired(captor.capture());

        List<AfterMatchFiredEvent> values = captor.getAllValues();
        // first rule
        Match act = values.get( 0 ).getMatch();
        assertThat( act.getRule().getName(),
                    is( "launch" ) );

        // second rule
        act = values.get( 1 ).getMatch();
        assertThat( act.getRule().getName(),
                    is( "ba" ) );
        assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                    is( 3 ) );
        assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                    is( 2 ) );

        // third rule
        act = values.get( 2 ).getMatch();
        assertThat( act.getRule().getName(),
                    is( "ab" ) );
        assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                    is( 3 ) );
        assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                    is( 2 ) );

        // fourth rule
        act = values.get( 3 ).getMatch();
        assertThat( act.getRule().getName(),
                    is( "ba" ) );
        assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                    is( 3 ) );
        assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                    is( 1 ) );

        // fifth rule
        act = values.get( 4 ).getMatch();
        assertThat( act.getRule().getName(),
                    is( "ab" ) );
        assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                    is( 3 ) );
        assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                    is( 1 ) );

        // sixth rule
        act = values.get( 5 ).getMatch();
        assertThat( act.getRule().getName(),
                    is( "ba" ) );
        assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                    is( 2 ) );
        assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                    is( 1 ) );

        // seventh rule
        act = values.get( 6 ).getMatch();
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

        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.CLOUD );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( config, str );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        SessionEntryPoint ep = ksession.getWorkingMemoryEntryPoint( "X" );

        ep.insert( new StockTick( 1,
                                  "RHT",
                                  10,
                                  1000 ) );
        int rulesFired = ksession.fireAllRules();
        assertEquals( 0,
                      rulesFired );

        org.kie.definition.type.FactType event = kbase.getFactType( "org.drools.cloud",
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

        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( config, str );
        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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

        ArgumentCaptor<AfterMatchFiredEvent> captor = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );
        verify( ael,
                times( 4 ) ).afterMatchFired(captor.capture());
        List<AfterMatchFiredEvent> aafe = captor.getAllValues();

        Assert.assertThat( aafe.get( 0 ).getMatch().getRule().getName(),
                           is( "R1" ) );
        Assert.assertThat( aafe.get( 1 ).getMatch().getRule().getName(),
                           is( "R1" ) );
        Assert.assertThat( aafe.get( 2 ).getMatch().getRule().getName(),
                           is( "R1" ) );
        Assert.assertThat( aafe.get( 3 ).getMatch().getRule().getName(),
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

        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( config, str );
        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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

        ArgumentCaptor<AfterMatchFiredEvent> captor = ArgumentCaptor.forClass( AfterMatchFiredEvent.class );
        verify( ael,
                times( 4 ) ).afterMatchFired(captor.capture());
        List<AfterMatchFiredEvent> aafe = captor.getAllValues();

        Assert.assertThat( aafe.get( 0 ).getMatch().getRule().getName(),
                           is( "R1" ) );
        Assert.assertThat( aafe.get( 1 ).getMatch().getRule().getName(),
                           is( "R1" ) );
        Assert.assertThat( aafe.get( 2 ).getMatch().getRule().getName(),
                           is( "R1" ) );
        Assert.assertThat( aafe.get( 3 ).getMatch().getRule().getName(),
                           is( "R3" ) );
    }
    
    
}
