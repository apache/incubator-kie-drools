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
package org.drools.mvelcompiler;

import java.util.Collections;
import java.util.function.Consumer;

import org.drools.Person;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class PreprocessCompilerTest implements CompilerTest {

    @Test
    public void testUncompiledMethod() {
        test("{modify( (List)$toEdit.get(0) ){ setEnabled( true ) }}",
             "{ { ((List) $toEdit.get(0)).setEnabled(true); } }",
             result -> assertThat(allUsedBindings(result)).isEmpty());
    }

    @Test
    public void testModify() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{  modify($p) { setCanDrink(true); } }",
             "{ { ($p).setCanDrink(true); } update($p); }",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void testModifyWithLambda() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{  modify($p) {  setCanDrinkLambda(() -> true); } }",
             "{ { ($p).setCanDrinkLambda(() -> true); } update($p); }",
             result ->assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void testNestedModify() {
        test("{    if ($fact.getResult() != null) {\n" +
                     "        $fact.setResult(\"OK\");\n" +
                     "    } else {\n" +
                     "        modify ($fact) {\n" +
                     "            setResult(\"FIRST\")\n" +
                     "        }\n" +
                                 "    }}",
             " { " +
                     "if ($fact.getResult() != null) { " +
                     "  $fact.setResult(\"OK\"); " +
                     "} else { " +
                         "{ " +
                         "  ($fact).setResult(\"FIRST\"); " +
                     "   } " +
                     "  update($fact); " +
                     "} " +
                     "} ",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$fact"));
    }

    @Test
    public void testMultiLineStringLiteral() {
        test(" { java.lang.String s = \"\"\"\n" +
                     "                      Pikachu\n" +
                     "                      Is\n" +
                     "                      Yellow\n" +
                     "                      \"\"\"; " +
                     "}",
             " { java.lang.String s = \"Pikachu\\nIs\\nYellow\\n\"; }");
    }

    @Test
    public void testMultiLineStringLiteralAsMethodCallExpr() {
        test(" { java.lang.String s = \"\"\"\n" +
                     "                      Charmander\n" +
                     "                      Is\n" +
                     "                      Red\n" +
                     "                      \"\"\"" +
                     ".formatted(2); " +
                     "        " +
                     "}",
             " { java.lang.String s = \"Charmander\\nIs\\nRed\\n\".formatted(2); }");
    }

    @Test
    public void testMultiLineStringWithStringCharacterInside() {
        test(" { java.lang.String s = \"\"\"\n" +
                     "                      Bulbasaur\n" +
                     "                      Is\n" +
                     "                      \"Green\"\n" +
                     "                      \"\"\";\n" +
                     "}",
             " { java.lang.String s = \"Bulbasaur\\nIs\\n\\\"Green\\\"\\n\"; }");
    }

    @Override
    public void test(Consumer<MvelCompilerContext> testFunction,
                      String inputExpression,
                      String expectedResult,
                      Consumer<CompiledBlockResult> resultAssert) {
        CompiledBlockResult compiled = new PreprocessCompiler().compile(inputExpression, Collections.emptySet());
        assertThat(compiled.resultAsString()).isEqualToIgnoringWhitespace(expectedResult);
        resultAssert.accept(compiled);
    }
}