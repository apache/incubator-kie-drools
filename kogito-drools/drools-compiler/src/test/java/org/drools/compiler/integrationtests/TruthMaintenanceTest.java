/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import org.drools.compiler.Cheese;
import org.drools.compiler.CheeseEqual;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Father;
import org.drools.compiler.Person;
import org.drools.compiler.Sensor;
import org.drools.compiler.YoungestFather;
import org.drools.core.ClassObjectFilter;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.rule.EntryPointId;
import org.drools.core.util.ObjectHashMap;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.ConsequenceException;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.logger.KnowledgeRuntimeLogger;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.drools.compiler.integrationtests.SerializationHelper.getSerialisedStatefulKnowledgeSession;
import static org.mockito.Mockito.mock;

public class TruthMaintenanceTest extends CommonTestMethodBase {

    @Test(timeout=10000)
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
        assertTrue( "c1's logical assertion should not be deleted",
                    list.contains( new Person( c1.getType() ) ) );
        assertTrue( "c2's logical assertion should  not be deleted",
                    list.contains( new Person( c2.getType() ) ) );
        assertFalse( "c3's logical assertion should be  deleted",
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
        assertFalse( "The logical assertion cor c2 should have been deleted",
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

    @Test(timeout=10000)
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

    @Test(timeout=10000)
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
        assertEquals( "cheese-type " + cheese1.getType() + " was deleted, but should not. Backed by cheese2 => type.",
                      1,
                      list.size() );
        assertEquals( "cheese-type " + cheese1.getType() + " was deleted, but should not. Backed by cheese2 => type.",
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
        assertEquals( "cheese-type " + cheese1.getType() + " was not deleted, but should have. Neither  cheese1 => type nor cheese2 => type is true.",
                      0,
                      list.size() );
    }

    @Test //(timeout=10000)
    //@Ignore
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
        assertEquals( "b was deleted, but it should not have. Is backed by b => b being true.",
                      1,
                      list.size() );
        assertEquals( "b was deleted, but it should not have. Is backed by b => b being true.",
                      b,
                      list.iterator().next() );

        h1 = session.getFactHandle( b );
        assertSame( ((InternalFactHandle)h1).getEqualityKey().getLogicalFactHandle(), h1);
        ((StatefulKnowledgeSessionImpl)session).getTruthMaintenanceSystem().delete( h1 );
        session.fireAllRules();
        list = session.getObjects( new ClassObjectFilter( a.getClass() ) );
        assertEquals( 0,
                      list.size() );
    }

    @Test(timeout=10000)
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

    @Test //(timeout=10000)
    public void testLogicalInsertionsNoLoop() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LogicalInsertionsNoLoop.drl");
        KieSession ksession = kbase.newKieSession();

        final List l = new ArrayList();
        final Person a = new Person( "a" );
        ksession.setGlobal( "a", a );
        ksession.setGlobal( "l", l );

        ksession.fireAllRules();
        assertEquals("a still in WM",
                     0,
                     ksession.getObjects(new ClassObjectFilter(a.getClass())).size());
        assertEquals( "Rule should not loop",
                      1,
                      l.size() );
    }

    @Test(timeout=10000)
    //@Ignore("in Java 8, the byte[] generated by serialization are not the same and requires investigation")
    public void testLogicalInsertionsWithModify() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LogicalInsertionsWithUpdate.drl");
        KieSession ksession = kbase.newKieSession();

