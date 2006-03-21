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

import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.Person;
import org.drools.WorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;

import junit.framework.TestCase;

public class IntegrationTest extends TestCase {
    public void testGlobals() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "globals_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();
        
        org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
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
        
        org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        List list = new ArrayList();
        workingMemory.setGlobal( "list", list );        
        
        Cheese stilton = new Cheese("stilton", 5);
        workingMemory.assertObject( stilton );
        
        workingMemory.fireAllRules();
        
        assertEquals( "stilton", list.get(  0 ) );        
    }        
    
    public void testOr() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "or_test.drl" ) ) );
        Package pkg = builder.getPackage();
        
        org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
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
        
        org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        Cheese stilton = new Cheese("stinky", 5);
        workingMemory.assertObject( stilton );
        List results = workingMemory.getQueryResults( "simple query" );
        assertEquals(1, results.size());        
    }
    
    public void testEval() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "eval_rule.drl" ) ) );
        Package pkg = builder.getPackage();
        
        org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
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
    
    public void testReturnValue() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "returnvalue_rule_test.drl" ) ) );
        Package pkg = builder.getPackage();
        
        org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
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
        
        org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
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
        
    }
    
}
