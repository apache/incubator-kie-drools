package org.drools.decisiontable.parser;


/*
 * Copyright 2005 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */



import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

import org.drools.decisiontable.model.Condition;
import org.drools.decisiontable.model.Consequence;
import org.drools.decisiontable.model.Import;

import org.drools.decisiontable.model.Rule;
import org.drools.decisiontable.model.Package;

/**
 * @author Shaun Addison, Michael Neale
 * 
 * Test an excel file.
 * 
 * Assumes it has a sheet called "Decision Tables" with a rule table identified
 * by a "RuleTable" cell
 */
public class RuleWorksheetParseTest extends TestCase
{

    public void testBasicWorkbookProperties() throws Exception
    {

        InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/BasicWorkbook.xls" );

        RuleSheetListener listener = getRuleSheetListener( stream );

        Properties props = listener.getProperties( );
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

    public void testComplexWorkbookProperties() throws Exception
    {

        InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/ComplexWorkbook.xls" );
        RuleSheetListener listener = getRuleSheetListener( stream );

        Properties props = listener.getProperties( );
        assertNotNull( props );
        String ruleSetName = props.getProperty( "RuleSet" );
        assertEquals( "ruleSetName",
                      ruleSetName );

    }

    public void testWorkbookParse() throws Exception
    {
        InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/BasicWorkbook.xls" );
        RuleSheetListener listener = getRuleSheetListener( stream );

        Package ruleset = listener.getRuleSet( );
        assertNotNull( ruleset );

        Rule firstRule = (Rule) ruleset.getRules( ).get( 0 );
        assertNotNull(firstRule.getSalience( ));
        assertTrue(firstRule.getSalience( ).intValue() > 0);

        // System.out.println(ruleset.toXML());

        assertEquals( "myruleset",
                      ruleset.getName( ) );
        assertEquals( 2,
                      ruleset.getImports( ).size( ) );
        assertEquals( 6,
                      ruleset.getRules( ).size( ) );

        // check imports
        Import imp = (Import) ruleset.getImports( ).get( 0 );
        assertEquals( "blah.class1",
                      imp.getClassName( ) );
        imp = (Import) ruleset.getImports( ).get( 1 );
        assertEquals( "blah.class2",
                      imp.getClassName( ) );

        // check rules
        Rule rule = (Rule) ruleset.getRules( ).get( 0 );
        Condition cond = (Condition) rule.getConditions( ).get( 0 );
        assertEquals( "myObject.getColour().equals(red)",
                      cond.getSnippet( ) );

        Consequence cons = (Consequence) rule.getConsequences( ).get( 0 );
        assertNotNull( cons );
        assertEquals( "myObject.setIsValid(Y)",
                      cons.getSnippet( ) );

        rule = (Rule) ruleset.getRules( ).get( 5 );
        cond = (Condition) rule.getConditions( ).get( 1 );
        assertEquals( "myObject.size () > 7",
                      cond.getSnippet( ) );
        cons = (Consequence) rule.getConsequences( ).get( 0 );
        assertEquals( "myObject.setIsValid(10-Jul-1974)",
                      cons.getSnippet( ) );

    }

    /**
     * See if it can cope with odd shaped rule table, including missing
     * conditions. Also is not "sequential".
     */
    public void testComplexWorksheetMissingConditions() throws Exception
    {
        InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/ComplexWorkbook.xls" );
        RuleSheetListener listener = getRuleSheetListener( stream );

        Package ruleset = listener.getRuleSet( );
        assertEquals( 6,
                      ruleset.getRules( ).size( ) );
        assertEquals( 0,
                      ruleset.getImports( ).size( ) );

        Rule rule = (Rule) ruleset.getRules( ).get( 0 );
        assertEquals( 3,
                      rule.getConditions( ).size( ) );
        assertEquals( 2,
                      rule.getConsequences( ).size( ) );
        Consequence cons = (Consequence) rule.getConsequences( ).get( 1 );
        assertEquals( "myObject.setIsValid(1, 2)",
                      cons.getSnippet( ) );
        Condition con = (Condition) rule.getConditions( ).get( 2 );
        assertEquals( "myObject.size() < 3",
                      con.getSnippet( ) );

        rule = (Rule) ruleset.getRules( ).get( 4 );

        // this should have less conditions
        assertEquals( 1,
                      rule.getConditions( ).size( ) );

        rule = (Rule) ruleset.getRules( ).get( 5 );
        assertEquals( 2,
                      rule.getConditions( ).size( ) );
        assertEquals( 1,
                      rule.getConsequences( ).size( ) );

    }

    /**
     * Utility method showing how to get a rule sheet listener from a stream.
     */
    public static RuleSheetListener getRuleSheetListener(InputStream stream) throws IOException
    {
        return RulesheetUtil.getRuleSheetListener( stream );
    }

}

