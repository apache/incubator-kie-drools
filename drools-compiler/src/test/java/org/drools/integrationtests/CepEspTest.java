package org.drools.integrationtests;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.ClockType;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.OrderEvent;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.StockTick;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.base.evaluators.TimeIntervalParser;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.time.SessionPseudoClock;
import org.drools.time.impl.DurationTimer;
import org.drools.time.impl.PseudoClockScheduler;
import org.drools.util.DroolsStreamUtils;

public class CepEspTest extends TestCase {
    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    private RuleBase loadRuleBase(final Reader reader) throws IOException,
                                                      DroolsParserException,
                                                      Exception {
        return loadRuleBase( reader,
                             null );
    }

    private RuleBase loadRuleBase(final Reader reader,
                                  final RuleBaseConfiguration conf) throws IOException,
                                                                   DroolsParserException,
                                                                   Exception {
        final PackageBuilder builder = new PackageBuilder();
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            System.out.println( parser.getErrors() );
            Assert.fail( "Error messages in parser, need to sort this our (or else collect error messages)" );
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

    public void testEventAssertion() throws Exception {
        // read in the source
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_CEP_SimpleEventAssertion.drl" ) ),
                      ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession( conf,
                                                                              null );

        SessionPseudoClock clock = session.getSessionClock();

        final List results = new ArrayList();

        session.setGlobal( "results",
                           results );

        StockTick tick1 = new StockTick( 1,
                                         "DROO",
                                         50,
                                         10000 );
        StockTick tick2 = new StockTick( 2,
                                         "ACME",
                                         10,
                                         10010 );
        StockTick tick3 = new StockTick( 3,
                                         "ACME",
                                         10,
                                         10100 );
        StockTick tick4 = new StockTick( 4,
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

    public void testPackageSerializationWithEvents() throws Exception {
        // read in the source
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_CEP_SimpleEventAssertion.drl" ) ),
                      ResourceType.DRL );

        // get the package
        Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();
        assertEquals( 1,
                      pkgs.size() );
        KnowledgePackage kpkg = pkgs.iterator().next();

        // serialize the package
        Package internalPkg = ((KnowledgePackageImp)kpkg).pkg; // nasty trick for test purposes
        byte[] serializedPkg = DroolsStreamUtils.streamOut( internalPkg );

        // recreate the pkg using a new kbuilder
        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder2.add( ResourceFactory.newByteArrayResource( serializedPkg ),
                       ResourceType.PKG );
        assertFalse( kbuilder2.getErrors().toString(),
                     kbuilder2.hasErrors() );

        // create the kbase
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder2.getKnowledgePackages() );

        // create the session
        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession( conf,
                                                                              null );

        final List<StockTick> results = new ArrayList<StockTick>();

        session.setGlobal( "results",
                           results );

        StockTick tick1 = new StockTick( 1,
                                         "DROO",
                                         50,
                                         10000 );
        StockTick tick2 = new StockTick( 2,
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

        StockTick tick1 = new StockTick( 1,
                                         "DROO",
                                         50,
                                         10000,
                                         5 );
        StockTick tick2 = new StockTick( 2,
                                         "ACME",
                                         10,
                                         11000,
                                         10 );
        StockTick tick3 = new StockTick( 3,
                                         "ACME",
                                         10,
                                         12000,
                                         8 );
        StockTick tick4 = new StockTick( 4,
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

        StockTick tick1 = new StockTick( 1,
                                         "DROO",
                                         50,
                                         10000,
                                         5 );
        StockTick tick2 = new StockTick( 2,
                                         "ACME",
                                         10,
                                         11000,
                                         10 );
        StockTick tick3 = new StockTick( 3,
                                         "ACME",
                                         10,
                                         12000,
                                         8 );
        StockTick tick4 = new StockTick( 4,
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

    public void testEventExpiration() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_EventExpiration.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final InternalRuleBase internal = (InternalRuleBase) ruleBase;
        final TimeIntervalParser parser = new TimeIntervalParser();

        assertEquals( parser.parse( "1h30m" )[0].longValue(),
                      internal.getTypeDeclaration( StockTick.class ).getExpirationOffset() );
    }

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

        StockTick tick1 = new StockTick( 1,
                                         "DROO",
                                         50,
                                         System.currentTimeMillis(),
                                         3 );
        StockTick tick2 = new StockTick( 2,
                                         "ACME",
                                         10,
                                         System.currentTimeMillis(),
                                         3 );
        StockTick tick3 = new StockTick( 3,
                                         "ACME",
                                         10,
                                         System.currentTimeMillis(),
                                         3 );
        StockTick tick4 = new StockTick( 4,
                                         "DROO",
                                         50,
                                         System.currentTimeMillis(),
                                         5 );
        StockTick tick5 = new StockTick( 5,
                                         "ACME",
                                         10,
                                         System.currentTimeMillis(),
                                         5 );
        StockTick tick6 = new StockTick( 6,
                                         "ACME",
                                         10,
                                         System.currentTimeMillis(),
                                         3 );
        StockTick tick7 = new StockTick( 7,
                                         "ACME",
                                         10,
                                         System.currentTimeMillis(),
                                         5 );
        StockTick tick8 = new StockTick( 8,
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

        StockTick tick1 = new StockTick( 1,
                                         "DROO",
                                         50,
                                         100000, // arbitrary timestamp
                                         3 );
        StockTick tick2 = new StockTick( 2,
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

        StockTick tick1 = new StockTick( 1,
                                         "DROO",
                                         50,
                                         104000, // arbitrary timestamp
                                         3 );
        StockTick tick2 = new StockTick( 2,
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

        StockTick tick1 = new StockTick( 1,
                                         "DROO",
                                         50,
                                         100000, // arbitrary timestamp
                                         3 );
        StockTick tick2 = new StockTick( 2,
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

    public void testDelayingNot() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_DelayingNot.drl" ) );
        final RuleBaseConfiguration rbconf = new RuleBaseConfiguration();
        rbconf.setEventProcessingMode( EventProcessingOption.STREAM );
        final RuleBase ruleBase = loadRuleBase( reader,
                                                rbconf );

        final Rule rule = ruleBase.getPackage( "org.drools" ).getRule( "Delaying Not" );
        assertEquals( 10000,
                      ((DurationTimer)rule.getTimer()).getDuration() );

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

        EventFactHandle st1 = (EventFactHandle) wm.insert( new StockTick( 1,
                                                                          "DROO",
                                                                          100,
                                                                          clock.getCurrentTime() ) );

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

        assertEquals( st1.getObject(),
                      results.get( 0 ) );

    }

    //    public void FIXME_testTransactionCorrelation() throws Exception {
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

    public void testIdleTime() throws Exception {
        // read in the source
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_CEP_SimpleEventAssertion.drl" ) ),
                      ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession( conf,
                                                                              null );
        InternalWorkingMemory iwm = ((StatefulKnowledgeSessionImpl) session).session;

        SessionPseudoClock clock = session.getSessionClock();

        final List results = new ArrayList();

        session.setGlobal( "results",
                           results );

        StockTick tick1 = new StockTick( 1,
                                         "DROO",
                                         50,
                                         10000 );
        StockTick tick2 = new StockTick( 2,
                                         "ACME",
                                         10,
                                         10010 );
        StockTick tick3 = new StockTick( 3,
                                         "ACME",
                                         10,
                                         10100 );
        StockTick tick4 = new StockTick( 4,
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
        logger.setFileName( "audit" );

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

    public void testCollectWithWindows() throws Exception {
        // read in the source
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_CEP_CollectWithWindows.drl" ) ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        final KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( ksconf,
                                                                               null );
        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( ksession );
        logger.setFileName( "audit" );

        List<Number> timeResults = new ArrayList<Number>();
        List<Number> lengthResults = new ArrayList<Number>();

        ksession.setGlobal( "timeResults",
                            timeResults );
        ksession.setGlobal( "lengthResults",
                            lengthResults );

        SessionPseudoClock clock = (SessionPseudoClock) ksession.getSessionClock();

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
}
