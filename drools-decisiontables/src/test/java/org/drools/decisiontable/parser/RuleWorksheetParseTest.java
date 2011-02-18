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

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;


import org.drools.decisiontable.parser.xls.PropertiesSheetListener.CaseInsensitiveMap;
import org.drools.template.model.Condition;
import org.drools.template.model.Consequence;
import org.drools.template.model.DRLOutput;
import org.drools.template.model.Import;
import org.drools.template.model.Package;
import org.drools.template.model.Rule;
import org.drools.template.parser.DataListener;
import org.drools.template.parser.DecisionTableParseException;

/**
 *
 * Test an excel file.
 * 
 * Assumes it has a sheet called "Decision Tables" with a rule table identified
 * by a "RuleTable" cell
 */
public class RuleWorksheetParseTest {

    @Test
    public void testBasicWorkbookProperties() throws Exception {

        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/BasicWorkbook.xls" );

        final RuleSheetListener listener = getRuleSheetListener( stream );

        final CaseInsensitiveMap props = listener.getProperties();
        assertNotNull( props );
        assertEquals( "myruleset", props.getSingleProperty( "RuleSet" ) );
        assertEquals( "someMisc",  props.getSingleProperty( "misc" ) );
        /*
         * System.out.println("Here are the global properties...");
         * listener.getProperties().list(System.out);
         */
    }

    @Test
    public void testComplexWorkbookProperties() throws Exception {
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/ComplexWorkbook.xls" );
        final RuleSheetListener listener = getRuleSheetListener( stream );

        final CaseInsensitiveMap props = listener.getProperties();
        assertNotNull( props );
        final String ruleSetName = props.getSingleProperty( "RuleSet" );
        assertEquals( "ruleSetName", ruleSetName );
    }

    @Test
    public void testWorkbookParse() throws Exception {
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/BasicWorkbook.xls" );
        final RuleSheetListener listener = getRuleSheetListener( stream );

        final Package ruleset = listener.getRuleSet();
        assertNotNull( ruleset );

        final Rule firstRule = (Rule) ruleset.getRules().get( 0 );
        assertNotNull( firstRule.getSalience() );
        assertTrue( Integer.parseInt( firstRule.getSalience() ) > 0 );

        // System.out.println(ruleset.toXML());

        assertEquals( "myruleset", ruleset.getName() );
        assertEquals( 3, ruleset.getImports().size() );
        assertEquals( 6, ruleset.getRules().size() );

        // check imports
        Import imp = (Import) ruleset.getImports().get( 0 );
        assertEquals( "blah.class1", imp.getClassName() );
        imp = (Import) ruleset.getImports().get( 1 );
        assertEquals( "blah.class2", imp.getClassName() );
        imp = (Import) ruleset.getImports().get( 2 );
        assertEquals( "lah.di.dah", imp.getClassName() );

        // check rules
        Rule rule = (Rule) ruleset.getRules().get( 0 );
        Condition cond = (Condition) rule.getConditions().get( 0 );
        assertEquals( "Foo(myObject.getColour().equals(red), myObject.size () > 12\\\")",
                cond.getSnippet() );

        Consequence cons = (Consequence) rule.getConsequences().get( 0 );
        assertNotNull( cons );
        assertEquals( "myObject.setIsValid(Y);", cons.getSnippet() );

        rule = (Rule) ruleset.getRules().get( 5 );
        cond = (Condition) rule.getConditions().get( 1 );
        assertEquals( "myObject.size () > 7", cond.getSnippet() );
        cons = (Consequence) rule.getConsequences().get( 0 );
        assertEquals( "myObject.setIsValid(10-Jul-1974)", cons.getSnippet() );

    }

    private RuleSheetListener listener;
    private int row;

    private void makeRuleSet(){
        listener = new DefaultRuleSheetListener();
        listener.startSheet( "bad_sheet" );
        row = 1;
        listener.newRow( row, 2 );
        listener.newCell( row, 1, "RuleSet",  DataListener.NON_MERGED );
        listener.newCell( row, 2, "myRuleSet", DataListener.NON_MERGED );
    }

