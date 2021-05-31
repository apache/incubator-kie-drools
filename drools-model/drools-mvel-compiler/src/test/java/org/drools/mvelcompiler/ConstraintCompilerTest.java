/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvelcompiler;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.drools.Gender;
import org.drools.Person;
import org.drools.core.addon.ClassTypeResolver;
import org.drools.core.addon.TypeResolver;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.junit.Test;

public class ConstraintCompilerTest implements CompilerTest {

    @Test
    public void testBigDecimalPromotion() {
        testExpression(c -> c.setRootPatternPrefix(Person.class, "_this"), "salary + salary",
                       "_this.getSalary().add(_this.getSalary())");
    }

    @Test
    public void testBigDecimalStringEquality() {
        testExpression(c -> c.setRootPatternPrefix(Person.class, "_this"), "salary == \"90\"",
                       "_this.getSalary().equals(new java.math.BigDecimal(\"90\"))");
    }

    @Test
    public void testBigDecimalStringNonEquality() {
        testExpression(c -> c.setRootPatternPrefix(Person.class, "_this"), "salary != \"90\"",
                       "!(_this.getSalary().equals(new java.math.BigDecimal(\"90\")))");
    }

    @Test
    public void testBigDecimalPromotionToIntMethod() {
        testExpression(c -> c.setRootPatternPrefix(Person.class, "_this"), "isEven(salary)",
                       "_this.isEven(_this.getSalary().intValue())");
    }

    @Test
    public void testConversionConstructorArgument() {
        testExpression(c -> c.addDeclaration("$p", Person.class), "new Person($p.name, $p)",
                       "new Person($p.getName(), $p)");
    }

    public void testExpression(Consumer<MvelCompilerContext> testFunction,
                               String inputExpression,
                               String expectedResult,
                               Consumer<CompiledExpressionResult> resultAssert) {
        Set<String> imports = new HashSet<>();
        imports.add("java.util.List");
        imports.add("java.util.ArrayList");
        imports.add("java.util.HashMap");
        imports.add("java.util.Map");
        imports.add("java.math.BigDecimal");
        imports.add("org.drools.Address");
        imports.add(Person.class.getCanonicalName());
        imports.add(Gender.class.getCanonicalName());
        TypeResolver typeResolver = new ClassTypeResolver(imports, this.getClass().getClassLoader());
        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext(typeResolver);
        testFunction.accept(mvelCompilerContext);
        CompiledExpressionResult compiled = new ConstraintCompiler(mvelCompilerContext).compileExpression(inputExpression);
        verifyBodyWithBetterDiff(expectedResult, compiled.resultAsString());
        resultAssert.accept(compiled);
    }

    void testExpression(Consumer<MvelCompilerContext> testFunction,
                        String inputExpression,
                        String expectedResult) {
        testExpression(testFunction, inputExpression, expectedResult, t -> {
        });
    }
}