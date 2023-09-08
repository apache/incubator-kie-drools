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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.decisiontable.parser.LhsBuilder.FieldType.FORALL_FIELD;
import static org.drools.decisiontable.parser.LhsBuilder.FieldType.NORMAL_FIELD;
import static org.drools.decisiontable.parser.LhsBuilder.FieldType.OPERATOR_FIELD;
import static org.drools.decisiontable.parser.LhsBuilder.FieldType.SINGLE_FIELD;

public class LhsBuilderTest {

    private LhsBuilder builder;

    @Test
    public void testBuildItUp() throws Exception {
        builder = new LhsBuilder(9, 1, "Person");

        builder.addTemplate(10, 1, "age");
        builder.addTemplate(10, 2, "size != $param");
        builder.addTemplate(10, 3, "date <");

        builder.addCellValue(11, 1, "42");
        builder.addCellValue(11, 2, "20");
        builder.addCellValue(11, 3, "30");

        assertThat(builder.getResult()).isEqualTo("Person(age == \"42\", size != 20, date < \"30\")");

        builder.clearValues();

        builder.addCellValue(12, 2, "42");
        assertThat(builder.getResult()).isEqualTo("Person(size != 42)");
    }

    @Test
    public void testEmptyCells() {
        builder = new LhsBuilder(9, 1, "Person");
        
        assertThat(builder.hasValues()).isFalse();
    }

    @Test
    public void testClassicMode() {
        builder = new LhsBuilder(9, 1, "");
        builder.addTemplate(10, 1, "Person(age < $param)");
        builder.addCellValue(11, 1, "42");

        assertThat(builder.getResult()).isEqualTo("Person(age < 42)");

        builder = new LhsBuilder(9, 3, null);
        builder.addTemplate(10, 3, "Foo(bar == $param)");
        builder.addTemplate(10, 4, "eval(true)");

        builder.addCellValue(11, 3, "42");
        builder.addCellValue(11, 4, "Y");

        assertThat(builder.getResult()).isEqualTo("Foo(bar == 42)\neval(true)");
    }

    @Test
    public void testForAllAndFunction() {
        builder = new LhsBuilder(9, 1, "");
        builder.addTemplate(10, 1, "forall(&&){Foo(bar != $)}");
        builder.addCellValue(11, 1, "42,43");
        
        assertThat(builder.getResult()).isEqualTo("Foo(bar != 42) && Foo(bar != 43)");
    }

    @Test
    public void testForAllOr() {
        builder = new LhsBuilder(9, 1, "Person");
        builder.addTemplate(10, 1, "forall(||){age < $}");
        builder.addCellValue(11, 1, "42");
        
        assertThat(builder.getResult()).isEqualTo("Person(age < 42)");
    }

    @Test
    public void testForAllOrPrefix() {
        builder = new LhsBuilder(9, 1, "Person");
        builder.addTemplate(10, 1, "age < 10 && forall(||){age < $}");
        builder.addCellValue(11, 1, "42");
        
        assertThat(builder.getResult()).isEqualTo("Person(age < 10 && age < 42)");
    }

    @Test
    public void testForAllOrCSV() {
        builder = new LhsBuilder(9, 1, "Person");
        builder.addTemplate(10, 1, "forall(||){age < $}");
        builder.addCellValue(11, 1, "42, 43, 44");
        assertThat(builder.getResult()).isEqualTo("Person(age < 42 || age < 43 || age < 44)");
    }

    @Test
    public void testForAllAnd() {
        builder = new LhsBuilder(9, 1, "Person");
        builder.addTemplate(10, 1, "forall(&&){age < $}");
        builder.addCellValue(11, 1, "42");
        
        assertThat(builder.getResult()).isEqualTo("Person(age < 42)");
    }

    @Test
    public void testForAllAndCSV() {
        builder = new LhsBuilder(9, 1, "Person");
        builder.addTemplate(10, 1, "forall(&&){age < $}");
        builder.addCellValue(11, 1, "42, 43, 44");
        
        assertThat(builder.getResult()).isEqualTo("Person(age < 42 && age < 43 && age < 44)");
    }

    @Test
    public void testForAllAndForAllOrCSVMultiple() {
        builder = new LhsBuilder(9, 1, "Person");
        builder.addTemplate(10, 1, "forall(&&){age < $ || age == $}");
        builder.addCellValue(11, 1, "42, 43, 44");
        
        assertThat(builder.getResult()).isEqualTo("Person(age < 42 || age == 42 && age < 43 || age == 43 && age < 44 || age == 44)");
    }

    @Test
    public void testForAllsAndForAllOrCSVMultiple() {
        builder = new LhsBuilder(9, 1, "Person");
        builder.addTemplate(10, 1, "forall(&&){age < $ || age == $} && forall(&&){age < $ || age == $}");
        builder.addCellValue(11, 1, "42, 43, 44");
        
        assertThat(builder.getResult()).isEqualTo("Person(age < 42 || age == 42 && age < 43 || age == 43 && age < 44 || age == 44 && age < 42 || age == 42 && age < 43 || age == 43 && age < 44 || age == 44)");
    }