        final Person p = new Person( "person" );
        p.setAge( 2 );
        FactHandle h = ksession.insert( p );
        assertEquals(1,
                     ksession.getObjects().size());

        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, false);
        assertEquals( 2,
                      ksession.getObjects().size() );

        Collection l = ksession.getObjects( new ClassObjectFilter( CheeseEqual.class ) );
        assertEquals( 1,
                      l.size() );
        assertEquals( 2,
                      ((CheeseEqual) l.iterator().next()).getPrice() );

        h = getFactHandle( h, ksession );
        ksession.retract( h );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, false);
        assertEquals( 0,
                      ksession.getObjects().size() );

        TruthMaintenanceSystem tms =  ((NamedEntryPoint)ksession.getEntryPoint(EntryPointId.DEFAULT.getEntryPointId()) ).getTruthMaintenanceSystem();

        final java.lang.reflect.Field field = tms.getClass().getDeclaredField( "equalityKeyMap" );
        field.setAccessible( true );
        final ObjectHashMap m = (ObjectHashMap) field.get( tms );
        field.setAccessible( false );
        assertEquals( "assertMap should be empty",
                      0,
                      m.size() );
    }

    @Test(timeout=10000)
    public void testLogicalInsertions2() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LogicalInsertions2.drl");
        KieSession ksession = kbase.newKieSession();

        final List events = new ArrayList();

        ksession.setGlobal( "events", events );

        final Sensor sensor = new Sensor( 80,
                                          80 );
        FactHandle handle = ksession.insert( sensor );

        // everything should be normal

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession, false );
        ksession.fireAllRules();

        final Collection list = ksession.getObjects();

        assertEquals( "Only sensor is there",
                      1,
                      list.size() );
        assertEquals( "Only one event",
                      1,
                      events.size() );

        // problems should be detected
        sensor.setPressure( 200 );
        sensor.setTemperature( 200 );
        
        handle = getFactHandle( handle, ksession );
        ksession.update( handle, sensor );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession, true );

        ksession.fireAllRules();
        //        logger.writeToDisk();

        assertEquals( "Only sensor is there",
                      1,
                      list.size() );

        TruthMaintenanceSystem tms =  ((NamedEntryPoint)ksession.getEntryPoint(EntryPointId.DEFAULT.getEntryPointId()) ).getTruthMaintenanceSystem();
        assertTrue(tms.getEqualityKeyMap().isEmpty());
    }

    @Test(timeout=10000)
    //@Ignore("in Java 8, the byte[] generated by serialization are not the same and requires investigation")
    public void testLogicalInsertionsNot() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LogicalInsertionsNot.drl");
        KieSession ksession = kbase.newKieSession();

        final Person a = new Person( "a" );
        final Cheese cheese = new Cheese( "brie",
                                          1 );
        ksession.setGlobal( "cheese",
                                 cheese );

        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        Collection list = ksession.getObjects();
        assertEquals( "i was not asserted by not a => i.",
                      1,
                      list.size() );
        assertEquals( "i was not asserted by not a => i.",
                      cheese,
                      list.iterator().next() );

        FactHandle h = ksession.insert( a );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        // no need to fire rules, assertion alone removes justification for i,
        // so it should be deleted.
        // workingMemory.fireAllRules();
        ksession.fireAllRules();
        list = ksession.getObjects();

        assertEquals( "a was not asserted or i not deleted.",
                      1,
                      list.size() );
        assertEquals( "a was asserted.",
                      a,
                      list.iterator().next() );
        assertFalse( "i was not rectracted.",
                     list.contains( cheese ) );

        // no rules should fire, but nevertheless...
        // workingMemory.fireAllRules();
        assertEquals("agenda should be empty.",
                     0,
                     ((InternalAgenda)((StatefulKnowledgeSessionImpl) ksession).getAgenda()).agendaSize());

        h = getFactHandle( h, ksession );
        ksession.retract( h );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        list = ksession.getObjects();
        assertEquals( "i was not asserted by not a => i.",
                      1,
                      list.size() );
        assertEquals( "i was not asserted by not a => i.",
                      cheese,
                      list.iterator().next() );
    }

    @Test(timeout=10000)
    public void testLogicalInsertionsNotPingPong() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LogicalInsertionsNotPingPong.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();

        final Person person = new Person( "person" );
        final Cheese cheese = new Cheese( "cheese",
                                          0 );
        ksession.setGlobal( "cheese", cheese );
        ksession.setGlobal( "person", person );
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        // not sure about desired state of working memory.
        assertEquals( "Rules have not fired (looped) expected number of times",
                      10,
                      list.size() );
    }

    @Test(timeout=10000)
    @Ignore("Currently cannot support updates")
    //@Ignore("in Java 8, the byte[] generated by serialization are not the same and requires investigation")
    public void testLogicalInsertionsUpdateEqual() throws Exception {
        // calling update on a justified FH, states it
        KieBase kbase = loadKnowledgeBase("test_LogicalInsertionsUpdateEqual.drl");
        KieSession ksession = kbase.newKieSession();

        final Person p = new Person( "person" );
        p.setAge( 2 );
        FactHandle h = ksession.insert( p );
        assertEquals(1,
                     ksession.getObjects().size());

        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        assertEquals( 2,
                      ksession.getObjects().size() );
        Collection l = ksession.getObjects( new ClassObjectFilter( CheeseEqual.class ) );
        assertEquals( 1,
                      l.size() );
        assertEquals( 3,
                      ((CheeseEqual) l.iterator().next()).getPrice() );

        h = getFactHandle( h, ksession );
        ksession.retract( h );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);


        Collection list = ksession.getObjects();
        // CheeseEqual was updated, making it stated, so it wouldn't have been logically deleted
        assertEquals( 1,
                      list.size() );
        assertEquals( new CheeseEqual("person", 3), list.iterator().next());
        FactHandle fh = ksession.getFactHandle( list.iterator().next() );
        ksession.retract( fh );
        
        list = ksession.getObjects();
        assertEquals( 0,
                      list.size() );        
        
        TruthMaintenanceSystem tms =  ((NamedEntryPoint)ksession.getEntryPoint(EntryPointId.DEFAULT.getEntryPointId()) ).getTruthMaintenanceSystem();

        final java.lang.reflect.Field field = tms.getClass().getDeclaredField( "equalityKeyMap" );
        field.setAccessible( true );
        final ObjectHashMap m = (ObjectHashMap) field.get( tms );
        field.setAccessible( false );
        assertEquals( "assertMap should be empty",
                      0,
                      m.size() );
    }

    @Test(timeout=10000)
    //@Ignore("in Java 8, the byte[] generated by serialization are not the same and requires investigation")
    public void testLogicalInsertionsWithExists() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LogicalInsertionWithExists.drl");
        KieSession ksession = kbase.newKieSession();

        final Person p1 = new Person( "p1",
                                      "stilton",
                                      20 );
        p1.setStatus( "europe" );
        FactHandle c1FactHandle = ksession.insert( p1 );
        final Person p2 = new Person( "p2",
                                      "stilton",
                                      30 );
        p2.setStatus( "europe" );
        FactHandle c2FactHandle = ksession.insert( p2 );
        final Person p3 = new Person( "p3",
                                      "stilton",
                                      40 );
        p3.setStatus( "europe" );
        FactHandle c3FactHandle = ksession.insert( p3 );
        ksession.fireAllRules();

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

        // all 3 in europe, so, 2 cheese
        Collection cheeseList = ksession.getObjects(new ClassObjectFilter(Cheese.class));
        assertEquals( 2,
                      cheeseList.size() );

        // europe=[ 1, 2 ], america=[ 3 ]
        p3.setStatus( "america" );
        c3FactHandle = getFactHandle( c3FactHandle, ksession );
        ksession.update( c3FactHandle,
                              p3 );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        cheeseList = ksession.getObjects(new ClassObjectFilter(Cheese.class));
        assertEquals( 1,
                      cheeseList.size() );

        // europe=[ 1 ], america=[ 2, 3 ]
        p2.setStatus( "america" );
        c2FactHandle = getFactHandle( c2FactHandle, ksession );
        ksession.update( c2FactHandle,
                              p2 );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        cheeseList = ksession.getObjects(new ClassObjectFilter(Cheese.class));
        assertEquals( 1,
                      cheeseList.size() );

        // europe=[ ], america=[ 1, 2, 3 ]
        p1.setStatus( "america" );
        c1FactHandle = getFactHandle( c1FactHandle, ksession );
        ksession.update( c1FactHandle,
                              p1 );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        cheeseList = ksession.getObjects(new ClassObjectFilter(Cheese.class));
        assertEquals( 2,
                      cheeseList.size() );

        // europe=[ 2 ], america=[ 1, 3 ]
        p2.setStatus( "europe" );
        c2FactHandle = getFactHandle( c2FactHandle, ksession );
        ksession.update( c2FactHandle,
                              p2 );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        cheeseList = ksession.getObjects(new ClassObjectFilter(Cheese.class));
        assertEquals( 1,
                      cheeseList.size() );

        // europe=[ 1, 2 ], america=[ 3 ]
        p1.setStatus( "europe" );
        c1FactHandle = getFactHandle( c1FactHandle, ksession );
        ksession.update( c1FactHandle,
                              p1 );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        cheeseList = ksession.getObjects(new ClassObjectFilter(Cheese.class));
        assertEquals( 1,
                      cheeseList.size() );

        // europe=[ 1, 2, 3 ], america=[ ]
        p3.setStatus( "europe" );
        c3FactHandle = getFactHandle( c3FactHandle, ksession );
        ksession.update( c3FactHandle,
                              p3 );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        cheeseList = ksession.getObjects(new ClassObjectFilter(Cheese.class));
        assertEquals( 2,
                      cheeseList.size() );
    }

    @Test //(timeout=10000)
    public void testLogicalInsertions3() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_logicalInsertions3.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal( "events", list );

        // asserting the sensor object
        final Sensor sensor = new Sensor( 150, 100 );
        FactHandle sensorHandle = ksession.insert( sensor );

        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession, true );
        
        // alarm must sound
        assertEquals( 2,
                      list.size() );
        assertEquals(2,
                     ksession.getObjects().size());

        // modifying sensor
        sensor.setTemperature( 125 );
        sensorHandle = getFactHandle( sensorHandle, ksession );
        ksession.update( sensorHandle,
                              sensor );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

        // alarm must continue to sound
        assertEquals( 3,
                      list.size() );
        assertEquals( 2,
                      ksession.getObjects().size() );

        // modifying sensor
        sensor.setTemperature( 80 );
        sensorHandle = getFactHandle( sensorHandle, ksession );
        ksession.update( sensorHandle,
                              sensor );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        // no alarms anymore
        assertEquals( 3,
                      list.size() );
        assertEquals( 1,
                      ksession.getObjects().size() );
    }

    @Test(timeout=10000)
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

    @Test(timeout=10000)
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
        sensor1Handle =  getFactHandle( sensor1Handle, session );
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

    @Test(timeout=10000)
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
        RuleRuntimeEventListener wmel = mock( RuleRuntimeEventListener.class );
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
    
    @Test(timeout=10000)
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

    @Test(timeout=10000)
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
    
    @Test(timeout=10000)
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
                                            StatefulKnowledgeSessionImpl session) {
        Map<Integer, FactHandle> handles = new HashMap<Integer, FactHandle>();
        for ( FactHandle fh : session.getFactHandles() ) {
            handles.put( ((InternalFactHandle) fh).getId(),
                         fh );
        }
        return (InternalFactHandle) handles.get( ((InternalFactHandle) factHandle).getId() );
    }     
    
    public InternalFactHandle getFactHandle(FactHandle factHandle,
                                            KieSession ksession) {
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

    @Test(timeout=10000)
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



    @Test(timeout=10000)
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


    @Test(timeout=10000)
    public void testPrimeJustificationWithEqualityMode() {
        String droolsSource =
                "package org.drools.tms.test; \n" +
                "declare Bar end \n" +
                "" +
                "declare Holder x : Bar end \n" +
                "" +
                "" +
                "rule Init \n" +
                "when \n" +
                "then \n" +
                "   insert( new Holder( new Bar() ) ); \n" +
                "end \n" +

                "rule Justify \n" +
                "when \n" +
                " $s : Integer() \n" +
                " $h : Holder( $b : x ) \n" +
                "then \n" +
                " insertLogical( $b ); \n" +
                "end \n" +

                "rule React \n" +
                "when \n" +
                " $b : Bar(  ) \n" +
                "then \n" +
                " System.out.println( $b );  \n" +
                "end \n" ;

        /////////////////////////////////////

        KieBaseConfiguration kieConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieConf.setOption( EqualityBehaviorOption.EQUALITY );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kieConf, droolsSource );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        FactHandle handle1 = session.insert( 10 );
        FactHandle handle2 = session.insert( 20 );

        assertEquals( 4, session.fireAllRules() );

        session.delete( handle1 );
        assertEquals( 0, session.fireAllRules() );
    }

    @Test(timeout=10000)
    public void testJustificationStateOverridingBySuperClass() {
        //DROOLS-352
        String droolsSource =
                "package org.drools.tms.test; \n" +
                "" +
                "declare Foo end \n" +
                "declare Bar extends Foo end \n" +
                "" +
                "rule Justify_Sub \n" +
                "when \n" +
                "then \n" +
                " insertLogical( new Bar() ); \n" +
                " insertLogical( 42.0 ); \n" +
                "end \n" +

                "rule Justify_Sup \n" +
                "when \n" +
                " $d : Double() \n" +
                "then \n" +
                " insertLogical( new Foo() ); \n" +
                "end \n" +
                "" +
                "" +
                "rule Fuu when Foo() then end \n" +
                "rule Bor when Bar() then end \n";

        /////////////////////////////////////

        KieBaseConfiguration kieConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kieConf.setOption( EqualityBehaviorOption.EQUALITY );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kieConf, droolsSource );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        session.fireAllRules();

        for ( FactHandle fh : session.getFactHandles() ) {
            InternalFactHandle ifh = (InternalFactHandle) fh;
            assertEquals( EqualityKey.JUSTIFIED, ifh.getEqualityKey().getStatus() );
        }

    }


    @Test(timeout=10000)
         public void testLogicalWithDeleteException() {
        String droolsSource =
                "package org.drools.tms.test; \n" +

                "global java.util.List list; \n" +

                "rule Justify \n" +
                "when \n" +
                "    String( this == 'go1' ) " +
                "then \n" +
                "    insertLogical( 'f1' ); \n" +
                "end \n" +
                ""
                ;

        KieBaseConfiguration kieConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kieConf.setOption( EqualityBehaviorOption.IDENTITY );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kieConf, droolsSource );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        session.setGlobal("list", list);

        session.insert( "go1" );
        session.fireAllRules();

        TruthMaintenanceSystem tms = ((StatefulKnowledgeSessionImpl)session).getTruthMaintenanceSystem();
        InternalFactHandle jfh1 = tms.get( "f1" ).getLogicalFactHandle();

        assertSame( jfh1, session.getFactHandle( "f1" ) );

        try {
            session.delete(jfh1);
            fail( "Sholud have thrown an exception, as the you cannot delete a wm fh on the tms" );
        } catch( IllegalArgumentException w) {

        }
    }

    @Test(timeout=10000)
    public void testStatedWithShadowAndDeleteException() {
        String droolsSource =
                "package org.drools.tms.test; \n" +

                "global java.util.List list; \n" +

                "rule Justify \n" +
                "when \n" +
                "    String( this == 'go1' ) " +
                "then \n" +
                "    insertLogical( 'f1' ); \n" +
                "end \n" +
                ""
                ;

        KieBaseConfiguration kieConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kieConf.setOption( EqualityBehaviorOption.IDENTITY );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kieConf, droolsSource );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        session.setGlobal("list", list);

        InternalFactHandle fh1 = (InternalFactHandle) session.insert( "f1" );
        InternalFactHandle fh2 = (InternalFactHandle) session.insert( "f2" );

        session.insert( "go1" );
        session.fireAllRules();

        // TMS is now enabled
        assertNotNull(fh1.getEqualityKey() );
        assertNotNull(fh2.getEqualityKey() );

        // EqualtyKey shows both are stated
        assertEquals( EqualityKey.STATED, fh1.getEqualityKey().getStatus());
        assertEquals(EqualityKey.STATED, fh2.getEqualityKey().getStatus() );

        // Only fh1 has a logical
        assertEquals( 1, fh1.getEqualityKey().getBeliefSet().size() );
        assertNull( fh2.getEqualityKey().getBeliefSet() );

        // Get the logical Handle too
        TruthMaintenanceSystem tms = ((StatefulKnowledgeSessionImpl)session).getTruthMaintenanceSystem();
        InternalFactHandle jfh1 = tms.get( "f1" ).getLogicalFactHandle();
        EqualityKey key = jfh1.getEqualityKey();
        assertSame( fh1.getEqualityKey(), key );
        assertNotSame( fh1, jfh1 );

        assertEquals(2, key.size());
        assertSame(jfh1, key.getLogicalFactHandle());

        try {
            tms.delete(fh1);
            fail( "Sholud have thrown an exception, as the you cannot delete a tms fh on the wm" );
        } catch( IllegalArgumentException w) {

        }

        try {
            session.delete(jfh1);
            fail( "Sholud have thrown an exception, as the you cannot delete a wm fh on the tms" );
        } catch( IllegalArgumentException w) {

        }
    }

    @Test(timeout=10000)
    public void testStatedShadowLogicalWithSingleOccurance() {
        String droolsSource =
                "package org.drools.tms.test; \n" +

                "global java.util.List list; \n" +

                "rule Justify \n" +
                "when \n" +
                "    String( this == 'go1' ) " +
                "then \n" +
                "    insertLogical( 'f1' ); \n" +
                "end \n" +


                "rule StillHere \n" +
                "when \n" +
                "    String( this == 'go2' ) " +
                "    s : String( this == 'f1' ) " +
                "then \n" +
                "    list.add( s ); \n" +
                "end \n" +
                ""
                ;

        KieBaseConfiguration kieConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kieConf.setOption( EqualityBehaviorOption.IDENTITY );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kieConf, droolsSource );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        session.setGlobal("list", list);

        InternalFactHandle fh1 = (InternalFactHandle) session.insert( "f1" );
        InternalFactHandle fh2 = (InternalFactHandle) session.insert( "f2" );

        session.insert( "go1" );
        session.fireAllRules();

        session.insert( "go2" );
        session.fireAllRules();

        // Make sure f1 only occurs once
        assertEquals( 1, list.size() );
        assertEquals( "f1", list.get( 0 ) );
    }

    @Test(timeout=10000)
    public void testLogicalThenStatedShadowSingleOccurance() {
        String droolsSource =
                "package org.drools.tms.test; \n" +

                "global java.util.List list; \n" +

                "rule Justify \n" +
                "when \n" +
                "    String( this == 'go1' ) " +
                "then \n" +
                "    insertLogical( 'f1' ); \n" +
                "end \n" +


                "rule StillHere \n" +
                "when \n" +
                "    String( this == 'go2' ) " +
                "    s : String( this == 'f1' ) " +
                "then \n" +
                "    list.add( s ); \n" +
                "end \n" +
                ""
                ;

        KieBaseConfiguration kieConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kieConf.setOption( EqualityBehaviorOption.IDENTITY );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kieConf, droolsSource );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        session.setGlobal("list", list);

        session.insert( "go1" );
        session.fireAllRules();

        TruthMaintenanceSystem tms = ((StatefulKnowledgeSessionImpl)session).getTruthMaintenanceSystem();
        InternalFactHandle jfh1 = tms.get( "f1" ).getLogicalFactHandle();
        assertEquals(EqualityKey.JUSTIFIED, jfh1.getEqualityKey().getStatus() );

        InternalFactHandle fh1 = (InternalFactHandle) session.insert( "f1" );
        InternalFactHandle fh2 = (InternalFactHandle) session.insert( "f2" );

        session.insert("go2");
        session.fireAllRules();

        assertEquals( EqualityKey.STATED, fh1.getEqualityKey().getStatus());
        assertSame( fh1.getEqualityKey(), jfh1.getEqualityKey() );
        assertNotSame( fh1, jfh1 );

        EqualityKey key = jfh1.getEqualityKey();
        assertSame( fh1.getEqualityKey(), key );
        assertNotSame( fh1, jfh1 );

        assertEquals(2, key.size());
        assertSame( jfh1,  key.getLogicalFactHandle() );

        // Make sure f1 only occurs once
        assertEquals( 1, list.size() );
        assertEquals( "f1", list.get( 0 ) );
    }

    @Test(timeout=10000)
    public void testStatedShadowLogicalThenLogicalOnly() {
        String droolsSource =
                "package org.drools.tms.test; \n" +

                "global java.util.List list; \n" +

                "rule Justify \n" +
                "when \n" +
                "    String( this == 'go1' ) " +
                "then \n" +
                "    insertLogical( 'f1' ); \n" +
                "end \n" +


                "rule StillHere \n" +
                "when \n" +
                "    String( this == 'go2' ) " +
                "    s : String( this == 'f1' ) " +
                "then \n" +
                "    list.add( s ); \n" +
                "end \n" +
                ""
                ;

        KieBaseConfiguration kieConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kieConf.setOption( EqualityBehaviorOption.IDENTITY );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kieConf, droolsSource );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        session.setGlobal("list", list);

        InternalFactHandle fh1 = (InternalFactHandle) session.insert( "f1" );
        InternalFactHandle fh2 = (InternalFactHandle) session.insert( "f2" );


        FactHandle g1 = session.insert( "go1" );
        session.fireAllRules();

        // This removes the stated position, but it should still be logical now and exist
        session.delete( fh1 );
        session.insert( "go2" );
        session.fireAllRules();

        // fh1 is invalid and no longer in the WM. However it's now reverted to it's Justified version and will still be there
        assertFalse(fh1.isValid());

        // Make sure f1 is still there, but logical only now
        assertEquals(1, list.size());
        assertEquals("f1", list.get(0));
        InternalFactHandle jfh1 = ((StatefulKnowledgeSessionImpl)session).getTruthMaintenanceSystem().get( "f1" ).getLogicalFactHandle();
        assertEquals( EqualityKey.JUSTIFIED, jfh1.getEqualityKey().getStatus());

        assertSame(jfh1, session.getFactHandle("f1") );
    }


    @Test(timeout=10000)
    public void testLogicalThenUpdateAsStatedShadowSingleOccurance() {
        String droolsSource =
                "package org.drools.tms.test; \n" +

                "global java.util.List list; \n" +

                "rule Justify \n" +
                "when \n" +
                "    String( this == 'go1' ) " +
                "then \n" +
                "    insertLogical( 'f1' ); \n" +
                "end \n" +


                "rule StillHere \n" +
                "when \n" +
                "    String( this == 'go2' ) " +
                "    s : String( this == 'f1' ) " +
                "then \n" +
                "    list.add( s ); \n" +
                "end \n" +
                ""
                ;

        KieBaseConfiguration kieConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kieConf.setOption( EqualityBehaviorOption.IDENTITY );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kieConf, droolsSource );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        session.setGlobal("list", list);

        session.insert( "go1" );
        session.fireAllRules();

        TruthMaintenanceSystem tms = ((StatefulKnowledgeSessionImpl)session).getTruthMaintenanceSystem();
        InternalFactHandle jfh1 = tms.get( "f1" ).getLogicalFactHandle();
        assertEquals(EqualityKey.JUSTIFIED, jfh1.getEqualityKey().getStatus() );

        InternalFactHandle fh1 = (InternalFactHandle) session.insert( "f1" );
        InternalFactHandle fh2 = (InternalFactHandle) session.insert( "f2" );

        session.insert("go2");
        session.fireAllRules();

        assertEquals( EqualityKey.STATED, fh1.getEqualityKey().getStatus());
        assertSame( fh1.getEqualityKey(), jfh1.getEqualityKey() );
        assertNotSame( fh1, jfh1 );

        // Make sure f1 only occurs once
        assertEquals( 1, list.size() );
        assertEquals( "f1", list.get( 0 ) );
    }

    @Test(timeout=10000)
    public void testLogicalWithStatedShadowThenDeleteLogicalThenDeleteStated() {
        String droolsSource =
                "package org.drools.tms.test; \n" +

                "global java.util.List list; \n" +

                "rule Justify \n" +
                "when \n" +
                "    String( this == 'go1' ) " +
                "then \n" +
                "    insertLogical( 'f1' ); \n" +
                "end \n" +


                "rule StillHere \n" +
                "when \n" +
                "    String( this in ('go2', 'go3', 'go4') ) " +
                "    s : String( this == 'f1' ) " +
                "then \n" +
                "    list.add( s ); \n" +
                "end \n" +
                ""
                ;

        KieBaseConfiguration kieConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kieConf.setOption( EqualityBehaviorOption.IDENTITY );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kieConf, droolsSource );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        session.setGlobal("list", list);

        session.insert( "go1" );
        session.fireAllRules();

        TruthMaintenanceSystem tms = ((StatefulKnowledgeSessionImpl)session).getTruthMaintenanceSystem();
        InternalFactHandle jfh1 = tms.get( "f1" ).getLogicalFactHandle();
        assertEquals(EqualityKey.JUSTIFIED, jfh1.getEqualityKey().getStatus() );

        InternalFactHandle fh1 = (InternalFactHandle) session.insert( "f1" );

        session.insert("go2");
        session.fireAllRules();

        assertEquals( EqualityKey.STATED, fh1.getEqualityKey().getStatus());
        assertEquals( 1, fh1.getEqualityKey().getBeliefSet().size() );
        assertSame( fh1.getEqualityKey(), jfh1.getEqualityKey() );
        assertNotSame( fh1, jfh1 );

        // Make sure f1 only occurs once
        assertEquals( 1, list.size() );
        assertEquals( "f1", list.get( 0 ) );

        list.clear();
        tms.delete( jfh1 );
        session.insert("go3");
        session.fireAllRules();

        assertNull(fh1.getEqualityKey().getBeliefSet());

        // Make sure f1 only occurs once
        assertEquals( 1, list.size() );
        assertEquals( "f1", list.get( 0 ) );

        list.clear();
        session.delete( fh1 );
        session.insert("go4");
        session.fireAllRules();

        assertEquals( 0, list.size() );
    }

    @Test(timeout=10000)
    public void testUpdate() {
        String droolsSource =
                "package org.drools.tms.test; \n" +
                " declare Feat1\n" +
                "   context : String @key\n" +
                "   value : double @key\n" +
                "   missing : boolean  = false\n" +
                " \n" +
                " end\n" +
                " \n" +
                " \n" +
                "rule \"InitAsMissing_Test_MLP_Feat1\"\n" +
                "when\n" +
                "    not Feat1( context == null )\n" +
                "then\n" +
                "    insertLogical( new Feat1( \"Test_MLP\", 0.0, true ) );\n" +
                "end\n" +
                " \n" +
                "rule \"miningFieldMissing_Test_MLP_Feat1\"\n" +
                "when\n" +
                "     $x : Feat1( $m : missing == true, context == \"Test_MLP\" )\n" +
                "then\n" +
                "    modify ( $x ) {\n" +
                "        setValue( 3.95 ),\n" +
                "        setMissing( false );\n" +
                "    }\n" +
                "end\n" +
                " \n" +
                "rule \"OverrideInput_Feat1\"\n" +
                "when\n" +
                "    $old: Feat1( value != 4.33 )\n" +
                "then\n" +
                "    retract( $old );\n" +
                "end\n" +
                " \n" +
                " \n" +
                "rule \"Input_Feat1\"\n" +
                "when\n" +
                "    not Feat1(  context == null )\n" +
                "then\n" +
                "    insert( new Feat1( null, 4.33 ) );\n" +
                "end"
                ;

        KieBaseConfiguration kieConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kieConf.setOption( EqualityBehaviorOption.IDENTITY );
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kieConf, droolsSource );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();

        try {
            session.fireAllRules();
            fail("Currently we cannot handle updates for a belief set that is mixed stated and justified" );
        } catch ( ConsequenceException e) {
            assertEquals(IllegalStateException.class, e.getCause().getClass() );
        }

    }

}

