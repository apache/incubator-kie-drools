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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.ObjectAssert;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.FEELBuilder;
import org.kie.dmn.feel.parser.feel11.profiles.DoCompileFEELProfile;
import org.kie.dmn.feel.runtime.BaseFEELTest.FEEL_TARGET;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseFEELCompilerTest {

    public FEEL_TARGET testFEELTarget;

    private FEEL feel = null; // due to @Parameter injection by JUnit framework, need to defer FEEL init to actual instance method, to have the opportunity for the JUNit framework to initialize all the @Parameters

    public String expression;

    public Map<String, Type> inputTypes;

    public Map<String, Object> inputValues;

    public Object result;

    public void expression(String expression, Map<String, Type> inputTypes, Map<String, Object> inputValues, Object result, FEEL_TARGET testFEELTarget) {
        this.expression = expression;
        this.inputTypes = inputTypes;
        this.inputValues =inputValues;
        this.result = result;
        this.testFEELTarget = testFEELTarget;

        feel = (testFEELTarget == FEEL_TARGET.JAVA_TRANSLATED) ? FEELBuilder.builder().withProfiles(Collections.singletonList(new DoCompileFEELProfile())).build() : FEELBuilder.builder().build();
        assertResult( expression, inputTypes, inputValues, result );
    }

    abstract protected void instanceTest(String expression, Map<String, Type> inputTypes, Map<String, Object> inputValues, Object result, FEEL_TARGET testFEELTarget);

    protected void assertResult(final String expression, final Map<String, Type> inputTypes, final Map<String,
            Object> inputValues, final Object expected) {
        final CompilerContext ctx = feel.newCompilerContext();
        inputTypes.forEach(ctx::addInputVariableType);
        final CompiledExpression compiledExpression = feel.compile(expression, ctx );

        Object retrieved = feel.evaluate(compiledExpression, inputValues);
        String description = String.format("Evaluating: '%s'", expression);
        ObjectAssert<Object> assertion = assertThat(retrieved).as(description);
        if (expected == null) {
            assertion.isNull();
        } else if (expected instanceof Class<?>) {
            assertion.isInstanceOf((Class<?>) expected);
        } else {
            assertion.isEqualTo(expected);
        }
    }

    protected static List<Object[]> enrichWith5thParameter(final Object[][] cases) {
        final List<Object[]> results = new ArrayList<>();
        for (final Object[] c : cases) {
            results.add(new Object[]{c[0], c[1], c[2], c[3], FEEL_TARGET.AST_INTERPRETED});
            results.add(new Object[]{c[0], c[1], c[2], c[3], FEEL_TARGET.JAVA_TRANSLATED});
        }
        return results;
    }
}
