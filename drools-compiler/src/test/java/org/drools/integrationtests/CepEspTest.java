package org.drools.integrationtests;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.ClockType;
import org.drools.OrderEvent;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.StockTick;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.time.SessionPseudoClock;
import org.drools.time.impl.PseudoClockScheduler;

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
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            System.out.println( parser.getErrors() );
            Assert.fail( "Error messages in parser, need to sort this our (or else collect error messages)" );
        }
        // pre build the package
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );
        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        // load up the rulebase
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        return ruleBase;
    }

    public void testEventAssertion() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_SimpleEventAssertion.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.PSEUDO_CLOCK );
        StatefulSession session = ruleBase.newStatefulSession( conf );
        
        final List results = new ArrayList();

        session.setGlobal( "results",    
                           results );

        StockTick tick1 = new StockTick( 1,
                                         "DROO",
                                         50,
                                         System.currentTimeMillis() );
        StockTick tick2 = new StockTick( 2,
                                         "ACME",
                                         10,
                                         System.currentTimeMillis() );
        StockTick tick3 = new StockTick( 3,
                                         "ACME",
                                         10,
                                         System.currentTimeMillis() );
        StockTick tick4 = new StockTick( 4,
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

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        assertEquals( 2,
                      ((List) session.getGlobal( "results" )).size() );

    }

    public void testEventAssertionWithDuration() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_SimpleEventAssertionWithDuration.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.PSEUDO_CLOCK );
        StatefulSession wm = ruleBase.newStatefulSession( conf );

        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        StockTick tick1 = new StockTick( 1,
                                         "DROO",
                                         50,
                                         System.currentTimeMillis(),
                                         5 );
        StockTick tick2 = new StockTick( 2,
                                         "ACME",
                                         10,
                                         System.currentTimeMillis(),
                                         10 );
        StockTick tick3 = new StockTick( 3,
                                         "ACME",
                                         10,
                                         System.currentTimeMillis(),
                                         8 );
        StockTick tick4 = new StockTick( 4,
                                         "DROO",
                                         50,
                                         System.currentTimeMillis(),
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

    public void testTimeRelationalOperators() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_TimeRelationalOperators.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.PSEUDO_CLOCK );
        StatefulSession wm = ruleBase.newStatefulSession( conf );

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
        clock.advanceTime( 4, TimeUnit.MILLISECONDS );
        InternalFactHandle handle2 = (InternalFactHandle) wm.insert( tick2 );
        clock.advanceTime( 4, TimeUnit.MILLISECONDS );
        InternalFactHandle handle3 = (InternalFactHandle) wm.insert( tick3 );
        clock.advanceTime( 4, TimeUnit.MILLISECONDS );
        InternalFactHandle handle4 = (InternalFactHandle) wm.insert( tick4 );
        InternalFactHandle handle5 = (InternalFactHandle) wm.insert( tick5 );
        clock.advanceTime( 1, TimeUnit.MILLISECONDS );
        InternalFactHandle handle6 = (InternalFactHandle) wm.insert( tick6 );
        InternalFactHandle handle7 = (InternalFactHandle) wm.insert( tick7 );
        clock.advanceTime( 2, TimeUnit.MILLISECONDS );
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
        assertEquals( tick7,
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

    public void testSimpleTimeWindow() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CEP_SimpleTimeWindow.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.PSEUDO_CLOCK );
        StatefulSession wm = ruleBase.newStatefulSession( conf );

        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        // how to initialize the clock?
        // how to configure the clock?
        SessionPseudoClock clock = (SessionPseudoClock) wm.getSessionClock();

        clock.advanceTime( 5, TimeUnit.SECONDS ); // 5 seconds
        EventFactHandle handle1 = (EventFactHandle) wm.insert( new OrderEvent( "1",
                                                                               "customer A",
                                                                               70 ) );
        assertEquals( 5000,
                      handle1.getStartTimestamp() );
        assertEquals( 0,
                      handle1.getDuration() );

        //        wm  = SerializationHelper.serializeObject(wm);
        wm.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( 70,
                      ((Number) results.get( 0 )).intValue() );

        // advance clock and assert new data
        clock.advanceTime( 10, TimeUnit.SECONDS ); // 10 seconds
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
        clock.advanceTime( 10, TimeUnit.SECONDS ); // 10 seconds
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
        clock.advanceTime( 10, TimeUnit.SECONDS ); // 10 seconds
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
        clock.advanceTime( 10, TimeUnit.SECONDS ); // 10 seconds
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
        clock.advanceTime( 10, TimeUnit.SECONDS ); // 10 seconds
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
        final RuleBase ruleBase = loadRuleBase( reader );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setClockType( ClockType.REALTIME_CLOCK );
        StatefulSession wm = ruleBase.newStatefulSession( conf );

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
    
}