    private void makeAttribute( String key, String val){
        row++;
        listener.newRow( row, 2 );
        listener.newCell( row, 1, key,  DataListener.NON_MERGED );
        listener.newCell( row, 2, val,  DataListener.NON_MERGED );
    }

    private void makeRuleTable(){
        listener.newRow( 10, 1 );
        listener.newCell(10, 1, "RuleTable myRuleTable",  DataListener.NON_MERGED );
    }

    private void makeRow( int row, String... values ) throws DecisionTableParseException {
        listener.newRow( row, values.length );
        for( int i = 0; i < values.length; i++ ){
            if( values[i] != null ){
                listener.newCell( row, i+1, values[i],  DataListener.NON_MERGED );
            }
        }
    }

    /**
     * Duplications of several columns are not permitted: NO-LOOP/U.
     */
    @Test
    public void testTooManyColumnsNoLoop() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow( 11, "C", "C", "A", "U", "U" );
            listener.finishSheet();
            fail( "should have failed" );
        } catch( DecisionTableParseException e ) {
            String badCell = RuleSheetParserUtil.rc2name( 11, 5 );
            System.err.println( e.getMessage() );
            assertTrue( e.getMessage().contains( badCell ) );
        }
    }

    /**
     * Duplications of several columns are not permitted : PRIORITY/P.
     */
    @Test
    public void testTooManyColumnsPriority() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow( 11, "C", "C", "A", "PRIORITY", "P" );
            listener.finishSheet();
            fail( "should have failed" );
        } catch( DecisionTableParseException e ) {
            String badCell = RuleSheetParserUtil.rc2name( 11, 5 );
            System.err.println( e.getMessage() );
            assertTrue( e.getMessage().contains( badCell ) );
        }
    }

    /**
     * Column headers must be valid.
     */
    @Test
    public void testBadColumnHeader() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow( 11, "Condition", "CONDITION", "A", "BLURB", "P" );
            listener.finishSheet();
            fail( "should have failed" );
        } catch( DecisionTableParseException e ) {
            String badCell = RuleSheetParserUtil.rc2name( 11, 4 );
            System.err.println( e.getMessage() );
            assertTrue( e.getMessage().contains( badCell ) );
        }
    }

    /**
     * Must have a type for pattern below a condition, not a snippet.
     */
    @Test
    public void testMissingCondition() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow( 11, "C",   "C", "C",   "A", "A" );
            makeRow( 12, "attr == $param", "attr == $param", "attr == $param", "action();", "action();" );
            listener.finishSheet();
            fail( "should have failed" );
        } catch( DecisionTableParseException e ) {
            String badCell = RuleSheetParserUtil.rc2name( 12, 1 );
            System.err.println( e.getMessage() );
            assertTrue( e.getMessage().contains( badCell ) );
        }
    }

    /**
     * Must have a code snippet in a condition.
     */
    @Test
    public void testMissingCodeSnippetCondition() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow( 11, "C",              "C",              "C",   "A",         "A" );
            makeRow( 12, "Foo",            "Foo",            "Foo" );
            makeRow( 13, "attr == $param", "attr == $param", "",    "action();", "action();" );
            makeRow( 15, "1",              "2",              "3",   "",          "" );
            listener.finishSheet();
            fail( "should have failed" );
        } catch( DecisionTableParseException e ) {
            String badCell = RuleSheetParserUtil.rc2name( 13, 3 );
            System.err.println( e.getMessage() );
            assertTrue( e.getMessage().contains( badCell ) );
        }
    }

    /**
     * Spurious code snippet.
     */
    @Test
    public void testSpuriousCodeSnippet() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow( 11, "C",              "C",              "A" );
            makeRow( 12, "Foo",            "Foo" );
            makeRow( 13, "attr == $param", "attr == $param", "action();", "attr > $param" );
            makeRow( 15, "1",              "2",              "" );
            listener.finishSheet();
            fail( "should have failed" );
        } catch( DecisionTableParseException e ) {
            String badCell = RuleSheetParserUtil.rc2name( 13, 4 );
            System.err.println( e.getMessage() );
            assertTrue( e.getMessage().contains( badCell ) );
        }
    }

    /**
     * Incorrect priority - not numeric
     */
    @Test
    public void testIncorrectPriority() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow( 11, "C",              "A",         "P" );
            makeRow( 12, "Foo",            "Foo"            );
            makeRow( 13, "attr == $param", "x"              );
            makeRow( 15, "1",              "show()",   "12E" );
            listener.finishSheet();
            fail( "should have failed" );
        } catch( DecisionTableParseException e ) {
            String badCell = RuleSheetParserUtil.rc2name( 15, 3 );
            System.err.println( e.getMessage() );
            assertTrue( e.getMessage().contains( badCell ) );
        }
    }

    /**
     * Must not have snippet for attribute
     */
    @Test
    public void testSnippetForAttribute() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow( 11, "C",              "A",         "G" );
            makeRow( 12, "Foo",            "Foo"            );
            makeRow( 13, "attr == $param", "x",       "XXX" );
            makeRow( 15, "1",              "show()",   "10" );
            listener.finishSheet();
            fail( "should have failed" );
        } catch( DecisionTableParseException e ) {
            String badCell = RuleSheetParserUtil.rc2name( 13, 3 );
            System.err.println( e.getMessage() );
            assertTrue( e.getMessage().contains( badCell ) );
        }
    }

    /**
     * Check correct rendering of string-valued attribute
     */
    @Test
    public void testRuleAttributeRendering() {
        makeRuleSet();
        makeRuleTable();
        makeRow( 11, "C",              "A",         "G"     );
        makeRow( 12, "Foo",            "Foo"                );
        makeRow( 13, "attr == $param", "x"                  );
        makeRow( 15, "1",              "show()",   "foo bar" );
        makeRow( 16, "2",              "list()",   "\"10\" group\"" );
        listener.finishSheet();
        Package p = listener.getRuleSet();
        DRLOutput dout = new DRLOutput();
        p.renderDRL(dout);
        String drl = dout.getDRL();
        // System.out.println( drl );
        assertTrue( drl.contains( "agenda-group \"foo bar\"" ) );
        assertTrue( drl.contains( "agenda-group \"10\\\" group\"" ) );
    }

    /**
     * Duplicate package level attribute
     */
    @Test
    public void testDuplicatePackageAttribute() {
        try {
            makeRuleSet();
            makeAttribute( "agenda-group", "agroup" );  // B3, C3
            makeAttribute( "agenda-group", "bgroup" );  // B3. B4
            makeRuleTable();
            makeRow( 11, "C",              "A",         "P" );
            makeRow( 12, "Foo",            "Foo"            );
            makeRow( 13, "attr == $param", "x"              );
            makeRow( 15, "1",              "show()",   "10" );
            listener.finishSheet();
            Package p = listener.getRuleSet();
            DRLOutput dout = new DRLOutput();
            p.renderDRL(dout);
            String drl = dout.getDRL();
            // System.out.println( drl );
            fail( "should have failed" );
        } catch( DecisionTableParseException e ) {
            System.err.println( e.getMessage() );
            assertTrue( e.getMessage().contains( "C3, C4" ) );
        }
    }

    /**
     * Check correct rendering of package level attributes
     */
    @Test
    public void testPackageAttributeRendering() {
        makeRuleSet();
        makeAttribute( "NO-LOOP", "true" );
        makeAttribute( "agenda-group", "agroup" );
        makeRuleTable();
        makeRow( 11, "C",              "A",         "P"     );
        makeRow( 12, "foo:Foo",        "foo"                );
        makeRow( 13, "attr == $param", "x($param)"          );
        makeRow( 15, "1",              "1",         "100"   );
        listener.finishSheet();
        Package p = listener.getRuleSet();
        DRLOutput dout = new DRLOutput();
        p.renderDRL(dout);
        String drl = dout.getDRL();
        // System.out.println( drl );
        assertTrue( drl.contains( "no-loop true" ) );
        assertTrue( drl.contains( "agenda-group \"agroup\"" ) );
    }

    /**
     * Must have a code snippet in an action.
     */
    @Test
    public void testMissingCodeSnippetAction() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow( 11, "C",              "A"          );
            makeRow( 12, "foo: Foo",       "Bar()"          );
            makeRow( 13, "attr == $param"  );
            makeRow( 15, "1",              "1"          );
            makeRow( 16, "2",              "2"          );
            listener.finishSheet();
            Package p = listener.getRuleSet();
            DRLOutput dout = new DRLOutput();
            p.renderDRL(dout);
            String drl = dout.getDRL();
            System.out.println( drl );
            fail( "should have failed" );
        } catch( DecisionTableParseException e ) {
            String badCell = RuleSheetParserUtil.rc2name( 13, 2 );
            System.err.println( e.getMessage() );
            assertTrue( e.getMessage().contains( badCell ) );
        }
    }

    @Test
    public void testMetadata() {
        makeRuleSet();
        makeRuleTable();
        makeRow( 11, "C",              "A",              "@",              "@"  );
        makeRow( 12, "foo: Foo",       "foo"          );
        makeRow( 13, "attr == $param", "goaway($param)", "Author($param)", "Version($1-$2)" );
        makeRow( 15, "1",              "1",              "J.W.Goethe",     "3,14"       );
        makeRow( 16, "2",              "2",              "",               ""       );
        listener.finishSheet();
        Package p = listener.getRuleSet();
        DRLOutput dout = new DRLOutput();
        p.renderDRL(dout);
        String drl = dout.getDRL();

        assertTrue( drl.contains( "@Author(J.W.Goethe)" ) );
        assertTrue( drl.contains( "@Version(3-14)" ) );
        assertFalse( drl.contains( "@Author()" ) );
        assertFalse( drl.contains( "@Version(-)" ) );
    }


    /**
     * See if it can cope with odd shaped rule table, including missing
     * conditions. Also is not "sequential".
     */
    @Test
    public void testComplexWorksheetMissingConditionsInLocaleEnUs() throws Exception {
        Locale originalDefaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        doComplexWorksheetMissingConditions();
        Locale.setDefault(originalDefaultLocale);
    }

    @Test @Ignore // TODO JBRULES-2880 TIRELLI: Ignore test while we decide what to do in order to solve i18n issues
    public void testComplexWorksheetMissingConditionsInLocaleFrFr() throws Exception {
        Locale originalDefaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.FRANCE);
        doComplexWorksheetMissingConditions();
        Locale.setDefault(originalDefaultLocale);
    }

    private void doComplexWorksheetMissingConditions() throws IOException {
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/ComplexWorkbook.xls" );
        final RuleSheetListener listener = getRuleSheetListener( stream );

        final Package ruleset = listener.getRuleSet();
        assertEquals( 6, ruleset.getRules().size() );
        assertEquals( 0, ruleset.getImports().size() );

        Rule rule = (Rule) ruleset.getRules().get( 0 );
        assertEquals( 3, rule.getConditions().size() );
        assertEquals( 2, rule.getConsequences().size() );
        final Consequence cons = (Consequence) rule.getConsequences().get( 1 );
        assertEquals( "myObject.setIsValid(1, 2)", cons.getSnippet() );
        final Condition con = (Condition) rule.getConditions().get( 2 );
        assertEquals( "myObject.size() < $3.00", con.getSnippet() );

        rule = (Rule) ruleset.getRules().get( 4 );

        // this should have less conditions
        assertEquals( 1, rule.getConditions().size() );

        rule = (Rule) ruleset.getRules().get( 5 );
        assertEquals( 2, rule.getConditions().size() );
        assertEquals( 1, rule.getConsequences().size() );
    }

    /**
     * Utility method showing how to get a rule sheet listener from a stream.
     */
    public static RuleSheetListener getRuleSheetListener(final InputStream stream) throws IOException {
        return RulesheetUtil.getRuleSheetListener( stream );
    }
}
