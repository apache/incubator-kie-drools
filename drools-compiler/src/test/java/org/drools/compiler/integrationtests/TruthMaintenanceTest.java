package org.drools.compiler.integrationtests;

import static org.drools.compiler.integrationtests.SerializationHelper.getSerialisedStatefulKnowledgeSession;
import static org.drools.compiler.integrationtests.SerializationHelper.getSerialisedStatefulSession;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.Cheese;
import org.drools.compiler.CheeseEqual;
import org.drools.core.ClassObjectFilter;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Father;
import org.drools.compiler.Person;
import org.drools.core.RuleBase;
import org.drools.compiler.Sensor;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.StatefulSession;
import org.drools.core.WorkingMemory;
import org.drools.compiler.YoungestFather;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.ObjectHashMap;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.Package;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.WorkingMemoryEventListener;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.internal.logger.KnowledgeRuntimeLogger;
import org.kie.api.runtime.rule.FactHandle;
import org.mockito.ArgumentCaptor;

public class TruthMaintenanceTest extends CommonTestMethodBase {

    @Test
    public void testLogicalInsertionsDynamicRule() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource("test_LogicalInsertionsDynamicRule.drl",
                getClass()),
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
        kbase = SerializationHelper.serializeObject(kbase);

        ksession.fireAllRules();
        
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );        

        kbase = ksession.getKieBase();

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
        assertEquals( 3, kbase.getKnowledgePackages().size() );
        KnowledgePackage test = null, test2 = null;
        // different JVMs return the package list in different order
        for( KnowledgePackage kpkg : kbase.getKnowledgePackages() ) {
            if( kpkg.getName().equals( "org.drools.compiler.test" )) {
                test = kpkg;
            } else if( kpkg.getName().equals( "org.drools.compiler.test2" )) {
                test2 = kpkg;
            }
        }
        
        assertNotNull( test );
        assertNotNull( test2 );
        assertEquals( "rule1",
                      test.getRules().iterator().next().getName() );
        assertEquals( "rule2",
                      test2.getRules().iterator().next().getName() );

        // now remove the first rule
        kbase.removeRule( test.getName(),
                          test.getRules().iterator().next().getName() );
        // different JVMs return the package list in different order
        for( KnowledgePackage kpkg : kbase.getKnowledgePackages() ) {
            if( kpkg.getName().equals( "org.drools.compiler.test" )) {
                test = kpkg;
            } else if( kpkg.getName().equals( "org.drools.compiler.test2" )) {
                test2 = kpkg;
            }
        }
        assertNotNull( test );
        assertNotNull( test2 );

        // Check the rule was correctly remove
        assertEquals( 0,
                      test.getRules().size() );
        assertEquals( 1,
                      test2.getRules().size() );
        assertEquals( "rule2",
                      test2.getRules().iterator().next().getName() );

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
        ksession.fireAllRules();
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

        // different JVMs return the package list in different order
        for( KnowledgePackage kpkg : kbase.getKnowledgePackages() ) {
            if( kpkg.getName().equals( "org.drools.compiler.test" )) {
                test = kpkg;
            } else if( kpkg.getName().equals( "org.drools.compiler.test2" )) {
                test2 = kpkg;
            }
        }
        assertNotNull( test );
        assertNotNull( test2 );

        kbase.removeRule( test2.getName(),
                          test2.getRules().iterator().next().getName() );
        kbase = SerializationHelper.serializeObject(kbase);
        
        // different JVMs return the package list in different order
        for( KnowledgePackage kpkg : kbase.getKnowledgePackages() ) {
            if( kpkg.getName().equals( "org.drools.compiler.test" )) {
                test = kpkg;
            } else if( kpkg.getName().equals( "org.drools.compiler.test2" )) {
                test2 = kpkg;
            }
        }
        assertNotNull( test );
        assertNotNull( test2 );
        
        assertEquals( 0,
                      test.getRules().size() );
        assertEquals( 0,
                      test2.getRules().size() );
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
        kbase = SerializationHelper.serializeObject(kbase);
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

        System.out.println( list );
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
        session.fireAllRules();

        assertEquals(0,
                     session.getObjects().size());
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
        kbase = SerializationHelper.serializeObject(kbase);
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
        kbase = SerializationHelper.serializeObject(kbase);
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
        kbase = SerializationHelper.serializeObject(kbase);
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
        ruleBase = SerializationHelper.serializeObject(ruleBase);
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
        ruleBase = SerializationHelper.serializeObject(ruleBase);
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

        TruthMaintenanceSystem tms =  ((NamedEntryPoint)workingMemory.getWorkingMemoryEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();

        final java.lang.reflect.Field field = tms.getClass().getDeclaredField( "equalityKeyMap" );
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
        ruleBase = SerializationHelper.serializeObject(ruleBase);
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

        TruthMaintenanceSystem tms =  ((NamedEntryPoint)workingMemory.getWorkingMemoryEntryPoint(EntryPointId.DEFAULT.getEntryPointId()) ).getTruthMaintenanceSystem();
        assertTrue(tms.getEqualityKeyMap().isEmpty());
    }

    @Test
    public void testLogicalInsertionsNot() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalInsertionsNot.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject(ruleBase);
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
        workingMemory.fireAllRules();
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
        ruleBase = SerializationHelper.serializeObject(ruleBase);
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
        // calling update on a justified FH, states it
        
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalInsertionsUpdateEqual.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject(ruleBase);
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
        
        
        List list = IteratorToList.convert( workingMemory.iterateObjects() );
        // CheeseEqual was updated, making it stated, so it wouldn't have been logically retracted
        assertEquals( 1,
                      list.size() );
        assertEquals( new CheeseEqual("person", 3), list.get( 0 ));
        FactHandle fh = workingMemory.getFactHandle( list.get(0) );
        workingMemory.retract( fh );
        
        list = IteratorToList.convert( workingMemory.iterateObjects() );
        assertEquals( 0,
                      list.size() );        
        
        TruthMaintenanceSystem tms =  ((NamedEntryPoint)workingMemory.getWorkingMemoryEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();

        final java.lang.reflect.Field field = tms.getClass().getDeclaredField( "equalityKeyMap" );
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
        ruleBase = SerializationHelper.serializeObject(ruleBase);
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
        ruleBase = SerializationHelper.serializeObject(ruleBase);
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
        kbase = SerializationHelper.serializeObject(kbase);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.setGlobal( "ga",
                            "a" );
        ksession.setGlobal( "gb",
                            "b" );
        ksession.setGlobal( "gs",
                            new Short( (short) 3 ) );

        ksession.fireAllRules();
        
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);

        FactHandle h = ksession.insert( new Integer( 6 ) );
        assertEquals( 1,
                      ksession.getObjects().size() );

        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        assertEquals( "There should be 2 CheeseEqual in Working Memory, 1 justified, 1 stated",
                      2,
                      ksession.getObjects( new ClassObjectFilter( CheeseEqual.class ) ).size() );
        assertEquals( 6,
                      ksession.getObjects().size() );

        h = getFactHandle( h, ksession );
        ksession.retract( h );
        ksession.fireAllRules();

        for ( Object o : ksession.getObjects() ) {
            System.out.println( o );
        }

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        assertEquals( 0,
                      ksession.getObjects( new ClassObjectFilter( CheeseEqual.class ) ).size() );
        assertEquals( 0,
                      ksession.getObjects( new ClassObjectFilter( Short.class ) ).size() );
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
        kbase = SerializationHelper.serializeObject(kbase);
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
        sensor1Handle =  getFactHandle( (org.drools.core.FactHandle) sensor1Handle, session );
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
        kbase = SerializationHelper.serializeObject(kbase);

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

        assertEquals(2, session.getObjects().size());

        TruthMaintenanceSystem tms =  ((NamedEntryPoint)session.getEntryPoint(EntryPointId.DEFAULT.getEntryPointId()) ).getTruthMaintenanceSystem();
        assertTrue(tms.getEqualityKeyMap().isEmpty());
    }
    
    @Test
    public void testTMSwithQueries() {
        String str =""+
                "package org.drools.compiler.test;\n" +
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

    @Test
    public void testTMSWithLateUpdate() {
        //  JBRULES-3416
        if( CommonTestMethodBase.phreak == RuleEngineOption.RETEOO ) {
            return;  // Feature can never work in Rete mode.
        }

        String str =""+
                "package org.drools.compiler.test;\n" +
                "\n" +
                "import org.drools.compiler.Father;\n" +
                "import org.drools.compiler.YoungestFather;\n" +
                "\n" +
                "rule \"findMarriedCouple\"\n" +
                "when\n" +
                "    $h: Father()\n" +
                "    not Father(father == $h)\n" +
                "then\n" +
                "    insertLogical(new YoungestFather($h));\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        StatefulKnowledgeSession kSession = createKnowledgeSession(kbase);

        Father abraham = new Father("abraham");
        Father bart = new Father("bart");
        Collection<? extends Object> youngestFathers;

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
        assertEquals(1, youngestFathers.size());
        assertEquals(bart, ((YoungestFather) youngestFathers.iterator().next()).getMan());


        //System.err.println(reportWMObjects(kSession));
    }
    
    @Test
    public void testTMSAdditionalValueArgument() {
        String str =""+
                "package org.drools.compiler.test;\n" +
                "\n" +
                "global String key \n" + 
                "\n" +
                "rule \"r1\" salience 10\n" +
                "when\n" +
                "then\n" +
                "    insertLogical(key, \"value1\");\n" +
                "end\n" +
                "rule \"r2\"\n" +
                "when\n" +
                "then\n" +
                "    insertLogical(key, \"value2\");\n" +
                "end\n" +                
                "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()),
                ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession kSession = createKnowledgeSession(kbase);
        String key = "key";
        kSession.setGlobal( "key", key );
        
        kSession.fireAllRules();
                        
        TruthMaintenanceSystem tms = ((NamedEntryPoint)((StatefulKnowledgeSessionImpl)kSession).session.getWorkingMemoryEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();
        
        InternalFactHandle fh = ( InternalFactHandle ) kSession.getFactHandle( key );
        
        BeliefSet bs =  fh.getEqualityKey().getBeliefSet();
        
        assertEquals( "value1", ((LogicalDependency) ((LinkedListEntry)bs.getFirst()).getObject()).getValue() );
        assertEquals( "value2", ((LogicalDependency) ((LinkedListEntry)bs.getFirst().getNext()).getObject()).getValue() );        
    }    
    
    public class IntervalRequirement
    {
        private int interval;
        private int staffingRequired;
        
        public IntervalRequirement(int interval, int staffingRequired) {
            super();
            this.interval = interval;
            this.staffingRequired = staffingRequired;
        }
        
        public int getInterval() {
            return interval;
        }

        public int getStaffingRequired() {
            return staffingRequired;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getClass().getSimpleName()).append(": ")
                .append("interval: ").append(this.interval)
                .append(", staffingRequired: ").append(this.staffingRequired)
                ;
            return sb.toString();
        }
    }

    public class ShiftAssignment
    {
        private int shiftStartTime = -1;
        private int shiftEndTime = -1;

        public ShiftAssignment() {
        }
        
        public int getShiftStartTime() {
            return this.shiftStartTime;
        }
        
        public int getShiftEndTime() {
            return this.shiftEndTime;
        }
        
        public void setShiftStartTime(int shiftStartTime) {
            this.shiftStartTime = shiftStartTime;
        }

        public void setShiftEndTime(int shiftEndTime) {
            this.shiftEndTime = shiftEndTime;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ShiftAssignment: ")
                .append(" ")
                .append("start: ").append(this.shiftStartTime).append(" end: ").append(this.shiftEndTime);
            return sb.toString();
        }
    }
    
    @Test
    public void testRepetitiveUpdatesOnSameFacts() throws Exception {
        // JBRULES-3320
        // Using the concept of shift assignments covering interval requirements (staffing required for a given interval)
        List notCovered = new ArrayList();          // Interval requirements not covered by any shift assignments
        List partiallyCovered = new ArrayList();    // Interval requirements partially covered by shift assignments (staffing requirement partially met)
        List totallyCovered = new ArrayList();      // Interval requirements totally covered by shift assignments (staffing requirement met or exceeded)

        // load up the knowledge base
        KnowledgeBase kbase = loadKnowledgeBase( "test_RepetitiveUpdatesOnSameFacts.drl" );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.setGlobal("totallyCovered", totallyCovered);
        ksession.setGlobal("partiallyCovered", partiallyCovered);
        ksession.setGlobal("notCovered", notCovered);

        KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
        
        // Using 4 IntervalRequirement objects that never change during the execution of the test
        // Staffing required at interval 100
        IntervalRequirement ir100 = new IntervalRequirement(100, 2);
        ksession.insert(ir100);
        // Staffing required at interval 101
        IntervalRequirement ir101 = new IntervalRequirement(101, 2);
        ksession.insert(ir101);
        // Staffing required at interval 102
        IntervalRequirement ir102 = new IntervalRequirement(102, 2);
        ksession.insert(ir102);
        // Staffing required at interval 103
        IntervalRequirement ir103 = new IntervalRequirement(103, 2);
        ksession.insert(ir103);

        // Using a single ShiftAssignment object that will get updated multiple times during the execution of the test
        ShiftAssignment sa = new ShiftAssignment();
        sa.setShiftStartTime(100);

        FactHandle saHandle = null;
    
        // Intersects 1 interval
        totallyCovered.clear();
        partiallyCovered.clear();
        notCovered.clear();
        sa.setShiftEndTime(101);
        System.out.println("ShiftAssignment set from " + sa.getShiftStartTime() + " to " + sa.getShiftEndTime());
        saHandle = ksession.insert(sa);
        ksession.fireAllRules();
        assertEquals("notCovered with " + sa, 3, notCovered.size());
        assertEquals("totallyCovered with " + sa, 0, totallyCovered.size());
        assertEquals("partiallyCovered with " + sa, 1, partiallyCovered.size());
        
        // Intersects 3 intervals
        totallyCovered.clear();
        partiallyCovered.clear();
        notCovered.clear();
        sa.setShiftEndTime(103);
        System.out.println("ShiftAssignment set from " + sa.getShiftStartTime() + " to " + sa.getShiftEndTime());
        ksession.update(saHandle, sa);
        ksession.fireAllRules();
        assertEquals("notCovered with " + sa, 0, notCovered.size()); // this was fired in the previous scenario
        assertEquals("totallyCovered with " + sa, 0, totallyCovered.size());
        assertEquals("partiallyCovered with " + sa, 3, partiallyCovered.size());
        
        // Intersects 2 intervals
        totallyCovered.clear();
        partiallyCovered.clear();
        notCovered.clear();
        sa.setShiftEndTime(102);
        System.out.println("ShiftAssignment set from " + sa.getShiftStartTime() + " to " + sa.getShiftEndTime());
        ksession.update(saHandle, sa);
        ksession.fireAllRules();
        assertEquals("notCovered with " + sa, 1, notCovered.size()); // new uncovered scenario
        assertEquals("totallyCovered with " + sa, 0, totallyCovered.size());
        assertEquals("partiallyCovered with " + sa, 2, partiallyCovered.size());
        
        // Intersects 4 intervals
        totallyCovered.clear();
        partiallyCovered.clear();
        notCovered.clear();
        sa.setShiftEndTime(104);
        System.out.println("ShiftAssignment set from " + sa.getShiftStartTime() + " to " + sa.getShiftEndTime());
        ksession.update(saHandle, sa);
        ksession.fireAllRules();
        assertEquals("notCovered with " + sa, 0, notCovered.size());
        assertEquals("totallyCovered with " + sa, 0, totallyCovered.size());
        assertEquals("partiallyCovered with " + sa, 4, partiallyCovered.size());
        
        // Intersects 1 interval
        totallyCovered.clear();
        partiallyCovered.clear();
        notCovered.clear();
        sa.setShiftEndTime(101);
        System.out.println("ShiftAssignment set from " + sa.getShiftStartTime() + " to " + sa.getShiftEndTime());
        ksession.update(saHandle, sa);
        ksession.fireAllRules();
        assertEquals("notCovered with " + sa, 3, notCovered.size());
        assertEquals("totallyCovered with " + sa, 0, totallyCovered.size());
        assertEquals("partiallyCovered with " + sa, 1, partiallyCovered.size());
        
        ksession.dispose();
        logger.close();
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

    public static class HashBrown {
        private int num;

        public HashBrown(int num) {
            this.num = num;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            HashBrown hashBrown = (HashBrown) o;
            if (num != hashBrown.num) return false;
            return true;
        }

        @Override
        public int hashCode() {
            return 31 + num;
        }
    }

    public static class HashBlack extends HashBrown {
        public HashBlack(int num) {
            super(num);
        }
    }

    @Test
    public void testTMSWithEquivalentSubclasses() {
        String droolsSource =
                "package project_java_rules2_xxx \n" +
                "import " + HashBrown.class.getCanonicalName() + "; \n" +

                "declare Foo id : int @key end \n\n" +

                "rule Zero \n" +
                "when \n" +
                " $s : String( this == \"go\" ) \n" +
                "then \n" +
                " insertLogical( new HashBrown(1) ); \n" +
                "end \n" +

                "rule Init \n" +
                "when \n" +
                "then \n" +
                " insertLogical( new HashBrown(7) ); \n" +
                "end \n" ;

        /////////////////////////////////////

        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( new ByteArrayResource( droolsSource.getBytes() ), ResourceType.DRL );
        assertFalse(kBuilder.getErrors().toString(), kBuilder.hasErrors());

        final RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setAssertBehaviour( RuleBaseConfiguration.AssertBehaviour.EQUALITY );
        conf.setSequentialAgenda( RuleBaseConfiguration.SequentialAgenda.SEQUENTIAL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( conf );
        kbase.addKnowledgePackages( kBuilder.getKnowledgePackages() );

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        session.fireAllRules();

        FactHandle handle = session.insert( new HashBlack( 1 ) );
        session.insert( "go" );
        session.fireAllRules();

        assertNotNull( ((DefaultFactHandle) handle).getEqualityKey() );
        session.dispose();
    }



    @Test
    public void testRestateJustified() {
        String droolsSource =
                "package org.drools.tms.test; \n" +

                "declare Foo id : int @key end \n\n" +

                "rule Zero \n" +
                "when \n" +
                " $s : String( this == \"go\" ) \n" +
                "then \n" +
                " insertLogical( new Foo(1) ); \n" +
                "end \n" +

                "rule Restate \n" +
                "when \n" +
                " $s : String( this == \"go2\" ) \n" +
                " $f : Foo( id == 1 ) \n" +
                "then \n" +
                " insert( $f ); \n" +
                "end \n" ;

        /////////////////////////////////////


        StatefulKnowledgeSession session = loadKnowledgeBaseFromString( droolsSource ).newStatefulKnowledgeSession();

        session.fireAllRules();
        FactHandle handle = session.insert( "go" );
        session.fireAllRules();

        FactHandle handle2 = session.insert( "go2" );
        session.fireAllRules();

        session.delete( handle );
        session.delete( handle2 );
        session.fireAllRules();

        assertEquals( 1, session.getObjects().size() );
        session.dispose();
    }

}

