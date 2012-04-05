package org.drools.integrationtests;

import static org.drools.integrationtests.SerializationHelper.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.Father;
import org.drools.YoungestFather;
import org.drools.Cheese;
import org.drools.CheeseEqual;
import org.drools.ClassObjectFilter;
import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.Sensor;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.TruthMaintenanceSystem;
import org.drools.compiler.PackageBuilder;
import org.drools.core.util.ObjectHashMap;
import org.drools.definition.KnowledgePackage;
import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class TruthMaintenanceTest extends CommonTestMethodBase {
    
    protected KnowledgeBase getKnowledgeBase() throws Exception {
        return KnowledgeBaseFactory.newKnowledgeBase();
    }

    protected KnowledgeBase getKnowledgeBase(KnowledgeBaseConfiguration config) throws Exception {
        return KnowledgeBaseFactory.newKnowledgeBase( config );
    }

    @Test
    public void testLogicalInsertionsDynamicRule() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_LogicalInsertionsDynamicRule.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();
        KnowledgeBase kbase = getKnowledgeBase();
        kbase.addKnowledgePackages( kpkgs );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final Cheese c1 = new Cheese( "a",
                                      1 );
        final Cheese c2 = new Cheese( "b",
                                      2 );
        final Cheese c3 = new Cheese( "c",
                                      3 );
        List list;

        ksession.insert( c1 );
        FactHandle h = ksession.insert( c2 );
        ksession.insert( c3 );
        ksession.fireAllRules();
        
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );        

        // Check logical Insertions where made for c2 and c3        
        list = new ArrayList( ksession.getObjects( new ClassObjectFilter( Person.class ) ) );
        assertEquals( 2,
                      list.size() );
        assertFalse( list.contains( new Person( c1.getType() ) ) );
        assertTrue( list.contains( new Person( c2.getType() ) ) );
        assertTrue( list.contains( new Person( c3.getType() ) ) );

        // this rule will make a logical assertion for c1 too
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_LogicalInsertionsDynamicRule2.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }        
        Collection<KnowledgePackage> kpkgs2 = kbuilder.getKnowledgePackages();
        kbase.addKnowledgePackages( kpkgs2 );
        kbase = SerializationHelper.serializeObject( kbase );

        ksession.fireAllRules();
        
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );        

        kbase = ksession.getKnowledgeBase();

        // check all now have just one logical assertion each
        list = new ArrayList( ksession.getObjects( new ClassObjectFilter( Person.class ) ) );
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( new Person( c1.getType() ) ) );
        assertTrue( list.contains( new Person( c2.getType() ) ) );
        assertTrue( list.contains( new Person( c3.getType() ) ) );
        
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );        

        // check the packages are correctly populated
        KnowledgePackage[] pkgs = (KnowledgePackage[]) kbase.getKnowledgePackages().toArray( new KnowledgePackage[]{} );
        assertEquals( "org.drools.test",
                      pkgs[0].getName() );
        assertEquals( "org.drools.test2",
                      pkgs[1].getName() );
        assertEquals( "rule1",
                      pkgs[0].getRules().iterator().next().getName() );
        assertEquals( "rule2",
                      pkgs[1].getRules().iterator().next().getName() );

        // now remove the first rule
        kbase.removeRule( pkgs[0].getName(),
                          pkgs[0].getRules().iterator().next().getName() );
        pkgs = (KnowledgePackage[]) kbase.getKnowledgePackages().toArray( new KnowledgePackage[]{} );

        // Check the rule was correctly remove
        assertEquals( 0,
                      pkgs[0].getRules().size() );
        assertEquals( 1,
                      pkgs[1].getRules().size() );
        assertEquals( "org.drools.test2",
                      pkgs[1].getName() );
        assertEquals( "rule2",
                      pkgs[1].getRules().iterator().next().getName() );

        list = new ArrayList( ksession.getObjects( new ClassObjectFilter( Person.class ) ) );
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
        h = getFactHandle( h, ksession );
        ksession.update( h,
                         c2 );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );        
        list = new ArrayList( ksession.getObjects( new ClassObjectFilter( Person.class ) ) );
        assertEquals( "c2 now has a higher price, its logical assertion should  be cancelled",
                      1,
                      list.size() );
        assertFalse( "The logical assertion cor c2 should have been retracted",
                     list.contains( new Person( c2.getType() ) ) );
        assertTrue( "The logical assertion  for c1 should exist",
                    list.contains( new Person( c1.getType() ) ) );

        pkgs = (KnowledgePackage[]) kbase.getKnowledgePackages().toArray( new KnowledgePackage[]{} );
        kbase.removeRule( pkgs[1].getName(),
                          pkgs[1].getRules().iterator().next().getName() );
        kbase = SerializationHelper.serializeObject( kbase );
        pkgs = (KnowledgePackage[]) kbase.getKnowledgePackages().toArray( new KnowledgePackage[]{} );
        assertEquals( 0,
                      pkgs[0].getRules().size() );
        assertEquals( 0,
                      pkgs[1].getRules().size() );
        list = new ArrayList( ksession.getObjects( new ClassObjectFilter( Person.class ) ) );
        assertEquals( 0,
                      list.size() );
    }

    @Test
    public void testLogicalInsertions() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_LogicalInsertions.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );
        StatefulKnowledgeSession session = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        FactHandle brieHandle = session.insert( brie );

        final Cheese provolone = new Cheese( "provolone",
                                             12 );
        FactHandle provoloneHandle = session.insert( provolone );

        session.fireAllRules();

        session = getSerialisedStatefulKnowledgeSession( session,
                                                         true ); 
        
        assertEquals( 3,
                      list.size() );

        assertEquals( 3,
                      session.getObjects().size() );

        brieHandle = getFactHandle( brieHandle, session );
        session.retract( brieHandle );
        
        session = getSerialisedStatefulKnowledgeSession( session,
                                                         true );         

        assertEquals( 2,
                      session.getObjects().size() );

        provoloneHandle = getFactHandle( provoloneHandle, session );
        session.retract( provoloneHandle );

        assertEquals( 0,
                      session.getObjects().size() );
    }

    @Test
    public void testLogicalInsertionsBacking() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_LogicalInsertionsBacking.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );
        StatefulKnowledgeSession session = createKnowledgeSession(kbase);

        final Cheese cheese1 = new Cheese( "c",
                                           1 );
        final Cheese cheese2 = new Cheese( cheese1.getType(),
                                           1 );

        FactHandle h1 = session.insert( cheese1 );
        session.fireAllRules();
        
        session = getSerialisedStatefulKnowledgeSession( session,
                                                         true );         
        
        Collection< ? > list = session.getObjects( new ClassObjectFilter( cheese1.getType().getClass() ) );
        assertEquals( 1,
                      list.size() );
        // probably dangerous, as contains works with equals, not identity
        assertEquals( cheese1.getType(),
                      list.iterator().next() );

        FactHandle h2 = session.insert( cheese2 );
        session.fireAllRules();
        
        session = getSerialisedStatefulKnowledgeSession( session,
                                                         true );         
        
        list = session.getObjects( new ClassObjectFilter( cheese1.getType().getClass() ) );
        assertEquals( 1,
                      list.size() );
        assertEquals( cheese1.getType(),
                      list.iterator().next() );

        assertEquals( 3,
                      session.getObjects().size() );

        h1 = getFactHandle( h1, session );
        session.retract( h1 );
        session = getSerialisedStatefulKnowledgeSession( session,
                                                         true );         
        session.fireAllRules();
        session = getSerialisedStatefulKnowledgeSession( session,
                                                         true );         
        list = session.getObjects( new ClassObjectFilter( cheese1.getType().getClass() ) );
        assertEquals( "cheese-type " + cheese1.getType() + " was retracted, but should not. Backed by cheese2 => type.",
                      1,
                      list.size() );
        assertEquals( "cheese-type " + cheese1.getType() + " was retracted, but should not. Backed by cheese2 => type.",
                      cheese1.getType(),
                      list.iterator().next() );

        h2 = getFactHandle( h2, session );
        session.retract( h2 );
        session = getSerialisedStatefulKnowledgeSession( session,
                                                         true );         
        session.fireAllRules();
        session = getSerialisedStatefulKnowledgeSession( session,
                                                         true );         
        list = session.getObjects( new ClassObjectFilter( cheese1.getType().getClass() ) );
        assertEquals( "cheese-type " + cheese1.getType() + " was not retracted, but should have. Neither  cheese1 => type nor cheese2 => type is true.",
                      0,
                      list.size() );
    }

    @Test
    public void testLogicalInsertionsSelfreferencing() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_LogicalInsertionsSelfreferencing.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession session = createKnowledgeSession(kbase);

        final Person b = new Person( "b" );
        final Person a = new Person( "a" );

        session.setGlobal( "b",
                           b );

        FactHandle h1 = session.insert( a );
        session.fireAllRules();
        Collection< ? > list = session.getObjects( new ClassObjectFilter( a.getClass() ) );
        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( a ) );
        assertTrue( list.contains( b ) );

        session.retract( h1 );
        session.fireAllRules();
        list = session.getObjects( new ClassObjectFilter( a.getClass() ) );
        assertEquals( "b was retracted, but it should not have. Is backed by b => b being true.",
                      1,
                      list.size() );
        assertEquals( "b was retracted, but it should not have. Is backed by b => b being true.",
                      b,
                      list.iterator().next() );

        h1 = session.getFactHandle( b );
        session.retract( h1 );
        session.fireAllRules();
        list = session.getObjects( new ClassObjectFilter( a.getClass() ) );
        assertEquals( 0,
                      list.size() );
    }

    @Test
    public void testLogicalInsertionsLoop() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_LogicalInsertionsLoop.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kpkgs );
        kbase = SerializationHelper.serializeObject( kbase );
        final StatefulKnowledgeSession session = createKnowledgeSession(kbase);

        final List l = new ArrayList();
        final Person a = new Person( "a" );
        session.setGlobal( "a",
                           a );
        session.setGlobal( "l",
                           l );

        session.fireAllRules();
        Collection< ? > list = session.getObjects( new ClassObjectFilter( a.getClass() ) );
        assertEquals( "a still asserted.",
                      0,
                      list.size() );
        assertEquals( "Rule has not fired (looped) expected number of times",
                      10,
                      l.size() );
    }

    @Test
    public void testLogicalInsertionsNoLoop() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalInsertionsNoLoop.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        List list;

        final List l = new ArrayList();
        final Person a = new Person( "a" );
        workingMemory.setGlobal( "a",
                                 a );
        workingMemory.setGlobal( "l",
                                 l );

        workingMemory.fireAllRules();
        list = IteratorToList.convert( workingMemory.iterateObjects( new ClassObjectFilter( a.getClass() ) ) );
        assertEquals( "a still in WM",
                      0,
                      list.size() );
        assertEquals( "Rule should not loop",
                      1,
                      l.size() );
    }

    @Test
    public void testLogicalInsertionsWithModify() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalInsertionsWithUpdate.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession workingMemory = ruleBase.newStatefulSession();

        List l;
        final Person p = new Person( "person" );
        p.setAge( 2 );
        FactHandle h = workingMemory.insert( p );
        assertEquals( 1,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );

        workingMemory.fireAllRules();
        workingMemory = getSerialisedStatefulSession( workingMemory );         
        assertEquals( 2,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );

        l = IteratorToList.convert( workingMemory.iterateObjects( new ClassObjectFilter( CheeseEqual.class ) ) );
        assertEquals( 1,
                      l.size() );
        assertEquals( 2,
                      ((CheeseEqual) l.get( 0 )).getPrice() );

        h = getFactHandle( h, workingMemory );
        workingMemory.retract( h );
        workingMemory = getSerialisedStatefulSession( workingMemory );        
        assertEquals( 0,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );

        TruthMaintenanceSystem tms = ((InternalWorkingMemory) workingMemory).getTruthMaintenanceSystem();

        final java.lang.reflect.Field field = tms.getClass().getDeclaredField( "assertMap" );
        field.setAccessible( true );
        final ObjectHashMap m = (ObjectHashMap) field.get( tms );
        field.setAccessible( false );
        assertEquals( "assertMap should be empty",
                      0,
                      m.size() );
    }

    @Test
    public void testLogicalInsertions2() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalInsertions2.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession workingMemory = ruleBase.newStatefulSession();

        //        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( workingMemory );
        //        logger.setFileName( "logical" );

        final List events = new ArrayList();

        workingMemory.setGlobal( "events",
                                 events );

        final Sensor sensor = new Sensor( 80,
                                          80 );
        FactHandle handle = workingMemory.insert( sensor );

        // everything should be normal
        
        workingMemory = getSerialisedStatefulSession( workingMemory );        
        workingMemory.fireAllRules();

        final List list = IteratorToList.convert( workingMemory.iterateObjects() );

        assertEquals( "Only sensor is there",
                      1,
                      list.size() );
        assertEquals( "Only one event",
                      1,
                      events.size() );

        // problems should be detected
        sensor.setPressure( 200 );
        sensor.setTemperature( 200 );
        
        handle = getFactHandle( handle, workingMemory );
        workingMemory.update( handle,
                              sensor );
        
        workingMemory = getSerialisedStatefulSession( workingMemory );

        workingMemory.fireAllRules();
        //        logger.writeToDisk();

        assertEquals( "Only sensor is there",
                      1,
                      list.size() );
        assertEquals( "Exactly seven events",
                      7,
                      events.size() );
    }

    @Test
    public void testLogicalInsertionsNot() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalInsertionsNot.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession workingMemory = ruleBase.newStatefulSession();

        List list;

        final Person a = new Person( "a" );
        final Cheese cheese = new Cheese( "brie",
                                          1 );
        workingMemory.setGlobal( "cheese",
                                 cheese );

        workingMemory.fireAllRules();
        workingMemory = getSerialisedStatefulSession( workingMemory );        
        list = IteratorToList.convert( workingMemory.iterateObjects() );
        assertEquals( "i was not asserted by not a => i.",
                      1,
                      list.size() );
        assertEquals( "i was not asserted by not a => i.",
                      cheese,
                      list.get( 0 ) );

        FactHandle h = workingMemory.insert( a );
        workingMemory = getSerialisedStatefulSession( workingMemory );      
        // no need to fire rules, assertion alone removes justification for i,
        // so it should be retracted.
        // workingMemory.fireAllRules();
        list = IteratorToList.convert( workingMemory.iterateObjects() );
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

        h = getFactHandle( h, workingMemory );
        workingMemory.retract( h );
        workingMemory = getSerialisedStatefulSession( workingMemory );
        workingMemory.fireAllRules();
        workingMemory = getSerialisedStatefulSession( workingMemory );
        list = IteratorToList.convert( workingMemory.iterateObjects() );
        assertEquals( "i was not asserted by not a => i.",
                      1,
                      list.size() );
        assertEquals( "i was not asserted by not a => i.",
                      cheese,
                      list.get( 0 ) );
    }

    @Test
    public void testLogicalInsertionsNotPingPong() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalInsertionsNotPingPong.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

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

    @Test
    public void testLogicalInsertionsUpdateEqual() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalInsertionsUpdateEqual.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession workingMemory = ruleBase.newStatefulSession();

        List l;
        final Person p = new Person( "person" );
        p.setAge( 2 );
        FactHandle h = workingMemory.insert( p );
        assertEquals( 1,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );

        workingMemory.fireAllRules();
        workingMemory = getSerialisedStatefulSession( workingMemory );
        assertEquals( 2,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );
        l = IteratorToList.convert( workingMemory.iterateObjects( new ClassObjectFilter( CheeseEqual.class ) ) );
        assertEquals( 1,
                      l.size() );
        assertEquals( 3,
                      ((CheeseEqual) l.get( 0 )).getPrice() );

        h = getFactHandle( h, workingMemory );
        workingMemory.retract( h );
        workingMemory = getSerialisedStatefulSession( workingMemory );
        assertEquals( 0,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );

        TruthMaintenanceSystem tms = ((InternalWorkingMemory) workingMemory).getTruthMaintenanceSystem();

        final java.lang.reflect.Field field = tms.getClass().getDeclaredField( "assertMap" );
        field.setAccessible( true );
        final ObjectHashMap m = (ObjectHashMap) field.get( tms );
        field.setAccessible( false );
        assertEquals( "assertMap should be empty",
                      0,
                      m.size() );
    }

    @Test
    public void testLogicalInsertionsWithExists() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalInsertionWithExists.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession workingMemory = ruleBase.newStatefulSession();

        final Person p1 = new Person( "p1",
                                      "stilton",
                                      20 );
        p1.setStatus( "europe" );
        FactHandle c1FactHandle = workingMemory.insert( p1 );
        final Person p2 = new Person( "p2",
                                      "stilton",
                                      30 );
        p2.setStatus( "europe" );
        FactHandle c2FactHandle = workingMemory.insert( p2 );
        final Person p3 = new Person( "p3",
                                      "stilton",
                                      40 );
        p3.setStatus( "europe" );
        FactHandle c3FactHandle = workingMemory.insert( p3 );
        workingMemory.fireAllRules();
        
        workingMemory = getSerialisedStatefulSession( workingMemory );

        // all 3 in europe, so, 2 cheese
        List cheeseList = IteratorToList.convert( workingMemory.iterateObjects( new ClassObjectFilter( Cheese.class ) ) );
        assertEquals( 2,
                      cheeseList.size() );

        // europe=[ 1, 2 ], america=[ 3 ]
        p3.setStatus( "america" );
        c3FactHandle = getFactHandle( c3FactHandle, workingMemory );
        workingMemory.update( c3FactHandle,
                              p3 );
        workingMemory = getSerialisedStatefulSession( workingMemory );
        workingMemory.fireAllRules();
        workingMemory = getSerialisedStatefulSession( workingMemory );
        cheeseList = IteratorToList.convert( workingMemory.iterateObjects( new ClassObjectFilter( Cheese.class ) ) );
        assertEquals( 1,
                      cheeseList.size() );

        // europe=[ 1 ], america=[ 2, 3 ]
        p2.setStatus( "america" );
        c2FactHandle = getFactHandle( c2FactHandle, workingMemory );
        workingMemory.update( c2FactHandle,
                              p2 );
        workingMemory = getSerialisedStatefulSession( workingMemory );
        workingMemory.fireAllRules();
        workingMemory = getSerialisedStatefulSession( workingMemory );
        cheeseList = IteratorToList.convert( workingMemory.iterateObjects( new ClassObjectFilter( Cheese.class ) ) );
        assertEquals( 1,
                      cheeseList.size() );

        // europe=[ ], america=[ 1, 2, 3 ]
        p1.setStatus( "america" );
        c1FactHandle = getFactHandle( c1FactHandle, workingMemory );
        workingMemory.update( c1FactHandle,
                              p1 );
        workingMemory = getSerialisedStatefulSession( workingMemory );
        workingMemory.fireAllRules();
        workingMemory = getSerialisedStatefulSession( workingMemory );
        cheeseList = IteratorToList.convert( workingMemory.iterateObjects( new ClassObjectFilter( Cheese.class ) ) );
        assertEquals( 2,
                      cheeseList.size() );

        // europe=[ 2 ], america=[ 1, 3 ]
        p2.setStatus( "europe" );
        c2FactHandle = getFactHandle( c2FactHandle, workingMemory );
        workingMemory.update( c2FactHandle,
                              p2 );
        workingMemory = getSerialisedStatefulSession( workingMemory );
        workingMemory.fireAllRules();
        workingMemory = getSerialisedStatefulSession( workingMemory );
        cheeseList = IteratorToList.convert( workingMemory.iterateObjects( new ClassObjectFilter( Cheese.class ) ) );
        assertEquals( 1,
                      cheeseList.size() );

        // europe=[ 1, 2 ], america=[ 3 ]
        p1.setStatus( "europe" );
        c1FactHandle = getFactHandle( c1FactHandle, workingMemory );
        workingMemory.update( c1FactHandle,
                              p1 );
        workingMemory = getSerialisedStatefulSession( workingMemory );
        workingMemory.fireAllRules();
        workingMemory = getSerialisedStatefulSession( workingMemory );
        cheeseList = IteratorToList.convert( workingMemory.iterateObjects( new ClassObjectFilter( Cheese.class ) ) );
        assertEquals( 1,
                      cheeseList.size() );

        // europe=[ 1, 2, 3 ], america=[ ]
        p3.setStatus( "europe" );
        c3FactHandle = getFactHandle( c3FactHandle, workingMemory );
        workingMemory.update( c3FactHandle,
                              p3 );
        workingMemory = getSerialisedStatefulSession( workingMemory );
        workingMemory.fireAllRules();
        workingMemory = getSerialisedStatefulSession( workingMemory );
        cheeseList = IteratorToList.convert( workingMemory.iterateObjects( new ClassObjectFilter( Cheese.class ) ) );
        assertEquals( 2,
                      cheeseList.size() );
    }

    @Test
    public void testLogicalInsertions3() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_logicalInsertions3.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "events",
                                 list );

        // asserting the sensor object
        final Sensor sensor = new Sensor( 150,
                                          100 );
        FactHandle sensorHandle = workingMemory.insert( sensor );

        workingMemory.fireAllRules();
        workingMemory = getSerialisedStatefulSession( workingMemory );
        
        // alarm must sound
        assertEquals( 2,
                      list.size() );
        assertEquals( 2,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );

        // modifying sensor
        sensor.setTemperature( 125 );
        sensorHandle = getFactHandle( sensorHandle, workingMemory );
        workingMemory.update( sensorHandle,
                              sensor );
        workingMemory = getSerialisedStatefulSession( workingMemory );
        workingMemory.fireAllRules();
        workingMemory = getSerialisedStatefulSession( workingMemory );

        // alarm must continue to sound
        assertEquals( 3,
                      list.size() );
        assertEquals( 2,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );

        // modifying sensor
        sensor.setTemperature( 80 );
        sensorHandle = getFactHandle( sensorHandle, workingMemory );
        workingMemory.update( sensorHandle,
                              sensor );
        workingMemory = getSerialisedStatefulSession( workingMemory );
        workingMemory.fireAllRules();

        // no alarms anymore
        assertEquals( 3,
                      list.size() );
        assertEquals( 1,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );
    }

    @Test
    public void testLogicalInsertionsAccumulatorPattern() throws Exception {
        // JBRULES-449
        KnowledgeBase kbase = loadKnowledgeBase( "test_LogicalInsertionsAccumulatorPattern.drl" );
        kbase = SerializationHelper.serializeObject( kbase );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.setGlobal( "ga",
                            "a" );
        ksession.setGlobal( "gb",
                            "b" );
        ksession.setGlobal( "gs",
                            new Short( (short) 3 ) );

        ksession.fireAllRules();
        
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession, 
                                                                              true );

        FactHandle h = ksession.insert( new Integer( 6 ) );
        assertEquals( 1,
                      ksession.getObjects().size() );

        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession, 
                                                                                   true );
        assertEquals( "There should be 2 CheeseEqual in Working Memory, 1 justified, 1 stated",
                      2,
                      ksession.getObjects( new ClassObjectFilter( CheeseEqual.class ) ).size() );
        assertEquals( 6,
                      ksession.getObjects().size() );

        h = getFactHandle( h, ksession );
        ksession.retract( h );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession, 
                                                                              true );
        assertEquals( "There should be only 1 CheeseEqual in Working Memory, 1 stated (the justified should have been retracted). Check TruthMaintenanceSystem justifiedMap",
                      1,
                      ksession.getObjects( new ClassObjectFilter( CheeseEqual.class ) ).size() );
        assertEquals( 1,
                      ksession.getObjects( new ClassObjectFilter( Short.class ) ).size() );
        assertEquals( 2,
                      ksession.getObjects().size() );

        //clean-up
        ksession.fireAllRules();
        assertEquals( 0,
                      ksession.getObjects().size() );
    }

    @Test
    public void testLogicalInsertionsModifySameRuleGivesDifferentLogicalInsertion() throws Exception {
        // TODO JBRULES-1804

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_LogicalInsertionsModifySameRuleGivesDifferentLogicalInsertion.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();

        KnowledgeBase kbase = getKnowledgeBase();
        kbase.addKnowledgePackages( pkgs );
        kbase = SerializationHelper.serializeObject( kbase );
        StatefulKnowledgeSession session = createKnowledgeSession(kbase);

        Sensor sensor1 = new Sensor( 100,
                                     0 );
        FactHandle sensor1Handle = session.insert( sensor1 );
        Sensor sensor2 = new Sensor( 200,
                                     0 );
        FactHandle sensor2Handle = session.insert( sensor2 );
        Sensor sensor3 = new Sensor( 200,
                                     0 );
        FactHandle sensor3Handle = session.insert( sensor3 );

        session.fireAllRules();
        
        session = getSerialisedStatefulKnowledgeSession( session,
                                                         true );  

        List temperatureList = new ArrayList( session.getObjects( new ClassObjectFilter( Integer.class ) ) );
        assertTrue( temperatureList.contains( Integer.valueOf( 100 ) ) );
        assertTrue( temperatureList.contains( Integer.valueOf( 200 ) ) );
        assertEquals( 2,
                      temperatureList.size() );

        sensor1.setTemperature( 150 );
        sensor1Handle =  getFactHandle( (org.drools.FactHandle) sensor1Handle, session );
        session.update( sensor1Handle, sensor1 );

        session = getSerialisedStatefulKnowledgeSession( session,
                                                         true );  
        session.fireAllRules();

        temperatureList = new ArrayList( session.getObjects( new ClassObjectFilter( Integer.class ) ) );
        assertFalse( temperatureList.contains( Integer.valueOf( 100 ) ) );
        assertTrue( temperatureList.contains( Integer.valueOf( 150 ) ) );
        assertTrue( temperatureList.contains( Integer.valueOf( 200 ) ) );
        assertEquals( 2,
                      temperatureList.size() );
    }

    @Test
    public void testLogicalInsertOrder() throws Exception {
        // JBRULES-1602
        // "rule 1" is never logical inserted, as it's rule is unmatched prior to calling logical insert
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_LogicalInsertOrder.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        KnowledgeBase kbase = getKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        kbase = SerializationHelper.serializeObject( kbase );

        final StatefulKnowledgeSession session = createKnowledgeSession(kbase);
        WorkingMemoryEventListener wmel = mock( WorkingMemoryEventListener.class );
        session.addEventListener( wmel );
        
        Person bob = new Person( "bob" );
        bob.setStatus( "hungry" );
        Person mark = new Person( "mark" );
        mark.setStatus( "thirsty" );

        session.insert( bob );
        session.insert( mark );
        
        int count = session.fireAllRules();
        
        assertEquals( 2, count );

        ArgumentCaptor<ObjectInsertedEvent> insertsCaptor = ArgumentCaptor.forClass( ObjectInsertedEvent.class );
        verify( wmel, times(3) ).objectInserted( insertsCaptor.capture() );
        List<ObjectInsertedEvent> inserts = insertsCaptor.getAllValues();
        assertThat( inserts.get( 0 ).getObject(), is(  (Object) bob ) );
        assertThat( inserts.get( 1 ).getObject(), is(  (Object) mark) );
        assertThat( inserts.get( 2 ).getObject(), is( (Object) "rule 2" ) );
        
        ArgumentCaptor<ObjectRetractedEvent> retractsCaptor = ArgumentCaptor.forClass( ObjectRetractedEvent.class );
        verify( wmel, times(1) ).objectRetracted( retractsCaptor.capture() );
        List<ObjectRetractedEvent> retracts = retractsCaptor.getAllValues();
        assertThat( retracts.get( 0 ).getOldObject(), is( (Object) "rule 2" ) );
    }
    
    @Test
    public void testTMSwithQueries() {
        String str =""+
                "package org.drools.test;\n" +
                "\n" +
                "global java.util.List list; \n" +
                "\n" +
                "declare Bean\n" +
                "    str : String\n" +
                "end\n" +
                "\n" +
                "query bean ( String $s )\n" +
                "    Bean(  $s ; )\n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule \"init\"\n" +
                "when\n" +
                "then\n" +
                "    insert( new Bean(\"AAA\") );\n" +
                "    insert( \"x\" );\n" +
                "end\n" +
                "\n" +
                "rule \"LogicIn\"\n" +
                "when\n" +
                "    String( this == \"x\" )\n" +
                "    ?bean(  \"AAA\" ; )\n" +
                "then\n" +
                "    insertLogical(\"y\");\n" +
                "    retract(\"x\");\n" +
                "end " +
                "\n" +
                "rule \"Never\"\n" +
                "salience -999\n" +
                "when\n" +
                "    $s : String( this == \"y\" )\n" +
                "then\n" +
                "    list.add($s);\n" +
                "end";
 
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()),
                ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession kSession = createKnowledgeSession(kbase);
 
        List list = new ArrayList();
        kSession.setGlobal("list",list);
 
 
        kSession.fireAllRules();
        assertEquals(0,list.size());
 
        //System.err.println(reportWMObjects(kSession));
    }

    @Test @Ignore
    public void testTMSWithLateUpdate() {
        // This is not actually fixable, as noted here, JBRULES-3416
        // facts must be updated, before changing other facts, as they act as HEAD in buckets.
        // leaving test here as @ignore here for future reference.
        String str =""+
                "package org.drools.test;\n" +
                "\n" +
                "import org.drools.Father;\n" +
                "import org.drools.YoungestFather;\n" +
                "\n" +
                "rule \"findMarriedCouple\"\n" +
                "when\n" +
                "    $h: Father()\n" +
                "    not Father(father == $h)\n" +
                "then\n" +
                "    insertLogical(new YoungestFather($h));\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()),
                ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession kSession = createKnowledgeSession(kbase);

        Father abraham = new Father("abraham");
        Father bart = new Father("bart");
        Collection<Object> youngestFathers;

        bart.setFather(abraham);
        FactHandle abrahamHandle = kSession.insert(abraham);
        FactHandle bartHandle = kSession.insert(bart);
        kSession.fireAllRules();
        
        youngestFathers = kSession.getObjects( new ClassObjectFilter(YoungestFather.class) );
        assertEquals( 1, youngestFathers.size() );
        assertEquals( bart, ((YoungestFather) youngestFathers.iterator().next()).getMan() );

        Father homer = new Father("homer");
        FactHandle homerHandle = kSession.insert(homer);

        homer.setFather(abraham);
        // If we do kSession.update(homerHandle, homer) here instead of after bart.setFather(homer) it works
        // But in some use cases we cannot do this because fact fields are actually called
        // while the facts are in an invalid temporary state
        bart.setFather(homer);
        // Late update call for homer, after bart has been changed too, but before fireAllRules
        kSession.update(homerHandle, homer);
        kSession.update(bartHandle, bart);
        kSession.fireAllRules();
        
        youngestFathers = kSession.getObjects( new ClassObjectFilter(YoungestFather.class) );
        assertEquals(bart, ((YoungestFather) youngestFathers.iterator().next()).getMan());
        assertEquals(1, youngestFathers.size());

        //System.err.println(reportWMObjects(kSession));
    }
    
    public InternalFactHandle getFactHandle(FactHandle factHandle,
                                            StatefulSession session) {
        Map<Integer, FactHandle> handles = new HashMap<Integer, FactHandle>();
        for ( FactHandle fh : session.getFactHandles() ) {
            handles.put( ((InternalFactHandle) fh).getId(),
                         fh );
        }
        return (InternalFactHandle) handles.get( ((InternalFactHandle) factHandle).getId() );
    }     
    
    public InternalFactHandle getFactHandle(FactHandle factHandle,
                                            StatefulKnowledgeSession ksession) {
        Map<Integer, FactHandle> handles = new HashMap<Integer, FactHandle>();
        for ( FactHandle fh : ksession.getFactHandles() ) {
            handles.put( ((InternalFactHandle) fh).getId(),
                         fh );
        }
        return (InternalFactHandle) handles.get( ((InternalFactHandle) factHandle).getId() );
    }    

}
