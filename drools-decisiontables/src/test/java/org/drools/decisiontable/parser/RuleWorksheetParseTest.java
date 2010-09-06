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

package org.drools.decisiontable.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

import org.drools.template.model.Condition;
import org.drools.template.model.Consequence;
import org.drools.template.model.Import;
import org.drools.template.model.Package;
import org.drools.template.model.Rule;

/**
 * @author Shaun Addison, Michael Neale
 * 
 * Test an excel file.
 * 
 * Assumes it has a sheet called "Decision Tables" with a rule table identified
 * by a "RuleTable" cell
 */
public class RuleWorksheetParseTest extends TestCase {

    public void testBasicWorkbookProperties() throws Exception {

        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/BasicWorkbook.xls" );

        final RuleSheetListener listener = getRuleSheetListener( stream );

        final Properties props = listener.getProperties();
        assertNotNull( props );
        assertEquals( "myruleset",
                      props.getProperty( "RuleSet" ) );
        assertEquals( "someMisc",
                      props.getProperty( "misc" ) );
        /*
         * System.out.println("Here are the global properties...");
         * listener.getProperties().list(System.out);
         */
    }

    public void testComplexWorkbookProperties() throws Exception {

        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/ComplexWorkbook.xls" );
        final RuleSheetListener listener = getRuleSheetListener( stream );

        final Properties props = listener.getProperties();
        assertNotNull( props );
        final String ruleSetName = props.getProperty( "RuleSet" );
        assertEquals( "ruleSetName",
                      ruleSetName );

    }

    public void testWorkbookParse() throws Exception {
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/BasicWorkbook.xls" );
        final RuleSheetListener listener = getRuleSheetListener( stream );

        final Package ruleset = listener.getRuleSet();
        assertNotNull( ruleset );

        final Rule firstRule = (Rule) ruleset.getRules().get( 0 );
        assertNotNull( firstRule.getSalience() );
        assertTrue( firstRule.getSalience().intValue() > 0 );

        // System.out.println(ruleset.toXML());

        assertEquals( "myruleset",
                      ruleset.getName() );
        assertEquals( 2,
                      ruleset.getImports().size() );
        assertEquals( 6,
                      ruleset.getRules().size() );

        // check imports
        Import imp = (Import) ruleset.getImports().get( 0 );
        assertEquals( "blah.class1",
                      imp.getClassName() );
        imp = (Import) ruleset.getImports().get( 1 );
        assertEquals( "blah.class2",
                      imp.getClassName() );

        // check rules
        Rule rule = (Rule) ruleset.getRules().get( 0 );
        Condition cond = (Condition) rule.getConditions().get( 0 );
        assertEquals( "Foo(myObject.getColour().equals(red), myObject.size () > 12\\\")",
                      cond.getSnippet() );

        Consequence cons = (Consequence) rule.getConsequences().get( 0 );
        assertNotNull( cons );
        assertEquals( "myObject.setIsValid(Y);",
                      cons.getSnippet() );

        rule = (Rule) ruleset.getRules().get( 5 );
        cond = (Condition) rule.getConditions().get( 1 );
        assertEquals( "myObject.size () > 7",
                      cond.getSnippet() );
        cons = (Consequence) rule.getConsequences().get( 0 );
        assertEquals( "myObject.setIsValid(10-Jul-1974)",
                      cons.getSnippet() );

    }

    /**
     * See if it can cope with odd shaped rule table, including missing
     * conditions. Also is not "sequential".
     */
    // TIRELLI: test commented out while we decide what to do in order to solve i18n issues
//    public void testComplexWorksheetMissingConditions() throws Exception {
//        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/ComplexWorkbook.xls" );
//        final RuleSheetListener listener = getRuleSheetListener( stream );
//
//        final Package ruleset = listener.getRuleSet();
//        assertEquals( 6,
//                      ruleset.getRules().size() );
//        assertEquals( 0,
//                      ruleset.getImports().size() );
//
//        Rule rule = (Rule) ruleset.getRules().get( 0 );
//        assertEquals( 3,
//                      rule.getConditions().size() );
//        assertEquals( 2,
//                      rule.getConsequences().size() );
//        final Consequence cons = (Consequence) rule.getConsequences().get( 1 );
//        assertEquals( "myObject.setIsValid(1, 2)",
//                      cons.getSnippet() );
//        final Condition con = (Condition) rule.getConditions().get( 2 );
//        assertEquals( "myObject.size() < $3.00",
//                      con.getSnippet() );
//
//        rule = (Rule) ruleset.getRules().get( 4 );
//
//        // this should have less conditions
//        assertEquals( 1,
//                      rule.getConditions().size() );
//
//        rule = (Rule) ruleset.getRules().get( 5 );
//        assertEquals( 2,
//                      rule.getConditions().size() );
//        assertEquals( 1,
//                      rule.getConsequences().size() );
//
//    }

    /**
     * Utility method showing how to get a rule sheet listener from a stream.
     */
    public static RuleSheetListener getRuleSheetListener(final InputStream stream) throws IOException {
        return RulesheetUtil.getRuleSheetListener( stream );
    }

}
