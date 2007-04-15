package org.drools.integrationtests;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.AssertedObject;
import org.drools.Cell;
import org.drools.Cheese;
import org.drools.Cheesery;
import org.drools.Child;
import org.drools.FactHandle;
import org.drools.FromTestClass;
import org.drools.Guess;
import org.drools.IndexedNumber;
import org.drools.Order;
import org.drools.OrderItem;
import org.drools.Person;
import org.drools.PersonInterface;
import org.drools.Primitives;
import org.drools.QueryResult;
import org.drools.QueryResults;
import org.drools.RandomNumber;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.Sensor;
import org.drools.State;
import org.drools.TestParam;
import org.drools.WorkingMemory;
import org.drools.Cheesery.Maturity;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.ParserError;
import org.drools.compiler.RuleError;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.event.DefaultWorkingMemoryEventListener;
import org.drools.event.ObjectAssertedEvent;
import org.drools.event.ObjectModifiedEvent;
import org.drools.event.ObjectRetractedEvent;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.facttemplates.Fact;
import org.drools.facttemplates.FactTemplate;
import org.drools.integrationtests.helloworld.Message;
import org.drools.lang.DrlDumper;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.InvalidRulePackage;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.xml.XmlDumper;

/** Run all the tests with the ReteOO engine implementation */
public class MiscTest extends TestCase {

    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }
    
    public void testGlobals() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "globals_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.setGlobal( "string",
                                 "stilton" );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        workingMemory.assertObject( stilton );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 0 ) );
    }

    public void testFieldBiningsAndEvalSharing() throws Exception {
        final String drl = "test_FieldBindingsAndEvalSharing.drl";
        evalSharingTest( drl );
    }

    public void testFieldBiningsAndPredicateSharing() throws Exception {
        final String drl = "test_FieldBindingsAndPredicateSharing.drl";
        evalSharingTest( drl );
    }

    private void evalSharingTest(final String drl) throws DroolsParserException,
                                                  IOException,
                                                  Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( drl ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory wm = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        wm.setGlobal( "list",
                      list );

        final TestParam tp1 = new TestParam();
        tp1.setValue2( "boo" );
        wm.assertObject( tp1 );

        wm.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    public void testFactBindings() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FactBindings.drl" ) ) );
        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List events = new ArrayList();
        final WorkingMemoryEventListener listener = new DefaultWorkingMemoryEventListener() {
            public void objectModified(ObjectModifiedEvent event) {
                events.add( event );
            }
        };

        workingMemory.addEventListener( listener );

        final Person bigCheese = new Person( "big cheese" );
        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );
        bigCheese.setCheese( cheddar );

        final FactHandle bigCheeseHandle = workingMemory.assertObject( bigCheese );
        final FactHandle cheddarHandle = workingMemory.assertObject( cheddar );
        workingMemory.fireAllRules();

        ObjectModifiedEvent event = (ObjectModifiedEvent) events.get( 0 );
        assertSame( cheddarHandle,
                    event.getFactHandle() );
        assertSame( cheddar,
                    event.getOldObject() );
        assertSame( cheddar,
                    event.getObject() );

        event = (ObjectModifiedEvent) events.get( 1 );
        assertSame( bigCheeseHandle,
                    event.getFactHandle() );
        assertSame( bigCheese,
                    event.getOldObject() );
        assertSame( bigCheese,
                    event.getObject() );
    }

    public void testNullHandling() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NullHandling.drl" ) ) );
        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );
        final Cheese nullCheese = new Cheese( null,
                                              2 );
        workingMemory.assertObject( nullCheese );

        final Person notNullPerson = new Person( "shoes butt back" );
        notNullPerson.setBigDecimal( new BigDecimal( "42.42" ) );

        workingMemory.assertObject( notNullPerson );

        final Person nullPerson = new Person( "whee" );
        nullPerson.setBigDecimal( null );

        workingMemory.assertObject( nullPerson );

        workingMemory.fireAllRules();
        System.out.println( list.get( 0 ) );
        assertEquals( 3,
                      list.size() );

    }

    public void testEmptyColumn() throws Exception {
        // pre build the package
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EmptyColumn.drl" ) ) );
        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        workingMemory.assertObject( stilton );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 0 ) );
    }

    private RuleBase loadRuleBase(final Reader reader) throws IOException,
                                                      DroolsParserException,
                                                      Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            Assert.fail( "Error messages in parser, need to sort this our (or else collect error messages)" );
        }
        // pre build the package
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );
        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        // load up the rulebase
        return ruleBase;
    }

    public void testExplicitAnd() throws Exception {
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_ExplicitAnd.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );
        workingMemory.assertObject( new Message( "hola" ) );

        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        workingMemory.assertObject( new Cheese( "brie",
                                                33 ) );

        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );
    }

    public void testHelloWorld() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "HelloWorld.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        // go !
        final Message message = new Message( "hola" );
        message.addToList( "hello" );
        message.setNumber( 42 );

        workingMemory.assertObject( message );
        workingMemory.assertObject( "boo" );
        workingMemory.fireAllRules();
        assertTrue( message.isFired() );
        assertEquals( message,
                      list.get( 0 ) );

    }

    public void testLiteral() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "literal_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        workingMemory.assertObject( stilton );

        workingMemory.fireAllRules();

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    public void testLiteralWithBoolean() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "literal_with_boolean.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final PersonInterface bill = new Person( "bill",
                                                 null,
                                                 12 );
        bill.setAlive( true );
        workingMemory.assertObject( bill );
        workingMemory.fireAllRules();

        assertEquals( bill,
                      list.get( 0 ) );
    }

    public void testFactTemplate() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FactTemplate.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final FactTemplate cheese = pkg.getFactTemplate( "Cheese" );
        final Fact stilton = cheese.createFact( 0 );
        stilton.setFieldValue( "name",
                               "stilton" );
        stilton.setFieldValue( "price",
                               new Integer( 100 ) );
        workingMemory.assertObject( stilton );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertEquals( stilton,
                      list.get( 0 ) );
        final Fact fact = (Fact) list.get( 0 );
        assertSame( stilton,
                    fact );
        assertEquals( new Integer( 200 ),
                      fact.getFieldValue( "price" ) );

    }

    public void testPropertyChangeSupport() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_PropertyChange.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final State state = new State( "initial" );
        workingMemory.assertObject( state,
                                    true );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        state.setFlag( true );
        assertEquals( 1,
                      list.size() );

        workingMemory.fireAllRules();
        assertEquals( 2,
                      list.size() );

        state.setState( "finished" );
        workingMemory.fireAllRules();
        assertEquals( 3,
                      list.size() );

    }

    public void testBigDecimal() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "big_decimal_and_comparable.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final PersonInterface bill = new Person( "bill",
                                                 null,
                                                 12 );
        bill.setBigDecimal( new BigDecimal( "42" ) );

        final PersonInterface ben = new Person( "ben",
                                                null,
                                                13 );
        ben.setBigDecimal( new BigDecimal( "43" ) );

        workingMemory.assertObject( bill );
        workingMemory.assertObject( ben );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    public void testCell() throws Exception {
        final Cell cell1 = new Cell( 9 );
        final Cell cell = new Cell( 0 );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "evalmodify.drl" ) ) );

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        final WorkingMemory memory = ruleBase.newWorkingMemory();
        memory.assertObject( cell1 );
        memory.assertObject( cell );
        memory.fireAllRules();
        assertEquals( 9,
                      cell.getValue() );
    }

    public void testOr() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "or_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        final FactHandle h = workingMemory.assertObject( cheddar );

        workingMemory.fireAllRules();

        // just one added
        assertEquals( "got cheese",
                      list.get( 0 ) );
        assertEquals( 1,
                      list.size() );

        workingMemory.retractObject( h );
        workingMemory.fireAllRules();

        // still just one
        assertEquals( 1,
                      list.size() );

        workingMemory.assertObject( new Cheese( "stilton",
                                                5 ) );
        workingMemory.fireAllRules();

        // now have one more
        assertEquals( 2,
                      list.size() );

    }

    public void testQuery() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "simple_query_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final Cheese stilton = new Cheese( "stinky",
                                           5 );
        workingMemory.assertObject( stilton );
        final QueryResults results = workingMemory.getQueryResults( "simple query" );
        assertEquals( 1,
                      results.size() );
    }

    public void testEval() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "eval_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        workingMemory.setGlobal( "five",
                                 new Integer( 5 ) );

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        workingMemory.assertObject( stilton );
        workingMemory.fireAllRules();

        assertEquals( stilton,
                      list.get( 0 ) );
    }

    public void testJaninoEval() throws Exception {
        final PackageBuilderConfiguration config = new PackageBuilderConfiguration();
        config.setCompiler( PackageBuilderConfiguration.JANINO );
        final PackageBuilder builder = new PackageBuilder( config );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "eval_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        workingMemory.setGlobal( "five",
                                 new Integer( 5 ) );

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        workingMemory.assertObject( stilton );
        workingMemory.fireAllRules();

        assertEquals( stilton,
                      list.get( 0 ) );
    }

    public void testEvalMore() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "eval_rule_test_more.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Person foo = new Person( "foo" );
        workingMemory.assertObject( foo );
        workingMemory.fireAllRules();

        assertEquals( foo,
                      list.get( 0 ) );
    }

    public void testReturnValue() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "returnvalue_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        workingMemory.setGlobal( "two",
                                 new Integer( 2 ) );

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final PersonInterface peter = new Person( "peter",
                                                  null,
                                                  12 );
        workingMemory.assertObject( peter );
        final PersonInterface jane = new Person( "jane",
                                                 null,
                                                 10 );
        workingMemory.assertObject( jane );

        workingMemory.fireAllRules();

        assertEquals( jane,
                      list.get( 0 ) );
        assertEquals( peter,
                      list.get( 1 ) );
    }

    public void testPredicate() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "predicate_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        workingMemory.setGlobal( "two",
                                 new Integer( 2 ) );

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final PersonInterface peter = new Person( "peter",
                                                  null,
                                                  12 );
        workingMemory.assertObject( peter );
        final PersonInterface jane = new Person( "jane",
                                                 null,
                                                 10 );
        workingMemory.assertObject( jane );

        workingMemory.fireAllRules();

        assertEquals( jane,
                      list.get( 0 ) );
        assertEquals( peter,
                      list.get( 1 ) );
    }
    
    public void testNullBehaviour() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "null_behaviour.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final PersonInterface p1 = new Person( "michael",
                                               "food",
                                               40 );
        final PersonInterface p2 = new Person( null,
                                               "drink",
                                               30 );
        workingMemory.assertObject( p1 );
        workingMemory.assertObject( p2 );

        workingMemory.fireAllRules();

    }

    public void testNullConstraint() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "null_constraint.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        final List foo = new ArrayList();
        workingMemory.setGlobal( "messages",
                                 foo );

        final PersonInterface p1 = new Person( null,
                                               "food",
                                               40 );
        final Primitives p2 = new Primitives();
        p2.setArrayAttribute( null );

        workingMemory.assertObject( p1 );
        workingMemory.assertObject( p2 );

        workingMemory.fireAllRules();
        assertEquals( 2,
                      foo.size() );

    }

    public void testImportFunctions() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ImportFunctions.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        final Cheese cheese = new Cheese( "stilton",
                                          15 );
        workingMemory.assertObject( cheese );
        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );
        workingMemory.fireAllRules();

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

    public void testBasicFrom() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_From.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        final List list1 = new ArrayList();
        workingMemory.setGlobal( "list1",
                                 list1 );
        final List list2 = new ArrayList();
        workingMemory.setGlobal( "list2",
                                 list2 );
        final List list3 = new ArrayList();
        workingMemory.setGlobal( "list3",
                                 list3 );

        final Cheesery cheesery = new Cheesery();
        final Cheese stilton = new Cheese( "stilton",
                                           12 );
        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );
        cheesery.addCheese( stilton );
        cheesery.addCheese( cheddar );
        workingMemory.setGlobal( "cheesery",
                                 cheesery );
        workingMemory.assertObject( cheesery );

        workingMemory.fireAllRules();

        // from using a global
        assertEquals( 2,
                      list1.size() );
        assertEquals( cheddar,
                      list1.get( 0 ) );
        assertEquals( stilton,
                      list1.get( 1 ) );

        // from using a declaration
        assertEquals( 2,
                      list2.size() );
        assertEquals( cheddar,
                      list2.get( 0 ) );
        assertEquals( stilton,
                      list2.get( 1 ) );

        // from using a declaration
        assertEquals( 1,
                      list3.size() );
        assertEquals( stilton,
                      list3.get( 0 ) );
    }

    public void testFromWithParams() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FromWithParams.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        final List list = new ArrayList();
        final Object globalObject = new Object();
        workingMemory.setGlobal( "list",
                                 list );
        workingMemory.setGlobal( "testObject",
                                 new FromTestClass() );
        workingMemory.setGlobal( "globalObject",
                                 globalObject );

        final Person bob = new Person( "bob" );
        workingMemory.assertObject( bob );

        workingMemory.fireAllRules();

        assertEquals( 6,
                      list.size() );

        final List array = (List) list.get( 0 );
        assertEquals( 3,
                      array.size() );
        final Person p = (Person) array.get( 0 );
        assertSame( p,
                    bob );

        assertEquals( new Integer( 42 ),
                      array.get( 1 ) );

        final List nested = (List) array.get( 2 );
        assertEquals( "x",
                      nested.get( 0 ) );
        assertEquals( "y",
                      nested.get( 1 ) );

        final Map map = (Map) list.get( 1 );
        assertEquals( 2,
                      map.keySet().size() );

        assertTrue( map.keySet().contains( bob ) );
        assertSame( globalObject,
                    map.get( bob ) );

        assertTrue( map.keySet().contains( "key1" ) );
        final Map nestedMap = (Map) map.get( "key1" );
        assertEquals( 1,
                      nestedMap.keySet().size() );
        assertTrue( nestedMap.keySet().contains( "key2" ) );
        assertEquals( "value2",
                      nestedMap.get( "key2" ) );

        assertEquals( new Integer( 42 ),
                      list.get( 2 ) );
        assertEquals( "literal",
                      list.get( 3 ) );
        assertSame( bob,
                    list.get( 4 ) );
        assertSame( globalObject,
                    list.get( 5 ) );
    }

    public void testWithInvalidRule() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "invalid_rule.drl" ) ) );
        final Package pkg = builder.getPackage();
        // Mark: please check if the conseqeuence/should/shouldn't be built
        // Rule badBoy = pkg.getRules()[0];
        // assertFalse(badBoy.isValid());

        RuntimeException runtime = null;
        // this should ralph all over the place.
        final RuleBase ruleBase = getRuleBase();
        try {
            ruleBase.addPackage( pkg );
            fail( "Should have thrown an exception as the rule is NOT VALID." );
        } catch ( final RuntimeException e ) {
            assertNotNull( e.getMessage() );
            runtime = e;
        }
        assertTrue( builder.getErrors().length > 0 );

        final String pretty = builder.printErrors();
        assertFalse( pretty.equals( "" ) );
        assertEquals( pretty,
                      runtime.getMessage() );

    }

    public void testErrorLineNumbers() throws Exception {
        // this test aims to test semantic errors
        // parser errors are another test case
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "errors_in_rule.drl" ) ) );
        final Package pkg = builder.getPackage();

        final DroolsError err = builder.getErrors()[0];
        final RuleError ruleErr = (RuleError) err;
        assertNotNull( ruleErr.getDescr() );
        assertTrue( ruleErr.getLine() != -1 );

        final DroolsError errs[] = builder.getErrors();

        assertEquals( 3,
                      builder.getErrors().length );

        // check that its getting it from the ruleDescr
        assertEquals( ruleErr.getLine(),
                      ruleErr.getDescr().getLine() );
        // check the absolute error line number (there are more).
        assertEquals( 11,
                      ruleErr.getLine() );

        // now check the RHS, not being too specific yet, as long as it has the
        // rules line number, not zero
        final RuleError rhs = (RuleError) builder.getErrors()[2];
        assertTrue( rhs.getLine() > 7 ); // not being too specific - may need to
        // change this when we rework the error
        // reporting

    }

    public void testErrorsParser() throws Exception {
        final DrlParser parser = new DrlParser();
        assertEquals( 0,
                      parser.getErrors().size() );
        parser.parse( new InputStreamReader( getClass().getResourceAsStream( "errors_parser_multiple.drl" ) ) );
        assertTrue( parser.hasErrors() );
        assertTrue( parser.getErrors().size() > 0 );
        assertTrue( parser.getErrors().get( 0 ) instanceof ParserError );
        final ParserError first = ((ParserError) parser.getErrors().get( 0 ));
        assertTrue( first.getMessage() != null );
        assertFalse( first.getMessage().equals( "" ) );
    }

    public void testFunction() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FunctionInConsequence.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        workingMemory.assertObject( stilton );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 0 ) );
    }

    public void testAssertRetract() throws Exception {
        // postponed while I sort out KnowledgeHelperFixer
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "assert_retract.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final PersonInterface person = new Person( "michael",
                                                   "cheese" );
        person.setStatus( "start" );
        workingMemory.assertObject( person );
        workingMemory.fireAllRules();

        assertEquals( 5,
                      list.size() );
        assertTrue( list.contains( "first" ) );
        assertTrue( list.contains( "second" ) );
        assertTrue( list.contains( "third" ) );
        assertTrue( list.contains( "fourth" ) );
        assertTrue( list.contains( "fifth" ) );

    }


    public void testPredicateAsFirstColumn() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "predicate_as_first_column.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final Cheese mussarela = new Cheese( "Mussarela",
                                             35 );
        workingMemory.assertObject( mussarela );
        final Cheese provolone = new Cheese( "Provolone",
                                             20 );
        workingMemory.assertObject( provolone );

        workingMemory.fireAllRules();

        Assert.assertEquals( "The rule is being incorrectly fired",
                             35,
                             mussarela.getPrice() );
        Assert.assertEquals( "Rule is incorrectly being fired",
                             20,
                             provolone.getPrice() );
    }


    public void testConsequenceException() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ConsequenceException.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final Cheese brie = new Cheese( "brie",
                                        12 );
        workingMemory.assertObject( brie );

        try {
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the Consequence" );
        } catch ( final Exception e ) {
            assertEquals( "this should throw an exception",
                          e.getCause().getMessage() );
        }
    }

    public void testFunctionException() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FunctionException.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final Cheese brie = new Cheese( "brie",
                                        12 );
        workingMemory.assertObject( brie );

        try {
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the Function" );
        } catch ( final Exception e ) {
            assertEquals( "this should throw an exception",
                          e.getCause().getMessage() );
        }
    }

    public void testEvalException() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EvalException.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final Cheese brie = new Cheese( "brie",
                                        12 );

        try {
            workingMemory.assertObject( brie );
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the Eval" );
        } catch ( final Exception e ) {
            assertEquals( "this should throw an exception",
                          e.getCause().getMessage() );
        }
    }

    public void testPredicateException() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_PredicateException.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final Cheese brie = new Cheese( "brie",
                                        12 );

        try {
            workingMemory.assertObject( brie );
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the Predicate" );
        } catch ( final Exception e ) {
            assertEquals( "this should throw an exception",
                          e.getCause().getMessage() );
        }
    }

    public void testReturnValueException() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ReturnValueException.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final Cheese brie = new Cheese( "brie",
                                        12 );

        try {
            workingMemory.assertObject( brie );
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the ReturnValue" );
        } catch ( final Exception e ) {
            assertTrue( e.getCause().getMessage().endsWith( "this should throw an exception" ) );
        }
    }

    public void testMultiRestrictionFieldConstraint() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_MultiRestrictionFieldConstraint.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list1 = new ArrayList();
        workingMemory.setGlobal( "list1",
                                 list1 );
        final List list2 = new ArrayList();
        workingMemory.setGlobal( "list2",
                                 list2 );
        final List list3 = new ArrayList();
        workingMemory.setGlobal( "list3",
                                 list3 );
        final List list4 = new ArrayList();
        workingMemory.setGlobal( "list4",
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

        final Person oldChili1 = new Person( "old chili2" );
        oldChili1.setAge( 45 );
        oldChili1.setHair( "green" );

        final Person oldChili2 = new Person( "old chili2" );
        oldChili2.setAge( 48 );
        oldChili2.setHair( "blue" );

        workingMemory.assertObject( youngChili1 );
        workingMemory.assertObject( youngChili2 );
        workingMemory.assertObject( chili1 );
        workingMemory.assertObject( chili2 );
        workingMemory.assertObject( oldChili1 );
        workingMemory.assertObject( oldChili2 );

        workingMemory.fireAllRules();

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


    public void testDumpers() throws Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "test_Dumpers.drl" ) ) );

        PackageBuilder builder = new PackageBuilder();
        builder.addPackage( pkg );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

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
        builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( drlResult ) );

        ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        workingMemory = ruleBase.newWorkingMemory();

        list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        assertEquals( 3,
                      list.size() );
        assertEquals( "3 1",
                      list.get( 0 ) );
        assertEquals( "MAIN",
                      list.get( 1 ) );
        assertEquals( "1 1",
                      list.get( 2 ) );

        final XmlDumper xmlDumper = new XmlDumper();
        final String xmlResult = xmlDumper.dump( pkg );

        // System.out.println( xmlResult );

        builder = new PackageBuilder();
        builder.addPackageFromXml( new StringReader( xmlResult ) );

        ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        workingMemory = ruleBase.newWorkingMemory();

        list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        assertEquals( 3,
                      list.size() );
        assertEquals( "3 1",
                      list.get( 0 ) );
        assertEquals( "MAIN",
                      list.get( 1 ) );
        assertEquals( "1 1",
                      list.get( 2 ) );
    }


    public void testContainsCheese() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ContainsCheese.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           12 );
        final FactHandle brieHandle = workingMemory.assertObject( stilton );

        final Cheesery cheesery = new Cheesery();
        cheesery.getCheeses().add( stilton );
        workingMemory.assertObject( cheesery );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( stilton,
                      list.get( 0 ) );
    }

    public void testStaticFieldReference() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_StaticField.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheesery cheesery1 = new Cheesery();
        cheesery1.setStatus( Cheesery.SELLING_CHEESE );
        cheesery1.setMaturity( Maturity.OLD );
        workingMemory.assertObject( cheesery1 );

        final Cheesery cheesery2 = new Cheesery();
        cheesery2.setStatus( Cheesery.MAKING_CHEESE );
        cheesery2.setMaturity( Maturity.YOUNG );
        workingMemory.assertObject( cheesery2 );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      list.size() );

        assertEquals( cheesery1,
                      list.get( 0 ) );
        assertEquals( cheesery2,
                      list.get( 1 ) );
    }


    public void testDuplicateRuleNames() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DuplicateRuleName1.drl" ) ) );

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DuplicateRuleName2.drl" ) ) );
        ruleBase.addPackage( builder.getPackage() );

        // @todo: this is from JBRULES-394 - maybe we should test more stuff
        // here?

    }

    public void testNullValuesIndexing() throws Exception {
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_NullValuesIndexing.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg1 = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg1 );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        // Adding person with null name and likes attributes
        final PersonInterface bob = new Person( null,
                                                null );
        bob.setStatus( "P1" );
        final PersonInterface pete = new Person( null,
                                                 null );
        bob.setStatus( "P2" );
        workingMemory.assertObject( bob );
        workingMemory.assertObject( pete );

        workingMemory.fireAllRules();

        Assert.assertEquals( "Indexing with null values is not working correctly.",
                             "OK",
                             bob.getStatus() );
        Assert.assertEquals( "Indexing with null values is not working correctly.",
                             "OK",
                             pete.getStatus() );

    }

    public void testSerializable() throws Exception {

        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Serializable.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg = builder.getPackage();

        assertEquals( 0,
                      builder.getErrors().length );

        RuleBase ruleBase = getRuleBase();// RuleBaseFactory.newRuleBase();

        ruleBase.addPackage( pkg );

        final byte[] ast = serializeOut( ruleBase );
        ruleBase = (RuleBase) serializeIn( ast );
        final Rule[] rules = ruleBase.getPackages()[0].getRules();
        assertEquals( 4,
                      rules.length );

        assertEquals( "match Person 1",
                      rules[0].getName() );
        assertEquals( "match Person 2",
                      rules[1].getName() );
        assertEquals( "match Person 3",
                      rules[2].getName() );
        assertEquals( "match Integer",
                      rules[3].getName() );

        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        workingMemory.setGlobal( "list",
                                 new ArrayList() );

        final Person bob = new Person( "bob" );
        workingMemory.assertObject( bob );

        final byte[] wm = serializeOut( workingMemory );

        workingMemory = ruleBase.newWorkingMemory( new ByteArrayInputStream( wm ) );

        assertEquals( 1,
                      workingMemory.getObjects().size() );
        assertEquals( bob,
                      workingMemory.getObjects().get( 0 ) );

        assertEquals( 2,
                      workingMemory.getAgenda().agendaSize() );

        workingMemory.fireAllRules();

        final List list = (List) workingMemory.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( new Integer( 4 ),
                      list.get( 0 ) );

        assertEquals( 2,
                      workingMemory.getObjects().size() );
        assertEquals( bob,
                      workingMemory.getObjects().get( 0 ) );
        assertEquals( new Person( "help" ),
                      workingMemory.getObjects().get( 1 ) );
    }


    public void testEmptyRule() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EmptyRule.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.fireAllRules();

        assertTrue( list.contains( "fired1" ) );
        assertTrue( list.contains( "fired2" ) );
    }

    public void testjustEval() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NoColumns.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.fireAllRules();

        assertTrue( list.contains( "fired1" ) );
        assertTrue( list.contains( "fired3" ) );
    }

    public void testOrWithBinding() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_OrWithBindings.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Person hola = new Person( "hola" );
        workingMemory.assertObject( hola );

        workingMemory.fireAllRules();

        assertEquals( 0,
                      list.size() );
        workingMemory.assertObject( new State( "x" ) );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( hola ) );

    }

    protected Object serializeIn(final byte[] bytes) throws IOException,
                                                    ClassNotFoundException {
        final ObjectInput in = new ObjectInputStream( new ByteArrayInputStream( bytes ) );
        final Object obj = in.readObject();
        in.close();
        return obj;
    }

    protected byte[] serializeOut(final Object obj) throws IOException {
        // Serialize to a byte array
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutput out = new ObjectOutputStream( bos );
        out.writeObject( obj );
        out.close();

        // Get the bytes of the serialized object
        final byte[] bytes = bos.toByteArray();
        return bytes;
    }

    public void testJoinNodeModifyObject() throws Exception {
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_JoinNodeModifyObject.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg1 = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg1 );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List orderedFacts = new ArrayList();
        final List errors = new ArrayList();

        workingMemory.setGlobal( "orderedNumbers",
                                 orderedFacts );
        workingMemory.setGlobal( "errors",
                                 errors );

        final int MAX = 5;
        for ( int i = 1; i <= MAX; i++ ) {
            final IndexedNumber n = new IndexedNumber( i,
                                                       MAX - i + 1 );
            workingMemory.assertObject( n );
        }
        workingMemory.fireAllRules();

        Assert.assertTrue( "Processing generated errors: " + errors.toString(),
                           errors.isEmpty() );

        for ( int i = 1; i <= MAX; i++ ) {
            final IndexedNumber n = (IndexedNumber) orderedFacts.get( i - 1 );
            Assert.assertEquals( "Fact is out of order",
                                 i,
                                 n.getIndex() );
        }
    }

    public void testQuery2() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Query.drl" ) ) );

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        workingMemory.fireAllRules();

        final QueryResults results = workingMemory.getQueryResults( "assertedobjquery" );

        if ( results == null || !results.iterator().hasNext() ) {
            Assert.fail( "The stated query should return a result" );
        } else {
            int counter = 0;
            for ( final Iterator it = results.iterator(); it.hasNext(); ) {
                final QueryResult result = (QueryResult) it.next();;
                final AssertedObject assertedObject = (AssertedObject) result.get( "assertedobj" );
                Assert.assertNotNull( "Query result is not expected to be null",
                                      assertedObject );
                counter++;
            }
            Assert.assertEquals( "Expecting a single result from the query",
                                 1,
                                 counter );
        }
    }

    public void testTwoQuerries() throws Exception {
        // @see JBRULES-410 More than one Query definition causes an incorrect
        // Rete network to be built.

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_TwoQuerries.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final Cheese stilton = new Cheese( "stinky",
                                           5 );
        workingMemory.assertObject( stilton );
        final Person per1 = new Person( "stinker",
                                        "smelly feet",
                                        70 );
        final Person per2 = new Person( "skunky",
                                        "smelly armpits",
                                        40 );

        workingMemory.assertObject( per1 );
        workingMemory.assertObject( per2 );

        QueryResults results = workingMemory.getQueryResults( "find stinky cheeses" );
        assertEquals( 1,
                      results.size() );

        results = workingMemory.getQueryResults( "find pensioners" );
        assertEquals( 1,
                      results.size() );
    }



    public void testInsurancePricingExample() throws Exception {
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "insurance_pricing_example.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );
        final WorkingMemory wm = ruleBase.newWorkingMemory();

        // now create some test data
        final Driver driver = new Driver();
        final Policy policy = new Policy();

        wm.assertObject( driver );
        wm.assertObject( policy );

        wm.fireAllRules();

        assertEquals( 120,
                      policy.getBasePrice() );
    }

    public void testLLR() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_JoinNodeModifyTuple.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newWorkingMemory();

        // 1st time
        org.drools.Target tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat( new Float( 60.26544f ) );
        tgt.setLon( new Float( 28.952137f ) );
        tgt.setCourse( new Float( 145.0f ) );
        tgt.setSpeed( new Float( 12.0f ) );
        tgt.setTime( new Float( 1.8666667f ) );
        wm.assertObject( tgt );

        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat( new Float( 60.236874f ) );
        tgt.setLon( new Float( 28.992579f ) );
        tgt.setCourse( new Float( 325.0f ) );
        tgt.setSpeed( new Float( 8.0f ) );
        tgt.setTime( new Float( 1.8666667f ) );
        wm.assertObject( tgt );

        wm.fireAllRules();

        // 2nd time
        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat( new Float( 60.265343f ) );
        tgt.setLon( new Float( 28.952267f ) );
        tgt.setCourse( new Float( 145.0f ) );
        tgt.setSpeed( new Float( 12.0f ) );
        tgt.setTime( new Float( 1.9f ) );
        wm.assertObject( tgt );

        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat( new Float( 60.236935f ) );
        tgt.setLon( new Float( 28.992493f ) );
        tgt.setCourse( new Float( 325.0f ) );
        tgt.setSpeed( new Float( 8.0f ) );
        tgt.setTime( new Float( 1.9f ) );
        wm.assertObject( tgt );

        wm.fireAllRules();

        // 3d time
        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat( new Float( 60.26525f ) );
        tgt.setLon( new Float( 28.952396f ) );
        tgt.setCourse( new Float( 145.0f ) );
        tgt.setSpeed( new Float( 12.0f ) );
        tgt.setTime( new Float( 1.9333333f ) );
        wm.assertObject( tgt );

        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat( new Float( 60.236996f ) );
        tgt.setLon( new Float( 28.992405f ) );
        tgt.setCourse( new Float( 325.0f ) );
        tgt.setSpeed( new Float( 8.0f ) );
        tgt.setTime( new Float( 1.9333333f ) );
        wm.assertObject( tgt );

        wm.fireAllRules();

        // 4th time
        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat( new Float( 60.265163f ) );
        tgt.setLon( new Float( 28.952526f ) );
        tgt.setCourse( new Float( 145.0f ) );
        tgt.setSpeed( new Float( 12.0f ) );
        tgt.setTime( new Float( 1.9666667f ) );
        wm.assertObject( tgt );

        tgt = new org.drools.Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat( new Float( 60.237057f ) );
        tgt.setLon( new Float( 28.99232f ) );
        tgt.setCourse( new Float( 325.0f ) );
        tgt.setSpeed( new Float( 8.0f ) );
        tgt.setTime( new Float( 1.9666667f ) );
        wm.assertObject( tgt );

        wm.fireAllRules();
    }

    public void testDoubleQueryWithExists() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DoubleQueryWithExists.drl" ) ) );
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

        QueryResults queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 2,
                      queryResults.size() );

        // europe=[ 1, 2 ], america=[ 3 ]
        p3.setStatus( "america" );
        workingMemory.modifyObject( c3FactHandle,
                                    p3 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 1,
                      queryResults.size() );

        // europe=[ 1 ], america=[ 2, 3 ]
        p2.setStatus( "america" );
        workingMemory.modifyObject( c2FactHandle,
                                    p2 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 1,
                      queryResults.size() );

        // europe=[ ], america=[ 1, 2, 3 ]
        p1.setStatus( "america" );
        workingMemory.modifyObject( c1FactHandle,
                                    p1 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 2,
                      queryResults.size() );

        // europe=[ 2 ], america=[ 1, 3 ]
        p2.setStatus( "europe" );
        workingMemory.modifyObject( c2FactHandle,
                                    p2 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 1,
                      queryResults.size() );

        // europe=[ 1, 2 ], america=[ 3 ]
        p1.setStatus( "europe" );
        workingMemory.modifyObject( c1FactHandle,
                                    p1 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 1,
                      queryResults.size() );

        // europe=[ 1, 2, 3 ], america=[ ]
        p3.setStatus( "europe" );
        workingMemory.modifyObject( c3FactHandle,
                                    p3 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 2,
                      queryResults.size() );
    }

    public void testFunctionWithPrimitives() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FunctionWithPrimitives.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        workingMemory.assertObject( stilton );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 10 ),
                      list.get( 0 ) );
    }

    public void testReturnValueAndGlobal() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ReturnValueAndGlobal.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List matchlist = new ArrayList();
        workingMemory.setGlobal( "matchingList",
                                 matchlist );

        final List nonmatchlist = new ArrayList();
        workingMemory.setGlobal( "nonMatchingList",
                                 nonmatchlist );

        workingMemory.setGlobal( "cheeseType",
                                 "stilton" );

        final Cheese stilton1 = new Cheese( "stilton",
                                            5 );
        final Cheese stilton2 = new Cheese( "stilton",
                                            7 );
        final Cheese brie = new Cheese( "brie",
                                        4 );
        workingMemory.assertObject( stilton1 );
        workingMemory.assertObject( stilton2 );
        workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      matchlist.size() );
        assertEquals( 1,
                      nonmatchlist.size() );
    }

    public void testDeclaringAndUsingBindsInSamePattern() throws Exception {
        final RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setRemoveIdentities( true );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DeclaringAndUsingBindsInSamePattern.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase( config );
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List sensors = new ArrayList();

        workingMemory.setGlobal( "sensors",
                                 sensors );

        final Sensor sensor1 = new Sensor( 100,
                                           150 );
        workingMemory.assertObject( sensor1 );
        workingMemory.fireAllRules();
        assertEquals( 0,
                      sensors.size() );

        final Sensor sensor2 = new Sensor( 200,
                                           150 );
        workingMemory.assertObject( sensor2 );
        workingMemory.fireAllRules();
        assertEquals( 3,
                      sensors.size() );
    }

    public void testMissingImports() {
        try {
            final PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_missing_import.drl" ) ) );
            final Package pkg = builder.getPackage();

            final RuleBase ruleBase = getRuleBase();
            ruleBase.addPackage( pkg );

            Assert.fail( "Should have thrown an InvalidRulePackage" );
        } catch ( final InvalidRulePackage e ) {
            // everything fine
        } catch ( final Exception e ) {
            e.printStackTrace();
            Assert.fail( "Should have thrown an InvalidRulePackage Exception instead of " + e.getMessage() );
        }
    }

    public void testNestedConditionalElements() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NestedConditionalElements.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final State state = new State( "SP" );
        workingMemory.assertObject( state );

        final Person bob = new Person( "Bob" );
        bob.setStatus( state.getState() );
        bob.setLikes( "stilton" );
        workingMemory.assertObject( bob );

        workingMemory.fireAllRules();

        assertEquals( 0,
                      list.size() );

        workingMemory.assertObject( new Cheese( bob.getLikes(),
                                                10 ) );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    public void testDeclarationUsage() throws Exception {

        try {
            final PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DeclarationUsage.drl" ) ) );
            final Package pkg = builder.getPackage();

            final RuleBase ruleBase = getRuleBase();
            ruleBase.addPackage( pkg );

            fail( "Should have trown an exception" );
        } catch ( final InvalidRulePackage e ) {
            // success ... correct exception thrown
        } catch ( final Exception e ) {
            e.printStackTrace();
            fail( "Wrong exception raised: " + e.getMessage() );
        }
    }

    public void testUnbalancedTrees() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_UnbalancedTrees.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );

        final WorkingMemory wm = ruleBase.newWorkingMemory();

        wm.assertObject( new Cheese( "a",
                                     10 ) );
        wm.assertObject( new Cheese( "b",
                                     10 ) );
        wm.assertObject( new Cheese( "c",
                                     10 ) );
        wm.assertObject( new Cheese( "d",
                                     10 ) );
        final Cheese e = new Cheese( "e",
                                     10 );
        wm.assertObject( e );

        wm.fireAllRules();

        Assert.assertEquals( "Rule should have fired twice, seting the price to 30",
                             30,
                             e.getPrice() );
        // success
    }

    public void testImportConflict() throws Exception {
        final RuleBase ruleBase = getRuleBase();
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ImportConflict.drl" ) ) );
        final Package pkg = builder.getPackage();
        ruleBase.addPackage( pkg );
    }

    public void testPrimitiveArray() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_primitiveArray.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        final List result = new ArrayList();
        workingMemory.setGlobal( "result",
                                 result );

        final Primitives p1 = new Primitives();
        p1.setPrimitiveArrayAttribute( new int[]{1, 2, 3} );
        p1.setArrayAttribute( new String[]{"a", "b"} );

        workingMemory.assertObject( p1 );

        workingMemory.fireAllRules();
        assertEquals( 3,
                      result.size() );
        assertEquals( 3,
                      ((Integer) result.get( 0 )).intValue() );
        assertEquals( 2,
                      ((Integer) result.get( 1 )).intValue() );
        assertEquals( 3,
                      ((Integer) result.get( 2 )).intValue() );

    }

    public void testEmptyIdentifier() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_emptyIdentifier.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        final List result = new ArrayList();
        workingMemory.setGlobal( "results",
                                 result );

        final Person person = new Person( "bob" );
        final Cheese cheese = new Cheese( "brie",
                                          10 );

        workingMemory.assertObject( person );
        workingMemory.assertObject( cheese );

        workingMemory.fireAllRules();
        assertEquals( 4,
                      result.size() );
    }

    public void testDuplicateVariableBinding() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_duplicateVariableBinding.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        final Map result = new HashMap();
        workingMemory.setGlobal( "results",
                                 result );

        final Cheese stilton = new Cheese( "stilton",
                                           20 );
        final Cheese brie = new Cheese( "brie",
                                        10 );

        workingMemory.assertObject( stilton );
        workingMemory.assertObject( brie );

        workingMemory.fireAllRules();
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

        workingMemory.assertObject( new Person( "bob",
                                                brie.getType() ) );
        workingMemory.fireAllRules();

        assertEquals( 6,
                      result.size() );
        assertEquals( brie.getPrice(),
                      ((Integer) result.get( "test3" + brie.getType() )).intValue() );
    }

    public void testDuplicateVariableBindingError() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_duplicateVariableBindingError.drl" ) ) );
        final Package pkg = builder.getPackage();

        assertFalse( pkg.isValid() );
        assertEquals( 6,
                      pkg.getErrorSummary().split( "\n" ).length );
    }

    public void testShadowProxyInHirarchies() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ShadowProxyInHirarchies.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        workingMemory.assertObject( new Child( "gp" ) );

        workingMemory.fireAllRules();
    }

    public void testSelfReference() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_SelfReference.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        final Order order = new Order( 10 );
        final OrderItem item1 = new OrderItem( order,
                                               1 );
        final OrderItem item2 = new OrderItem( order,
                                               2 );
        final OrderItem anotherItem1 = new OrderItem( null,
                                                      3 );
        final OrderItem anotherItem2 = new OrderItem( null,
                                                      4 );
        workingMemory.assertObject( order );
        workingMemory.assertObject( item1 );
        workingMemory.assertObject( item2 );
        workingMemory.assertObject( anotherItem1 );
        workingMemory.assertObject( anotherItem2 );

        workingMemory.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertTrue( results.contains( item1 ) );
        assertTrue( results.contains( item2 ) );
    }

    public void testNumberComparisons() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NumberComparisons.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        // asserting the sensor object
        final RandomNumber rn = new RandomNumber();
        rn.setValue( 10 );
        workingMemory.assertObject( rn );

        final Guess guess = new Guess();
        guess.setValue( new Integer( 5 ) );

        final FactHandle handle = workingMemory.assertObject( guess );

        workingMemory.fireAllRules();

        // HIGHER
        assertEquals( 1,
                      list.size() );
        assertEquals( "HIGHER",
                      list.get( 0 ) );

        guess.setValue( new Integer( 15 ) );
        workingMemory.modifyObject( handle,
                                    guess );

        workingMemory.fireAllRules();

        // LOWER
        assertEquals( 2,
                      list.size() );
        assertEquals( "LOWER",
                      list.get( 1 ) );

        guess.setValue( new Integer( 10 ) );
        workingMemory.modifyObject( handle,
                                    guess );

        workingMemory.fireAllRules();

        // CORRECT
        assertEquals( 3,
                      list.size() );
        assertEquals( "CORRECT",
                      list.get( 2 ) );

    }

    public void testSkipModify() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_skipModify.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        final Cheese cheese = new Cheese( "brie",
                                          10 );
        final FactHandle handle = workingMemory.assertObject( cheese );

        final Person bob = new Person( "bob",
                                       "stilton" );
        workingMemory.assertObject( bob );

        cheese.setType( "stilton" );
        workingMemory.modifyObject( handle,
                                    cheese );
        workingMemory.fireAllRules();
        assertEquals( 2,
                      results.size() );
    }

    public void testEventModel() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EventModel.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory wm = ruleBase.newWorkingMemory();

        final List agendaList = new ArrayList();
        final AgendaEventListener agendaEventListener = new AgendaEventListener() {

            public void activationCancelled(ActivationCancelledEvent event,
                                            WorkingMemory workingMemory) {
                agendaList.add( event );

            }

            public void activationCreated(ActivationCreatedEvent event,
                                          WorkingMemory workingMemory) {
                agendaList.add( event );
            }

            public void afterActivationFired(AfterActivationFiredEvent event,
                                             WorkingMemory workingMemory) {
                agendaList.add( event );
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event,
                                          WorkingMemory workingMemory) {
                agendaList.add( event );
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event,
                                          WorkingMemory workingMemory) {
                agendaList.add( event );
            }

            public void beforeActivationFired(BeforeActivationFiredEvent event,
                                              WorkingMemory workingMemory) {
                agendaList.add( event );
            }

        };

        final List wmList = new ArrayList();
        final WorkingMemoryEventListener workingMemoryListener = new WorkingMemoryEventListener() {

            public void objectAsserted(ObjectAssertedEvent event) {
                wmList.add( event );
            }

            public void objectModified(ObjectModifiedEvent event) {
                wmList.add( event );
            }

            public void objectRetracted(ObjectRetractedEvent event) {
                wmList.add( event );
            }

        };

        wm.addEventListener( workingMemoryListener );

        final Cheese stilton = new Cheese( "stilton",
                                           15 );
        final Cheese cheddar = new Cheese( "cheddar",
                                           17 );

        final FactHandle stiltonHandle = wm.assertObject( stilton );

        final ObjectAssertedEvent oae = (ObjectAssertedEvent) wmList.get( 0 );
        assertSame( stiltonHandle,
                    oae.getFactHandle() );

        wm.modifyObject( stiltonHandle,
                         stilton );
        final ObjectModifiedEvent ome = (ObjectModifiedEvent) wmList.get( 1 );
        assertSame( stiltonHandle,
                    ome.getFactHandle() );

        wm.retractObject( stiltonHandle );
        final ObjectRetractedEvent ore = (ObjectRetractedEvent) wmList.get( 2 );
        assertSame( stiltonHandle,
                    ore.getFactHandle() );

        wm.assertObject( cheddar );
    }

    public void testImplicitDeclarations() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_implicitDeclarations.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );
        workingMemory.setGlobal( "factor",
                                 new Double( 1.2 ) );

        final Cheese cheese = new Cheese( "stilton",
                                          10 );
        workingMemory.assertObject( cheese );

        workingMemory.fireAllRules();
        assertEquals( 1,
                      results.size() );
    }

    public void testCastingInsideEvals() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_castsInsideEval.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        workingMemory.setGlobal( "value",
                                 new Integer( 20 ) );

        workingMemory.fireAllRules();
    }
    

}