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
package org.drools.model.codegen.execmodel.generator.drlxparse;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.expr.Expression;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.generator.DRLIdGenerator;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.modelcompiler.util.EvaluationUtil;
import org.drools.util.ClassTypeResolver;
import org.drools.util.TypeResolver;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstraintParserTest {

    private ConstraintParser parser;

    @Before
    public void setup() {
        PackageModel packageModel = new PackageModel("org.kie.test:constraint-parser-test:1.0.0", "org.kie.test", null, null, new DRLIdGenerator());
        Set<String> imports = new HashSet<>();
        imports.add(Person.class.getCanonicalName());
        TypeResolver typeResolver = new ClassTypeResolver(imports, getClass().getClassLoader());
        RuleContext context = new RuleContext(null, null, packageModel, typeResolver, null, 0);
        parser = ConstraintParser.defaultConstraintParser(context, packageModel);
    }

    @Test
    public void testNullSafeExpressions() {
        SingleDrlxParseSuccess result = (SingleDrlxParseSuccess) parser.drlxParse(Person.class, "$p", "address!.city == \"London\"");

        List<Expression> nullSafeExpressions = result.getNullSafeExpressions();
        assertThat(nullSafeExpressions.size()).isEqualTo(1);
        assertThat(nullSafeExpressions.get(0).toString()).isEqualTo("_this.getAddress() != null"); // will be added as the first predicate

        assertThat(result.getExpr().toString()).isEqualTo("org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getAddress().getCity(), \"London\")");
    }

    @Test
    public void testNullSafeExpressionsWithOr() {
        SingleDrlxParseSuccess result = (SingleDrlxParseSuccess) parser.drlxParse(Person.class, "$p", "name == \"John\" || == address!.city");

        assertThat(result.getNullSafeExpressions().size()).isEqualTo(0); // not using NullSafeExpressions for complex OR cases

        // null check is done after the first constraint
        assertThat(result.getExpr().toString()).isEqualTo("_this.getName() == \"John\" || _this.getAddress() != null && _this.getName() == _this.getAddress().getCity()");
    }

    @Test
    public void testImplicitCastExpression() {
        SingleDrlxParseSuccess result = (SingleDrlxParseSuccess) parser.drlxParse(Object.class, "$o", "this#Person.name == \"Mark\"");

        Optional<Expression> implicitCastExpression = result.getImplicitCastExpression();
        assertThat(implicitCastExpression.isPresent()).isTrue();
        assertThat(implicitCastExpression.get().toString()).isEqualTo("_this instanceof Person"); // will be added as the first predicate

        assertThat(result.getExpr().toString()).isEqualTo("_this instanceof " + Person.class.getCanonicalName() +
                " && org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(((" + Person.class.getCanonicalName() + ") _this).getName(), \"Mark\")");
    }

    @Test
    public void testImplicitCastExpressionWithOr() {
        SingleDrlxParseSuccess result = (SingleDrlxParseSuccess) parser.drlxParse(Object.class, "$o", "\"Mark\" == this.toString() || == this#Person.address.city");

        Optional<Expression> implicitCastExpression = result.getImplicitCastExpression();
        assertThat(implicitCastExpression.isPresent()).isTrue();
        assertThat(implicitCastExpression.get().toString()).isEqualTo("_this instanceof Person"); // will be added as the first predicate

        // instanceof check is done after the first constraint
        assertThat(result.getExpr().toString()).isEqualTo("\"Mark\" == _this.toString() || _this instanceof " + Person.class.getCanonicalName() + " && \"Mark\" == ((" + Person.class.getCanonicalName() + ") _this).getAddress().getCity()");
    }

    @Test
    public void testMultiplyStringIntWithBindVariableCompareToBigDecimal() {
        SingleDrlxParseSuccess result = (SingleDrlxParseSuccess) parser.drlxParse(Person.class, "$p", "money == likes * 10"); // assuming likes contains number String

        assertThat(result.getExpr().toString()).isEqualTo(EvaluationUtil.class.getCanonicalName() + ".equals(" + EvaluationUtil.class.getCanonicalName() + ".toBigDecimal(_this.getMoney()), " + EvaluationUtil.class.getCanonicalName() + ".toBigDecimal(Double.valueOf(_this.getLikes()) * 10))");
    }

    @Test
    public void testBigDecimalLiteralWithBindVariable() {
        SingleDrlxParseSuccess result = (SingleDrlxParseSuccess) parser.drlxParse(Person.class, "$p", "$bd : 10.3B");

        assertThat(result.getExpr().toString()).isEqualTo("new java.math.BigDecimal(\"10.3\")");
    }

    @Test
    public void testBigIntegerLiteralWithBindVariable() {
        SingleDrlxParseSuccess result = (SingleDrlxParseSuccess) parser.drlxParse(Person.class, "$p", "$bi : 10I");

        assertThat(result.getExpr().toString()).isEqualTo("new java.math.BigInteger(\"10\")");
    }
}
