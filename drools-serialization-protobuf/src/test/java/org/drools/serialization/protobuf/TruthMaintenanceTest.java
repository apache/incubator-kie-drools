/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.serialization.protobuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.ClassObjectFilter;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.rule.EntryPointId;
import org.drools.core.util.ObjectHashMap;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.CheeseEqual;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.Sensor;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.drools.serialization.protobuf.SerializationHelper.getSerialisedStatefulKnowledgeSession;
import static org.drools.serialization.protobuf.SerializationHelper.serializeObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

        Collection<KiePackage> kpkgs = kbuilder.getKnowledgePackages();
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) getKnowledgeBase();
        kbase.addPackages( kpkgs );
        KieSession ksession = createKnowledgeSession(kbase);
        try {
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
            Collection<KiePackage> kpkgs2 = kbuilder.getKnowledgePackages();
            kbase.addPackages( kpkgs2 );
            kbase = serializeObject(kbase);

            ksession.fireAllRules();

            ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                              true );

            kbase = (InternalKnowledgeBase) ksession.getKieBase();

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
            assertEquals( 3, kbase.getKiePackages().size() );
            KiePackage test = null, test2 = null;
            // different JVMs return the package list in different order
            for( KiePackage kpkg : kbase.getKiePackages() ) {
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
            for( KiePackage kpkg : kbase.getKiePackages() ) {
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
            for( KiePackage kpkg : kbase.getKiePackages() ) {
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
            kbase = serializeObject(kbase);

            // different JVMs return the package list in different order
            for( KiePackage kpkg : kbase.getKiePackages() ) {
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
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout=10000)
    public void testLogicalInsertions() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_LogicalInsertions.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        Collection<KiePackage> kpkgs = kbuilder.getKnowledgePackages();

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kpkgs );
        kbase = serializeObject(kbase);
        KieSession session = createKnowledgeSession(kbase);
        try {
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
            session.delete( brieHandle );

            session = getSerialisedStatefulKnowledgeSession( session,
                                                             true );

            assertEquals( 2,
                          session.getObjects().size() );

            provoloneHandle = getFactHandle( provoloneHandle, session );
            session.delete( provoloneHandle );
            session.fireAllRules();

            assertEquals(0,
                         session.getObjects().size());
        } finally {
            session.dispose();
        }
    }

    @Test(timeout=10000)
    public void testLogicalInsertionsBacking() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_LogicalInsertionsBacking.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        Collection<KiePackage> kpkgs = kbuilder.getKnowledgePackages();

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kpkgs );
        kbase = serializeObject(kbase);
        KieSession session = createKnowledgeSession(kbase);
        try {
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
            session.delete( h1 );
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
            session.delete( h2 );
            session = getSerialisedStatefulKnowledgeSession( session,
                                                             true );
            session.fireAllRules();
            session = getSerialisedStatefulKnowledgeSession( session,
                                                             true );
            list = session.getObjects( new ClassObjectFilter( cheese1.getType().getClass() ) );
            assertEquals( "cheese-type " + cheese1.getType() + " was not deleted, but should have. Neither  cheese1 => type nor cheese2 => type is true.",
                          0,
                          list.size() );
        } finally {
            session.dispose();
        }
    }

    @Test(timeout=10000)
    //@Ignore("in Java 8, the byte[] generated by serialization are not the same and requires investigation")
    public void testLogicalInsertionsWithModify() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LogicalInsertionsWithUpdate.drl");
        KieSession ksession = kbase.newKieSession();
        try {
            final Person p = new Person( "person" );
            p.setAge( 2 );
            FactHandle h = ksession.insert( p );
            assertEquals(1,
                         ksession.getObjects().size());

            ksession.fireAllRules();
            ksession = getSerialisedStatefulKnowledgeSession(ksession, false);
            assertEquals( 2,
                          ksession.getObjects().size() );

            Collection l = ksession.getObjects( new ClassObjectFilter( CheeseEqual.class ) );
            assertEquals( 1,
                          l.size() );
            assertEquals( 2,
                          ((CheeseEqual) l.iterator().next()).getPrice() );

            h = getFactHandle( h, ksession );
            ksession.delete( h );
            ksession = getSerialisedStatefulKnowledgeSession(ksession, false);
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
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout=10000)
    public void testLogicalInsertions2() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LogicalInsertions2.drl");
        KieSession ksession = kbase.newKieSession();
        try {
            final List events = new ArrayList();

            ksession.setGlobal( "events", events );

            final Sensor sensor = new Sensor( 80,
                                              80 );
            FactHandle handle = ksession.insert( sensor );

            // everything should be normal

            ksession = getSerialisedStatefulKnowledgeSession( ksession, false );
            ksession.fireAllRules();

            Collection list = ksession.getObjects();

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

            ksession = getSerialisedStatefulKnowledgeSession( ksession, true );

            ksession.fireAllRules();
            list = ksession.getObjects();

            assertEquals( "Only sensor is there",
                          1,
                          list.size() );

            TruthMaintenanceSystem tms =  ((NamedEntryPoint)ksession.getEntryPoint(EntryPointId.DEFAULT.getEntryPointId()) ).getTruthMaintenanceSystem();
            assertTrue(tms.getEqualityKeyMap().isEmpty());
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout=10000)
    //@Ignore("in Java 8, the byte[] generated by serialization are not the same and requires investigation")
    public void testLogicalInsertionsNot() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LogicalInsertionsNot.drl");
        KieSession ksession = kbase.newKieSession();
        try {
            final Person a = new Person( "a" );
            final Cheese cheese = new Cheese( "brie",
                                              1 );
            ksession.setGlobal( "cheese",
                                cheese );

            ksession.fireAllRules();
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            Collection list = ksession.getObjects();
            assertEquals( "i was not asserted by not a => i.",
                          1,
                          list.size() );
            assertEquals( "i was not asserted by not a => i.",
                          cheese,
                          list.iterator().next() );

            FactHandle h = ksession.insert( a );

            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
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
            ksession.delete( h );
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            list = ksession.getObjects();
            assertEquals( "i was not asserted by not a => i.",
                          1,
                          list.size() );
            assertEquals( "i was not asserted by not a => i.",
                          cheese,
                          list.iterator().next() );
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout=10000)
    @Ignore("Currently cannot support updates")
    //@Ignore("in Java 8, the byte[] generated by serialization are not the same and requires investigation")
    public void testLogicalInsertionsUpdateEqual() throws Exception {
        // calling update on a justified FH, states it
        KieBase kbase = loadKnowledgeBase("test_LogicalInsertionsUpdateEqual.drl");
        KieSession ksession = kbase.newKieSession();
        try {
            final Person p = new Person( "person" );
            p.setAge( 2 );
            FactHandle h = ksession.insert( p );
            assertEquals(1,
                         ksession.getObjects().size());

            ksession.fireAllRules();
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            assertEquals( 2,
                          ksession.getObjects().size() );
            Collection l = ksession.getObjects( new ClassObjectFilter( CheeseEqual.class ) );
            assertEquals( 1,
                          l.size() );
            assertEquals( 3,
                          ((CheeseEqual) l.iterator().next()).getPrice() );

            h = getFactHandle( h, ksession );
            ksession.delete( h );
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);


            Collection list = ksession.getObjects();
            // CheeseEqual was updated, making it stated, so it wouldn't have been logically deleted
            assertEquals( 1,
                          list.size() );
            assertEquals( new CheeseEqual("person", 3), list.iterator().next());
            FactHandle fh = ksession.getFactHandle( list.iterator().next() );
            ksession.delete( fh );

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
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout=10000)
    //@Ignore("in Java 8, the byte[] generated by serialization are not the same and requires investigation")
    public void testLogicalInsertionsWithExists() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LogicalInsertionWithExists.drl");
        KieSession ksession = kbase.newKieSession();
        try {
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

            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);

            // all 3 in europe, so, 2 cheese
            Collection cheeseList = ksession.getObjects(new ClassObjectFilter(Cheese.class));
            assertEquals( 2,
                          cheeseList.size() );

            // europe=[ 1, 2 ], america=[ 3 ]
            p3.setStatus( "america" );
            c3FactHandle = getFactHandle( c3FactHandle, ksession );
            ksession.update( c3FactHandle,
                             p3 );
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            cheeseList = ksession.getObjects(new ClassObjectFilter(Cheese.class));
            assertEquals( 1,
                          cheeseList.size() );

            // europe=[ 1 ], america=[ 2, 3 ]
            p2.setStatus( "america" );
            c2FactHandle = getFactHandle( c2FactHandle, ksession );
            ksession.update( c2FactHandle,
                             p2 );
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            cheeseList = ksession.getObjects(new ClassObjectFilter(Cheese.class));
            assertEquals( 1,
                          cheeseList.size() );

            // europe=[ ], america=[ 1, 2, 3 ]
            p1.setStatus( "america" );
            c1FactHandle = getFactHandle( c1FactHandle, ksession );
            ksession.update( c1FactHandle,
                             p1 );
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            cheeseList = ksession.getObjects(new ClassObjectFilter(Cheese.class));
            assertEquals( 2,
                          cheeseList.size() );

            // europe=[ 2 ], america=[ 1, 3 ]
            p2.setStatus( "europe" );
            c2FactHandle = getFactHandle( c2FactHandle, ksession );
            ksession.update( c2FactHandle,
                             p2 );
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            cheeseList = ksession.getObjects(new ClassObjectFilter(Cheese.class));
            assertEquals( 1,
                          cheeseList.size() );

            // europe=[ 1, 2 ], america=[ 3 ]
            p1.setStatus( "europe" );
            c1FactHandle = getFactHandle( c1FactHandle, ksession );
            ksession.update( c1FactHandle,
                             p1 );
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            cheeseList = ksession.getObjects(new ClassObjectFilter(Cheese.class));
            assertEquals( 1,
                          cheeseList.size() );

            // europe=[ 1, 2, 3 ], america=[ ]
            p3.setStatus( "europe" );
            c3FactHandle = getFactHandle( c3FactHandle, ksession );
            ksession.update( c3FactHandle,
                             p3 );
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            cheeseList = ksession.getObjects(new ClassObjectFilter(Cheese.class));
            assertEquals( 2,
                          cheeseList.size() );
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout=10000)
    public void testLogicalInsertions3() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_logicalInsertions3.drl");
        KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal( "events", list );

            // asserting the sensor object
            final Sensor sensor = new Sensor( 150, 100 );
            FactHandle sensorHandle = ksession.insert( sensor );

            ksession.fireAllRules();
            ksession = getSerialisedStatefulKnowledgeSession( ksession, true );

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
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);

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
            ksession = getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();

            // no alarms anymore
            assertEquals( 3,
                          list.size() );
            assertEquals( 1,
                          ksession.getObjects().size() );
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout=10000)
    public void testLogicalInsertionsAccumulatorPattern() throws Exception {
        // JBRULES-449
        KieBase kbase = loadKnowledgeBase( "test_LogicalInsertionsAccumulatorPattern.drl" );
        kbase = serializeObject(kbase);
        KieSession ksession = kbase.newKieSession();
        try {
            ksession.setGlobal( "ga",
                                "a" );
            ksession.setGlobal( "gb",
                                "b" );
            ksession.setGlobal( "gs",
                                new Short( (short) 3 ) );

            ksession.fireAllRules();

            ksession = getSerialisedStatefulKnowledgeSession(ksession,
                                                                                 true);

            FactHandle h = ksession.insert( new Integer( 6 ) );
            assertEquals( 1,
                          ksession.getObjects().size() );

            ksession.fireAllRules();
            ksession = getSerialisedStatefulKnowledgeSession(ksession,
                                                                                 true);
            assertEquals( "There should be 2 CheeseEqual in Working Memory, 1 justified, 1 stated",
                          2,
                          ksession.getObjects( new ClassObjectFilter( CheeseEqual.class ) ).size() );
            assertEquals( 6,
                          ksession.getObjects().size() );

            h = getFactHandle( h, ksession );
            ksession.delete( h );
            ksession.fireAllRules();

            for ( Object o : ksession.getObjects() ) {
                System.out.println( o );
            }

            ksession = getSerialisedStatefulKnowledgeSession(ksession,
                                                                                 true);
            assertEquals( 0,
                          ksession.getObjects( new ClassObjectFilter( CheeseEqual.class ) ).size() );
            assertEquals( 0,
                          ksession.getObjects( new ClassObjectFilter( Short.class ) ).size() );
            assertEquals( 0,
                          ksession.getObjects().size() );
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout=10000)
    public void testLogicalInsertionsModifySameRuleGivesDifferentLogicalInsertion() throws Exception {
        // TODO JBRULES-1804

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_LogicalInsertionsModifySameRuleGivesDifferentLogicalInsertion.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        Collection<KiePackage> pkgs = kbuilder.getKnowledgePackages();

        InternalKnowledgeBase kbase = (InternalKnowledgeBase) getKnowledgeBase();
        kbase.addPackages( pkgs );
        kbase = serializeObject(kbase);
        KieSession session = createKnowledgeSession(kbase);
        try {
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
        } finally {
            session.dispose();
        }
    }

    public InternalFactHandle getFactHandle(FactHandle factHandle,
                                            StatefulKnowledgeSessionImpl session) {
        Map<Long, FactHandle> handles = new HashMap<>();
        for ( FactHandle fh : session.getFactHandles() ) {
            handles.put( ((InternalFactHandle) fh).getId(),
                    fh );
        }
        return (InternalFactHandle) handles.get( ((InternalFactHandle) factHandle).getId() );
    }

    public InternalFactHandle getFactHandle(FactHandle factHandle,
                                            KieSession ksession) {
        Map<Long, FactHandle> handles = new HashMap<>();
        for ( FactHandle fh : ksession.getFactHandles() ) {
            handles.put( ((InternalFactHandle) fh).getId(),
                    fh );
        }
        return (InternalFactHandle) handles.get( ((InternalFactHandle) factHandle).getId() );
    }
}

