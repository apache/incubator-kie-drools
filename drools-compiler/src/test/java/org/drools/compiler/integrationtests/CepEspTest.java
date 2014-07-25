package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.OrderEvent;
import org.drools.compiler.Sensor;
import org.drools.compiler.StockTick;
import org.drools.compiler.StockTickEvent;
import org.drools.compiler.StockTickInterface;
import org.drools.core.ClockType;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.WorkingMemory;
import org.drools.core.audit.WorkingMemoryFileLogger;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.evaluators.TimeIntervalParser;
import org.drools.core.common.DefaultAgenda;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.GarbageCollector;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.NodeMemories;
import org.drools.core.common.RightTupleSets;
import org.drools.core.common.StreamTupleEntryQueue;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.Rete;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.ObjectType;
import org.drools.core.time.SessionPseudoClock;
import org.drools.core.time.impl.DurationTimer;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.core.util.DateUtils;
import org.drools.core.util.DroolsStreamUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.TimerJobFactoryOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionClock;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CepEspTest extends CommonTestMethodBase {
    
    @Test(timeout=10000)
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
        props.put("timestamp",
                  new Integer(99));
        props.put( "duration",
                   new Integer( 52 ) );
        msg.setProperties(props);

        EventFactHandle efh = (EventFactHandle) ksession.insert( msg );
        assertEquals( 98,
                      efh.getStartTimestamp() );
        assertEquals( 53,
                      efh.getDuration() );

    }

    @Test(timeout=10000)
    public void testJavaSqlTimestamp() {
        String rule = "";
        rule += "package " + Message.class.getPackage().getName() + "\n" +
                "declare " + Message.class.getCanonicalName() + "\n" +
                 "   @role( event ) \n" +
                 "   @timestamp( startTime ) \n" +
                 "   @duration( duration )\n" +
                "end\n";
        
        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Message msg = new Message();
        msg.setStartTime( new Timestamp( 10000 ) );
        msg.setDuration( 1000l );

        EventFactHandle efh = (EventFactHandle) ksession.insert( msg );
        assertEquals( 10000,
                      efh.getStartTimestamp() );
        assertEquals( 1000,
                      efh.getDuration() );
    }

    @Test(timeout=10000)
    public void testEventAssertion() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "test_CEP_SimpleEventAssertion.drl" );
        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get("pseudo") );
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

    @Test(timeout=10000)
    public void testAnnotatedEventAssertion() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "test_CEP_SimpleAnnotatedEventAssertion.drl" );
        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get("pseudo") );
        StatefulKnowledgeSession session = createKnowledgeSession(kbase, conf);

        SessionPseudoClock clock = (SessionPseudoClock) session.<SessionClock>getSessionClock();

        final List results = new ArrayList();

        session.setGlobal( "results",
                           results );

        StockTickInterface tick1 = new StockTickEvent( 1,
                                                      "DROO",
                                                      50,
                                                      10000 );
        StockTickInterface tick2 = new StockTickEvent( 2,
                                                      "ACME",
                                                      10,
                                                      10010 );
        StockTickInterface tick3 = new StockTickEvent( 3,
                                                      "ACME",
                                                      10,
                                                      10100 );
        StockTickInterface tick4 = new StockTickEvent( 4,
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
    @Test(timeout=10000)
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

    @Test(timeout=10000)
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

    @Test(timeout=10000)
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

    @Test(timeout=10000)
    public void testEventExpiration() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "test_CEP_EventExpiration.drl" );

        // read in the source
        TypeDeclaration factType = ((KnowledgeBaseImpl)kbase).getTypeDeclaration( StockTick.class );
        final TimeIntervalParser parser = new TimeIntervalParser();

        assertEquals( parser.parse( "1h30m" )[0].longValue(),
                      factType.getExpirationOffset() );
    }

    @Test(timeout=10000)
    public void testEventExpiration2() throws Exception {
        // read in the source
        KieBaseConfiguration kbc = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbc.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBase( kbc, "test_CEP_EventExpiration2.drl" );

        final TimeIntervalParser parser = new TimeIntervalParser();

        Map<ObjectType, ObjectTypeNode> objectTypeNodes = ((KnowledgeBaseImpl)kbase).getRete().getObjectTypeNodes( EntryPointId.DEFAULT );
        ObjectTypeNode node = objectTypeNodes.get( new ClassObjectType( StockTick.class ) );

        assertNotNull( node );

        // the expiration policy @expires(10m) should override the temporal operator usage 
        assertEquals( parser.parse( "10m" )[0].longValue() + 1,
                      node.getExpirationOffset() );
    }

    @Test(timeout=10000)
    public void testEventExpiration3() throws Exception {
        // read in the source
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_EventExpiration3.drl" );
        
        final TimeIntervalParser parser = new TimeIntervalParser();

        Map<ObjectType, ObjectTypeNode> objectTypeNodes = ((KnowledgeBaseImpl)kbase).getRete().getObjectTypeNodes( EntryPointId.DEFAULT );
        ObjectTypeNode node = objectTypeNodes.get( new ClassObjectType( StockTick.class ) );

        assertNotNull( node );

        // the expiration policy @expires(10m) should override the temporal operator usage 
        assertEquals( parser.parse( "10m" )[0].longValue() + 1,
                      node.getExpirationOffset() );
    }

    @Test(timeout=10000)
    public void testEventExpiration4() throws Exception {
        // read in the source
        final KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_EventExpiration4.drl" );

        final KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( "pseudo" ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, sconf);

        EntryPoint eventStream = ksession.getEntryPoint( "Event Stream" );

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

    @Test(timeout=10000)
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

    @Test(timeout=10000)
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

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        StockTickInterface tick1 = new StockTick( 1, "DROO", 50, System.currentTimeMillis(), 3 );        
        StockTickInterface tick2 = new StockTick( 2, "ACME", 10, System.currentTimeMillis(), 3 );
        StockTickInterface tick3 = new StockTick( 3, "ACME", 10, System.currentTimeMillis(), 3 );
        StockTickInterface tick4 = new StockTick( 4, "DROO", 50, System.currentTimeMillis(), 5 );
        StockTickInterface tick5 = new StockTick( 5, "ACME", 10, System.currentTimeMillis(), 5 );
        StockTickInterface tick6 = new StockTick( 6, "ACME", 10, System.currentTimeMillis(), 3 );
        StockTickInterface tick7 = new StockTick( 7, "ACME", 10, System.currentTimeMillis(), 5 );
        StockTickInterface tick8 = new StockTick( 8, "ACME", 10, System.currentTimeMillis(), 3 );
        

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

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        StockTick[] stocks = ( StockTick[] ) list.get(0);
        assertSame( tick4, stocks[0]);
        assertSame( tick2, stocks[1]);
    }

    @Test(timeout=10000)
    public void testComplexOperator() throws Exception {
        // read in the source
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_ComplexOperator.drl" );

        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase, sconf );

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        final PseudoClockScheduler clock = (PseudoClockScheduler) ksession.<SessionClock>getSessionClock();
        clock.setStartupTime( 1000 );

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );

        StockTickInterface tick1 = new StockTick( 1, "DROO", 50, 0, 3 );
        StockTickInterface tick2 = new StockTick( 2, "ACME", 10, 4, 3 );
        StockTickInterface tick3 = new StockTick( 3, "ACME", 10, 8, 3 );
        StockTickInterface tick4 = new StockTick( 4, "DROO", 50, 12, 5 );
        StockTickInterface tick5 = new StockTick( 5, "ACME", 10, 12, 5 );
        StockTickInterface tick6 = new StockTick( 6, "ACME", 10, 13, 3 );
        StockTickInterface tick7 = new StockTick( 7, "ACME", 10, 13, 5 );
        StockTickInterface tick8 = new StockTick( 8, "ACME", 10, 15, 3 );

        ksession.insert( tick1 );
        ksession.insert( tick2 );
        ksession.insert( tick3 );
        ksession.insert( tick4 );
        ksession.insert( tick5 );
        ksession.insert( tick6 );
        ksession.insert( tick7 );
        ksession.insert( tick8 );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        StockTick[] stocks = ( StockTick[] ) list.get(0);
        assertSame( tick4, stocks[0]);
        assertSame( tick2, stocks[1]);
    }

    @Test(timeout=10000)
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

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        StockTickInterface tick1 = new StockTick( 1, "DROO", 50, System.currentTimeMillis(), 3 );
        StockTickInterface tick2 = new StockTick( 2, "ACME", 10, System.currentTimeMillis(), 3 );
        StockTickInterface tick3 = new StockTick( 3, "ACME", 10, System.currentTimeMillis(), 3 );
        StockTickInterface tick4 = new StockTick( 4, "DROO", 50, System.currentTimeMillis(), 5 );
        StockTickInterface tick5 = new StockTick( 5, "ACME", 10, System.currentTimeMillis(), 5 );
        StockTickInterface tick6 = new StockTick( 6, "ACME", 10, System.currentTimeMillis(), 3 );
        StockTickInterface tick7 = new StockTick( 7, "ACME", 10, System.currentTimeMillis(), 5 );
        StockTickInterface tick8 = new StockTick( 8, "ACME", 10, System.currentTimeMillis(), 3 );

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

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        StockTick[] stocks = ( StockTick[] ) list.get(0);
        assertSame( tick1, stocks[0]);
        assertSame( tick2, stocks[1]);
    }

    @Test(timeout=10000)
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

    @Test(timeout=10000)
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

    @Test(timeout=10000)
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

    @Test(timeout=10000)
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

    @Test (timeout=10000)
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

    @Test(timeout=10000)
    public void testSimpleLengthWindowWithQueue() throws Exception {
        // read in the source
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_SimpleLengthWindow.drl" );

        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase, sconf );

        final List results = new ArrayList();

        ksession.setGlobal("results",
                           results);

        EventFactHandle handle1 = (EventFactHandle) ksession.insert( new OrderEvent( "1", "customer A", 80 ) );
        ksession  = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);


        // assert new data
        EventFactHandle handle2 = (EventFactHandle) ksession.insert( new OrderEvent( "2", "customer A", 70 ) );
        ksession  = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

        // assert new data
        EventFactHandle handle3 = (EventFactHandle) ksession.insert( new OrderEvent( "3", "customer A", 60 ) );
        ksession  = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

        // assert new data
        EventFactHandle handle4 = (EventFactHandle) ksession.insert( new OrderEvent( "4", "customer A", 50 ) );
        ksession  = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

        ksession.fireAllRules();

        assertEquals( 1,
                      results.size() );

        assertEquals(60,
                     ((Number) results.get(0)).intValue());

        // assert new data
        EventFactHandle handle5 = (EventFactHandle) ksession.insert( new OrderEvent( "5", "customer A", 10 ) );
        ksession  = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        assertEquals( 1,
                      results.size() );

        EventFactHandle handle6 = (EventFactHandle) ksession.insert( new OrderEvent( "6", "customer A", 90 ) );
        ksession  = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertEquals( 50,
                      ((Number) results.get( 1 )).intValue() );

    }



    @Test(timeout=10000)
    public void testDelayingNot() throws Exception {
        // read in the source
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_DelayingNot.drl" );
        
        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase, sconf );

        final RuleImpl rule = (RuleImpl) kbase.getRule( "org.drools.compiler", "Delaying Not" );
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

    @Test(timeout=10000)
    public void testDelayingNot2() throws Exception {
        String str = "package org.drools.compiler\n" +
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
    
    @Test(timeout=10000)
    public void testDelayingNotWithPreEpochClock() throws Exception {
        String str = "package org.drools.compiler\n" +
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
    
    //    @Test(timeout=10000) @Ignore
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

    @Test(timeout=10000)
    public void testIdleTime() throws Exception {
        // read in the source
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_CEP_SimpleEventAssertion.drl" ) ),
                      ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        StatefulKnowledgeSessionImpl session = (StatefulKnowledgeSessionImpl)createKnowledgeSession(kbase, conf);

        SessionPseudoClock clock = (SessionPseudoClock) session.getSessionClock();

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
                      session.getIdleTime() );
        InternalFactHandle handle1 = (InternalFactHandle) session.insert( tick1 );
        clock.advanceTime( 10,
                           TimeUnit.SECONDS );
        assertEquals( 10000,
                      session.getIdleTime() );
        InternalFactHandle handle2 = (InternalFactHandle) session.insert( tick2 );
        assertEquals( 0,
                      session.getIdleTime() );
        clock.advanceTime( 15,
                           TimeUnit.SECONDS );
        assertEquals( 15000,
                      session.getIdleTime() );
        clock.advanceTime( 15,
                           TimeUnit.SECONDS );
        assertEquals( 30000,
                      session.getIdleTime() );
        InternalFactHandle handle3 = (InternalFactHandle) session.insert( tick3 );
        assertEquals( 0,
                      session.getIdleTime() );
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
                      session.getIdleTime() );
        session.fireAllRules();
        assertEquals( 0,
                      session.getIdleTime() );

        assertEquals( 2,
                      ((List) session.getGlobal( "results" )).size() );

    }

    @Test(timeout=10000)
    public void testIdleTimeAndTimeToNextJob() throws Exception {
        // read in the source
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBase( conf, "test_CEP_SimpleTimeWindow.drl" );
        
        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSessionImpl wm = (StatefulKnowledgeSessionImpl)createKnowledgeSession( kbase, sconf );

        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( (WorkingMemory) wm );
        File testTmpDir = new File( "target/test-tmp/" );
        testTmpDir.mkdirs();
        logger.setFileName( "target/test-tmp/testIdleTimeAndTimeToNextJob-audit" );

        try {
            List results = new ArrayList();

            wm.setGlobal( "results",
                          results );

            // how to initialize the clock?
            // how to configure the clock?
            SessionPseudoClock clock = (SessionPseudoClock) wm.getSessionClock();
            clock.advanceTime( 5,
                               TimeUnit.SECONDS ); // 5 seconds

            // there is no next job, so returns -1
            assertEquals( -1,
                          wm.getTimeToNextJob() );
            wm.insert( new OrderEvent( "1",
                                       "customer A",
                                       70 ) );
            assertEquals( 0,
                          wm.getIdleTime() );
            // now, there is a next job in 30 seconds: expire the event
            assertEquals( 30000,
                          wm.getTimeToNextJob() );

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
                          wm.getTimeToNextJob() );

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
                          wm.getTimeToNextJob() );

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
                          wm.getIdleTime() );
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
                          wm.getIdleTime() );

            //        wm  = SerializationHelper.serializeObject(wm);
            wm.fireAllRules();

            // still under the threshold, so no fire
            assertEquals( 3,
                          results.size() );
        } finally {
            logger.writeToDisk();
        }
    }

    @Test(timeout=10000)
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

    @Test(timeout=10000)
    public void testPseudoSchedulerRemoveJobTest() {
        String str = "import " + CepEspTest.class.getName() + ".A\n";
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
        private Timestamp timestamp;
        private Long duration;

        public Properties getProperties() {
            return properties;
        }

        public void setProperties( Properties properties ) {
            this.properties = properties;
        }

        public Timestamp getStartTime() {
            return timestamp;
        }

        public void setStartTime(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        public Long getDuration() {
            return duration;
        }

        public void setDuration(Long duration) {
            this.duration = duration;
        }
    }

    @Test(timeout=10000)
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

    @Test(timeout=10000)
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
        EntryPoint ep1 = ksession.getEntryPoint( "stocktick stream" );

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

    @Test(timeout=10000)
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
        EntryPoint ep1 = ksession1.getEntryPoint( "stocktick stream" );

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

    @Test(timeout=10000)
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

    @Test(timeout=10000)
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

    @Test(timeout=10000)
    public void testTemporalOperators2() throws Exception {
        // read in the source
        final RuleBaseConfiguration kbconf = new RuleBaseConfiguration();
        kbconf.setEventProcessingMode( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBase( kbconf, "test_CEP_TemporalOperators2.drl" );

        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, sconf);

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();

        EntryPoint ep = ksession.getEntryPoint( "X" );

        clock.advanceTime( 1000,
                           TimeUnit.SECONDS );
        ep.insert( new StockTick( 1, "A", 10, clock.getCurrentTime() ) );

        clock.advanceTime( 8,
                           TimeUnit.SECONDS );
        ep.insert( new StockTick( 2, "B", 10, clock.getCurrentTime() ) );

        clock.advanceTime( 8,
                           TimeUnit.SECONDS );
        ep.insert( new StockTick( 3, "B", 10, clock.getCurrentTime() ) );

        clock.advanceTime( 8,
                           TimeUnit.SECONDS );
        int rules = ksession.fireAllRules();
//        assertEquals( 2,
//                      rules );

//        assertEquals( 1, list.size() );
//        StockTick[] stocks = ( StockTick[] ) list.get(0);
//        assertSame( tick4, stocks[0]);
//        assertSame( tick2, stocks[1]);
    }

    @Test(timeout=10000)
    public void testTemporalOperatorsInfinity() throws Exception {
        // read in the source
        final RuleBaseConfiguration kbconf = new RuleBaseConfiguration();
        kbconf.setEventProcessingMode( EventProcessingOption.STREAM );
        KnowledgeBase kbase = loadKnowledgeBase( kbconf, "test_CEP_TemporalOperators3.drl" );

        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( sconf,
                                                                               null );
        List list = new ArrayList();
        ksession.setGlobal("list", list);

        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();

        EntryPoint ep = ksession.getEntryPoint( "X" );

        clock.advanceTime( 1000, TimeUnit.SECONDS );

        int rules = 0;
        ep.insert( new StockTick( 1, "A", 10, clock.getCurrentTime() ) );
        clock.advanceTime( 8, TimeUnit.SECONDS );
        //int rules = ksession.fireAllRules();
        System.out.println( list );

        ep.insert( new StockTick( 2, "B", 10, clock.getCurrentTime() ) );
        clock.advanceTime( 8, TimeUnit.SECONDS );
        //rules = ksession.fireAllRules();
        System.out.println( list );

        ep.insert( new StockTick( 3, "B", 10, clock.getCurrentTime() ) );
        clock.advanceTime( 8, TimeUnit.SECONDS );
        rules = ksession.fireAllRules();
        System.out.println( list );

        assertEquals( 3,
                      rules );
    }

    @Test (timeout=10000)
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
                     "rule \"ab\" \n" +
                     "when\n" +
                     "    A( $a : id ) over window:length( 1 )\n" +
                     "    B( $b : id ) over window:length( 1 )\n" +
                     "then\n" +
                     "    System.out.println(\"AB: ( \"+$a+\", \"+$b+\" )\");\n" +
                     "end\n" +
                     "rule \"ba\" salience 10\n" +
                     "when\n" +
                     "    B( $b : id ) over window:length( 1 )\n" +
                     "    A( $a : id ) over window:length( 1 )\n" +
                     "then\n" +
                     "    System.out.println(\"BA: ( \"+$b+\", \"+$a+\" )\");\n" +
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

        if ( phreak == RuleEngineOption.PHREAK ) {
            // first rule
            act = values.get( 1 ).getMatch();
            assertThat( act.getRule().getName(),
                        is( "ba" ) );
            assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                        is( 2 ) );
            assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                        is( 1 ) );

            // second rule
            act = values.get( 2 ).getMatch();
            assertThat( act.getRule().getName(),
                        is( "ba" ) );
            assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                        is( 3 ) );
            assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                        is( 1 ) );

            // third rule
            act = values.get( 3 ).getMatch();
            assertThat( act.getRule().getName(),
                        is( "ba" ) );
            assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                        is( 3 ) );
            assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                        is( 2 ) );

            // fourth rule
            act = values.get( 4 ).getMatch();
            assertThat( act.getRule().getName(),
                        is( "ab" ) );
            assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                        is( 2 ) );
            assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                        is( 1 ) );

            // fifth rule
            act = values.get( 5 ).getMatch();
            assertThat( act.getRule().getName(),
                        is( "ab" ) );
            assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                        is( 3 ) );
            assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                        is( 1 ) );

            // sixth rule
            act = values.get( 6 ).getMatch();
            assertThat( act.getRule().getName(),
                        is( "ab" ) );
            assertThat( ((Number) act.getDeclarationValue( "$a" )).intValue(),
                        is( 3 ) );
            assertThat( ((Number) act.getDeclarationValue( "$b" )).intValue(),
                        is( 2 ) );
        } else {
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


    }

    @Test(timeout=10000)
    public void testCloudModeExpiration() throws IOException,
                                            ClassNotFoundException,
                                         InstantiationException,
                                         IllegalAccessException,
                                         InterruptedException {
        String str = "package org.drools.cloud\n" +
                     "import org.drools.compiler.*\n" +
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

        EntryPoint ep = ksession.getEntryPoint( "X" );

        ep.insert( new StockTick( 1,
                                  "RHT",
                                  10,
                                  1000 ) );
        int rulesFired = ksession.fireAllRules();
        assertEquals( 0,
                      rulesFired );

        org.kie.api.definition.type.FactType event = kbase.getFactType( "org.drools.cloud",
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

    @Test(timeout=10000)
    public void testSalienceWithEventsPseudoClock() throws IOException,
                                                   ClassNotFoundException {
        String str = "package org.drools.compiler\n" +
                     "import " + StockTick.class.getName() + "\n" +
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

    @Test(timeout=10000)
    public void testSalienceWithEventsRealtimeClock() throws IOException,
                                                     ClassNotFoundException, InterruptedException {
        String str = "package org.drools.compiler\n" +
                     "import " + StockTick.class.getName() + "\n" +
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

    @Test(timeout=10000)
    public void testExpireEventOnEndTimestamp() throws Exception {
        // DROOLS-40
        String str =
                "package org.drools.compiler;\n" +
                "\n" +
                "import org.drools.compiler.StockTick;\n" +
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

        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = loadKnowledgeBaseFromString(config, str);

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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

    @Test(timeout=10000)
    public void testEventExpirationDuringAccumulate() throws Exception {
        if ( phreak == RuleEngineOption.RETEOO ) {
            return; // this test is failing for Rete
        }
        // DROOLS-70
        String str =
                "package org.drools.integrationtests\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "declare Stock\n" +
                "    @role( event )\n" +
                "    @expires( 1s ) // setting to a large value causes the test to pass\n" +
                "    name : String\n" +
                "    value : Double\n" +
                "end\n" +
                "\n" +
                "rule \"collect events\"\n" +
                "when\n" +
                "    stocks := List()\n" +
                "        from accumulate( $zeroStock : Stock( value == 0.0 )\n" +
                "                         collectList( $zeroStock ) )\n" +
                "then\n" +
                "    // empty consequence\n" +
                "end";

        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = loadKnowledgeBaseFromString(config, str);

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(conf, null);

        final StockFactory stockFactory = new StockFactory(kbase);

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future sessionFuture = executor.submit(new Runnable() {

            @Override
            public void run() {
                ksession.fireUntilHalt();
            }
        });

        try {
            for (int iteration = 0; iteration < 100; iteration++) {
                this.populateSessionWithStocks(ksession, stockFactory);
            }
            // let the engine finish its job
            Thread.sleep(2000);

        } finally {
            ksession.halt();
            // not to swallow possible exception
            sessionFuture.get();
        }
    }

    private void populateSessionWithStocks(StatefulKnowledgeSession ksession, StockFactory stockFactory) {
        final SessionPseudoClock clock = ksession.getSessionClock();

        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(stockFactory.createStock("ST1", 0d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(stockFactory.createStock("ST2", 1d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(stockFactory.createStock("ST3", 0d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(stockFactory.createStock("ST4", 0d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(stockFactory.createStock("ST5", 0d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(stockFactory.createStock("ST6", 1d));
    }

    /**
     * Factory creating events used in the test.
     */
    private static class StockFactory {

        private static final String DRL_PACKAGE_NAME = "org.drools.integrationtests";

        private static final String DRL_FACT_NAME = "Stock";

        private final KnowledgeBase kbase;

        public StockFactory(final KnowledgeBase kbase) {
            this.kbase = kbase;
        }

        public Object createStock(final String name, final Double value) {
            try {
                return this.createDRLStock(name, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to create Stock instance defined in DRL", e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Unable to create Stock instance defined in DRL", e);
            }
        }

        private Object createDRLStock(final String name, final Double value)
                throws IllegalAccessException, InstantiationException {

            final FactType stockType = kbase.getFactType(DRL_PACKAGE_NAME, DRL_FACT_NAME);

            final Object stock = stockType.newInstance();
            stockType.set(stock, "name", name);
            stockType.set(stock, "value", value);

            return stock;
        }
    }

    @Test(timeout=10000)
    public void testEventExpirationInSlidingWindow() throws Exception {
        // DROOLS-70
        String str =
                "package org.drools.integrationtests\n" +
                "\n" +
                "declare Stock\n" +
                "    @role( event )\n" +
                "    name : String\n" +
                "    value : Double\n" +
                "end\n" +
                "\n" +
                "rule \"collect time window contents\"\n" +
                "when\n" +
                "    Stock( value == 0.0 ) over window:time(2s)\n" +
                "then\n" +
                "    // empty consequence\n" +
                "end";

        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = loadKnowledgeBaseFromString(config, str);

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(conf, null);

        final StockFactory stockFactory = new StockFactory(kbase);

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future sessionFuture = executor.submit(new Runnable() {
            @Override
            public void run() {
                ksession.fireUntilHalt();
            }
        });

        try {
            for (int iteration = 0; iteration < 100; iteration++) {
                this.populateSessionWithStocks(ksession, stockFactory);
            }
            // let the engine finish its job
            Thread.sleep(5000);

        } finally {
            ksession.halt();
            // not to swallow possible exception
            sessionFuture.get();
        }
    }

    @Test(timeout=10000)
    public void testSlidingWindowsAccumulateExternalJoin() throws Exception {
        // DROOLS-106
        // The logic may not be optimal, but was used to detect a WM corruption
        String str =
                "package testing2;\n" +
                "\n" +
                "import java.util.*;\n" +
                "import org.drools.compiler.StockTick;\n" +
                "" +
                "global List list;\n" +
                "" +
                "declare StockTick\n" +
                " @role( event )\n" +
                " @duration( duration )\n" +
                "end\n" +
                "\n" +
                "rule test\n" +
                "when\n" +
                " $primary : StockTick( $name : company ) over window:length(1)\n" +
                " accumulate ( " +
                " $tick : StockTick( company == $name ) , " +
                " $num : count( $tick ) )\n" +

                "then\n" +
                " System.out.println(\"Found name: \" + $primary + \" with \" +$num );\n" +
                " list.add( $num.intValue() ); \n" +
                "end\n" +
                "";

        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = loadKnowledgeBaseFromString(config, str);

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(conf, null);

        int seq = 0;
        List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.insert( new StockTick( seq++, "AAA", 10.0, 10L ) );
        ksession.fireAllRules();
        assertEquals(list, Arrays.asList(1));

        ksession.insert(new StockTick(seq++, "AAA", 15.0, 10L));
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
        assertEquals(list, Arrays.asList(1, 2, 1, 1, 3));

        // NPE Here
        ksession.fireAllRules();

    }

    @Test (timeout=10000)
    public void testTimeAndLengthWindowConflict() throws Exception {
        // JBRULES-3671
        String drl = "package org.drools.compiler;\n" +
                     "\n" +
                     "import java.util.List\n" +
                     "\n" +
                     "global List timeResults;\n" +
                     "global List lengthResults;\n" +
                     "\n" +
                     "declare OrderEvent\n" +
                     " @role( event )\n" +
                     "end\n" +
                     "\n" +
                     "rule \"collect with time window\"\n" +
                     "when\n" +
                     " $list : List( empty == false ) from collect(\n" +
                     " $o : OrderEvent() over window:time(30s) )\n" +
                     "then\n" +
                     " timeResults.add( $list.size() );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"collect with length window\"\n" +
                     "when\n" +
                     " accumulate (\n" +
                     " $o : OrderEvent( $tot : total ) over window:length(3)," +
                     " $avg : average( $tot ) )\n" +
                     "then\n" +
                     " lengthResults.add( $avg );\n" +
                     "end\n";

        final KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBaseFromString( kbconf, drl );

        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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
        // assertTrue( lengthResults.contains( 45 ) );

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

    @Test (timeout=10000)
    public void testTimeWindowWithPastEvents() throws Exception {
        // JBRULES-2258 
        String drl = "package org.drools.compiler;\n" +
                     "\n" +
                     "import java.util.List\n" +
                     "\n" +
                     "global List timeResults;\n" +
                     "\n" +
                     "declare StockTick\n" +
                     " @role( event )\n" +
                     " @timestamp( time ) \n" +
                     "end\n" +
                     "\n" +
                     "rule \"collect with time window\"\n" +
                     "when\n" +
                     " accumulate(\n" +
                     " $o : StockTick() over window:time(10ms)," +
                     " $tot : count( $o );" +
                     " $tot > 0 )\n" +
                     "then\n" +
                     " System.out.println( $tot ); \n" +
                     " timeResults.add( $tot );\n" +
                     "end\n";

        final KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBaseFromString( kbconf, drl );

        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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
        System.out.println(timeResults);
        assertTrue(timeResults.isEmpty());

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
    @Ignore
    public void testLeakingActivationsWithDetachedExpiredNonCancelling() throws Exception {
        // JBRULES-3558 - DROOLS 311
        // TODO: it is still possible to get multiple insertions of the Recording object
        // if you set the @expires of Motion to 1ms, maybe because the event expires too soon
        String drl = "package org.drools;\n" +
                     "\n" +
                     "import java.util.List\n" +
                     "\n" +
                     "global List list; \n" +
                     "" +
                     "declare Motion\n" +
                     "    @role( event )\n" +
                     "    @expires( 10ms )\n" +
                     "    @timestamp( timestamp )\n" +
                     "    timestamp : long\n" +
                     "end\n" +
                     "\n" +
                     "declare Recording\n" +
                     "end\n" +
                     "\n" +
                     "" +
                     "rule Init salience 1000 when\n" +
                     "    $l : Long() \n" +
                     "then\n" +
                     "    System.out.println( \" Insert motion \" + $l );\n" +
                     "    insert( new Motion( $l ) ); \n" +
                     "end\n" +
                     "" +
                     "rule \"StartRecording\" when\n" +
                     "   $mot : Motion()\n" +
                     "   not Recording()\n" +
                     " then\n" +
                     "   list.add( $mot ); \n " +
                     "   System.out.println(\"Recording started\");\n" +
                     "   insert(new Recording());\n" +
                     "end\n";

        final KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBaseFromString( kbconf, drl );
        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( ClockType.REALTIME_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, ksconf);

        List<Number> list = new ArrayList<Number>();

        ksession.setGlobal( "list", list );

        ksession.insert( new Long( 1000 ) );
        ksession.insert( new Long( 1001 ) );
        ksession.insert( new Long( 1002 ) );

        Thread.sleep(1000);

        ksession.fireAllRules();
        assertEquals( 1, list.size() );

    }


    @Test(timeout=10000)
    public void testTwoWindowsInsideCEAndOut() throws Exception {
        String drl = "package org.drools.compiler;\n" +
                     "\n" +
                     "import java.util.List\n" +
                     "\n" +
                     "global List timeResults;\n" +
                     "\n" +
                     "declare " + OrderEvent.class.getCanonicalName() + "\n" +
                     " @role( event )\n" +
                     "end\n" +
                     "\n" +
                     "rule \"r1\"\n" +
                     "when\n" +
                     "    $o1 : OrderEvent() over window:length(3) \n" +
                     "        accumulate(  $o2 : OrderEvent() over window:length(3);\n" +
                     "                     $avg : average( $o2.getTotal() ) )\n" +
                     "then\n" +
                     "     System.out.println( $o1.getTotal() + \":\" + $avg ); \n" +
                     "end\n";

        final KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBaseFromString( kbconf, drl );

        KieSessionConfiguration sconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession wm = createKnowledgeSession( kbase, sconf );



        wm.insert( new OrderEvent( "1", "customer A", 70 ) );
        wm.insert( new OrderEvent( "2", "customer A", 60 ) );
        wm.insert( new OrderEvent( "3", "customer A", 50 ) );
        wm.insert( new OrderEvent( "4", "customer A", 40 ) );
        wm.insert( new OrderEvent( "5", "customer A", 30 ) );
        wm.insert( new OrderEvent( "6", "customer A", 20 ) );
        wm.insert( new OrderEvent( "7", "customer A", 10 ) );
        wm.fireAllRules();
    }

    @Test
    public void testUpdateEventThroughEntryPoint() throws Exception {
        String drl = "import org.drools.compiler.integrationtests.CepEspTest.TestEvent\n" +
                     "\n" +
                     "declare TestEvent\n" +
                     "    @role( event )\n" +
                     "    @expires( 4s )\n" +
                     "end\n" +
                     "\n" +
                     "rule \"TestEventReceived\"\n" +
                     "    no-loop\n" +
                     "    when\n" +
                     "        $event : TestEvent ( name != null ) over window:time( 4s ) from entry-point EventStream\n" +
                     "    then\n" +
                     "        // insert( new Message( $event.getValue().toString() ) );\n" +
                     "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        KieModuleModel kieModule = ks.newKieModuleModel();
        kieModule.newKieBaseModel("KBase")
                 .setDefault(true)
                 .setEventProcessingMode(EventProcessingOption.STREAM)
                 .newKieSessionModel("KSession")
                 .setDefault(true);

        kfs.writeKModuleXML(kieModule.toXML());
        kfs.write("src/main/resources/lifecycle.drl", drl);

        KieBuilder builder = ks.newKieBuilder(kfs).buildAll();
        assertEquals(0, builder.getResults().getMessages().size());

        KieSession kieSession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();

        EntryPoint entryPoint = kieSession.getEntryPoint("EventStream");

        TestEvent event = new TestEvent("testEvent1");
        FactHandle handle = entryPoint.insert(event);

        TestEvent event2 = new TestEvent("testEvent2");
        entryPoint.update(handle, event2);

        // make sure the event is in the entry-point
        assertFalse(entryPoint.getObjects().contains(event));
        assertTrue(entryPoint.getObjects().contains(event2));
        assertEquals(entryPoint.getObject(handle), event2);

        kieSession.dispose();
    }

    public static class TestEvent implements Serializable {

        private final String name;

        public TestEvent(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return String.format("TestEvent[name=%s]", name);
        }
    }

    @Test
    public void testThrowsWhenCreatingKieBaseUsingWindowsInCloudMode() {
        String drl =
            "declare TestEvent\n" +
            "    @role( event )\n" +
            "    name : String\n" +
            "end\n" +
            "\n" +
            "rule R when\n" +
            "        TestEvent ( name == \"EventA\" ) over window:time( 1s ) from entry-point EventStream\n" +
            "    then\n" +
            "        // consequence\n" +
            "end\n";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kieModule = ks.newKieModuleModel();
        KieBaseModel defaultBase = kieModule.newKieBaseModel("KBase")
                                            .setDefault(true)
                                            .addPackage("*")
                                            .setEventProcessingMode(EventProcessingOption.CLOUD);
        defaultBase.newKieSessionModel("KSession")
                   .setClockType(ClockTypeOption.get("pseudo"))
                   .setDefault(true);

        KieFileSystem kfs = ks.newKieFileSystem().write("src/main/resources/r1.drl", drl);

        kfs.writeKModuleXML(kieModule.toXML());
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();

        KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        try {
            kieContainer.getKieBase("KBase");
            fail("Should throw a RuntimeException because the CLOUD kbase is trying to use features only available in STREAM mode");
        } catch (Exception e) { }
    }

    @Test
    public void testStreamModeWithSubnetwork() {
        // BZ-1009348

        String drl = "package org.drools.compiler.integrationtests\n" +
                     "\n" +
                     "declare Event\n" +
                     "  @role(event)\n" +
                     "  name : String\n" +
                     "end\n" +
                     "\n" +
                     "global java.util.List list\n" +
                     "\n" +
                     "rule \"firstRule\"\n" +
                     "\n" +
                     "    when\n" +
                     "        not (\n" +
                     "            $e : Event() over window:length(3)\n" +
                     "            and Event( this == $e ) // test pass when you comment this line\n" +
                     "        )\n" +
                     "    then\n" +
                     "        list.add(\"firstRule\");\n" +
                     "\n" +
                     "end";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/notinfusion.drl", drl);

        KieModuleModel kmoduleModel = ks.newKieModuleModel();
        kmoduleModel.newKieBaseModel("KieBase")
                    .addPackage("*")
                    .setDefault(true)
                    .setEventProcessingMode(EventProcessingOption.STREAM)
                    .newKieSessionModel("KieSession")
                    .setDefault(true);

        kfs.writeKModuleXML(kmoduleModel.toXML());

        KieBuilder kbuilder = ks.newKieBuilder(kfs).buildAll();

        List<org.kie.api.builder.Message> res = kbuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR);

        assertEquals(res.toString(), 0, res.size());

        KieSession ksession = ks.newKieContainer(kbuilder.getKieModule().getReleaseId()).newKieSession();

        ArrayList<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        assertEquals(1, list.size());
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
    public void testEventTimestamp() {
        // DROOLS-268
        String drl = "\n" +
                     "import org.drools.compiler.integrationtests.CepEspTest.Event; \n" +
                     "global java.util.List list; \n" +
                     "global org.drools.core.time.SessionPseudoClock clock; \n" +
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
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

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
                     "import org.drools.compiler.integrationtests.CepEspTest.Event; \n" +
                     "global java.util.List list; \n" +
                     "global org.drools.core.time.SessionPseudoClock clock; \n" +
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
                     " $event0 : Event( type == 0 )\n" +
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
        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        //init stateful knowledge session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( sessionConfig, null );
        ArrayList list = new ArrayList( );
        ksession.setGlobal( "list", list );

        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        ksession.setGlobal( "clock", clock );

        ksession.insert( new Event( 0, 0, clock.getCurrentTime() ) );
        clock.advanceTime(100, TimeUnit.MILLISECONDS);

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

        assertEquals( 1300, time );

        ksession.dispose();
    }

    @Test
    public void testModifyInStreamMode() {
        // BZ-1012933
        String drl =
                "import org.drools.compiler.integrationtests.CepEspTest.SimpleFact;\n" +
                "global java.util.List list;\n" +
                "declare SimpleFact\n" +
                "    @role( event )\n" +
                "end\n" +
                "\n" +
                "rule \"MyRule\"\n" +
                "when\n" +
                "    $f : SimpleFact( status == \"NOK\" )\n" +
                "then\n" +
                "    list.add(\"Firing\");" +
                "    $f.setStatus(\"OK\");\n" +
                "    update ($f);\n" +
                "end\n";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( EventProcessingOption.STREAM );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(kconf, drl);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        SimpleFact fact = new SimpleFact("id1");
        ksession.insert(fact);
        ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals("OK", fact.getStatus());
    }

    @Test
    public void testCollectAfterRetract() {
        // BZ-1015109
        String drl =
                "import org.drools.compiler.integrationtests.CepEspTest.SimpleFact;\n" +
                "import java.util.List;\n" +
                "global List list;\n" +
                "\n" +
                "declare SimpleFact\n" +
                "    @role( event )\n" +
                "end\n" +
                "\n" +
                "rule \"Retract facts if 2 or more\" salience 1000\n" +
                "when\n" +
                "    $facts : List( size >= 2 ) from collect( SimpleFact() )\n" +
                "then\n" +
                "    for (Object f: new java.util.LinkedList($facts)) {\n" +
                "        System.out.println(\"Retracting \"+f);\n" +
                "        retract(f);\n" +
                "    }\n" +
                "end\n" +
                "\n" +
                "rule \"Still facts in WM\"\n" +
                "when\n" +
                "    $facts : List( size != 0 ) from collect( SimpleFact() )\n" +
                "then\n" +
                "    list.add( $facts.size() );\n" +
                "end\n";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( EventProcessingOption.STREAM );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(kconf, drl);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.insert(new SimpleFact("id1"));
        ksession.insert(new SimpleFact("id2"));
        ksession.insert(new SimpleFact("id3"));

        ksession.fireAllRules();
        assertEquals(0, list.size());
    }

    @Test
    public void testCollectAfterUpdate() {
        // DROOLS-295
        String drl =
                "import org.drools.compiler.integrationtests.CepEspTest.SimpleFact;\n" +
                "import java.util.List;\n" +
                "\n" +
                "declare SimpleFact\n" +
                "    @role( event )\n" +
                "end\n" +
                "\n" +
                "rule \"Rule1\"\n" +
                "    when\n" +
                "        event : SimpleFact( status == \"NOK\" )\n" +
                "        list: List(size < 4) from collect( SimpleFact(this != event, status==\"1\") )\n" +
                "    then\n" +
                "        event.setStatus(\"1\");\n" +
                "        update(event);\n" +
                "    end\n" +
                "\n" +
                "rule \"Rule2\"\n" +
                "    when\n" +
                "        event : SimpleFact( status == \"NOK\" )\n" +
                "        list: List(size >= 4) from collect( SimpleFact(this != event, status==\"1\") )\n" +
                "    then\n" +
                "        for (Object ev2: new java.util.LinkedList(list)) retract(ev2);\n" +
                "        event.setStatus(\"2\");\n" +
                "        update(event);\n" +
                "    end\n";

        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( EventProcessingOption.STREAM );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(kconf, drl);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        for (int i = 0; i < 4; i++) {
            ksession.insert(new SimpleFact("id" + i));
        }
        ksession.fireAllRules();
        assertEquals("all events should be in WM", 4, ksession.getFactCount());

        ksession.insert(new SimpleFact("last"));
        ksession.fireAllRules();
        assertEquals("only one event should be still in WM", 1, ksession.getFactCount());
    }

    public static class SimpleFact {

        private String status = "NOK";
        private final String id;

        public SimpleFact(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(final String s) {
            status = s;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName()+" (id="+id+", status=" + status+")";
        }
    }

    @Test
    public void testTemporalQuery() {
        // BZ-967441
        String drl =
                 "package org.drools.compiler.integrationtests;\n" +
                 "\n" +
                 "import org.drools.compiler.integrationtests.CepEspTest.TestEvent;\n" +
                 "\n" +
                 "declare TestEvent\n" +
                 "    @role( event )\n" +
                 "end\n" +
                 "\n" +
                 "query EventsBeforeNineSeconds\n" +
                 "   $event : TestEvent() from entry-point EStream\n" +
                 "   $result : TestEvent ( this after [0s, 9s] $event) from entry-point EventStream\n" +
                 "end\n";

        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/querytest.drl", drl);

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();

        KieBase kbase = KieServices.Factory.get()
                                   .newKieContainer(kbuilder.getKieModule().getReleaseId())
                                   .getKieBase();

        KieSessionConfiguration ksconfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconfig.setOption(ClockTypeOption.get("pseudo"));

        KieSession ksession = kbase.newKieSession(ksconfig, null);

        SessionPseudoClock clock = ksession.getSessionClock();

        EntryPoint ePoint = ksession.getEntryPoint("EStream");
        EntryPoint entryPoint = ksession.getEntryPoint("EventStream");

        ePoint.insert(new TestEvent("zero"));
        entryPoint.insert(new TestEvent("one"));
        clock.advanceTime( 10, TimeUnit.SECONDS );
        entryPoint.insert(new TestEvent("two"));
        clock.advanceTime( 10, TimeUnit.SECONDS );
        entryPoint.insert(new TestEvent("three"));
        QueryResults results = ksession.getQueryResults("EventsBeforeNineSeconds");
        assertEquals(1, results.size());

        ksession.dispose();
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
    @Ignore
    public void testExpirationAtHighRates() throws InterruptedException {
        // DROOLS-130
        String drl = "package droolsfusioneval\n" +
                     "" +
                     "global java.util.List list; \n" +
                     "" +
                     "import org.drools.compiler.integrationtests.CepEspTest.ProbeEvent;\n" +
                     "import org.drools.compiler.integrationtests.CepEspTest.ProbeCounter;\n" +
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

        KieBaseConfiguration kbconfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
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
        EntryPoint ep01 = session.getEntryPoint("ep01");


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

            Thread.sleep( 2500 );
        } catch ( Throwable t ) {
            fail( t.getMessage() );
        }

        assertEquals( eventLimit, myTotal );
        assertEquals( eventLimit, list.size() );
        assertEquals( 0, session.getEntryPoint( "ep01" ).getObjects().size() );
    }


    @Test
    public void AfterOperatorInCEPQueryTest() {

        String drl = "package org.drools;\n" +
                     "import org.drools.compiler.StockTick; \n" +
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

        final KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBaseFromString( kbconf,  drl );

        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, ksconf);

        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        EntryPoint ePoint = ksession.getEntryPoint( "EStream" );
        EntryPoint entryPoint = ksession.getEntryPoint( "EventStream" );


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
                     "import org.drools.compiler.StockTick; \n" +
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
        final KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        kbconf.setOption( RuleEngineOption.PHREAK );

        KieSessionConfiguration knowledgeSessionConfiguration = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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
                     "import org.drools.compiler.StockTick; \n" +
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
        final KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        kbconf.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kb = loadKnowledgeBaseFromString( kbconf, drl );
        StatefulKnowledgeSession ks = kb.newStatefulKnowledgeSession( );

        ks.insert( new StockTick( 2, "BBB", 1.0, 0 ) );
        Thread.sleep( 1500 );

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

        System.out.print( list );
        assertEquals( 1, list.size() );
        assertEquals( Arrays.asList( 3L ), list );


    }

    @Test
    public void testDuplicateFiring1() throws InterruptedException {

        String drl = "package org.test;\n" +
                     "import org.drools.compiler.StockTick;\n " +
                     "" +
                     "global java.util.List list \n" +
                     "" +
                     "declare StockTick @role(event) end \n" +
                     "" +
                     "rule \"slidingTimeCount\"\n" +
                     "when\n" +
                     "  accumulate ( $e: StockTick() over window:time(300ms) from entry-point SensorEventStream, " +
                     "              $n : count( $e );" +
                     "              $n > 0 )\n" +
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
        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        baseConfig.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(baseConfig);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //init session clock
        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get("pseudo") );
        //init stateful knowledge session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(sessionConfig, null);
        SessionPseudoClock clock = ksession.getSessionClock();
        ArrayList list = new ArrayList(  );
        ksession.setGlobal( "list", list );

        //entry point for sensor events
        EntryPoint sensorEventStream = ksession.getEntryPoint( "SensorEventStream" );

        ksession.insert( "Go" );
        System.out.println("1. fireAllRules()");

        //insert events
        for(int i=2;i<8;i++){
            StockTick event = new StockTick( (i-1), "XXX", 1.0, 0 );
            sensorEventStream.insert( event );

            System.out.println(i + ". fireAllRules()");
            ksession.fireAllRules();

            clock.advanceTime(105, TimeUnit.MILLISECONDS);
        }

        //let thread sleep for another 1m to see if dereffered rules fire (timers, (not) after rules)
        clock.advanceTime(100*40*1, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();

        assertEquals( Arrays.asList( 1L, 2L, 3L, 3L, 3L, 3L, -1 ), list );

        ksession.dispose();
    }

    @Test
    public void testDuplicateFiring2() throws InterruptedException {

        String drl = "package org.test;\n" +
                     "import org.drools.compiler.StockTick;\n " +
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
        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.CLOUD );
        baseConfig.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(baseConfig);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //init session clock
        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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
                     "import org.drools.compiler.StockTick;\n " +
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
        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( RuleEngineOption.PHREAK );
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get("pseudo") );
        //init stateful knowledge session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( sessionConfig, null );
        SessionPseudoClock clock = ksession.getSessionClock();
        ArrayList list = new ArrayList(  );
        ksession.setGlobal( "list", list );

        long now = 0;

        StockTick event1 = new StockTick( 1, "XXX", 1.0, now );
        StockTick event2 = new StockTick( 2, "XXX", 1.0, now + 240 );
        StockTick event3 = new StockTick( 2, "XXX", 1.0, now + 380 );
        StockTick event4 = new StockTick( 2, "XXX", 1.0, now + 500 );

        ksession.insert( event1 );
        ksession.insert( event2 );
        ksession.insert( event3 );
        ksession.insert( event4 );

        clock.advanceTime( 220, TimeUnit.MILLISECONDS );

        ksession.fireAllRules();

        clock.advanceTime( 400, TimeUnit.MILLISECONDS );

        ksession.fireAllRules();

        assertEquals( Arrays.asList( 3L, 1L ), list );
    }


    public static class MyEvent {
        private long timestamp;
        public MyEvent( long timestamp ) { this.timestamp = timestamp; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp( long timestamp ) { this.timestamp = timestamp; }
        public String toString() { return "MyEvent{" + "timestamp=" + timestamp + '}';  }
    }

    @Test
    public void testEventStreamWithEPsAndDefaultPseudo() throws InterruptedException {
        //DROOLS-286
        String drl = "\n" +
                     "import java.util.*;\n" +
                     "import org.drools.compiler.integrationtests.CepEspTest.MyEvent; \n" +
                     "" +
                     "declare MyEvent\n" +
                     "    @role(event)\n" +
                     "    @timestamp(timestamp)\n" +
                     "end\n" +
                     "\n" +
                     "" +
                     "global java.util.List list; \n" +
                     "" +
                     "rule \"over 0.3s\"\n" +
                     "salience 1 \n" +
                     "    when\n" +
                     "        $list: List() from collect(MyEvent() over window:time(300ms))\n" +
                     "    then\n" +
                     "        System.out.println(\"Rule: with in 0.3s --> \" + $list);\n" +
                     "        list.add( $list.size() ); \n" +
                     "end\n" +
                     "\n" +
                     "rule \"over 1s\"\n" +
                     "salience 2 \n" +
                     "    when\n" +
                     "        $list: List() from collect(MyEvent() over window:time(1s))\n" +
                     "    then\n" +
                     "        System.out.println(\"Rule: with in 1s --> \" + $list);\n" +
                     "        list.add( $list.size() ); \n" +
                     "end\n" +
                     "\n" +
                     "rule \"over 3s\"\n" +
                     "salience 3 \n" +
                     "    when\n" +
                     "        $list: List() from collect(MyEvent() over window:time(3s))\n" +
                     "    then\n" +
                     "        System.out.println(\"Rule: with in 3s --> \" + $list);\n" +
                     "        list.add( $list.size() ); \n" +
                     "end\n" +
                     "\n" +
                     "rule \"over 0.3s ep\"\n" +
                     "salience 4 \n" +
                     "    when\n" +
                     "        $list: List() from collect(MyEvent() over window:time(300ms) from entry-point \"stream\")\n" +
                     "    then\n" +
                     "        System.out.println(\"Rule: with in 0.3s use ep --> \" + $list);\n" +
                     "        list.add( $list.size() ); \n" +
                     "end\n" +
                     "\n" +
                     "rule \"over 1s ep\"\n" +
                     "salience 5 \n" +
                     "    when\n" +
                     "        $list: List() from collect(MyEvent() over window:time(1s) from entry-point \"stream\")\n" +
                     "    then\n" +
                     "        System.out.println(\"Rule: with in 1s use ep --> \" + $list);\n" +
                     "        list.add( $list.size() ); \n" +
                     "end\n" +
                     "\n" +
                     "rule \"over 3s ep\"\n" +
                     "salience 6 \n" +
                     "    when\n" +
                     "        $list: List() from collect(MyEvent() over window:time(3s) from entry-point \"stream\")\n" +
                     "    then\n" +
                     "        System.out.println(\"Rule: with in 3s use ep --> \" + $list);\n" +
                     "        list.add( $list.size() ); \n" +
                     "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        baseConfig.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        //init stateful knowledge session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( sessionConfig, null );
        SessionPseudoClock clock = ksession.getSessionClock();

        ArrayList list = new ArrayList( );
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();
        list.clear();

        for ( int j = 0; j < 5; j++ ) {
            clock.advanceTime( 500, TimeUnit.MILLISECONDS );
            ksession.insert( new MyEvent( clock.getCurrentTime() ) );
            ksession.getEntryPoint( "stream" ).insert( new MyEvent( clock.getCurrentTime() ) );
            clock.advanceTime( 500, TimeUnit.MILLISECONDS );
            ksession.fireAllRules();

            System.out.println( list );
            switch ( j ) {
                case 0 : assertEquals( Arrays.asList( 1, 1, 0, 1, 1, 0 ), list );
                    break;
                case 1 : assertEquals( Arrays.asList( 2, 1, 0, 2, 1, 0 ), list );
                    break;
                case 2 :
                case 3 :
                case 4 : assertEquals( Arrays.asList( 3, 1, 0, 3, 1, 0 ), list );
                    break;
                default: fail();
            }
            list.clear();

            System.out.println( "-------------- SLEEP ------------" );
        }

        ksession.dispose();
    }

    @Test
    public void testExpirationOnModification() throws InterruptedException {
        //DROOLS-374
        String drl = "\n" +
                     "import java.util.*;\n" +
                     "global List list; " +

                     "declare SomeEvent\n" +
                     "    @role( event )\n" +
                     "    @expires( 200ms )\n" +
                     "  done : boolean = false \n" +
                     "end\n" +

                     "rule Count \n" +
                     "  no-loop \n " +
                     "    when " +
                     "        $ev : SomeEvent( done == false  ) " +
                     "        accumulate ( SomeEvent() over window:time( 10s )," +
                     "                     $num : count( 1 ) )\n" +
                     "    then\n" +
                            // ok, modifies should never happen. But they are allowed, and if they do happen, the behaviour should be consistent
                     "        modify ( $ev ) { setDone( true ); } " +
                     "        list.add( $num ); \n" +
                     "end\n" +

                     "rule Init \n" +
                     "    when\n" +
                     "      $s : String() " +
                     "    then\n" +
                     "      retract( $s ); " +
                     "      insert( new SomeEvent() ); " +
                     "end\n" +

                     "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        baseConfig.setOption( RuleEngineOption.PHREAK );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        //init stateful knowledge session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( sessionConfig, null );
        SessionPseudoClock clock = ksession.getSessionClock();

        ArrayList list = new ArrayList( );
        ksession.setGlobal( "list", list );

        ksession.insert( "go" );
        ksession.fireAllRules();

        clock.advanceTime( 100, TimeUnit.MILLISECONDS );

        ksession.insert( "go" );
        ksession.fireAllRules();

        clock.advanceTime( 500, TimeUnit.MILLISECONDS );

        ksession.insert( "go" );
        ksession.fireAllRules();

        assertEquals( Arrays.asList( 1L, 2L, 1L ), list );

        ksession.dispose();
    }



    @Test
    public void testTemporalEvaluatorsWithEventsFromNode() throws InterruptedException {
        //DROOLS-421
        String drl = "\n" +
                     "import java.util.*; " +
                     "global List list; " +

                     "declare Delivery " +
                     "  @role( event ) @timestamp( effectiveDate ) " +
                     "  effectiveDate : Date " +
                     "  configs : List " +
                     "end " +

                     "declare Config " +
                     "  @role( event ) @timestamp( todate ) " +
                     "  todate : Date " +
                     "end " +

                     "rule Control " +
                     "when " +
                     "  $dpo: Delivery() " +
                     "  $gCfg: Config( todate == null || this after[ 0d ] $dpo ) from $dpo.configs " +
                     "then " +
                     "  list.add( 0 ); " +
                     "end " +

                     "rule Init " +
                     "when " +
                     "then " +
                     "  Delivery dpo = new Delivery( new Date(), new ArrayList() ); " +
                     "  Config gCfg = new Config(); " +
                     "      gCfg.setTodate( new Date( new Date().getTime() + 1000 ) ); " +
                     "  dpo.getConfigs().add( gCfg ); " +
                     "  " +
                     "  insert( dpo ); " +
                     "end " +

                     "";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ResourceFactory.newByteArrayResource( drl.getBytes() ).setTargetPath( "rules.drl" ) );

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder( kfs );
        kbuilder.buildAll();

        assertEquals( 0, kbuilder.getResults().getMessages().size() );

        KieSession ksession = ks.newKieContainer( kbuilder.getKieModule().getReleaseId() ).newKieSession();
        assertNotNull( ksession );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        ksession.dispose();

    }

    @Test
    public void testTemporalEvaluatorsUsingRawDateFields() throws InterruptedException {
        //DROOLS-421
        String drl = "\n" +
                     "import java.util.*; " +
                     "global List list; " +

                     "declare Delivery " +
                     "  effectiveDate : Date " +
                     "end " +

                     "declare Config " +
                     "  todate : Date " +
                     "end " +

                     "rule Control " +
                     "when " +
                     "  $dpo: Delivery( $eff : effectiveDate ) " +
                     "  $gCfg: Config( todate == null || todate after[ 0d ] $eff ) " +
                     "then " +
                     "  list.add( 0 ); " +
                     "end " +

                     "rule Init " +
                     "when " +
                     "then " +
                     "  Delivery dpo = new Delivery( new Date( 1000 ) ); " +
                     "  Config gCfg = new Config(); " +
                     "      gCfg.setTodate( new Date( 2000 ) ); " +
                     "  " +
                     "  insert( dpo ); " +
                     "  insert( gCfg ); " +
                     "end " +

                     "";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ResourceFactory.newByteArrayResource( drl.getBytes() ).setTargetPath( "rules.drl" ) );

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder( kfs );
        kbuilder.buildAll();

        assertEquals( 0, kbuilder.getResults().getMessages().size() );

        KieSession ksession = ks.newKieContainer( kbuilder.getKieModule().getReleaseId() ).newKieSession();
        assertNotNull( ksession );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        ksession.dispose();

    }


    @Test
    public void testTemporalEvaluatorsUsingRawDateFieldsFromFrom() throws InterruptedException {
        //DROOLS-421
        String drl = "\n" +
                     "import java.util.*; " +
                     "global List list; " +

                     "declare Delivery " +
                     "  effectiveDate : Date " +
                     "end " +

                     "declare Config " +
                     "  todate : Date " +
                     "end " +

                     "rule Control " +
                     "when " +
                     "  $dpo: Delivery( $eff : effectiveDate ) from new Delivery( new Date( 1000 ) ) " +
                     "  $gCfg: Config( todate == null || todate after[ 0d ] $eff ) from new Config( new Date( 2000 ) ) " +
                     "then " +
                     "  list.add( 0 ); " +
                     "end " +

                     "";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ResourceFactory.newByteArrayResource( drl.getBytes() ).setTargetPath( "rules.drl" ) );

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder( kfs );
        kbuilder.buildAll();

        assertEquals( 0, kbuilder.getResults().getMessages().size() );

        KieSession ksession = ks.newKieContainer( kbuilder.getKieModule().getReleaseId() ).newKieSession();
        assertNotNull( ksession );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        ksession.dispose();

    }

    @Test
    public void testTemporalEvaluatorsUsingSelfDates() throws InterruptedException {
        //DROOLS-421
        String drl = "\n" +
                     "import java.util.*; " +
                     "global List list; " +

                     "declare Delivery " +
                     "  thisDate : Date " +
                     "  thatDate : Date " +
                     "end " +

                     "rule Init when then insert( new Delivery( new Date( 1000 ), new Date( 200 ) ) ); end " +

                     "rule Control " +
                     "when " +
                     "  Delivery( thisDate == null || thisDate after[ 0d ] thatDate ) " +
                     "then " +
                     "  list.add( 0 ); " +
                     "end " +

                     "";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ResourceFactory.newByteArrayResource( drl.getBytes() ).setTargetPath( "rules.drl" ) );

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder( kfs );
        kbuilder.buildAll();

        assertEquals( 0, kbuilder.getResults().getMessages().size() );

        KieSession ksession = ks.newKieContainer( kbuilder.getKieModule().getReleaseId() ).newKieSession();
        assertNotNull( ksession );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        ksession.dispose();

    }

    @Test
    public void testEventOffsetExpirationOverflow() {
        // DROOLS-455
        String drl = "\n" +
                     "import java.util.*; " +
                     "" +
                     "declare LongLastingEvent \n" +
                     " @role( event )" +
                     " @timestamp( start ) " +
                     " @duration( duration ) " +
                     "      start : long " +
                     "      duration : long " +
                     "end \n" +
                     "" +
                     "rule Insert " +
                     "  when " +
                     "  then " +
                     "      insert( new LongLastingEvent( 100, Long.MAX_VALUE ) ); " +
                     "  end " +
                     " " +
                     " " +
                     "rule Collect \n" +
                     "when\n" +
                     " accumulate( $x: LongLastingEvent() over window:time(1h), $num : count($x) ) \n" +
                     "then " +
                     "end " +
                     "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        //init stateful knowledge session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( sessionConfig, null );

        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();

        // generate the event
        ksession.fireAllRules();

        // move on..
        clock.advanceTime( 10, TimeUnit.SECONDS );

        ksession.fireAllRules();

        // The event should still be there...

        assertEquals( 1, ksession.getObjects().size() );

        ksession.dispose();
    }

    @Test @Ignore("Cannot reproduce with pseudoclock and takes too long with system clock")
    public void testTimedRuleWithAccumulate() {
        // BZ-1083103
        String drl = "import " + SynthEvent.class.getCanonicalName() + "\n" +
                     "import java.util.Date\n" +
                     "\n" +
                     "declare SynthEvent\n" +
                     "    @role( event )\n" +
                     "    @timestamp( timestamp )\n" +
                     "end\n" +
                     "\n" +
                     "declare EventCounter\n" +
                     "      @role( event )\n" +
                     "      @timestamp( timestamp )\n" +
                     "      id          : long\n" +
                     "      key         : String\n" +
                     "      timestamp   : Date\n" +
                     "end\n" +
                     "\n" +
                     "rule \"Create counter\"\n" +
                     "when\n" +
                     "$e : SynthEvent() from entry-point \"synth\"\n" +
                     "then\n" +
                     "    entryPoints[\"counters\"].insert(new EventCounter( $e.getId(), \"event\", $e.getTimestamp() ) );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"Count epm\"\n" +
                     "timer ( cron: 0/10 * * * * ? )\n" +
                     //"timer ( int: 2s 1s )\n" +
                     "when\n" +
                     "    Number( $count : intValue ) from accumulate(\n" +
                     "       EventCounter( key == \"event\" ) over window:time( 60s ) from entry-point \"counters\", count(1) )\n" +
                     "then\n" +
                     "    System.out.println(\"[\" + new Date() + \"] epm = \" + $count );\n" +
                     "end\n " +
                     "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final KieSession ksession = kbase.newKieSession();
        EntryPoint synthEP =  ksession.getEntryPoint("synth");

        new Thread(){
            public void run() {
                System.out.println("[" + new Date() + "] start!");
                ksession.fireUntilHalt();
            };
        }.start();

        long counter = 0;
        while(true) {
            counter++;
            synthEP.insert(new SynthEvent(counter));
            try {
                Thread.sleep(20L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if ((counter % 1000) == 0) {
                System.out.println("Total events: " + counter);
            }
        } 
   }


    public static class SynthEvent {
        private final long id;
        private final Date timestamp;

        public SynthEvent(long id) {
            this.id = id;
            timestamp = new Date();
        }

        public long getId() {
            return id;
        }

        public Date getTimestamp() {
            return timestamp;
        }
    }

    @Test
    public void testExpiredEventModification() {
        // BZ-1082990
        String drl = "import " + SimpleEvent.class.getCanonicalName() + "\n" +
                     "import java.util.Date\n" +
                     "\n" +
                     "declare OtherFact\n" +
                     "    @role( event )\n" +
                     "end\n" +
                     "\n" +
                     "declare SimpleEvent\n" +
                     "    @role( event )\n" +
                     "    @expires( 1h )\n" +
                     "    @timestamp( dateEvt )\n" +
                     "end\n" +
                     "\n" +
                     "\n" +
                     "rule R no-loop\n" +
                     "    when\n" +
                     "        $e : SimpleEvent()\n" +
                     "        not OtherFact( this after[1ms, 1h] $e )\n" +
                     "    then\n" +
                     "        modify($e) {setCode(\"code2\")};\n" +
                     "    end\n " +
                     "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        final KieSession ksession = kbase.newKieSession(sessionConfig, null);
        PseudoClockScheduler clock = ksession.getSessionClock();
        clock.setStartupTime(System.currentTimeMillis());

        SimpleEvent event = new SimpleEvent("code1");
        event.setDateEvt(System.currentTimeMillis() - (2 * 60 * 60 * 1000));
        ksession.insert(event);
        ksession.fireAllRules();
    }

    public static class SimpleEvent {
        private String code;
        private Long dateEvt;


        public SimpleEvent(final String aCode) {
            this.code = aCode;
            this.dateEvt = System.currentTimeMillis();
        }

        public SimpleEvent(final String aCode, final Long dateEvt) {
            this.code = aCode;
            this.dateEvt = dateEvt;
        }

        public String getCode() {
            return code;
        }

        public void setCode(final String code) {
            this.code = code;
        }

        public Long getDateEvt() {
            return dateEvt;
        }

        public void setDateEvt(Long dateEvt) {
            this.dateEvt = dateEvt;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " (code=" + code + ")";
        }
    }

    @Test
    public void testTemporalOperatorWithConstant() {
        // BZ-1096243
        String drl = "import " + SimpleEvent.class.getCanonicalName() + "\n" +
                     "import java.util.Date\n" +
                     "global java.util.List list" +
                     "\n" +
                     "declare SimpleEvent\n" +
                     "    @role( event )\n" +
                     "    @timestamp( dateEvt )\n" +
                     "end\n" +
                     "\n" +
                     "rule R \n" +
                     "    when\n" +
                     "        $e : SimpleEvent( this after \"01-Jan-2014\"  )\n" +
                     "    then\n" +
                     "        list.add(\"1\");\n" +
                     "    end\n " +
                     "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal("list", list);

        SimpleEvent event = new SimpleEvent("code1", DateUtils.parseDate("18-Mar-2014").getTime());
        ksession.insert(event);
        ksession.fireAllRules();

        assertEquals(1, list.size());
    }

    @Test
    public void testTemporalOperatorWithConstantAndJoin() throws Exception {
        // BZ 1096243
        String drl = "import " + SimpleEvent.class.getCanonicalName() + "\n" +
                     "import java.util.Date\n" +
                     "global java.util.List list" +
                     "\n" +
                     "declare SimpleEvent\n" +
                     "    @role( event )\n" +
                     "    @timestamp( dateEvt )\n" +
                     "end\n" +
                     "\n" +
                     "rule R \n" +
                     "    when\n" +
                     "        $e1 : SimpleEvent( this after \"01-Jan-2014\"  )\n" +
                     "        $e2 : SimpleEvent( this after $e1 ) \n" +
                     "    then\n" +
                     "        list.add(\"1\");\n" +
                     "    end\n " +
                     "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal("list", list);

        SimpleEvent event1 = new SimpleEvent("code1", DateUtils.parseDate("18-Mar-2014").getTime());
        ksession.insert(event1);
        SimpleEvent event2 = new SimpleEvent("code2", DateUtils.parseDate("19-Mar-2014").getTime());
        ksession.insert(event2);
        ksession.fireAllRules();

        assertEquals(1, list.size());
    }

    @Test
    public void testDynamicSalienceInStreamMode() throws Exception {
        // DROOLS-526
        String drl =
                "import java.util.concurrent.atomic.AtomicInteger;\n" +
                "\n" +
                "global AtomicInteger salience1\n" +
                "global AtomicInteger salience2\n" +
                "global java.util.List list\n" +
                "\n" +
                "declare Integer\n" +
                " @role(event)\n" +
                "end\n" +
                "\n" +
                "rule R1\n" +
                "salience salience1.get()\n" +
                "when\n" +
                " $i : Integer()\n" +
                "then\n" +
                " retract($i);\n" +
                " salience1.decrementAndGet();\n" +
                " list.add(1);\n" +
                "end \n" +
                "\n" +
                "rule R2\n" +
                "salience salience2.get()\n" +
                "when\n" +
                " $i : Integer()\n" +
                "then\n" +
                " retract($i);\n" +
                " salience2.decrementAndGet();\n" +
                " list.add(2);\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSession ksession = kbase.newKieSession();
        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);
        ksession.setGlobal("salience1", new AtomicInteger(9));
        ksession.setGlobal("salience2", new AtomicInteger(10));

        for (int i = 0; i < 10; i++) {
            ksession.insert(i);
            ksession.fireAllRules();
        }

        assertEquals(list, Arrays.asList(2, 1, 2, 1, 2, 1, 2, 1, 2, 1));
    }

    @Test
    public void testRightTupleLeak() throws Exception {
        // DROOLS-516
        String drl =
                "declare Integer @role(event) end\n" +
                "declare Long @role(event) end\n" +
                "\n" +
                "rule R1 when\n" +
                " $long : Long()\n" +
                " Integer( this > $long )\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                " $i : Integer()\n" +
                "then\n" +
                " retract( $i );\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSession ksession = kbase.newKieSession();

        for (int i = 0; i < 10; i++) {
            ksession.insert(new Integer(i));
            ksession.fireAllRules();
        }

        // force gc
        GarbageCollector gc = ((InternalAgenda) ksession.getAgenda()).getGarbageCollector();
        assertEquals(10, gc.getDeleteCounter());
        gc.forceGcUnlinkedRules();

        Rete rete = ((KnowledgeBaseImpl)kbase).getRete();
        JoinNode joinNode = null;
        for (ObjectTypeNode otn : rete.getObjectTypeNodes()) {
            if ( Integer.class == otn.getObjectType().getValueType().getClassType() ) {
                joinNode = (JoinNode)otn.getSinkPropagator().getSinks()[0];
                break;
            }
        }

        assertNotNull(joinNode);
        InternalWorkingMemory wm = (InternalWorkingMemory)ksession;
        BetaMemory memory = (BetaMemory)wm.getNodeMemory(joinNode);
        assertEquals(0, memory.getSegmentMemory().getStreamQueue().size());

        RightTupleSets stagedRightTuples = memory.getStagedRightTuples();
        assertEquals(0, stagedRightTuples.deleteSize());
        assertEquals(0, stagedRightTuples.insertSize());
    }

    @Test
    public void testRightTupleLeak2() throws Exception {
        // DROOLS-516
        String drl =
                "declare Integer @role(event) end\n" +
                "declare Long @role(event) end\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R1 when\n" +
                " $long : Long()\n" +
                " $i : Integer( this > $long )\n" +
                "then\n" +
                " list.add($i);\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                " $i : Integer( this > 3 )\n" +
                "then\n" +
                " retract( $i );\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        for (int i = 0; i < 10; i++) {
            ksession.insert(new Integer(i));
            ksession.fireAllRules();
        }

        // force gc
        ((DefaultAgenda)ksession.getAgenda()).getGarbageCollector().forceGcUnlinkedRules();

        Rete rete = ((KnowledgeBaseImpl)kbase).getRete();
        JoinNode joinNode = null;
        for (ObjectTypeNode otn : rete.getObjectTypeNodes()) {
            if ( Integer.class == otn.getObjectType().getValueType().getClassType() ) {
                joinNode = (JoinNode)otn.getSinkPropagator().getSinks()[0];
                break;
            }
        }

        assertNotNull(joinNode);
        InternalWorkingMemory wm = (InternalWorkingMemory)ksession;
        BetaMemory memory = (BetaMemory)wm.getNodeMemory(joinNode);
        assertEquals(0, memory.getSegmentMemory().getStreamQueue().size());

        RightTupleSets stagedRightTuples = memory.getStagedRightTuples();
        assertEquals(0, stagedRightTuples.deleteSize());
        assertEquals(4, stagedRightTuples.insertSize());

        ksession.insert(new Long(0));
        ksession.fireAllRules();

        assertEquals(3, list.size());
        assertTrue(list.containsAll(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void testRightTupleLeak3() throws Exception {
        // DROOLS-516
        String drl =
                "declare Integer @role(event) end\n" +
                "declare Long @role(event) end\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R1 when\n" +
                " $long : Long()\n" +
                " $i : Integer( this > $long )\n" +
                " String()\n" +
                "then\n" +
                " list.add($i);\n" +
                "end\n" +
                "rule R2 when\n" +
                " $long : Long()\n" +
                " $i : Integer( this > $long )\n" +
                " eval( $i % 2 == 0 )\n" +
                "then\n" +
                " retract($i);\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        FactHandle sFH = ksession.insert("");
        ksession.insert(new Long(0));
        ksession.fireAllRules();

        for (int i = 0; i < 10; i++) {
            ksession.insert(new Integer(i));
            if (i == 6) {
                ksession.delete(sFH);
            }
        }
        ksession.fireAllRules();

        // force gc
        ((DefaultAgenda)ksession.getAgenda()).getGarbageCollector().forceGcUnlinkedRules();

        Rete rete = ((KnowledgeBaseImpl)kbase).getRete();
        JoinNode joinNode = null;
        for (ObjectTypeNode otn : rete.getObjectTypeNodes()) {
            if ( Integer.class == otn.getObjectType().getValueType().getClassType() ) {
                joinNode = (JoinNode)otn.getSinkPropagator().getSinks()[0];
                break;
            }
        }

        assertNotNull(joinNode);
        InternalWorkingMemory wm = (InternalWorkingMemory)ksession;
        BetaMemory memory = (BetaMemory)wm.getNodeMemory(joinNode);
        assertEquals(0, memory.getSegmentMemory().getStreamQueue().size());

        RightTupleSets stagedRightTuples = memory.getStagedRightTuples();
        assertEquals(4, stagedRightTuples.deleteSize());
        assertEquals(0, stagedRightTuples.insertSize());

        ksession.insert("");
        ksession.fireAllRules();

        assertEquals(0, stagedRightTuples.deleteSize());
        assertEquals(0, stagedRightTuples.insertSize());

        System.out.println(list);

        assertEquals(5, list.size());
        assertTrue(list.containsAll(Arrays.asList(1, 3, 5, 7, 9)));
    }

    @Test
    public void testDurationMemoryLeakWithAlwaysLinkedRules() throws Exception {
        String drl =
                "import org.drools.compiler.StockTick;\n " +

                "declare StockTick\n"+
                " @role( event )\n"+
                " @timestamp( time )\n"+
                "end\n"+

                "rule Clear \n"+
                "when\n"+
                " $droo : StockTick( company == \"DROO\" )\n"+
                "then\n"+
                " delete($droo);\n"+
                "end\n"+

                "rule Cancel\n"+
                "when\n"+
                " $oord : StockTick( company != \"DROO\" )\n"+
                " not StockTick( company == \"DROO\" )\n"+
                "then\n"+
                "end";


        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ksession = helper.build( EventProcessingOption.STREAM ).newKieSession();

        assertEquals("FactCount should be 0[1]", 0, ksession.getFactCount());

        for ( int j = 0; j < 100; j++ ) {
            ksession.insert(new StockTick(0, "DROO", 1.00));
        }
        ksession.fireAllRules();
        assertEquals("FactCount should still be 0[2]", 0, ksession.getFactCount());

        ((DefaultAgenda)ksession.getAgenda()).getGarbageCollector().forceGcUnlinkedRules();

        NodeMemories nm = ( (StatefulKnowledgeSessionImpl) ksession ).getNodeMemories();
        for ( int j = 0; j < nm.length(); j++ ) {
            Memory mem = nm.peekNodeMemory( j );
            if ( mem != null && mem instanceof PathMemory) {
                PathMemory pathMemory = (PathMemory) mem;
                StreamTupleEntryQueue kiu = pathMemory.getStreamQueue();
                System.out.println( kiu + " >> " + kiu.size() );
                assertEquals( 0, kiu.size() );
            }
        }
    }

    @Test
    public void test2NotsWithTemporalConstraints() {
        // BZ-1122738 DROOLS-479
        String drl = "import " + SimpleEvent.class.getCanonicalName() + "\n" +
                     "import java.util.Date\n" +
                     "\n" +
                     "declare OtherFact\n" +
                     "    @role( event )\n" +
                     "end\n" +
                     "\n" +
                     "declare SimpleEvent\n" +
                     "    @role( event )\n" +
                     "    @timestamp( dateEvt )\n" +
                     "end\n" +
                     "\n" +
                     "\n" +
                     "rule R\n" +
                     "    when\n" +
                     "        $e : SimpleEvent()\n" +
                     "        not OtherFact( this after[0, 1h] $e )\n" +
                     "        not OtherFact( this after[0, 1h] $e )\n" +
                     "    then\n" +
                     "        $e.setCode(\"code2\");\n" +
                     "    end\n " +
                     "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        final KieSession ksession = kbase.newKieSession(sessionConfig, null);
        PseudoClockScheduler clock = ksession.getSessionClock();
        clock.setStartupTime(System.currentTimeMillis());

        SimpleEvent event = new SimpleEvent("code1");
        event.setDateEvt(System.currentTimeMillis() - (2 * 60 * 60 * 1000));
        ksession.insert(event);
        ksession.fireAllRules();
        assertEquals("code2", event.getCode());
    }
}