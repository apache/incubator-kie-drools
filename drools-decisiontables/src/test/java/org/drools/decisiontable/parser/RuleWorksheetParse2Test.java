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

import org.drools.template.model.DRLOutput;
import org.drools.template.model.Package;
import org.drools.template.parser.DataListener;
import org.drools.template.parser.DecisionTableParseException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.drools.decisiontable.parser.RuleSheetParserUtil.rc2name;

/**
 *
 * Test an excel file.
 * 
 * Assumes it has a sheet called "Decision Tables" with a rule table identified
 * by a "RuleTable" cell
 */
public class RuleWorksheetParse2Test {
    
    private RuleSheetListener listener;
    private int row;

    /**
     * Duplications of several columns are not permitted: NO-LOOP/U.
     */
    @Test
    public void tooManyColumnsNoLoop() {
        makeRuleSet();
        makeRuleTable();
            
        assertThatException().isThrownBy(() -> makeRow(11, "C", "C", "A", "U", "U")).withMessageContaining(rc2name(11, 5));
    }

    /**
     * Duplications of several columns are not permitted : PRIORITY/P.
     */
    @Test
    public void tooManyColumnsPriority() {
        makeRuleSet();
        makeRuleTable();
            
        assertThatException().isThrownBy(() -> makeRow(11, "C", "C", "A", "PRIORITY", "P")).withMessageContaining(rc2name(11, 5));
    }

    /**
     * Column headers must be valid.
     */
    @Test
    public void columnsHeadersMustBeValid() {
        makeRuleSet();
        makeRuleTable();

        assertThatException().isThrownBy(() -> makeRow(11, "Condition", "CONDITION", "A", "SMURF", "P")).withMessageContaining(rc2name(11, 4));
    }

    /**
     * Must have a type for pattern below a condition, not a snippet.
     */
    @Test
    public void mustHaveATypeForAPatternBelowACondition() {
        makeRuleSet();
        makeRuleTable();
        makeRow(11, "C",   "C", "C",   "A", "A");

        assertThatException().isThrownBy(() -> makeRow(12, "attr == $param", "attr == $param", "attr == $param", "action();", "action();"))
            .withMessageContaining(rc2name(12, 1));
    }

    /**
     * Spurious code snippet.
     */
    @Test
    public void spuriousCodeSnippet() {
        makeRuleSet();
        makeRuleTable();
        makeRow(11, "C",              "C",              "A");
        makeRow(12, "Foo",            "Foo");
        assertThatException().isThrownBy(() -> makeRow(13, "attr == $param", "attr == $param", "action();", "attr > $param"))
        .withMessageContaining(rc2name(13, 4));
    }

    /**
     * Incorrect priority - not numeric
     */
    @Test
    public void incorrectNonNumericPriority() {
        makeRuleSet();
        makeRuleTable();
        makeRow(11, "C",              "A",         "P");
        makeRow(12, "Foo",            "Foo");
        makeRow(13, "attr == $param", "x");
        
        assertThatException().isThrownBy(() -> makeRow(15, "1",              "show()",   "12E")).withMessageContaining(rc2name(15, 3));   
    }

    /**
     * Must not have snippet for attribute
     */
    @Test
    public void mustNotHaveSnippetForAttribute() {
        makeRuleSet();
        makeRuleTable();
        makeRow(11, "C",              "A",         "G");
        makeRow(12, "Foo",            "Foo");
        
        assertThatException().isThrownBy(() -> makeRow(13, "attr == $param", "x",       "XXX")).withMessageContaining(rc2name(13, 3)); 
    }

    /**
     * Check correct rendering of string-valued attribute
     */
    @Test
    public void testRuleAttributeRendering() {
        makeRuleSet();
        makeRuleTable();
        makeRow(11, "C",              "A",         "G");
        makeRow(12, "Foo",            "Foo");
        makeRow(13, "attr == $param", "x");
        makeRow(15, "1",              "show()",   "foo bar");
        makeRow(16, "2",              "list()",   "\"10\" group\"");
        listener.finishSheet();
        
        Package p = listener.getRuleSet();
        DRLOutput dout = new DRLOutput();
        p.renderDRL(dout);
        String drl = dout.getDRL();
        assertThat(drl).contains("agenda-group \"foo bar\"", "agenda-group \"10\\\" group\"");
    }

