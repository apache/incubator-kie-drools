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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.Cell;
import org.drools.Cheese;
import org.drools.Cheesery;
import org.drools.FactHandle;
import org.drools.Person;
import org.drools.PersonInterface;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.Sensor;
import org.drools.State;
import org.drools.WorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsError;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.ParserError;
import org.drools.compiler.RuleError;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.event.DefaultAgendaEventListener;
import org.drools.integrationtests.helloworld.Message;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.spi.AgendaGroup;
import org.drools.spi.XorGroup;

/**
 * This contains the test cases for each engines implementation to execute.
 * All integration tests get added here, and will be executed for each engine type. 
 */
public abstract class IntegrationCases extends TestCase {

    /** Implementation specific subclasses must provide this. */
    protected abstract RuleBase getRuleBase() throws Exception;

    public void testGlobals() throws Exception {

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "globals_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese stilton = new Cheese( "stilton",
                                     5 );
        workingMemory.assertObject( stilton );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 0 ) );
    }

    public void testEmptyColumn() throws Exception {
        //pre build the package
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EmptyColumn.drl" ) ) );
        Package pkg = builder.getPackage();

        //add the package to a rulebase
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );

        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese stilton = new Cheese( "stilton",
                                     5 );
        workingMemory.assertObject( stilton );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 0 ) );
    }

    public void testHelloWorld() throws Exception {

        //read in the source
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "HelloWorld.drl" ) );
        DrlParser parser = new DrlParser();
        PackageDescr packageDescr = parser.parse( reader );

        //pre build the package
        PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );
        Package pkg = builder.getPackage();

        //add the package to a rulebase
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        //load up the rulebase

        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        //go !            
        Message message = new Message( "hola" );
        message.addToList( "hello" );
        message.setNumber( 42 );

        workingMemory.assertObject( message );
        workingMemory.assertObject( "boo" );
        workingMemory.fireAllRules();
        assertTrue( message.isFired() );

    }

    public void testLiteral() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "literal_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        AgendaEventListener listener = new DefaultAgendaEventListener() {

            public void activationCreated(ActivationCreatedEvent event) {
                System.out.println( event );
            }

            public void activationCancelled(ActivationCancelledEvent event) {
                System.out.println( event );
            }

            public void beforeActivationFired(BeforeActivationFiredEvent event) {
                System.out.println( event );
            }

            public void afterActivationFired(AfterActivationFiredEvent event) {
                System.out.println( event );
            }
        };

        workingMemory.addEventListener( listener );

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese stilton = new Cheese( "stilton",
                                     5 );
        workingMemory.assertObject( stilton );

        workingMemory.fireAllRules();

        assertEquals( "stilton",
                      list.get( 0 ) );
    }

    public void testLiteralWithBoolean() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "literal_with_boolean.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        PersonInterface bill = new Person( "bill",
                                           null,
                                           12 );
        bill.setAlive( true );
        workingMemory.assertObject( bill );
        workingMemory.fireAllRules();

        assertEquals( bill,
                      list.get( 0 ) );
    }

    public void testPropertyChangeSupport() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "Test_PropertyChange.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        State state = new State( "initial" );
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

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "big_decimal_and_comparable.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        PersonInterface bill = new Person( "bill",
                                           null,
                                           12 );
        bill.setBigDecimal( new BigDecimal( "42" ) );
        workingMemory.assertObject( new BigDecimal( "43" ) );
        workingMemory.assertObject( bill );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    public void testCell() throws Exception {
        Cell cell1 = new Cell( 9 );
        Cell cell = new Cell( 0 );

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "evalmodify.drl" ) ) );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        WorkingMemory memory = ruleBase.newWorkingMemory();
        memory.assertObject( cell1 );
        memory.assertObject( cell );
        memory.fireAllRules();
        assertEquals( 9,
                      cell.getValue() );
    }

    public void testOr() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "or_test.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese cheddar = new Cheese( "cheddar",
                                     5 );
        FactHandle h = workingMemory.assertObject( cheddar );

        workingMemory.fireAllRules();

        //just one added
        assertEquals( "got cheese",
                      list.get( 0 ) );
        assertEquals( 1,
                      list.size() );

        workingMemory.retractObject( h );
        workingMemory.fireAllRules();

        //still just one
        assertEquals( 1,
                      list.size() );

        workingMemory.assertObject( new Cheese( "stilton",
                                                5 ) );
        workingMemory.fireAllRules();

        //now have one more
        assertEquals( 2,
                      list.size() );

    }

    public void testQuery() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "simple_query_test.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        Cheese stilton = new Cheese( "stinky",
                                     5 );
        workingMemory.assertObject( stilton );
        QueryResults results = workingMemory.getQueryResults( "simple query" );
        assertEquals( 1,
                      results.size() );
    }

    public void testEval() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "eval_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        workingMemory.setGlobal( "five",
                                 new Integer( 5 ) );

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese stilton = new Cheese( "stilton",
                                     5 );
        workingMemory.assertObject( stilton );
        workingMemory.fireAllRules();

        assertEquals( stilton,
                      list.get( 0 ) );
    }

    public void testEvalMore() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "eval_rule_test_more.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.assertObject( "foo" );
        workingMemory.fireAllRules();

        assertEquals( "foo",
                      list.get( 0 ) );
    }

    public void testReturnValue() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "returnvalue_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        workingMemory.setGlobal( "two",
                                 new Integer( 2 ) );

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        PersonInterface peter = new Person( "peter",
                                            null,
                                            12 );
        workingMemory.assertObject( peter );
        PersonInterface jane = new Person( "jane",
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
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "predicate_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        workingMemory.setGlobal( "two",
                                 new Integer( 2 ) );

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        PersonInterface peter = new Person( "peter",
                                            null,
                                            12 );
        workingMemory.assertObject( peter );
        PersonInterface jane = new Person( "jane",
                                           null,
                                           10 );
        workingMemory.assertObject( jane );

        workingMemory.fireAllRules();

        assertEquals( jane,
                      list.get( 0 ) );
        assertEquals( peter,
                      list.get( 1 ) );
    }

    public void testNot() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "not_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese stilton = new Cheese( "stilton",
                                     5 );
        FactHandle stiltonHandle = workingMemory.assertObject( stilton );
        Cheese cheddar = new Cheese( "cheddar",
                                     7 );
        FactHandle cheddarHandle = workingMemory.assertObject( cheddar );
        workingMemory.fireAllRules();

        assertEquals( 0,
                      list.size() );

        workingMemory.retractObject( stiltonHandle );

        workingMemory.fireAllRules();

        assertEquals( 4,
                      list.size() );
        Assert.assertTrue( list.contains( new Integer( 5 ) ) );
        Assert.assertTrue( list.contains( new Integer( 6 ) ) );
        Assert.assertTrue( list.contains( new Integer( 7 ) ) );
        Assert.assertTrue( list.contains( new Integer( 8 ) ) );
    }

    public void testNotWithBindings() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "not_with_bindings_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();

        Rule rule = pkg.getRules()[0];
        assertTrue( rule.isValid() );
        assertEquals( 0,
                      builder.getErrors().length );
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese stilton = new Cheese( "stilton",
                                     5 );
        FactHandle stiltonHandle = workingMemory.assertObject( stilton );
        Cheese cheddar = new Cheese( "cheddar",
                                     7 );
        FactHandle cheddarHandle = workingMemory.assertObject( cheddar );

        PersonInterface paul = new Person( "paul",
                                           "stilton",
                                           12 );
        workingMemory.assertObject( paul );
        workingMemory.fireAllRules();

        assertEquals( 0,
                      list.size() );

        workingMemory.retractObject( stiltonHandle );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    public void testNullBehaviour() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "null_behaviour.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        PersonInterface p1 = new Person( "michael",
                                         "food",
                                         40 );
        PersonInterface p2 = new Person( null,
                                         "drink",
                                         30 );
        workingMemory.assertObject( p1 );
        workingMemory.assertObject( p2 );

        workingMemory.fireAllRules();

    }

    public void testNullConstraint() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "null_constraint.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        List foo = new ArrayList();
        workingMemory.setGlobal( "messages",
                                 foo );

        PersonInterface p1 = new Person( null,
                                         "food",
                                         40 );

        workingMemory.assertObject( p1 );

        workingMemory.fireAllRules();
        assertEquals( 1,
                      foo.size() );

    }

    public void testExists() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "exists_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese cheddar = new Cheese( "cheddar",
                                     7 );
        FactHandle cheddarHandle = workingMemory.assertObject( cheddar );
        workingMemory.fireAllRules();

        assertEquals( 0,
                      list.size() );

        Cheese stilton = new Cheese( "stilton",
                                     5 );
        FactHandle stiltonHandle = workingMemory.assertObject( stilton );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        Cheese brie = new Cheese( "brie",
                                  5 );
        FactHandle brieHandle = workingMemory.assertObject( brie );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    public void testWithInvalidRule() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "invalid_rule.drl" ) ) );
        Package pkg = builder.getPackage();
        //Mark: please check if the conseqeuence/should/shouldn't be built
        //        Rule badBoy = pkg.getRules()[0];
        //        assertFalse(badBoy.isValid());

        RuntimeException runtime = null;
        //this should ralph all over the place.
        RuleBase ruleBase = getRuleBase();
        try {
            ruleBase.addPackage( pkg );
            fail( "Should have thrown an exception as the rule is NOT VALID." );
        } catch ( RuntimeException e ) {
            assertNotNull( e.getMessage() );
            runtime = e;
        }
        assertTrue( builder.getErrors().length > 0 );

        String pretty = builder.printErrors();
        assertFalse( pretty.equals( "" ) );
        assertEquals( pretty,
                      runtime.getMessage() );

    }

    public void testErrorLineNumbers() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "errors_in_rule.drl" ) ) );
        Package pkg = builder.getPackage();

        DroolsError err = builder.getErrors()[0];
        RuleError ruleErr = (RuleError) err;
        assertNotNull( ruleErr.getDescr() );
        assertTrue( ruleErr.getLine() != -1 );

        assertEquals( 3,
                      builder.getErrors().length );

        //check that its getting it from the ruleDescr
        assertEquals( ruleErr.getLine(),
                      ruleErr.getDescr().getLine() );
        //check the absolute error line number (there are more).
        assertEquals( 11,
                      ruleErr.getLine() );

        //now check the RHS, not being too specific yet, as long as it has the rules line number, not zero
        RuleError rhs = (RuleError) builder.getErrors()[2];
        assertTrue( rhs.getLine() > 7 ); //not being too specific - may need to change this when we rework the error reporting

    }

    public void testErrorsParser() throws Exception {
        DrlParser parser = new DrlParser();
        assertEquals( 0,
                      parser.getErrors().size() );
        parser.parse( new InputStreamReader( getClass().getResourceAsStream( "errors_parser_multiple.drl" ) ) );
        assertTrue( parser.hasErrors() );
        assertTrue( parser.getErrors().size() > 0 );
        assertTrue( parser.getErrors().get( 0 ) instanceof ParserError );
        ParserError first = ((ParserError) parser.getErrors().get( 0 ));
        assertTrue( first.getMessage() != null );
        assertFalse( first.getMessage().equals( "" ) );
    }

    public void testFunction() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FunctionInConsequence.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese stilton = new Cheese( "stilton",
                                     5 );
        workingMemory.assertObject( stilton );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 0 ) );
    }

    public void testDynamicFunction() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicFunction1.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese stilton = new Cheese( "stilton",
                                     5 );
        workingMemory.assertObject( stilton );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 0 ) );

        // Check a function can be removed from a package.
        // Once removed any efforts to use it should throw an Exception
        pkg.removeFunction( "addFive" );

        Cheese cheddar = new Cheese( "cheddar",
                                     5 );
        workingMemory.assertObject( cheddar );

        try {
            workingMemory.fireAllRules();
            fail( "Function should have been removed and NoClassDefFoundError thrown from the Consequence" );
        } catch ( NoClassDefFoundError e ) {
        }

        // Check a new function can be added to replace an old function
        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicFunction2.drl" ) ) );

        ruleBase.addPackage( builder.getPackage() );

        Cheese brie = new Cheese( "brie",
                                  5 );
        workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 6 ),
                      list.get( 1 ) );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicFunction3.drl" ) ) );

        ruleBase.addPackage( builder.getPackage() );

        Cheese feta = new Cheese( "feta",
                                  5 );
        workingMemory.assertObject( feta );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 2 ) );

    }

    public void testAssertRetract() throws Exception {
        //postponed while I sort out KnowledgeHelperFixer
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "assert_retract.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        PersonInterface person = new Person( "michael",
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

    public void testWithExpanderDSL() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new InputStreamReader( getClass().getResourceAsStream( "rule_with_expander_dsl.drl" ) );
        Reader dsl = new InputStreamReader( getClass().getResourceAsStream( "test_expander.dsl" ) );
        builder.addPackageFromDrl( source,
                                   dsl );

        //the compiled package
        Package pkg = builder.getPackage();
        assertTrue( pkg.isValid() );
        assertEquals( null,
                      pkg.getErrorSummary() );
        //Check errors
        String err = builder.printErrors();
        assertEquals( "",
                      err );

        assertEquals( 0,
                      builder.getErrors().length );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );

        WorkingMemory wm = ruleBase.newWorkingMemory();
        wm.assertObject( new Person( "Bob",
                                     "http://foo.bar" ) );
        wm.assertObject( new Cheese( "stilton",
                                     42 ) );

        List messages = new ArrayList();
        wm.setGlobal( "messages",
                      messages );
        wm.fireAllRules();

        //should have fired
        assertEquals( 1,
                      messages.size() );

    }

    public void testWithExpanderMore() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new InputStreamReader( getClass().getResourceAsStream( "rule_with_expander_dsl_more.drl" ) );
        Reader dsl = new InputStreamReader( getClass().getResourceAsStream( "test_expander.dsl" ) );
        builder.addPackageFromDrl( source,
                                   dsl );

        //the compiled package
        Package pkg = builder.getPackage();
        assertTrue( pkg.isValid() );
        assertEquals( null,
                      pkg.getErrorSummary() );
        //Check errors
        String err = builder.printErrors();
        assertEquals( "",
                      err );
        assertEquals( 0,
                      builder.getErrors().length );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );

        WorkingMemory wm = ruleBase.newWorkingMemory();
        wm.assertObject( "rage" );
        wm.assertObject( new Integer( 66 ) );

        List messages = new ArrayList();
        wm.setGlobal( "messages",
                      messages );
        wm.fireAllRules();

        //should have NONE, as both conditions should be false.
        assertEquals( 0,
                      messages.size() );

        wm.assertObject( "fire" );
        wm.fireAllRules();

        //still no firings
        assertEquals( 0,
                      messages.size() );

        wm.assertObject( new Integer( 42 ) );

        wm.fireAllRules();

        //YOUR FIRED
        assertEquals( 1,
                      messages.size() );

    }

    public void testPredicateAsFirstColumn() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "predicate_as_first_column.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        Cheese mussarela = new Cheese( "Mussarela",
                                       35 );
        workingMemory.assertObject( mussarela );
        Cheese provolone = new Cheese( "Provolone",
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

    public void testSalience() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "salience_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        PersonInterface person = new Person( "Edson",
                                             "cheese" );
        workingMemory.assertObject( person );

        workingMemory.fireAllRules();

        Assert.assertEquals( "Two rules should have been fired",
                             2,
                             list.size() );
        Assert.assertEquals( "Rule 3 should have been fired first",
                             "Rule 3",
                             list.get( 0 ) );
        Assert.assertEquals( "Rule 2 should have been fired second",
                             "Rule 2",
                             list.get( 1 ) );

    }

    public void testNoLoop() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "no-loop.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese brie = new Cheese( "brie",
                                  12 );
        workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        Assert.assertEquals( "Should not loop  and thus size should be 1",
                             1,
                             list.size() );

    }

    public void testConsequenceException() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ConsequenceException.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        Cheese brie = new Cheese( "brie",
                                  12 );
        workingMemory.assertObject( brie );

        try {
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the Consequence" );
        } catch ( Exception e ) {
            assertEquals( "this should throw an exception",
                          e.getCause().getMessage() );
        }
    }

    public void testFunctionException() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FunctionException.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        Cheese brie = new Cheese( "brie",
                                  12 );
        workingMemory.assertObject( brie );

        try {
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the Function" );
        } catch ( Exception e ) {
            assertEquals( "this should throw an exception",
                          e.getCause().getMessage() );
        }
    }

    public void testEvalException() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EvalException.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        Cheese brie = new Cheese( "brie",
                                  12 );

        try {
            workingMemory.assertObject( brie );
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the Eval" );
        } catch ( Exception e ) {
            assertEquals( "this should throw an exception",
                          e.getCause().getMessage() );
        }
    }

    public void testPredicateException() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_PredicateException.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        Cheese brie = new Cheese( "brie",
                                  12 );

        try {
            workingMemory.assertObject( brie );
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the Predicate" );
        } catch ( Exception e ) {
            assertEquals( "this should throw an exception",
                          e.getCause().getMessage() );
        }
    }

    public void testReturnValueException() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ReturnValueException.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        Cheese brie = new Cheese( "brie",
                                  12 );

        try {
            workingMemory.assertObject( brie );
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the ReturnValue" );
        } catch ( Exception e ) {
            assertEquals( "this should throw an exception",
                          e.getCause().getMessage() );
        }
    }

    public void testAgendaGroups() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_AgendaGroups.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese brie = new Cheese( "brie",
                                  12 );
        workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        assertEquals( 7,
                      list.size() );

        assertEquals( "group3",
                      list.get( 0 ) );
        assertEquals( "group4",
                      list.get( 1 ) );
        assertEquals( "group3",
                      list.get( 2 ) );
        assertEquals( "MAIN",
                      list.get( 3 ) );
        assertEquals( "group1",
                      list.get( 4 ) );
        assertEquals( "group1",
                      list.get( 5 ) );
        assertEquals( "MAIN",
                      list.get( 6 ) );

        workingMemory.setFocus( "group2" );
        workingMemory.fireAllRules();

        assertEquals( 8,
                      list.size() );
        assertEquals( "group2",
                      list.get( 7 ) );
    }

    public void testXorGroups() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_XorGroups.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese brie = new Cheese( "brie",
                                  12 );
        workingMemory.assertObject( brie );

        XorGroup xorGroup0 = workingMemory.getAgenda().getXorGroup( "xor-group-0" );
        assertEquals( 2,
                      xorGroup0.size() );

        XorGroup xorGroup3 = workingMemory.getAgenda().getXorGroup( "xor-group-3" );
        assertEquals( 1,
                      xorGroup3.size() );

        AgendaGroup agendaGroup3 = workingMemory.getAgenda().getAgendaGroup( "agenda-group-3" );
        assertEquals( 1,
                      agendaGroup3.size() );

        AgendaGroup agendaGroupMain = workingMemory.getAgenda().getAgendaGroup( "MAIN" );
        assertEquals( 3,
                      agendaGroupMain.size() );

        workingMemory.clearAgendaGroup( "agenda-group-3" );
        assertEquals( 0,
                      xorGroup3.size() );
        assertEquals( 0,
                      agendaGroup3.size() );

        workingMemory.fireAllRules();

        assertEquals( 0,
                      xorGroup0.size() );

        assertEquals( 2,
                      list.size() );
        assertEquals( "rule0",
                      list.get( 0 ) );
        assertEquals( "rule2",
                      list.get( 1 ) );

    }

    public void testDuration() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Duration.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese brie = new Cheese( "brie",
                                  12 );
        FactHandle brieHandle = workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        // now check for update
        assertEquals( 0,
                      list.size() );

        // sleep for 300ms
        Thread.sleep( 300 );

        // now check for update
        assertEquals( 1,
                      list.size() );

    }

    public void testDurationWithNoLoop() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Duration_with_NoLoop.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese brie = new Cheese( "brie",
                                  12 );
        FactHandle brieHandle = workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        // now check for update
        assertEquals( 0,
                      list.size() );

        // sleep for 300ms
        Thread.sleep( 300 );

        // now check for update
        assertEquals( 1,
                      list.size() );
    }

    public void testFireRuleAfterDuration() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FireRuleAfterDuration.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese brie = new Cheese( "brie",
                                  12 );
        FactHandle brieHandle = workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        // now check for update
        assertEquals( 0,
                      list.size() );

        // sleep for 300ms
        Thread.sleep( 300 );

        workingMemory.fireAllRules();

        // now check for update
        assertEquals( 2,
                      list.size() );

    }

    public void testContainsCheese() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ContainsCheese.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese stilton = new Cheese( "stilton",
                                     12 );
        FactHandle brieHandle = workingMemory.assertObject( stilton );

        Cheesery cheesery = new Cheesery();
        cheesery.getCheeses().add( stilton );
        workingMemory.assertObject( cheesery );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( stilton,
                      list.get( 0 ) );
    }

    public void testStaticFieldReference() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_StaticField.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheesery cheesery = new Cheesery();
        cheesery.setStatus( Cheesery.SELLING_CHEESE );
        workingMemory.assertObject( cheesery );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( cheesery,
                      list.get( 0 ) );
    }

    public void testDynamicRuleAdditions() throws Exception {
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) );

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        Package pkg1 = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg1 );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        workingMemory.setGlobal( "total",
                                 new Integer( 0 ) );

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        // Adding person in advance. There is no Person() object
        // type node in memory yet, but the rule engine is supposed
        // to handle that correctly
        PersonInterface bob = new Person( "bob",
                                          "stilton" );
        bob.setStatus( "Not evaluated" );
        workingMemory.assertObject( bob );

        Cheese stilton = new Cheese( "stilton",
                                     5 );
        workingMemory.assertObject( stilton );

        Cheese cheddar = new Cheese( "cheddar",
                                     5 );
        workingMemory.assertObject( cheddar );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( "stilton",
                      list.get( 0 ) );

        reader = new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic2.drl" ) );
        builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        Package pkg2 = builder.getPackage();
        ruleBase.addPackage( pkg2 );

        assertEquals( 3,
                      list.size() );

        assertEquals( "stilton",
                      list.get( 0 ) );

        assertTrue( "cheddar".equals( list.get( 1 ) ) || "cheddar".equals( list.get( 2 ) ) );

        assertTrue( "stilton".equals( list.get( 1 ) ) || "stilton".equals( list.get( 2 ) ) );

        list.clear();

        reader = new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3.drl" ) );
        builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        Package pkg3 = builder.getPackage();
        ruleBase.addPackage( pkg3 );

        // Package 3 has a rule working on Person instances.
        // As we added person instance in advance, rule should fire now
        workingMemory.fireAllRules();

        Assert.assertEquals( "Rule from package 3 should have been fired",
                             "match Person ok",
                             bob.getStatus() );

        assertEquals( 1,
                      list.size() );

        assertEquals( bob,
                      list.get( 0 ) );

        reader = new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic4.drl" ) );
        builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        Package pkg4 = builder.getPackage();
        ruleBase.addPackage( pkg4 );

        Assert.assertEquals( "Rule from package 4 should have been fired",
                             "Who likes Stilton ok",
                             bob.getStatus() );

        assertEquals( 2,
                      list.size() );

        assertEquals( bob,
                      list.get( 1 ) );

    }

    public void testDynamicRuleRemovals() throws Exception {

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic2.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic4.drl" ) ) );
        Package pkg = builder.getPackage();

        org.drools.reteoo.RuleBaseImpl reteooRuleBase = null;
        org.drools.leaps.RuleBaseImpl leapsRuleBase = null;
        RuleBase ruleBase = getRuleBase();
        //org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
        if ( ruleBase instanceof org.drools.reteoo.RuleBaseImpl ) {
            reteooRuleBase = (org.drools.reteoo.RuleBaseImpl) ruleBase;
        } else if ( ruleBase instanceof org.drools.leaps.RuleBaseImpl ) {
            leapsRuleBase = (org.drools.leaps.RuleBaseImpl) ruleBase;
        }
        ruleBase.addPackage( pkg );

        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        PersonInterface bob = new Person( "bob",
                                          "stilton" );
        bob.setStatus( "Not evaluated" );
        workingMemory.assertObject( bob );

        Cheese stilton1 = new Cheese( "stilton",
                                      5 );
        workingMemory.assertObject( stilton1 );

        Cheese stilton2 = new Cheese( "stilton",
                                      3 );
        workingMemory.assertObject( stilton2 );

        Cheese stilton3 = new Cheese( "stilton",
                                      1 );
        workingMemory.assertObject( stilton3 );

        Cheese cheddar = new Cheese( "cheddar",
                                     5 );
        workingMemory.assertObject( cheddar );
        //        
        //        workingMemory.get
        //        
        //        workingMemory.fireAllRules();

        assertEquals( 11,
                      workingMemory.getAgenda().getActivations().length );

        if ( reteooRuleBase != null ) {
            reteooRuleBase.removeRule( "org.drools.test",
                                       "Who likes Stilton" );
            assertEquals( 8,
                          workingMemory.getAgenda().getActivations().length );

            reteooRuleBase.removeRule( "org.drools.test",
                                       "like cheese" );

            Cheese muzzarela = new Cheese( "muzzarela",
                                           5 );
            workingMemory.assertObject( muzzarela );

            assertEquals( 4,
                          workingMemory.getAgenda().getActivations().length );

            reteooRuleBase.removePackage( "org.drools.test" );

            assertEquals( 0,
                          workingMemory.getAgenda().getActivations().length );
        } else if ( leapsRuleBase != null ) {
            leapsRuleBase.removeRule( "org.drools.test",
                                       "Who likes Stilton" );
            assertEquals( 8,
                          workingMemory.getAgenda().getActivations().length );

            leapsRuleBase.removeRule( "org.drools.test",
                                       "like cheese" );

            Cheese muzzarela = new Cheese( "muzzarela",
                                           5 );
            workingMemory.assertObject( muzzarela );

            assertEquals( 4,
                          workingMemory.getAgenda().getActivations().length );

            leapsRuleBase.removePackage( "org.drools.test" );

            assertEquals( 0,
                          workingMemory.getAgenda().getActivations().length );

        }
    }

    public void testDynamicRuleRemovalsUnusedWorkingMemory() throws Exception {

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic2.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic4.drl" ) ) );
        Package pkg = builder.getPackage();

        org.drools.reteoo.RuleBaseImpl reteooRuleBase = null;
        org.drools.leaps.RuleBaseImpl leapsRuleBase = null;
        RuleBase ruleBase = getRuleBase();
        //org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
        if ( ruleBase instanceof org.drools.reteoo.RuleBaseImpl ) {
            reteooRuleBase = (org.drools.reteoo.RuleBaseImpl) ruleBase;
        } else if ( ruleBase instanceof org.drools.leaps.RuleBaseImpl ) {
            leapsRuleBase = (org.drools.leaps.RuleBaseImpl) ruleBase;
        }
        ruleBase.addPackage( pkg );

        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        if ( reteooRuleBase != null ) {
            assertEquals( 1,
                          reteooRuleBase.getPackages().length );
            assertEquals( 4,
                          reteooRuleBase.getPackages()[0].getRules().length );

            reteooRuleBase.removeRule( "org.drools.test",
                                       "Who likes Stilton" );
            assertEquals( 3,
                          reteooRuleBase.getPackages()[0].getRules().length );

            reteooRuleBase.removeRule( "org.drools.test",
                                       "like cheese" );
            assertEquals( 2,
                          reteooRuleBase.getPackages()[0].getRules().length );

            reteooRuleBase.removePackage( "org.drools.test" );
            assertEquals( 0,
                          reteooRuleBase.getPackages().length );
        } else if ( leapsRuleBase != null ) {
            assertEquals( 1,
                          leapsRuleBase.getPackages().length );
            assertEquals( 4,
                          leapsRuleBase.getPackages()[0].getRules().length );

            leapsRuleBase.removeRule( "org.drools.test",
                                      "Who likes Stilton" );
            assertEquals( 3,
                          leapsRuleBase.getPackages()[0].getRules().length );

            leapsRuleBase.removeRule( "org.drools.test",
                                      "like cheese" );
            assertEquals( 2,
                          leapsRuleBase.getPackages()[0].getRules().length );

            leapsRuleBase.removePackage( "org.drools.test" );
            assertEquals( 0,
                          leapsRuleBase.getPackages().length );
        }
    }

    public void testNullValuesIndexing() throws Exception {
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_NullValuesIndexing.drl" ) );

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        Package pkg1 = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg1 );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        // Adding person with null name and likes attributes
        PersonInterface bob = new Person( null,
                                          null );
        bob.setStatus( "P1" );
        PersonInterface pete = new Person( null,
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
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Serializable.drl" ) );

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        Package pkg = builder.getPackage();

        assertEquals( 0,
                      builder.getErrors().length );

        RuleBase ruleBase = getRuleBase();//RuleBaseFactory.newRuleBase();

        ruleBase.addPackage( pkg );

        byte[] ast = serializeOut( ruleBase );
        ruleBase = (RuleBase) serializeIn( ast );
        Rule[] rules = ruleBase.getPackages()[0].getRules();
        assertEquals( 3,
                      rules.length );

        assertEquals( "match Person 1",
                      rules[0].getName() );
        assertEquals( "match Person 2",
                      rules[1].getName() );
        assertEquals( "match Person 3",
                      rules[2].getName() );
    }

    public void testLogicalAssertions() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertions.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        Cheese brie = new Cheese( "brie",
                                  12 );
        FactHandle brieHandle = workingMemory.assertObject( brie );

        Cheese provolone = new Cheese( "provolone",
                                       12 );
        FactHandle provoloneHandle = workingMemory.assertObject( provolone );

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
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsBacking.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        Cheese cheese1 = new Cheese( "c",
                                     1 );
        Cheese cheese2 = new Cheese( cheese1.getType(),
                                     1 );
        List list;

        FactHandle h1 = workingMemory.assertObject( cheese1 );
        workingMemory.fireAllRules();
        list = workingMemory.getObjects( cheese1.getType().getClass() );
        assertEquals( 1,
                      list.size() );
        //probably dangerous, as contains works with equals, not identity
        assertEquals( cheese1.getType(),
                      list.get( 0 ) );
        //FactHandle ht = workingMemory.getFactHandle(c1.getType());

        FactHandle h2 = workingMemory.assertObject( cheese2 );
        workingMemory.fireAllRules();
        list = workingMemory.getObjects( cheese1.getType().getClass() );
        assertEquals( 1,
                      list.size() );
        assertEquals( cheese1.getType(),
                      list.get( 0 ) );

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
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsSelfreferencing.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list;

        String b = new String( "b" );
        String a = new String( "a" );

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
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsLoop.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list;

        List l = new ArrayList();
        String a = new String( "a" );
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
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsNoLoop.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list;

        List l = new ArrayList();
        String a = new String( "a" );
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

    public void testLogicalAssertions2() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertions2.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List events = new ArrayList();

        workingMemory.setGlobal( "events",
                                 events );

        Sensor sensor = new Sensor( 80,
                                    80 );
        FactHandle handle = workingMemory.assertObject( sensor );

        // everything should be normal
        workingMemory.fireAllRules();

        List list = workingMemory.getObjects();

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
        assertEquals( "Only sensor is there",
                      1,
                      list.size() );
        assertEquals( "Exactly six events",
                      6,
                      events.size() );
    }

    public void testLogicalAssertionsNot() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsNot.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list;

        String a = new String( "a" );
        Integer i = new Integer( 1 );
        workingMemory.setGlobal( "i",
                                 i );

        workingMemory.fireAllRules();
        list = workingMemory.getObjects();
        assertEquals( "i was not asserted by not a => i.",
                      1,
                      list.size() );
        assertEquals( "i was not asserted by not a => i.",
                      i,
                      list.get( 0 ) );

        FactHandle h = workingMemory.assertObject( a );
        //no need to fire rules, assertion alone removes justification for i, so it should be retracted.
        //workingMemory.fireAllRules();
        list = workingMemory.getObjects();
        assertEquals( "a was not asserted or i not retracted.",
                      1,
                      list.size() );
        assertEquals( "a was asserted.",
                      a,
                      list.get( 0 ) );
        assertFalse( "i was not rectracted.",
                     list.contains( i ) );

        //no rules should fire, but nevertheless...
        //workingMemory.fireAllRules();
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
                      i,
                      list.get( 0 ) );
    }

    public void testLogicalAssertionsNotPingPong() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsNotPingPong.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        //workingMemory.addEventListener(new DebugAgendaEventListener());
        //workingMemory.addEventListener(new DebugWorkingMemoryEventListener());

        List list;

        List l = new ArrayList();

        String s = new String( "s" );
        Integer i = new Integer( 1 );
        workingMemory.setGlobal( "i",
                                 i );
        workingMemory.setGlobal( "s",
                                 s );
        workingMemory.setGlobal( "l",
                                 l );

        workingMemory.fireAllRules();

        //not sure about desired state of working memory. 
        assertEquals( "Rules have not fired (looped) expected number of times",
                      10,
                      l.size() );
    }

    public void testLogicalAssertionsDynamicRule() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsDynamicRule.drl" ) ) );
        Package pkg = builder.getPackage();

        org.drools.reteoo.RuleBaseImpl reteooRuleBase = null;
        org.drools.leaps.RuleBaseImpl leapsRuleBase = null;
        RuleBase ruleBase = getRuleBase();
        if ( ruleBase instanceof org.drools.reteoo.RuleBaseImpl ) {
            reteooRuleBase = (org.drools.reteoo.RuleBaseImpl) ruleBase;
        } else if ( ruleBase instanceof org.drools.leaps.RuleBaseImpl ) {
            leapsRuleBase = (org.drools.leaps.RuleBaseImpl) ruleBase;
        }
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        //        workingMemory.addEventListener(new org.drools.event.DebugAgendaEventListener());
        //        workingMemory.addEventListener(new org.drools.event.DebugWorkingMemoryEventListener());

        Cheese c1 = new Cheese( "a",
                                1 );
        Cheese c2 = new Cheese( "b",
                                2 );
        Cheese c3 = new Cheese( "c",
                                3 );
        List list;

        workingMemory.assertObject( c1 );
        FactHandle h = workingMemory.assertObject( c2 );
        workingMemory.assertObject( c3 );
        workingMemory.fireAllRules();
        list = workingMemory.getObjects( c1.getType().getClass() );
        assertEquals( 2,
                      list.size() );
        assertFalse( list.contains( c1.getType() ) );
        assertTrue( list.contains( c2.getType() ) );
        assertTrue( list.contains( c3.getType() ) );

        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_LogicalAssertionsDynamicRule2.drl" ) );
        builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        Package pkg2 = builder.getPackage();
        ruleBase.addPackage( pkg2 );

        workingMemory.fireAllRules();

        list = workingMemory.getObjects( c1.getType().getClass() );
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( c1.getType() ) );
        assertTrue( list.contains( c2.getType() ) );
        assertTrue( list.contains( c3.getType() ) );

        assertEquals( "org.drools.test",
                      ruleBase.getPackages()[0].getName() );
        assertEquals( "org.drools.test2",
                      ruleBase.getPackages()[1].getName() );
        assertEquals( "rule1",
                      ruleBase.getPackages()[0].getRules()[0].getName() );
        assertEquals( "rule2",
                      ruleBase.getPackages()[1].getRules()[0].getName() );

        if ( reteooRuleBase != null ) {
            reteooRuleBase.removeRule( ruleBase.getPackages()[0].getName(),
                                       ruleBase.getPackages()[0].getRules()[0].getName() );
        } else if ( leapsRuleBase != null ) {
            leapsRuleBase.removeRule( ruleBase.getPackages()[0].getName(),
                                      ruleBase.getPackages()[0].getRules()[0].getName() );
        }

        assertEquals( 0,
                      ruleBase.getPackages()[0].getRules().length );
        assertEquals( 1,
                      ruleBase.getPackages()[1].getRules().length );
        assertEquals( "org.drools.test2",
                      ruleBase.getPackages()[1].getName() );
        assertEquals( "rule2",
                      ruleBase.getPackages()[1].getRules()[0].getName() );

        list = workingMemory.getObjects( c1.getType().getClass() );
        assertEquals( "remove of rule should retract objects logically asserted based on the rule",
                      2,
                      list.size() );
        assertTrue( "remove of rule should retract objects logically asserted based on the rule",
                    list.contains( c1.getType() ) );
        assertTrue( "remove of rule should retract objects logically asserted based on the rule",
                    list.contains( c2.getType() ) );
        assertFalse( "remove of rule should retract objects logically asserted based on the rule",
                     list.contains( c3.getType() ) );

        c2.setPrice( 3 );
        workingMemory.modifyObject( h,
                                    c2 );
        list = workingMemory.getObjects( c1.getType().getClass() );
        assertEquals( "remove of rule should remove one justification for c2 -> type",
                      1,
                      list.size() );
        assertFalse( "remove of rule should remove one justification for c2 -> type",
                     list.contains( c2.getType() ) );
        assertTrue( "remove of rule should remove one justification for c2 -> type",
                    list.contains( c1.getType() ) );

        if ( reteooRuleBase != null ) {
            reteooRuleBase.removeRule( ruleBase.getPackages()[1].getName(),
                                       ruleBase.getPackages()[1].getRules()[0].getName() );
        } else if ( leapsRuleBase != null ) {
            leapsRuleBase.removeRule( ruleBase.getPackages()[1].getName(),
                                      ruleBase.getPackages()[1].getRules()[0].getName() );
        }
        assertEquals( 0,
                      ruleBase.getPackages()[0].getRules().length );
        assertEquals( 0,
                      ruleBase.getPackages()[1].getRules().length );
        list = workingMemory.getObjects( c1.getType().getClass() );
        assertEquals( 0,
                      list.size() );
    }

    public void testEmptyRule() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EmptyRule.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.fireAllRules();

        assertTrue( list.contains( "fired1" ) );
        assertTrue( list.contains( "fired2" ) );
    }

    public void testjustEval() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_NoColumns.drl" ) ) );
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.fireAllRules();

        assertTrue( list.contains( "fired1" ) );
        assertTrue( list.contains( "fired3" ) );
    }

    private Object serializeIn(byte[] bytes) throws IOException,
                                            ClassNotFoundException {
        ObjectInput in = new ObjectInputStream( new ByteArrayInputStream( bytes ) );
        Object obj = in.readObject();
        in.close();
        return obj;
    }

    private byte[] serializeOut(Object obj) throws IOException {
        // Serialize to a byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream( bos );
        out.writeObject( obj );
        out.close();

        // Get the bytes of the serialized object
        byte[] bytes = bos.toByteArray();
        return bytes;
    }

}
