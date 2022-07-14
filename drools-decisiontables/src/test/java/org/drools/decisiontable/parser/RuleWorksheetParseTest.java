/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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
import java.util.List;
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
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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
        assertThat(props).isNotNull();
        assertThat(props.getSingleProperty("RuleSet")).isEqualTo("data");
        assertThat(props.getSingleProperty("misc")).isEqualTo("someMisc");
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
        assertThat(props).isNotNull();
        final String ruleSetName = props.getSingleProperty( "RuleSet" );
        assertThat(ruleSetName).isEqualTo("data");
    }

    @Test
    public void testWorkbookParse() throws Exception {
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/BasicWorkbook.xls" );
        final RuleSheetListener listener = getRuleSheetListener( stream );

        final Package ruleset = listener.getRuleSet();
        assertThat(ruleset).isNotNull();

        final Rule firstRule = (Rule) ruleset.getRules().get( 0 );
        assertThat(firstRule.getSalience()).isNotNull();
        assertThat(Integer.parseInt(firstRule.getSalience()) > 0).isTrue();

        // System.out.println(ruleset.toXML());

        assertThat(ruleset.getName()).isEqualTo("data");
        assertThat(ruleset.getImports().size()).isEqualTo(3);
        assertThat(ruleset.getRules().size()).isEqualTo(6);

        // check imports
        Import imp = (Import) ruleset.getImports().get( 0 );
        assertThat(imp.getClassName()).isEqualTo("blah.class1");
        imp = (Import) ruleset.getImports().get( 1 );
        assertThat(imp.getClassName()).isEqualTo("blah.class2");
        imp = (Import) ruleset.getImports().get( 2 );
        assertThat(imp.getClassName()).isEqualTo("lah.di.dah");

        // check rules
        Rule rule = (Rule) ruleset.getRules().get( 0 );
        Condition cond = (Condition) rule.getConditions().get( 0 );
        assertThat(cond.getSnippet()).isEqualTo("Foo(myObject.getColour().equals(colors.get(\"red\")), myObject.size () > 12\\\")");

        Consequence cons = (Consequence) rule.getConsequences().get( 0 );
        assertThat(cons).isNotNull();
        assertThat(cons.getSnippet()).isEqualTo("myObject.setIsValid(Y);");

        rule = (Rule) ruleset.getRules().get( 5 );
        cond = (Condition) rule.getConditions().get( 1 );
        assertThat(cond.getSnippet()).isEqualTo("myObject.size () > 7");
        cons = (Consequence) rule.getConsequences().get( 0 );
        assertThat(cons.getSnippet()).isEqualTo("myObject.setIsValid(10-Jul-1974)");

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
            assertThat(e.getMessage().contains(badCell)).isTrue();
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
            assertThat(e.getMessage().contains(badCell)).isTrue();
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
            makeRow( 11, "Condition", "CONDITION", "A", "SMURF", "P" );
            listener.finishSheet();
            fail( "should have failed" );
        } catch( DecisionTableParseException e ) {
            String badCell = RuleSheetParserUtil.rc2name( 11, 4 );
            System.err.println( e.getMessage() );
            assertThat(e.getMessage().contains(badCell)).isTrue();
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
            assertThat(e.getMessage().contains(badCell)).isTrue();
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
            assertThat(e.getMessage().contains(badCell)).isTrue();
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
            assertThat(e.getMessage().contains(badCell)).isTrue();
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
            assertThat(e.getMessage().contains(badCell)).isTrue();
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
        assertThat(drl.contains("agenda-group \"foo bar\"")).isTrue();
        assertThat(drl.contains("agenda-group \"10\\\" group\"")).isTrue();
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
            assertThat(e.getMessage().contains("C3, C4")).isTrue();
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
        assertThat(drl.contains("no-loop true")).isTrue();
        assertThat(drl.contains("agenda-group \"agroup\"")).isTrue();
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
            assertThat(e.getMessage().contains(badCell)).isTrue();
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

        assertThat(drl.contains("@Author(J.W.Goethe)")).isTrue();
        assertThat(drl.contains("@Version(3-14)")).isTrue();
        assertThat(drl.contains("@Author()")).isFalse();
        assertThat(drl.contains("@Version(-)")).isFalse();
    }

    @Test
    public void testQuoteEscapingEnabled() throws Exception {
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/QuoteEscapeEnabledWorkbook.xls" );
        final RuleSheetListener listener = getRuleSheetListener( stream );

        final Package ruleset = listener.getRuleSet();
        assertThat(ruleset).isNotNull();
        DRLOutput dout = new DRLOutput();
        ruleset.renderDRL(dout);
        String drl = dout.getDRL();
        System.out.println(drl);
        
        // check rules
        Rule rule = ruleset.getRules().get( 0 );
        Condition cond = rule.getConditions().get( 0 );
        assertThat(cond.getSnippet()).isEqualTo("Foo(myObject.getColour().equals(red), myObject.size () > 12\\\")");
    }

    @Test
    public void testQuoteEscapingDisabled() throws Exception {
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/QuoteEscapeDisabledWorkbook.xls" );
        final RuleSheetListener listener = getRuleSheetListener( stream );

        final Package ruleset = listener.getRuleSet();
        assertThat(ruleset).isNotNull();
        DRLOutput dout = new DRLOutput();
        ruleset.renderDRL(dout);
        String drl = dout.getDRL();
        System.out.println(drl);
        
        // check rules
        Rule rule = (Rule) ruleset.getRules().get( 0 );
        Condition cond = (Condition) rule.getConditions().get( 0 );
        assertThat(cond.getSnippet()).isEqualTo("Foo(myObject.getColour().equals(red), myObject.size () > \"12\")");
        rule = ruleset.getRules().get( 1 );
        cond = rule.getConditions().get( 0 );
        assertThat(cond.getSnippet()).isEqualTo("Foo(myObject.getColour().equals(blue), myObject.size () > 12\")");
    }

    @Test
    public void testSalienceRange() throws Exception {
        // DROOLS-1225
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/SalienceRangeWorkbook.xls" );
        final RuleSheetListener listener = getRuleSheetListener( stream );

        final Package ruleset = listener.getRuleSet();
        assertThat(ruleset).isNotNull();
        DRLOutput dout = new DRLOutput();
        ruleset.renderDRL(dout);
        String drl = dout.getDRL();
        System.out.println(drl);

        // check rules
        List<Rule> rules = ruleset.getRules();
        assertThat(rules.get(0).getSalience()).isEqualTo("10000");
        assertThat(rules.get(1).getSalience()).isEqualTo("9999");
    }

    @Test
    public void testSalienceOutOfRange() throws Exception {
        // DROOLS-1225
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/SalienceOutOfRangeWorkbook.xls" );
        try {
            final RuleSheetListener listener = getRuleSheetListener( stream );
            fail( "should have failed" );
        } catch (DecisionTableParseException e) { }
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
        assertThat(ruleset.getRules().size()).isEqualTo(6);
        assertThat(ruleset.getImports().size()).isEqualTo(0);

        Rule rule = (Rule) ruleset.getRules().get( 0 );
        assertThat(rule.getConditions().size()).isEqualTo(3);
        assertThat(rule.getConsequences().size()).isEqualTo(2);
        final Consequence cons = (Consequence) rule.getConsequences().get( 1 );
        assertThat(cons.getSnippet()).isEqualTo("myObject.setIsValid(1, 2)");
        final Condition con = (Condition) rule.getConditions().get( 2 );
        assertThat(con.getSnippet()).isEqualTo("myObject.size() < $3.00");

        rule = (Rule) ruleset.getRules().get( 4 );

        // this should have less conditions
        assertThat(rule.getConditions().size()).isEqualTo(1);

        rule = (Rule) ruleset.getRules().get( 5 );
        assertThat(rule.getConditions().size()).isEqualTo(2);
        assertThat(rule.getConsequences().size()).isEqualTo(1);
    }

    @Test
    public void testNumericDisabled() throws Exception {
        // DROOLS-1378
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream( "/data/NumericDisabled.xls" );
        final RuleSheetListener listener = getRuleSheetListener( stream );

        final Package ruleset = listener.getRuleSet();
        assertThat(ruleset).isNotNull();
        DRLOutput dout = new DRLOutput();
        ruleset.renderDRL( dout );
        String drl = dout.getDRL();
        System.out.println( drl );

        // check rules
        Rule rule = (Rule) ruleset.getRules().get( 0 );
        Condition cond = (Condition) rule.getConditions().get( 0 );
        assertThat(cond.getSnippet()).isEqualTo("Cheese(price == 6600)");
    }

    /**
     * Utility method showing how to get a rule sheet listener from a stream.
     */
    public static RuleSheetListener getRuleSheetListener(final InputStream stream) throws IOException {
        return RulesheetUtil.getRuleSheetListener( stream );
    }
}