    @Test
    public void testIdentifyFieldTypes() {
        builder = new LhsBuilder(9, 1, "");
        
        assertThat(builder.calcFieldType("age")).isEqualTo(SINGLE_FIELD);
        assertThat(builder.calcFieldType("age <")).isEqualTo(OPERATOR_FIELD);
        assertThat(builder.calcFieldType("age < $param")).isEqualTo(NORMAL_FIELD);
        assertThat(builder.calcFieldType("forall(||){age < $}")).isEqualTo(FORALL_FIELD);
        assertThat(builder.calcFieldType("forall(&&){age < $}")).isEqualTo(FORALL_FIELD);
        assertThat(builder.calcFieldType("forall(,){age < $}")).isEqualTo(FORALL_FIELD);
        assertThat(builder.calcFieldType("forall(){age < $}")).isEqualTo(FORALL_FIELD);
        assertThat(builder.calcFieldType("forall(){age < $} && forall(){age == $}")).isEqualTo(FORALL_FIELD);
        assertThat(builder.calcFieldType("x && forall(){age < $} && forall(){age == $}")).isEqualTo(FORALL_FIELD);
        assertThat(builder.calcFieldType("x && forall(){age < $} && forall(){age == $} && y")).isEqualTo(FORALL_FIELD);
        assertThat(builder.calcFieldType("age < $para")).isEqualTo(SINGLE_FIELD);
        assertThat(builder.calcFieldType("forall{||}{age < $}")).isEqualTo(SINGLE_FIELD);
        assertThat(builder.calcFieldType("forall(){}")).isEqualTo(SINGLE_FIELD);
        assertThat(builder.calcFieldType("forall(){age < $")).isEqualTo(SINGLE_FIELD);
        assertThat(builder.calcFieldType("forall(){,")).isEqualTo(SINGLE_FIELD);
        assertThat(builder.calcFieldType("forall({})")).isEqualTo(SINGLE_FIELD);
        assertThat(builder.calcFieldType("forall({}){test})")).isEqualTo(SINGLE_FIELD);
        assertThat(builder.calcFieldType("forall(&&){{}})")).isEqualTo(SINGLE_FIELD);
        assertThat(builder.calcFieldType("forall(&&){{})")).isEqualTo(SINGLE_FIELD);
    }

    @Test
    public void testIdentifyColumnCorrectly() {
        builder = new LhsBuilder(9, 1, null);
        assertThat(builder.isMultipleConstraints()).isFalse();

        //will be added to Foo
        builder = new LhsBuilder(9, 1, "Foo");
        assertThat(builder.isMultipleConstraints()).isTrue();

        //will be added to eval
        builder = new LhsBuilder(9, 1, "f:Foo() eval  ");
        assertThat(builder.isMultipleConstraints()).isTrue();

        // will just be verbatim
        builder = new LhsBuilder(9, 1, "f: Foo()");
        assertThat(builder.isMultipleConstraints()).isTrue();
    }

    @Test
    public void testTypeConst3() {
        builder = new LhsBuilder(9, 1, "Type");
        builder.addTemplate(10, 1, "flda");
        builder.addTemplate(10, 2, "fldb >");
        builder.addTemplate(10, 3, "fldc str[startsWith]");
        builder.addCellValue(11, 1, "good");
        builder.addCellValue(11, 2, "42");
        builder.addCellValue(11, 3, "abc");
        
        assertThat(builder.getResult()).isEqualTo("Type(flda == \"good\", fldb > \"42\", fldc str[startsWith] \"abc\")");
    }

    @Test
    public void testTypeParConst2() {
        builder = new LhsBuilder(9, 1, "Type()");
        builder.addTemplate(10, 1, "flda");
        builder.addTemplate(10, 2, "fldb >");
        builder.addCellValue(11, 1, "good");
        builder.addCellValue(11, 2, "42");
        
        assertThat(builder.getResult()).isEqualTo("Type(flda == \"good\", fldb > \"42\")");
    }

    @Test
    public void testTypeConstFrom() {
        builder = new LhsBuilder(9, 1, "Type from $west");
        builder.addTemplate(10, 1, "flda");
        builder.addCellValue(11, 1, "good");
        
        assertThat(builder.getResult()).isEqualTo("Type(flda == \"good\") from $west");
    }

    @Test
    public void testTypeEvalExp2() {
        builder = new LhsBuilder(9, 1, "Type($a:a,$b:b) eval");
        builder.addTemplate(10, 1, "$a > $param");
        builder.addTemplate(10, 2, "$b < $param");
        builder.addCellValue(11, 1, "1");
        builder.addCellValue(11, 2, "99");
        assertThat(builder.getResult()).isEqualTo("Type($a:a,$b:b) eval($a > 1 && $b < 99)");
    }

    @Test
    public void testEvalExp2() {
        builder = new LhsBuilder(9, 1, "eval()");
        builder.addTemplate(10, 1, "$a > $param");
        builder.addTemplate(10, 2, "$b < $param");
        builder.addCellValue(11, 1, "1");
        builder.addCellValue(11, 2, "99");
        
        assertThat(builder.getResult()).isEqualTo("eval($a > 1 && $b < 99)");
    }

    @Test
    public void testTypeParPlain() {
        builder = new LhsBuilder(9, 1, null);
        builder.addTemplate(10, 1, "Type()");
        builder.addCellValue(11, 1, "x");
        
        assertThat(builder.getResult()).isEqualTo("Type()");
    }
}
