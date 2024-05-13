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
package org.kie.dmn.feel.runtime;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.ast.InfixOperator;
import org.kie.dmn.feel.lang.impl.FEELBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.util.NumberEvalHelper.getBigDecimalOrNull;

class FEELNumberCoercionTest {
    private final FEEL feel = FEELBuilder.builder().build();
    
    private Object evaluateInfix(final Object x, final InfixOperator op, final Object y) {
        final Map<String, Object> inputVariables = new HashMap<>();
        inputVariables.put("x", x);
        inputVariables.put("y", y);
        final String expression = "x " + op.symbol + " y";
        System.out.println(expression);
        return feel.evaluate(expression, inputVariables);
    }

    @Test
    void test() {
        assertThat( evaluateInfix( 1  , InfixOperator.LT  , 2d  )).isEqualTo(Boolean.TRUE);
        assertThat( evaluateInfix( 2d , InfixOperator.LT  ,  1  )).isEqualTo(Boolean.FALSE);
        assertThat( evaluateInfix( 1  , InfixOperator.LTE , 2d  )).isEqualTo(Boolean.TRUE);
        assertThat( evaluateInfix( 2d , InfixOperator.LTE ,  1  )).isEqualTo(Boolean.FALSE);
        assertThat( evaluateInfix( 1  , InfixOperator.GT  , 2d  )).isEqualTo(Boolean.FALSE);
        assertThat( evaluateInfix( 2d , InfixOperator.GT  ,  1  )).isEqualTo(Boolean.TRUE);
        assertThat( evaluateInfix( 1  , InfixOperator.GTE , 2d  )).isEqualTo(Boolean.FALSE);
        assertThat( evaluateInfix( 2d , InfixOperator.GTE ,  1  )).isEqualTo(Boolean.TRUE);
    }
    
    @SafeVarargs
    private final Object evaluate(final String expression, final Map.Entry<String, ?>... vars) {
        final HashMap<String, Object> inputVariables = new HashMap<>();
        for (final Map.Entry<String, ?> v : vars) {
            inputVariables.put(v.getKey(), v.getValue());
        }
        return feel.evaluate(expression, inputVariables);
    }
    
    private static Map.Entry<String, Object> var(final String name, final Object value) {
        return new SimpleEntry<>(name, value);
    }

    @Test
    void others() {
        assertThat( evaluate("ceiling( 1.01 )") ).isEqualTo(getBigDecimalOrNull( 2d ) );
        assertThat( evaluate("ceiling( x )", var("x", 1.01d )) ).isEqualTo(getBigDecimalOrNull( 2d ) );
        assertThat( ((Map) evaluate("{ myf : function( v1, v2 ) ceiling(v1), invoked: myf(v2: false, v1: x) }", var("x", 1.01d) )).get("invoked")).isEqualTo(getBigDecimalOrNull( 2d ) );
        assertThat( ((Map) evaluate("{ myf : function( v1, v2 ) v1, invoked: myf(v2: false, v1: x) }", var("x", 1.01d) )).get("invoked")).isEqualTo(getBigDecimalOrNull( 1.01d ) );

        assertThat( evaluate(" x.y ", var("x", new HashMap<String, Object>(){{ put("y", 1.01d); }} ))).isEqualTo(getBigDecimalOrNull( 1.01d ) );
        assertThat( evaluate("ceiling( x.y )", var("x", new HashMap<String, Object>(){{ put("y", 1.01d); }} ))).isEqualTo(getBigDecimalOrNull( 2d ) );
    }

    @Test
    void methodGetBigDecimalOrNull() {
        assertThat( getBigDecimalOrNull((short) 1)).isEqualTo(BigDecimal.ONE);
        assertThat( getBigDecimalOrNull((byte) 1)).isEqualTo(BigDecimal.ONE);
        assertThat( getBigDecimalOrNull(1)).isEqualTo(BigDecimal.ONE);
        assertThat( getBigDecimalOrNull(1L)).isEqualTo(BigDecimal.ONE);
        assertThat( getBigDecimalOrNull(1f)).isEqualTo(BigDecimal.ONE);
        assertThat( getBigDecimalOrNull(1.1f)).isEqualTo(BigDecimal.valueOf(1.1));
        assertThat( getBigDecimalOrNull(1d)).isEqualTo(BigDecimal.ONE);
        assertThat( getBigDecimalOrNull(1.1d)).isEqualTo(BigDecimal.valueOf(1.1));
        assertThat( getBigDecimalOrNull("1")).isEqualTo(BigDecimal.ONE);
        assertThat( getBigDecimalOrNull("1.1")).isEqualTo(BigDecimal.valueOf(1.1));
        assertThat( getBigDecimalOrNull("1.1000000")).isEqualTo(BigDecimal.valueOf(1.1).setScale(7, BigDecimal.ROUND_HALF_EVEN));
        assertThat( getBigDecimalOrNull(Double.POSITIVE_INFINITY)).isNull();
        assertThat( getBigDecimalOrNull(Double.NEGATIVE_INFINITY)).isNull();
        assertThat( getBigDecimalOrNull(Double.NaN)).isNull();
    }
}