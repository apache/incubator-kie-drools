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

import java.util.List;

import org.drools.template.model.Global;
import org.drools.template.model.Import;
import org.drools.template.parser.DecisionTableParseException;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.drools.decisiontable.parser.RuleSheetParserUtil.getImportList;
import static org.drools.decisiontable.parser.RuleSheetParserUtil.getRuleName;
import static org.drools.decisiontable.parser.RuleSheetParserUtil.getVariableList;
import static org.drools.decisiontable.parser.RuleSheetParserUtil.isStringMeaningTrue;
import static org.drools.decisiontable.parser.RuleSheetParserUtil.rc2name;

/**
 *
 * Nuff said...
 */
public class RuleSheetParserUtilTest {

    @Test
    public void testRuleName() {
        final String row = "  RuleTable       This is my rule name";
        final String result = getRuleName(row);
        
        assertThat(result).isEqualTo("This is my rule name");
    }

    /**
     * This is here as the old way was to do this.
     */
    @Ignore
    @Test
    public void testInvalidRuleName() {
        final String row = "RuleTable       This is my rule name (type class)";
        
        assertThatIllegalArgumentException().isThrownBy(() -> getRuleName(row));
    }

    @Test
    public void testIsStringMeaningTrue() {
        assertThat(isStringMeaningTrue("true")).isTrue();
        assertThat(isStringMeaningTrue("TRUE")).isTrue();
        assertThat(isStringMeaningTrue("yes")).isTrue();
        assertThat(isStringMeaningTrue("oN")).isTrue();

        assertThat(isStringMeaningTrue("no")).isFalse();
        assertThat(isStringMeaningTrue("false")).isFalse();
        assertThat(isStringMeaningTrue(null)).isFalse();
    }

    @Test
    public void getImportList_nullValue() {
        assertThat(getImportList(null)).isNotNull().isEmpty();

    }

    @Test
    public void getImportList_listOfEmptyString() {
        assertThat(getImportList(List.of(""))).isNotNull().isEmpty();

    }

    @Test
    public void getImportList_maniValues() {
        List<Import> list = getImportList(List.of("", "com.something.Yeah, com.something.No,com.something.yeah.*"));
        
        assertThat(list).hasSize(3).extracting(x -> x.getClassName()).containsExactly("com.something.Yeah", "com.something.No", "com.something.yeah.*");
    }


    @Test
    public void testListVariables() {
        List<Global> varList = getVariableList(List.of("Var1 var1, Var2 var2,Var3 var3"));
        
        assertThat(varList).isNotNull().hasSize(3).extracting(x -> x.getClassName()).containsExactly("Var1", "Var2", "Var3");
    }

    @Test
    public void testBadVariableFormat() {
        List<String> varCells = List.of("class1, object2");
        
        assertThatExceptionOfType(DecisionTableParseException.class).isThrownBy(() -> getVariableList(varCells));
    }

    @Test
    public void testRowColumnToCellNAme() {
        assertThat(rc2name(0, 0)).isEqualTo("A1");
        assertThat(rc2name(0, 10)).isEqualTo("K1");
        assertThat(rc2name(0, 42)).isEqualTo("AQ1");
        assertThat(rc2name(9, 27)).isEqualTo("AB10");
        assertThat(rc2name(99, 53)).isEqualTo("BB100");
    }
}
