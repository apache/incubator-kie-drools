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
package org.drools.model.codegen.execmodel.generator.drlxparse;

import java.util.Map;

import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.util.MethodUtils;
import org.drools.model.codegen.execmodel.generator.DrlxParseUtil;
import org.drools.model.codegen.execmodel.generator.TypedExpression;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.THIS_PLACEHOLDER;

public class CoercedExpressionTest {

    @Test
    public void avoidCoercing() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAge()", int.class);
        final TypedExpression right = expr("10", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("10", int.class));
    }

    @Test
    public void avoidCoercing2() {
        final TypedExpression left = expr("$pr.compareTo(new BigDecimal(\"0.0\"))", int.class);
        final TypedExpression right = expr("0", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("0", int.class));
    }

    @Test
    public void intToDouble() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".doubleValue()", double.class);
        final TypedExpression right = expr("0", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("0.0", double.class));
    }

    @Test
    public void charToString() {
        final TypedExpression left = expr(THIS_PLACEHOLDER, java.lang.String.class);
        final TypedExpression right = expr("\'x'", char.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        final TypedExpression expected = new TypedExpression(new StringLiteralExpr("x"), String.class);
        assertThat(coerce.getCoercedRight()).isEqualTo(expected);
    }

    @Test
    public void stringToInt() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getName()", String.class);
        final TypedExpression right = expr("40", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("String.valueOf(40)", String.class));
    }

    @Test
    public void stringToInt2() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAge()", int.class);
        final TypedExpression right = expr("\"50\"", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedLeft()).isEqualTo(expr(THIS_PLACEHOLDER + ".getAge()", int.class));
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("50", int.class));
    }

    @Test
    public void charToStringOnLeft() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getCharPrimitive()", char.class);
        final TypedExpression right = expr("$c1", java.lang.String.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedLeft()).isEqualTo(expr("String.valueOf(_this.getCharPrimitive())", String.class));
    }

    @Test
    public void avoidCoercingStrings() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getName()", String.class);
        final TypedExpression right = expr("\"50\"", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("\"50\"", String.class));
    }

    @Test
    public void avoidCoercingStrings2() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAge()", int.class);
        final TypedExpression right = expr("\"50\"", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("50", int.class));
    }

    @Test
    public void avoidCoercingBinaryExpressions () {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAddress().getCity() == \"Brno\" && _this.getAddress().getStreet() == \"Technology Park\"", String.class);
        final TypedExpression right = expr(THIS_PLACEHOLDER + ".getAddress().getNumber() == 1", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedLeft()).isEqualTo(expr(THIS_PLACEHOLDER + ".getAddress().getCity() == \"Brno\" && _this.getAddress().getStreet() == \"Technology Park\"", String.class));
        assertThat(coerce.getCoercedRight()).isEqualTo(expr(THIS_PLACEHOLDER + ".getAddress().getNumber() == 1", int.class));
    }

    @Test
    public void castToShort() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAgeAsShort()", java.lang.Short.class);
        final TypedExpression right = expr("40", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("(short)40", int.class));
    }

    @Test
    public void castMaps() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAge()", Integer.class);
        final TypedExpression right = expr("$m.get(\"age\")", java.util.Map.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("(java.lang.Integer)$m.get(\"age\")", Map.class));
    }

    @Test
    public void doNotCastNumberLiteralInt() {
        final TypedExpression left = expr("getValue()", java.lang.Object.class);
        final TypedExpression right = expr("20", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("20", int.class));
    }

    @Test
    public void doNotCastNumberLiteralShort() {
        final TypedExpression left = expr("getValue()", java.lang.Object.class);
        final TypedExpression right = expr("20", short.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("20", short.class));
    }

    @Test
    public void doNotCastNumberLiteralDouble() {
        final TypedExpression left = expr("getValue()", java.lang.Object.class);
        final TypedExpression right = expr("20", double.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("20", double.class));
    }

    @Test
    public void doNotCastNameExprLiterals() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAgeAsShort()", java.lang.Short.class);
        final TypedExpression right = expr("$age", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("$age", int.class));
    }

    @Test
    public void doNotCastNameExprLiterals2() {
        final TypedExpression left = expr("exprDouble", java.lang.Double.class);
        final TypedExpression right = expr("$age", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("$age", int.class));
    }

    @Test
    public void doNotCast() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".intValue()", int.class);
        final TypedExpression right = expr("$one << $shift", long.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("$one << $shift", long.class));
    }

    @Test
    public void doNotCastNullLiteral() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".isApproved()", java.lang.Boolean.class);
        final TypedExpression right = expr("null", MethodUtils.NullType.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("null", MethodUtils.NullType.class));
    }

    private TypedExpression expr(String exprString, Class<?> exprClass) {
        return new TypedExpression(DrlxParseUtil.parseExpression(exprString).getExpr(), exprClass);
    }

    @Test
    @Ignore("should support bigDecimal coercion also?")
    public void coerceBigDecimal() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getRate()", java.math.BigDecimal.class);
        final TypedExpression right = expr("0.0d", Double.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("java.math.BigDecimal.valueOf(0.0)", double.class));
    }

    @Test
    public void testStringToBooleanTrue() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getBooleanValue", Boolean.class);
        final TypedExpression right = expr("\"true\"", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("true", Boolean.class));
    }

    @Test
    public void testStringToBooleanFalse() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getBooleanValue", Boolean.class);
        final TypedExpression right = expr("\"false\"", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("false", Boolean.class));
    }

    @Test
    public void testStringToBooleanRandomStringError() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getBooleanValue", Boolean.class);
        final TypedExpression right = expr("\"randomString\"", String.class);
        assertThatThrownBy(() -> new CoercedExpression(left, right, false).coerce())
                .isInstanceOf(CoercedExpression.CoercedExpressionException.class);
    }

    @Test
    public void testIntegerToBooleanError() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getBooleanValue", Boolean.class);
        final TypedExpression right = expr("1", Integer.class);
        assertThatThrownBy(() -> new CoercedExpression(left, right, false).coerce())
                .isInstanceOf(CoercedExpression.CoercedExpressionException.class);
    }

    @Test
    public void testNameExprToString() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getName", String.class);
        final TypedExpression right = expr("$maxName", Comparable.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, true).coerce();
        assertThat(coerce.getCoercedRight()).isEqualTo(expr("(java.lang.String) $maxName", String.class));
    }
}