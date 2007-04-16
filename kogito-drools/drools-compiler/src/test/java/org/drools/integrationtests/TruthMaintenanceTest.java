package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.CheeseEqual;
import org.drools.FactHandle;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.Sensor;
import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.TruthMaintenanceSystem;
import org.drools.compiler.PackageBuilder;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.event.DefaultAgendaEventListener;
import org.drools.event.DefaultWorkingMemoryEventListener;
import org.drools.event.ObjectAssertedEvent;
import org.drools.event.ObjectModifiedEvent;
import org.drools.event.ObjectRetractedEvent;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.rule.Package;
import org.drools.util.ObjectHashMap;

public class TruthMaintenanceTest extends TestCase {
    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }
    
    public void testLogicalAssertions() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertions.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        final FactHandle brieHandle = workingMemory.assertObject( brie );

        final Cheese provolone = new Cheese( "provolone",
                                             12 );
        final FactHandle provoloneHandle = workingMemory.assertObject( provolone );

        workingMemory.fireAllRules();

        assertEquals( 3,
                      list.size() );

        assertEquals( 3,
                      workingMemory.getObjects().size() );

        workingMemory.retractObject( brieHandle );

        assertEquals( 2,
                      workingMemory.getObjects().size() );

        workingMemory.retractObject( provoloneHandle );

        assertEquals( 0,
                      workingMemory.getObjects().size() );
    }

    public void testLogicalAssertionsBacking() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsBacking.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final Cheese cheese1 = new Cheese( "c",
                                           1 );
        final Cheese cheese2 = new Cheese( cheese1.getType(),
                                           1 );
        List list;

        final FactHandle h1 = workingMemory.assertObject( cheese1 );
        workingMemory.fireAllRules();
        list = workingMemory.getObjects( cheese1.getType().getClass() );
        assertEquals( 1,
                      list.size() );
        // probably dangerous, as contains works with equals, not identity
        assertEquals( cheese1.getType(),
                      list.get( 0 ) );
        // FactHandle ht = workingMemory.getFactHandle(c1.getType());

        final FactHandle h2 = workingMemory.assertObject( cheese2 );
        workingMemory.fireAllRules();
        list = workingMemory.getObjects( cheese1.getType().getClass() );
        assertEquals( 1,
                      list.size() );
        assertEquals( cheese1.getType(),
                      list.get( 0 ) );
        
        assertEquals( 3, workingMemory.getObjects().size() );

        workingMemory.retractObject( h1 );
        workingMemory.fireAllRules();
        list = workingMemory.getObjects( cheese1.getType().getClass() );
        assertEquals( "cheese-type " + cheese1.getType() + " was retracted, but should not. Backed by cheese2 => type.",
                      1,
                      list.size() );
        assertEquals( "cheese-type " + cheese1.getType() + " was retracted, but should not. Backed by cheese2 => type.",
                      cheese1.getType(),
                      list.get( 0 ) );

        workingMemory.retractObject( h2 );
        workingMemory.fireAllRules();
        list = workingMemory.getObjects( cheese1.getType().getClass() );
        assertEquals( "cheese-type " + cheese1.getType() + " was not retracted, but should have. Neither  cheese1 => type nor cheese2 => type is true.",
                      0,
                      list.size() );
    }

    public void testLogicalAssertionsSelfreferencing() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsSelfreferencing.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list;

        final Person b = new Person( "b" );
        final Person a = new Person( "a" );

        workingMemory.setGlobal( "b",
                                 b );

        FactHandle h1 = workingMemory.assertObject( a );
        workingMemory.fireAllRules();
        list = workingMemory.getObjects( a.getClass() );
        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( a ) );
        assertTrue( list.contains( b ) );

        workingMemory.retractObject( h1 );
        workingMemory.fireAllRules();
        list = workingMemory.getObjects( a.getClass() );
        assertEquals( "b was retracted, but it should not have. Is backed by b => b being true.",
                      1,
                      list.size() );
        assertEquals( "b was retracted, but it should not have. Is backed by b => b being true.",
                      b,
                      list.get( 0 ) );

        h1 = workingMemory.getFactHandle( b );
        workingMemory.retractObject( h1 );
        workingMemory.fireAllRules();
        list = workingMemory.getObjects( a.getClass() );
        assertEquals( 0,
                      list.size() );
    }

    public void testLogicalAssertionsLoop() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsLoop.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list;

        final List l = new ArrayList();
        final Person a = new Person( "a" );
        workingMemory.setGlobal( "a",
                                 a );
        workingMemory.setGlobal( "l",
                                 l );

        workingMemory.fireAllRules();
        list = workingMemory.getObjects( a.getClass() );
        assertEquals( "a still asserted.",
                      0,
                      list.size() );
        assertEquals( "Rule has not fired (looped) expected number of times",
                      10,
                      l.size() );
    }

    public void testLogicalAssertionsNoLoop() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsNoLoop.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list;

        final List l = new ArrayList();
        final Person a = new Person( "a" );
        workingMemory.setGlobal( "a",
                                 a );
        workingMemory.setGlobal( "l",
                                 l );

        workingMemory.fireAllRules();
        list = workingMemory.getObjects( a.getClass() );
        assertEquals( "a still in WM",
                      0,
                      list.size() );
        assertEquals( "Rule should not loop",
                      1,
                      l.size() );
    }

    public void testLogicalAssertionsWithModify() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsWithModify.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        final WorkingMemoryEventListener l2 = new DefaultWorkingMemoryEventListener() {
            public void objectAsserted(ObjectAssertedEvent event) {
                System.out.println( event );
            }

            public void objectRetracted(ObjectRetractedEvent event) {
                System.out.println( event );
            }

            public void objectModified(ObjectModifiedEvent event) {
                System.out.println( event );
            }
        };
        
        workingMemory.addEventListener( l2 );        

        List l;
        final Person p = new Person( "person" );
        p.setAge( 2 );
        final FactHandle h = workingMemory.assertObject( p );
        assertEquals( 1,
                      workingMemory.getObjects().size() );

        workingMemory.fireAllRules();
        assertEquals( 2,
                      workingMemory.getObjects().size() );
        l = workingMemory.getObjects( CheeseEqual.class );
        assertEquals( 1,
                      l.size() );
        assertEquals( 2,
                      ((CheeseEqual) l.get( 0 )).getPrice() );

        workingMemory.retractObject( h );
        assertEquals( 0,
                      workingMemory.getObjects().size() );

        TruthMaintenanceSystem tms = ((InternalWorkingMemory) workingMemory).getTruthMaintenanceSystem();

        final java.lang.reflect.Field field = tms.getClass().getDeclaredField( "assertMap" );
        field.setAccessible( true );
        final ObjectHashMap m = (ObjectHashMap) field.get( tms );
        field.setAccessible( false );
        assertEquals( "assertMap should be empty",
                      0,
                      m.size() );
    }

    public void testLogicalAssertions2() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertions2.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( workingMemory );
        logger.setFileName( "logical" );

        final List events = new ArrayList();

        workingMemory.setGlobal( "events",
                                 events );

        final Sensor sensor = new Sensor( 80,
                                          80 );
        final FactHandle handle = workingMemory.assertObject( sensor );

        // everything should be normal
        workingMemory.fireAllRules();

        final List list = workingMemory.getObjects();

        assertEquals( "Only sensor is there",
                      1,
                      list.size() );
        assertEquals( "Only one event",
                      1,
                      events.size() );

        // problems should be detected
        sensor.setPressure( 200 );
        sensor.setTemperature( 200 );
        workingMemory.modifyObject( handle,
                                    sensor );

        workingMemory.fireAllRules();
        logger.writeToDisk();

        assertEquals( "Only sensor is there",
                      1,
                      list.size() );
        assertEquals( "Exactly seven events",
                      7,
                      events.size() );
    }

    public void testLogicalAssertionsNot() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsNot.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list;

        final Person a = new Person( "a" );
        final Cheese cheese = new Cheese( "brie",
                                          1 );
        workingMemory.setGlobal( "cheese",
                                 cheese );

        workingMemory.fireAllRules();
        list = workingMemory.getObjects();
        assertEquals( "i was not asserted by not a => i.",
                      1,
                      list.size() );
        assertEquals( "i was not asserted by not a => i.",
                      cheese,
                      list.get( 0 ) );

        final FactHandle h = workingMemory.assertObject( a );
        // no need to fire rules, assertion alone removes justification for i,
        // so it should be retracted.
        // workingMemory.fireAllRules();
        list = workingMemory.getObjects();
        assertEquals( "a was not asserted or i not retracted.",
                      1,
                      list.size() );
        assertEquals( "a was asserted.",
                      a,
                      list.get( 0 ) );
        assertFalse( "i was not rectracted.",
                     list.contains( cheese ) );

        // no rules should fire, but nevertheless...
        // workingMemory.fireAllRules();
        assertEquals( "agenda should be empty.",
                      0,
                      workingMemory.getAgenda().agendaSize() );

        workingMemory.retractObject( h );
        workingMemory.fireAllRules();
        list = workingMemory.getObjects();
        assertEquals( "i was not asserted by not a => i.",
                      1,
                      list.size() );
        assertEquals( "i was not asserted by not a => i.",
                      cheese,
                      list.get( 0 ) );
    }

    public void testLogicalAssertionsNotPingPong() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsNotPingPong.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        // workingMemory.addEventListener(new DebugAgendaEventListener());
        // workingMemory.addEventListener(new
        // DebugWorkingMemoryEventListener());

        final List list = new ArrayList();

        final Person person = new Person( "person" );
        final Cheese cheese = new Cheese( "cheese",
                                          0 );
        workingMemory.setGlobal( "cheese",
                                 cheese );
        workingMemory.setGlobal( "person",
                                 person );
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.fireAllRules();

        // not sure about desired state of working memory.
        assertEquals( "Rules have not fired (looped) expected number of times",
                      10,
                      list.size() );
    }

    public void testLogicalAssertionsDynamicRule() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsDynamicRule.drl" ) ) );
        final Package pkg = builder.getPackage();

        org.drools.reteoo.ReteooRuleBase reteooRuleBase = null;
        // org.drools.leaps.LeapsRuleBase leapsRuleBase = null;
        final RuleBase ruleBase = getRuleBase();
        if ( ruleBase instanceof org.drools.reteoo.ReteooRuleBase ) {
            reteooRuleBase = (org.drools.reteoo.ReteooRuleBase) ruleBase;
            // } else if ( ruleBase instanceof org.drools.leaps.LeapsRuleBase )
            // {
            // leapsRuleBase = (org.drools.leaps.LeapsRuleBase) ruleBase;
        }
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        // workingMemory.addEventListener(new
        // org.drools.event.DebugAgendaEventListener());
        // workingMemory.addEventListener(new
        // org.drools.event.DebugWorkingMemoryEventListener());

        final Cheese c1 = new Cheese( "a",
                                      1 );
        final Cheese c2 = new Cheese( "b",
                                      2 );
        final Cheese c3 = new Cheese( "c",
                                      3 );
        List list;

        workingMemory.assertObject( c1 );
        final FactHandle h = workingMemory.assertObject( c2 );
        workingMemory.assertObject( c3 );
        workingMemory.fireAllRules();

        // Check logical assertions where made for c2 and c3
        list = workingMemory.getObjects( Person.class );
        assertEquals( 2,
                      list.size() );
        assertFalse( list.contains( new Person( c1.getType() ) ) );
        assertTrue( list.contains( new Person( c2.getType() ) ) );
        assertTrue( list.contains( new Person( c3.getType() ) ) );

        // this rule will make a logical assertion for c1 too
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsDynamicRule2.drl" ) );
        builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg2 = builder.getPackage();
        ruleBase.addPackage( pkg2 );

        workingMemory.fireAllRules();

        // check all now have just one logical assertion each
        list = workingMemory.getObjects( Person.class );
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( new Person( c1.getType() ) ) );
        assertTrue( list.contains( new Person( c2.getType() ) ) );
        assertTrue( list.contains( new Person( c3.getType() ) ) );

        // check the packages are correctly populated
        assertEquals( "org.drools.test",
                      ruleBase.getPackages()[0].getName() );
        assertEquals( "org.drools.test2",
                      ruleBase.getPackages()[1].getName() );
        assertEquals( "rule1",
                      ruleBase.getPackages()[0].getRules()[0].getName() );
        assertEquals( "rule2",
                      ruleBase.getPackages()[1].getRules()[0].getName() );

        // now remove the first rule
        if ( reteooRuleBase != null ) {
            reteooRuleBase.removeRule( ruleBase.getPackages()[0].getName(),
                                       ruleBase.getPackages()[0].getRules()[0].getName() );
            // } else if ( leapsRuleBase != null ) {
            // leapsRuleBase.removeRule( ruleBase.getPackages()[0].getName(),
            // ruleBase.getPackages()[0].getRules()[0].getName() );
        }

        // Check the rule was correctly remove
        assertEquals( 0,
                      ruleBase.getPackages()[0].getRules().length );
        assertEquals( 1,
                      ruleBase.getPackages()[1].getRules().length );
        assertEquals( "org.drools.test2",
                      ruleBase.getPackages()[1].getName() );
        assertEquals( "rule2",
                      ruleBase.getPackages()[1].getRules()[0].getName() );

        list = workingMemory.getObjects( Person.class );
        assertEquals( "removal of the rule should result in retraction of c3's logical assertion",
                      2,
                      list.size() );
        assertTrue( "c1's logical assertion should not be retracted",
                    list.contains( new Person( c1.getType() ) ) );
        assertTrue( "c2's logical assertion should  not be retracted",
                    list.contains( new Person( c2.getType() ) ) );
        assertFalse( "c3's logical assertion should be  retracted",
                     list.contains( new Person( c3.getType() ) ) );

        c2.setPrice( 3 );
        workingMemory.modifyObject( h,
                                    c2 );
        list = workingMemory.getObjects( Person.class );
        assertEquals( "c2 now has a higher price, its logical assertion should  be cancelled",
                      1,
                      list.size() );
        assertFalse( "The logical assertion cor c2 should have been retracted",
                     list.contains( new Person( c2.getType() ) ) );
        assertTrue( "The logical assertion  for c1 should exist",
                    list.contains( new Person( c1.getType() ) ) );

        if ( reteooRuleBase != null ) {
            reteooRuleBase.removeRule( ruleBase.getPackages()[1].getName(),
                                       ruleBase.getPackages()[1].getRules()[0].getName() );
            // } else if ( leapsRuleBase != null ) {
            // leapsRuleBase.removeRule( ruleBase.getPackages()[1].getName(),
            // ruleBase.getPackages()[1].getRules()[0].getName() );
        }
        assertEquals( 0,
                      ruleBase.getPackages()[0].getRules().length );
        assertEquals( 0,
                      ruleBase.getPackages()[1].getRules().length );
        list = workingMemory.getObjects( Person.class );
        assertEquals( 0,
                      list.size() );
    }

    public void testLogicalAssertionsModifyEqual() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsModifyEqual.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List l;
        final Person p = new Person( "person" );
        p.setAge( 2 );
        final FactHandle h = workingMemory.assertObject( p );
        assertEquals( 1,
                      workingMemory.getObjects().size() );

        workingMemory.fireAllRules();
        assertEquals( 2,
                      workingMemory.getObjects().size() );
        l = workingMemory.getObjects( CheeseEqual.class );
        assertEquals( 1,
                      l.size() );
        assertEquals( 3,
                      ((CheeseEqual) l.get( 0 )).getPrice() );

        workingMemory.retractObject( h );
        assertEquals( 0,
                      workingMemory.getObjects().size() );

        TruthMaintenanceSystem tms = ((InternalWorkingMemory) workingMemory).getTruthMaintenanceSystem();

        final java.lang.reflect.Field field = tms.getClass().getDeclaredField( "assertMap" );
        field.setAccessible( true );
        final ObjectHashMap m = (ObjectHashMap) field.get( tms );
        field.setAccessible( false );
        assertEquals( "assertMap should be empty",
                      0,
                      m.size() );
    }

    public void testLogicalAssertionsWithExists() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionWithExists.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final Person p1 = new Person( "p1",
                                      "stilton",
                                      20 );
        p1.setStatus( "europe" );
        final FactHandle c1FactHandle = workingMemory.assertObject( p1 );
        final Person p2 = new Person( "p2",
                                      "stilton",
                                      30 );
        p2.setStatus( "europe" );
        final FactHandle c2FactHandle = workingMemory.assertObject( p2 );
        final Person p3 = new Person( "p3",
                                      "stilton",
                                      40 );
        p3.setStatus( "europe" );
        final FactHandle c3FactHandle = workingMemory.assertObject( p3 );
        workingMemory.fireAllRules();

        // all 3 in europe, so, 2 cheese
        List cheeseList = workingMemory.getObjects( Cheese.class );
        assertEquals( 2,
                      cheeseList.size() );

        // europe=[ 1, 2 ], america=[ 3 ]
        p3.setStatus( "america" );
        workingMemory.modifyObject( c3FactHandle,
                                    p3 );
        workingMemory.fireAllRules();
        cheeseList = workingMemory.getObjects( Cheese.class );
        assertEquals( 1,
                      cheeseList.size() );

        // europe=[ 1 ], america=[ 2, 3 ]
        p2.setStatus( "america" );
        workingMemory.modifyObject( c2FactHandle,
                                    p2 );
        workingMemory.fireAllRules();
        cheeseList = workingMemory.getObjects( Cheese.class );
        assertEquals( 1,
                      cheeseList.size() );

        // europe=[ ], america=[ 1, 2, 3 ]
        p1.setStatus( "america" );
        workingMemory.modifyObject( c1FactHandle,
                                    p1 );
        workingMemory.fireAllRules();
        cheeseList = workingMemory.getObjects( Cheese.class );
        assertEquals( 2,
                      cheeseList.size() );

        // europe=[ 2 ], america=[ 1, 3 ]
        p2.setStatus( "europe" );
        workingMemory.modifyObject( c2FactHandle,
                                    p2 );
        workingMemory.fireAllRules();
        cheeseList = workingMemory.getObjects( Cheese.class );
        assertEquals( 1,
                      cheeseList.size() );

        // europe=[ 1, 2 ], america=[ 3 ]
        p1.setStatus( "europe" );
        workingMemory.modifyObject( c1FactHandle,
                                    p1 );
        workingMemory.fireAllRules();
        cheeseList = workingMemory.getObjects( Cheese.class );
        assertEquals( 1,
                      cheeseList.size() );

        // europe=[ 1, 2, 3 ], america=[ ]
        p3.setStatus( "europe" );
        workingMemory.modifyObject( c3FactHandle,
                                    p3 );
        workingMemory.fireAllRules();
        cheeseList = workingMemory.getObjects( Cheese.class );
        assertEquals( 2,
                      cheeseList.size() );
    }
    
    public void testLogicalAssertions3() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_logicalAssertions3.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "events",
                                 list );

        // asserting the sensor object
        final Sensor sensor = new Sensor( 150,
                                          100 );
        final FactHandle sensorHandle = workingMemory.assertObject( sensor );

        workingMemory.fireAllRules();

        // alarm must sound
        assertEquals( 2,
                      list.size() );
        assertEquals( 2,
                      workingMemory.getObjects().size() );

        // modifying sensor
        sensor.setTemperature( 125 );
        workingMemory.modifyObject( sensorHandle,
                                    sensor );
        workingMemory.fireAllRules();

        // alarm must continue to sound
        assertEquals( 3,
                      list.size() );
        assertEquals( 2,
                      workingMemory.getObjects().size() );

        // modifying sensor
        sensor.setTemperature( 80 );
        workingMemory.modifyObject( sensorHandle,
                                    sensor );
        workingMemory.fireAllRules();

        // no alarms anymore
        assertEquals( 3,
                      list.size() );
        assertEquals( 1,
                      workingMemory.getObjects().size() );
    }
    
    public void testLogicalAssertionsAccumulatorPattern() throws Exception {
        // JBRULES-449
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsAccumulatorPattern.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        workingMemory.setGlobal( "ga", "a");
        workingMemory.setGlobal( "gb", "b");
        workingMemory.setGlobal( "gs", new Short((short)3));
        
        workingMemory.fireAllRules();

        List l;
        final FactHandle h = workingMemory.assertObject( new Integer( 6 ) );
        assertEquals( 1,
                      workingMemory.getObjects().size() );

        workingMemory.fireAllRules();
        l = workingMemory.getObjects( CheeseEqual.class );
        assertEquals( "There should be 2 CheeseEqual in Working Memory, 1 justified, 1 stated", 2, l.size() );
        assertEquals( 6, workingMemory.getObjects().size() );

        workingMemory.retractObject( h );
        l = workingMemory.getObjects( CheeseEqual.class );
        assertEquals( "There should be only 1 CheeseEqual in Working Memory, 1 stated (the justified should have been retracted). Check TruthMaintenanceSystem justifiedMap", 1, l.size() );
        l = workingMemory.getObjects( Short.class );
        assertEquals( 1, l.size() );
        assertEquals( 2, workingMemory.getObjects().size() );
        
        //clean-up
        workingMemory.fireAllRules();
        assertEquals( 0, workingMemory.getObjects().size() );
    }    
    

}
