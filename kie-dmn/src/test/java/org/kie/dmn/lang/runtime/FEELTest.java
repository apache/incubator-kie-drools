/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.lang.runtime;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

public class FEELTest {

    private static final Map<String, Object> EMPTY_INPUT = Collections.EMPTY_MAP;

    @Test
    public void testConstantNull() {
        assertThat( FEEL.evaluate( "null", EMPTY_INPUT ), is( nullValue() ) );
    }

    @Test
    public void testBooleanTrue() {
        assertThat( FEEL.evaluate( "true", EMPTY_INPUT ), is( Boolean.TRUE ) );
    }

    @Test
    public void testBooleanFalse() {
        assertThat( FEEL.evaluate( "false", EMPTY_INPUT ), is( Boolean.FALSE ) );
    }

    @Test
    public void testUnaryDash() {
        // dash is an unary test that always matches, so for now, returning true.
        // have to double check to know if this is not the case
        assertThat( FEEL.evaluate( "-", EMPTY_INPUT ), is( Boolean.TRUE ) );
    }

    @Test
    public void testConstantNumber() {
        assertThat( FEEL.evaluate( "50", EMPTY_INPUT ), is( new BigDecimal( "50" ) ) );
    }

    @Test
    public void testConstantNumberNeg() {
        assertThat( FEEL.evaluate( "-50", EMPTY_INPUT ), is( new BigDecimal( "-50" ) ) );
    }

    @Test
    public void testConstantNumberFloat() {
        assertThat( FEEL.evaluate( "50.872", EMPTY_INPUT ), is( new BigDecimal( "50.872" ) ) );
    }

    @Test
    public void testConstantNumberNegFloat() {
        assertThat( FEEL.evaluate( "-50.567", EMPTY_INPUT ), is( new BigDecimal( "-50.567" ) ) );
    }

    @Test
    public void testConstantString() {
        // quotes are a syntactical markup character for strings, so they disappear when the expression is evaluated
        assertThat( FEEL.evaluate( "\"foo bar\"", EMPTY_INPUT ), is( "foo bar" ) );
    }

    @Test
    public void testConstantEmptyString() {
        // quotes are a syntactical markup character for strings, so they disappear when the expression is evaluated
        assertThat( FEEL.evaluate( "\"\"", EMPTY_INPUT ), is( "" ) );
    }

    @Test
    public void testMathExprAdd1() {
        assertThat( FEEL.evaluate( "10+5", EMPTY_INPUT ), is( BigDecimal.valueOf( 15 ) ) );
    }

    @Test
    public void testMathExprAdd2() {
        assertThat( FEEL.evaluate( "-10 + -5", EMPTY_INPUT ), is( BigDecimal.valueOf( -15 ) ) );
    }

    @Test
    public void testMathExprAdd3() {
        assertThat( FEEL.evaluate( "(-10) + (-5)", EMPTY_INPUT ), is( BigDecimal.valueOf( -15 ) ) );
    }

    @Test
    public void testMathExprSub1() {
        assertThat( FEEL.evaluate( "10-5", EMPTY_INPUT ), is( BigDecimal.valueOf( 5 ) ) );
    }

    @Test
    public void testMathExprSub2() {
        assertThat( FEEL.evaluate( "-10 - -5", EMPTY_INPUT ), is( BigDecimal.valueOf( -5 ) ) );
    }

    @Test
    public void testMathExprSub3() {
        assertThat( FEEL.evaluate( "(-10) - (-5)", EMPTY_INPUT ), is( BigDecimal.valueOf( -5 ) ) );
    }

    @Test
    public void testMathExprSub4() {
        assertThat( FEEL.evaluate( "(10 + 20) - (-5 + 3)", EMPTY_INPUT ), is( BigDecimal.valueOf( 32 ) ) );
    }

    @Test
    public void testMathExprMult1() {
        assertThat( FEEL.evaluate( "10*5", EMPTY_INPUT ), is( BigDecimal.valueOf( 50 ) ) );
    }

    @Test
    public void testMathExprMult2() {
        assertThat( FEEL.evaluate( "-10 * -5", EMPTY_INPUT ), is( BigDecimal.valueOf( 50 ) ) );
    }

    @Test
    public void testMathExprMult3() {
        assertThat( FEEL.evaluate( "(-10) * (-5)", EMPTY_INPUT ), is( BigDecimal.valueOf( 50 ) ) );
    }

