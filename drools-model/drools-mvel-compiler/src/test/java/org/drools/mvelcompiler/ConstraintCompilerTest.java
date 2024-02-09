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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.drools.Gender;
import org.drools.Person;
import org.drools.util.ClassTypeResolver;
import org.drools.util.TypeResolver;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.junit.Test;

public class ConstraintCompilerTest implements CompilerTest {

    @Test
    public void testBigDecimalPromotion() {
        testExpression(c -> c.setRootPatternPrefix(Person.class, "_this"), "salary + salary",
                       "_this.getSalary().add(_this.getSalary(), java.math.MathContext.DECIMAL128)");
    }

    @Test
    public void testBigDecimalStringEquality() {
        testExpression(c -> c.setRootPatternPrefix(Person.class, "_this"), "salary == \"90\"",
                       "_this.getSalary().compareTo(new java.math.BigDecimal(\"90\")) == 0");
    }

    @Test
    public void testBigDecimalStringNonEquality() {
        testExpression(c -> c.setRootPatternPrefix(Person.class, "_this"), "salary != \"90\"",
                       "_this.getSalary().compareTo(new java.math.BigDecimal(\"90\")) != 0");
    }

    @Test
    public void testBigDecimalPromotionToIntMethod() {
        testExpression(c -> c.setRootPatternPrefix(Person.class, "_this"), "isEven(salary)",
                       "_this.isEven(_this.getSalary().intValue())");
    }

    @Test
    public void testConversionConstructorArgument() {
        testExpression(c -> c.addDeclaration("$p", Person.class), "new Person($p.name, $p)",
                       "new org.drools.Person($p.getName(), $p)");
    }

    @Test
    public void testBigDecimalMultiplyInt() {
        testExpression(c -> c.addDeclaration("$bd1", BigDecimal.class), "$bd1 * 10",
                       "$bd1.multiply(new java.math.BigDecimal(10), java.math.MathContext.DECIMAL128)");
    }

    @Test
    public void testBigDecimalMultiplyNegativeInt() {
        testExpression(c -> c.addDeclaration("$bd1", BigDecimal.class), "$bd1 * -1",
                       "$bd1.multiply(new java.math.BigDecimal(-1), java.math.MathContext.DECIMAL128)");
    }

    @Test
    public void testBigDecimalAddInt() {
        testExpression(c -> c.addDeclaration("$bd1", BigDecimal.class), "$bd1 + 10",
                       "$bd1.add(new java.math.BigDecimal(10), java.math.MathContext.DECIMAL128)");
    }

    @Test
    public void testBigDecimalSubtractInt() {
        testExpression(c -> c.addDeclaration("$bd1", BigDecimal.class), "$bd1 - 10",
                       "$bd1.subtract(new java.math.BigDecimal(10), java.math.MathContext.DECIMAL128)");
    }

    @Test
    public void testBigDecimalDivideInt() {
        testExpression(c -> c.addDeclaration("$bd1", BigDecimal.class), "$bd1 / 10",
                       "$bd1.divide(new java.math.BigDecimal(10), java.math.MathContext.DECIMAL128)");
    }

    @Test
    public void testBigDecimalModInt() {
        testExpression(c -> c.addDeclaration("$bd1", BigDecimal.class), "$bd1 % 10",
                       "$bd1.remainder(new java.math.BigDecimal(10), java.math.MathContext.DECIMAL128)");
    }

    @Test
    public void testBigDecimalValueOfInteger() {
        testExpression(c -> c.addDeclaration("$bdvalue", BigDecimal.class),
                "$bdvalue + BigDecimal.valueOf(10)",
                "$bdvalue.add(BigDecimal.valueOf(10), java.math.MathContext.DECIMAL128)");
    }

    @Test
    public void testBigDecimalValueOfDouble() {
        testExpression(c -> c.addDeclaration("$bdvalue", BigDecimal.class),
                "$bdvalue + BigDecimal.valueOf(0.5)",
                "$bdvalue.add(BigDecimal.valueOf(0.5), java.math.MathContext.DECIMAL128)");
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