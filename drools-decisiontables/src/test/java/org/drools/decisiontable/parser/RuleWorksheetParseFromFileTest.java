/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.decisiontable.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.drools.decisiontable.parser.xls.PropertiesSheetListener.CaseInsensitiveMap;
import org.drools.template.model.Condition;
import org.drools.template.model.Consequence;
import org.drools.template.model.Package;
import org.drools.template.model.Rule;
import org.drools.template.parser.DecisionTableParseException;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 *
 * Test an excel file.
 * 
 * Assumes it has a sheet called "Decision Tables" with a rule table identified
 * by a "RuleTable" cell
 */
public class RuleWorksheetParseFromFileTest {

    private RuleSheetListener listener;
    private InputStream stream;
    private Package ruleset;

    @Test
    public void testBasicWorkbookProperties() throws Exception {
        stream = RuleWorksheetParseFromFileTest.class.getResourceAsStream("/data/BasicWorkbook.drl.xls");
        listener = RulesheetUtil.getRuleSheetListener(stream);

        final CaseInsensitiveMap props = listener.getProperties();
        assertThat(props).isNotNull();
        assertThat(props.getSingleProperty("RuleSet")).isEqualTo("data");
        assertThat(props.getSingleProperty("misc")).isEqualTo("someMisc");
    }

    @Test
    public void testComplexWorkbookProperties() throws Exception {
        stream = RuleWorksheetParseFromFileTest.class.getResourceAsStream("/data/ComplexWorkbook.drl.xls");
        listener = RulesheetUtil.getRuleSheetListener(stream);

        final CaseInsensitiveMap props = listener.getProperties();
        assertThat(props).isNotNull();
        assertThat(props.getSingleProperty("RuleSet")).isEqualTo("data");
    }

    @Test
    public void testWorkbookParse() throws Exception {
        stream = RuleWorksheetParseFromFileTest.class.getResourceAsStream("/data/BasicWorkbook.drl.xls");
        listener = RulesheetUtil.getRuleSheetListener(stream);
        
        ruleset = listener.getRuleSet();
        assertThat(ruleset).isNotNull();

        final Rule firstRule = ruleset.getRules().get(0);
        assertThat(firstRule.getSalience()).isNotNull();
        assertThat(Integer.parseInt(firstRule.getSalience())).isGreaterThan(0);

        // System.out.println(ruleset.toXML());

        assertThat(ruleset.getName()).isEqualTo("data");
        assertThat(ruleset.getImports()).hasSize(3);
        assertThat(ruleset.getRules()).hasSize(6);

        // check imports
        assertThat(ruleset.getImports()).extracting(x -> x.getClassName()).containsExactly("blah.class1", "blah.class2", "lah.di.dah");

        // check rules
        Rule rule = ruleset.getRules().get(0);
        Condition cond = rule.getConditions().get(0);
        assertThat(cond.getSnippet()).isEqualTo("Foo(myObject.getColour().equals(red), myObject.size () > 12\\\")");

        Consequence cons = rule.getConsequences().get(0);
        assertThat(cons).isNotNull();
        assertThat(cons.getSnippet()).isEqualTo("myObject.setIsValid(Y);");

        Rule rule5 = ruleset.getRules().get(5);
        
        Condition cond5 = rule5.getConditions().get(1);
        assertThat(cond5.getSnippet()).isEqualTo("myObject.size () > 7");
        
        Consequence cons5 = rule5.getConsequences().get(0);
        assertThat(cons5.getSnippet()).isEqualTo("myObject.setIsValid(10-Jul-1974)");
    }



    @Test
    public void testQuoteEscapingEnabled() throws Exception {
        stream = RuleWorksheetParseFromFileTest.class.getResourceAsStream("/data/QuoteEscapeEnabledWorkbook.drl.xls");
        listener = RulesheetUtil.getRuleSheetListener(stream);

        ruleset = listener.getRuleSet();
        assertThat(ruleset).isNotNull();
        
        // check rules
        Rule rule = ruleset.getRules().get(0);
        Condition cond = rule.getConditions().get(0);
        assertThat(cond.getSnippet()).isEqualTo("Foo(myObject.getColour().equals(red), myObject.size () > 12\\\")");
    }

    @Test
    public void testQuoteEscapingDisabled() throws Exception {
        stream = RuleWorksheetParseFromFileTest.class.getResourceAsStream("/data/QuoteEscapeDisabledWorkbook.drl.xls");
        listener = RulesheetUtil.getRuleSheetListener(stream);

        ruleset = listener.getRuleSet();
        assertThat(ruleset).isNotNull();
        
        // check rules
        Rule rule = ruleset.getRules().get(0);
        Condition cond = rule.getConditions().get(0);
        assertThat(cond.getSnippet()).isEqualTo("Foo(myObject.getColour().equals(red), myObject.size () > \"12\")");
        
        Rule rule1 = ruleset.getRules().get(1);
        Condition cond1 = rule1.getConditions().get(0);
        assertThat(cond1.getSnippet()).isEqualTo("Foo(myObject.getColour().equals(blue), myObject.size () > 12\")");
    }