    @Test
    public void testMathExprMult4() {
        assertThat( FEEL.evaluate( "(10 + 20) * (-5 * 3)", EMPTY_INPUT ), is( BigDecimal.valueOf( -450 ) ) );
    }

    @Test
    public void testMathExprDiv1() {
        assertThat( FEEL.evaluate( "10/5", EMPTY_INPUT ), is( BigDecimal.valueOf( 2 ) ) );
    }

    @Test
    public void testMathExprDiv2() {
        assertThat( FEEL.evaluate( "-10 / -5", EMPTY_INPUT ), is( BigDecimal.valueOf( 2 ) ) );
    }

    @Test
    public void testMathExprDiv3() {
        assertThat( FEEL.evaluate( "(-10) / (-5)", EMPTY_INPUT ), is( BigDecimal.valueOf( 2 ) ) );
    }

    @Test
    public void testMathExprDiv4() {
        assertThat( FEEL.evaluate( "(10 + 20) / (-5 * 3)", EMPTY_INPUT ), is( BigDecimal.valueOf( -2 ) ) );
    }

    @Test
    public void testMathExprDiv5() {
        assertThat( FEEL.evaluate( "(10 + 20) / 0", EMPTY_INPUT ), is( nullValue() ) );
    }

    @Test
    public void testMathExprPow1() {
        assertThat( FEEL.evaluate( "10 ** 5", EMPTY_INPUT ), is( BigDecimal.valueOf( 100000 ) ) );
    }

    @Test @Ignore( "Java BigDecimals do not support negative numbers as power. Need to figure out what to do." )
    public void testMathExprPow2() {
        assertThat( FEEL.evaluate( "10 ** -5", EMPTY_INPUT ), is( BigDecimal.valueOf( -0.00001 ) ) );
    }

    @Test
    public void testMathExprPow3() {
        assertThat( FEEL.evaluate( "(5+2) ** 5", EMPTY_INPUT ), is( BigDecimal.valueOf( 16807 ) ) );
    }

    @Test
    public void testMathExprPow4() {
        assertThat( FEEL.evaluate( "5+2 ** 5", EMPTY_INPUT ), is( BigDecimal.valueOf( 37 ) ) );
    }

    @Test
    public void testMathExprPow5() {
        assertThat( FEEL.evaluate( "5+2 ** 5+3", EMPTY_INPUT ), is( BigDecimal.valueOf( 40 ) ) );
    }

    @Test
    public void testMathExprPow6() {
        assertThat( FEEL.evaluate( "5+2 ** (5+3)", EMPTY_INPUT ), is( BigDecimal.valueOf( 261 ) ) );
    }

    @Test
    public void testMathExprNull1() {
        assertThat( FEEL.evaluate( "10 + null", EMPTY_INPUT ), is( nullValue() ) );
    }

    @Test
    public void testMathExprNull2() {
        assertThat( FEEL.evaluate( "null + 10", EMPTY_INPUT ), is( nullValue() ) );
    }

    @Test
    public void testMathExprNull3() {
        assertThat( FEEL.evaluate( "10 - null", EMPTY_INPUT ), is( nullValue() ) );
    }

    @Test
    public void testMathExprNull4() {
        assertThat( FEEL.evaluate( "null - 10", EMPTY_INPUT ), is( nullValue() ) );
    }

    @Test
    public void testMathExprNull5() {
        assertThat( FEEL.evaluate( "10 * null", EMPTY_INPUT ), is( nullValue() ) );
    }

    @Test
    public void testMathExprNull6() {
        assertThat( FEEL.evaluate( "null * 10", EMPTY_INPUT ), is( nullValue() ) );
    }

    @Test
    public void testMathExprNull7() {
        assertThat( FEEL.evaluate( "10 / null", EMPTY_INPUT ), is( nullValue() ) );
    }

    @Test
    public void testMathExprNull8() {
        assertThat( FEEL.evaluate( "null / 10", EMPTY_INPUT ), is( nullValue() ) );
    }

    @Test
    public void testMathExprComp1() {
        assertThat( FEEL.evaluate( "10 + 20 / -5 - 3", EMPTY_INPUT ), is( BigDecimal.valueOf( 3 ) ) );
    }

    @Test
    public void testMathExprComp2() {
        assertThat( FEEL.evaluate( "10 + 20 / ( -5 - 3 )", EMPTY_INPUT ), is( BigDecimal.valueOf( 7.5 ) ) );
    }




}
