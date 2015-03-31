/*
 * Copyright 2005 JBoss Inc
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

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.compiler.Address;
import org.drools.compiler.Attribute;
import org.drools.compiler.Bar;
import org.drools.compiler.Cat;
import org.drools.compiler.Cell;
import org.drools.compiler.Cheese;
import org.drools.compiler.CheeseEqual;
import org.drools.compiler.Cheesery;
import org.drools.compiler.Cheesery.Maturity;
import org.drools.compiler.Child;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.DomainObjectHolder;
import org.drools.compiler.FactA;
import org.drools.compiler.FactB;
import org.drools.compiler.FactC;
import org.drools.compiler.FirstClass;
import org.drools.compiler.Foo;
import org.drools.compiler.FromTestClass;
import org.drools.compiler.Guess;
import org.drools.compiler.IndexedNumber;
import org.drools.compiler.LongAddress;
import org.drools.compiler.Message;
import org.drools.compiler.MockPersistentSet;
import org.drools.compiler.Move;
import org.drools.compiler.ObjectWithSet;
import org.drools.compiler.Order;
import org.drools.compiler.OrderItem;
import org.drools.compiler.OuterClass;
import org.drools.compiler.Person;
import org.drools.compiler.PersonFinal;
import org.drools.compiler.PersonInterface;
import org.drools.compiler.PersonWithEquals;
import org.drools.compiler.Pet;
import org.drools.compiler.PolymorphicFact;
import org.drools.compiler.Primitives;
import org.drools.compiler.RandomNumber;
import org.drools.compiler.SecondClass;
import org.drools.compiler.Sensor;
import org.drools.compiler.SpecialString;
import org.drools.compiler.State;
import org.drools.compiler.StockTick;
import org.drools.compiler.Target;
import org.drools.compiler.TestParam;
import org.drools.compiler.Triangle;
import org.drools.compiler.Win;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.ParserError;
import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialectConfiguration;
import org.drools.core.ClassObjectFilter;
import org.drools.core.audit.WorkingMemoryConsoleLogger;
import org.drools.core.base.RuleNameEndsWithAgendaFilter;
import org.drools.core.base.RuleNameEqualsAgendaFilter;
import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.IdentityPlaceholderResolverStrategy;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.MapBackedClassLoader;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.command.Setter;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.RemoveIdentitiesOption;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.conf.SequentialOption;
import org.kie.internal.conf.ShareAlphaNodesOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
  * Run all the tests with the ReteOO engine implementation
  */
 public class MiscTest extends CommonTestMethodBase {

     private static Logger logger = LoggerFactory.getLogger( MiscTest.class );

     @Test
     public void testImportFunctions() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ImportFunctions.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         final Cheese cheese = new Cheese( "stilton",
                                           15 );
         session.insert( cheese );
         List list = new ArrayList();
         session.setGlobal( "list",
                            list );
         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );
         int fired = session.fireAllRules();

         list = (List) session.getGlobal( "list" );

         assertEquals( 4,
                       fired );
         assertEquals( 4,
                       list.size() );

         assertEquals( "rule1",
                       list.get( 0 ) );
         assertEquals( "rule2",
                       list.get( 1 ) );
         assertEquals( "rule3",
                       list.get( 2 ) );
         assertEquals( "rule4",
                       list.get( 3 ) );
     }

     @Test
     public void testStaticFieldReference() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_StaticField.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         // will test serialisation of int and typesafe enums tests
         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );

         List list = new ArrayList();
         session.setGlobal( "list",
                            list );

         final Cheesery cheesery1 = new Cheesery();
         cheesery1.setStatus( Cheesery.SELLING_CHEESE );
         cheesery1.setMaturity( Maturity.OLD );
         session.insert( cheesery1 );
         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );

         final Cheesery cheesery2 = new Cheesery();
         cheesery2.setStatus( Cheesery.MAKING_CHEESE );
         cheesery2.setMaturity( Maturity.YOUNG );
         session.insert( cheesery2 );
         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );

         session.fireAllRules();

         assertEquals( 2,
                       list.size() );

         assertEquals( cheesery1,
                       list.get( 0 ) );
         assertEquals( cheesery2,
                       list.get( 1 ) );
     }

     @Test
     public void testMetaConsequence() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_MetaConsequence.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );
         List results = new ArrayList();
         session.setGlobal( "results",
                            results );

         session.insert( new Person( "Michael" ) );

         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );
         results = (List) session.getGlobal( "results" );

         session.fireAllRules();
         assertEquals( 2,
                       results.size() );
         assertEquals( "bar",
                       (results.get( 0 )) );
         assertEquals( "bar2",
                       (results.get( 1 )) );

     }

     @Test
     public void testEnabledExpression() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_enabledExpression.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );
         List results = new ArrayList();
         session.setGlobal( "results",
                            results );

         session.insert( new Person( "Michael" ) );

         results = (List) session.getGlobal( "results" );

         session.fireAllRules();
         assertEquals( 3,
                       results.size() );
         assertTrue( results.contains( "1" ) );
         assertTrue( results.contains( "2" ) );
         assertTrue( results.contains( "3" ) );

     }

     @Test
     public void testGetStatefulKnowledgeSessions() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "empty.drl" ) );

         StatefulKnowledgeSession ksession_1 = createKnowledgeSession( kbase );
         String expected_1 = "expected_1";
         String expected_2 = "expected_2";
         FactHandle handle_1 = ksession_1.insert( expected_1 );
         FactHandle handle_2 = ksession_1.insert( expected_2 );
         ksession_1.fireAllRules();
         Collection<StatefulKnowledgeSession> coll_1 = kbase.getStatefulKnowledgeSessions();
         assertTrue( coll_1.size() == 1 );

         StatefulKnowledgeSession ksession_2 = coll_1.iterator().next();
         Object actual_1 = ksession_2.getObject( handle_1 );
         Object actual_2 = ksession_2.getObject( handle_2 );
         assertEquals( expected_1,
                       actual_1 );
         assertEquals( expected_2,
                       actual_2 );

         ksession_1.dispose();
         Collection<StatefulKnowledgeSession> coll_2 = kbase.getStatefulKnowledgeSessions();
         assertTrue( coll_2.size() == 0 );

         // here to make sure it's safe to call dispose() twice
         ksession_1.dispose();
         Collection<StatefulKnowledgeSession> coll_3 = kbase.getStatefulKnowledgeSessions();
         assertTrue( coll_3.size() == 0 );
     }

     @Test
     public void testGetFactHandle() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "empty.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         for ( int i = 0; i < 20; i++ ) {
             Object object = new Object();
             ksession.insert( object );
             FactHandle factHandle = ksession.getFactHandle( object );
             assertNotNull( factHandle );
             assertEquals( object,
                           ksession.getObject( factHandle ) );
         }
         ksession.dispose();
     }

     @Test
     public void testPrimitiveArray() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_primitiveArray.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         List result = new ArrayList();
         session.setGlobal( "result",
                            result );

         final Primitives p1 = new Primitives();
         p1.setPrimitiveIntArray( new int[]{1, 2, 3} );
         p1.setArrayAttribute( new String[]{"a", "b"} );

         session.insert( p1 );

         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );
         result = (List) session.getGlobal( "result" );

         session.fireAllRules();
         assertEquals( 3,
                       result.size() );
         assertEquals( 3,
                       ((Integer) result.get( 0 )).intValue() );
         assertEquals( 2,
                       ((Integer) result.get( 1 )).intValue() );
         assertEquals( 3,
                       ((Integer) result.get( 2 )).intValue() );

     }

     @Test
     public void testMVELSoundex() throws Exception {

         // read in the source
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "MVEL_soundex.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                               true );

         Cheese c = new Cheese( "fubar",
                                2 );

         ksession.insert( c );
         ksession.fireAllRules();
         assertEquals( 42,
                       c.getPrice() );
     }

     @Test
     public void testMVELSoundexNoCharParam() throws Exception {

         // read in the source
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "MVEL_soundexNPE2500.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                               true );

         Cheese foobarCheese = new Cheese( "foobar",
                                           2 );
         Cheese nullCheese = new Cheese( null,
                                         2 );
         Cheese starCheese = new Cheese( "*",
                                         2 );

         ksession.insert( foobarCheese );
         ksession.insert( nullCheese );
         ksession.insert( starCheese );
         ksession.fireAllRules();
         assertEquals( 42,
                       foobarCheese.getPrice() );
         assertEquals( 2,
                       nullCheese.getPrice() );
         assertEquals( 2,
                       starCheese.getPrice() );
     }

     @Test
     public void testMVELRewrite() throws Exception {

         // read in the source
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_MVELrewrite.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                               true );
         List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         Cheese brie = new Cheese( "brie",
                                   2 );
         Cheese stilton = new Cheese( "stilton",
                                      2 );
         Cheesery cheesery = new Cheesery();
         cheesery.addCheese( brie );
         cheesery.addCheese( stilton );

         ksession.insert( cheesery );
         ksession.fireAllRules();

         assertEquals( 1,
                       results.size() );
         assertEquals( cheesery,
                       results.get( 0 ) );
     }

     @Test
     public void testVariableDeclaration() throws Exception {
         String str = "rule KickOff\n" +
                      "dialect \"mvel\"\n" +
                      "when\n" +
                      "then\n" +
                      "int i;\n" +
                      "end";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                       ResourceType.DRL );

         if ( kbuilder.hasErrors() ) {
             fail( kbuilder.getErrors().toString() );
         }
     }

     @Test
     public void testMissingImport() throws Exception {
         String str = "";
         str += "package org.drools.compiler \n";
         str += "import " + Person.class.getName() + "\n";
         str += "global java.util.List list \n";
         str += "rule rule1 \n";
         str += "when \n";
         str += "    $i : Cheese() \n";
         str += "         MissingClass( fieldName == $i ) \n";
         str += "then \n";
         str += "    list.add( $i ); \n";
         str += "end \n";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                       ResourceType.DRL );

         if ( kbuilder.hasErrors() ) {
             logger.warn( kbuilder.getErrors().toString() );
         }
         assertTrue( kbuilder.hasErrors() );
     }

     @Test
     public void testInvalidModify1() throws Exception {
         String str = "";
         str += "package org.drools.compiler \n";
         str += "import " + Cheese.class.getName() + "\n";
         str += "global java.util.List list \n";
         str += "rule rule1 \n";
         str += "    no-loop \n";
         str += "when \n";
         str += "    $i : Cheese() \n";
         str += "then \n";
         str += "    modify( $i ); ";
         str += "    list.add( $i ); \n";
         str += "end \n";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                       ResourceType.DRL );

         if ( kbuilder.hasErrors() ) {
             logger.warn( kbuilder.getErrors().toString() );
         }
         assertTrue( kbuilder.hasErrors() );
     }

     @Test
     public void testInvalidModify2() throws Exception {
         String str = "";
         str += "package org.drools.compiler \n";
         str += "import " + Cheese.class.getName() + "\n";
         str += "global java.util.List list \n";
         str += "rule rule1 \n";
         str += "    no-loop \n";
         str += "when \n";
         str += "    $i : Cheese() \n";
         str += "then \n";
         str += "    modify( $i ) { setType( \"stilton\" ); setType( \"stilton\" );}; ";
         str += "    list.add( $i ); \n";
         str += "end \n";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                       ResourceType.DRL );

         if ( kbuilder.hasErrors() ) {
             logger.warn( kbuilder.getErrors().toString() );
         }
         assertTrue( kbuilder.hasErrors() );
     }

     @Test
     public void testIncrementOperator() throws Exception {
         String str = "";
         str += "package org.drools.compiler \n";
         str += "global java.util.List list \n";
         str += "rule rule1 \n";
         str += "    dialect \"java\" \n";
         str += "when \n";
         str += "    $I : Integer() \n";
         str += "then \n";
         str += "    int i = $I.intValue(); \n";
         str += "    i += 5; \n";
         str += "    list.add( i ); \n";
         str += "end \n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );
         ksession.insert( 5 );

         ksession.fireAllRules();

         assertEquals( 1,
                       list.size() );
         assertEquals( 10,
                       list.get( 0 ) );
     }

     @Test
     public void testKnowledgeRuntimeAccess() throws Exception {
         String str = "";
         str += "package org.drools.compiler.test\n";
         str += "import " + Message.class.getName() + "\n";
         str += "rule \"Hello World\"\n";
         str += "when\n";
         str += "    Message( )\n";
         str += "then\n";
         str += "    System.out.println( drools.getKieRuntime() );\n";
         str += "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

         kbase = SerializationHelper.serializeObject( kbase );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( new Message( "help" ) );
         ksession.fireAllRules();
         ksession.dispose();
     }

     @Test
     public void testEvalWithBigDecimal() throws Exception {
         String str = "";
         str += "package org.drools.compiler \n";
         str += "import java.math.BigDecimal; \n";
         str += "global java.util.List list \n";
         str += "rule rule1 \n";
         str += "    dialect \"java\" \n";
         str += "when \n";
         str += "    $bd : BigDecimal() \n";
         str += "    eval( $bd.compareTo( BigDecimal.ZERO ) > 0 ) \n";
         str += "then \n";
         str += "    list.add( $bd ); \n";
         str += "end \n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );
         ksession.insert( new BigDecimal( 1.5 ) );

         ksession.fireAllRules();

         assertEquals( 1,
                       list.size() );
         assertEquals( new BigDecimal( 1.5 ),
                       list.get( 0 ) );
     }

     @Test
     public void testFieldBiningsAndEvalSharing() throws Exception {
         final String drl = "test_FieldBindingsAndEvalSharing.drl";
         evalSharingTest( drl );
     }

     @Test
     public void testFieldBiningsAndPredicateSharing() throws Exception {
         final String drl = "test_FieldBindingsAndPredicateSharing.drl";
         evalSharingTest( drl );
     }

     private void evalSharingTest(final String drl) throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( drl );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         final TestParam tp1 = new TestParam();
         tp1.setValue2( "boo" );
         ksession.insert( tp1 );

         ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                               true );
         ksession.fireAllRules();

         assertEquals( 1,
                       ((List) ksession.getGlobal( "list" )).size() );
     }

     @Test
     public void testGeneratedBeans1() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase("test_GeneratedBeans.drl");

         // Retrieve the generated fact type
         FactType cheeseFact = kbase.getFactType( "org.drools.generatedbeans",
                                                  "Cheese" );

         // Create a new Fact instance
         Object cheese = cheeseFact.newInstance();

         // Set a field value using the more verbose method chain...
         // should we add short cuts?
         //        cheeseFact.getField( "type" ).set( cheese,
         //                                           "stilton" );

         cheeseFact.set( cheese,
                         "type",
                         "stilton" );
         assertEquals( "stilton",
                       cheeseFact.get( cheese,
                                       "type" ) );

         FactType personType = kbase.getFactType( "org.drools.generatedbeans",
                                                  "Person" );

         Object ps = personType.newInstance();
         personType.set( ps,
                         "age",
                         42 );

         Map<String, Object> personMap = personType.getAsMap( ps );
         assertEquals( 42,
                       personMap.get( "age" ) );

         personMap.put( "age",
                        43 );
         personType.setFromMap( ps,
                                personMap );

         assertEquals( 43,
                       personType.get( ps,
                                       "age" ) );

         // just documenting toString() result:
         //        assertEquals( "Cheese( type=stilton )",
         //                      cheese.toString() );

         // reading the field attribute, using the method chain
         assertEquals( "stilton",
                       cheeseFact.getField( "type" ).get( cheese ) );

         // creating a stateful session
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         Object cg = cheeseFact.newInstance();
         ksession.setGlobal( "cg",
                             cg );
         List<Object> result = new ArrayList<Object>();
         ksession.setGlobal( "list",
                             result );

         // inserting fact
         ksession.insert( cheese );

         // firing rules
         ksession.fireAllRules();

         // checking results
         assertEquals( 1,
                       result.size() );
         assertEquals( new Integer( 5 ),
                       result.get( 0 ) );

         // creating a person that likes the cheese:
         // Retrieve the generated fact type
         FactType personFact = kbase.getFactType( "org.drools.generatedbeans",
                                                  "Person" );

         // Create a new Fact instance
         Object person = personFact.newInstance();

         // Set a field value using the more verbose method chain...
         // should we add short cuts?
         personFact.getField( "likes" ).set( person,
                                             cheese );
         // demonstrating primitive type support
         personFact.getField( "age" ).set( person,
                                           7 );

         // just documenting toString() result:
         //        assertEquals( "Person( age=7, likes=Cheese( type=stilton ) )",
         //                      person.toString() );

         // inserting fact
         ksession.insert( person );

         // firing rules
         ksession.fireAllRules();

         // checking results
         assertEquals( 2,
                       result.size() );
         assertEquals( person,
                       result.get( 1 ) );

     }

     @Test
     public void testGeneratedBeansMVEL() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase("test_GeneratedBeansMVEL.drl");

         // Retrieve the generated fact type
         FactType pf = kbase.getFactType( "mortgages",
                                          "Applicant" );
         FactType af = kbase.getFactType( "mortgages",
                                          "LoanApplication" );

         Object person = pf.newInstance();
         pf.set( person,
                 "creditRating",
                 "OK" );

         Object application = af.newInstance();
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         ksession.insert( person );
         ksession.insert( application );

         ksession.fireAllRules();
     }

     @Test
     public void testGeneratedBeans2() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase("test_GeneratedBeans2.drl");

         // Retrieve the generated fact type
         FactType cheeseFact = kbase.getFactType( "org.drools.generatedbeans",
                                                  "Cheese" );

         // Create a new Fact instance
         Object cheese = cheeseFact.newInstance();

         cheeseFact.set( cheese,
                         "type",
                         "stilton" );
         assertEquals( "stilton",
                       cheeseFact.get( cheese,
                                       "type" ) );

         // testing equals method
         Object cheese2 = cheeseFact.newInstance();
         cheeseFact.set( cheese2,
                         "type",
                         "stilton" );
         assertEquals( cheese,
                       cheese2 );

         FactType personType = kbase.getFactType( "org.drools.generatedbeans",
                                                  "Person" );

         Object ps = personType.newInstance();
         personType.set( ps,
                         "name",
                         "mark" );
         personType.set( ps,
                         "last",
                         "proctor" );
         personType.set( ps,
                         "age",
                         42 );

         Object ps2 = personType.newInstance();
         personType.set( ps2,
                         "name",
                         "mark" );
         personType.set( ps2,
                         "last",
                         "proctor" );
         personType.set( ps2,
                         "age",
                         30 );

         assertEquals( ps,
                       ps2 );

         personType.set( ps2,
                         "last",
                         "little" );

         assertFalse( ps.equals( ps2 ) );

         // creating a stateful session
         StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
         Object cg = cheeseFact.newInstance();
         wm.setGlobal( "cg",
                       cg );
         List result = new ArrayList();
         wm.setGlobal( "list",
                       result );

         // inserting fact
         wm.insert( cheese );

         // firing rules
         wm.fireAllRules();

         // checking results
         assertEquals( 1,
                       result.size() );
         assertEquals( new Integer( 5 ),
                       result.get( 0 ) );

         // creating a person that likes the cheese:
         // Retrieve the generated fact type
         FactType personFact = kbase.getFactType( "org.drools.generatedbeans",
                                                  "Person" );

         // Create a new Fact instance
         Object person = personFact.newInstance();

         // Set a field value using the more verbose method chain...
         // should we add short cuts?
         personFact.getField( "likes" ).set( person,
                                             cheese );
         // demonstrating primitive type support
         personFact.getField( "age" ).set( person,
                                           7 );

         // just documenting toString() result:
         //        assertEquals( "Person( age=7, likes=Cheese( type=stilton ) )",
         //                      person.toString() );

         // inserting fact
         wm.insert( person );

         // firing rules
         wm.fireAllRules();

         // checking results
         assertEquals( 2,
                       result.size() );
         assertEquals( person,
                       result.get( 1 ) );

     }

     @Test
     public void testDeclaredFactAndFunction() throws Exception {
         String rule = "package com.jboss.qa;\n";
         rule += "global java.util.List list\n";
         rule += "declare Address\n";
         rule += "    street: String\n";
         rule += "end\n";
         rule += "function void myFunction() {\n";
         rule += "}\n";
         rule += "rule \"r1\"\n";
         rule += "    dialect \"mvel\"\n";
         rule += "when\n";
         rule += "    Address()\n";
         rule += "then\n";
         rule += "    list.add(\"r1\");\n";
         rule += "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         List list = new ArrayList();
         session.setGlobal( "list",
                            list );

         FactType addressFact = kbase.getFactType( "com.jboss.qa", "Address" );
         Object address = addressFact.newInstance();
         session.insert( address );
         session.fireAllRules();

         list = (List) session.getGlobal( "list" );
         assertEquals( 1,
                       list.size() );

         assertEquals( "r1",
                       list.get( 0 ) );
     }

     @Test
     public void testTypeDeclarationOnSeparateResource() throws Exception {
         String file1 = "package a.b.c\n" +
                        "declare SomePerson\n" +
                        "    weight : double\n" +
                        "    height : double\n" +
                        "end\n";
         String file2 = "package a.b.c\n" +
                        "import org.drools.compiler.*\n" +
                        "declare Holder\n" +
                        "    person : Person\n" +
                        "end\n" +
                        "rule \"create holder\"\n" +
                        "    when\n" +
                        "        person : Person( )\n" +
                        "        not (\n" +
                        "            Holder( person; )\n" +
                        "        )\n" +
                        "    then\n" +
                        "        insert(new Holder(person));\n" +
                        "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( file1, file2 );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         assertEquals( 0,
                       ksession.fireAllRules() );
         ksession.insert( new Person( "Bob" ) );
         assertEquals( 1,
                       ksession.fireAllRules() );
         assertEquals( 0,
                       ksession.fireAllRules() );

     }

     @Test
     public void testUppercaseField() throws Exception {
         String rule = "package org.drools.compiler.test;\n";
         rule += "global java.util.List list\n";
         rule += "declare Address\n";
         rule += "    Street: String\n";
         rule += "end\n";
         rule += "rule \"r1\"\n";
         rule += "when\n";
         rule += "    Address($street: Street)\n";
         rule += "then\n";
         rule += "    list.add($street);\n";
         rule += "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.setGlobal( "list",
                             new ArrayList<String>() );

         FactType addressType = kbase.getFactType( "org.drools.compiler.test",
                                                   "Address" );
         Object address = addressType.newInstance();
         addressType.set( address,
                          "Street",
                          "5th Avenue" );

         ksession.insert( address );

         ksession.fireAllRules();

         List list = (List) ksession.getGlobal( "list" );
         assertEquals( 1,
                       list.size() );
         assertEquals( "5th Avenue",
                       list.get( 0 ) );

         ksession.dispose();
     }

     @Test
     public void testUppercaseField2() throws Exception {
         String rule = "package org.drools.compiler\n" +
                       "declare SomeFact\n" +
                       "    Field : String\n" +
                       "    aField : String\n" +
                       "end\n" +
                       "rule X\n" +
                       "when\n" +
                       "    SomeFact( Field == \"foo\", aField == \"bar\" )\n" +
                       "then\n" +
                       "end\n";
         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         FactType factType = kbase.getFactType( "org.drools.compiler",
                                                "SomeFact" );
         Object fact = factType.newInstance();
         factType.set( fact,
                       "Field",
                       "foo" );
         factType.set( fact,
                       "aField",
                       "bar" );

         ksession.insert( fact );
         int rules = ksession.fireAllRules();

         assertEquals( 1,
                       rules );
         ksession.dispose();
     }

     @Test
     public void testNullHandling() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "test_NullHandling.drl" );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         session.setGlobal( "list",
                            list );
         final Cheese nullCheese = new Cheese( null,
                                               2 );
         session.insert( nullCheese );

         final Person notNullPerson = new Person( "shoes butt back" );
         notNullPerson.setBigDecimal( new BigDecimal( "42.42" ) );

         session.insert( notNullPerson );

         Person nullPerson = new Person( "whee" );
         nullPerson.setBigDecimal( null );

         session.insert( nullPerson );

         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );
         session.fireAllRules();
         //System.out.println(((List) session.getGlobal("list")).get(0));
         assertEquals( 3,
                       ((List) session.getGlobal( "list" )).size() );

         nullPerson = new Person( null );

         session.insert( nullPerson );
         session.fireAllRules();
         assertEquals( 4,
                       ((List) session.getGlobal( "list" )).size() );

     }

     @Test
     public void testNullFieldOnCompositeSink() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase("test_NullFieldOnCompositeSink.drl");

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         ksession.insert( new Attribute() );
         ksession.insert( new Message() );
         ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                               true );
         ksession.fireAllRules();

         assertEquals( 1,
                       ((List) ksession.getGlobal( "list" )).size() );
         assertEquals( "X",
                       ((List) ksession.getGlobal( "list" )).get( 0 ) );

     }

     @Test
     public void testEmptyPattern() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "test_EmptyPattern.drl" );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         List list = new ArrayList();
         session.setGlobal( "list",
                            list );

         final Cheese stilton = new Cheese( "stilton",
                                            5 );
         session.insert( stilton );

         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );
         session.fireAllRules();

         assertEquals( 5,
                       ((List) session.getGlobal( "list" )).get( 0 ) );
     }

     @Test
     public void testExplicitAnd() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase("test_ExplicitAnd.drl");

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         final List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );
         ksession.insert( new Message( "hola" ) );

         ksession.fireAllRules();
         assertEquals( 0,
                       list.size() );

         ksession.insert( new Cheese( "brie",
                                      33 ) );

         ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                               true );
         ksession.fireAllRules();
         assertEquals( 1,
                       ((List) ksession.getGlobal( "list" )).size() );
     }

     @Test
     public void testHelloWorld() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase("HelloWorld.drl");

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         // go !
         final Message message = new Message( "hola" );
         message.addToList( "hello" );
         message.setNumber( 42 );

         ksession.insert( message );
         ksession.insert( "boo" );
         //        ksession    = SerializationHelper.serializeObject(ksession);
         ksession.fireAllRules();
         assertTrue( message.isFired() );
         assertEquals( message,
                       ((List) ksession.getGlobal( "list" )).get( 0 ) );

     }

     @Test
     public void testExtends() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "extend_rule_test.drl" );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         //Test 2 levels of inheritance, and basic rule
         List list = new ArrayList();
         session.setGlobal( "list",
                            list );
         final Cheese mycheese = new Cheese( "cheddar",
                                             4 );
         FactHandle handle = session.insert( mycheese );
         session.fireAllRules();

         assertEquals( 2,
                       list.size() );
         assertTrue( list.contains( "rule 4" ) );
         assertTrue( list.contains( "rule 2b" ) );

         //Test 2nd level (parent) to make sure rule honors the extend rule
         list = new ArrayList();
         session.setGlobal( "list",
                            list );
         session.delete( handle );
         final Cheese mycheese2 = new Cheese( "notcheddar",
                                              4 );
         FactHandle handle2 = session.insert( mycheese2 );
         session.fireAllRules();

         assertEquals( "rule 4",
                       list.get( 0 ) );
         assertEquals( 1,
                       list.size() );

         //Test 3 levels of inheritance, all levels
         list = new ArrayList();
         session.setGlobal( "list",
                            list );
         session.delete( handle2 );
         final Cheese mycheese3 = new Cheese( "stilton",
                                              6 );
         FactHandle handle3 = session.insert( mycheese3 );
         session.fireAllRules();
         //System.out.println(list.toString());
         assertEquals( "rule 3",
                       list.get( 0 ) );
         assertEquals( 1,
                       list.size() );

         //Test 3 levels of inheritance, third only
         list = new ArrayList();
         session.setGlobal( "list",
                            list );
         session.delete( handle3 );
         final Cheese mycheese4 = new Cheese( "notstilton",
                                              6 );
         FactHandle handle4 = session.insert( mycheese4 );
         session.fireAllRules();
         //System.out.println(((List) session.getGlobal( "list" )).toString());
         assertTrue( ((List) session.getGlobal( "list" )).size() == 0 );

         //Test 3 levels of inheritance, 2nd only
         list = new ArrayList();
         session.setGlobal( "list",
                            list );
         session.delete( handle4 );
         final Cheese mycheese5 = new Cheese( "stilton",
                                              7 );
         FactHandle handle5 = session.insert( mycheese5 );
         session.fireAllRules();
         //System.out.println(((List) session.getGlobal( "list" )).toString());
         assertEquals( 0,
                       list.size() );

     }


     @Test
     public void testExtends2() {
         KnowledgeBase kbase = loadKnowledgeBase( "test_RuleExtend.drl" );

         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         final Cheese stilton = new Cheese( "stilton",
                                            5 );
         final Cheese cheddar = new Cheese( "cheddar",
                                            7 );
         final Cheese brie = new Cheese( "brie",
                                         5 );

         ksession.insert( stilton );
         ksession.insert( cheddar );
         ksession.insert( brie );

         ksession.fireAllRules();

         assertEquals( 2,
                       results.size() );
         assertEquals( "stilton",
                       results.get( 0 ) );
         assertEquals( "brie",
                       results.get( 1 ) );
     }

     @Test
     public void testLatinLocale() throws Exception {
         Locale defaultLoc = Locale.getDefault();

         try {
             // setting a locale that uses COMMA as decimal separator
             Locale.setDefault( new Locale( "pt",
                                            "BR" ) );

             KnowledgeBase kbase = loadKnowledgeBase("test_LatinLocale.drl");

             StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

             final List<String> results = new ArrayList<String>();
             ksession.setGlobal( "results",
                                 results );

             final Cheese mycheese = new Cheese( "cheddar",
                                                 4 );
             FactHandle handle = ksession.insert( mycheese );
             ksession.fireAllRules();

             assertEquals( 1,
                           results.size() );
             assertEquals( "1",
                           results.get( 0 ) );

             mycheese.setPrice( 8 );
             mycheese.setDoublePrice( 8.50 );

             ksession.update( handle,
                              mycheese );
             ksession.fireAllRules();
             assertEquals( 2,
                           results.size() );
             assertEquals( "3",
                           results.get( 1 ) );
         } finally {
             Locale.setDefault( defaultLoc );
         }
     }

     @Test
     public void testLiteral() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "literal_rule_test.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         session.setGlobal( "list",
                            list );

         final Cheese stilton = new Cheese( "stilton",
                                            5 );
         session.insert( stilton );
         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );

         session.fireAllRules();

         assertEquals( "stilton",
                       ((List) session.getGlobal( "list" )).get( 0 ) );
     }

     @Test
     public void testLiteralWithEscapes() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_literal_with_escapes.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         session.setGlobal( "list",
                            list );

         String expected = "s\tti\"lto\nn";
         final Cheese stilton = new Cheese( expected,
                                            5 );
         session.insert( stilton );
         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );

         int fired = session.fireAllRules();
         assertEquals( 1,
                       fired );

         assertEquals( expected,
                       ((List) session.getGlobal( "list" )).get( 0 ) );
     }

     @Test
     public void testLiteralWithBoolean() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "literal_with_boolean.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         session.setGlobal( "list",
                            list );

         final PersonInterface bill = new Person( "bill",
                                                  null,
                                                  12 );
         bill.setAlive( true );
         session.insert( bill );
         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );

         session.fireAllRules();

         assertEquals( bill,
                       ((List) session.getGlobal( "list" )).get( 0 ) );
     }

     @Test
     public void testFactBindings() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_FactBindings.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         RuleRuntimeEventListener wmel = mock( RuleRuntimeEventListener.class );
         ksession.addEventListener( wmel );

         final Person bigCheese = new Person( "big cheese" );
         final Cheese cheddar = new Cheese( "cheddar",
                                            15 );
         bigCheese.setCheese( cheddar );

         final FactHandle bigCheeseHandle = ksession.insert( bigCheese );
         final FactHandle cheddarHandle = ksession.insert( cheddar );
         ksession.fireAllRules();

         ArgumentCaptor<org.kie.api.event.rule.ObjectUpdatedEvent> arg = ArgumentCaptor.forClass( org.kie.api.event.rule.ObjectUpdatedEvent.class );
         verify( wmel, times( 2 ) ).objectUpdated( arg.capture() );

         org.kie.api.event.rule.ObjectUpdatedEvent event = arg.getAllValues().get( 0 );
         assertSame( cheddarHandle,
                     event.getFactHandle() );
         assertSame( cheddar,
                     event.getOldObject() );
         assertSame( cheddar,
                     event.getObject() );

         event = arg.getAllValues().get( 1 );
         assertSame( bigCheeseHandle,
                     event.getFactHandle() );
         assertSame( bigCheese,
                     event.getOldObject() );
         assertSame( bigCheese,
                     event.getObject() );
     }

     @Test
     public void testPropertyChangeSupportNewAPI() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_PropertyChangeTypeDecl.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         session.setGlobal( "list",
                            list );

         final State state = new State( "initial" );
         session.insert( state );
         session.fireAllRules();

         assertEquals( 1,
                       ((List) session.getGlobal( "list" )).size() );

         state.setFlag( true );
         assertEquals( 1,
                       ((List) session.getGlobal( "list" )).size() );

         session.fireAllRules();
         assertEquals( 2,
                       ((List) session.getGlobal( "list" )).size() );

         state.setState( "finished" );

         session.dispose();

         // checks that the session removed itself from the bean listeners list
         assertEquals( 0,
                       state.getPropertyChangeListeners().length );
     }

     @Test
     public void testDisconnectedFactHandle() {
         KnowledgeBase kbase = getKnowledgeBase();
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         DefaultFactHandle helloHandle = (DefaultFactHandle) ksession.insert( "hello" );
         DefaultFactHandle goodbyeHandle = (DefaultFactHandle) ksession.insert( "goodbye" );

         FactHandle key = new DefaultFactHandle( helloHandle.toExternalForm() );
         assertEquals( "hello",
                       ksession.getObject( key ) );

         key = new DefaultFactHandle( goodbyeHandle.toExternalForm() );
         assertEquals( "goodbye",
                       ksession.getObject( key ) );
     }

     @Test
     public void testBigDecimal() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "big_decimal_and_comparable.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         session.setGlobal( "list",
                            list );

         final PersonInterface bill = new Person( "bill",
                                                  null,
                                                  42 );
         bill.setBigDecimal( new BigDecimal( "42" ) );

         final PersonInterface ben = new Person( "ben",
                                                 null,
                                                 43 );
         ben.setBigDecimal( new BigDecimal( "43" ) );

         session.insert( bill );
         session.insert( new Cheese( "gorgonzola", 43 ) );
         session.insert( ben );
         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );
         session.fireAllRules();

         assertEquals( 1,
                       ((List) session.getGlobal( "list" )).size() );
     }

     @Test
     public void testBigDecimalIntegerLiteral() throws Exception {

         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "big_decimal_and_literal.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         session.setGlobal( "list",
                            list );

         final PersonInterface bill = new Person( "bill",
                                                  null,
                                                  12 );
         bill.setBigDecimal( new BigDecimal( "42" ) );
         bill.setBigInteger( new BigInteger( "42" ) );

         session.insert( bill );
         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );
         session.fireAllRules();

         assertEquals( 6,
                       ((List) session.getGlobal( "list" )).size() );
     }

     @Test
     public void testBigDecimalWithFromAndEval() throws Exception {
         String rule = "package org.drools.compiler.test;\n";
         rule += "rule \"Test Rule\"\n";
         rule += "when\n";
         rule += "    $dec : java.math.BigDecimal() from java.math.BigDecimal.TEN;\n";
         rule += "    eval( $dec.compareTo(java.math.BigDecimal.ONE) > 0 )\n";
         rule += "then\n";
         rule += "    System.out.println(\"OK!\");\n";
         rule += "end";

         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( rule ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         session.fireAllRules();
     }

     @Test()
     public void testImport() throws Exception {
         // Same package as this test
         String rule = "";
         rule += "package org.drools.compiler.integrationtests;\n";
         rule += "import java.lang.Math;\n";
         rule += "rule \"Test Rule\"\n";
         rule += "  dialect \"mvel\"\n";
         rule += "  when\n";
         rule += "  then\n";
         // Can't handle the TestFact.TEST
         rule += "    new TestFact(TestFact.TEST);\n";
         rule += "end";

         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( rule ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         ksession.fireAllRules();
     }

     @Test
     public void testMVELConsequenceWithMapsAndArrays() throws Exception {
         String rule = "package org.drools.compiler.test;\n";
         rule += "import java.util.ArrayList\n";
         rule += "import java.util.HashMap\n";
         rule += "global java.util.List list\n";
         rule += "rule \"Test Rule\"\n";
         rule += "    dialect \"mvel\"";
         rule += "when\n";
         rule += "then\n";
         rule += "    m = new HashMap();\n";
         rule += "    l = new ArrayList();\n";
         rule += "    l.add(\"first\");\n";
         rule += "    m.put(\"content\", l);\n";
         rule += "    System.out.println(((ArrayList)m[\"content\"])[0]);\n";
         rule += "    list.add(((ArrayList)m[\"content\"])[0]);\n";
         rule += "end";

         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( rule ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         List list = new ArrayList();
         session.setGlobal( "list",
                            list );
         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );
         session.fireAllRules();

         assertEquals( 1,
                       ((List) session.getGlobal( "list" )).size() );
         assertEquals( "first",
                       ((List) session.getGlobal( "list" )).get( 0 ) );
     }

     @Test
     public void testCell() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "evalmodify.drl" ) );

         Environment env = EnvironmentFactory.newEnvironment();
         env.set( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES,
                  new ObjectMarshallingStrategy[]{
                  new IdentityPlaceholderResolverStrategy( ClassObjectMarshallingStrategyAcceptor.DEFAULT )} );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase, null, env );

         final Cell cell1 = new Cell( 9 );
         final Cell cell = new Cell( 0 );

         session.insert( cell1 );
         FactHandle cellHandle = session.insert( cell );

         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );

         session.fireAllRules();
         assertEquals( 9,
                       cell.getValue() );
     }

     @Test
     public void testNesting() throws Exception {
         Person p = new Person();
         p.setName( "Michael" );

         Address add1 = new Address();
         add1.setStreet( "High" );

         Address add2 = new Address();
         add2.setStreet( "Low" );

         List l = new ArrayList();
         l.add( add1 );
         l.add( add2 );

         p.setAddresses( l );

         DrlParser parser = new DrlParser( LanguageLevelOption.DRL5 );
         PackageDescr desc = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "nested_fields.drl" ) ) );
         List packageAttrs = desc.getAttributes();
         assertEquals( 1,
                       desc.getRules().size() );
         assertEquals( 1,
                       packageAttrs.size() );

         RuleDescr rule = (RuleDescr) desc.getRules().get( 0 );
         Map<String, AttributeDescr> ruleAttrs = rule.getAttributes();
         assertEquals( 1,
                       ruleAttrs.size() );

         assertEquals( "mvel",
                       ((AttributeDescr) ruleAttrs.get( "dialect" )).getValue() );
         assertEquals( "dialect",
                       ((AttributeDescr) ruleAttrs.get( "dialect" )).getName() );

         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( desc ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         session.insert( p );
         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );

         session.fireAllRules();
     }

     @Test
     public void testOr() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "or_test.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         session.setGlobal( "list",
                            list );

         final Cheese cheddar = new Cheese( "cheddar",
                                            5 );
         final FactHandle h = session.insert( cheddar );

         session.fireAllRules();

         // just one added
         assertEquals( "got cheese",
                       list.get( 0 ) );
         assertEquals( 1,
                       list.size() );

         session.delete( h );
         session.fireAllRules();

         // still just one
         assertEquals( 1,
                       list.size() );

         session.insert( new Cheese( "stilton",
                                     5 ) );
         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );
         session.fireAllRules();

         // now have one more
         assertEquals( 2,
                       ((List) session.getGlobal( "list" )).size() );
     }

     @Test
     public void testEval() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "eval_rule_test.drl" );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.setGlobal( "five",
                             new Integer( 5 ) );

         final List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         final Cheese stilton = new Cheese( "stilton",
                                            5 );
         ksession.insert( stilton );
         ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                               true );
         ksession.fireAllRules();

         assertEquals( stilton,
                       ((List) ksession.getGlobal( "list" )).get( 0 ) );
     }

     @Test
     public void testJaninoEval() throws Exception {
         KnowledgeBuilderConfiguration kbconf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
         kbconf.setProperty( JavaDialectConfiguration.JAVA_COMPILER_PROPERTY, "JANINO" );
         KnowledgeBase kbase = loadKnowledgeBase( kbconf, "eval_rule_test.drl" );

         kbase = SerializationHelper.serializeObject( kbase );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.setGlobal( "five",
                             new Integer( 5 ) );

         final List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         final Cheese stilton = new Cheese( "stilton",
                                            5 );
         ksession.insert( stilton );
         ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                               true );
         ksession.fireAllRules();

         assertEquals( stilton,
                       ((List) ksession.getGlobal( "list" )).get( 0 ) );
     }

     @Test
     public void testEvalMore() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "eval_rule_test_more.drl" );
         StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

         final List list = new ArrayList();
         session.setGlobal( "list",
                            list );

         final Person foo = new Person( "foo" );
         session.insert( foo );
         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );
         session.fireAllRules();

         assertEquals( foo,
                       ((List) session.getGlobal( "list" )).get( 0 ) );
     }

     @Test
     public void testReturnValue() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "returnvalue_rule_test.drl" );
         kbase = SerializationHelper.serializeObject( kbase );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.setGlobal( "two",
                             new Integer( 2 ) );

         final List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         final PersonInterface peter = new Person( "peter",
                                                   null,
                                                   12 );
         ksession.insert( peter );
         final PersonInterface jane = new Person( "jane",
                                                  null,
                                                  10 );
         ksession.insert( jane );

         ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession, true );
         ksession.fireAllRules();

         assertEquals( jane,
                       ((List) ksession.getGlobal( "list" )).get( 0 ) );
         assertEquals( peter,
                       ((List) ksession.getGlobal( "list" )).get( 1 ) );
     }

     @Test
     public void testPredicate() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "predicate_rule_test.drl" );

         kbase = SerializationHelper.serializeObject( kbase );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.setGlobal( "two",
                             new Integer( 2 ) );

         final List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         final PersonInterface peter = new Person( "peter",
                                                   null,
                                                   12 );
         ksession.insert( peter );
         final PersonInterface jane = new Person( "jane",
                                                  null,
                                                  10 );
         ksession.insert( jane );

         ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                               true );
         ksession.fireAllRules();

         assertEquals( jane,
                       ((List) ksession.getGlobal( "list" )).get( 0 ) );
         assertEquals( peter,
                       ((List) ksession.getGlobal( "list" )).get( 1 ) );
     }

     @Test
     public void testNullBehaviour() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "null_behaviour.drl" );
         StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

         final PersonInterface p1 = new Person( "michael",
                                                "food",
                                                40 );
         final PersonInterface p2 = new Person( null,
                                                "drink",
                                                30 );
         session.insert( p1 );
         session.insert( p2 );

         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );
         session.fireAllRules();
     }

     @Test
     public void testNullConstraint() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "null_constraint.drl" );
         StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

         final List foo = new ArrayList();
         session.setGlobal( "messages",
                            foo );

         final PersonInterface p1 = new Person( null,
                                                "food",
                                                40 );
         final Primitives p2 = new Primitives();
         p2.setArrayAttribute( null );

         session.insert( p1 );
         session.insert( p2 );

         session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session,
                                                                              true );
         session.fireAllRules();
         assertEquals( 2,
                       ((List) session.getGlobal( "messages" )).size() );

     }

     @Test
     public void testBasicFrom() throws Exception {
         if( CommonTestMethodBase.phreak == RuleEngineOption.RETEOO ) {
             return;  //Disbaled due to phreak, as tests is order specific
         }

         KnowledgeBase kbase = loadKnowledgeBase("test_From.drl"  );
         kbase = SerializationHelper.serializeObject( kbase );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         final List list1 = new ArrayList();
         ksession.setGlobal( "list1",
                             list1 );
         final List list2 = new ArrayList();
         ksession.setGlobal( "list2",
                             list2 );
         final List list3 = new ArrayList();
         ksession.setGlobal( "list3",
                             list3 );

         final Cheesery cheesery = new Cheesery();
         final Cheese stilton = new Cheese( "stilton",
                                            12 );
         final Cheese cheddar = new Cheese( "cheddar",
                                            15 );
         cheesery.addCheese( stilton );
         cheesery.addCheese( cheddar );
         ksession.setGlobal( "cheesery",
                             cheesery );
         ksession.insert( cheesery );

         Person p = new Person( "stilton" );
         ksession.insert( p );

         ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession, true );
         ksession.fireAllRules();
         ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession, true );
         ksession.fireAllRules();

         // from using a global
         assertEquals( 2,
                       ((List) ksession.getGlobal( "list1" )).size() );
         assertEquals( cheddar,
                       ((List) ksession.getGlobal( "list1" )).get( 0 ) );
         assertEquals( stilton,
                       ((List) ksession.getGlobal( "list1" )).get( 1 ) );

         // from using a declaration
         assertEquals( 2,
                       ((List) ksession.getGlobal( "list2" )).size() );
         assertEquals( cheddar,
                       ((List) ksession.getGlobal( "list2" )).get( 0 ) );
         assertEquals( stilton,
                       ((List) ksession.getGlobal( "list2" )).get( 1 ) );

         // from using a declaration
         assertEquals( 1,
                       ((List) ksession.getGlobal( "list3" )).size() );
         assertEquals( stilton,
                       ((List) ksession.getGlobal( "list3" )).get( 0 ) );
     }

     @Test
     public void testFromWithParams() throws Exception {
         if( CommonTestMethodBase.phreak == RuleEngineOption.RETEOO ) {
             return;  //Disbaled due to phreak, as tests is order specific
         }

         KnowledgeBase kbase = loadKnowledgeBase( "test_FromWithParams.drl" );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         List list = new ArrayList();
         final Object globalObject = new Object();
         ksession.setGlobal( "list",
                             list );
         ksession.setGlobal( "testObject",
                             new FromTestClass() );
         ksession.setGlobal( "globalObject",
                             globalObject );

         final Person bob = new Person( "bob" );
         ksession.insert( bob );

         // TODO java.io.NotSerializableException: org.mvel.util.FastList
         //        ksession    = SerializationHelper.serializeObject(ksession);
         ksession.fireAllRules();

         assertEquals( 6,
                       ((List) ksession.getGlobal( "list" )).size() );


         final List array = (List) ((List) ksession.getGlobal( "list" )).get( 0 );
         assertEquals( 3,
                       array.size() );
         final Person p = (Person) array.get( 0 );
         assertEquals( p,
                       bob );

         assertEquals( new Integer( 42 ),
                       array.get( 1 ) );

         final List nested = (List) array.get( 2 );
         assertEquals( "x",
                       nested.get( 0 ) );
         assertEquals( "y",
                       nested.get( 1 ) );

         final Map map = (Map) ((List) ksession.getGlobal( "list" )).get( 1 );
         assertEquals( 2,
                       map.keySet().size() );

         assertTrue( map.keySet().contains( bob ) );
         assertEquals( globalObject,
                       map.get( bob ) );

         assertTrue( map.keySet().contains( "key1" ) );
         final Map nestedMap = (Map) map.get( "key1" );
         assertEquals( 1,
                       nestedMap.keySet().size() );
         assertTrue( nestedMap.keySet().contains( "key2" ) );
         assertEquals( "value2",
                       nestedMap.get( "key2" ) );

         assertEquals( new Integer( 42 ),
                       ((List) ksession.getGlobal( "list" )).get( 2 ) );
         assertEquals( "literal",
                       ((List) ksession.getGlobal( "list" )).get( 3 ) );
         assertEquals( bob,
                       ((List) ksession.getGlobal( "list" )).get( 4 ) );
         assertEquals( globalObject,
                       ((List) ksession.getGlobal( "list" )).get( 5 ) );
     }

     @Test
     public void testFromWithNewConstructor() throws Exception {
         DrlParser parser = new DrlParser( LanguageLevelOption.DRL5 );
         PackageDescr descr = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "test_FromWithNewConstructor.drl" ) ) );

         Collection<KnowledgePackage> pkgs = loadKnowledgePackages( descr );
         pkgs = SerializationHelper.serializeObject( pkgs );
     }

     /**
      * JBRULES-1415 Certain uses of from causes NullPointerException in WorkingMemoryLogger
      */
     @Test
     public void testFromDeclarationWithWorkingMemoryLogger() throws Exception {
         String rule = "package org.drools.compiler.test;\n";
         rule += "import org.drools.compiler.Cheesery\n";
         rule += "import org.drools.compiler.Cheese\n";
         rule += "global java.util.List list\n";
         rule += "rule \"Test Rule\"\n";
         rule += "when\n";
         rule += "    $cheesery : Cheesery()\n";
         rule += "    Cheese( $type : type) from $cheesery.cheeses\n";
         rule += "then\n";
         rule += "    list.add( $type );\n";
         rule += "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
         StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

         List list = new ArrayList();
         session.setGlobal( "list",
                            list );

         Cheesery cheesery = new Cheesery();
         cheesery.addCheese( new Cheese( "stilton",
                                         22 ) );

         session.insert( cheesery );

         // TODO java.io.EOFException
         //        session = SerializationHelper.serializeObject(session);
         session.fireAllRules();

         assertEquals( 1,
                       ((List) session.getGlobal( "list" )).size() );
         assertEquals( "stilton",
                       ((List) session.getGlobal( "list" )).get( 0 ) );
     }

     @Test
     public void testWithInvalidRule() throws Exception {
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newClassPathResource( "invalid_rule.drl",
                                                             getClass() ),
                       ResourceType.DRL );

         assertTrue( kbuilder.hasErrors() );

         final String pretty = kbuilder.getErrors().toString();
         assertFalse( pretty.equals( "" ) );
     }

     @Test
     public void testWithInvalidRule2() throws Exception {
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newClassPathResource( "invalid_rule2.drl",
                                                             getClass() ),
                       ResourceType.DRL );

         assertTrue( kbuilder.hasErrors() );
     }

     @Test
     public void testErrorLineNumbers() throws Exception {
         // this test aims to test semantic errors
         // parser errors are another test case
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newClassPathResource( "errors_in_rule.drl", getClass() ),
                       ResourceType.DRL );

         KnowledgeBuilderError[] errors = kbuilder.getErrors().toArray( new KnowledgeBuilderError[0] );
         assertEquals( 3,
                       errors.length );

         final DescrBuildError stiltonError = (DescrBuildError) errors[0];
         assertTrue( stiltonError.getMessage().contains( "Stilton" ) );
         assertNotNull( stiltonError.getDescr() );
         assertTrue( stiltonError.getLine() != -1 );

         // check that its getting it from the ruleDescr
         assertEquals( stiltonError.getLine(),
                       stiltonError.getDescr().getLine() );
         // check the absolute error line number (there are more).
         assertEquals( 11,
                       stiltonError.getLine() );

         final DescrBuildError poisonError = (DescrBuildError) errors[1];
         assertTrue( poisonError.getMessage().contains( "Poison" ) );
         assertEquals( 13,
                       poisonError.getLine() );

         KnowledgeBuilderConfigurationImpl cfg = new KnowledgeBuilderConfigurationImpl();
         JavaDialectConfiguration javaConf = (JavaDialectConfiguration) cfg.getDialectConfiguration( "java" );
         switch ( javaConf.getCompiler() ) {
             case JavaDialectConfiguration.NATIVE : assertTrue( errors[2].getMessage().contains( "illegal" ) );
                 break;
             case JavaDialectConfiguration.ECLIPSE: assertTrue( errors[2].getMessage().contains( "add" ) );
                 break;
             case JavaDialectConfiguration.JANINO: assertTrue( errors[2].getMessage().contains( "Unexpected" ) );
                 break;
             default: fail( "Unknown compiler used" );
         }

         // now check the RHS, not being too specific yet, as long as it has the
         // rules line number, not zero
         final DescrBuildError rhsError = (DescrBuildError) errors[2];
         assertTrue( rhsError.getLine() >= 8 && rhsError.getLine() <= 17 ); // TODO this should be 16
     }

     @Test
     public void testErrorsParser() throws Exception {
         final DrlParser parser = new DrlParser( LanguageLevelOption.DRL5 );
         assertEquals( 0,
                       parser.getErrors().size() );
         parser.parse( new InputStreamReader( getClass().getResourceAsStream( "errors_parser_multiple.drl" ) ) );
         assertTrue( parser.hasErrors() );
         assertTrue( parser.getErrors().size() > 0 );
         assertTrue( parser.getErrors().get( 0 ) instanceof ParserError);
         final ParserError first = ((ParserError) parser.getErrors().get( 0 ));
         assertTrue( first.getMessage() != null );
         assertFalse( first.getMessage().equals( "" ) );
     }

     @Test
     public void testAssertRetract() throws Exception {
         // postponed while I sort out KnowledgeHelperFixer
         KnowledgeBase kbase = loadKnowledgeBase( "assert_retract.drl" );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         final List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         final PersonInterface person = new Person( "michael",
                                                    "cheese" );
         person.setStatus( "start" );
         ksession.insert( person );

         ksession.fireAllRules();

         List<String> results = (List<String>) ksession.getGlobal( "list" );
         for ( String result : results ) {
             logger.info( result );
         }
         assertEquals( 5,
                       results.size() );
         assertTrue( results.contains( "first" ) );
         assertTrue( results.contains( "second" ) );
         assertTrue( results.contains( "third" ) );
         assertTrue( results.contains( "fourth" ) );
         assertTrue( results.contains( "fifth" ) );

     }

     @Test
     public void testPredicateAsFirstPattern() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "predicate_as_first_pattern.drl" );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         final Cheese mussarela = new Cheese( "Mussarela",
                                              35 );
         ksession.insert( mussarela );
         final Cheese provolone = new Cheese( "Provolone",
                                              20 );
         ksession.insert( provolone );

         ksession.fireAllRules();

         assertEquals( "The rule is being incorrectly fired",
                       35,
                       mussarela.getPrice() );
         assertEquals( "Rule is incorrectly being fired",
                       20,
                       provolone.getPrice() );
     }

     @Test
     public void testConsequenceException() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "test_ConsequenceException.drl" );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         final Cheese brie = new Cheese( "brie",
                                         12 );
         ksession.insert( brie );

         try {
             ksession.fireAllRules();
             fail( "Should throw an Exception from the Consequence" );
         } catch ( final org.kie.api.runtime.rule.ConsequenceException e ) {
             assertEquals( "Throw Consequence Exception",
                           e.getMatch().getRule().getName() );
             assertEquals( "this should throw an exception",
                           e.getCause().getMessage() );
         }
     }

     @Test
     public void testEvalException() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_EvalException.drl" ) );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         final Cheese brie = new Cheese( "brie",
                                         12 );
         try {
             ksession.insert( brie );
             ksession.fireAllRules();
             fail( "Should throw an Exception from the Eval" );
         } catch ( final Exception e ) {
             assertEquals( "this should throw an exception",
                           e.getCause().getMessage() );
         }
     }

     @Test
     public void testPredicateException() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_PredicateException.drl" ) );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         final Cheese brie = new Cheese( "brie",
                                         12 );
         try {
             ksession.insert( brie );
             ksession.fireAllRules();
             fail( "Should throw an Exception from the Predicate" );
         } catch ( final Exception e ) {
             Throwable cause = e.getCause();
             if ( cause instanceof InvocationTargetException ) {
                 cause = ((InvocationTargetException) cause).getTargetException();
             }
             assertTrue( cause.getMessage().contains( "this should throw an exception" ) );
         }
     }

     @Test
     public void testReturnValueException() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ReturnValueException.drl" ) );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         final Cheese brie = new Cheese( "brie",
                                         12 );
         try {
             ksession.insert( brie );
             ksession.fireAllRules();
             fail( "Should throw an Exception from the ReturnValue" );
         } catch ( final Exception e ) {
             Throwable root = e;
             while ( root.getCause() != null )
                 root = root.getCause();
             root.getMessage().contains( "this should throw an exception" );
         }
     }

     @Test
     public void testMultiRestrictionFieldConstraint() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_MultiRestrictionFieldConstraint.drl" ) );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         final List list1 = new ArrayList();
         ksession.setGlobal( "list1",
                             list1 );
         final List list2 = new ArrayList();
         ksession.setGlobal( "list2",
                             list2 );
         final List list3 = new ArrayList();
         ksession.setGlobal( "list3",
                             list3 );
         final List list4 = new ArrayList();
         ksession.setGlobal( "list4",
                             list4 );

         final Person youngChili1 = new Person( "young chili1" );
         youngChili1.setAge( 12 );
         youngChili1.setHair( "blue" );
         final Person youngChili2 = new Person( "young chili2" );
         youngChili2.setAge( 25 );
         youngChili2.setHair( "purple" );

         final Person chili1 = new Person( "chili1" );
         chili1.setAge( 35 );
         chili1.setHair( "red" );

         final Person chili2 = new Person( "chili2" );
         chili2.setAge( 38 );
         chili2.setHair( "indigigo" );

         final Person oldChili1 = new Person( "old chili1" );
         oldChili1.setAge( 45 );
         oldChili1.setHair( "green" );

         final Person oldChili2 = new Person( "old chili2" );
         oldChili2.setAge( 48 );
         oldChili2.setHair( "blue" );

         ksession.insert( youngChili1 );
         ksession.insert( youngChili2 );
         ksession.insert( chili1 );
         ksession.insert( chili2 );
         ksession.insert( oldChili1 );
         ksession.insert( oldChili2 );

         ksession.fireAllRules();

         assertEquals( 1,
                       list1.size() );
         assertTrue( list1.contains( chili1 ) );

         assertEquals( 2,
                       list2.size() );
         assertTrue( list2.contains( chili1 ) );
         assertTrue( list2.contains( chili2 ) );

         assertEquals( 2,
                       list3.size() );
         assertTrue( list3.contains( youngChili1 ) );
         assertTrue( list3.contains( youngChili2 ) );

         assertEquals( 2,
                       list4.size() );
         assertTrue( list4.contains( youngChili1 ) );
         assertTrue( list4.contains( chili1 ) );
     }

     @Test
     public void testDumpers() throws Exception {
         final DrlParser parser = new DrlParser( LanguageLevelOption.DRL5 );
         final PackageDescr pkg = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "test_Dumpers.drl" ) ) );

         if ( parser.hasErrors() ) {
             for ( DroolsError error : parser.getErrors() ) {
                 logger.warn( error.toString() );
             }
             fail( parser.getErrors().toString() );
         }

         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( pkg ) );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         final Cheese brie = new Cheese( "brie",
                                         12 );
         ksession.insert( brie );

         ksession.fireAllRules();

         assertEquals( 3,
                       list.size() );
         assertEquals( "3 1",
                       list.get( 0 ) );
         assertEquals( "MAIN",
                       list.get( 1 ) );
         assertEquals( "1 1",
                       list.get( 2 ) );

         final DrlDumper drlDumper = new DrlDumper();
         final String drlResult = drlDumper.dump( pkg );

         System.out.println( drlResult );

         kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( drlResult ) );
         ksession = kbase.newStatefulKnowledgeSession();

         list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         ksession.insert( brie );

         ksession.fireAllRules();

         assertEquals( 3,
                       list.size() );
         assertEquals( "3 1",
                       list.get( 0 ) );
         assertEquals( "MAIN",
                       list.get( 1 ) );
         assertEquals( "1 1",
                       list.get( 2 ) );
     }

     @Test
     public void testContainsCheese() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ContainsCheese.drl" ) );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         final List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         final Cheese stilton = new Cheese( "stilton",
                                            12 );
         ksession.insert( stilton );
         final Cheese brie = new Cheese( "brie",
                                         10 );
         ksession.insert( brie );

         final Cheesery cheesery = new Cheesery();
         cheesery.getCheeses().add( stilton );
         ksession.insert( cheesery );

         ksession.fireAllRules();

         assertEquals( 2,
                       list.size() );

         assertEquals( stilton,
                       list.get( 0 ) );
         assertEquals( brie,
                       list.get( 1 ) );
     }

     @Test
     public void testDuplicateRuleNames() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_DuplicateRuleName1.drl" ) );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         Collection<KnowledgePackage> kpkgs = loadKnowledgePackages( "test_DuplicateRuleName2.drl" );
         kbase.addKnowledgePackages( kpkgs );
         // @todo: this is from JBRULES-394 - maybe we should test more stuff
         // here?
     }

     @Test
     public void testNullValuesIndexing() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_NullValuesIndexing.drl" ) );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         // Adding person with null name and likes attributes
         final PersonInterface bob = new Person( null,
                                                 null );
         bob.setStatus( "P1" );
         final PersonInterface pete = new Person( null,
                                                  null );
         bob.setStatus( "P2" );
         ksession.insert( bob );
         ksession.insert( pete );

         ksession.fireAllRules();

         assertEquals( "Indexing with null values is not working correctly.",
                       "OK",
                       bob.getStatus() );
         assertEquals( "Indexing with null values is not working correctly.",
                       "OK",
                       pete.getStatus() );
     }

     @Test
     public void testEmptyRule() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_EmptyRule.drl" ) );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         final List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         ksession.fireAllRules();

         assertTrue( list.contains( "fired1" ) );
         assertTrue( list.contains( "fired2" ) );
     }

     @Test
     public void testjustEval() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_NoPatterns.drl" ) );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         final List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         ksession.fireAllRules();

         assertTrue( list.contains( "fired1" ) );
         assertTrue( list.contains( "fired3" ) );
     }

     @Test
     public void testOrWithBinding() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_OrWithBindings.drl" ) );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         final Person hola = new Person( "hola" );
         ksession.insert( hola );

         ksession.fireAllRules();

         assertEquals( 0,
                       list.size() );
         Cheese brie = new Cheese( "brie" );
         ksession.insert( brie );

         ksession.fireAllRules();

         assertEquals( 2,
                       list.size() );
         assertTrue( list.contains( hola ) );
         assertTrue( list.contains( brie ) );

     }

     @Test
     public void testJoinNodeModifyObject() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_JoinNodeModifyObject.drl" ) );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         try {
             final List orderedFacts = new ArrayList();
             final List errors = new ArrayList();
             ksession.setGlobal( "orderedNumbers",
                                 orderedFacts );
             ksession.setGlobal( "errors",
                                 errors );
             final int MAX = 2;
             for ( int i = 1; i <= MAX; i++ ) {
                 final IndexedNumber n = new IndexedNumber( i,
                                                            MAX - i + 1 );
                 ksession.insert( n );
             }
             ksession.fireAllRules();
             assertTrue( "Processing generated errors: " + errors.toString(),
                         errors.isEmpty() );
             for ( int i = 1; i <= MAX; i++ ) {
                 final IndexedNumber n = (IndexedNumber) orderedFacts.get( i - 1 );
                 assertEquals( "Fact is out of order",
                               i,
                               n.getIndex() );
             }
         } finally {
         }
     }

     @Test
     public void testInsurancePricingExample() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "insurance_pricing_example.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         // now create some test data
         final Driver driver = new Driver();
         final Policy policy = new Policy();

         ksession.insert( driver );
         ksession.insert( policy );

         ksession.fireAllRules();

         assertEquals( 120,
                       policy.getBasePrice() );
     }

     @Test
     public void testLLR() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_JoinNodeModifyTuple.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                               true );

         // 1st time
         Target tgt = new Target();
         tgt.setLabel( "Santa-Anna" );
         tgt.setLat( new Float( 60.26544f ) );
         tgt.setLon( new Float( 28.952137f ) );
         tgt.setCourse( new Float( 145.0f ) );
         tgt.setSpeed( new Float( 12.0f ) );
         tgt.setTime( new Float( 1.8666667f ) );
         ksession.insert( tgt );

         tgt = new Target();
         tgt.setLabel( "Santa-Maria" );
         tgt.setLat( new Float( 60.236874f ) );
         tgt.setLon( new Float( 28.992579f ) );
         tgt.setCourse( new Float( 325.0f ) );
         tgt.setSpeed( new Float( 8.0f ) );
         tgt.setTime( new Float( 1.8666667f ) );
         ksession.insert( tgt );

         ksession.fireAllRules();

         // 2nd time
         tgt = new Target();
         tgt.setLabel( "Santa-Anna" );
         tgt.setLat( new Float( 60.265343f ) );
         tgt.setLon( new Float( 28.952267f ) );
         tgt.setCourse( new Float( 145.0f ) );
         tgt.setSpeed( new Float( 12.0f ) );
         tgt.setTime( new Float( 1.9f ) );
         ksession.insert( tgt );

         tgt = new Target();
         tgt.setLabel( "Santa-Maria" );
         tgt.setLat( new Float( 60.236935f ) );
         tgt.setLon( new Float( 28.992493f ) );
         tgt.setCourse( new Float( 325.0f ) );
         tgt.setSpeed( new Float( 8.0f ) );
         tgt.setTime( new Float( 1.9f ) );
         ksession.insert( tgt );

         ksession.fireAllRules();

         // 3d time
         tgt = new Target();
         tgt.setLabel( "Santa-Anna" );
         tgt.setLat( new Float( 60.26525f ) );
         tgt.setLon( new Float( 28.952396f ) );
         tgt.setCourse( new Float( 145.0f ) );
         tgt.setSpeed( new Float( 12.0f ) );
         tgt.setTime( new Float( 1.9333333f ) );
         ksession.insert( tgt );

         tgt = new Target();
         tgt.setLabel( "Santa-Maria" );
         tgt.setLat( new Float( 60.236996f ) );
         tgt.setLon( new Float( 28.992405f ) );
         tgt.setCourse( new Float( 325.0f ) );
         tgt.setSpeed( new Float( 8.0f ) );
         tgt.setTime( new Float( 1.9333333f ) );
         ksession.insert( tgt );

         ksession.fireAllRules();

         // 4th time
         tgt = new Target();
         tgt.setLabel( "Santa-Anna" );
         tgt.setLat( new Float( 60.265163f ) );
         tgt.setLon( new Float( 28.952526f ) );
         tgt.setCourse( new Float( 145.0f ) );
         tgt.setSpeed( new Float( 12.0f ) );
         tgt.setTime( new Float( 1.9666667f ) );
         ksession.insert( tgt );

         tgt = new Target();
         tgt.setLabel( "Santa-Maria" );
         tgt.setLat( new Float( 60.237057f ) );
         tgt.setLon( new Float( 28.99232f ) );
         tgt.setCourse( new Float( 325.0f ) );
         tgt.setSpeed( new Float( 8.0f ) );
         tgt.setTime( new Float( 1.9666667f ) );
         ksession.insert( tgt );

         ksession.fireAllRules();
     }

     @Test
     public void testReturnValueAndGlobal() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ReturnValueAndGlobal.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List matchlist = new ArrayList();
         ksession.setGlobal( "matchingList",
                             matchlist );

         final List nonmatchlist = new ArrayList();
         ksession.setGlobal( "nonMatchingList",
                             nonmatchlist );

         ksession.setGlobal( "cheeseType",
                             "stilton" );

         final Cheese stilton1 = new Cheese( "stilton",
                                             5 );
         final Cheese stilton2 = new Cheese( "stilton",
                                             7 );
         final Cheese brie = new Cheese( "brie",
                                         4 );
         ksession.insert( stilton1 );
         ksession.insert( stilton2 );
         ksession.insert( brie );

         ksession.fireAllRules();

         assertEquals( 2,
                       matchlist.size() );
         assertEquals( 1,
                       nonmatchlist.size() );
     }

     @Test
     public void testDeclaringAndUsingBindsInSamePattern() throws Exception {
         KieBaseConfiguration kbc = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
         kbc.setOption( RemoveIdentitiesOption.YES );
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( kbc, "test_DeclaringAndUsingBindsInSamePattern.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List sensors = new ArrayList();

         ksession.setGlobal( "sensors",
                             sensors );

         final Sensor sensor1 = new Sensor( 100,
                                            150 );
         ksession.insert( sensor1 );
         ksession.fireAllRules();
         assertEquals( 0,
                       sensors.size() );

         final Sensor sensor2 = new Sensor( 200,
                                            150 );
         ksession.insert( sensor2 );
         ksession.fireAllRules();
         assertEquals( 3,
                       sensors.size() );
     }

     @Test
     public void testMissingImports() {
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newClassPathResource( "test_missing_import.drl",
                                                             getClass() ),
                       ResourceType.DRL );
         assertTrue( kbuilder.hasErrors() );
     }

     @Test
     public void testNestedConditionalElements() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_NestedConditionalElements.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         final State state = new State( "SP" );
         ksession.insert( state );

         final Person bob = new Person( "Bob" );
         bob.setStatus( state.getState() );
         bob.setLikes( "stilton" );
         ksession.insert( bob );

         ksession.fireAllRules();

         assertEquals( 0,
                       list.size() );

         ksession.insert( new Cheese( bob.getLikes(),
                                      10 ) );
         ksession.fireAllRules();

         assertEquals( 1,
                       list.size() );
     }

     @Test
     public void testDeclarationUsage() throws Exception {
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newClassPathResource( "test_DeclarationUsage.drl",
                                                             getClass() ),
                       ResourceType.DRL );
         assertTrue( kbuilder.hasErrors() );
     }

     @Test
     public void testDeclareAndFrom() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "test_DeclareWithFrom.drl" );
         FactType profileType = kbase.getFactType( "org.drools.compiler",
                                                   "Profile" );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         Object profile = profileType.newInstance();
         Map<String, Integer> map = new HashMap<String, Integer>();
         map.put( "internet",
                  Integer.valueOf( 2 ) );
         profileType.set( profile,
                          "pageFreq",
                          map );

         ksession.insert( profile );
         ksession.fireAllRules();
         ksession.dispose();
     }

     @Test
     public void testDeclarationNonExistingField() throws Exception {
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newClassPathResource( "test_DeclarationOfNonExistingField.drl",
                                                             getClass() ),
                       ResourceType.DRL );
         assertTrue( kbuilder.hasErrors() );
     }

     @Test
     public void testUnbalancedTrees() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_UnbalancedTrees.drl" ) );
         StatefulKnowledgeSession wm = createKnowledgeSession( kbase );

         wm.insert( new Cheese( "a",
                                10 ) );
         wm.insert( new Cheese( "b",
                                10 ) );
         wm.insert( new Cheese( "c",
                                10 ) );
         wm.insert( new Cheese( "d",
                                10 ) );
         final Cheese e = new Cheese( "e",
                                      10 );
         wm.insert( e );

         wm.fireAllRules();

         assertEquals( "Rule should have fired twice, seting the price to 30",
                       30,
                       e.getPrice() );
         // success
     }

     @Test
     public void testImportConflict() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ImportConflict.drl" ) );
         StatefulKnowledgeSession wm = createKnowledgeSession( kbase );
     }

     @Test
     public void testEmptyIdentifier() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_emptyIdentifier.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List result = new ArrayList();
         ksession.setGlobal( "results",
                             result );

         final Person person = new Person( "bob" );
         final Cheese cheese = new Cheese( "brie",
                                           10 );

         ksession.insert( person );
         ksession.insert( cheese );

         ksession.fireAllRules();
         assertEquals( 4,
                       result.size() );
     }

     @Test
     public void testDuplicateVariableBinding() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_duplicateVariableBinding.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final Map result = new HashMap();
         ksession.setGlobal( "results",
                             result );

         final Cheese stilton = new Cheese( "stilton",
                                            20 );
         final Cheese brie = new Cheese( "brie",
                                         10 );

         ksession.insert( stilton );
         ksession.insert( brie );

         ksession.fireAllRules();
         assertEquals( 5,
                       result.size() );
         assertEquals( stilton.getPrice(),
                       ((Integer) result.get( stilton.getType() )).intValue() );
         assertEquals( brie.getPrice(),
                       ((Integer) result.get( brie.getType() )).intValue() );

         assertEquals( stilton.getPrice(),
                       ((Integer) result.get( stilton )).intValue() );
         assertEquals( brie.getPrice(),
                       ((Integer) result.get( brie )).intValue() );

         assertEquals( stilton.getPrice(),
                       ((Integer) result.get( "test3" + stilton.getType() )).intValue() );

         ksession.insert( new Person( "bob",
                                      brie.getType() ) );
         ksession.fireAllRules();

         assertEquals( 6,
                       result.size() );
         assertEquals( brie.getPrice(),
                       ((Integer) result.get( "test3" + brie.getType() )).intValue() );
     }

     @Test
     public void testShadowProxyInHirarchies() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ShadowProxyInHirarchies.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( new Child( "gp" ) );

         ksession.fireAllRules();
     }

     @Test
     public void testSelfReference() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_SelfReference.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         final Order order = new Order( 10,
                                        "Bob" );
         final OrderItem item1 = new OrderItem( order,
                                                1 );
         final OrderItem item2 = new OrderItem( order,
                                                2 );
         final OrderItem anotherItem1 = new OrderItem( null,
                                                       3 );
         final OrderItem anotherItem2 = new OrderItem( null,
                                                       4 );
         ksession.insert( order );
         ksession.insert( item1 );
         ksession.insert( item2 );
         ksession.insert( anotherItem1 );
         ksession.insert( anotherItem2 );

         ksession.fireAllRules();

         assertEquals( 2,
                       results.size() );
         assertTrue( results.contains( item1 ) );
         assertTrue( results.contains( item2 ) );
     }

     @Test
     public void testNumberComparisons() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_NumberComparisons.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         // asserting the sensor object
         final RandomNumber rn = new RandomNumber();
         rn.setValue( 10 );
         ksession.insert( rn );

         final Guess guess = new Guess();
         guess.setValue( new Integer( 5 ) );

         final FactHandle handle = ksession.insert( guess );

         ksession.fireAllRules();

         // HIGHER
         assertEquals( 1,
                       list.size() );
         assertEquals( "HIGHER",
                       list.get( 0 ) );

         guess.setValue( new Integer( 15 ) );
         ksession.update( handle,
                          guess );

         ksession.fireAllRules();

         // LOWER
         assertEquals( 2,
                       list.size() );
         assertEquals( "LOWER",
                       list.get( 1 ) );

         guess.setValue( new Integer( 10 ) );
         ksession.update( handle,
                          guess );

         ksession.fireAllRules();

         // CORRECT
         assertEquals( 3,
                       list.size() );
         assertEquals( "CORRECT",
                       list.get( 2 ) );

     }

     @Test
     public void testEventModel() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_EventModel.drl" ) );
         StatefulKnowledgeSession wm = createKnowledgeSession( kbase );

         RuleRuntimeEventListener wmel = mock( RuleRuntimeEventListener.class );
         wm.addEventListener( wmel );

         final Cheese stilton = new Cheese( "stilton",
                                            15 );

         final FactHandle stiltonHandle = wm.insert( stilton );

         ArgumentCaptor<org.kie.api.event.rule.ObjectInsertedEvent> oic = ArgumentCaptor.forClass( org.kie.api.event.rule.ObjectInsertedEvent.class );
         verify( wmel ).objectInserted( oic.capture() );
         assertSame( stiltonHandle,
                     oic.getValue().getFactHandle() );

         wm.update( stiltonHandle,
                    stilton );
         ArgumentCaptor<org.kie.api.event.rule.ObjectUpdatedEvent> ouc = ArgumentCaptor.forClass( org.kie.api.event.rule.ObjectUpdatedEvent.class );
         verify( wmel ).objectUpdated( ouc.capture() );
         assertSame( stiltonHandle,
                     ouc.getValue().getFactHandle() );

         wm.delete( stiltonHandle );
         ArgumentCaptor<ObjectDeletedEvent> orc = ArgumentCaptor.forClass( ObjectDeletedEvent.class );
         verify( wmel ).objectDeleted(orc.capture());
         assertSame( stiltonHandle,
                     orc.getValue().getFactHandle() );

     }

     @Test
     public void testImplicitDeclarations() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_implicitDeclarations.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );
         ksession.setGlobal( "factor",
                             new Double( 1.2 ) );

         final Cheese cheese = new Cheese( "stilton",
                                           10 );
         ksession.insert( cheese );

         ksession.fireAllRules();
         assertEquals( 1,
                       results.size() );
     }

     @Test
     public void testMVELImplicitWithFrom() throws IOException,
                                           ClassNotFoundException {
         String str = "" +
                      "package org.drools.compiler.test \n" +
                      "import java.util.List \n" +
                      "global java.util.List list \n" +
                      "global java.util.List list2 \n" +
                      "rule \"show\" dialect \"mvel\" \n" +
                      "when  \n" +
                      "    $m : List( eval( size == 0 ) ) from [list] \n" +
                      "then \n" +
                      "    list2.add('r1'); \n" +
                      "end \n";

         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );
         ksession.setGlobal( "list2",
                             list );

         ksession.fireAllRules();

         assertEquals( "r1",
                       list.get( 0 ) );
     }

     @Test
     public void testJavaImplicitWithFrom() throws IOException,
                                           ClassNotFoundException {
         String str = "" +
                      "package org.drools.compiler.test \n" +
                      "import java.util.List \n" +
                      "global java.util.List list \n" +
                      "global java.util.List list2 \n" +
                      "rule \"show\" dialect \"java\" \n" +
                      "when  \n" +
                      "    $m : List( eval( size == 0 )  ) from [list] \n" +
                      "then \n" +
                      "    list2.add('r1'); \n" +
                      "end \n";
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );
         ksession.setGlobal( "list2",
                             list );

         ksession.fireAllRules();

         assertEquals( "r1",
                       list.get( 0 ) );
     }

     @Test
     public void testCastingInsideEvals() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_castsInsideEval.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.setGlobal( "value",
                             new Integer( 20 ) );

         ksession.fireAllRules();
     }

     @Test
     public void testMemberOfAndNotMemberOf() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_memberOf.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         final Cheese stilton = new Cheese( "stilton",
                                            12 );
         final Cheese muzzarela = new Cheese( "muzzarela",
                                              10 );
         final Cheese brie = new Cheese( "brie",
                                         15 );
         ksession.insert( stilton );
         ksession.insert( muzzarela );

         final Cheesery cheesery = new Cheesery();
         cheesery.getCheeses().add( stilton.getType() );
         cheesery.getCheeses().add( brie.getType() );
         ksession.insert( cheesery );

         ksession.fireAllRules();

         assertEquals( 2,
                       list.size() );

         assertEquals( stilton,
                       list.get( 0 ) );
         assertEquals( muzzarela,
                       list.get( 1 ) );
     }

     @Test
     public void testContainsInArray() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_contains_in_array.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         final Primitives p = new Primitives();
         p.setStringArray( new String[]{"test1", "test3"} );
         ksession.insert( p );

         ksession.fireAllRules();

         assertEquals( 2,
                       list.size() );

         assertEquals( "ok1",
                       list.get( 0 ) );
         assertEquals( "ok2",
                       list.get( 1 ) );
     }

     @Test
     public void testNodeSharingNotExists() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_nodeSharingNotExists.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         ksession.fireAllRules();

         assertEquals( 1,
                       list.size() );

         assertEquals( "rule1",
                       list.get( 0 ) );

         ksession.insert( new Cheese( "stilton",
                                      10 ) );
         ksession.fireAllRules();

         assertEquals( 2,
                       list.size() );

         assertEquals( "rule2",
                       list.get( 1 ) );
     }

     @Test
     public void testNullBinding() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_nullBindings.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         ksession.insert( new Person( "bob" ) );
         ksession.insert( new Person( null ) );

         ksession.fireAllRules();

         assertEquals( 1,
                       list.size() );

         assertEquals( "OK",
                       list.get( 0 ) );
     }

     @Test
     public void testModifyRetractWithFunction() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_RetractModifyWithFunction.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final Cheese stilton = new Cheese( "stilton",
                                            7 );
         final Cheese muzzarella = new Cheese( "muzzarella",
                                               9 );
         final int sum = stilton.getPrice() + muzzarella.getPrice();
         final FactHandle stiltonHandle = ksession.insert( stilton );
         final FactHandle muzzarellaHandle = ksession.insert( muzzarella );

         ksession.fireAllRules();

         assertEquals( sum,
                       stilton.getPrice() );
         assertEquals( 1,
                       ksession.getFactCount() );
         assertNotNull( ksession.getObject( stiltonHandle ) );
         assertNotNull( ksession.getFactHandle( stilton ) );

         assertNull( ksession.getObject( muzzarellaHandle ) );
         assertNull( ksession.getFactHandle( muzzarella ) );

     }

     @Test
     public void testConstraintConnectors() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ConstraintConnectors.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         final Person youngChili1 = new Person( "young chili1" );
         youngChili1.setAge( 12 );
         youngChili1.setHair( "blue" );
         final Person youngChili2 = new Person( "young chili2" );
         youngChili2.setAge( 25 );
         youngChili2.setHair( "purple" );

         final Person chili1 = new Person( "chili1" );
         chili1.setAge( 35 );
         chili1.setHair( "red" );

         final Person chili2 = new Person( "chili2" );
         chili2.setAge( 38 );
         chili2.setHair( "indigigo" );

         final Person oldChili1 = new Person( "old chili1" );
         oldChili1.setAge( 45 );
         oldChili1.setHair( "green" );

         final Person oldChili2 = new Person( "old chili2" );
         oldChili2.setAge( 48 );
         oldChili2.setHair( "blue" );

         final Person veryold = new Person( "very old" );
         veryold.setAge( 99 );
         veryold.setHair( "gray" );

         ksession.insert( youngChili1 );
         ksession.insert( youngChili2 );
         ksession.insert( chili1 );
         ksession.insert( chili2 );
         ksession.insert( oldChili1 );
         ksession.insert( oldChili2 );
         ksession.insert( veryold );

         ksession.fireAllRules();

         assertEquals( 4,
                       results.size() );
         assertEquals( chili1,
                       results.get( 0 ) );
         assertEquals( oldChili1,
                       results.get( 1 ) );
         assertEquals( youngChili1,
                       results.get( 2 ) );
         assertEquals( veryold,
                       results.get( 3 ) );
     }

     @Test
     public void testConnectorsAndOperators() throws Exception {
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ConstraintConnectorsAndOperators.drl" ) );
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( new StockTick( 1,
                                         "RHT",
                                         10,
                                         1000 ) );
         ksession.insert( new StockTick( 2,
                                         "IBM",
                                         10,
                                         1100 ) );
         final int fired = ksession.fireAllRules();

         assertEquals( 1,
                       fired );
     }

     @Test
     public void testConstraintConnectorOr() throws Exception {
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ConstraintConnectorOr.drl" ) );
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List<Person> results = new ArrayList<Person>();
         ksession.setGlobal( "results",
                             results );

         final Person mark = new Person( "Mark" );
         mark.setAlive( true );
         mark.setHappy( true );

         final Person bush = new Person( "Bush" );
         bush.setAlive( true );
         bush.setHappy( false );

         final Person conan = new Person( "Conan" );
         conan.setAlive( false );
         conan.setHappy( true );

         final Person nero = new Person( "Nero" );
         nero.setAlive( false );
         nero.setHappy( false );

         ksession.insert( mark );
         ksession.insert( bush );
         ksession.insert( conan );
         ksession.insert( nero );

         ksession.fireAllRules();

         assertEquals( 3,
                       results.size() );
         assertTrue( results.contains( mark ) );
         assertTrue( results.contains( bush ) );
         assertTrue( results.contains( conan ) );
     }

     @Test
     public void testMatchesNotMatchesCheese() throws Exception {
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_MatchesNotMatches.drl" ) );
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         final Cheese stilton = new Cheese( "stilton",
                                            12 );
         final Cheese stilton2 = new Cheese( "stilton2",
                                             12 );
         final Cheese agedStilton = new Cheese( "aged stilton",
                                                12 );
         final Cheese brie = new Cheese( "brie",
                                         10 );
         final Cheese brie2 = new Cheese( "brie2",
                                          10 );
         final Cheese muzzarella = new Cheese( "muzzarella",
                                               10 );
         final Cheese muzzarella2 = new Cheese( "muzzarella2",
                                                10 );
         final Cheese provolone = new Cheese( "provolone",
                                              10 );
         final Cheese provolone2 = new Cheese( "another cheese (provolone)",
                                               10 );
         ksession.insert( stilton );
         ksession.insert( stilton2 );
         ksession.insert( agedStilton );
         ksession.insert( brie );
         ksession.insert( brie2 );
         ksession.insert( muzzarella );
         ksession.insert( muzzarella2 );
         ksession.insert( provolone );
         ksession.insert( provolone2 );

         ksession.fireAllRules();

         logger.info( list.toString() );
         assertEquals( 4,
                       list.size() );

         assertEquals( stilton,
                       list.get( 0 ) );
         assertEquals( brie,
                       list.get( 1 ) );
         assertEquals( agedStilton,
                       list.get( 2 ) );
         assertEquals( provolone,
                       list.get( 3 ) );
     }

     @Test
     public void testAutomaticBindings() throws Exception {
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_AutoBindings.drl" ) );
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         final Person bob = new Person( "bob",
                                        "stilton" );
         final Cheese stilton = new Cheese( "stilton",
                                            12 );
         ksession.insert( bob );
         ksession.insert( stilton );

         ksession.fireAllRules();
         assertEquals( 1,
                       list.size() );

         assertEquals( bob,
                       list.get( 0 ) );
     }

     @Test
     public void testMatchesMVEL() throws Exception {
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_MatchesMVEL.drl" ) );
         final StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         session.setGlobal( "results",
                            results );

         Map map = new HashMap();
         map.put( "content",
                  "hello ;=" );
         session.insert( map );

         session.fireAllRules();

         assertEquals( 1,
                       results.size() );
     }

     @Test
     public void testMatchesMVEL2() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "test_MatchesMVEL2.drl" );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Map map = new HashMap();
         map.put( "content",
                  "String with . and (routine)" );
         ksession.insert( map );
         int fired = ksession.fireAllRules();

         assertEquals( 2,
                       fired );
     }

     @Test
     public void testMatchesMVEL3() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "test_MatchesMVEL2.drl" );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Map map = new HashMap();
         map.put( "content",
                  "String with . and ()" );
         ksession.insert( map );
         int fired = ksession.fireAllRules();

         assertEquals( 1,
                       fired );
     }

     @Test
     public void testQualifiedFieldReference() throws Exception {
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_QualifiedFieldReference.drl" ) );
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         final Person bob = new Person( "bob",
                                        "stilton" );
         final Cheese stilton = new Cheese( "stilton",
                                            12 );
         ksession.insert( bob );
         ksession.insert( stilton );

         ksession.fireAllRules();

         assertEquals( 1,
                       list.size() );

         assertEquals( bob,
                       list.get( 0 ) );
     }

     @Test
     public void testEvalInline() throws Exception {
         final String text = "package org.drools.compiler\n" +
                             "rule \"inline eval\"\n" +
                             "when\n" +
                             "    $str : String()\n" +
                             "    Person( eval( name.startsWith($str) && age == 18) )\n" +
                             "then\n" +
                             "end";
         KnowledgeBase kbase = loadKnowledgeBaseFromString( text );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( "b" );

         ksession.insert( new Person( "mark",
                                      50 ) );
         int rules = ksession.fireAllRules();
         assertEquals( 0,
                       rules );

         ksession.insert( new Person( "bob",
                                      18 ) );
         rules = ksession.fireAllRules();
         assertEquals( 1,
                       rules );

     }

     @Test
     public void testMethodCalls() throws Exception {
         final String text = "package org.drools.compiler\n" +
                             "rule \"method calls\"\n" +
                             "when\n" +
                             "    Person( getName().substring(2) == 'b' )\n" +
                             "then\n" +
                             "end";
         KnowledgeBase kbase = loadKnowledgeBaseFromString( text );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( new Person( "mark",
                                      50 ) );
         int rules = ksession.fireAllRules();
         assertEquals( 0,
                       rules );

         ksession.insert( new Person( "bob",
                                      18 ) );
         rules = ksession.fireAllRules();
         assertEquals( 1,
                       rules );

     }

     @Test
     public void testAlphaExpression() throws Exception {
         final String text = "package org.drools.compiler\n" +
                             "rule \"alpha\"\n" +
                             "when\n" +
                             "    Person( 5 < 6 )\n" +
                             "then\n" +
                             "end";
         KnowledgeBase kbase = loadKnowledgeBaseFromString( text );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( new Person( "mark",
                                      50 ) );
         int rules = ksession.fireAllRules();
         assertEquals( 1,
                       rules );

     }

     @Test
     public void testEvalCE() throws Exception {
         final String text = "package org.drools.compiler\n" +
                             "rule \"inline eval\"\n" +
                             "when\n" +
                             "    $str : String()\n" +
                             "    $p   : Person()\n" +
                             "    eval( $p.getName().startsWith($str) && $p.getName().endsWith($str) )" +
                             "then\n" +
                             "end";
         KnowledgeBase kbase = loadKnowledgeBaseFromString( text );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( "b" );

         ksession.insert( new Person( "mark",
                                      50 ) );
         int rules = ksession.fireAllRules();
         assertEquals( 0,
                       rules );

         ksession.insert( new Person( "bob",
                                      18 ) );
         rules = ksession.fireAllRules();
         assertEquals( 1,
                       rules );

     }

     @Test
     public void testEvalRewrite() throws Exception {
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_EvalRewrite.drl" ) );
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         final Order order1 = new Order( 10,
                                         "Bob" );
         final OrderItem item11 = new OrderItem( order1,
                                                 1 );
         final OrderItem item12 = new OrderItem( order1,
                                                 2 );
         order1.addItem( item11 );
         order1.addItem( item12 );
         final Order order2 = new Order( 11,
                                         "Bob" );
         final OrderItem item21 = new OrderItem( order2,
                                                 1 );
         final OrderItem item22 = new OrderItem( order2,
                                                 2 );
         order2.addItem( item21 );
         order2.addItem( item22 );
         final Order order3 = new Order( 12,
                                         "Bob" );
         final OrderItem item31 = new OrderItem( order3,
                                                 1 );
         final OrderItem item32 = new OrderItem( order3,
                                                 2 );
         order3.addItem( item31 );
         order3.addItem( item32 );
         final Order order4 = new Order( 13,
                                         "Bob" );
         final OrderItem item41 = new OrderItem( order4,
                                                 1 );
         final OrderItem item42 = new OrderItem( order4,
                                                 2 );
         order4.addItem( item41 );
         order4.addItem( item42 );
         ksession.insert( order1 );
         ksession.insert( item11 );
         ksession.insert( item12 );
         ksession.insert( order2 );
         ksession.insert( item21 );
         ksession.insert( item22 );
         ksession.insert( order3 );
         ksession.insert( item31 );
         ksession.insert( item32 );
         ksession.insert( order4 );
         ksession.insert( item41 );
         ksession.insert( item42 );

         ksession.fireAllRules();

         assertEquals( 5,
                       list.size() );
         assertTrue( list.contains( item11 ) );
         assertTrue( list.contains( item12 ) );
         assertTrue( list.contains( item22 ) );
         assertTrue( list.contains( order3 ) );
         assertTrue( list.contains( order4 ) );

     }

     @Test
     public void testMapAccess() throws Exception {
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_MapAccess.drl" ) );
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         Map map = new HashMap();
         map.put( "name",
                  "Edson" );
         map.put( "surname",
                  "Tirelli" );
         map.put( "age",
                  "28" );

         ksession.insert( map );

         ksession.fireAllRules();

         assertEquals( 1,
                       list.size() );
         assertTrue( list.contains( map ) );
     }

     @Test
     public void testMapNullConstraint() throws Exception {
         KnowledgeBase kbase = loadKnowledgeBase( "test_mapNullConstraints.drl" );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         org.kie.api.event.rule.AgendaEventListener ael = mock( org.kie.api.event.rule.AgendaEventListener.class );
         ksession.addEventListener( ael );
         new WorkingMemoryConsoleLogger( ksession );

         Map addresses = new HashMap();
         addresses.put( "home",
                        new Address( "home street" ) );
         Person bob = new Person( "Bob" );
         bob.setNamedAddresses( addresses );

         ksession.insert( bob );
         ksession.fireAllRules();

         ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> arg = ArgumentCaptor.forClass( org.kie.api.event.rule.AfterMatchFiredEvent.class );
         verify( ael,
                 times( 4 ) ).afterMatchFired(arg.capture());
         org.kie.api.event.rule.AfterMatchFiredEvent aaf = arg.getAllValues().get( 0 );
         assertThat( aaf.getMatch().getRule().getName(),
                     is( "1. home != null" ) );
         aaf = arg.getAllValues().get( 1 );
         assertThat( aaf.getMatch().getRule().getName(),
                     is( "2. not home == null" ) );

         aaf = arg.getAllValues().get( 2 );
         assertThat( aaf.getMatch().getRule().getName(),
                     is( "7. work == null" ) );
         aaf = arg.getAllValues().get( 3 );
         assertThat( aaf.getMatch().getRule().getName(),
                     is( "8. not work != null" ) );
     }

     @Test
     public void testNoneTypeSafeDeclarations() {
         // same namespace
         String str = "package org.drools.compiler\n" +
                      "global java.util.List list\n" +
                      "declare Person\n" +
                      "    @typesafe(false)\n" +
                      "end\n" +
                      "rule testTypeSafe\n dialect \"mvel\" when\n" +
                      "   $p : Person( object.street == 's1' )\n" +
                      "then\n" +
                      "   list.add( $p );\n" +
                      "end\n";

         executeTypeSafeDeclarations( str,
                                      true );

         // different namespace with import
         str = "package org.drools.compiler.test\n" +
               "import org.drools.compiler.Person\n" +
               "global java.util.List list\n" +
               "declare Person\n" +
               "    @typesafe(false)\n" +
               "end\n" +
               "rule testTypeSafe\n dialect \"mvel\" when\n" +
               "   $p : Person( object.street == 's1' )\n" +
               "then\n" +
               "   list.add( $p );\n" +
               "end\n";
         executeTypeSafeDeclarations( str,
                                      true );

         // different namespace without import using qualified name
         str = "package org.drools.compiler.test\n" +
               "global java.util.List list\n" +
               "declare org.drools.compiler.Person\n" +
               "    @typesafe(false)\n" +
               "end\n" +
               "rule testTypeSafe\n dialect \"mvel\" when\n" +
               "   $p : org.drools.compiler.Person( object.street == 's1' )\n" +
               "then\n" +
               "   list.add( $p );\n" +
               "end\n";
         executeTypeSafeDeclarations( str,
                                      true );

         // this should fail as it's not declared non typesafe
         str = "package org.drools.compiler.test\n" +
               "global java.util.List list\n" +
               "declare org.drools.compiler.Person\n" +
               "    @typesafe(true)\n" +
               "end\n" +
               "rule testTypeSafe\n dialect \"mvel\" when\n" +
               "   $p : org.drools.compiler.Person( object.street == 's1' )\n" +
               "then\n" +
               "   list.add( $p );\n" +
               "end\n";
         executeTypeSafeDeclarations( str,
                                      false );
     }

     private void executeTypeSafeDeclarations(String str,
                                              boolean mustSucceed) {
         KnowledgeBase kbase = null;
         try {
             kbase = loadKnowledgeBaseFromString(str);
             if ( !mustSucceed ) {
                 fail("Compilation Should fail" );
             }
         } catch ( Throwable e ) {
             if ( mustSucceed ) {
                 fail("Compilation Should succeed" );
             }
             return;
         }

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         Address a = new Address( "s1" );
         Person p = new Person( "yoda" );
         p.setObject( a );

         ksession.insert( p );
         ksession.fireAllRules();
         assertEquals( p,
                       list.get( 0 ) );
     }

     @Test
     public void testMapAccessWithVariable() throws Exception {
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_MapAccessWithVariable.drl" ) );
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         Map map = new HashMap();
         map.put( "name",
                  "Edson" );
         map.put( "surname",
                  "Tirelli" );
         map.put( "age",
                  "28" );

         ksession.insert( map );
         ksession.insert( "name" );

         ksession.fireAllRules();

         assertEquals( 1,
                       list.size() );
         assertTrue( list.contains( map ) );
     }

     // Drools does not support variables inside bindings yet... but we should...
     @Test
     public void testMapAccessWithVariable2() {
         String str = "package org.drools.compiler;\n" +
                      "import java.util.Map;\n" +
                      "rule \"map access with variable\"\n" +
                      "    when\n" +
                      "        $key : String( )\n" +
                      "        $p1 : Person( name == 'Bob', namedAddresses[$key] != null, $na : namedAddresses[$key] )\n" +
                      "        $p2 : Person( name == 'Mark', namedAddresses[$key] == $na )\n" +
                      "    then\n" +
                      "end\n";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                       ResourceType.DRL );

         Assert.assertTrue( kbuilder.hasErrors() );
     }

     @Test
     public void testHalt() throws Exception {
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_halt.drl" ) );
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         ksession.insert( new Integer( 0 ) );
         ksession.fireAllRules();

         assertEquals( 10,
                       results.size() );
         for ( int i = 0; i < 10; i++ ) {
             assertEquals( new Integer( i ),
                           results.get( i ) );
         }
     }

     @Test
     public void testFireLimit() throws Exception {
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_fireLimit.drl" ) );
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         ksession.insert( new Integer( 0 ) );
         int count = ksession.fireAllRules();
         assertEquals( 21, count );

         assertEquals( 20,
                       results.size() );
         for ( int i = 0; i < 20; i++ ) {
             assertEquals( new Integer( i ),
                           results.get( i ) );
         }
         results.clear();

         ksession.insert( new Integer( 0 ) );
         count = ksession.fireAllRules( 10 );
         assertEquals( 10, count );

         assertEquals( 10,
                       results.size() );
         for ( int i = 0; i < 10; i++ ) {
             assertEquals( new Integer( i ),
                           results.get( i ) );
         }

         count = ksession.fireAllRules(); //should finish the rest
         assertEquals( 11, count );
         assertEquals( 20,
                       results.size() );
         for ( int i = 0; i < 20; i++ ) {
             assertEquals( new Integer( i ),
                           results.get( i ) );
         }
         results.clear();

         ksession.insert( new Integer( 0 ) );
         count = ksession.fireAllRules();

         assertEquals( 21, count );

         assertEquals( 20,
                       results.size() );
         for ( int i = 0; i < 20; i++ ) {
             assertEquals( new Integer( i ),
                           results.get( i ) );
         }
         results.clear();

     }

     @Test
     public void testEqualitySupport() throws Exception {
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_equalitySupport.drl" ) );
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         PersonWithEquals person = new PersonWithEquals( "bob",
                                                         30 );

         ksession.insert( person );

         ksession.fireAllRules();

         assertEquals( 1,
                       results.size() );
         assertEquals( "mark",
                       results.get( 0 ) );

     }

     @Test
     public void testCharComparisons() throws Exception {
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_charComparisons.drl" ) );
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         Primitives p1 = new Primitives();
         p1.setCharPrimitive( 'a' );
         p1.setStringAttribute( "b" );
         Primitives p2 = new Primitives();
         p2.setCharPrimitive( 'b' );
         p2.setStringAttribute( "a" );

         ksession.insert( p1 );
         ksession.insert( p2 );

         ksession.fireAllRules();

         assertEquals( 3,
                       results.size() );
         assertEquals( "1",
                       results.get( 0 ) );
         assertEquals( "2",
                       results.get( 1 ) );
         assertEquals( "3",
                       results.get( 2 ) );

     }

     @Test
     public void testAlphaNodeSharing() throws Exception {
         KieBaseConfiguration kbc = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
         kbc.setOption( ShareAlphaNodesOption.YES );
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( kbc, "test_alphaNodeSharing.drl" ) );
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         Person p1 = new Person( "bob",
                                 5 );
         ksession.insert( p1 );

         ksession.fireAllRules();

         assertEquals( 2,
                       results.size() );
         assertEquals( "1",
                       results.get( 0 ) );
         assertEquals( "2",
                       results.get( 1 ) );

     }

     @Test
     public void testSelfReference2() throws Exception {
         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_SelfReference2.drl" ) );
         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         ksession.insert( new Cheese() );

         ksession.fireAllRules();

         assertEquals( 0,
                       results.size() );
     }

     @Test
     public void testSelfJoinWithIndex() throws IOException,
                                        ClassNotFoundException {
         String drl = "";
         drl += "package org.drools.compiler.test\n";
         drl += "import org.drools.compiler.Person\n";
         drl += "global java.util.List list\n";
         drl += "rule test1\n";
         drl += "when\n";
         drl += "   $p1 : Person( $name : name, $age : age )\n";
         drl += "   $p2 : Person( name == $name, age < $age)\n";
         drl += "then\n";
         drl += "    list.add( $p1 );\n";
         drl += "end\n";

         final KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( drl ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         Person p1 = new Person( "darth",
                                 30 );
         FactHandle fh1 = ksession.insert( p1 );

         Person p2 = new Person( "darth",
                                 25 );
         FactHandle fh2 = ksession.insert( p2 ); // creates activation.

         p1.setName( "yoda" );
         ksession.update( fh1,
                          p1 ); // creates activation

         ksession.fireAllRules();

         assertEquals( 0,
                       list.size() );
     }

     @Test
     public void testMergingDifferentPackages() throws Exception {
         // using the same builder
         try {
             Collection<KnowledgePackage> kpkgs = loadKnowledgePackages( "test_RuleNameClashes1.drl",
                                                                         "test_RuleNameClashes2.drl" );
             assertEquals( 3,
                           kpkgs.size() );
             for ( KnowledgePackage kpkg : kpkgs ) {
                 if (kpkg.getName().equals("org.drools.package1")) {
                     assertEquals( "rule 1",
                                   kpkg.getRules().iterator().next().getName() );
                 }
             }
         } catch ( KnowledgeBuilderImpl.PackageMergeException e ) {
             fail( "unexpected exception: " + e.getMessage() );
         } catch ( RuntimeException e ) {
             e.printStackTrace();
             fail( "unexpected exception: " + e.getMessage() );
         }
     }

     @Test
     public void testSelfJoinAndNotWithIndex() throws IOException,
                                              ClassNotFoundException {
         String drl = "";
         drl += "package org.drools.compiler.test\n";
         drl += "import org.drools.compiler.Person\n";
         drl += "global java.util.List list\n";
         drl += "rule test1\n";
         drl += "when\n";

         // selects the youngest person, for
         drl += "   $p1 : Person( )\n";
         drl += "     not Person( name == $p1.name, age < $p1.age )\n";

         // select the youngest person with the same name as $p1, but different likes and must be older
         drl += "   $p2 : Person( name == $p1.name, likes != $p1.likes, age > $p1.age)\n";
         drl += "     not Person( name == $p1.name, likes == $p2.likes, age < $p2.age )\n";
         drl += "then\n";
         drl += "    System.out.println( $p1 + \":\" + $p2 );\n";
         drl += "    list.add( $p1 );\n";
         drl += "    list.add( $p2 );\n";
         drl += "end\n";

         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( drl ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         Person p0 = new Person( "yoda", 0 );
         p0.setLikes( "cheddar" );
         FactHandle fh0 = ksession.insert( p0 );

         Person p1 = new Person( "darth", 15 );
         p1.setLikes( "cheddar" );
         FactHandle fh1 = ksession.insert( p1 );

         Person p2 = new Person( "darth", 25 );
         p2.setLikes( "cheddar" );
         FactHandle fh2 = ksession.insert( p2 ); // creates activation.

         Person p3 = new Person( "darth", 30 );
         p3.setLikes( "brie" );
         FactHandle fh3 = ksession.insert( p3 );

         ksession.fireAllRules();
         // selects p1 and p3
         assertEquals( 2,
                       list.size() );
         assertSame( p1,
                     list.get( 0 ) );
         assertSame( p3,
                     list.get( 1 ) );

         p1.setName( "yoda" );
         ksession.update( fh1,
                          p1 ); // creates activation

         ksession.fireAllRules();
         // now selects p2 and p3
         assertEquals( 4,
                       list.size() );
         assertSame( p2,
                     list.get( 2 ) );
         assertSame( p3,
                     list.get( 3 ) );
     }

     @Test
     public void testMergingDifferentPackages2() throws Exception {
         // using different builders
         try {
             Collection<KnowledgePackage> kpkgs1 = loadKnowledgePackages( "test_RuleNameClashes1.drl" );
             assertEquals( 1,
                           kpkgs1.iterator().next().getRules().size() );

             Collection<KnowledgePackage> kpkgs2 = loadKnowledgePackages( "test_RuleNameClashes2.drl" );
             assertEquals( 1,
                           kpkgs2.iterator().next().getRules().size() );

             KnowledgeBase kbase = loadKnowledgeBase();
             kbase.addKnowledgePackages( kpkgs1 );
             kbase.addKnowledgePackages( kpkgs2 );
             kbase = SerializationHelper.serializeObject( kbase );
             StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

             final List results = new ArrayList();
             ksession.setGlobal( "results",
                                 results );

             ksession.insert( new Cheese( "stilton",
                                          10 ) );
             ksession.insert( new Cheese( "brie",
                                          5 ) );

             ksession.fireAllRules();

             assertEquals( results.toString(),
                           2,
                           results.size() );
             assertTrue( results.contains( "p1.r1" ) );
             assertTrue( results.contains( "p2.r1" ) );

         } catch ( KnowledgeBuilderImpl.PackageMergeException e ) {
             fail( "Should not raise exception when merging different packages into the same rulebase: " + e.getMessage() );
         } catch ( Exception e ) {
             e.printStackTrace();
             fail( "unexpected exception: " + e.getMessage() );
         }
     }

     @Test
     public void testMergePackageWithSameRuleNames() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_MergePackageWithSameRuleNames1.drl" ) );
         Collection<KnowledgePackage> kpkgs = loadKnowledgePackages( "test_MergePackageWithSameRuleNames2.drl" );
         kbase.addKnowledgePackages( kpkgs );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         ksession.fireAllRules();

         assertEquals( 1,
                       results.size() );

         assertEquals( "rule1 for the package2",
                       results.get( 0 ) );
     }

     @Test
     public void testRuleRemovalWithJoinedRootPattern() {
         String str = "";
         str += "package org.drools.compiler \n";
         str += "rule rule1 \n";
         str += "when \n";
         str += "  String() \n";
         str += "  Person() \n";
         str += "then \n";
         str += "end  \n";
         str += "rule rule2 \n";
         str += "when \n";
         str += "  String() \n";
         str += "  Cheese() \n";
         str += "then \n";
         str += "end  \n";
         KnowledgeBase kbase = loadKnowledgeBaseFromString( RuleEngineOption.PHREAK, str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         DefaultFactHandle handle = (DefaultFactHandle) ksession.insert( "hello" );
         ksession.fireAllRules();
         LeftTuple leftTuple = handle.getFirstLeftTuple();
         assertNotNull( leftTuple );
         assertNotNull( leftTuple.getPeer() );
         kbase.removeRule( "org.drools.compiler",
                           "rule2" );
         leftTuple = handle.getFirstLeftTuple();
         assertNotNull( leftTuple );
         assertNull( leftTuple.getLeftParentNext() );
     }

     // JBRULES-1808
     @Test
     public void testKnowledgeHelperFixerInStrings() {
         String str = "";
         str += "package org.simple \n";
         str += "global java.util.List list \n";
         str += "rule xxx \n";
         str += "  no-loop true ";
         str += "when \n";
         str += "  $fact : String() \n";
         str += "then \n";
         str += "  list.add(\"This is an update()\"); \n";
         str += "  list.add(\"This is an update($fact)\"); \n";
         str += "  update($fact); \n";
         str += "end  \n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         ksession.insert( "hello" );
         ksession.fireAllRules();

         ksession.dispose();

         assertEquals( 2,
                       list.size() );
         assertEquals( "This is an update()",
                       list.get( 0 ) );
         assertEquals( "This is an update($fact)",
                       list.get( 1 ) );
     }

     @Test
     public void testEmptyAfterRetractInIndexedMemory() {
         String str = "";
         str += "package org.simple \n";
         str += "import org.drools.compiler.Person\n";
         str += "global java.util.List list \n";
         str += "rule xxx dialect 'mvel' \n";
         str += "when \n";
         str += "  Person( $name : name ) \n";
         str += "  $s : String( this == $name) \n";
         str += "then \n";
         str += "  list.add($s); \n";
         str += "end  \n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         Person p = new Person( "ackbar" );
         FactHandle ph = ksession.insert( p );
         FactHandle sh = ksession.insert( "ackbar" );
         ksession.fireAllRules();
         ksession.dispose();

         assertEquals( 1,
                       list.size() );
         assertEquals( "ackbar",
                       list.get( 0 ) );
     }

     @Test
     public void testRuleReplacement() throws Exception {
         // test rule replacement
         Collection<KnowledgePackage> kpkgs = loadKnowledgePackages( "test_RuleNameClashes3.drl",
                                                                     "test_RuleNameClashes3.drl" );

         for (KnowledgePackage kpkg : kpkgs) {
             if (kpkg.getName().equals("org.drools.package1")) {
                 assertEquals( 1, kpkg.getRules().size() );
                 break;
             }
         }

         KnowledgeBase kbase = loadKnowledgeBase();
         kbase.addKnowledgePackages( kpkgs );
         kbase = SerializationHelper.serializeObject( kbase );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         ksession.insert( new Cheese( "stilton",
                                      10 ) );
         ksession.insert( new Cheese( "brie",
                                      5 ) );

         ksession.fireAllRules();

         assertEquals( results.toString(),
                       0,
                       results.size() );

         ksession.insert( new Cheese( "muzzarella",
                                      7 ) );

         ksession.fireAllRules();

         assertEquals( results.toString(),
                       1,
                       results.size() );
         assertTrue( results.contains( "p1.r3" ) );
     }

     @Test
     public void testBindingsOnConnectiveExpressions() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_bindings.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         ksession.insert( new Cheese( "stilton",
                                      15 ) );

         ksession.fireAllRules();

         assertEquals( 2,
                       results.size() );
         assertEquals( "stilton",
                       results.get( 0 ) );
         assertEquals( new Integer( 15 ),
                       results.get( 1 ) );
     }

     @Test
     public void testMultipleFroms() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_multipleFroms.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         final Cheesery cheesery = new Cheesery();
         cheesery.addCheese( new Cheese( "stilton",
                                         15 ) );
         cheesery.addCheese( new Cheese( "brie",
                                         10 ) );

         ksession.setGlobal( "cheesery",
                             cheesery );

         ksession.fireAllRules();

         assertEquals( 2,
                       results.size() );
         assertEquals( 2,
                       ((List) results.get( 0 )).size() );
         assertEquals( 2,
                       ((List) results.get( 1 )).size() );
     }

     @Test
     public void testNullHashing() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_NullHashing.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         ksession.insert( new Cheese( "stilton",
                                      15 ) );
         ksession.insert( new Cheese( "",
                                      10 ) );
         ksession.insert( new Cheese( null,
                                      8 ) );

         ksession.fireAllRules();

         assertEquals( 3,
                       results.size() );
     }

     @Test
     public void testDefaultBetaConstrains() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_DefaultBetaConstraint.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );
         final FirstClass first = new FirstClass( "1",
                                                  "2",
                                                  "3",
                                                  "4",
                                                  "5" );
         final FactHandle handle = ksession.insert( first );
         ksession.fireAllRules();
         assertEquals( 1,
                       results.size() );
         assertEquals( "NOT",
                       results.get( 0 ) );

         ksession.insert( new SecondClass() );
         ksession.update( handle,
                          first );
         ksession.fireAllRules();
         assertEquals( 2,
                       results.size() );
         assertEquals( "NOT",
                       results.get( 1 ) );

         ksession.update( handle,
                          first );
         ksession.insert( new SecondClass( null,
                                           "2",
                                           "3",
                                           "4",
                                           "5" ) );
         ksession.fireAllRules();
         assertEquals( 3,
                       results.size() );
         assertEquals( "NOT",
                       results.get( 2 ) );

         ksession.update( handle,
                          first );
         ksession.insert( new SecondClass( "1",
                                           null,
                                           "3",
                                           "4",
                                           "5" ) );
         ksession.fireAllRules();
         assertEquals( 4,
                       results.size() );
         assertEquals( "NOT",
                       results.get( 3 ) );

         ksession.update( handle,
                          first );
         ksession.insert( new SecondClass( "1",
                                           "2",
                                           null,
                                           "4",
                                           "5" ) );
         ksession.fireAllRules();
         assertEquals( 5,
                       results.size() );
         assertEquals( "NOT",
                       results.get( 4 ) );

         ksession.update( handle,
                          first );
         ksession.insert( new SecondClass( "1",
                                           "2",
                                           "3",
                                           null,
                                           "5" ) );
         ksession.fireAllRules();
         assertEquals( 6,
                       results.size() );
         assertEquals( "NOT",
                       results.get( 5 ) );

         ksession.update( handle,
                          first );
         ksession.insert( new SecondClass( "1",
                                           "2",
                                           "3",
                                           "4",
                                           null ) );
         ksession.fireAllRules();
         assertEquals( 7,
                       results.size() );
         assertEquals( "NOT",
                       results.get( 6 ) );

         ksession.insert( new SecondClass( "1",
                                           "2",
                                           "3",
                                           "4",
                                           "5" ) );
         ksession.update( handle,
                          first );
         ksession.fireAllRules();
         assertEquals( 8,
                       results.size() );
         assertEquals( "EQUALS",
                       results.get( 7 ) );

     }

     @Test
     public void testBooleanWrapper() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_BooleanWrapper.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         Primitives p1 = new Primitives();
         ksession.insert( p1 );
         ksession.fireAllRules();
         assertEquals( 0,
                       results.size() );

         Primitives p2 = new Primitives();
         p2.setBooleanWrapper( Boolean.FALSE );
         ksession.insert( p2 );
         ksession.fireAllRules();
         assertEquals( 0,
                       results.size() );

         Primitives p3 = new Primitives();
         p3.setBooleanWrapper( Boolean.TRUE );
         ksession.insert( p3 );
         ksession.fireAllRules();
         assertEquals( 1,
                       results.size() );

     }

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
     public void testNotInStatelessSession() throws Exception {
         KieBaseConfiguration kbc = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
         kbc.setOption( RuleEngineOption.RETEOO );
         kbc.setOption( SequentialOption.YES );
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( kbc, "test_NotInStatelessSession.drl" ) );
         StatelessKnowledgeSession session = createStatelessKnowledgeSession( kbase );

         List list = new ArrayList();
         session.setGlobal( "list",
                            list );
         session.execute( "not integer" );
         assertEquals( "not integer",
                       list.get( 0 ) );
     }

     @Test
     public void testDynamicallyAddInitialFactRule() throws Exception {
         String rule = "package org.drools.compiler.test\n" +
                       "global java.util.List list\n" +
                       "rule xxx when\n" +
                       "   i:Integer()\n" +
                       "then\n" +
                       "   list.add(i);\n" +
                       "end";
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( rule ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         List list = new ArrayList();
         session.setGlobal( "list",
                            list );

         session.insert( new Integer( 5 ) );
         session.fireAllRules();

         assertEquals( new Integer( 5 ),
                       list.get( 0 ) );

         rule = "package org.drools.compiler.test\n" +
                "global java.util.List list\n" +
                "rule xxx when\n" +
                "then\n" +
                "   list.add(\"x\");\n" +
                "end";
         Collection<KnowledgePackage> kpkgs = loadKnowledgePackagesFromString( rule );
         kbase.addKnowledgePackages( kpkgs );

         session.fireAllRules();

         assertEquals( "x",
                       list.get( 1 ) );
     }

     @Test
     public void testEvalRewriteWithSpecialOperators() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_EvalRewriteWithSpecialOperators.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         final Order order1 = new Order( 10,
                                         "Bob" );
         final OrderItem item11 = new OrderItem( order1,
                                                 1 );
         final OrderItem item12 = new OrderItem( order1,
                                                 2 );
         order1.addItem( item11 );
         order1.addItem( item12 );
         final Order order2 = new Order( 11,
                                         "Bob" );
         final OrderItem item21 = new OrderItem( order2,
                                                 1 );
         final OrderItem item22 = new OrderItem( order2,
                                                 2 );
         order2.addItem( item21 );
         order2.addItem( item22 );
         final Order order3 = new Order( 12,
                                         "Bob" );
         final OrderItem item31 = new OrderItem( order3,
                                                 1 );
         final OrderItem item32 = new OrderItem( order3,
                                                 2 );
         final OrderItem item33 = new OrderItem( order3,
                                                 3 );
         order3.addItem( item31 );
         order3.addItem( item32 );
         order3.addItem( item33 );
         final Order order4 = new Order( 13,
                                         "Bob" );
         final OrderItem item41 = new OrderItem( order4,
                                                 1 );
         final OrderItem item42 = new OrderItem( order4,
                                                 2 );
         order4.addItem( item41 );
         order4.addItem( item42 );
         final Order order5 = new Order( 14,
                                         "Mark" );
         final OrderItem item51 = new OrderItem( order5,
                                                 1 );
         final OrderItem item52 = new OrderItem( order5,
                                                 2 );
         order5.addItem( item51 );
         order5.addItem( item52 );
         ksession.insert( order1 );
         ksession.insert( item11 );
         ksession.insert( item12 );
         ksession.insert( order2 );
         ksession.insert( item21 );
         ksession.insert( item22 );
         ksession.insert( order3 );
         ksession.insert( item31 );
         ksession.insert( item32 );
         ksession.insert( item33 );
         ksession.insert( order4 );
         ksession.insert( item41 );
         ksession.insert( item42 );
         ksession.insert( order5 );
         ksession.insert( item51 );
         ksession.insert( item52 );

         ksession.fireAllRules();

         assertEquals( 9,
                       list.size() );
         int index = 0;
         assertEquals( item11,
                       list.get( index++ ) );
         assertEquals( item12,
                       list.get( index++ ) );
         assertEquals( item21,
                       list.get( index++ ) );
         assertEquals( item22,
                       list.get( index++ ) );
         assertEquals( item31,
                       list.get( index++ ) );
         assertEquals( item33,
                       list.get( index++ ) );
         assertEquals( item41,
                       list.get( index++ ) );
         assertEquals( order5,
                       list.get( index++ ) );
         assertEquals( order5,
                       list.get( index++ ) );

     }

     @Test
     public void testImportColision() throws Exception {
         Collection<KnowledgePackage> kpkgs1 = loadKnowledgePackages( "nested1.drl" );
         Collection<KnowledgePackage> kpkgs2 = loadKnowledgePackages( "nested2.drl" );
         KnowledgeBase kbase = loadKnowledgeBase();
         kbase.addKnowledgePackages( kpkgs1 );
         kbase.addKnowledgePackages( kpkgs2 );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         kbase = SerializationHelper.serializeObject( kbase );

         ksession.insert( new FirstClass() );
         ksession.insert( new SecondClass() );
         ksession.insert( new FirstClass.AlternativeKey() );
         ksession.insert( new SecondClass.AlternativeKey() );

         ksession.fireAllRules();
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
     public void testNestedAccessors() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_NestedAccessors.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         final Order order1 = new Order( 11,
                                         "Bob" );
         final OrderItem item11 = new OrderItem( order1,
                                                 1 );
         final OrderItem item12 = new OrderItem( order1,
                                                 2 );
         order1.addItem( item11 );
         order1.addItem( item12 );

         ksession.insert( order1 );
         ksession.insert( item11 );
         ksession.insert( item12 );

         ksession.fireAllRules();

         assertEquals( 0,
                       list.size() );

         final Order order2 = new Order( 12,
                                         "Mark" );
         Order.OrderStatus status = new Order.OrderStatus();
         status.setActive( true );
         order2.setStatus( status );
         final OrderItem item21 = new OrderItem( order2,
                                                 1 );
         final OrderItem item22 = new OrderItem( order2,
                                                 2 );
         order1.addItem( item21 );
         order1.addItem( item22 );

         ksession.insert( order2 );
         ksession.insert( item21 );
         ksession.insert( item22 );

         ksession.fireAllRules();

         assertEquals( 2,
                       list.size() );
         assertSame( item21,
                     list.get( 0 ) );
         assertSame( item22,
                     list.get( 1 ) );
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
     public void testFromNestedAccessors() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_FromNestedAccessors.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         final Order order1 = new Order( 11,
                                         "Bob" );
         final OrderItem item11 = new OrderItem( order1,
                                                 1 );
         final OrderItem item12 = new OrderItem( order1,
                                                 2 );
         order1.addItem( item11 );
         order1.addItem( item12 );

         ksession.insert( order1 );
         ksession.insert( item11 );
         ksession.insert( item12 );

         ksession.fireAllRules();

         assertEquals( 1,
                       list.size() );

         assertSame( order1.getStatus(),
                     list.get( 0 ) );
     }

     @Test
     public void testFromArrayIteration() throws Exception {
         if( CommonTestMethodBase.phreak == RuleEngineOption.RETEOO ) {
             return;  //Disbaled due to phreak, as tests is order specific
         }
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_FromArrayIteration.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         List list = new ArrayList();

         session.setGlobal( "list",
                            list );
         session.insert( new DomainObjectHolder() );

         session.fireAllRules();

         assertEquals( 3,
                       list.size() );

         assertEquals( "Message3",
                       list.get( 0 ) );
         assertEquals( "Message2",
                       list.get( 1 ) );
         assertEquals( "Message1",
                       list.get( 2 ) );

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
     public void testEvalRewriteMatches() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_EvalRewriteMatches.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         final Order order1 = new Order( 14,
                                         "Mark" );
         final OrderItem item11 = new OrderItem( order1,
                                                 1 );
         final OrderItem item12 = new OrderItem( order1,
                                                 2 );
         order1.addItem( item11 );
         order1.addItem( item12 );

         ksession.insert( order1 );
         ksession.insert( item11 );
         ksession.insert( item12 );

         ksession.fireAllRules();

         assertEquals( 2,
                       list.size() );
         assertTrue( list.contains( item11 ) );
         assertTrue( list.contains( item12 ) );
     }

     @Test
     public void testConsequenceBuilderException() throws Exception {
         final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         builder.add( ResourceFactory.newClassPathResource( "test_ConsequenceBuilderException.drl", getClass() ),
                      ResourceType.DRL );

         assertTrue( builder.hasErrors() );
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
     public void testAlphaEvalWithOrCE() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_AlphaEvalWithOrCE.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         FactA a = new FactA();
         a.setField1( "a value" );

         ksession.insert( a );
         ksession.insert( new FactB() );
         ksession.insert( new FactC() );

         ksession.fireAllRules();

         assertEquals( "should not have fired",
                       0,
                       list.size() );
     }

     @Test
     public void testModifyRetractAndModifyInsert() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ModifyRetractInsert.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         Person bob = new Person( "Bob" );
         bob.setStatus( "hungry" );
         ksession.insert( bob );
         ksession.insert( new Cheese() );
         ksession.insert( new Cheese() );

         ksession.fireAllRules( 2 );

         assertEquals( "should have fired only once",
                       1,
                       list.size() );
     }

     @Test
     public void testAlphaCompositeConstraints() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_AlphaCompositeConstraints.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         Person bob = new Person( "bob",
                                  30 );

         ksession.insert( bob );
         ksession.fireAllRules();

         assertEquals( 1,
                       list.size() );
     }

     @Test
     public void testModifyBlock() throws Exception {
         doModifyTest( "test_ModifyBlock.drl" );
     }

     @Test
     public void testModifyBlockWithPolymorphism() throws Exception {
         doModifyTest( "test_ModifyBlockWithPolymorphism.drl" );
     }

     private void doModifyTest(String drlResource) throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( drlResource ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         Person bob = new Person( "Bob" );
         bob.setStatus( "hungry" );

         Cheese c = new Cheese();

         ksession.insert( bob );
         ksession.insert( c );

         ksession.fireAllRules();

         assertEquals( 10,
                       c.getPrice() );
         assertEquals( "fine",
                       bob.getStatus() );
     }

     @Test
     public void testModifyBlockWithFrom() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ModifyBlockWithFrom.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         ksession.setGlobal( "results",
                             results );

         Person bob = new Person( "Bob" );
         Address addr = new Address( "abc" );
         bob.addAddress( addr );

         ksession.insert( bob );
         ksession.insert( addr );

         ksession.fireAllRules();

         // modify worked
         assertEquals( "12345",
                       addr.getZipCode() );
         // chaining worked
         assertEquals( 1,
                       results.size() );
         assertEquals( addr,
                       results.get( 0 ) );
     }

     // this test requires mvel 1.2.19. Leaving it commented until mvel is released.
     @Test
     public void testJavaModifyBlock() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_JavaModifyBlock.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         Person bob = new Person( "Bob",
                                  30 );
         bob.setStatus( "hungry" );
         ksession.insert( bob );
         ksession.insert( new Cheese() );
         ksession.insert( new Cheese() );
         ksession.insert( new OuterClass.InnerClass( 1 ) );

         ksession.fireAllRules();

         assertEquals( 2,
                       list.size() );
         assertEquals( "full",
                       bob.getStatus() );
         assertEquals( 31,
                       bob.getAge() );
         assertEquals( 2,
                       ((OuterClass.InnerClass) list.get( 1 )).getIntAttr() );
     }

     @Test
     public void testOrCE() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_OrCE.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         ksession.insert( new Cheese( "brie",
                                      10 ) );
         ksession.insert( new Person( "bob" ) );

         ksession.fireAllRules();

         assertEquals( "should have fired once",
                       1,
                       list.size() );
     }

     @Test
     public void testOrWithAndUsingNestedBindings() throws IOException,
                                                   ClassNotFoundException {
         String str = "";
         str += "package org.drools.compiler\n";
         str += "import org.drools.compiler.Person\n";
         str += "global java.util.List mlist\n";
         str += "global java.util.List jlist\n";
         str += "rule rule1 dialect \"mvel\" \n";
         str += "when\n";
         str += "$a : Person( name == \"a\" )\n";
         str += "  (or $b : Person( name == \"b1\" )\n";
         str += "      (and $p : Person( name == \"p2\" )\n";
         str += "           $b : Person( name == \"b2\" ) )\n";
         str += "      (and $p : Person( name == \"p3\" )\n";
         str += "           $b : Person( name == \"b3\" ) )\n";
         str += "   )\n ";
         str += "then\n";
         str += "   mlist.add( $b );\n";
         str += "end\n";
         str += "rule rule2 dialect \"java\" \n";
         str += "when\n";
         str += "$a : Person( name == \"a\" )\n";
         str += "  (or $b : Person( name == \"b1\" )\n";
         str += "      (and $p : Person( name == \"p2\" )\n";
         str += "           $b : Person( name == \"b2\" ) )\n";
         str += "      (and $p : Person( name == \"p3\" )\n";
         str += "           $b : Person( name == \"b3\" ) )\n";
         str += "   )\n ";
         str += "then\n";
         str += "   jlist.add( $b );\n";
         str += "end\n";

         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Person a = new Person( "a" );
         Person b1 = new Person( "b1" );
         Person p2 = new Person( "p2" );
         Person b2 = new Person( "b2" );
         Person p3 = new Person( "p3" );
         Person b3 = new Person( "b3" );

         List mlist = new ArrayList();
         List jlist = new ArrayList();

         ksession.setGlobal( "mlist",
                             mlist );
         ksession.setGlobal( "jlist",
                             jlist );
         ksession.insert( a );
         ksession.insert( b1 );
         ksession.fireAllRules();
         assertEquals( b1,
                       mlist.get( 0 ) );
         assertEquals( b1,
                       jlist.get( 0 ) );

         ksession.dispose();
         ksession = createKnowledgeSession( kbase );
         ksession.setGlobal( "mlist",
                             mlist );
         ksession.setGlobal( "jlist",
                             jlist );
         ksession.insert( a );
         ksession.insert( b2 );
         ksession.insert( p2 );
         ksession.fireAllRules();
         assertEquals( b2,
                       mlist.get( 1 ) );
         assertEquals( b2,
                       jlist.get( 1 ) );

         ksession.dispose();
         ksession = createKnowledgeSession( kbase );
         ksession.setGlobal( "mlist",
                             mlist );
         ksession.setGlobal( "jlist",
                             jlist );
         ksession.insert( a );
         ksession.insert( b3 );
         ksession.insert( p3 );
         ksession.fireAllRules();
         assertEquals( b3,
                       mlist.get( 2 ) );
         assertEquals( b3,
                       jlist.get( 2 ) );

     }

     @Test
     public void testFieldBindingOnWrongFieldName() {
         //JBRULES-2527

         String str = "";
         str += "package org.drools.compiler\n";
         str += "import org.drools.compiler.Person\n";
         str += "global java.util.List mlist\n";
         str += "rule rule1 \n";
         str += "when\n";
         str += "   Person( $f : invalidFieldName, eval( $f != null ) )\n";
         str += "then\n";
         str += "end\n";

         try {
             KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
             kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                           ResourceType.DRL );

             if ( !kbuilder.hasErrors() ) {
                 fail( "KnowledgeBuilder should have errors" );
             }
         } catch ( Exception e ) {
             fail( "Exception should not be thrown " );
         }

         str = "";
         str += "package org.drools.compiler\n";
         str += "import org.drools.compiler.Person\n";
         str += "global java.util.List mlist\n";
         str += "rule rule1 \n";
         str += "when\n";
         str += "   Person( $f : invalidFieldName, name == ( $f ) )\n";
         str += "then\n";
         str += "end\n";

         try {
             KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
             kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                           ResourceType.DRL );

             if ( !kbuilder.hasErrors() ) {
                 fail( "KnowledgeBuilder should have errors" );
             }
         } catch ( Exception e ) {
             e.printStackTrace();
             fail( "Exception should not be thrown " );
         }
     }

     @Test
     public void testDeepNestedConstraints() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_DeepNestedConstraints.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         ksession.insert( new Person( "bob",
                                      "muzzarela" ) );
         ksession.insert( new Cheese( "brie",
                                      10 ) );
         ksession.insert( new Cheese( "muzzarela",
                                      80 ) );

         ksession.fireAllRules();

         assertEquals( "should have fired twice",
                       2,
                       list.size() );
     }

     @Test
     public void testGetFactHandleEqualityBehavior() throws Exception {
         KieBaseConfiguration kbc = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
         kbc.setOption( EqualityBehaviorOption.EQUALITY );
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( kbc ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         CheeseEqual cheese = new CheeseEqual( "stilton",
                                               10 );
         ksession.insert( cheese );
         FactHandle fh = ksession.getFactHandle( new CheeseEqual( "stilton",
                                                                                          10 ) );
         assertNotNull( fh );
     }

     @Test
     public void testGetFactHandleIdentityBehavior() throws Exception {
         KieBaseConfiguration kbc = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
         kbc.setOption(EqualityBehaviorOption.IDENTITY);
         KnowledgeBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase(kbc));
         StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

         CheeseEqual cheese = new CheeseEqual( "stilton",
                                               10 );
         ksession.insert( cheese );
         FactHandle fh1 = ksession.getFactHandle( new Cheese( "stilton",
                                                             10 ) );
         assertNull( fh1 );
         FactHandle fh2 = ksession.getFactHandle( cheese );
         assertNotNull( fh2 );
     }

     @Test
     public void testOrCEFollowedByEval() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_OrCEFollowedByEval.drl" ) );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         ksession.setGlobal( "results",
                             list );

         ksession.insert( new FactA( "X" ) );
         InternalFactHandle b = (InternalFactHandle) ksession.insert( new FactB( "X" ) );

         ksession.fireAllRules();

         assertEquals( "should have fired",
                       2,
                       list.size() );
         assertTrue( list.contains( b.getObject() ) );
     }

     @Test
     public void testNPEOnMVELAlphaPredicates() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_NPEOnMVELPredicate.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         final List list = new ArrayList();
         session.setGlobal( "results",
                            list );

         Cheese cheese = new Cheese( "stilton",
                                     10 );
         Cheesery cheesery = new Cheesery();
         cheesery.addCheese( cheese );
         Person bob = new Person( "bob",
                                  "stilton" );
         Cheese cheese2 = new Cheese();
         bob.setCheese( cheese2 );

         FactHandle p = session.insert( bob );
         FactHandle c = session.insert( cheesery );

         session.fireAllRules();

         assertEquals( "should not have fired",
                       0,
                       list.size() );

         cheese2.setType( "stilton" );
         session.update( p,
                         bob );
         session.fireAllRules();

         assertEquals( 1,
                       list.size() );

     }

     @Test
     public void testModifyWithLockOnActive() throws Exception {
         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ModifyWithLockOnActive.drl" ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         final List results = new ArrayList();
         session.setGlobal( "results",
                            results );

         final Person bob = new Person( "Bob",
                                        15 );
         final Person mark = new Person( "Mark",
                                         16 );
         final Person michael = new Person( "Michael",
                                            14 );
         session.insert( bob );
         session.insert( mark );
         session.insert( michael );
         session.getAgenda().getAgendaGroup( "feeding" ).setFocus();
         session.fireAllRules( 5 );

         assertEquals( 2,
                       ((List) session.getGlobal( "results" )).size() );
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
     public void testEvalWithLineBreaks() throws Exception {
         final KnowledgeBase kbase = loadKnowledgeBase("test_EvalWithLineBreaks.drl");

         final List<Person> results = new ArrayList<Person>();

         final StatefulKnowledgeSession session = createKnowledgeSession( kbase );
         session.setGlobal( "results",
                            results );

         session.insert( Integer.valueOf( 10 ) );
         session.fireAllRules();

         assertEquals( 1,
                       results.size() );
         assertEquals( Integer.valueOf( 10 ),
                       results.get( 0 ) );
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
     public void testKnowledgeContextJava() {
         KnowledgeBase kbase = loadKnowledgeBase("test_KnowledgeContextJava.drl");

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List<String> list = new ArrayList<String>();
         ksession.setGlobal( "list",
                             list );
         ksession.insert( new Message() );
         ksession.fireAllRules();
         assertEquals( 1,
                       list.size() );
         assertEquals( "Hello World",
                       list.get( 0 ) );
     }

     @Test
     public void testListOfMaps() {
         KnowledgeBase kbase = loadKnowledgeBase("test_TestMapVariableRef.drl");

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

         Map mapOne = new HashMap<String, Object>();
         Map mapTwo = new HashMap<String, Object>();

         mapOne.put( "MSG",
                     "testMessage" );
         mapTwo.put( "MSGTWO",
                     "testMessage" );

         list.add( mapOne );
         list.add( mapTwo );
         ksession.insert( list );
         ksession.fireAllRules();

         assertEquals( 3,
                       list.size() );

     }

     @Test
     public void testKnowledgeContextMVEL() {
         KnowledgeBase kbase = loadKnowledgeBase("test_KnowledgeContextMVEL.drl");
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List<String> list = new ArrayList<String>();
         ksession.setGlobal( "list",
                             list );
         ksession.insert( new Message() );
         ksession.fireAllRules();
         assertEquals( 1,
                       list.size() );
         assertEquals( "Hello World",
                       list.get( 0 ) );
     }

     @Test
     public void testJBRules2055() {
         KnowledgeBase kbase = loadKnowledgeBase("test_JBRules2055.drl");
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List<String> results = new ArrayList<String>();
         ksession.setGlobal( "results",
                             results );
         ksession.insert( new Cheese( "stilton" ) );
         ksession.insert( new Cheese( "brie" ) );
         ksession.insert( new Cheese( "muzzarella" ) );
         ksession.insert( new Person( "bob",
                                      "stilton" ) );
         ksession.fireAllRules();
         assertEquals( 2,
                       results.size() );
         assertEquals( "stilton",
                       results.get( 0 ) );
         assertEquals( "brie",
                       results.get( 1 ) );

     }

     @Test
     public void testJBRules2369() {
         KnowledgeBase kbase = loadKnowledgeBase("test_JBRules2369.drl");
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List<String> results = new ArrayList<String>();
         ksession.setGlobal( "results",
                             results );

         FactA a = new FactA();
         FactB b = new FactB( Integer.valueOf( 0 ) );

         FactHandle aHandle = ksession.insert( a );
         FactHandle bHandle = ksession.insert( b );

         ksession.fireAllRules();

         assertEquals( 1,
                       results.size() );

         ksession.update( aHandle,
                          a );

         ksession.fireAllRules();
         assertEquals( 2,
                       results.size() );
     }

     @Test
     public void testInsertionOrder() {
         KnowledgeBase kbase = loadKnowledgeBase("test_InsertionOrder.drl");

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List<String> results = new ArrayList<String>();
         ksession.setGlobal( "results",
                             results );
         ksession.insert( new Move( 1,
                                    2 ) );
         ksession.insert( new Move( 2,
                                    3 ) );

         Win win2 = new Win( 2 );
         Win win3 = new Win( 3 );

         ksession.fireAllRules();
         assertEquals( 2,
                       results.size() );
         assertTrue( results.contains( win2 ) );
         assertTrue( results.contains( win3 ) );

         ksession.dispose();
         ksession = createKnowledgeSession( kbase );
         results = new ArrayList<String>();
         ksession.setGlobal( "results",
                             results );
         // reverse the order of the inserts
         ksession.insert( new Move( 2,
                                    3 ) );
         ksession.insert( new Move( 1,
                                    2 ) );

         ksession.fireAllRules();
         assertEquals( 2,
                       results.size() );
         assertTrue( results.contains( win2 ) );
         assertTrue( results.contains( win3 ) );

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
     public void testNetworkBuildErrorAcrossEntryPointsAndFroms() throws Exception {
         String rule1 = "package org.drools.compiler\n";
         rule1 += "global java.util.List list\n";
         rule1 += "rule rule1\n";
         rule1 += "when\n";
         rule1 += "         Cheese() from entry-point \"testep\"\n";
         rule1 += "    $p : Person() from list\n";
         rule1 += "then \n";
         rule1 += "  list.add( \"rule1\" ) ;\n";
         rule1 += "  insert( $p );\n";
         rule1 += "end\n";
         rule1 += "rule rule2\n";
         rule1 += "when\n";
         rule1 += "  $p : Person() \n";
         rule1 += "then \n";
         rule1 += "  list.add( \"rule2\" ) ;\n";
         rule1 += "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString(rule1);

         final StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         final EntryPoint ep = ksession.getEntryPoint( "testep" );

         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         list.add( new Person( "darth" ) );
         ep.insert( new Cheese( "cheddar" ) );

         ksession.fireAllRules();
         assertEquals( 3,
                       list.size() );
     }

     @Test
     public void testBindingToMissingField() throws Exception {
         // JBRULES-3047
         String rule1 = "package org.drools.compiler\n";
         rule1 += "rule rule1\n";
         rule1 += "when\n";
         rule1 += "    Integer( $i : noSuchField ) \n";
         rule1 += "    eval( $i > 0 )\n";
         rule1 += "then \n";
         rule1 += "end\n";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( rule1.getBytes() ),
                       ResourceType.DRL );

         if ( !kbuilder.hasErrors() ) {
             fail( "this should have errors" );
         }
     }

     @Test
     public void testJBRules2140() {
         KnowledgeBase kbase = loadKnowledgeBase("test_JBRules2140.drl");
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List<String> results = new ArrayList<String>();
         ksession.setGlobal( "results",
                             results );
         ksession.fireAllRules();
         assertEquals( 2,
                       results.size() );
         assertTrue( results.contains( "java" ) );
         assertTrue( results.contains( "mvel" ) );

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
     public void testAddRemoveListeners() throws Exception {
         final KnowledgeBase kbase = loadKnowledgeBase("test_AddRemoveListeners.drl");

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         // creating listener as a jmock proxy
         final RuleRuntimeEventListener wmeListener = mock( RuleRuntimeEventListener.class );

         ksession.addEventListener( wmeListener );

         // listener will be notified of both facts insertion
         ksession.insert( new Cheese( "stilton" ) );
         ksession.insert( wmeListener );

         // firing rules will remove listener
         ksession.fireAllRules();

         // inserting another object into the working memory, listener should NOT be notified,
         // since it is no longer listening.
         ksession.insert( new Cheese( "brie" ) );

         verify( wmeListener,
                 times( 2 ) ).objectInserted( any( org.kie.api.event.rule.ObjectInsertedEvent.class ) );
     }

     @Test
     public void testInsert() throws Exception {
         String drl = "";
         drl += "package test\n";
         drl += "import org.drools.compiler.Person\n";
         drl += "import org.drools.compiler.Pet\n";
         drl += "import java.util.ArrayList\n";
         drl += "global java.util.List list\n";
         drl += "rule test\n";
         drl += "when\n";
         drl += "$person:Person()\n";
         drl += "$pets : ArrayList()\n";
         drl += "   from collect( \n";
         drl += "      Pet(\n";
         drl += "         ownerName == $person.name\n";
         drl += "      )\n";
         drl += "   )\n";
         drl += "then\n";
         drl += "  list.add( $person );\n";
         drl += "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List list = new ArrayList();
         ksession.setGlobal("list", list);

         Person p = new Person( "Toni" );
         ksession.insert( p);
         ksession.insert( new Pet( "Toni" ) );

         ksession.fireAllRules();

         assertEquals( 1, list.size() );
         assertSame( p, list.get( 0 ) );

     }


     @Test
     public void testMemberOfNotWorkingWithOr() throws Exception {

         String rule = "";
         rule += "package org.drools.compiler;\n";
         rule += "import java.util.ArrayList;\n";
         rule += "import org.drools.compiler.Person;\n";
         rule += "rule \"Test Rule\"\n";
         rule += "when\n";
         rule += "    $list: ArrayList()                                   \n";
         rule += "    ArrayList()                                          \n";
         rule += "            from collect(                                \n";
         rule += "                  Person(                                \n";
         rule += "                      (                                  \n";
         rule += "                          pet memberOf $list             \n";
         rule += "                      ) || (                             \n";
         rule += "                          pet == null                    \n";
         rule += "                      )                                  \n";
         rule += "                  )                                      \n";
         rule += "            )\n";
         rule += "then\n";
         rule += "  System.out.println(\"hello person\");\n";
         rule += "end";

         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( rule ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         Person toni = new Person( "Toni",
                                   12 );
         toni.setPet( new Pet( "Mittens" ) );

         session.insert( new ArrayList() );
         session.insert( toni );

         session.fireAllRules();
     }

     @Test
     public void testUnNamed() throws Exception {

         String rule = "";
         rule += "package org.drools.compiler;\n";
         rule += "import java.util.ArrayList;\n";
         rule += "import org.drools.compiler.Person;\n";
         rule += "rule \"Test Rule\"\n";
         rule += "when\n";
         rule += "    $list: ArrayList()                                   \n";
         rule += "    ArrayList()                                          \n";
         rule += "            from collect(                                \n";
         rule += "                  Person(                                \n";
         rule += "                      (                                  \n";
         rule += "                          pet memberOf $list             \n";
         rule += "                      ) || (                             \n";
         rule += "                          pet == null                    \n";
         rule += "                      )                                  \n";
         rule += "                  )                                      \n";
         rule += "            )\n";
         rule += "then\n";
         rule += "  System.out.println(\"hello person\");\n";
         rule += "end";

         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( rule ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );

         Person toni = new Person( "Toni",
                                   12 );
         toni.setPet( new Pet( "Mittens" ) );

         session.insert( new ArrayList() );
         session.insert( toni );

         session.fireAllRules();
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
     public void testGenericsInRHS() throws Exception {

         String rule = "";
         rule += "package org.drools.compiler;\n";
         rule += "import java.util.Map;\n";
         rule += "import java.util.HashMap;\n";
         rule += "rule \"Test Rule\"\n";
         rule += "  when\n";
         rule += "  then\n";
         rule += "    Map<String,String> map = new HashMap<String,String>();\n";
         rule += "end";

         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( rule ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );
         assertNotNull( session );
     }

     @Test
     public void testAccessingMapValues() throws Exception {

         String rule = "";
         rule += "package org.drools.compiler;\n";
         rule += "import org.drools.compiler.Pet;\n";
         rule += "rule \"Test Rule\"\n";
         rule += "  when\n";
         rule += "    $pet: Pet()\n";
         rule += "    Pet( \n";
         rule += "      ownerName == $pet.attributes[\"key\"] \n";
         rule += "    )\n";
         rule += "  then\n";
         rule += "    System.out.println(\"hi pet\");\n";
         rule += "end";

         KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( rule ) );
         StatefulKnowledgeSession session = createKnowledgeSession( kbase );
         assertNotNull( session );

         Pet pet1 = new Pet( "Toni" );
         pet1.getAttributes().put( "key",
                                   "value" );
         Pet pet2 = new Pet( "Toni" );

         session.insert( pet1 );
         session.insert( pet2 );

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
     public void testMVELConsequenceWithoutSemiColon1() throws Exception {
         String drl = "";
         drl += "package test\n";
         drl += "import org.drools.compiler.Person\n";
         drl += "import org.drools.compiler.Pet\n";
         drl += "rule test dialect 'mvel'\n";
         drl += "when\n";
         drl += "$person:Person()\n";
         drl += "$pet:Pet()\n";
         drl += "then\n";
         drl += "    delete($person) // some comment\n";
         drl += "    delete($pet) // another comment\n";
         drl += "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString(drl);

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         // create working memory mock listener
         RuleRuntimeEventListener wml = Mockito.mock( RuleRuntimeEventListener.class );

         ksession.addEventListener( wml );

         FactHandle personFH = ksession.insert( new Person( "Toni" ) );
         FactHandle petFH = ksession.insert( new Pet( "Toni" ) );

         int fired = ksession.fireAllRules();
         assertEquals( 1,
                       fired );

         // capture the arguments and check that the retracts happened
         ArgumentCaptor<ObjectDeletedEvent> retracts = ArgumentCaptor.forClass( ObjectDeletedEvent.class );
         verify( wml,
                 times( 2 ) ).objectDeleted(retracts.capture());
         List<ObjectDeletedEvent> values = retracts.getAllValues();
         assertThat( values.get( 0 ).getFactHandle(),
                     is( personFH ) );
         assertThat( values.get( 1 ).getFactHandle(),
                     is( petFH ) );

     }

     @Test
     public void testRuleMetaAttributes() throws Exception {
         String drl = "";
         drl += "package test\n";
         drl += "rule \"test meta attributes\"\n";
         drl += "    @id(1234 ) @author(  john_doe  ) @text(\"It's an escaped\\\" string\"  )\n";
         drl += "when\n";
         drl += "then\n";
         drl += "    // some comment\n";
         drl += "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString(drl);

         Rule rule = kbase.getRule( "test", "test meta attributes" );

         assertNotNull( rule );
         assertThat( (Integer) rule.getMetaData().get( "id" ),
                     is( 1234 ) );
         assertThat( (String) rule.getMetaData().get( "author" ),
                     is( "john_doe" ) );
         assertThat( (String) rule.getMetaData().get( "text" ),
                     is( "It's an escaped\" string" ) );

     }

     // following test depends on MVEL: http://jira.codehaus.org/browse/MVEL-212
     @Test
     public void testMVELConsequenceUsingFactConstructors() throws Exception {
         String drl = "";
         drl += "package test\n";
         drl += "import org.drools.compiler.Person\n";
         drl += "global org.drools.core.runtime.StatefulKnowledgeSession ksession\n";
         drl += "rule test dialect 'mvel'\n";
         drl += "when\n";
         drl += "    $person:Person( name == 'mark' )\n";
         drl += "then\n";
         drl += "    // below constructor for Person does not exist\n";
         drl += "    Person p = new Person( 'bob', 30, 555 )\n";
         drl += "    ksession.update(ksession.getFactHandle($person), new Person('bob', 30, 999, 453, 534, 534, 32))\n";
         drl += "end\n";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                       ResourceType.DRL );
         KnowledgeBuilderErrors errors = kbuilder.getErrors();

         assertTrue( kbuilder.hasErrors() );
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
     public void testOrWithReturnValueRestriction() throws Exception {
         String fileName = "test_OrWithReturnValue.drl";
         KnowledgeBase kbase = loadKnowledgeBase( fileName );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( new Cheese( "brie",
                                      18 ) );
         ksession.insert( new Cheese( "stilton",
                                      8 ) );
         ksession.insert( new Cheese( "brie",
                                      28 ) );

         int fired = ksession.fireAllRules();
         assertEquals( 2,
                       fired );
     }

     @Test
     public void testFromExprFollowedByNot() {
         String rule = "";
         rule += "package org.drools.compiler;\n";
         rule += "global java.util.List list;\n";
         rule += "rule \"Rule 1\"\n";
         rule += "    when\n";
         rule += "        p : Person ($var: pet )\n";
         rule += "        Pet () from $var\n";
         rule += "        not Pet ()\n";
         rule += "    then\n";
         rule += "       list.add( p );\n";
         rule += "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString(rule);
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         List list = new ArrayList();
         ksession.setGlobal( "list", list );

         Person p = new Person();
         p.setPet( new Pet() );
         ksession.insert( p );
         ksession.fireAllRules();

         assertEquals( 1, list.size() );
         assertSame(p, list.get(0));

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
     public void testLastMemoryEntryExistsBug() {
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
         str += "    exists A( this != null ) \n";
         str += "then \n";
         str += "  list.add(\"fired x1\"); \n";
         str += "end  \n";
         str += "rule x2 \n";
         str += "when \n";
         str += "    $s : String( this == 'x2' ) \n";
         str += "    exists A( field1 == $s, this != null ) \n"; // this ensures an index bucket
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

         assertEquals( 2,
                       list.size() );

         ksession.dispose();
     }

     @Test
     public void testNotIterativeModifyBug() {
         // JBRULES-2809
         // This bug occurs when a tuple is modified, the remove/add puts it onto the memory end
         // However before this was done it would attempt to find the next tuple, starting from itself
         // This meant it would just re-add itself as the blocker, but then be moved to end of the memory
         // If this tuple was then removed or changed, the blocked was unable to check previous tuples.

         String str = "";
         str += "package org.simple \n";
         str += "import " + A.class.getCanonicalName() + "\n";
         str += "global java.util.List list \n";
         str += "rule xxx \n";
         str += "when \n";
         str += "  $f1 : A() \n";
         str += "    not A(this != $f1,  eval(field2 == $f1.getField2())) \n";
         str += "    eval( !$f1.getField1().equals(\"1\") ) \n";
         str += "then \n";
         str += "  list.add($f1); \n";
         str += "end  \n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         A a1 = new A( "2",
                       "2" );
         A a2 = new A( "1",
                       "2" );
         A a3 = new A( "1",
                       "2" );

         FactHandle fa1 = ksession.insert( a1 );
         FactHandle fa2 = ksession.insert( a2 );
         FactHandle fa3 = ksession.insert( a3 );
         ksession.fireAllRules();

         // a1 is blocked by a2
         assertEquals( 0,
                       list.size() );

         // modify a2, so that a1 is now blocked by a3
         a2.setField2( "1" ); // Do
         ksession.update( fa2,
                          a2 );
         a2.setField2( "2" ); // Undo
         ksession.update( fa2,
                          a2 );

         // modify a3 to cycle, so that it goes on the memory end, but in a previous bug still blocked a1
         ksession.update( fa3,
                          a3 );

         a3.setField2( "1" ); // Do
         ksession.update( fa3,
                          a3 );
         ksession.fireAllRules();
         assertEquals( 0,
                       list.size() ); // this should still now blocked by a2, but bug from previous update hanging onto blocked

         ksession.dispose();
     }

     @Test
     public void testModifyWithLiaToEval() {
         String str = "";
         str += "package org.simple \n";
         str += "import " + Person.class.getCanonicalName() + "\n";
         str += "global java.util.List list \n";
         str += "rule xxx \n";
         str += "when \n";
         str += "    $p : Person() \n";
         str += "    eval( $p.getAge() > 30 ) \n";
         str += "then \n";
         str += "  list.add($p); \n";
         str += "end  \n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         Person p1 = new Person( "darth", 25 );
         FactHandle fh = ksession.insert( p1 );
         ksession.fireAllRules();
         assertEquals( 0, list.size() );

         p1.setAge( 35 );
         ksession.update( fh, p1 );
         ksession.fireAllRules();
         assertEquals( 1, list.size() );

         ksession.dispose();
     }

     @Test
     public void testExistsIterativeModifyBug() {
         // JBRULES-2809
         // This bug occurs when a tuple is modified, the remove/add puts it onto the memory end
         // However before this was done it would attempt to find the next tuple, starting from itself
         // This meant it would just re-add itself as the blocker, but then be moved to end of the memory
         // If this tuple was then removed or changed, the blocked was unable to check previous tuples.

         String str = "";
         str += "package org.simple \n";
         str += "import " + A.class.getCanonicalName() + "\n";
         str += "global java.util.List list \n";
         str += "rule xxx \n";
         str += "when \n";
         str += "  $f1 : A() \n";
         str += "    exists A(this != $f1, eval(field2 == $f1.getField2())) \n";
         str += "    eval( !$f1.getField1().equals(\"1\") ) \n";
         str += "then \n";
         str += "  list.add($f1); \n";
         str += "end  \n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         List list = new ArrayList();
         ksession.setGlobal( "list",
                             list );

         A a1 = new A( "2",
                       "2" );
         A a2 = new A( "1",
                       "2" );
         A a3 = new A( "1",
                       "2" );

         FactHandle fa1 = ksession.insert( a1 );
         FactHandle fa2 = ksession.insert( a2 );
         FactHandle fa3 = ksession.insert( a3 );

         // a2, a3 are blocked by a1
         // modify a1, so that a1,a3 are now blocked by a2
         a1.setField2( "1" ); // Do
         ksession.update( fa1,
                          a1 );
         a1.setField2( "2" ); // Undo
         ksession.update( fa1,
                          a1 );

         // modify a2, so that a1,a2 are now blocked by a3
         a2.setField2( "1" ); // Do
         ksession.update( fa2,
                          a2 );
         a2.setField2( "2" ); // Undo
         ksession.update( fa2,
                          a2 );

         // modify a3 to cycle, so that it goes on the memory end, but in a previous bug still blocked a1
         ksession.update( fa3,
                          a3 );

         a3.setField2( "1" ); // Do
         ksession.update( fa3,
                          a3 );
         ksession.fireAllRules();
         assertEquals( 1,
                       list.size() ); // a2 should still be blocked by a1, but bug from previous update hanging onto blocked

         ksession.dispose();
     }

     @Test
     public void testBindingsWithOr() throws InstantiationException,
                                     IllegalAccessException {
         // JBRULES-2917: matching of field==v1 || field==v2 breaks when variable binding is added

         String str = "package org.drools.compiler\n" +
                      "declare Assignment\n" +
                      "    source : int\n" +
                      "    target : int\n" +
                      "end\n" +
                      "rule ValueIsTheSame1\n" +
                      "when\n" +
                      "    Assignment( $t: target == 10 || target == source )\n" +
                      "then\n" +
                      "end\n" +
                      "rule ValueIsTheSame2\n" +
                      "when\n" +
                      "    Assignment( $t: target == source || target == 10 )\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         FactType asgType = kbase.getFactType( "org.drools.compiler",
                                               "Assignment" );
         Object asg = asgType.newInstance();
         asgType.set( asg,
                      "source",
                      10 );
         asgType.set( asg,
                      "target",
                      10 );

         ksession.insert( asg );

         int rules = ksession.fireAllRules();
         ksession.dispose();

         assertEquals( 2,
                       rules );
     }

     @Test
     public void testMVELClassReferences() throws InstantiationException,
                                          IllegalAccessException {
         String str = "package org.drools.compiler\n" +
                      "declare Assignment\n" +
                      "    source : Class\n" +
                      "    target : Class\n" +
                      "end\n" +
                      "rule ObjectIsAssignable1\n" +
                      "when\n" +
                      "    Assignment( $t: target == java.lang.Object.class || target == source )\n" +
                      "then\n" +
                      "end\n" +
                      "rule ObjectIsAssignable2\n" +
                      "when\n" +
                      "    Assignment( $t: target == source || target == java.lang.Object.class )\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         FactType asgType = kbase.getFactType( "org.drools.compiler",
                                               "Assignment" );
         Object asg = asgType.newInstance();
         asgType.set( asg,
                      "source",
                      Object.class );
         asgType.set( asg,
                      "target",
                      Object.class );

         ksession.insert( asg );

         int rules = ksession.fireAllRules();
         ksession.dispose();

         assertEquals( 2,
                       rules );
     }

     @Test
     public void testNotMatchesSucceeds() throws InstantiationException,
                                         IllegalAccessException {
         // JBRULES-2914: Rule misfires due to "not matches" not working

         String str = "package org.drools.compiler\n" +
                      "rule NotMatches\n" +
                      "when\n" +
                      "    Person( name == null || (name != null && name not matches \"-.{2}x.*\" ) )\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Person p = new Person( "-..x..xrwx" );

         ksession.insert( p );

         int rules = ksession.fireAllRules();
         ksession.dispose();

         assertEquals( 0,
                       rules );
     }

     @Test
     public void testNotMatchesFails() throws InstantiationException,
                                      IllegalAccessException {
         // JBRULES-2914: Rule misfires due to "not matches" not working

         String str = "package org.drools.compiler\n" +
                      "rule NotMatches\n" +
                      "when\n" +
                      "    Person( name == null || (name != null && name not matches \"-.{2}x.*\" ) )\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Person p = new Person( "d..x..xrwx" );

         ksession.insert( p );

         int rules = ksession.fireAllRules();
         ksession.dispose();

         assertEquals( 1,
                       rules );
     }

     @Test
     public void testNotEqualsOperator() {
         // JBRULES-3003: restriction evaluation returns 'false' for "trueField != falseField"

         String str = "package org.drools.compiler\n" +
                      "rule NotEquals\n" +
                      "when\n" +
                      "    Primitives( booleanPrimitive != booleanWrapper )\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Primitives p = new Primitives();
         p.setBooleanPrimitive( true );
         p.setBooleanWrapper( Boolean.FALSE );

         ksession.insert( p );

         int rules = ksession.fireAllRules();
         ksession.dispose();

         assertEquals( 1,
                       rules );
     }

     @Test
     public void testNotContainsOperator() {
         // JBRULES-2404: "not contains" operator doesn't work on nested fields

         String str = "package org.drools.compiler\n" +
                      "rule NotContains\n" +
                      "when\n" +
                      "    $oi : OrderItem( )\n" +
                      "    $o  : Order( items.values() not contains $oi )" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Order order1 = new Order( 1,
                                   "XYZ" );
         Order order2 = new Order( 2,
                                   "ABC" );
         OrderItem item11 = new OrderItem( order1,
                                           1 );
         order1.addItem( item11 );
         OrderItem item21 = new OrderItem( order2,
                                           1 );
         order2.addItem( item21 );

         ksession.insert( order1 );
         ksession.insert( item11 );

         // should not fire, as item11 is contained in order1.items
         int rules = ksession.fireAllRules();
         assertEquals( 0,
                       rules );

         // should fire as item21 is not contained in order1.items
         ksession.insert( item21 );
         rules = ksession.fireAllRules();
         assertEquals( 1,
                       rules );
     }

     @Test
     public void testOrWithFrom() {
         // JBRULES-2274: Rule does not fire as expected using deep object model and nested 'or' clause

         String str = "package org.drools.compiler\n" +
                      "rule NotContains\n" +
                      "when\n" +
                      "    $oi1 : OrderItem( )\n" +
                      "    $o1  : Order(number == 1) from $oi1.order; \n" +
                      "    ( eval(true) or eval(true) )\n" +
                      "    $oi2 : OrderItem( )\n" +
                      "    $o2  : Order(number == 2) from $oi2.order; \n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Order order1 = new Order( 1,
                                   "XYZ" );
         Order order2 = new Order( 2,
                                   "ABC" );
         OrderItem item11 = new OrderItem( order1,
                                           1 );
         order1.addItem( item11 );
         OrderItem item21 = new OrderItem( order2,
                                           1 );
         order2.addItem( item21 );

         ksession.insert( order1 );
         ksession.insert( order2 );
         ksession.insert( item11 );
         ksession.insert( item21 );

         int rules = ksession.fireAllRules();
         assertEquals( 2,
                       rules );
     }

     @Test
     public void testSoundsLike() {
         // JBRULES-2991: Operator soundslike is broken

         String str = "package org.drools.compiler\n" +
                      "rule SoundsLike\n" +
                      "when\n" +
                      "    Person( name soundslike \"Bob\" )\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( new Person( "Bob" ) );
         ksession.insert( new Person( "Mark" ) );

         int rules = ksession.fireAllRules();
         assertEquals( 1,
                       rules );
     }

     @Test
     public void testAgendaFilter1() {
         String str = "package org.drools.compiler\n" +
                      "rule Aaa when then end\n" +
                      "rule Bbb when then end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         org.kie.api.event.rule.AgendaEventListener ael = mock( org.kie.api.event.rule.AgendaEventListener.class );
         ksession.addEventListener( ael );

         RuleNameStartsWithAgendaFilter af = new RuleNameStartsWithAgendaFilter( "B" );

         int rules = ksession.fireAllRules( af );
         assertEquals( 1,
                       rules );

         ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> arg = ArgumentCaptor.forClass( org.kie.api.event.rule.AfterMatchFiredEvent.class );
         verify( ael ).afterMatchFired(arg.capture());
         assertThat( arg.getValue().getMatch().getRule().getName(),
                     is( "Bbb" ) );
     }

     @Test
     public void testAgendaFilter2() {
         String str = "package org.drools.compiler\n" +
                      "rule Aaa when then end\n" +
                      "rule Bbb when then end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         org.kie.api.event.rule.AgendaEventListener ael = mock( org.kie.api.event.rule.AgendaEventListener.class );
         ksession.addEventListener( ael );

         RuleNameEndsWithAgendaFilter af = new RuleNameEndsWithAgendaFilter( "a" );

         int rules = ksession.fireAllRules( af );
         assertEquals( 1,
                       rules );

         ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> arg = ArgumentCaptor.forClass( org.kie.api.event.rule.AfterMatchFiredEvent.class );
         verify( ael ).afterMatchFired(arg.capture());
         assertThat( arg.getValue().getMatch().getRule().getName(),
                     is( "Aaa" ) );
     }

     @Test
     public void testAgendaFilter3() {
         String str = "package org.drools.compiler\n" +
                      "rule Aaa when then end\n" +
                      "rule Bbb when then end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         org.kie.api.event.rule.AgendaEventListener ael = mock( org.kie.api.event.rule.AgendaEventListener.class );
         ksession.addEventListener( ael );

         RuleNameMatchesAgendaFilter af = new RuleNameMatchesAgendaFilter( ".*b." );

         int rules = ksession.fireAllRules( af );
         assertEquals( 1,
                       rules );

         ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> arg = ArgumentCaptor.forClass( org.kie.api.event.rule.AfterMatchFiredEvent.class );
         verify( ael ).afterMatchFired(arg.capture());
         assertThat( arg.getValue().getMatch().getRule().getName(),
                     is( "Bbb" ) );
     }

     @Test
     public void testAgendaFilter4() {
         String str = "package org.drools.compiler\n" +
                      "rule Aaa when then end\n" +
                      "rule Bbb when then end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         org.kie.api.event.rule.AgendaEventListener ael = mock( org.kie.api.event.rule.AgendaEventListener.class );
         ksession.addEventListener( ael );

         RuleNameEqualsAgendaFilter af = new RuleNameEqualsAgendaFilter( "Aaa" );

         int rules = ksession.fireAllRules( af );
         assertEquals( 1,
                       rules );

         ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> arg = ArgumentCaptor.forClass( org.kie.api.event.rule.AfterMatchFiredEvent.class );
         verify( ael ).afterMatchFired(arg.capture());
         assertThat( arg.getValue().getMatch().getRule().getName(),
                     is( "Aaa" ) );
     }

     @Test
     public void testRestrictionsWithOr() {
         // JBRULES-2203: NullPointerException When Using Conditional Element "or" in LHS Together with a Return Value Restriction

         String str = "package org.drools.compiler\n" +
                      "rule \"test\"\n" +
                      "when\n" +
                      "    Cheese( price == (1 + 1) );\n" +
                      "    (or eval(true);\n" +
                      "        eval(true);\n" +
                      "    )\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( new Cheese( "Stilton",
                                      2 ) );

         int rules = ksession.fireAllRules();
         assertEquals( 2,
                       rules );
     }

     @Test
     public void testMapModel() {
         String str = "package org.drools.compiler\n" +
                      "import java.util.Map\n" +
                      "rule \"test\"\n" +
                      "when\n" +
                      "    Map( type == \"Person\", name == \"Bob\" );\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Map<String, String> mark = new HashMap<String, String>();
         mark.put( "type",
                   "Person" );
         mark.put( "name",
                   "Mark" );

         ksession.insert( mark );

         int rules = ksession.fireAllRules();
         assertEquals( 0,
                       rules );

         Map<String, String> bob = new HashMap<String, String>();
         bob.put( "type",
                  "Person" );
         bob.put( "name",
                  "Bob" );

         ksession.insert( bob );

         rules = ksession.fireAllRules();
         assertEquals( 1,
                       rules );

     }

     @Test
     public void testConstraintExpression() {
         String str = "package org.drools.compiler\n" +
                      "rule \"test\"\n" +
                      "when\n" +
                      "    Person( 5*2 > 3 );\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( new Person( "Bob" ) );

         int rules = ksession.fireAllRules();
         assertEquals( 1,
                       rules );
     }

     @Test
     public void testMethodConstraint() {
         String str = "package org.drools.compiler\n" +
                      "rule \"test\"\n" +
                      "when\n" +
                      "    Person( isAlive() );\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Person person = new Person( "Bob" );
         person.setAlive( true );
         ksession.insert( person );

         int rules = ksession.fireAllRules();
         assertEquals( 1,
                       rules );
     }

     @Test
     public void testComplexOperator() {
         String str = "package org.drools.compiler\n" +
                      "rule \"test in\"\n" +
                      "when\n" +
                      "    Person( $name : name in (\"bob\", \"mark\") )\n" +
                      "then\n" +
                      "    boolean test = $name != null;" +
                      "end\n" +
                      "rule \"test not in\"\n" +
                      "when\n" +
                      "    Person( $name : name not in (\"joe\", \"doe\") )\n" +
                      "then\n" +
                      "    boolean test = $name != null;" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Person person = new Person( "bob" );
         ksession.insert( person );

         int rules = ksession.fireAllRules();
         assertEquals( 2,
                       rules );
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

     public void testFreeFormExpressions() {
         String str = "package org.drools.compiler\n" +
                      "rule r1\n" +
                      "when\n" +
                      "    $p1 : Person( age > 2*10, 10 < age )\n" +
                      "    $p2 : Person( age > 2*$p1.age )\n" +
                      "then\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Person bob = new Person( "bob",
                                  30 );
         Person mark = new Person( "mark",
                                   61 );
         ksession.insert( bob );
         ksession.insert( mark );

         int rules = ksession.fireAllRules();
         assertEquals( 1,
                       rules );
     }

     @Test
     public void testFreeFormExpressions2() {
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
     public void testJBRULES_2995() {
         String str = "package org.drools.compiler\n" +
                      "rule r1\n" +
                      "when\n" +
                      "    Primitives( classAttr == java.lang.String.class, \n" +
                      "                eval(classAttr.equals( java.lang.String.class ) ),\n" +
                      "                classAttr == String.class )\n" +
                      "then\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Primitives primitives = new Primitives();
         primitives.setClassAttr( String.class );
         ksession.insert( primitives );
         int rules = ksession.fireAllRules();
         assertEquals( 1,
                       rules );
     }

     @Test
     public void testJBRULES2872() {
         String str = "package org.drools.compiler.test\n" +
                      "import org.drools.compiler.FactA\n" +
                      "rule X\n" +
                      "when\n" +
                      "    FactA( enumVal == TestEnum.ONE || == TestEnum.TWO )\n" +
                      "then\n" +
                      "end\n";
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                       ResourceType.DRL );

         assertTrue( kbuilder.hasErrors() );
         KnowledgeBuilderErrors errors = kbuilder.getErrors();
         logger.info( errors.toString() );
         assertEquals( 1,
                       errors.size() );
         KnowledgeBuilderError error = errors.iterator().next();
         assertEquals( 5,
                       error.getLines()[0] );
     }

     @Test
     public void testJBRULES3030() {
         String str = "package org.drools.compiler\n" +
                      "rule X\n" +
                      "when\n" +
                      "    $gp : GrandParent()" +
                      "    $ch : ChildHolder( child == $gp )\n" +
                      "then\n" +
                      "end\n";
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                       ResourceType.DRL );

         assertFalse( kbuilder.hasErrors() );
     }

     @Test
     public void testJBRULES3111() {
         String str = "package org.drools.compiler\n" +
                      "declare Bool123\n" +
                      "    bool1 : boolean\n" +
                      "    bool2 : boolean\n" +
                      "    bool3 : boolean\n" +
                      "end\n" +
                      "declare Thing\n" +
                      "    name : String\n" +
                      "    bool123 : Bool123\n" +
                      "end\n" +
                      "rule kickOff\n" +
                      "when\n" +
                      "then\n" +
                      "    insert( new Thing( \"one\", new Bool123( true, false, false ) ) );\n" +
                      "    insert( new Thing( \"two\", new Bool123( false, false, false ) ) );\n" +
                      "    insert( new Thing( \"three\", new Bool123( false, false, false ) ) );\n" +
                      "end\n" +
                      "rule r1\n" +
                      "when\n" +
                      "    $t: Thing( bool123.bool1 == true )\n" +
                      "then\n" +
                      "end\n" +
                      "rule r2\n" +
                      "when\n" +
                      "    $t: Thing( bool123.bool2 == true )\n" +
                      "then\n" +
                      "end\n" +
                      "rule r3\n" +
                      "when\n" +
                      "    $t: Thing( bool123.bool3 == true )\n" +
                      "then\n" +
                      "end";
         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         org.kie.api.event.rule.AgendaEventListener ael = mock( org.kie.api.event.rule.AgendaEventListener.class );
         ksession.addEventListener( ael );

         int rulesFired = ksession.fireAllRules();
         assertEquals( 2,
                       rulesFired );

         ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> captor = ArgumentCaptor.forClass( org.kie.api.event.rule.AfterMatchFiredEvent.class );
         verify( ael,
                 times( 2 ) ).afterMatchFired(captor.capture());
         List<org.kie.api.event.rule.AfterMatchFiredEvent> aafe = captor.getAllValues();

         Assert.assertThat( aafe.get( 0 ).getMatch().getRule().getName(),
                            is( "kickOff" ) );
         Assert.assertThat( aafe.get( 1 ).getMatch().getRule().getName(),
                            is( "r1" ) );

         Object value = aafe.get( 1 ).getMatch().getDeclarationValue( "$t" );
         String name = (String) MVEL.eval( "$t.name",
                                           Collections.singletonMap( "$t",
                                                                     value ) );

         Assert.assertThat( name,
                            is( "one" ) );

     }

     @Test
     public void testBigLiterals() {
         String str = "package org.drools.compiler\n" +
                      "rule X\n" +
                      "when\n" +
                      "    Primitives( bigInteger == 10I, bigInteger < (50I), bigDecimal == 10B, bigDecimal < (50B) )\n" +
                      "then\n" +
                      "end\n";
         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         Primitives p = new Primitives();
         p.setBigDecimal( BigDecimal.valueOf( 10 ) );
         p.setBigInteger( BigInteger.valueOf( 10 ) );
         ksession.insert( p );

         int rulesFired = ksession.fireAllRules();
         assertEquals( 1,
                       rulesFired );
     }

     @Test
     public void testNonBooleanConstraint() {
         String str = "package org.drools.compiler\n" +
                      "import java.util.List\n" +
                      "rule \"test\"\n" +
                      "when\n" +
                      "    $p1: Person( name + name )\n" +
                      "then\n" +
                      "end";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                       ResourceType.DRL );

         assertTrue( kbuilder.hasErrors() );
         logger.info( kbuilder.getErrors().toString() );
     }

     @Test
     public void testModifyJava() {
         String str = "package org.drools.compiler\n" +
                      "import java.util.List\n" +
                      "rule \"test\"\n" +
                      "when\n" +
                      "    $l : List() from collect ( Person( alive == false ) );\n" +
                      "then\n" +
                      "    for(Object p : $l ) {\n" +
                      "        Person p2 = (Person) p;\n" +
                      "        modify(p2) { setAlive(true) }\n" +
                      "    }\n" +
                      "end";
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                       ResourceType.DRL );

         Assert.assertFalse( kbuilder.getErrors().toString(),
                             kbuilder.hasErrors() );
     }

     @Test
     public void testModifyMVEL() {
         String str = "package org.drools.compiler\n" +
                      "import java.util.List\n" +
                      "rule \"test\"\n" +
                      "    dialect \"mvel\"\n" +
                      "when\n" +
                      "    $l : List() from collect ( Person( alive == false ) );\n" +
                      "then\n" +
                      "    for(Object p : $l ) {\n" +
                      "        Person p2 = (Person) p;\n" +
                      "        modify(p2) { setAlive(true) }\n" +
                      "    }\n" +
                      "end";
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                       ResourceType.DRL );

         Assert.assertFalse( kbuilder.getErrors().toString(),
                             kbuilder.hasErrors() );
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
     public void testGUVNOR578_2() throws Exception {
         MapBackedClassLoader loader = new MapBackedClassLoader( this.getClass().getClassLoader() );

         JarInputStream jis = new JarInputStream( this.getClass().getResourceAsStream( "/primespoc.jar" ) );

         JarEntry entry = null;
         byte[] buf = new byte[1024];
         int len = 0;
         while ( (entry = jis.getNextJarEntry()) != null ) {
             if ( !entry.isDirectory() ) {
                 ByteArrayOutputStream out = new ByteArrayOutputStream();
                 while ( (len = jis.read( buf )) >= 0 ) {
                     out.write( buf,
                                0,
                                len );
                 }
                 loader.addResource( entry.getName(),
                                     out.toByteArray() );
             }
         }

         List<JarInputStream> jarInputStreams = new ArrayList<JarInputStream>();
         jarInputStreams.add( jis );

         KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, loader);
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);

         String header = "import fr.gouv.agriculture.dag.agorha.business.primes.SousPeriodePrimeAgent\n";

         kbuilder.add(ResourceFactory.newByteArrayResource(header.getBytes()), ResourceType.DRL);
         assertFalse( kbuilder.hasErrors() );

         String passingRule = "rule \"rule1\"\n"
                              + "dialect \"mvel\"\n"
                              + "when\n"
                              + "SousPeriodePrimeAgent( echelle == \"abc\" )"
                              + "then\n"
                              + "end\n";

         String failingRule = "rule \"rule2\"\n"
                              + "dialect \"mvel\"\n"
                              + "when\n"
                              + "SousPeriodePrimeAgent( quotiteRemuneration == 123 , echelle == \"abc\" )"
                              + "then\n"
                              + "end\n";

         kbuilder.add(ResourceFactory.newByteArrayResource(passingRule.getBytes()), ResourceType.DRL);
         assertFalse( kbuilder.hasErrors() );

         kbuilder.add(ResourceFactory.newByteArrayResource(failingRule.getBytes()), ResourceType.DRL);
         assertFalse( kbuilder.hasErrors() );
     }

     @Test
     public void testJBRULES3323() throws Exception {

         //adding rules. it is important to add both since they reciprocate
         StringBuilder rule = new StringBuilder();
         rule.append( "package de.orbitx.accumulatetesettest;\n" );
         rule.append( "import java.util.Set;\n" );
         rule.append( "import java.util.HashSet;\n" );
         rule.append( "import org.drools.compiler.Foo;\n" );
         rule.append( "import org.drools.compiler.Bar;\n" );

         rule.append( "rule \"Sub optimal foo parallelism - this rule is causing NPE upon reverse\"\n" );
         rule.append( "when\n" );
         rule.append( "$foo : Foo($leftId : id, $leftBar : bar != null)\n" );
         rule.append( "$fooSet : Set()\n" );
         rule.append( "from accumulate ( Foo(id > $leftId, bar != null && != $leftBar, $bar : bar),\n" );
         rule.append( "collectSet( $bar ) )\n" );
         rule.append( "then\n" );
         rule.append( "//System.out.println(\"ok\");\n" );
         rule.append( "end\n" );

         //building stuff
         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule.toString() );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         //adding test data
         Bar[] barList = new Bar[3];
         for ( int i = 0; i < barList.length; i++ ) {
             barList[i] = new Bar( String.valueOf( i ) );
         }

         Foo[] fooList = new Foo[4];
         for ( int i = 0; i < fooList.length; i++ ) {
             fooList[i] = new Foo( String.valueOf( i ), i == 3 ? barList[2] : barList[i] );
         }

         for ( Foo foo : fooList ) {
             ksession.insert( foo );
         }

         //the NPE is caused by exactly this sequence. of course there are more sequences but this
         //appears to be the most short one
         int[] magicFoos = new int[]{3, 3, 1, 1, 0, 0, 2, 2, 1, 1, 0, 0, 3, 3, 2, 2, 3, 1, 1};
         int[] magicBars = new int[]{1, 2, 0, 1, 1, 0, 1, 2, 2, 1, 2, 0, 0, 2, 0, 2, 0, 0, 1};

         //upon final rule firing an NPE will be thrown in org.drools.core.rule.Accumulate
         for ( int i = 0; i < magicFoos.length; i++ ) {
             Foo tehFoo = fooList[magicFoos[i]];
             FactHandle fooFactHandle = ksession.getFactHandle( tehFoo );
             tehFoo.setBar( barList[magicBars[i]] );
             ksession.update( fooFactHandle, tehFoo );
             ksession.fireAllRules();
         }
         ksession.dispose();
     }

     @Test
     public void testJBRULES3326() throws Exception {
         StringBuilder rule = new StringBuilder();
         rule.append( "package org.drools.compiler\n" );
         rule.append( "rule X\n" );
         rule.append( "when\n" );
         rule.append( "    Message(!!!false)\n" );
         rule.append( "then\n" );
         rule.append( "end\n" );

         //building stuff
         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule.toString() );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( new Message( "test" ) );
         int rules = ksession.fireAllRules();
         assertEquals( 1, rules );
         ksession.dispose();
     }

     @Test
     public void testDispose() throws Exception {
         StringBuilder rule = new StringBuilder();
         rule.append( "package org.drools.compiler\n" );
         rule.append( "rule X\n" );
         rule.append( "when\n" );
         rule.append( "    Message()\n" );
         rule.append( "then\n" );
         rule.append( "end\n" );

         //building stuff
         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule.toString() );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         ksession.insert( new Message( "test" ) );
         int rules = ksession.fireAllRules();
         assertEquals( 1, rules );

         ksession.dispose();

         try {
             // the following should raise an IllegalStateException as the session was already disposed
             ksession.fireAllRules();
             fail( "An IllegallStateException should have been raised as the session was disposed before the method call." );
         } catch ( IllegalStateException ise ) {
             // success
         }
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

     @Test
     public void testNestedAccessors2() throws Exception {
         String rule = "package org.drools.compiler\n" +
                       "rule 'rule1'" +
                       "    salience 10\n" +
                       "when\n" +
                       "    Cheesery( typedCheeses[0].type == 'stilton' );\n" +
                       "then\n" +
                       "end\n" +
                       "rule 'rule2'\n" +
                       "when\n" +
                       "    Cheesery( typedCheeses[0].price == 10 );\n" +
                       "then\n" +
                       "end";

         //building stuff
         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule.toString() );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
         org.kie.api.event.rule.AgendaEventListener ael = mock( org.kie.api.event.rule.AgendaEventListener.class );
         ksession.addEventListener( ael );

         Cheesery c1 = new Cheesery();
         c1.addCheese( new Cheese( "stilton", 20 ) );
         Cheesery c2 = new Cheesery();
         c2.addCheese( new Cheese( "brie", 10 ) );
         Cheesery c3 = new Cheesery();
         c3.addCheese( new Cheese( "muzzarella", 30 ) );

         ksession.insert( c1 );
         ksession.insert( c2 );
         ksession.insert( c3 );
         ksession.fireAllRules();

         ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> captor = ArgumentCaptor.forClass( org.kie.api.event.rule.AfterMatchFiredEvent.class );
         verify( ael, times( 2 ) ).afterMatchFired(captor.capture());

         List<org.kie.api.event.rule.AfterMatchFiredEvent> values = captor.getAllValues();
         assertThat( (Cheesery) values.get( 0 ).getMatch().getObjects().get( 0 ), is( c1 ) );
         assertThat( (Cheesery) values.get( 1 ).getMatch().getObjects().get( 0 ), is( c2 ) );

         ksession.dispose();
     }

     @Test
     public void testMVELConstraintsWithFloatingPointNumbersInScientificNotation() {

         String rule = "package test; \n" +
                       "dialect \"mvel\"\n" +
                       "global java.util.List list;" +
                       "\n" +
                       "declare Bean \n" +
                       " field : double \n" +
                       "end \n" +
                       "\n" +
                       "rule \"Init\" \n" +
                       "when \n" +
                       "then \n" +
                       "\t insert( new Bean( 1.0E-2 ) ); \n" +
                       "end \n" +
                       "\n" +
                       "rule \"Check\" \n" +
                       "when \n" +
                       "\t Bean( field < 1.0E-1 ) \n" +
                       "then \n" +
                       "\t list.add( \"OK\" ); \n" +
                       "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
         StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();

         List<String> list = new ArrayList<String>();
         kSession.setGlobal( "list", list );

         kSession.fireAllRules();

         assertEquals( 1, list.size() );
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
     public void testMvelDoubleInvocation() throws Exception {
         String rule = "package org.drools.compiler\n" +
                       "import " + MiscTest.class.getName() + ".TestUtility;\n" +
                       "import " + MiscTest.class.getName() + ".TestFact;\n" +
                       "rule \"First Rule\"\n" +
                       "    when\n" +
                       "    $tf : TestFact(TestUtility.utilMethod(s, \"Value1\") == true\n" +
                       "             && i > 0\n" +
                       "    )\n" +
                       "    then\n" +
                       "        System.out.println(\"First Rule Fires\");\n" +
                       "end\n" +
                       "\n" +
                       "rule \"Second Rule\"\n" +
                       "    when\n" +
                       "    $tf : TestFact(TestUtility.utilMethod(s, \"Value2\") == true\n" +
                       "             && i > 0\n" +
                       "    )\n" +
                       "    then\n" +
                       "        System.out.println(\"Second Rule Fires\");\n" +
                       "end\n" +
                       "\n" +
                       "rule \"Third Rule\"\n" +
                       "    when\n" +
                       "    $tf : TestFact(TestUtility.utilMethod(s, \"Value3\") == true\n" +
                       "             && i > 0\n" +
                       "    )\n" +
                       "    then\n" +
                       "        System.out.println(\"Third Rule Fires\");\n" +
                       "end ";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule.toString() );
         StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

         TestFact fact = new TestFact();
         fact.setS( "asdf" );
         fact.setI( 10 );
         ksession.insert( fact );
         ksession.fireAllRules();

         ksession.dispose();
     }

     public static class TestUtility {
         public static Boolean utilMethod(String s1,
                                          String s2) {
             Boolean result = null;

             if ( s1 != null ) {
                 result = s1.equals( s2 );
             }

             logger.info( "in utilMethod >" + s1 + "<  >" + s2 + "< returns " + result );
             return result;
         }
     }

     public static class TestFact {
         private int    i;
         private String s;

         public int getI() {
             return i;
         }

         public void setI(int i) {
             this.i = i;
         }

         public String getS() {
             return s;
         }

         public void setS(String s) {
             this.s = s;
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

     @Test
     public void testPatternMatchingOnThis() throws Exception {
         String rule = "package org.drools.compiler\n" +
                       "rule R1 when\n" +
                       "    $i1: Integer()\n" +
                       "    $i2: Integer( this > $i1 )\n" +
                       "then\n" +
                       "   System.out.println( $i2 + \" > \" + $i1 );\n" +
                       "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.insert( new Integer( 1 ) );
         ksession.insert( new Integer( 2 ) );

         int rules = ksession.fireAllRules();
         assertEquals( 1, rules );
     }

     @Test
     public void testArrayUsage() {
         String str = "import org.drools.compiler.TestParam;\n" +
                      "\n" +
                      "global java.util.List list;\n" +
                      "\n" +
                      "rule \"Intercept\"\n" +
                      "when\n" +
                      "    TestParam( value1 == \"extract\", $args : elements )\n" +
                      "    $s : String( this == $args[$s.length() - $s.length()] )\n" +
                      "    $s1 : String( this == $args[0] )\n" +
                      "    $s2 : String( this == $args[1] )\n" +
                      "    Integer( this == 2 ) from $args.length\n" +
                      "    $s3 : String( this == $args[$args.length - $args.length  + 1] )\n" +
                      "then\n" +
                      "    delete( $s1 );  \n" +
                      "    delete( $s2 );  \n" +
                      "    list.add( $s1 ); \n" +
                      "    list.add( $s2 ); \n" +

                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         java.util.List list = new java.util.ArrayList();
         ksession.setGlobal( "list", list );

         int N = 2;
         for ( int j = 0; j < N; j++ ) {
             TestParam o = new TestParam();
             o.setValue1("extract" );
             o.setElements(new Object[]{"x1_" + j, "x2_" + j});
             ksession.insert( "x1_" + j );
             ksession.insert( "x2_" + j );
             ksession.insert( o );
             ksession.fireAllRules();
         }

         assertEquals( 4, list.size() );
         assertTrue( list.contains( "x1_0"));
         assertTrue( list.contains( "x1_1"));
         assertTrue( list.contains( "x2_0"));
         assertTrue( list.contains( "x2_1"));

         ksession.dispose();
     }

     @Test(timeout = 5000)
     public void testEfficientBetaNodeNetworkUpdate() {
         // [JBRULES-3372]
         String str =
                 "declare SimpleMembership\n" +
                         "    listId : String\n" +
                         "    patientId : String\n" +
                         "end\n" +
                         "declare SimplePatientFact\n" +
                         "    value : int\n" +
                         "    patientId : String\n" +
                         "end\n" +
                         "rule \"A\"\n" +
                         "when\n" +
                         "$slm : SimpleMembership($pid : patientId, listId == \"5072\" )\n" +
                         "and not (\n" +
                         "    (\n" +
                         "        (\n" +
                         "            SimplePatientFact(value == 1, patientId == $pid)\n" +
                         "        ) or (\n" +
                         "            SimplePatientFact(value == 2, patientId == $pid)\n" +
                         "        )\n" +
                         "    ) and (\n" +
                         "        (\n" +
                         "            SimplePatientFact(value == 6, patientId == $pid)\n" +
                         "        ) or (\n" +
                         "            SimplePatientFact(value == 7, patientId == $pid)\n" +
                         "        ) or (\n" +
                         "            SimplePatientFact(value == 8, patientId == $pid)\n" +
                         "        )\n" +
                         "    ) and (\n" +
                         "       (\n" +
                         "           SimplePatientFact(value == 9, patientId == $pid)\n" +
                         "       ) or (\n" +
                         "           SimplePatientFact(value == 10, patientId == $pid)\n" +
                         "       ) or (\n" +
                         "           SimplePatientFact(value == 11, patientId == $pid)\n" +
                         "       ) or (\n" +
                         "           SimplePatientFact(value == 12, patientId == $pid)\n" +
                         "       ) or (\n" +
                         "           SimplePatientFact(value == 13, patientId == $pid)\n" +
                         "       )\n" +
                         "   )\n" +
                         ")\n" +
                         "then\n" +
                         "   System.out.println(\"activated\");\n" +
                         "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
     }

     @Test
     public void testModifyCommand() {
         String str =
                 "rule \"sample rule\"\n" +
                         "   when\n" +
                         "   then\n" +
                         "       System.out.println(\"\\\"Hello world!\\\"\");\n" +
                         "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         Person p1 = new Person( "John", "nobody", 25 );
         ksession.execute( CommandFactory.newInsert( p1 ) );
         FactHandle fh = ksession.getFactHandle( p1 );

         Person p = new Person( "Frank", "nobody", 30 );
         List<Setter> setterList = new ArrayList<Setter>();
         setterList.add( CommandFactory.newSetter( "age", String.valueOf( p.getAge() ) ) );
         setterList.add( CommandFactory.newSetter( "name", p.getName() ) );
         setterList.add( CommandFactory.newSetter( "likes", p.getLikes() ) );

         ksession.execute( CommandFactory.newModify( fh, setterList ) );
     }

     @Test
     public void testMVELTypeCoercion() {
         String str = "package org.drools.compiler.test; \n" +
                      "\n" +
                      "global java.util.List list;" +
                      "\n" +
                      "declare Bean\n" +
                      // NOTICE: THIS WORKS WHEN THE FIELD IS "LIST", BUT USED TO WORK WITH ARRAYLIST TOO
                      "  field : java.util.ArrayList\n" +
                      "end\n" +
                      "\n" +
                      "\n" +
                      "rule \"Init\"\n" +
                      "when  \n" +
                      "then\n" +
                      "  insert( new Bean( new java.util.ArrayList( java.util.Arrays.asList( \"x\" ) ) ) );\n" +
                      "end\n" +
                      "\n" +
                      "rule \"Check\"\n" +
                      "when\n" +
                      "  $b : Bean( $fld : field == [\"x\"] )\n" +
                      "then\n" +
                      "  System.out.println( $fld );\n" +
                      "  list.add( \"OK\" ); \n" +
                      "end";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
         if ( kbuilder.hasErrors() ) {
             fail( kbuilder.getErrors().toString() );
         }
         KieBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
         kbConf.setOption( EqualityBehaviorOption.EQUALITY );
         KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbConf );
         kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         java.util.List list = new java.util.ArrayList();
         ksession.setGlobal( "list", list );

         ksession.fireAllRules();
         assertTrue( list.contains( "OK" ) );

         ksession.dispose();
     }

     @Test
     public void testPatternOnClass() throws Exception {
         String rule = "import org.drools.core.reteoo.InitialFactImpl\n" +
                       "import org.drools.compiler.FactB\n" +
                       "rule \"Clear\" when\n" +
                       "   $f: Object(class != FactB.class)\n" +
                       "then\n" +
                       "   if( ! ($f instanceof InitialFactImpl) ){\n" +
                       "     delete( $f );\n" +
                       "   }\n" +
                       "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.insert( new FactA() );
         ksession.insert( new FactA() );
         ksession.insert( new FactB() );
         ksession.insert( new FactB() );
         ksession.insert( new FactC() );
         ksession.insert( new FactC() );
         ksession.fireAllRules();

         for ( FactHandle fact : ksession.getFactHandles() ) {
             InternalFactHandle internalFact = (InternalFactHandle) fact;
             assertTrue( internalFact.getObject() instanceof FactB );
         }
     }

     @Test
     public void testPatternOffset() throws Exception {
         // JBRULES-3427
         String str = "package org.drools.compiler.test; \n" +
                      "declare A\n" +
                      "end\n" +
                      "declare B\n" +
                      "   field : int\n" +
                      "end\n" +
                      "declare C\n" +
                      "   field : int\n" +
                      "end\n" +
                      "rule R when\n" +
                      "( " +
                      "   A( ) or ( A( ) and B( ) ) " +
                      ") and (\n" +
                      "   A( ) or ( B( $bField : field ) and C( field != $bField ) )\n" +
                      ")\n" +
                      "then\n" +
                      "    System.out.println(\"rule fired\");\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         FactType typeA = kbase.getFactType( "org.drools.compiler.test", "A" );
         FactType typeB = kbase.getFactType( "org.drools.compiler.test", "B" );
         FactType typeC = kbase.getFactType( "org.drools.compiler.test", "C" );

         Object a = typeA.newInstance();
         ksession.insert( a );

         Object b = typeB.newInstance();
         typeB.set( b, "field", 1 );
         ksession.insert( b );

         Object c = typeC.newInstance();
         typeC.set( c, "field", 1 );
         ksession.insert( c );

         ksession.fireAllRules();
     }

     @Test
     public void testCommentDelimiterInString() throws Exception {
         // JBRULES-3401
         String str = "rule x\n" +
                      "dialect \"mvel\"\n" +
                      "when\n" +
                      "then\n" +
                      "System.out.println( \"/*\" );\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
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

     @Test
     public void testRetractLeftTuple() throws Exception {
         // JBRULES-3420
         String str = "import " + MiscTest.class.getName() + ".*\n" +
                      "rule R1 salience 3\n" +
                      "when\n" +
                      "   $b : InterfaceB( )\n" +
                      "   $a : ClassA( b == null )\n" +
                      "then\n" +
                      "   $a.setB( $b );\n" +
                      "   update( $a );\n" +
                      "end\n" +
                      "rule R2 salience 2\n" +
                      "when\n" +
                      "   $b : ClassB( id == \"123\" )\n" +
                      "   $a : ClassA( b != null && b.id == $b.id )\n" +
                      "then\n" +
                      "   $b.setId( \"456\" );\n" +
                      "   update( $b );\n" +
                      "end\n" +
                      "rule R3 salience 1\n" +
                      "when\n" +
                      "   InterfaceA( $b : b )\n" +
                      "then\n" +
                      "   delete( $b );\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.insert( new ClassA() );
         ksession.insert( new ClassB() );
         assertEquals( 3, ksession.fireAllRules() );
     }

     @Test
     public void testVariableBindingWithOR() throws Exception {
         // JBRULES-3390
         String str1 = "package org.drools.compiler.test; \n" +
                       "declare A\n" +
                       "end\n" +
                       "declare B\n" +
                       "   field : int\n" +
                       "end\n" +
                       "declare C\n" +
                       "   field : int\n" +
                       "end\n" +
                       "rule R when\n" +
                       "( " +
                       "   A( ) and ( B( $bField : field ) or C( $cField : field ) ) " +
                       ")\n" +
                       "then\n" +
                       "    System.out.println($bField);\n" +
                       "end\n";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str1.getBytes() ), ResourceType.DRL );
         assertTrue( kbuilder.hasErrors() );

         String str2 = "package org.drools.compiler.test; \n" +
                       "declare A\n" +
                       "end\n" +
                       "declare B\n" +
                       "   field : int\n" +
                       "end\n" +
                       "declare C\n" +
                       "   field : int\n" +
                       "end\n" +
                       "rule R when\n" +
                       "( " +
                       "   A( ) and ( B( $field : field ) or C( $field : field ) ) " +
                       ")\n" +
                       "then\n" +
                       "    System.out.println($field);\n" +
                       "end\n";

         KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder2.add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL );
         assertFalse( kbuilder2.hasErrors() );
     }

     @Test
     public void testModifySimple() {
         String str = "package org.drools.compiler;\n" +
                      "\n" +
                      "rule \"test modify block\"\n" +
                      "when\n" +
                      "    $p: Person( name == \"hungry\" )\n" +
                      "then\n" +
                      "    modify( $p ) { setName(\"fine\") }\n" +
                      "end\n" +
                      "\n" +
                      "rule \"Log\"\n" +
                      "when\n" +
                      "    $o: Object()\n" +
                      "then\n" +
                      "    System.out.println( $o );\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         Person p = new Person();
         p.setName( "hungry" );
         ksession.insert( p );

         ksession.fireAllRules();

         ksession.dispose();
     }

     @Test
     public void testDeclaresWithArrayFields() throws Exception {
         String rule = "package org.drools.compiler.test; \n" +
                       "import " + org.drools.compiler.test.Person.class.getName() + ";\n" +
                       "import " + org.drools.compiler.test.Man.class.getName() + ";\n" +
                       "\n" +
                       "global java.util.List list;" +
                       "\n" +
                       "declare Cheese\n" +
                       "   name : String = \"ched\" \n" +
                       "end \n" +
                       "" +
                       "declare X\n" +
                       "    fld \t: String   = \"xx\"                                      @key \n" +
                       "    achz\t: Cheese[] \n" +
                       "    astr\t: String[] " + " = new String[] {\"x\", \"y11\" } \n" +
                       "    aint\t: int[] \n" +
                       "    sint\t: short[] \n" +
                       "    bint\t: byte[] \n" +
                       "    lint\t: long[] \n" +
                       "    dint\t: double[] \n" +
                       "    fint\t: float[] \n" +
                       "    zint\t: Integer[] " + " = new Integer[] {2,3}                   @key \n" +
                       "    aaaa\t: String[][] \n" +
                       "    bbbb\t: int[][] \n" +
                       "    aprs\t: Person[] " + " = new Person[] { new Man() } \n" +
                       "end\n" +
                       "\n" +
                       "rule \"Init\"\n" +
                       "when\n" +
                       "\n" +
                       "then\n" +
                       "    X x = new X( \"xx\", \n" +
                       "                 new Cheese[0], \n" +
                       "                 new String[] { \"x\", \"y22\" }, \n" +
                       "                 new int[] { 7, 9 }, \n" +
                       "                 new short[] { 3, 4 }, \n" +
                       "                 new byte[] { 1, 2 }, \n" +
                       "                 new long[] { 100L, 200L }, \n" +
                       "                 new double[] { 3.2, 4.4 }, \n" +
                       "                 new float[] { 3.2f, 4.4f }, \n" +
                       "                 new Integer[] { 2, 3 }, \n" +
                       "                 new String[2][3], \n" +
                       "                 new int[5][3], \n" +
                       "                 null \n" +
                       "    ); \n" +
                       "   insert( x );\n" +
                       "   " +
                       "   X x2 = new X(); \n" +
                       "   x2.setAint( new int[2] ); \n " +
                       "   x2.getAint()[0] = 7; \n" +
                       "   insert( x2 );\n" +
                       "   " +
                       "   if ( x.hashCode() == x2.hashCode() ) list.add( \"hash\" );  \n" +
                       "   " +
                       "   if( x.equals( x2 ) ) list.add( \"equals\" );  \n" +
                       "   " +
                       "   list.add( x.getAint(  )[0] );  \n" +
                       "end \n" +
                       "\n" +
                       "rule \"Check\"\n" +
                       "when\n" +
                       "    X( astr.length > 0,            \n" +
                       "       astr[0] == \"x\",           \n" +
                       "       $x : astr[1],               \n" +
                       "       aint[0] == 7  )             \n" +
                       "then\n" +
                       "    list.add( $x );\n" +
                       "end \n" +
                       "";

         System.out.println( rule );

         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
         List list = new ArrayList();
         ksession.setGlobal( "list", list );

         ksession.fireAllRules();

         assertTrue( list.contains( "hash" ) );
         assertTrue( list.contains( "equals" ) );
         assertTrue( list.contains( 7 ) );
         assertTrue( list.contains( "y11" ) );
         assertTrue( list.contains( "y22" ) );

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
     public void testBindingToNullFieldWithEquality() {
         // JBRULES-3396
         String str = "package org.drools.compiler.test; \n" +
                      "\n" +
                      "global java.util.List list;" +
                      "\n" +
                      "declare Bean\n" +
                      "  id    : String @key\n" +
                      "  field : String\n" +
                      "end\n" +
                      "\n" +
                      "\n" +
                      "rule \"Init\"\n" +
                      "when  \n" +
                      "then\n" +
                      "  insert( new Bean( \"x\" ) );\n" +
                      "end\n" +
                      "\n" +
                      "rule \"Check\"\n" +
                      "when\n" +
                      "  $b : Bean( $fld : field )\n" +
                      "then\n" +
                      "  System.out.println( $fld );\n" +
                      "  list.add( \"OK\" ); \n" +
                      "end";

         KieBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
         kbConf.setOption( EqualityBehaviorOption.EQUALITY );

         KnowledgeBase kbase = loadKnowledgeBaseFromString( kbConf, str );
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
     public void testVarargConstraint() throws Exception {
         // JBRULES-3268
         String str = "package org.drools.compiler.test;\n" +
                      "import " + MiscTest.class.getName() + ".VarargBean;\n" +
                      " global java.util.List list;\n" +
                      "\n" +
                      "rule R1 when\n" +
                      "   VarargBean( isOddArgsNr(1, 2, 3) )\n" +
                      "then\n" +
                      "   list.add(\"odd\");\n" +
                      "end\n" +
                      "rule R2 when\n" +
                      "   VarargBean( isOddArgsNr(1, 2, 3, 4) )\n" +
                      "then\n" +
                      "   list.add(\"even\");\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         List list = new ArrayList();
         ksession.setGlobal( "list", list );

         ksession.insert( new VarargBean() );
         ksession.fireAllRules();
         assertEquals( 1, list.size() );
         assertTrue( list.contains( "odd" ) );
     }

     public static class VarargBean {
         public boolean isOddArgsNr(int... args) {
             return args.length % 2 == 1;
         }
     }

     @Test
     public void testPackageImportWithMvelDialect() throws Exception {
         // JBRULES-2244
         String str = "package org.drools.compiler.test;\n" +
                      "import org.drools.compiler.*\n" +
                      "dialect \"mvel\"\n" +
                      "rule R1 no-loop when\n" +
                      "   $p : Person( )" +
                      "   $c : Cheese( )" +
                      "then\n" +
                      "   modify($p) { setCheese($c) };\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         Person p = new Person( "Mario", 38 );
         ksession.insert( p );
         Cheese c = new Cheese( "Gorgonzola" );
         ksession.insert( c );

         assertEquals( 1, ksession.fireAllRules() );
         assertSame( c, p.getCheese() );
     }

     @Test
     public void testNoMvelSyntaxInFunctions() throws Exception {
         // JBRULES-3433
         String str = "import java.util.*;\n" +
                      "dialect \"mvel\"\n" +
                      "function Integer englishToInt(String englishNumber) { \n" +
                      "   Map m = [\"one\":1, \"two\":2, \"three\":3, \"four\":4, \"five\":5]; \n" +
                      "   Object obj = m.get(englishNumber.toLowerCase()); \n" +
                      "   return Integer.parseInt(obj.toString()); \n" +
                      "}\n";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

         assertTrue( kbuilder.hasErrors() );
     }

     @Test
     public void testMissingClosingBraceOnModify() throws Exception {
         // JBRULES-3436
         String str = "package org.drools.compiler.test;\n" +
                      "import org.drools.compiler.*\n" +
                      "rule R1 when\n" +
                      "   $p : Person( )" +
                      "   $c : Cheese( )" +
                      "then\n" +
                      "   modify($p) { setCheese($c) ;\n" +
                      "end\n";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );

         assertTrue( kbuilder.hasErrors() );
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
     public void testDeclaredTypeAsFieldForAnotherDeclaredType() {
         // JBRULES-3468
         String str = "package com.sample\n" +
                      "\n" +
                      "import com.sample.*;\n" +
                      "\n" +
                      "declare Item\n" +
                      "        id : int;\n" +
                      "end\n" +
                      "\n" +
                      "declare Priority\n" +
                      "        name : String;\n" +
                      "        priority : int;\n" +
                      "end\n" +
                      "\n" +
                      "declare Cap\n" +
                      "        item : Item;\n" +
                      "        name : String\n" +
                      "end\n" +
                      "\n" +
                      "rule \"split cart into items\"\n" +
                      "when\n" +
                      "then\n" +
                      "        insert(new Item(1));\n" +
                      "        insert(new Item(2));\n" +
                      "        insert(new Item(3));\n" +
                      "end\n" +
                      "\n" +
                      "rule \"Priorities\"\n" +
                      "when\n" +
                      "then\n" +
                      "        insert(new Priority(\"A\", 3));\n" +
                      "        insert(new Priority(\"B\", 2));\n" +
                      "        insert(new Priority(\"C\", 5));\n" +
                      "end\n" +
                      "\n" +
                      "rule \"Caps\"\n" +
                      "when\n" +
                      "        $i : Item()\n" +
                      "        $p : Priority($name : name)\n" +
                      "then\n" +
                      "        insert(new Cap($i, $name));\n" +
                      "end\n" +
                      "\n" +
                      "rule \"test\"\n" +
                      "when\n" +
                      "        $i : Item()\n" +
                      "        Cap(item.id == $i.id)\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         assertEquals( 20, ksession.fireAllRules() );
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

     @Test
     public void testDeclaredTypesDefaultHashCode() {
         // JBRULES-3481
         String str = "package com.sample\n" +
                      "\n" +
                      "global java.util.List list; \n" +
                      "" +
                      "declare Bean\n" +
                      " id : int \n" +
                      "end\n" +
                      "\n" +
                      "declare KeyedBean\n" +
                      " id : int @key \n" +
                      "end\n" +
                      "\n" +
                      "\n" +
                      "rule Create\n" +
                      "when\n" +
                      "then\n" +
                      " list.add( new Bean(1) ); \n" +
                      " list.add( new Bean(2) ); \n" +
                      " list.add( new KeyedBean(1) ); \n" +
                      " list.add( new KeyedBean(1) ); \n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         List list = new ArrayList();

         ksession.setGlobal( "list", list );
         ksession.fireAllRules();

         ksession.dispose();

         assertFalse( list.get( 0 ).hashCode() == 34 );
         assertFalse( list.get( 1 ).hashCode() == 34 );
         assertFalse( list.get( 0 ).hashCode() == list.get( 1 ).hashCode() );
         assertNotSame( list.get( 0 ), list.get( 1 ) );
         assertFalse( list.get( 0 ).equals( list.get( 1 ) ) );

         assertTrue( list.get( 2 ).hashCode() == 32 );
         assertTrue( list.get( 3 ).hashCode() == 32 );
         assertNotSame( list.get( 2 ), list.get( 3 ) );
         assertTrue( list.get( 2 ).equals( list.get( 3 ) ) );

     }

     @Test
     public void testJittingConstraintWithInvocationOnLiteral() {
         String str = "package com.sample\n" +
                      "import org.drools.compiler.Person\n" +
                      "rule XXX when\n" +
                      "  Person( name.toString().toLowerCase().contains( \"mark\".toString().toLowerCase() ) )\n" +
                      "then\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.insert( new Person( "mark", 37 ) );
         ksession.insert( new Person( "mario", 38 ) );

         ksession.fireAllRules();
         ksession.dispose();
     }

     @Test
     public void testJittingMethodWithCharSequenceArg() {
         String str = "package com.sample\n" +
                      "import org.drools.compiler.Person\n" +
                      "rule XXX when\n" +
                      "  Person( $n : name, $n.contains( \"mark\" ) )\n" +
                      "then\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.insert( new Person( "mark", 37 ) );
         ksession.insert( new Person( "mario", 38 ) );

         ksession.fireAllRules();
         ksession.dispose();
     }

     @Test
     public void testMapAccessorWithPrimitiveKey() {
         String str = "package com.sample\n" +
                      "import " + MiscTest.class.getName() + ".MapContainerBean\n" +
                      "rule R1 when\n" +
                      "  MapContainerBean( map[1] == \"one\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule R2 when\n" +
                      "  MapContainerBean( map[1+1] == \"two\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule R3 when\n" +
                      "  MapContainerBean( map[this.get3()] == \"three\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule R4 when\n" +
                      "  MapContainerBean( map[4] == null )\n" +
                      "then\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.insert( new MapContainerBean() );
         assertEquals( 4, ksession.fireAllRules() );
         ksession.dispose();
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
     public void testFromWithStrictModeOff() {
         // JBRULES-3533
         String str =
                 "import java.util.Map;\n" +
                         "dialect \"mvel\"\n" +
                         "rule \"LowerCaseFrom\"\n" +
                         "when\n" +
                         "   Map($valOne : this['keyOne'] !=null)\n" +
                         "   $lowerValue : String() from $valOne.toLowerCase()\n" +
                         "then\n" +
                         "   System.out.println( $valOne.toLowerCase() );\n" +
                         "end\n";

         KnowledgeBuilderConfigurationImpl pkgBuilderCfg = new KnowledgeBuilderConfigurationImpl();
         MVELDialectConfiguration mvelConf = (MVELDialectConfiguration) pkgBuilderCfg.getDialectConfiguration( "mvel" );
         mvelConf.setStrict( false );
         mvelConf.setLangLevel( 5 );

         KnowledgeBase kbase = loadKnowledgeBaseFromString( pkgBuilderCfg, str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         Map<String, String> testMap = new HashMap<String, String>();
         testMap.put( "keyOne", "valone" );
         testMap.put( "valTwo", "valTwo" );
         ksession.insert( testMap );
         assertEquals( 1, ksession.fireAllRules() );
         ksession.dispose();
     }

     @Test
     public void testFromWithStrictModeOn() {
         // JBRULES-3533
         String str =
                 "import java.util.Map;\n" +
                         "dialect \"mvel\"\n" +
                         "rule \"LowerCaseFrom\"\n" +
                         "when\n" +
                         "   Map($valOne : this['keyOne'] !=null)\n" +
                         "   $lowerValue : String() from $valOne.toLowerCase()\n" +
                         "then\n" +
                         "   System.out.println( $valOne.toLowerCase() );\n" +
                         "end\n";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
         assertTrue( kbuilder.hasErrors() );
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
     public void testArithmeticExpressionWithNull() {
         // JBRULES-3568
         String str = "import " + MiscTest.class.getName() + ".PrimitiveBean;\n" +
                      "rule R when\n" +
                      "   PrimitiveBean(primitive/typed > 0.7)\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.insert( new PrimitiveBean( 0.9, 1.1 ) );
         ksession.insert( new PrimitiveBean( 0.9, null ) );
         assertEquals( 1, ksession.fireAllRules() );
         ksession.dispose();
     }

     public static class PrimitiveBean {
         public final double primitive;
         public final Double typed;

         public PrimitiveBean(double primitive,
                              Double typed) {
             this.primitive = primitive;
             this.typed = typed;
         }

         public double getPrimitive() {
             return primitive;
         }

         public Double getTyped() {
             return typed;
         }
     }

     public void testMvelMatches() {
         String str = "package com.sample\n" +
                      "import org.drools.compiler.Person\n" +
                      "global java.util.List results;" +
                      "rule XXX when\n" +
                      "  Person( $n : name ~= \"\\\\D.*\" )\n" +
                      "then\n" +
                      "  results.add( $n ); \n " +
                      "end \n" +
                      "rule YY when\n" +
                      "  Person( $a : age, $n : name ~= \"\\\\d\\\\D.*\" )\n" +
                      "then\n" +
                      "  results.add( $a ); \n " +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         List res = new ArrayList();
         ksession.setGlobal( "results", res );

         ksession.insert( new Person( "mark", 37 ) );
         ksession.insert( new Person( "mario", 38 ) );
         ksession.insert( new Person( "1mike", 44 ) );
         ksession.insert( new Person( "52matt", 44 ) );

         ksession.fireAllRules();
         ksession.dispose();

         assertEquals( 3, res.size() );
         assertTrue( res.contains( "mark" ) );
         assertTrue( res.contains( "mario" ) );
         assertTrue( res.contains( 44 ) );
     }

     @Test
     public void testRuleFlowGroupWithLockOnActivate() {
         // JBRULES-3590
         String str = "import org.drools.compiler.Person;\n" +
                      "import org.drools.compiler.Cheese;\n" +
                      "rule R1\n" +
                      "ruleflow-group \"group1\"\n" +
                      "lock-on-active true\n" +
                      "when\n" +
                      "   $p : Person()\n" +
                      "then\n" +
                      "   $p.setName(\"John\");\n" +
                      "   update ($p);\n" +
                      "end\n" +
                      "rule R2\n" +
                      "ruleflow-group \"group1\"\n" +
                      "lock-on-active true\n" +
                      "when\n" +
                      "   $p : Person( name == null )\n" +
                      "   forall ( Cheese ( type == \"cheddar\" ))\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.insert( new Person() );
         ksession.insert( new Cheese( "gorgonzola" ) );
         ((InternalAgenda) ksession.getAgenda()).activateRuleFlowGroup( "group1" );
         assertEquals( 1, ksession.fireAllRules() );
         ksession.dispose();
     }

     @Test
     public void testInstanceof() throws Exception {
         // JBRULES-3591
         String str = "import org.drools.compiler.*;\n" +
                      "rule R1 when\n" +
                      "   Person( address instanceof LongAddress )\n" +
                      "then\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         Person mark = new Person( "mark" );
         mark.setAddress( new LongAddress( "uk" ) );
         ksession.insert( mark );

         assertEquals( 1, ksession.fireAllRules() );
         ksession.dispose();
     }

     @Test
     public void testFromNodeWithMultipleBetas() throws Exception {
         String str = "import org.drools.compiler.*;\n" +
                      "rule R1 when\n" +
                      "   $p : Person( $name : name, $addresses : addresses )\n" +
                      "   $c : Cheese( $type: type == $name )\n" +
                      "   $a : Address( street == $type, suburb == $name ) from $addresses\n" +
                      "then\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         Person p = new Person( "x" );
         p.addAddress( new Address( "x", "x", "x" ) );
         p.addAddress( new Address( "y", "y", "y" ) );
         ksession.insert( p );

         ksession.insert( new Cheese( "x" ) );
         ksession.fireAllRules();
         ksession.dispose();
     }

     @Test
     public void testMvelFunctionWithDeclaredTypeArg() {
         // JBRULES-3562
         String rule = "package test; \n" +
                       "dialect \"mvel\"\n" +
                       "global java.lang.StringBuilder value;\n" +
                       "function String getFieldValue(Bean bean) {" +
                       "   return bean.getField();" +
                       "}" +
                       "declare Bean \n" +
                       "   field : String \n" +
                       "end \n" +
                       "\n" +
                       "rule R1 \n" +
                       "when \n" +
                       "then \n" +
                       "   insert( new Bean( \"mario\" ) ); \n" +
                       "end \n" +
                       "\n" +
                       "rule R2 \n" +
                       "when \n" +
                       "   $bean : Bean( ) \n" +
                       "then \n" +
                       "   value.append( getFieldValue($bean) ); \n" +
                       "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         StringBuilder sb = new StringBuilder();
         ksession.setGlobal( "value", sb );
         ksession.fireAllRules();

         assertEquals( "mario", sb.toString() );
         ksession.dispose();
     }

     @Test
     public void testMvelFunctionWithDeclaredTypeArgForGuvnor() throws Exception {
         // JBRULES-3562
         String function = "function String getFieldValue(Bean bean) {" +
                           " return bean.getField();" +
                           "}\n";
         String declaredFactType = "declare Bean \n" +
                                   " field : String \n" +
                                   "end \n";
         String rule = "rule R2 \n" +
                       "dialect 'mvel'\n" +
                       "when \n" +
                       " $bean : Bean( ) \n" +
                       "then \n" +
                       " System.out.println( getFieldValue($bean) ); \n" +
                       "end\n";

         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
         kbuilder.add(ResourceFactory.newByteArrayResource(declaredFactType.getBytes()), ResourceType.DRL);
         kbuilder.add(ResourceFactory.newByteArrayResource(function.getBytes()), ResourceType.DRL);
         kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL);

         for ( KnowledgeBuilderError error : kbuilder.getErrors() ) {
             System.out.println( "ERROR:" );
             System.out.println( error.getMessage() );
         }
         assertFalse( kbuilder.hasErrors() );
     }

     public void testGenericsList() throws Exception {
         String str = "import org.drools.compiler.*;\n" +
                      "rule R1 when\n" +
                      "   $c : Cheese( $type: type )\n" +
                      "   $p : Person( $name : name, addresses.get(0).street == $type )\n" +
                      "then\n" +
                      "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         Person p = new Person( "x" );
         p.addAddress( new Address( "x", "x", "x" ) );
         p.addAddress( new Address( "y", "y", "y" ) );
         ksession.insert( p );

         ksession.insert( new Cheese( "x" ) );
         assertEquals( 1, ksession.fireAllRules() );
         ksession.dispose();
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
     public void testRHSClone() {
         // JBRULES-3539
         String str = "import java.util.Map;\n" +
                      "dialect \"mvel\"\n" +
                      "rule \"RHSClone\"\n" +
                      "when\n" +
                      "   Map($valOne : this['keyOne'] !=null)\n" +
                      "then\n" +
                      "   System.out.println( $valOne.clone() );\n" +
                      "end\n";

         KnowledgeBuilderConfigurationImpl pkgBuilderCfg = new KnowledgeBuilderConfigurationImpl();
         MVELDialectConfiguration mvelConf = (MVELDialectConfiguration) pkgBuilderCfg.getDialectConfiguration( "mvel" );
         mvelConf.setStrict( false );
         mvelConf.setLangLevel( 5 );
         KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( pkgBuilderCfg );
         kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
         KnowledgeBuilderErrors errors = kbuilder.getErrors();
         if ( errors.size() > 0 ) {
             for ( KnowledgeBuilderError error : errors ) {
                 System.err.println( error );
             }
             fail( "Could not parse knowledge" );
         }
     }

     @Test
     public void testDeclaredTypeWithHundredsProps() {
         // JBRULES-3621
         StringBuilder sb = new StringBuilder( "declare MyType\n" );
         for ( int i = 0; i < 300; i++ ) {
             sb.append( "i" + i + " : int\n" );
         }
         sb.append( "end" );

         KnowledgeBase kbase = loadKnowledgeBaseFromString( sb.toString() );
     }

     @Test
     public void testAddRuleWithFrom() {
         // JBRULES-3499
         String str1 = "global java.util.List names;\n" +
                       "global java.util.List list;\n";

         String str2 = "import org.drools.compiler.*;\n" +
                       "global java.util.List names;\n" +
                       "global java.util.List list;\n" +
                       "rule R1 when\n" +
                       "   $p : Person( )\n" +
                       "   String( this == $p.name ) from names\n" +
                       "then\n" +
                       " list.add( $p );\n" +
                       "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString(str1);
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         List<String> names = new ArrayList<String>();
         names.add("Mark");
         ksession.setGlobal( "names", names );

         List<String> list = new ArrayList<String>();
         ksession.setGlobal("list", list);

         Person p = new Person( "Mark" );
         ksession.insert( p );

         ksession.fireAllRules();

         kbase.addKnowledgePackages( loadKnowledgePackagesFromString(str2) );

         ksession.fireAllRules();

         assertEquals(1, list.size());
         assertSame(p, list.get(0));
         ksession.dispose();
     }

     @Test
     public void testConstantLeft() {
         // JBRULES-3627
         String str = "import org.drools.compiler.*;\n" +
                      "rule R1 when\n" +
                      "   $p : Person( \"Mark\" == name )\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.insert( new Person( null ) );
         ksession.insert( new Person( "Mark" ) );

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
         verify( ael, never() ).matchCancelled(any(org.kie.api.event.rule.MatchCancelledEvent.class));
     }

     @Test
     public void testNullConstantLeft() {
         // JBRULES-3627
         String str = "import org.drools.compiler.*;\n" +
                      "rule R1 when\n" +
                      "   $p : Person( null == name )\n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.insert( new Person( null ) );
         ksession.insert( new Person( "Mark" ) );

         assertEquals( 1, ksession.fireAllRules() );
         ksession.dispose();
     }

     @Test
     public void testJitConstraintInvokingConstructor() {
         // JBRULES-3628
         String str = "import org.drools.compiler.Person;\n" +
                      "rule R1 when\n" +
                      "   Person( new Integer( ageAsInteger ) < 40 ) \n" +
                      "then\n" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.insert( new Person( "Mario", 38 ) );

         assertEquals( 1, ksession.fireAllRules() );
         ksession.dispose();
     }

     @Test
     public void testRemoveRuleWithFromNode() throws Exception {
         // JBRULES-3631
         String str =
                 "package org.drools.compiler;\n" +
                 "import org.drools.compiler.*;\n" +
                 "rule R1 when\n" +
                 "   not( Person( name == \"Mark\" ));\n" +
                 "then\n" +
                 "end\n" +
                 "rule R2 when\n" +
                 "   $p: Person( name == \"Mark\" );\n" +
                 "   not( Address() from $p.getAddresses() );\n" +
                 "then\n" +
                 "end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
         assertEquals(2, kbase.getKnowledgePackage("org.drools.compiler").getRules().size());
         kbase.removeRule( "org.drools.compiler", "R2");

         assertEquals( 1,  kbase.getKnowledgePackage( "org.drools.compiler" ).getRules().size() );
     }

     @Test
     public void testDeterministicOTNOrdering() throws Exception {
         // JBRULES-3632
         String str =
                 "package indexingproblem.remove.me.anditworks;\n" +
                         "declare Criteria\n" +
                         "   processed : boolean\n" +
                         "end\n" +
                         "\n" +
                         "declare CheeseCriteria extends Criteria end\n" +
                         "\n" +
                         "rule setUp salience 10000 when\n" +
                         "then\n" +
                         "   insert(new CheeseCriteria());\n" +
                         "end\n" +
                         "\n" +
                         "rule aaa when\n" +
                         "   CheeseCriteria( )\n" +
                         "then\n" +
                         "end\n" +
                         "\n" +
                         "rule bbb when\n" +
                         "   CheeseCriteria( )\n" +
                         "then\n" +
                         "end\n" +
                         "\n" +
                         "rule ccc when\n" +
                         "   CheeseCriteria( )\n" +
                         "then\n" +
                         "end\n" +
                         "\n" +
                         "rule eeeFalse when\n" +
                         "   Criteria( processed == false )\n" +
                         "then\n" +
                         "end\n" +
                         "\n" +
                         "declare Filter end\n" +
                         "\n" +
                         "rule fffTrue when\n" +
                         "   Criteria( processed == true )\n" +
                         "   Filter( )\n" +
                         "then\n" +
                         "end\n" +
                         "\n" +
                         "rule ruleThatFails when\n" +
                         "   $criteria : Criteria( processed == false )\n" +
                         "then\n" +
                         "   modify($criteria) { setProcessed(true) }\n" +
                         "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         kbase = SerializationHelper.serializeObject( kbase );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.fireAllRules();

         // check that OTNs ordering is not breaking serialization
         ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession, true );
         ksession.fireAllRules();
     }

     @Test
     public void testRemoveBigRule() throws Exception {
         // JBRULES-3496
         String str =
                 "package org.drools.compiler.test\n" +
                         "\n" +
                         "declare SimpleFact\n" +
                         "   patientSpaceId : String\n" +
                         "   block : int\n" +
                         "end\n" +
                         "\n" +
                         "declare SimpleMembership\n" +
                         "   patientSpaceId : String\n" +
                         "   listId : String\n" +
                         "end\n" +
                         "\n" +
                         "declare SimplePatient\n" +
                         "   spaceId : String\n" +
                         "end\n" +
                         "\n" +
                         "rule \"RTR - 47146 retract\"\n" +
                         "agenda-group \"list membership\"\n" +
                         "when\n" +
                         "   $listMembership0 : SimpleMembership( $listMembershipPatientSpaceIdRoot : patientSpaceId, ( listId != null && listId == \"47146\" ) )\n" +
                         "   not ( $patient0 : SimplePatient( $patientSpaceIdRoot : spaceId, spaceId != null && spaceId == $listMembershipPatientSpaceIdRoot ) \n" +
                         "       and ( ( " +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 1 )\n" +
                         "         ) or ( " +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 2 )\n" +
                         "         ) or (" +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 3 )\n" +
                         "         ) or (" +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 4 )\n" +
                         "         ) or (" +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 5 )\n" +
                         "       ) ) and ( ( " +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 6 )\n" +
                         "         ) or (" +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 7 )\n" +
                         "         ) or (" +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 8 )\n" +
                         "       ) ) and ( ( " +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 9 )\n" +
                         "         ) or (" +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 10 )\n" +
                         "         ) or (" +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 11 )\n" +
                         "         ) or (" +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 12 )\n" +
                         "         ) or (" +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 13 )\n" +
                         "         ) or ( (" +
                         "            SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 14 )\n" +
                         "           ) and (" +
                         "              SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 15 )\n" +
                         "         ) ) or ( ( " +
                         "            SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 16 )\n" +
                         "           ) and ( " +
                         "             SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 17 )\n" +
                         "         ) ) or ( ( " +
                         "             SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 18 )\n" +
                         "           ) and (" +
                         "             SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 19 )\n" +
                         "         ) ) or (" +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 20 )\n" +
                         "         ) or (" +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 21 )\n" +
                         "         ) or (" +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 22 )\n" +
                         "         ) or ( ( " +
                         "             SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 23 )\n" +
                         "         ) and (" +
                         "             SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 24 )\n" +
                         "     ) ) ) and ( ( " +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 25 )\n" +
                         "         ) or (" +
                         "           SimpleFact( patientSpaceId == $patientSpaceIdRoot, block == 26 )\n" +
                         "     ) ) )\n" +
                         "then\n" +
                         "end\n";

         Collection<KnowledgePackage> kpgs = loadKnowledgePackagesFromString(str);

         Assert.assertEquals(1, kpgs.size());

         KnowledgeBase kbase = getKnowledgeBase();
         kbase.addKnowledgePackages( kpgs );

         kbase.removeKnowledgePackage( kpgs.iterator().next().getName() );
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

     @Test
     public void testCommentWithCommaInRHS() {
         // JBRULES-3648
         String str = "import org.drools.compiler.*;\n" +
                      "rule R1 when\n" +
                      "   $p : Person( age < name.length ) \n" +
                      "then\n" +
                      "   insertLogical(new Person(\"Mario\",\n" +
                      "       // this is the age,\n" +
                      "       38));" +
                      "end";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
     }

     @Test
     public void testAlphaHashingWithConstants() {
         // JBRULES-3658
         String str = "import " + Person.class.getName() + ";\n" +
                      "import " + MiscTest.class.getName() + ";\n" +
                      "rule R1 when\n" +
                      "   $p : Person( age == 38 )\n" +
                      "then end\n" +
                      "rule R2 when\n" +
                      "   $p : Person( age == 37+1 )\n" +
                      "then end\n" +
                      "rule R3 when\n" +
                      "   $p : Person( age == 36+2 )\n" +
                      "then end\n";

         KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
         StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

         ksession.insert( new Person( "Mario", 38 ) );
         assertEquals( 3, ksession.fireAllRules() );
     }

     @Test
     public void testMemoriesCCEWhenAddRemoveAddRule() {
         // JBRULES-3656
         String rule1 = "import " + MiscTest.class.getCanonicalName() + ".*\n" +
                        "import java.util.Date\n" +
                        "rule \"RTR - 28717 retract\"\n" +
                        "when\n" +
                        "        $listMembership0 : SimpleMembership( $listMembershipPatientSpaceIdRoot : patientSpaceId,\n" +
                        "        ( listId != null && listId == \"28717\" ) ) and not ($patient0 : SimplePatient( $patientSpaceIdRoot : spaceId, spaceId != null &&\n" +
                        "        spaceId == $listMembershipPatientSpaceIdRoot ) and\n" +
                        "        (($ruleTime0 : RuleTime( $ruleTimeStartOfDay4_1 : startOfDay, $ruleTimeTime4_1 : time ) and $patient1 :\n" +
                        "        SimplePatient( spaceId != null && spaceId == $patientSpaceIdRoot, birthDate != null && (birthDate after[0s,1d] $ruleTimeStartOfDay4_1) ) ) ) )\n" +
                        "then\n" +
                        "end";

         String rule2 = "import " + MiscTest.class.getCanonicalName() + ".*\n" +
                        "import java.util.Date\n" +
                        "rule \"RTR - 28717 retract\"\n" +
                        "when  $listMembership0 : SimpleMembership( $listMembershipPatientSpaceIdRoot : patientSpaceId, ( listId != null && listId == \"28717\" ) )\n" +
                        "    and not ($patient0 : SimplePatient( $patientSpaceIdRoot : spaceId, spaceId != null && spaceId == $listMembershipPatientSpaceIdRoot )\n" +
                        "    and ( ($ruleTime0 : RuleTime( $ruleTimeStartOfDay4_1 : startOfDay, $ruleTimeTime4_1 : time )\n" +
                        "    and $patient1 : SimplePatient( spaceId != null && spaceId == $patientSpaceIdRoot, birthDate != null && (birthDate not after[0s,1d] $ruleTimeStartOfDay4_1) ) ) ) )\n" +
                        "then\n" +
                        "end";

         KnowledgeBase kbase = getKnowledgeBase();
         StatefulKnowledgeSession knowledgeSession = kbase.newStatefulKnowledgeSession();

         kbase.addKnowledgePackages(loadKnowledgePackagesFromString( rule1 ) );


         kbase.addKnowledgePackages(loadKnowledgePackagesFromString(rule2) );
     }

     public static class RuleTime {
         public Date getTime() {
             return new Date();
         }

         public Date getStartOfDay() {
             return new Date();
         }
     }

     public static class SimpleMembership {
         public String getListId() {
             return "";
         }

         public String getPatientSpaceId() {
             return "";
         }
     }

     public class SimplePatient {
         public String getSpaceId() {
             return "";
         }

         public String getFactHandleString() {
             return "";
         }

         public Date getBirthDate() {
             return new Date();
         }
     }
 }
