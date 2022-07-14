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

import java.util.ArrayList;
import java.util.List;

import org.drools.template.model.Global;
import org.drools.template.model.Import;
import org.drools.template.parser.DecisionTableParseException;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 *
 * Nuff said...
 */
public class RuleSheetParserUtilTest {

    @Test
    public void testRuleName() {
        final String row = "  RuleTable       This is my rule name";
        final String result = RuleSheetParserUtil.getRuleName( row );
        assertThat(result).isEqualTo("This is my rule name");
    }

    /**
     * This is here as the old way was to do this.
     */
    @Ignore
    @Test
    public void testInvalidRuleName() {
        final String row = "RuleTable       This is my rule name (type class)";
        try {
            final String result = RuleSheetParserUtil.getRuleName( row );
            fail( "should have failed, but get result: " + result );
        } catch ( final IllegalArgumentException e ) {
            assertThat(e.getMessage()).isNotNull();
        }
    }

    @Test
    public void testIsStringMeaningTrue() {
        assertThat(RuleSheetParserUtil.isStringMeaningTrue("true")).isTrue();
        assertThat(RuleSheetParserUtil.isStringMeaningTrue("TRUE")).isTrue();
        assertThat(RuleSheetParserUtil.isStringMeaningTrue("yes")).isTrue();
        assertThat(RuleSheetParserUtil.isStringMeaningTrue("oN")).isTrue();

        assertThat(RuleSheetParserUtil.isStringMeaningTrue("no")).isFalse();
        assertThat(RuleSheetParserUtil.isStringMeaningTrue("false")).isFalse();
        assertThat(RuleSheetParserUtil.isStringMeaningTrue(null)).isFalse();
    }

    @Test
    public void testListImports() {
        List<String> cellVals = null;

        List<Import> list = RuleSheetParserUtil.getImportList( cellVals );
        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(0);

        cellVals = new ArrayList<String>();
        cellVals.add( "" );
        assertThat(RuleSheetParserUtil.getImportList(cellVals).size()).isEqualTo(0);

        cellVals.add( 0, "com.something.Yeah, com.something.No,com.something.yeah.*" );
        list = RuleSheetParserUtil.getImportList( cellVals );
        assertThat(list.size()).isEqualTo(3);
        assertThat((list.get(0)).getClassName()).isEqualTo("com.something.Yeah");
        assertThat((list.get(1)).getClassName()).isEqualTo("com.something.No");
        assertThat((list.get(2)).getClassName()).isEqualTo("com.something.yeah.*");
    }

    @Test
    public void testListVariables() {
        List<String> varCells = new ArrayList<String>();
        varCells.add( "Var1 var1, Var2 var2,Var3 var3" );
        final List<Global> varList = RuleSheetParserUtil.getVariableList( varCells );
        assertThat(varList).isNotNull();
        assertThat(varList.size()).isEqualTo(3);
        Global var = varList.get( 0 );
        assertThat(var.getClassName()).isEqualTo("Var1");
        var = varList.get( 2 );
        assertThat(var.getClassName()).isEqualTo("Var3");
        assertThat(var.getIdentifier()).isEqualTo("var3");
    }

    @Test
    public void testBadVariableFormat() {
        List<String> varCells = new ArrayList<String>();
        varCells.add( "class1, object2" );
        try {
            RuleSheetParserUtil.getVariableList( varCells );
            fail( "should not work" );
        } catch ( final DecisionTableParseException e ) {
            assertThat(e.getMessage()).isNotNull();
        }
    }

    @Test
    public void testRowColumnToCellNAme() {
        String cellName = RuleSheetParserUtil.rc2name( 0, 0 );
        assertThat(cellName).isEqualTo("A1");

        cellName = RuleSheetParserUtil.rc2name( 0, 10 );
        assertThat(cellName).isEqualTo("K1");

        cellName = RuleSheetParserUtil.rc2name( 0, 42 );
        assertThat(cellName).isEqualTo("AQ1");

        cellName = RuleSheetParserUtil.rc2name( 9, 27 );
        assertThat(cellName).isEqualTo("AB10");

        cellName = RuleSheetParserUtil.rc2name( 99, 53 );
        assertThat(cellName).isEqualTo("BB100");
    }
}