    /**
     * Duplicate package level attribute
     */
    @Test
    public void packageLevelAttributesShouldNotBeDuplicated() {
        makeRuleSet();
        makeAttribute("agenda-group", "agroup");  // B3, C3
        makeAttribute("agenda-group", "bgroup");  // B3. B4
        makeRuleTable();
        makeRow(11, "C",              "A",         "P");
        makeRow(12, "Foo",            "Foo");
        makeRow(13, "attr == $param", "x");
        makeRow(15, "1",              "show()",   "10");
        listener.finishSheet();
        
        assertThatException().isThrownBy(() -> listener.getRuleSet()).withMessageContaining("C3, C4");

    }

    /**
     * Check correct rendering of package level attributes
     */
    @Test
    public void packageAttributesAreRenderedCorrectly() {
        makeRuleSet();
        makeAttribute("NO-LOOP", "true");
        makeAttribute("agenda-group", "agroup");
        makeRuleTable();
        makeRow(11, "C",              "A",         "P");
        makeRow(12, "foo:Foo",        "foo");
        makeRow(13, "attr == $param", "x($param)");
        makeRow(15, "1",              "1",         "100");
        listener.finishSheet();
        Package p = listener.getRuleSet();
        DRLOutput dout = new DRLOutput();
        p.renderDRL(dout);
        String drl = dout.getDRL();
        
        assertThat(drl).contains("no-loop true", "agenda-group \"agroup\"");
    }

    /**
     * Must have a code snippet in an action.
     */
    @Test
    public void missingCodeSnippetInAction() {
        makeRuleSet();
        makeRuleTable();
        makeRow(11, "C",              "A");
        makeRow(12, "foo: Foo",       "Bar()");
        makeRow(13, "attr == $param");
        assertThatException().isThrownBy(() -> makeRow(15, "1",              "1")).withMessageContaining(rc2name(13, 2));
    }

    @Test
    public void metadataIsCorrect() {
        makeRuleSet();
        makeRuleTable();
        makeRow(11, "C",              "A",              "@",              "@");
        makeRow(12, "foo: Foo",       "foo");
        makeRow(13, "attr == $param", "goaway($param)", "Author($param)", "Version($1-$2)");
        makeRow(15, "1",              "1",              "J.W.Goethe",     "3,14");
        makeRow(16, "2",              "2",              "",               "");
        listener.finishSheet();
        
        Package p = listener.getRuleSet();
        DRLOutput dout = new DRLOutput();
        p.renderDRL(dout);
        String drl = dout.getDRL();
        assertThat(drl).contains("@Author(J.W.Goethe)", "@Version(3-14)");
        assertThat(drl).doesNotContain("@Author()", "@Version(-)");
    }



    private void makeRuleSet(){
        listener = new DefaultRuleSheetListener();
        listener.startSheet("bad_sheet");
        row = 1;
        listener.newRow(row, 2);
        listener.newCell(row, 1, "RuleSet",  DataListener.NON_MERGED);
        listener.newCell(row, 2, "myRuleSet", DataListener.NON_MERGED);
    }

    private void makeAttribute(String key, String val){
        row++;
        listener.newRow(row, 2);
        listener.newCell(row, 1, key,  DataListener.NON_MERGED);
        listener.newCell(row, 2, val,  DataListener.NON_MERGED);
    }

    private void makeRuleTable(){
        listener.newRow(10, 1);
        listener.newCell(10, 1, "RuleTable myRuleTable",  DataListener.NON_MERGED);
    }

    private void makeRow(int row, String... values) throws DecisionTableParseException {
        listener.newRow(row, values.length);
        for(int i = 0; i < values.length; i++){
            if(values[i] != null){
                listener.newCell(row, i+1, values[i],  DataListener.NON_MERGED);
            }
        }
    }

}
