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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.drools.template.parser.DataListener;
import org.drools.template.parser.TemplateDataListener;

/**
 *         Note that some of this may still use the drools 2.x syntax, as it is not compiled, only tested that it
 *         generates DRL in the correct structure (not that the DRL itself is correct).
 */
public class ExternalSpreadsheetCompilerIntegrationTest {
    @Test
    public void testIntegration() throws Exception
    {
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        final String drl = converter.compile("/data/IntegrationExampleTest.xls", "/templates/test_integration.drl", 18, 3);
        //COMPILE
        System.out.println( drl );
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( drl ) );

        final Package pkg = builder.getPackage();
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
