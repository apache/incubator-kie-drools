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
package org.drools.modelcompiler.builder.generator.drlxparse;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.expr.Expression;
import org.drools.util.ClassTypeResolver;
import org.drools.util.TypeResolver;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DRLIdGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.domain.Person;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConstraintParserTest {

    private ConstraintParser parser;

    @Before
    public void setup() {
        PackageModel packageModel = new PackageModel("org.kie.test:constraint-parser-test:1.0.0", "org.kie.test", null, null, new DRLIdGenerator());
        Set<String> imports = new HashSet<>();
        imports.add("org.drools.modelcompiler.domain.Person");
        TypeResolver typeResolver = new ClassTypeResolver(imports, getClass().getClassLoader());
        RuleContext context = new RuleContext(null, packageModel, typeResolver, null, 0);
        parser = ConstraintParser.defaultConstraintParser(context, packageModel);
    }

    @Test
    public void testNullSafeExpressions() {
        SingleDrlxParseSuccess result = (SingleDrlxParseSuccess) parser.drlxParse(Person.class, "$p", "address!.city == \"London\"");

        List<Expression> nullSafeExpressions = result.getNullSafeExpressions();
        assertEquals(1, nullSafeExpressions.size());
        assertEquals("_this.getAddress() != null", nullSafeExpressions.get(0).toString()); // will be added as the first predicate

        assertEquals("org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getAddress().getCity(), \"London\")", result.getExpr().toString());
    }

    @Test
    public void testNullSafeExpressionsWithOr() {
        SingleDrlxParseSuccess result = (SingleDrlxParseSuccess) parser.drlxParse(Person.class, "$p", "name == \"John\" || == address!.city");

        assertEquals(0, result.getNullSafeExpressions().size()); // not using NullSafeExpressions for complex OR cases

        // null check is done after the first constraint
        assertEquals("_this.getName() == \"John\" || _this.getAddress() != null && _this.getName() == _this.getAddress().getCity()", result.getExpr().toString());
    }

    @Test
    public void testImplicitCastExpression() {
        SingleDrlxParseSuccess result = (SingleDrlxParseSuccess) parser.drlxParse(Object.class, "$o", "this#Person.name == \"Mark\"");

        Optional<Expression> implicitCastExpression = result.getImplicitCastExpression();
        assertTrue(implicitCastExpression.isPresent());
        assertEquals("_this instanceof Person", implicitCastExpression.get().toString()); // will be added as the first predicate

        assertEquals("_this instanceof org.drools.modelcompiler.domain.Person" +
                     " && org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(((org.drools.modelcompiler.domain.Person) _this).getName(), \"Mark\")",
                     result.getExpr().toString());
    }

    @Test
    public void testImplicitCastExpressionWithOr() {
        SingleDrlxParseSuccess result = (SingleDrlxParseSuccess) parser.drlxParse(Object.class, "$o", "\"Mark\" == this.toString() || == this#Person.address.city");

        Optional<Expression> implicitCastExpression = result.getImplicitCastExpression();
        assertTrue(implicitCastExpression.isPresent());
        assertEquals("_this instanceof Person", implicitCastExpression.get().toString()); // will be added as the first predicate

        // instanceof check is done after the first constraint
        assertEquals("\"Mark\" == _this.toString() || _this instanceof org.drools.modelcompiler.domain.Person && \"Mark\" == ((org.drools.modelcompiler.domain.Person) _this).getAddress().getCity()",
                     result.getExpr().toString());
    }

    @Test
    public void testMultiplyStringIntWithBindVariableCompareToBigDecimal() {
        SingleDrlxParseSuccess result = (SingleDrlxParseSuccess) parser.drlxParse(Person.class, "$p", "money == likes * 10"); // assuming likes contains number String

        assertEquals("org.drools.modelcompiler.util.EvaluationUtil.equals(org.drools.modelcompiler.util.EvaluationUtil.toBigDecimal(_this.getMoney()), org.drools.modelcompiler.util.EvaluationUtil.toBigDecimal(Double.valueOf(_this.getLikes()) * 10))",
                     result.getExpr().toString());
    }

    @Test
    public void testBigDecimalLiteralWithBindVariable() {
        SingleDrlxParseSuccess result = (SingleDrlxParseSuccess) parser.drlxParse(Person.class, "$p", "$bd : 10.3B");

        assertEquals("new java.math.BigDecimal(\"10.3\")", result.getExpr().toString());
    }

    @Test
    public void testBigIntegerLiteralWithBindVariable() {
        SingleDrlxParseSuccess result = (SingleDrlxParseSuccess) parser.drlxParse(Person.class, "$p", "$bi : 10I");

        assertEquals("new java.math.BigInteger(\"10\")", result.getExpr().toString());
    }
}
