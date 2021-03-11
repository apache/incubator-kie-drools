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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.parser.feel11.profiles.DoCompileFEELProfile;
import org.kie.dmn.feel.runtime.BaseFEELTest.FEEL_TARGET;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public abstract class BaseFEELCompilerTest {

    @Parameterized.Parameter(4)
    public FEEL_TARGET testFEELTarget;

    private FEEL feel = null; // due to @Parameter injection by JUnit framework, need to defer FEEL init to actual instance method, to have the opportunity for the JUNit framework to initialize all the @Parameters

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
        feel = (testFEELTarget == FEEL_TARGET.JAVA_TRANSLATED) ? FEEL.newInstance(Collections.singletonList(new DoCompileFEELProfile())) : FEEL.newInstance();
        assertResult( expression, inputTypes, inputValues, result );
    }

    protected void assertResult(final String expression, final Map<String, Type> inputTypes, final Map<String, Object> inputValues, final Object result) {
        final CompilerContext ctx = feel.newCompilerContext();
        inputTypes.forEach(ctx::addInputVariableType);
        final CompiledExpression compiledExpression = feel.compile(expression, ctx );

        if( result == null ) {
            assertThat( "Evaluating: '" + expression + "'", feel.evaluate( compiledExpression, inputValues ), is( nullValue() ) );
        } else if( result instanceof Class<?> ) {
            assertThat( "Evaluating: '" + expression + "'", feel.evaluate( compiledExpression, inputValues ), is( instanceOf( (Class<?>) result ) ) );
        } else {
            assertThat( "Evaluating: '"+expression+"'", feel.evaluate( compiledExpression, inputValues ), is( result ) );
        }
    }

    protected static List<Object[]> enrichWith5thParameter(final Object[][] cases) {
        final List<Object[]> results = new ArrayList<>();
        for (final Object[] c : cases) {
            results.add(new Object[]{c[0], c[1], c[2], c[3], FEEL_TARGET.AST_INTERPRETED});
        }
        for (final Object[] c : cases) {
            results.add(new Object[]{c[0], c[1], c[2], c[3], FEEL_TARGET.JAVA_TRANSLATED});
        }
        return results;
    }
}
