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

package org.drools.decisiontable;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.*;
import org.drools.rule.Package;
import org.drools.template.parser.DataListener;
import org.drools.template.parser.TemplateDataListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 *         basic tests for converter utility. Note that some of this may
 *         still use the drools 2.x syntax, as it is not compiled, only tested
 *         that it generates DRL in the correct structure (not that the DRL
 *         itself is correct).
 */
public class ExternalSpreadsheetCompilerTest {
    @Test
    public void testLoadFromClassPath() {
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        final String drl = converter.compile( "/data/MultiSheetDST.xls",
                                              "/templates/test_template1.drl",
                                              11,
                                              2 );
        assertNotNull( drl );

        // System.out.println(drl);

        assertTrue( drl.indexOf( "rule \"How cool is Shaun 12\"" ) > 0 );
        assertTrue( drl.indexOf( "rule \"How cool is Kumar 11\"" ) > 0 );
        assertTrue( drl.indexOf( "import example.model.User;" ) > -1 );
        assertTrue( drl.indexOf( "import example.model.Car;" ) > -1 );
    }

    @Test
    public void testLoadSpecificWorksheet() {
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        final String drl = converter.compile( "/data/MultiSheetDST.xls",
                                              "Another Sheet",
                                              "/templates/test_template1.drl",
                                              11,
                                              2 );
        // System.out.println(drl);
        assertNotNull( drl );
    }

    @Test
    public void testLoadCsv() {
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        final String drl = converter.compile( "/data/ComplexWorkbook.csv",
                                              "/templates/test_template2.drl",
                                              InputType.CSV,
                                              10,
                                              2 );
        assertNotNull( drl );

        assertTrue( drl.indexOf( "myObject.setIsValid(1, 2)" ) > 0 );
        assertTrue( drl.indexOf( "myObject.size () > 2" ) > 0 );

        assertTrue( drl.indexOf( "Foo(myObject.getColour().equals(red),\n        myObject.size () > 1" ) > 0 );
    }

    @Test
    public void testLoadBasicWithMergedCells() {
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        final String drl = converter.compile( "/data/BasicWorkbook.xls",
                                              "/templates/test_template3.drl",
                                              InputType.XLS,
                                              10,
                                              2 );

        final String drl1 = converter.compile( "/data/BasicWorkbook.xls",
                                               "/templates/test_template3.drl",
                                               InputType.XLS,
                                               21,
                                               2 );

        assertNotNull( drl );

        Pattern p = Pattern.compile( ".*setIsValid\\(Y\\).*setIsValid\\(Y\\).*setIsValid\\(Y\\).*",
                                     Pattern.DOTALL | Pattern.MULTILINE );
        Matcher m = p.matcher( drl );
        assertTrue( m.matches() );

        assertTrue( drl.indexOf( "This is a function block" ) > -1 );
        assertTrue( drl.indexOf( "global Class1 obj1;" ) > -1 );
        assertTrue( drl1.indexOf( "myObject.setIsValid(10-Jul-1974)" ) > -1 );
        assertTrue( drl.indexOf( "myObject.getColour().equals(blue)" ) > -1 );
        assertTrue( drl.indexOf( "Foo(myObject.getColour().equals(red), myObject.size() > 12\")" ) > -1 );

        assertTrue( drl.indexOf( "b: Bar()\n        eval(myObject.size() < 3)" ) > -1 );
        assertTrue( drl.indexOf( "b: Bar()\n        eval(myObject.size() < 9)" ) > -1 );

        assertTrue( drl.indexOf( "Foo(myObject.getColour().equals(red), myObject.size() > 1)" ) < drl.indexOf( "b: Bar()\n        eval(myObject.size() < 3)" ) );

    }

    @Test
    public void testLoadBasicWithExtraCells() {
        final ExternalSpreadsheetCompiler compiler = new ExternalSpreadsheetCompiler();
        final String drl = compiler.compile( "/data/BasicWorkbook.xls",
                                             "/templates/test_template4.drl",
                                             InputType.XLS,
                                             10,
                                             2 );
        assertNotNull( drl );

        assertTrue( drl.indexOf( "This is a function block" ) > -1 );
        assertTrue( drl.indexOf( "global Class1 obj1;" ) > -1 );
        assertTrue( drl.indexOf( "myObject.getColour().equals(blue)" ) > -1 );
        assertTrue( drl.indexOf( "Foo(myObject.getColour().equals(red), myObject.size() > 12\")" ) > -1 );

        assertTrue( drl.indexOf( "b: Bar()\n        eval(myObject.size() < 3)" ) > -1 );
        assertTrue( drl.indexOf( "b: Bar()\n        eval(myObject.size() < 9)" ) > -1 );

        assertTrue( drl.indexOf( "Foo(myObject.getColour().equals(red), myObject.size() > 1)" ) < drl.indexOf( "b: Bar()\n        eval(myObject.size() < 3)" ) );
    }


    @Test
    public void testIntegration() throws Exception
    {
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        final String drl = converter.compile("/data/IntegrationExampleTest.xls", "/templates/test_integration.drl", 18, 3);
        //COMPILE
        System.out.println( drl );
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( drl ) );

        final org.drools.rule.Package pkg = builder.getPackage();
        assertNotNull( pkg );
        assertEquals( 0,
                builder.getErrors().getErrors().length );

        //BUILD RULEBASE
        final RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackage( pkg );

        //NEW WORKING MEMORY
        final WorkingMemory wm = rb.newStatefulSession();

        //ASSERT AND FIRE
        wm.insert( new Cheese( "stilton",
                42 ) );
        wm.insert( new Person( "michael",
                "stilton",
                42 ) );
        final List<String> list = new ArrayList<String>();
        wm.setGlobal( "list",
                list );
        wm.fireAllRules();
        assertEquals( 1,
                list.size() );
    }

    @Test
    public void testPricing() throws Exception
    {
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        TemplateDataListener l1 = new TemplateDataListener(10, 3, "/templates/test_pricing1.drl");
        listeners.add(l1);
        TemplateDataListener l2 = new TemplateDataListener(30, 3, "/templates/test_pricing2.drl");
        listeners.add(l2);
        converter.compile("/data/ExamplePolicyPricing.xls", InputType.XLS, listeners);
        //COMPILE
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( l1.renderDRL() ) );
        builder.addPackageFromDrl( new StringReader( l2.renderDRL() ) );

        final Package pkg = builder.getPackage();
        assertNotNull( pkg );
        DroolsError[] errors = builder.getErrors().getErrors();
//        for (int i = 0; i < errors.length; i++) {
//            DroolsError error = errors[i];
//            System.out.println(error.getMessage());
//        }
        assertEquals( 0,
                errors.length );

        //BUILD RULEBASE
        final RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackage( pkg );

        WorkingMemory wm = rb.newStatefulSession();

        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        wm.insert(driver);
        wm.insert(policy);

        wm.fireAllRules();

        System.out.println("BASE PRICE IS: " + policy.getBasePrice());
        System.out.println("DISCOUNT IS: " + policy.getDiscountPercent());

        int basePrice = policy.getBasePrice();
        assertEquals(120, basePrice);
    }

}
