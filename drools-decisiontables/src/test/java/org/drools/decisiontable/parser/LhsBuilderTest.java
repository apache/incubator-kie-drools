/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.drools.decisiontable.parser.LhsBuilder.FieldType;

import org.junit.Test;
import static org.junit.Assert.*;

public class LhsBuilderTest {

    @Test
    public void testBuildItUp() throws Exception {
        LhsBuilder builder = new LhsBuilder( 9, 1, "Person" );

        builder.addTemplate(10, 1, "age");
        builder.addTemplate(10, 2, "size != $param");
        builder.addTemplate(10, 3, "date <");

        builder.addCellValue(11, 1, "42");
        builder.addCellValue(11, 2, "20");
        builder.addCellValue(11, 3, "30");


        assertEquals("Person(age == \"42\", size != 20, date < \"30\")", builder.getResult());

        builder.clearValues();

        builder.addCellValue(12, 2, "42" );
        assertEquals("Person(size != 42)", builder.getResult());
    }

    @Test
    public void testEmptyCells() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "Person" );
        assertFalse(builder.hasValues());
    }

    @Test
    public void testClassicMode() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "" );
        builder.addTemplate( 10, 1, "Person(age < $param)");
        builder.addCellValue( 11, 1, "42" );

        assertEquals("Person(age < 42)", builder.getResult());

        builder = new LhsBuilder( 9, 3, null );
        builder.addTemplate( 10, 3, "Foo(bar == $param)");
        builder.addTemplate( 10, 4, "eval(true)");

        builder.addCellValue( 11, 3, "42" );
        builder.addCellValue( 11, 4, "Y" );

        assertEquals("Foo(bar == 42)\neval(true)", builder.getResult());
    }

    @Test
    public void testForAllAndFucntion() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "" );
        builder.addTemplate( 10, 1, "forall(&&){Foo(bar != $)}");
        builder.addCellValue( 11, 1, "42,43");
        assertEquals("Foo(bar != 42) && Foo(bar != 43)", builder.getResult());
    }

    @Test
    public void testForAllOr() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "Person" );
        builder.addTemplate( 10, 1, "forall(||){age < $}");
        builder.addCellValue( 11, 1, "42");
        assertEquals("Person(age < 42)", builder.getResult());
    }

    @Test
    public void testForAllOrPrefix() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "Person" );
        builder.addTemplate( 10, 1, "age < 10 && forall(||){age < $}");
        builder.addCellValue( 11, 1, "42");
        assertEquals("Person(age < 10 && age < 42)", builder.getResult());
    }

    @Test
    public void testForAllOrCSV() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "Person" );
        builder.addTemplate( 10, 1, "forall(||){age < $}");
        builder.addCellValue( 11, 1, "42, 43, 44");
        assertEquals("Person(age < 42 || age < 43 || age < 44)", builder.getResult());
    }

    @Test
    public void testForAllAnd() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "Person" );
        builder.addTemplate(10, 1, "forall(&&){age < $}");
        builder.addCellValue(11, 1, "42");
        assertEquals("Person(age < 42)", builder.getResult());
    }

    @Test
    public void testForAllAndCSV() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "Person" );
        builder.addTemplate(10, 1, "forall(&&){age < $}");
        builder.addCellValue(11, 1, "42, 43, 44");
        assertEquals("Person(age < 42 && age < 43 && age < 44)", builder
                .getResult());
    }

    @Test
    public void testForAllAndForAllOrCSVMultiple() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "Person" );
        builder.addTemplate(10, 1, "forall(&&){age < $ || age == $}");
        builder.addCellValue(11, 1, "42, 43, 44");
        assertEquals(
                "Person(age < 42 || age == 42 && age < 43 || age == 43 && age < 44 || age == 44)",
                builder.getResult());
    }

    @Test
    public void testForAllsAndForAllOrCSVMultiple() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "Person" );
        builder.addTemplate(10, 1, "forall(&&){age < $ || age == $} && forall(&&){age < $ || age == $}");
        builder.addCellValue(11, 1, "42, 43, 44");
        assertEquals(
                "Person(age < 42 || age == 42 && age < 43 || age == 43 && age < 44 || age == 44 && age < 42 || age == 42 && age < 43 || age == 43 && age < 44 || age == 44)",
                builder.getResult());
    }

    @Test
    public void testIdentifyFieldTypes() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "" );
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("age"));
        assertEquals(FieldType.OPERATOR_FIELD, builder.calcFieldType("age <"));
        assertEquals(FieldType.NORMAL_FIELD, builder.calcFieldType("age < $param"));
        assertEquals(FieldType.FORALL_FIELD, builder.calcFieldType("forall(||){age < $}"));
        assertEquals(FieldType.FORALL_FIELD, builder.calcFieldType("forall(&&){age < $}"));
        assertEquals(FieldType.FORALL_FIELD, builder.calcFieldType("forall(,){age < $}"));
        assertEquals(FieldType.FORALL_FIELD, builder.calcFieldType("forall(){age < $}"));
        assertEquals(FieldType.FORALL_FIELD, builder.calcFieldType("forall(){age < $} && forall(){age == $}"));
        assertEquals(FieldType.FORALL_FIELD, builder.calcFieldType("x && forall(){age < $} && forall(){age == $}"));
        assertEquals(FieldType.FORALL_FIELD, builder.calcFieldType("x && forall(){age < $} && forall(){age == $} && y"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("age < $para"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall{||}{age < $}"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall(){}"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall(){age < $"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall(){,"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall({})"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall({}){test})"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall(&&){{}})"));
        assertEquals(FieldType.SINGLE_FIELD, builder.calcFieldType("forall(&&){{})"));
    }

    @Test
    public void testIdentifyColumnCorrectly() {
        LhsBuilder builder = new LhsBuilder( 9, 1, null );
        assertFalse(builder.isMultipleConstraints());

        //will be added to Foo
        builder = new LhsBuilder( 9, 1, "Foo" );
        assertTrue(builder.isMultipleConstraints());

        //will be added to eval
        builder = new LhsBuilder( 9, 1, "f:Foo() eval  " );
        assertTrue(builder.isMultipleConstraints());

        // will just be verbatim
        builder = new LhsBuilder( 9, 1, "f: Foo()" );
        assertTrue(builder.isMultipleConstraints());
    }

    @Test
    public void testTypeConst3() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "Type" );
        builder.addTemplate( 10, 1, "flda");
        builder.addTemplate( 10, 2, "fldb >");
        builder.addTemplate( 10, 3, "fldc str[startsWith]");
        builder.addCellValue( 11, 1, "good");
        builder.addCellValue( 11, 2, "42");
        builder.addCellValue( 11, 3, "abc");
        assertEquals("Type(flda == \"good\", fldb > \"42\", fldc str[startsWith] \"abc\")", builder.getResult());
    }

    @Test
    public void testTypeParConst2() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "Type()" );
        builder.addTemplate( 10, 1, "flda");
        builder.addTemplate( 10, 2, "fldb >");
        builder.addCellValue( 11, 1, "good");
        builder.addCellValue( 11, 2, "42");
        assertEquals("Type(flda == \"good\", fldb > \"42\")", builder.getResult());
    }

    @Test
    public void testTypeConstFrom() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "Type from $west" );
        builder.addTemplate( 10, 1, "flda");
        builder.addCellValue( 11, 1, "good");
        assertEquals("Type(flda == \"good\") from $west", builder.getResult());
    }

    @Test
    public void testTypeEvalExp2() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "Type($a:a,$b:b) eval" );
        builder.addTemplate( 10, 1, "$a > $param");
        builder.addTemplate( 10, 2, "$b < $param");
        builder.addCellValue( 11, 1, "1");
        builder.addCellValue( 11, 2, "99");
        assertEquals("Type($a:a,$b:b) eval($a > 1 && $b < 99)", builder.getResult());
    }

    @Test
    public void testEvalExp2() {
        LhsBuilder builder = new LhsBuilder( 9, 1, "eval()" );
        builder.addTemplate( 10, 1, "$a > $param");
        builder.addTemplate( 10, 2, "$b < $param");
        builder.addCellValue( 11, 1, "1");
        builder.addCellValue( 11, 2, "99");
        assertEquals("eval($a > 1 && $b < 99)", builder.getResult());
    }

    @Test
    public void testTypeParPlain() {
        LhsBuilder builder = new LhsBuilder( 9, 1, null );
        builder.addTemplate( 10, 1, "Type()");
        builder.addCellValue( 11, 1, "x");
        assertEquals("Type()", builder.getResult());
    }
}
