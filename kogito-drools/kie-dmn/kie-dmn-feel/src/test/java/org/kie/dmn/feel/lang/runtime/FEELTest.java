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

package org.kie.dmn.feel.lang.runtime;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

public class FEELTest {

    private static final Map<String, Object> EMPTY_INPUT = Collections.EMPTY_MAP;

    @Test
    public void testExpressions() {
        Object[][] cases = new Object[][] {
                // constants
                { "null", EMPTY_INPUT, null },
                { "true", EMPTY_INPUT, Boolean.TRUE },
                { "false", EMPTY_INPUT, Boolean.FALSE },
                // dash is an unary test that always matches, so for now, returning true.
                // have to double check to know if this is not the case
                { "-", EMPTY_INPUT, Boolean.TRUE },
                { "50", EMPTY_INPUT, new BigDecimal( "50" ) },
                { "-50", EMPTY_INPUT, new BigDecimal( "-50" ) },
                { "50.872", EMPTY_INPUT, new BigDecimal( "50.872" ) },
                { "-50.567", EMPTY_INPUT, new BigDecimal( "-50.567" ) },
                // quotes are a syntactical markup character for strings, so they disappear when the expression is evaluated
                { "\"foo bar\"", EMPTY_INPUT, "foo bar" },
                { "\"\"", EMPTY_INPUT, "" },

                // math operations
                { "10+5", EMPTY_INPUT, BigDecimal.valueOf( 15 ) },
                { "-10 + -5", EMPTY_INPUT, BigDecimal.valueOf( -15 ) },
                { "(-10) + (-5)", EMPTY_INPUT, BigDecimal.valueOf( -15 ) },
                { "10-5", EMPTY_INPUT, BigDecimal.valueOf( 5 ) },
                { "-10 - -5", EMPTY_INPUT, BigDecimal.valueOf( -5 ) },
                { "(-10) - (-5)", EMPTY_INPUT, BigDecimal.valueOf( -5 ) },
                { "(10 + 20) - (-5 + 3)", EMPTY_INPUT, BigDecimal.valueOf( 32 ) },
                { "10*5", EMPTY_INPUT, BigDecimal.valueOf( 50 ) },
                { "-10 * -5", EMPTY_INPUT, BigDecimal.valueOf( 50 ) },
                { "(-10) * (-5)", EMPTY_INPUT, BigDecimal.valueOf( 50 ) },
                { "(10 + 20) * (-5 * 3)", EMPTY_INPUT, BigDecimal.valueOf( -450 ) },
                { "10/5", EMPTY_INPUT, BigDecimal.valueOf( 2 ) },
                { "-10 / -5", EMPTY_INPUT, BigDecimal.valueOf( 2 ) },
                { "(-10) / (-5)", EMPTY_INPUT, BigDecimal.valueOf( 2 ) },
                { "(10 + 20) / (-5 * 3)", EMPTY_INPUT, BigDecimal.valueOf( -2 ) },
                { "(10 + 20) / 0", EMPTY_INPUT, null },
                { "10 ** 5", EMPTY_INPUT, BigDecimal.valueOf( 100000 ) },
                { "(5+2) ** 5", EMPTY_INPUT, BigDecimal.valueOf( 16807 ) },
                { "5+2 ** 5", EMPTY_INPUT, BigDecimal.valueOf( 37 ) },
                { "5+2 ** 5+3", EMPTY_INPUT, BigDecimal.valueOf( 40 ) },
                { "5+2 ** (5+3)", EMPTY_INPUT, BigDecimal.valueOf( 261 ) },
                { "10 + null", EMPTY_INPUT, null },
                { "null + 10", EMPTY_INPUT, null },
                { "10 - null", EMPTY_INPUT, null },
                { "null - 10", EMPTY_INPUT, null },
                { "10 * null", EMPTY_INPUT, null },
                { "null * 10", EMPTY_INPUT, null },
                { "10 / null", EMPTY_INPUT, null },
                { "null / 10", EMPTY_INPUT, null },
                { "10 + 20 / -5 - 3", EMPTY_INPUT, BigDecimal.valueOf( 3 ) },
                { "10 + 20 / ( -5 - 3 )", EMPTY_INPUT, BigDecimal.valueOf( 7.5 ) },

                // ternary logic operations as per the spec
                { "true and true", EMPTY_INPUT, Boolean.TRUE },
                { "true and false", EMPTY_INPUT, Boolean.FALSE },
                { "true and null", EMPTY_INPUT,  null },
                { "false and true", EMPTY_INPUT, Boolean.FALSE },
                { "false and false", EMPTY_INPUT, Boolean.FALSE },
                { "false and null", EMPTY_INPUT, Boolean.FALSE },
                { "null and true", EMPTY_INPUT, null },
                { "null and false", EMPTY_INPUT, Boolean.FALSE },
                { "null and null", EMPTY_INPUT, null },
                { "true or true", EMPTY_INPUT, Boolean.TRUE },
                { "true or false", EMPTY_INPUT, Boolean.TRUE },
                { "true or null", EMPTY_INPUT,  Boolean.TRUE },
                { "false or true", EMPTY_INPUT, Boolean.TRUE },
                { "false or false", EMPTY_INPUT, Boolean.FALSE },
                { "false or null", EMPTY_INPUT, null },
                { "null or true", EMPTY_INPUT, Boolean.TRUE },
                { "null or false", EMPTY_INPUT, null },
                { "null or null", EMPTY_INPUT, null },
                // logical operator priority
                { "false and false or true", EMPTY_INPUT, Boolean.TRUE },
                { "false and (false or true)", EMPTY_INPUT, Boolean.FALSE },
                { "true or false and false", EMPTY_INPUT, Boolean.TRUE },
                { "(true or false) and false", EMPTY_INPUT, Boolean.FALSE },

                // number comparisons
                { "10.4 < 20.6", EMPTY_INPUT, Boolean.TRUE },
                { "10.4 <= 20.6", EMPTY_INPUT, Boolean.TRUE },
                { "10.4 = 20.6", EMPTY_INPUT, Boolean.FALSE },
                { "10.4 != 20.6", EMPTY_INPUT, Boolean.TRUE },
                { "10.4 > 20.6", EMPTY_INPUT, Boolean.FALSE },
                { "10.4 >= 20.6", EMPTY_INPUT, Boolean.FALSE },
                { "15.25 = 15.25", EMPTY_INPUT, Boolean.TRUE },
                { "15.25 != 15.25", EMPTY_INPUT, Boolean.FALSE },

                // string comparisons
                { "\"foo\" < \"bar\"", EMPTY_INPUT, Boolean.FALSE },
                { "\"foo\" <= \"bar\"", EMPTY_INPUT, Boolean.FALSE },
                { "\"foo\" = \"bar\"", EMPTY_INPUT, Boolean.FALSE },
                { "\"foo\" != \"bar\"", EMPTY_INPUT, Boolean.TRUE },
                { "\"foo\" > \"bar\"", EMPTY_INPUT, Boolean.TRUE },
                { "\"foo\" >= \"bar\"", EMPTY_INPUT, Boolean.TRUE },
                { "\"foo\" = \"foo\"", EMPTY_INPUT, Boolean.TRUE },
                { "\"foo\" != \"foo\"", EMPTY_INPUT, Boolean.FALSE },

                // boolean comparisons
                { "true = true", EMPTY_INPUT, Boolean.TRUE },
                { "false = false", EMPTY_INPUT, Boolean.TRUE },
                { "false = true", EMPTY_INPUT, Boolean.FALSE },
                { "true = false", EMPTY_INPUT, Boolean.FALSE },
                { "true != true", EMPTY_INPUT, Boolean.FALSE },
                { "false != false", EMPTY_INPUT, Boolean.FALSE },
                { "false != true", EMPTY_INPUT, Boolean.TRUE },
                { "true != false", EMPTY_INPUT, Boolean.TRUE },

                // null comparisons and comparisons between different types


//                { "", EMPTY_INPUT,  },
//                { "", EMPTY_INPUT,  },
//                { "", EMPTY_INPUT,  },
//                { "", EMPTY_INPUT,  },
//                { "", EMPTY_INPUT,  },

        };
        for( Object[] c : cases ) {
            assertCase( (String) c[0], (Map<String, Object>) c[1], c[2] );
        }
    }

    private void assertCase( String expr, Map<String, Object> input, Object result ) {
        if( result == null ) {
            assertThat( "Evaluating: '"+expr+"'", FEEL.evaluate( expr, input ), is( nullValue() ) );
        } else {
            assertThat( "Evaluating: '"+expr+"'", FEEL.evaluate( expr, input ), is( result ) );
        }
    }


    @Test @Ignore( "Java BigDecimals do not support negative numbers as power. Need to figure out what to do." )
    public void testMathExprPow2() {
        assertThat( FEEL.evaluate( "10 ** -5", EMPTY_INPUT ), is( BigDecimal.valueOf( -0.00001 ) ) );
    }

}
