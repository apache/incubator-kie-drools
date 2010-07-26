/**
 * Copyright 2010 JBoss Inc
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

package org.drools.examples;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.examples.templates.Cheese;
import org.drools.examples.templates.Person;

/**
 * This shows off a very simple rule template where the data provider is a spreadsheet.
 * @author Steve
 *
 */
public class SimpleRuleTemplateExample {
    public static void main(String[] args) throws Exception {
        SimpleRuleTemplateExample launcher = new SimpleRuleTemplateExample();
        launcher.executeExample();        
    }

    private void executeExample() throws Exception {
        
        //first we compile the spreadsheet with the template
        //to create a whole lot of rules.
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        //the data we are interested in starts at row 2, column 2 (e.g. B2)
        final String drl = converter.compile(getSpreadsheetStream(), getRulesStream(), 2, 2);

        //Uncomment to see rules
        //System.out.println( drl );
        //BUILD RULEBASE
        final RuleBase rb = buildRuleBase(drl);

        WorkingMemory wm = rb.newStatefulSession();
        
        //now create some test data
        wm.insert( new Cheese( "stilton",
                               42 ) );
        wm.insert( new Person( "michael",
                               "stilton",
                               42 ) );
        final List<String> list = new ArrayList<String>();
        wm.setGlobal( "list",
                      list );
        
        wm.fireAllRules();
        
        System.out.println(list);
        
    }

    /** Build the rule base from the generated DRL */
    private RuleBase buildRuleBase(String... drls) throws DroolsParserException, IOException, Exception {
        //now we build the rule package and rulebase, as if they are normal rules
        PackageBuilder builder = new PackageBuilder();
        for ( String drl : drls ) {
            builder.addPackageFromDrl( new StringReader( drl ) );
        }
        
        //add the package to a rulebase (deploy the rule package).
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        return ruleBase;
    }

    private InputStream getSpreadsheetStream() {
        return this.getClass().getResourceAsStream("ExampleCheese.xls");
    }
    
    private InputStream getRulesStream() {
        return this.getClass().getResourceAsStream("Cheese.drt");
    }
    
}
