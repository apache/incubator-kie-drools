/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.drools.compiler.Address;
import org.drools.compiler.Cat;
import org.drools.compiler.Cell;
import org.drools.compiler.Cheese;
import org.drools.compiler.Cheesery;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.LongAddress;
import org.drools.compiler.MockPersistentSet;
import org.drools.compiler.ObjectWithSet;
import org.drools.compiler.Person;
import org.drools.compiler.PersonFinal;
import org.drools.compiler.PersonInterface;
import org.drools.compiler.PolymorphicFact;
import org.drools.compiler.Primitives;
import org.drools.compiler.SpecialString;
import org.drools.compiler.Triangle;
import org.drools.core.ClassObjectFilter;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * Run all the tests with the ReteOO engine implementation
  */
 public class MiscTest extends CommonTestMethodBase {

    private static Logger logger = LoggerFactory.getLogger(MiscTest.class);

     @Test
     public void testCrossProductRemovingIdentityEquals() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_CrossProductRemovingIdentityEquals.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         List list1 = new ArrayList();
         List list2 = new ArrayList();

         session.setGlobal( "list1",
                            list1 );

         SpecialString first42 = new SpecialString( "42" );
         SpecialString second43 = new SpecialString( "43" );
         SpecialString world = new SpecialString( "World" );
         session.insert( world );
         session.insert( first42 );
         session.insert( second43 );

         session.fireAllRules();

         assertEquals( 6,
                       list1.size() );

         list2 = Arrays.asList( new String[]{"42:43", "43:42", "World:42", "42:World", "World:43", "43:World"} );
         Collections.sort( list1 );
         Collections.sort( list2 );
         assertEquals( list2,
                       list1 );
     }

     @Test
     public void testIterateObjects() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_IterateObjects.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         ksession.insert( new Cheese( "stilton",
                                      10 ) );

         ksession.fireAllRules();

         Iterator events = ksession.getObjects( new ClassObjectFilter( PersonInterface.class ) ).iterator();

         assertTrue( events.hasNext() );
         assertEquals( 1,
                       results.size() );
         assertEquals( results.get( 0 ),
                       events.next() );
     }

     @Test
     public void testAutovivificationOfVariableRestrictions() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_AutoVivificationVR.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         ksession.insert( new Cheese( "stilton",
                                      10,
                                      8 ) );

         ksession.fireAllRules();

         assertEquals( 1,
                       results.size() );
     }

     @Test
     public void testShadowProxyOnCollections() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ShadowProxyOnCollections.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         final Cheesery cheesery = new Cheesery();
         ksession.insert( cheesery );

         ksession.fireAllRules();

         assertEquals( 1,
                       results.size() );
         assertEquals( 1,
                       cheesery.getCheeses().size() );
         assertEquals( results.get( 0 ),
                       cheesery.getCheeses().get( 0 ) );
     }

     @Test
     public void testShadowProxyOnCollections2() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ShadowProxyOnCollections2.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         List list = new ArrayList();
         list.add( "example1" );
         list.add( "example2" );

         MockPersistentSet mockPersistentSet = new MockPersistentSet( false );
         mockPersistentSet.addAll( list );
         ObjectWithSet objectWithSet = new ObjectWithSet();
         objectWithSet.setSet( mockPersistentSet );

         ksession.insert( objectWithSet );

         ksession.fireAllRules();

         assertEquals( 1,
                       results.size() );
         assertEquals( "show",
                       objectWithSet.getMessage() );
     }

     @Test
     public void testWorkingMemoryLoggerWithUnbalancedBranches() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_Logger.drl" ) );
         StatefulKnowledgeSession wm = createKnowledgeSession( kbase );

         try {
             wm.fireAllRules();

             wm.insert( new Cheese( "a",
                                    10 ) );
             wm.insert( new Cheese( "b",
                                    11 ) );

             wm.fireAllRules();

         } catch ( Exception e ) {
             e.printStackTrace();
             fail( "No exception should be raised " );
         }

     }

     @Test
     public void testSubNetworks() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_SubNetworks.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );
     }

     @Test
     public void testFinalClass() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_FinalClass.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         final PersonFinal bob = new PersonFinal();
         bob.setName( "bob" );
         bob.setStatus( null );

         ksession.insert( bob );

         ksession.fireAllRules();

         assertEquals( 1,
                       list.size() );
     }

     @Test
     public void testRuntimeTypeCoercion() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_RuntimeTypeCoercion.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         final PolymorphicFact fact = new PolymorphicFact( new Integer( 10 ) );
         final FactHandle handle = ksession.insert( fact );

         ksession.fireAllRules();

         assertEquals( 1,
                       list.size() );
         assertEquals( fact.getData(),
                       list.get( 0 ) );

         fact.setData( "10" );
         ksession.update( handle,
                          fact );
         ksession.fireAllRules();

         assertEquals( 2,
                       list.size() );
         assertEquals( fact.getData(),
                       list.get( 1 ) );

         try {
             fact.setData( new Boolean( true ) );
             ksession.update( handle,
                              fact );

             assertEquals( 2,
                           list.size() );
         } catch ( ClassCastException cce ) {
         }
     }

     @Test
     public void testRuntimeTypeCoercion2() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_RuntimeTypeCoercion2.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         final Primitives fact = new Primitives();
         fact.setBooleanPrimitive( true );
         fact.setBooleanWrapper( new Boolean( true ) );
         fact.setObject( new Boolean( true ) );
         fact.setCharPrimitive( 'X' );
         final FactHandle handle = ksession.insert( fact );

         ksession.fireAllRules();

         int index = 0;
         assertEquals( list.toString(),
                       4,
                       list.size() );
         assertEquals( "boolean",
                       list.get( index++ ) );
         assertEquals( "boolean wrapper",
                       list.get( index++ ) );
         assertEquals( "boolean object",
                       list.get( index++ ) );
         assertEquals( "char",
                       list.get( index++ ) );

         fact.setBooleanPrimitive( false );
         fact.setBooleanWrapper( null );
         fact.setCharPrimitive( '\0' );
         fact.setObject( new Character( 'X' ) );
         ksession.update( handle,
                          fact );
         ksession.fireAllRules();
         assertEquals( 5,
                       list.size() );
         assertEquals( "char object",
                       list.get( index++ ) );

         fact.setObject( null );
         ksession.update( handle,
                          fact );
         ksession.fireAllRules();
         assertEquals( 6,
                       list.size() );
         assertEquals( "null object",
                       list.get( index++ ) );

     }

     @Test
     public void testNPEOnParenthesis() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase("test_ParenthesisUsage.drl");

         final List<Person> results = new ArrayList<Person>();

         final StatefulKnowledgeSession session = createKnowledgeSession( kbase );
         session.setGlobal( "results",
                            results );

         Person bob = new Person( "Bob",
                                  20 );
         bob.setAlive( true );
         Person foo = new Person( "Foo",
                                  0 );
         foo.setAlive( false );

         session.insert( bob );
         session.fireAllRules();

         assertEquals( 1,
                       results.size() );
         assertEquals( bob,
                       results.get( 0 ) );

         session.insert( foo );
         session.fireAllRules();

         assertEquals( 2,
                       results.size() );
         assertEquals( foo,
                       results.get( 1 ) );
     }

     @Test
     public void testDRLWithoutPackageDeclaration() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase("test_NoPackageDeclaration.drl");

         // no package defined, so it is set to the default
         final FactType factType = kbase.getFactType( "defaultpkg",
                                                      "Person" );
         assertNotNull( factType );
         final Object bob = factType.newInstance();
         factType.set( bob,
                       "name",
                       "Bob" );
         factType.set( bob,
                       "age",
                       Integer.valueOf( 30 ) );

         final StatefulKnowledgeSession session = createKnowledgeSession( kbase );
         final List results = new ArrayList();
         session.setGlobal( "results",
                            results );

         session.insert( bob );
         session.fireAllRules();

         assertEquals( 1,
                       results.size() );
         assertEquals( bob,
                       results.get( 0 ) );
     }

     @Test
     public void testFireAllWhenFiringUntilHalt() {
         KnowledgeBase kbase = getKnowledgeBase();
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Runnable fireUntilHalt = new Runnable() {
             public void run() {
                 ksession.fireUntilHalt();
             }
         };
         Runnable fireAllRules = new Runnable() {
             public void run() {
                 ksession.fireAllRules();
             }
         };
         Thread t1 = new Thread( fireUntilHalt );
         Thread t2 = new Thread( fireAllRules );
         t1.start();
         try {
             Thread.currentThread().sleep( 500 );
         } catch ( InterruptedException e ) {
         }
         t2.start();
         // give the chance for t2 to finish
         try {
             Thread.currentThread().sleep( 1000 );
         } catch ( InterruptedException e ) {
         }
         boolean aliveT2 = t2.isAlive();
         ksession.halt();
         try {
             Thread.currentThread().sleep( 1000 );
         } catch ( InterruptedException e ) {
         }
         boolean aliveT1 = t1.isAlive();
         if ( t2.isAlive() ) {
             t2.interrupt();
         }
         if ( t1.isAlive() ) {
             t1.interrupt();
         }
         assertFalse( "T2 should have finished",
                      aliveT2 );
         assertFalse( "T1 should have finished",
                      aliveT1 );
     }

     @Test
     public void testFireUntilHaltFailingAcrossEntryPoints() throws Exception {
         String rule1 = "package org.drools.compiler\n";
         rule1 += "global java.util.List list\n";
         rule1 += "rule testFireUntilHalt\n";
         rule1 += "when\n";
         rule1 += "       Cheese()\n";
         rule1 += "  $p : Person() from entry-point \"testep2\"\n";
         rule1 += "then \n";
         rule1 += "  list.add( $p ) ;\n";
         rule1 += "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString(rule1);

         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         final EntryPoint ep = ksession.getEntryPoint( "testep2" );

         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         ksession.insert( new Cheese( "cheddar" ) );
         ksession.fireAllRules();

         Runnable fireUntilHalt = new Runnable() {
             public void run() {
                 ksession.fireUntilHalt();
             }
         };

         Thread t1 = new Thread( fireUntilHalt );
         t1.start();

         Thread.currentThread().sleep( 500 );
         ep.insert( new Person( "darth" ) );
         Thread.currentThread().sleep( 500 );
         ksession.halt();
         t1.join( 5000 );
         boolean alive = t1.isAlive();
         if ( alive ) {
             t1.interrupt();
         }
         assertFalse( "Thread should have died!",
                      alive );
         assertEquals( 1,
                       list.size() );
     }

     @Test
     public void testGeneratedBeansSerializable() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase("test_GeneratedBeansSerializable.drl");

         // Retrieve the generated fact type
         FactType cheeseFact = kbase.getFactType( "org.drools.generatedbeans",
                                                  "Cheese" );

         assertTrue( "Generated beans must be serializable",
                     Serializable.class.isAssignableFrom( cheeseFact.getFactClass() ) );

         // Create a new Fact instance
         Object cheese = cheeseFact.newInstance();
         cheeseFact.set( cheese,
                         "type",
                         "stilton" );

         // another instance
         Object cheese2 = cheeseFact.newInstance();
         cheeseFact.set( cheese2,
                         "type",
                         "brie" );

         // creating a stateful session
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List<Number> results = new ArrayList<Number>();
         ksession.setGlobal( "results",
                             results );

         // inserting fact
         ksession.insert( cheese );
         ksession.insert( cheese2 );

         // firing rules
         ksession.fireAllRules();

         // checking results
         assertEquals( 1,
                       results.size() );
         assertEquals( 2,
                       results.get( 0 ).intValue() );

     }

     @Test
     // this isn't possible, we can only narrow with type safety, not widen.
     // unless typesafe=false is used
             public void
             testAccessFieldsFromSubClass() throws Exception {
         // Exception in ClassFieldAccessorStore line: 116

         String rule = "";
         rule += "package org.drools.compiler;\n";
         rule += "import org.drools.compiler.Person;\n";
         rule += "import org.drools.compiler.Pet;\n";
         rule += "import org.drools.compiler.Cat;\n";
         rule += "declare Person @typesafe(false) end\n";
         rule += "rule \"Test Rule\"\n";
         rule += "when\n";
         rule += "    Person(\n";
         rule += "      pet.breed == \"Siamise\"\n";
         rule += "    )\n";
         rule += "then\n";
         rule += "System.out.println(\"hello person\");\n";
         rule += "end";

         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( rule ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         Person person = new Person();

         person.setPet( new Cat( "Mittens" ) );

         session.insert( person );

         session.fireAllRules();
     }

     @Test
     public void testClassLoaderHits() throws Exception {
         final KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
         //conf.setOption( ClassLoaderCacheOption.DISABLED );
         final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( conf );
         kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_GeneratedBeansMVEL.drl" ) ),
                       ResourceType.DRL );
         kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_GeneratedBeans.drl" ) ),
                       ResourceType.DRL );
         kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_NullFieldOnCompositeSink.drl" ) ),
                       ResourceType.DRL );
         assertFalse( kbuilder.getErrors().toString(),
                      kbuilder.hasErrors() );

         //((CompositeClassLoader)((PackageBuilderConfiguration)conf).getClassLoader()).dumpStats();

     }

     @Test
     public void testRuleChainingWithLogicalInserts() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase("test_RuleChaining.drl");

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         // create working memory mock listener
         RuleRuntimeEventListener wml = Mockito.mock( RuleRuntimeEventListener.class );
         org.kie.api.event.rule.AgendaEventListener ael = Mockito.mock( org.kie.api.event.rule.AgendaEventListener.class );

         ksession.addEventListener( wml );
         ksession.addEventListener( ael );

         int fired = ksession.fireAllRules();
         assertEquals( 3,
                       fired );

         // capture the arguments and check that the rules fired in the proper sequence
         ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> actvs = ArgumentCaptor.forClass( org.kie.api.event.rule.AfterMatchFiredEvent.class );
         verify( ael,
                 times( 3 ) ).afterMatchFired(actvs.capture());
         List<org.kie.api.event.rule.AfterMatchFiredEvent> values = actvs.getAllValues();
         assertThat( values.get( 0 ).getMatch().getRule().getName(),
                     is( "init" ) );
         assertThat( values.get( 1 ).getMatch().getRule().getName(),
                     is( "r1" ) );
         assertThat( values.get( 2 ).getMatch().getRule().getName(),
                     is( "r2" ) );

         verify( ael,
                 never() ).matchCancelled(any(org.kie.api.event.rule.MatchCancelledEvent.class));
         verify( wml,
                 times( 2 ) ).objectInserted( any( org.kie.api.event.rule.ObjectInsertedEvent.class ) );
         verify( wml,
                 never() ).objectDeleted(any(ObjectDeletedEvent.class));
     }

     @Test
     public void testLastMemoryEntryNotBug() {
         // JBRULES-2809
         // This occurs when a blocker is the last in the node's memory, or if there is only one fact in the node
         // And it gets no opportunity to rematch with itself

         String str = "";
         str += "package org.simple \n";
         str += "import " + A.class.getCanonicalName() + "\n";
         str += "global java.util.List list \n";
         str += "rule x1 \n";
         str += "when \n";
         str += "    $s : String( this == 'x1' ) \n";
         str += "    not A( this != null ) \n";
         str += "then \n";
         str += "  list.add(\"fired x1\"); \n";
         str += "end  \n";
         str += "rule x2 \n";
         str += "when \n";
         str += "    $s : String( this == 'x2' ) \n";
         str += "    not A( field1 == $s, this != null ) \n"; // this ensures an index bucket
         str += "then \n";
         str += "  list.add(\"fired x2\"); \n";
         str += "end  \n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         ksession.insert( "x1" );
         ksession.insert( "x2" );
         A a1 = new A( "x1",
                       null );
         A a2 = new A( "x2",
                       null );

         FactHandle fa1 = ksession.insert( a1 );
         FactHandle fa2 = ksession.insert( a2 );

         // make sure the 'exists' is obeyed when fact is cycled causing add/remove node memory
         ksession.update( fa1,
                          a1 );
         ksession.update( fa2,
                          a2 );
         ksession.fireAllRules();

         assertEquals( 0,
                       list.size() );

         ksession.dispose();
     }

     @Test
     public void testEventsInDifferentPackages() {
         String str = "package org.drools.compiler.test\n" +
                      "import org.drools.compiler.*\n" +
                      "declare StockTick\n" +
                      "    @role( event )\n" +
                      "end\n" +
                      "rule r1\n" +
                      "when\n" +
                      "then\n" +
                      "    StockTick st = new StockTick();\n" +
                      "    st.setCompany(\"RHT\");\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         int rules = ksession.fireAllRules();
         assertEquals( 1,
                       rules );
     }

     @Test
     public void testClassTypeAttributes() {
         String str = "package org.drools.compiler\n" +
                      "rule r1\n" +
                      "when\n" +
                      "    Primitives( classAttr == null )" +
                      "then\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( new Primitives() );
         int rules = ksession.fireAllRules();
         assertEquals( 1,
                       rules );
     }

     @Test
     public void testFreeFormExpressions() {
         String str = "package org.drools.compiler\n" +
                      "rule r1\n" +
                      "when\n" +
                      "    $p1 : Cell( row == 2 )\n" +
                      "    $p2 : Cell( row == $p1.row + 1, row == ($p1.row + 1), row == 1 + $p1.row, row == (1 + $p1.row) )\n" +
                      "then\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Cell c1 = new Cell( 1, 2, 0 );
         Cell c2 = new Cell( 1, 3, 0 );
         ksession.insert( c1 );
         ksession.insert( c2 );

         int rules = ksession.fireAllRules();
         assertEquals( 1,
                       rules );
     }

     @Test
     public void testAddMissingResourceToPackageBuilder() throws Exception {
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

         try {
             kbuilder.add( ResourceFactory.newClassPathResource("some.rf"),
                           ResourceType.DRL );
             fail( "adding a missing resource should fail" );
         } catch ( RuntimeException e ) {
         }

         try {
             kbuilder.add( ResourceFactory.newClassPathResource( "some.rf" ),
                           ResourceType.DRF );
             fail( "adding a missing resource should fail" );
         } catch ( RuntimeException e ) {
         }
     }

     @Test
     public void testPackageNameOfTheBeast() throws Exception {
         // JBRULES-2749 Various rules stop firing when they are in unlucky packagename and there is a function declared

         String ruleFileContent1 = "package org.drools.integrationtests;\n" +
                                   "function void myFunction() {\n" +
                                   "}\n" +
                                   "declare MyDeclaredType\n" +
                                   "  someProperty: boolean\n" +
                                   "end";
         String ruleFileContent2 = "package de.something;\n" + // FAILS
         //        String ruleFileContent2 = "package de.somethinga;\n" + // PASSES
         //        String ruleFileContent2 = "package de.somethingb;\n" + // PASSES
         //        String ruleFileContent2 = "package de.somethingc;\n" + // PASSES
         //        String ruleFileContent2 = "package de.somethingd;\n" + // PASSES
         //        String ruleFileContent2 = "package de.somethinge;\n" + // FAILS
         //        String ruleFileContent2 = "package de.somethingf;\n" + // FAILS
         //        String ruleFileContent2 = "package de.somethingg;\n" + // FAILS
         "import org.drools.integrationtests.*;\n" +
                                   "rule \"CheckMyDeclaredType\"\n" +
                                   "  when\n" +
                                   "    MyDeclaredType()\n" +
                                   "  then\n" +
                                   "    insertLogical(\"THIS-IS-MY-MARKER-STRING\");\n" +
                                   "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( ruleFileContent1,
                                                            ruleFileContent2 );
         StatefulKnowledgeSession knowledgeSession = createKnowledgeSession( kbase );

         final FactType myDeclaredFactType = kbase.getFactType( "org.drools.integrationtests",
                                                                "MyDeclaredType" );
         Object myDeclaredFactInstance = myDeclaredFactType.newInstance();
         knowledgeSession.insert( myDeclaredFactInstance );

         int rulesFired = knowledgeSession.fireAllRules();

         assertEquals( 1,
                       rulesFired );

         knowledgeSession.dispose();
     }

     @Test
     public void testInnerEnum() throws Exception {
         StringBuilder rule = new StringBuilder();
         rule.append( "package org.drools.compiler\n" );
         rule.append( "rule X\n" );
         rule.append( "when\n" );
         rule.append( "    Triangle( type == Triangle.Type.UNCLASSIFIED )\n" );
         rule.append( "then\n" );
         rule.append( "end\n" );

         //building stuff
         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule.toString() );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( new Triangle() );
         int rules = ksession.fireAllRules();
         assertEquals( 1, rules );
         ksession.dispose();
     }

     public static class A {
         private String field1;
         private String field2;

         public A(String field1,
                  String field2) {
             this.field1 = field1;
             this.field2 = field2;
         }

         public String getField1() {
             return field1;
         }

         public void setField1(String field1) {
             this.field1 = field1;
         }

         public String getField2() {
             return field2;
         }

         public void setField2(String field2) {
             this.field2 = field2;
         }

         public String toString() {
             return "A) " + field1 + ":" + field2;
         }
     }

     @Test
     public void testUnwantedCoersion() throws Exception {
         String rule = "package org.drools.compiler\n" +
                       "import " + MiscTest.class.getName() + ".InnerBean;\n" +
                       "import " + MiscTest.class.getName() + ".OuterBean;\n" +
                       "rule \"Test.Code One\"\n" +
                       "when\n" +
                       "   OuterBean($code : inner.code in (\"1.50\", \"2.50\"))\n" +
                       "then\n" +
                       "   System.out.println(\"Code compared values: 1.50, 2.50 - actual code value: \" + $code);\n" +
                       "end\n" +
                       "rule \"Test.Code Two\"\n" +
                       "when\n" +
                       "   OuterBean($code : inner.code in (\"1.5\", \"2.5\"))\n" +
                       "then\n" +
                       "   System.out.println(\"Code compared values: 1.5, 2.5 - actual code value: \" + $code);\n" +
                       "end\n" +
                       "rule \"Big Test ID One\"\n" +
                       "when\n" +
                       "   OuterBean($id : id in (\"3.5\", \"4.5\"))\n" +
                       "then\n" +
                       "   System.out.println(\"ID compared values: 3.5, 4.5 - actual ID value: \" + $id);\n" +
                       "end\n" +
                       "rule \"Big Test ID Two\"\n" +
                       "when\n" +
                       "   OuterBean($id : id in ( \"3.0\", \"4.0\"))\n" +
                       "then\n" +
                       "   System.out.println(\"ID compared values: 3.0, 4.0 - actual ID value: \" + $id);\n" +
                       "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         InnerBean innerTest = new InnerBean();
         innerTest.setCode( "1.500" );
         ksession.insert( innerTest );

         OuterBean outerTest = new OuterBean();
         outerTest.setId( "3" );
         outerTest.setInner( innerTest );
         ksession.insert( outerTest );

         OuterBean outerTest2 = new OuterBean();
         outerTest2.setId( "3.0" );
         outerTest2.setInner( innerTest );
         ksession.insert( outerTest2 );

         int rules = ksession.fireAllRules();
         assertEquals( 1, rules );
     }

     public static class InnerBean {
         private String code;

         public String getCode() {
             return code;
         }

         public void setCode(String code) {
             this.code = code;
         }
     }

     public static class OuterBean {
         private InnerBean inner;
         private String    id;

         public InnerBean getInner() {
             return inner;
         }

         public void setInner(InnerBean inner) {
             this.inner = inner;
         }

         public String getId() {
             return id;
         }

         public void setId(String id) {
             this.id = id;
         }
     }

     @Test
     public void testShiftOperator() throws Exception {
         String rule = "dialect \"mvel\"\n" +
                       "rule kickOff\n" +
                       "when\n" +
                       "then\n" +
                       "   insert( Integer.valueOf( 1 ) );\n" +
                       "   insert( Long.valueOf( 1 ) );\n" +
                       "   insert( Integer.valueOf( 65552 ) ); // 0x10010\n" +
                       "   insert( Long.valueOf( 65552 ) );\n" +
                       "   insert( Integer.valueOf( 65568 ) ); // 0x10020\n" +
                       "   insert( Long.valueOf( 65568 ) );\n" +
                       "   insert( Integer.valueOf( 65536 ) ); // 0x10000\n" +
                       "   insert( Long.valueOf( 65536L ) );\n" +
                       "   insert( Long.valueOf( 4294967296L ) ); // 0x100000000L\n" +
                       "end\n" +
                       "rule test1\n" +
                       "   salience -1\n" +
                       "when\n" +
                       "   $a: Integer( $one: intValue == 1 )\n" +
                       "   $b: Integer( $shift: intValue )\n" +
                       "   $c: Integer( $i: intValue, intValue == ($one << $shift ) )\n" +
                       "then\n" +
                       "   System.out.println( \"test1 \" + $a + \" << \" + $b + \" = \" + Integer.toHexString( $c ) );\n" +
                       "end\n" +
                       "rule test2\n" +
                       "   salience -2\n" +
                       "when\n" +
                       "   $a: Integer( $one: intValue == 1 )\n" +
                       "   $b: Long ( $shift: longValue )\n" +
                       "   $c: Integer( $i: intValue, intValue == ($one << $shift ) )\n" +
                       "then\n" +
                       "   System.out.println( \"test2 \" + $a + \" << \" + $b + \" = \" + Integer.toHexString( $c ) );\n" +
                       "end\n" +
                       "rule test3\n" +
                       "   salience -3\n" +
                       "when\n" +
                       "   $a: Long ( $one: longValue == 1 )\n" +
                       "   $b: Long ( $shift: longValue )\n" +
                       "   $c: Integer( $i: intValue, intValue == ($one << $shift ) )\n" +
                       "then\n" +
                       "   System.out.println( \"test3 \" + $a + \" << \" + $b + \" = \" + Integer.toHexString( $c ) );\n" +
                       "end\n" +
                       "rule test4\n" +
                       "   salience -4\n" +
                       "when\n" +
                       "   $a: Long ( $one: longValue == 1 )\n" +
                       "   $b: Integer( $shift: intValue )\n" +
                       "   $c: Integer( $i: intValue, intValue == ($one << $shift ) )\n" +
                       "then\n" +
                       "   System.out.println( \"test4 \" + $a + \" << \" + $b + \" = \" + Integer.toHexString( $c ) );\n" +
                       "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
         int rules = ksession.fireAllRules();
         assertEquals( 13, rules );
     }

     public interface InterfaceA {
         InterfaceB getB();
     }

     public interface InterfaceB {
     }

     public static class ClassA
             implements
             InterfaceA {
         private ClassB b = null;

         public ClassB getB() {
             return b;
         }

         public void setB(InterfaceB b) {
             this.b = (ClassB) b;
         }
     }

     public static class ClassB
             implements
             InterfaceB {
         private String id = "123";

         public String getId() {
             return id;
         }

         public void setId(String id) {
             this.id = id;
         }

         @Override
         public boolean equals(Object o) {
             if ( this == o ) return true;
             if ( o == null || getClass() != o.getClass() ) return false;

             ClassB classB = (ClassB) o;

             if ( id != null ? !id.equals( classB.id ) : classB.id != null ) return false;

             return true;
         }

         @Override
         public int hashCode() {
             return Integer.valueOf( id );
         }
     }

     @Test
     public void testCovariance() throws Exception {
         // JBRULES-3392
         String str =
                 "import " + MiscTest.class.getName() + ".*\n" +
                         "rule x\n" +
                         "when\n" +
                         "   $b : ClassB( )\n" +
                         "   $a : ClassA( b.id == $b.id )\n" +
                         "then\n" +
                         "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ClassA a = new ClassA();
         ClassB b = new ClassB();
         a.setB( b );

         ksession.insert( a );
         ksession.insert( b );
         assertEquals( 1, ksession.fireAllRules() );
     }

     public static class Parent {
     }

     public static class ChildA extends Parent {
         private final int x;

         public ChildA(int x) {
             this.x = x;
         }

         public int getX() {
             return x;
         }
     }

     public static class ChildB extends Parent {
         private final int x;

         public ChildB(int x) {
             this.x = x;
         }

         public int getX() {
             return x;
         }
     }

     @Test
     public void testTypeUnsafe() throws Exception {
         String str = "import " + MiscTest.class.getName() + ".*\n" +
                      "declare\n" +
                      "   Parent @typesafe(false)\n" +
                      "end\n" +
                      "rule R1\n" +
                      "when\n" +
                      "   $a : Parent( x == 1 )\n" +
                      "then\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         for ( int i = 0; i < 20; i++ ) {
             ksession.insert( new ChildA( i % 10 ) );
             ksession.insert( new ChildB( i % 10 ) );
         }

         assertEquals( 4, ksession.fireAllRules() );

         // give time to async jitting to complete
         Thread.sleep( 100 );

         ksession.insert( new ChildA( 1 ) );
         ksession.insert( new ChildB( 1 ) );
         assertEquals( 2, ksession.fireAllRules() );
     }

     @Test
     public void testConstructorWithOtherDefaults() {
         String str = "" +
                      "\n" +
                      "global java.util.List list;\n" +
                      "\n" +
                      "declare Bean\n" +
                      "   kField : String     @key\n" +
                      "   sField : String     = \"a\"\n" +
                      "   iField : int        = 10\n" +
                      "   dField : double     = 4.32\n" +
                      "   aField : Long[]     = new Long[] { 100L, 1000L }\n" +
                      "end" +
                      "\n" +
                      "rule \"Trig\"\n" +
                      "when\n" +
                      "    Bean( kField == \"key\", sField == \"a\", iField == 10, dField == 4.32, aField[1] == 1000L ) \n" +
                      "then\n" +
                      "    list.add( \"OK\" );\n" +
                      "end\n" +
                      "\n" +
                      "rule \"Exec\"\n" +
                      "when\n" +
                      "then\n" +
                      "    insert( new Bean( \"key\") ); \n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         java.util.List list = new java.util.ArrayList();
         ksession.setGlobal( "list", list );

         ksession.fireAllRules();
         assertTrue( list.contains( "OK" ) );

         ksession.dispose();
     }

     @Test
     public void testCoercionOfStringValueWithoutQuotes() throws Exception {
         // JBRULES-3080
         String str = "package org.drools.compiler.test; \n" +
                      "declare A\n" +
                      "   field : String\n" +
                      "end\n" +
                      "rule R when\n" +
                      "   A( field == 12 )\n" +
                      "then\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         FactType typeA = kbase.getFactType( "org.drools.compiler.test", "A" );
         Object a = typeA.newInstance();
         typeA.set( a, "field", "12" );
         ksession.insert( a );

         assertEquals( 1, ksession.fireAllRules() );
     }

     @Test
     public void testPrimitiveToBoxedCoercionInMethodArgument() throws Exception {
         String str = "package org.drools.compiler.test;\n" +
                      "import " + MiscTest.class.getName() + "\n" +
                      "import org.drools.compiler.*\n" +
                      "rule R1 when\n" +
                      "   Person( $ag1 : age )" +
                      "   $p2 : Person( name == MiscTest.integer2String($ag1) )" +
                      "then\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         Person p = new Person( "42", 42 );
         ksession.insert( p );
         assertEquals( 1, ksession.fireAllRules() );
     }

     public static String integer2String(Integer value) {
         return "" + value;
     }

     @Test
     public void testKeyedInterfaceField() {
         //JBRULES-3441
         String str = "package org.drools.compiler.integrationtest; \n" +
                      "\n" +
                      "import " + MiscTest.class.getName() + ".*; \n" +
                      "" +
                      "global java.util.List list;" +
                      "" +
                      "declare Bean\n" +
                      "  id    : InterfaceB @key\n" +
                      "end\n" +
                      "\n" +
                      "\n" +
                      "rule \"Init\"\n" +
                      "when  \n" +
                      "then\n" +
                      "  insert( new Bean( new ClassB() ) );\n" +
                      "end\n" +
                      "\n" +
                      "rule \"Check\"\n" +
                      "when\n" +
                      "  $b : Bean( )\n" +
                      "then\n" +
                      "  list.add( $b.hashCode() ); \n" +
                      "  list.add( $b.equals( new Bean( new ClassB() ) ) ); \n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         java.util.List list = new java.util.ArrayList();
         ksession.setGlobal( "list", list );

         ksession.fireAllRules();
         assertTrue( list.contains( 31 + 123 ) );
         assertTrue( list.contains( true ) );

         ksession.dispose();
     }

     @Test
     public void testCheckDuplicateVariables() throws Exception {
         // JBRULES-3035
         String str = "package com.sample\n" +
                      "import org.drools.compiler.*\n" +
                      "rule R1 when\n" +
                      "   Person( $a: age, $a: name ) // this should cause a compile-time error\n" +
                      "then\n" +
                      "end";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
         assertTrue( kbuilder.hasErrors() );

         str = "package com.sample\n" +
               "rule R1 when\n" +
               "   accumulate( Object(), $c: count(1), $c: max(1) ) // this should cause a compile-time error\n" +
               "then\n" +
               "end";

         kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
         assertTrue( kbuilder.hasErrors() );

         str = "package com.sample\n" +
               "rule R1 when\n" +
               "   Number($i: intValue) from accumulate( Object(), $i: count(1) ) // this should cause a compile-time error\n" +
               "then\n" +
               "end";

         kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
         assertTrue( kbuilder.hasErrors() );
     }

     public static class MapContainerBean {
         private final Map<Integer, String> map = new HashMap<Integer, String>();

         MapContainerBean() {
             map.put( 1, "one" );
             map.put( 2, "two" );
             map.put( 3, "three" );
         }

         public Map<Integer, String> getMap() {
             return map;
         }

         public int get3() {
             return 3;
         }
     }

     @Test
     public void testEntryPointWithVarIN() {
         String str = "package org.drools.compiler.test;\n" +
                      "\n" +
                      "global java.util.List list;\n" +
                      "\n" +
                      "rule \"In\"\n" +
                      "when\n" +
                      "   $x : Integer()\n " +
                      "then\n" +
                      "   drools.getEntryPoint(\"inX\").insert( $x );\n" +
                      "end\n" +
                      "\n" +
                      "rule \"Out\"\n" +
                      "when\n" +
                      "   $i : Integer() from entry-point \"inX\"\n" +
                      "then\n" +
                      "   list.add( $i );\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.insert( 10 );

         List res = new ArrayList();
         ksession.setGlobal( "list", res );

         ksession.fireAllRules();
         ksession.dispose();
         assertTrue( res.contains( 10 ) );
     }

     @Test
     public void testGenericsOption() throws Exception {
         // JBRULES-3579
         String str = "import org.drools.compiler.*;\n" +
                      "rule R1 when\n" +
                      "   $c : Cheese( $type: type )\n" +
                      "   $p : Person( $name : name, addressOption.get.street == $type )\n" +
                      "then\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         Person p = new Person( "x" );
         p.setAddress( new Address( "x", "x", "x" ) );
         ksession.insert( p );

         ksession.insert( new Cheese( "x" ) );
         assertEquals( 1, ksession.fireAllRules() );
         ksession.dispose();
     }

     @Test
     public void noDormantCheckOnModifies() throws Exception {
         // Test case for BZ 862325
         String str = "package org.drools.compiler;\n"
                      + " rule R1\n"
                      + "    salience 10\n"
                      + "    when\n"
                      + "        $c : Cheese( price == 10 ) \n"
                      + "        $p : Person( ) \n"
                      + "    then \n"
                      + "        modify($c) { setPrice( 5 ) }\n"
                      + "        modify($p) { setAge( 20 ) }\n"
                      + "end\n"
                      + "rule R2\n"
                      + "    when\n"
                      + "        $p : Person( )"
                      + "    then \n"
                      + "        // noop\n"
                      + "end\n";
         // load up the knowledge base
         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         org.kie.api.event.rule.AgendaEventListener ael = mock( org.kie.api.event.rule.AgendaEventListener.class );
         ksession.addEventListener( ael );

         ksession.insert( new Person( "Bob", 19 ) );
         ksession.insert( new Cheese( "brie", 10 ) );
         ksession.fireAllRules();

         // both rules should fire exactly once
         verify( ael, times( 2 ) ).afterMatchFired(any(org.kie.api.event.rule.AfterMatchFiredEvent.class));
         // no cancellations should have happened
         verify( ael, never() ).matchCancelled( any( org.kie.api.event.rule.MatchCancelledEvent.class ) );
     }

     @Test
     public void testCompilationFailureOnTernaryComparison() {
         // JBRULES-3642
         String str =
                 "declare Cont\n" +
                         "  val:Integer\n" +
                         "end\n" +
                         "rule makeFacts\n" +
                         "salience 10\n" +
                         "when\n" +
                         "then\n" +
                         "    insert( new Cont(2) );\n" +
                         "end\n" +
                         "rule R1\n" +
                         "when\n" +
                         "    $c: Cont( 3 < val < 10 )\n" +
                         "then\n" +
                         "end";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
         assertTrue( kbuilder.hasErrors() );
     }
 }