    @Test
    public void testSalienceRange() throws Exception {
        // DROOLS-1225
        stream = RuleWorksheetParseFromFileTest.class.getResourceAsStream("/data/SalienceRangeWorkbook.drl.xls");
        listener = RulesheetUtil.getRuleSheetListener(stream);

        ruleset = listener.getRuleSet();
        assertThat(ruleset).isNotNull();

        // check rules
        List<Rule> rules = ruleset.getRules();
        assertThat(rules.get(0).getSalience()).isEqualTo("10000");
        assertThat(rules.get(1).getSalience()).isEqualTo("9999");
    }

    @Test
    public void testSalienceOutOfRange() throws Exception {
        // DROOLS-1225
        stream = RuleWorksheetParseFromFileTest.class.getResourceAsStream("/data/SalienceOutOfRangeWorkbook.drl.xls");
        
        assertThatExceptionOfType(DecisionTableParseException.class).isThrownBy(() -> RulesheetUtil.getRuleSheetListener(stream));
    }

    /**
     * See if it can cope with odd shaped rule table, including missing
     * conditions. Also is not "sequential".
     */
    @Test
    public void testComplexWorksheetMissingConditionsInLocaleEnUs() throws Exception {
        Locale originalDefaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        try {
            doComplexWorksheetMissingConditions();
        } finally {
            Locale.setDefault(originalDefaultLocale);
        }
    }

    @Test @Ignore // TODO JBRULES-2880 TIRELLI: Ignore test while we decide what to do in order to solve i18n issues
    public void testComplexWorksheetMissingConditionsInLocaleFrFr() throws Exception {
        Locale originalDefaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.FRANCE);
        try {
            doComplexWorksheetMissingConditions();
        } finally {
            Locale.setDefault(originalDefaultLocale);
        }
    }

    private void doComplexWorksheetMissingConditions() throws IOException {
        stream = RuleWorksheetParseFromFileTest.class.getResourceAsStream("/data/ComplexWorkbook.drl.xls");
        listener = RulesheetUtil.getRuleSheetListener(stream);

        ruleset = listener.getRuleSet();
        assertThat(ruleset.getRules()).hasSize(6);
        assertThat(ruleset.getImports()).hasSize(0);

        Rule rule = ruleset.getRules().get(0);
        assertThat(rule.getConditions()).hasSize(3);
        assertThat(rule.getConsequences()).hasSize(2);
        
        final Consequence cons = rule.getConsequences().get(1);
        assertThat(cons.getSnippet()).isEqualTo("myObject.setIsValid(1, 2)");
        
        final Condition con = rule.getConditions().get(2);
        assertThat(con.getSnippet()).isEqualTo("myObject.size() < $3.00");

        Rule rule4 = ruleset.getRules().get(4);

        // this should have less conditions
        assertThat(rule4.getConditions()).hasSize(1);

        Rule rule5 = ruleset.getRules().get(5);
        assertThat(rule5.getConditions()).hasSize(2);
        assertThat(rule5.getConsequences()).hasSize(1);
    }

    @Test
    public void testNumericDisabled() throws Exception {
        // DROOLS-1378
        stream = RuleWorksheetParseFromFileTest.class.getResourceAsStream("/data/NumericDisabled.drl.xls");
        listener = RulesheetUtil.getRuleSheetListener(stream);

        ruleset = listener.getRuleSet();
        assertThat(ruleset).isNotNull();

        // check rules
        Rule rule = ruleset.getRules().get(0);
        Condition cond = rule.getConditions().get(0);
        assertThat(cond.getSnippet()).isEqualTo("Cheese(price == 6600)");
    }

    @Test
    public void numericDisabled_timestamp() throws Exception {
        // DROOLS-7322
        stream = RuleWorksheetParseFromFileTest.class.getResourceAsStream("/data/NumericDisabledForTimestamp.drl.xls");
        listener = RulesheetUtil.getRuleSheetListener(stream);

        ruleset = listener.getRuleSet();
        assertThat(ruleset).isNotNull();

        // check rules
        Rule rule = ruleset.getRules().get(0);
        Consequence cond = rule.getConsequences().get(1);
        assertThat(cond.getSnippet()).containsIgnoringWhitespaces("value = \"00:00:00\"");
    }

   

}
