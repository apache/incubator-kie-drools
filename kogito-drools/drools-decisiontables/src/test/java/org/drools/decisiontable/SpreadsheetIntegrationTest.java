package org.drools.decisiontable;

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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

public class SpreadsheetIntegrationTest extends TestCase {

    public void testExecute() throws Exception {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        final String drl = converter.compile( "/data/IntegrationExampleTest.xls",
                                        InputType.XLS );
        assertNotNull( drl );
        //System.out.println(drl);

        //COMPILE
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( drl ) );

        final Package pkg = builder.getPackage();
        assertNotNull( pkg );
        System.out.println( pkg.getErrorSummary() );
        assertEquals( 0,
                      builder.getErrors().length );

        //BUILD RULEBASE
        final RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackage( pkg );

        //NEW WORKING MEMORY
        final WorkingMemory wm = rb.newWorkingMemory();

        //ASSERT AND FIRE
        wm.assertObject( new Cheese( "stilton",
                                     42 ) );
        wm.assertObject( new Person( "michael",
                                     "stilton",
                                     42 ) );
        final List list = new ArrayList();
        wm.setGlobal( "list",
                      list );
        wm.fireAllRules();
        assertEquals( 1,
                      list.size() );

    }

}