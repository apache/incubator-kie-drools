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
package org.kie.dmn.feel.runtime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.kie.dmn.feel.util.EvalHelper.getBigDecimalOrNull;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.ast.InfixOpNode.InfixOperator;

public class FEELNumberCoercionTest {
    private final FEEL feel = FEEL.newInstance();
    
    private Object evaluateInfix(Object x, InfixOperator op, Object y) {
        Map<String, Object> inputVariables = new HashMap<>();
        inputVariables.put("x", x);
        inputVariables.put("y", y);
        String expression = "x " + op.symbol + " y";
        System.out.println(expression);
        return feel.evaluate(expression, inputVariables);
    }
    
    @Test
    public void test() {
        assertThat( evaluateInfix( 1  , InfixOperator.LT  , 2d  ), is( true  )  );
        assertThat( evaluateInfix( 2d , InfixOperator.LT  ,  1  ), is( false )  );
        assertThat( evaluateInfix( 1  , InfixOperator.LTE , 2d  ), is( true  )  );
        assertThat( evaluateInfix( 2d , InfixOperator.LTE ,  1  ), is( false )  );
        assertThat( evaluateInfix( 1  , InfixOperator.GT  , 2d  ), is( false )  );
        assertThat( evaluateInfix( 2d , InfixOperator.GT  ,  1  ), is( true  )  );
        assertThat( evaluateInfix( 1  , InfixOperator.GTE , 2d  ), is( false )  );
        assertThat( evaluateInfix( 2d , InfixOperator.GTE ,  1  ), is( true  )  );
    }
    
    @SafeVarargs
    private final Object evaluate(String expression, Map.Entry<String, ?>... vars) {
        HashMap<String, Object> inputVariables = new HashMap<>();
        for (Map.Entry<String, ?> v : vars) {
            inputVariables.put(v.getKey(), v.getValue());
        }
        return feel.evaluate(expression, inputVariables);
    }
    
    private static Map.Entry<String, Object> var(String name, Object value) {
        return new SimpleEntry<String, Object>(name, value);
    }
    
    @Test
    public void testOthers() {
        assertThat( evaluate("ceiling( 1.01 )") , is( getBigDecimalOrNull( 2d ) ) );
        assertThat( evaluate("ceiling( x )", var("x", 1.01d )) , is( getBigDecimalOrNull( 2d ) ) );
        assertThat( ((Map) evaluate("{ myf : function( v1, v2 ) ceiling(v1), invoked: myf(v2: false, v1: x) }", var("x", 1.01d) )).get("invoked"), is( getBigDecimalOrNull( 2d ) ) );
        assertThat( ((Map) evaluate("{ myf : function( v1, v2 ) v1, invoked: myf(v2: false, v1: x) }", var("x", 1.01d) )).get("invoked"), is( getBigDecimalOrNull( 1.01d ) ) );

        assertThat( evaluate(" x.y ", var("x", new HashMap(){{ put("y", 1.01d); }} )), is( getBigDecimalOrNull( 1.01d ) ) );
        assertThat( evaluate("ceiling( x.y )", var("x", new HashMap(){{ put("y", 1.01d); }} )), is( getBigDecimalOrNull( 2d ) ) );
    }
    
    
}