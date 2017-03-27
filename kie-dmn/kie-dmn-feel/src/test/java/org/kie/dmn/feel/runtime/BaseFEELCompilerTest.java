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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public abstract class BaseFEELCompilerTest {

    private final FEEL feel = FEEL.newInstance();

    @Parameterized.Parameter(0)
    public String expression;

    @Parameterized.Parameter(1)
    public Map<String, Type> inputTypes;

    @Parameterized.Parameter(2)
    public Map<String, Object> inputValues;

    @Parameterized.Parameter(3)
    public Object result;

    @Test
    public void testExpression() {
        assertResult( expression, inputTypes, inputValues, result );
    }

    protected void assertResult(String expression, Map<String, Type> inputTypes, Map<String, Object> inputValues, Object result) {
        CompilerContext ctx = feel.newCompilerContext();
        inputTypes.forEach( (name, type) -> ctx.addInputVariableType( name, type ) );
        CompiledExpression compiledExpression = feel.compile( expression, ctx );

        if( result == null ) {
            assertThat( "Evaluating: '" + expression + "'", feel.evaluate( compiledExpression, inputValues ), is( nullValue() ) );
        } else if( result instanceof Class<?> ) {
            assertThat( "Evaluating: '" + expression + "'", feel.evaluate( compiledExpression, inputValues ), is( instanceOf( (Class<?>) result ) ) );
        } else {
            assertThat( "Evaluating: '"+expression+"'", feel.evaluate( compiledExpression, inputValues ), is( result ) );
        }
    }
}
