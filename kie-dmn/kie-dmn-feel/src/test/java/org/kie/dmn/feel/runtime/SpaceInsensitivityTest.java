/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.model.Person;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.SpaceInsensitiveBuilder;
import org.kie.dmn.feel.util.SpaceInsensitiveBuilder.SpacesAndStringParts;

import static org.junit.Assert.assertEquals;

public class SpaceInsensitivityTest {

    private static final char SPACE_NORMALIZATION_CHAR = ' ';
    private static final FEEL FEEL = org.kie.dmn.feel.FEEL.newInstance();

    @Test
    public void test_EvalHelperNormalization() {
        SpacesAndStringParts run = new SpaceInsensitiveBuilder("a").append("person").build();
        String result = EvalHelper.normalizeVariableName(run.toString());
        assertEquals(run.asLogical(), result, "a" + SPACE_NORMALIZATION_CHAR + "person");
    }

    @Test
    public void test_FEELsymbols() {
        CompositeType personType = (CompositeType) JavaBackedType.of( Person.class );
        CompilerContext cctx = FEEL.newCompilerContext();
        cctx.addInputVariableType("a person ", personType);
        CompiledExpression ccexpr = FEEL.compile("a person.first name", cctx);
        Map<String, Object> ectx = new HashMap<>();
        ectx.put(" a  person ", new Person("John", "Doe", 47));
        Object result = FEEL.evaluate(ccexpr, ectx);
        assertEquals("John", result);
    }

    @Test
    public void test_FEELsymbols2() {
        CompositeType personType = (CompositeType) JavaBackedType.of(Person.class);
        CompilerContext cctx = FEEL.newCompilerContext();
        SpacesAndStringParts compilerContextPersonSymbol = new SpaceInsensitiveBuilder("a").append("person").build();
        cctx.addInputVariableType(compilerContextPersonSymbol.toString(), personType);
        CompiledExpression ccexpr = FEEL.compile("a person.first name", cctx);
        Map<String, Object> ectx = new HashMap<>();
        SpacesAndStringParts evaluationContextPersonSymbol = new SpaceInsensitiveBuilder("a").append("person").build();
        ectx.put(evaluationContextPersonSymbol.toString(), new Person("John", "Doe", 47));
        Object result = FEEL.evaluate(ccexpr, ectx);
        String logicalRun = "compilerContextPersonSymbol:" + compilerContextPersonSymbol.asLogical() + ", evaluationContextPersonSymbol:" + evaluationContextPersonSymbol.asLogical();
        assertEquals("failed for: " + logicalRun, "John", result);
    }

    @Test
    public void test_FEELsymbols3() {
        CompositeType personType = (CompositeType) JavaBackedType.of(Person.class);
        CompilerContext cctx = FEEL.newCompilerContext();
        SpacesAndStringParts cctxPersonSymbol = new SpaceInsensitiveBuilder("a").append("person").build();
        cctx.addInputVariableType(cctxPersonSymbol.toString(), personType);
        SpacesAndStringParts feelExpr = new SpaceInsensitiveBuilder("a").append("person").append(".").append("first").append("name").build();
        CompiledExpression ccexpr = FEEL.compile(feelExpr.toString(), cctx);
        Map<String, Object> ectx = new HashMap<>();
        SpacesAndStringParts ectxPersonSymbol = new SpaceInsensitiveBuilder("a").append("person").build();
        ectx.put(ectxPersonSymbol.toString(), new Person("John", "Doe", 47));
        Object result = FEEL.evaluate(ccexpr, ectx);
        String logicalRun = "cctxPersonSymbol:" + cctxPersonSymbol.asLogical() + ", ectxPersonSymbol:" + ectxPersonSymbol.asLogical() + ", feelExpr" + feelExpr.asLogical();
        assertEquals("failed for: " + logicalRun, "John", result);
    }


}
