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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.compiler.Address;
import org.drools.compiler.Attribute;
import org.drools.compiler.Bar;
import org.drools.compiler.Cat;
import org.drools.compiler.Cell;
import org.drools.compiler.Cheese;
import org.drools.compiler.Cheesery;
import org.drools.compiler.Cheesery.Maturity;
import org.drools.compiler.Child;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.FactA;
import org.drools.compiler.FactB;
import org.drools.compiler.FactC;
import org.drools.compiler.FirstClass;
import org.drools.compiler.Foo;
import org.drools.compiler.Guess;
import org.drools.compiler.LongAddress;
import org.drools.compiler.Message;
import org.drools.compiler.MockPersistentSet;
import org.drools.compiler.ObjectWithSet;
import org.drools.compiler.Order;
import org.drools.compiler.OrderItem;
import org.drools.compiler.Person;
import org.drools.compiler.PersonFinal;
import org.drools.compiler.PersonInterface;
import org.drools.compiler.PersonWithEquals;
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
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.ParserError;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialectConfiguration;
import org.drools.core.ClassObjectFilter;
import org.drools.core.common.DefaultFactHandle;
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
import org.kie.internal.command.CommandFactory;
import org.kie.internal.conf.ShareAlphaNodesOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * Run all the tests with the ReteOO engine implementation
  */
 public class MiscTest extends CommonTestMethodBase {

    private static Logger logger = LoggerFactory.getLogger(MiscTest.class);

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
         assertEquals( 26,
                       stiltonError.getLine() );

         final DescrBuildError poisonError = (DescrBuildError) errors[1];
         assertTrue( poisonError.getMessage().contains( "Poison" ) );
         assertEquals( 28,
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
         assertTrue(rhsError.getLine() >= 23 && rhsError.getLine() <= 32); // TODO this should be 16
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