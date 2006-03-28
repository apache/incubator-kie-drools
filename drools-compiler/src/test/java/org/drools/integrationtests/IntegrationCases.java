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

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.event.DefaultAgendaEventListener;
import org.drools.rule.Package;
import org.drools.rule.Rule;

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
        workingMemory.setGlobal( "list", list );        
        
        Cheese stilton = new Cheese("stilton", 5);
        workingMemory.assertObject( stilton );       
        
        workingMemory.fireAllRules();
        
        assertEquals( new Integer( 5 ), list.get(  0 ) );  
    }    
    
    public void testLiteral() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "literal_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        AgendaEventListener listener =  new DefaultAgendaEventListener() {
            
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
        workingMemory.setGlobal( "list", list );        
        
        Cheese stilton = new Cheese("stilton", 5);
        workingMemory.assertObject( stilton );
        
        workingMemory.fireAllRules();
        
        assertEquals( "stilton", list.get(  0 ) );        
    }
    
    public void testLiteralWithBoolean() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "literal_with_boolean.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();                
        
        List list = new ArrayList();
        workingMemory.setGlobal( "list", list );         
        
        Person bill = new Person("bill", null, 12);
        bill.setAlive( true );
        workingMemory.assertObject( bill );                
        workingMemory.fireAllRules();
        
        assertEquals( bill, list.get(  0 ) );
    }

    
    
    public void testOr() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "or_test.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        List list = new ArrayList();
        workingMemory.setGlobal( "list", list );        
        
        Cheese cheddar = new Cheese("cheddar", 5);
        FactHandle h = workingMemory.assertObject( cheddar );
        
        workingMemory.fireAllRules();
        
        //just one added
        assertEquals( "got cheese", list.get(  0 ) );
        assertEquals(1, list.size());
        
        workingMemory.retractObject( h );
        workingMemory.fireAllRules();
        
        //still just one
        assertEquals(1, list.size());
        
        workingMemory.assertObject( new Cheese("stilton", 5) );
        workingMemory.fireAllRules();
        
        //now have one more
        assertEquals(2, list.size());
        
    }      
    
    public void testQuery() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "simple_query_test.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        Cheese stilton = new Cheese("stinky", 5);
        workingMemory.assertObject( stilton );
        List results = workingMemory.getQueryResults( "simple query" );
        assertEquals(1, results.size());        
    }
    
    public void testEval() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "eval_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        workingMemory.setGlobal( "five", new Integer( 5 ) );
        
        List list = new ArrayList();
        workingMemory.setGlobal( "list", list );  
        
        Cheese stilton = new Cheese("stilton", 5);
        workingMemory.assertObject( stilton );
        workingMemory.fireAllRules();
        
        assertEquals( stilton, list.get(  0 ) );  
    }      
    
    public void testEvalMore() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "eval_rule_test_more.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        
        List list = new ArrayList();
        workingMemory.setGlobal( "list", list );  
        
        workingMemory.assertObject( "foo" );
        workingMemory.fireAllRules();
        
        assertEquals( "foo", list.get(  0 ) );  
    }        
    
    public void testReturnValue() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "returnvalue_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        workingMemory.setGlobal( "two", new Integer( 2 ) );
        
        List list = new ArrayList();
        workingMemory.setGlobal( "list", list );         
        
        Person peter = new Person("peter", null, 12);
        workingMemory.assertObject( peter );
        Person jane = new Person("jane", null, 10);
        workingMemory.assertObject( jane );    
                
        workingMemory.fireAllRules();
        
        assertEquals( jane, list.get(  0 ) );
        assertEquals( peter, list.get(  1 ) );
    }   
    
    public void testPredicate() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "predicate_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        workingMemory.setGlobal( "two", new Integer( 2 ) );
        
        List list = new ArrayList();
        workingMemory.setGlobal( "list", list );         
        
        Person peter = new Person("peter", null, 12);
        workingMemory.assertObject( peter );
        Person jane = new Person("jane", null, 10);
        workingMemory.assertObject( jane );    
                
        workingMemory.fireAllRules();
        
        assertEquals( jane, list.get(  0 ) );
        assertEquals( peter, list.get(  1 ) ); 
    }     
    
    public void testNot() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "not_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();       
        
        List list = new ArrayList();
        workingMemory.setGlobal( "list", list );                  
        
        Cheese stilton = new Cheese("stilton", 5);
        FactHandle stiltonHandle = workingMemory.assertObject( stilton );
        Cheese cheddar = new Cheese("cheddar", 7);
        FactHandle cheddarHandle = workingMemory.assertObject( cheddar );        
        workingMemory.fireAllRules();
        
        assertEquals( 0, list.size() );  
        
        workingMemory.retractObject( stiltonHandle );
        
        workingMemory.fireAllRules();
        
        assertEquals( 1, list.size() );          
    }
    
    public void testNotWithBindings() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "not_with_bindings_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();
         
        Rule rule = pkg.getRules()[0];
        assertTrue(rule.isValid());
        assertEquals(0, builder.getErrors().length);
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();       
        
        List list = new ArrayList();
        workingMemory.setGlobal( "list", list );                  
        
        Cheese stilton = new Cheese("stilton", 5);
        FactHandle stiltonHandle = workingMemory.assertObject( stilton );
        Cheese cheddar = new Cheese("cheddar", 7);
        FactHandle cheddarHandle = workingMemory.assertObject( cheddar );  
        
        Person paul = new Person("paul", "stilton", 12);
        workingMemory.assertObject( paul );        
        workingMemory.fireAllRules();
        
        assertEquals( 0, list.size() );  
        
        workingMemory.retractObject( stiltonHandle );
        
        workingMemory.fireAllRules();
        
        assertEquals( 1, list.size() );              
    }   
    
    public void testExists() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "exists_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();       
        
        List list = new ArrayList();
        workingMemory.setGlobal( "list", list );                  
        

        Cheese cheddar = new Cheese("cheddar", 7);
        FactHandle cheddarHandle = workingMemory.assertObject( cheddar );        
        workingMemory.fireAllRules();
        
        assertEquals( 0, list.size() );  
        
        Cheese stilton = new Cheese("stilton", 5);
        FactHandle stiltonHandle = workingMemory.assertObject( stilton );
        workingMemory.fireAllRules();
        
        assertEquals( 1, list.size() );  
        
        Cheese brie = new Cheese("brie", 5);
        FactHandle brieHandle = workingMemory.assertObject( brie );
        workingMemory.fireAllRules();    
        
        assertEquals( 1, list.size() );          
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
            fail("Should have thrown an exception as the rule is NOT VALID.");
        } catch (RuntimeException e) {
            assertNotNull(e.getMessage());
            runtime = e;
        }
        assertTrue(builder.getErrors().length > 0);

        String pretty = builder.printErrors();
        assertFalse(pretty.equals( "" ));
        assertEquals(pretty, runtime.getMessage());
        
        System.err.println(pretty);
        
    }
    
    public void testFunction() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "function_in_consequence_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        List list = new ArrayList();
        workingMemory.setGlobal( "list", list );        
        
        Cheese stilton = new Cheese("stilton", 5);
        workingMemory.assertObject( stilton );       
        
        workingMemory.fireAllRules();
        
        assertEquals( new Integer( 5 ), list.get(  0 ) );          
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
        workingMemory.setGlobal( "list", list );        
        
        Person person = new Person("michael", "cheese");
        person.setStatus( "start" );
        workingMemory.assertObject( person );
        workingMemory.fireAllRules();
        
        assertEquals(5, list.size());
        assertTrue(list.contains( "first" ));
        assertTrue(list.contains( "second" ));
        assertTrue(list.contains( "third" ));
        assertTrue(list.contains( "fourth" ));
        assertTrue(list.contains( "fifth" ));
                  
    }    
    
    public void testWithExpanderDSL() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new InputStreamReader(getClass().getResourceAsStream( "rule_with_expander_dsl.drl" ));
        Reader dsl = new InputStreamReader(getClass().getResourceAsStream( "test_expander.dsl" ));
        builder.addPackageFromDrl( source, dsl );
        
        //the compiled package
        Package pkg = builder.getPackage();
        assertTrue( pkg.isValid() );
        assertEquals(null, pkg.getErrorSummary());
        //Check errors
        String err = builder.printErrors();
        assertEquals("", err);
        
        assertEquals(0, builder.getErrors().length);
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        
        WorkingMemory wm = ruleBase.newWorkingMemory();
        wm.assertObject( new Person("Bob", "stilton") );
        wm.assertObject( new Cheese("stilton", 42) );
        
        
        List messages = new ArrayList();
        wm.setGlobal( "messages", messages );
        wm.fireAllRules();
        
        //should have fired
        assertEquals(1, messages.size());
        
    }
    
    public void testWithExpanderMore() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new InputStreamReader(getClass().getResourceAsStream( "rule_with_expander_dsl_more.drl" ));
        Reader dsl = new InputStreamReader(getClass().getResourceAsStream( "test_expander.dsl" ));
        builder.addPackageFromDrl( source, dsl );
        
        //the compiled package
        Package pkg = builder.getPackage();
        assertTrue( pkg.isValid() );
        assertEquals(null, pkg.getErrorSummary());
        //Check errors
        String err = builder.printErrors();
        assertEquals("", err);
        assertEquals(0, builder.getErrors().length);
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        
        WorkingMemory wm = ruleBase.newWorkingMemory();
        wm.assertObject( "rage" );
        wm.assertObject( new Integer(66) );
        
        
        List messages = new ArrayList();
        wm.setGlobal( "messages", messages );
        wm.fireAllRules();
        
        //should have NONE, as both conditions should be false.
        assertEquals(0, messages.size());
        
        wm.assertObject( "fire" );
        wm.fireAllRules();
        
        //still no firings
        assertEquals(0, messages.size());
        
        wm.assertObject( new Integer(42) );
        
        wm.fireAllRules();
        
        //YOUR FIRED
        assertEquals(1, messages.size());        
        
    }    
    
    public void testPredicateAsFirstColumn() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "predicate_as_first_column.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        Cheese mussarela = new Cheese("Mussarela", 35);
        workingMemory.assertObject( mussarela );
        Cheese provolone = new Cheese("Provolone", 20);
        workingMemory.assertObject( provolone );
                
        workingMemory.fireAllRules();
        
        Assert.assertEquals( "The rule is being incorrectly fired", 35, mussarela.getPrice( ) );
        Assert.assertEquals( "Rule is incorrectly being fired", 20, provolone.getPrice( ) ); 
    }     
    
    public void testSalience() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "salience_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        List list = new ArrayList();
        workingMemory.setGlobal( "list", list );        
        
        Person person = new Person("Edson", "cheese");
        workingMemory.assertObject( person );       
        
        workingMemory.fireAllRules();
        
        Assert.assertEquals( "Two rules should have been fired", 2, list.size() );
        Assert.assertEquals( "Rule 3 should have been fired first", "Rule 3", list.get(0) );
        Assert.assertEquals( "Rule 2 should have been fired second", "Rule 2", list.get(1) );
        
    }    
    
    public void testNoLoop() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "no-loop.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        List list = new ArrayList();
        workingMemory.setGlobal( "list", list );        
        
        Cheese brie = new Cheese( "brie", 12 );
        workingMemory.assertObject( brie );       
        
        workingMemory.fireAllRules();
        
        Assert.assertEquals( "Should not loop  and thus size should be 1", 1, list.size() );
        
    }        
    
    public void testConsequenceException() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ConsequenceException.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
               
        Cheese brie = new Cheese( "brie", 12 );
        workingMemory.assertObject( brie );       
        
        try  {
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the Consequence" );
        } catch ( Exception e ) {
            assertEquals( "this should throw an exception", e.getCause().getMessage() );
        }               
    }     
    
    public void testFunctionException() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FunctionException.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
               
        Cheese brie = new Cheese( "brie", 12 );
        workingMemory.assertObject( brie );       
        
        try  {
            workingMemory.fireAllRules();
            fail( "Should throw an Exception from the Function" );
        } catch ( Exception e ) {
            assertEquals( "this should throw an exception", e.getCause().getMessage() );
        }               
    }     
    
    public void testEvalException() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_EvalException.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
               
        Cheese brie = new Cheese( "brie", 12 );       
        
        try  {
            workingMemory.assertObject( brie );
            fail( "Should throw an Exception from the Eval" );
        } catch ( Exception e ) {
            assertEquals( "this should throw an exception", e.getCause().getMessage() );
        }               
    }  
    
    public void testPredicateException() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_PredicateException.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
               
        Cheese brie = new Cheese( "brie", 12 );       
        
        try  {
            workingMemory.assertObject( brie );
            fail( "Should throw an Exception from the Predicate" );
        } catch ( Exception e ) {
            assertEquals( "this should throw an exception", e.getCause().getMessage() );
        }               
    }
    
    public void testReturnValueException() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ReturnValueException.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
               
        Cheese brie = new Cheese( "brie", 12 );       
        
        try  {
            workingMemory.assertObject( brie );
            fail( "Should throw an Exception from the ReturnValue" );
        } catch ( Exception e ) {
            assertEquals( "this should throw an exception", e.getCause().getMessage() );
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
        workingMemory.setGlobal( "list", list );   
               
        Cheese brie = new Cheese( "brie", 12 );       
        workingMemory.assertObject( brie );
        
        workingMemory.fireAllRules();
        
        assertEquals( 7, list.size() );
        
        assertEquals( "group3", list.get( 0 ) );
        assertEquals( "group4", list.get( 1 ) );
        assertEquals( "group3", list.get( 2 ) );
        assertEquals( "MAIN", list.get( 3 ) );
        assertEquals( "group1", list.get( 4 ) );
        assertEquals( "group1", list.get( 5 ) );
        assertEquals( "MAIN", list.get( 6 ) );
        
    }         
}
