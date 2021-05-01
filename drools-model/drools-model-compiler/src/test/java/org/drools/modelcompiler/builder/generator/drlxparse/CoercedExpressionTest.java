package org.drools.modelcompiler.builder.generator.drlxparse;

import java.util.Map;

import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.assertj.core.api.Assertions;
import org.drools.core.util.MethodUtils;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.junit.Ignore;
import org.junit.Test;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.THIS_PLACEHOLDER;
import static org.junit.Assert.*;

public class CoercedExpressionTest {

    @Test
    public void avoidCoercing() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAge()", int.class);
        final TypedExpression right = expr("10", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("10", int.class), coerce.getCoercedRight());
    }

    @Test
    public void avoidCoercing2() {
        final TypedExpression left = expr("$pr.compareTo(new BigDecimal(\"0.0\"))", int.class);
        final TypedExpression right = expr("0", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("0", int.class), coerce.getCoercedRight());
    }

    @Test
    public void intToDouble() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".doubleValue()", double.class);
        final TypedExpression right = expr("0", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("0.0", double.class), coerce.getCoercedRight());
    }

    @Test
    public void charToString() {
        final TypedExpression left = expr(THIS_PLACEHOLDER, java.lang.String.class);
        final TypedExpression right = expr("\'x'", char.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        final TypedExpression expected = new TypedExpression(new StringLiteralExpr("x"), String.class);
        assertEquals(expected, coerce.getCoercedRight());
    }

    @Test
    public void stringToInt() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getName()", String.class);
        final TypedExpression right = expr("40", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("String.valueOf(40)", String.class), coerce.getCoercedRight());
    }

    @Test
    public void stringToInt2() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAge()", int.class);
        final TypedExpression right = expr("\"50\"", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr(THIS_PLACEHOLDER + ".getAge()", int.class), coerce.getCoercedLeft());
        assertEquals(expr("50", int.class), coerce.getCoercedRight());
    }

    @Test
    public void charToStringOnLeft() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getCharPrimitive()", char.class);
        final TypedExpression right = expr("$c1", java.lang.String.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("String.valueOf(_this.getCharPrimitive())", String.class), coerce.getCoercedLeft());
    }

    @Test
    public void avoidCoercingStrings() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getName()", String.class);
        final TypedExpression right = expr("\"50\"", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("\"50\"", String.class), coerce.getCoercedRight());
    }

    @Test
    public void avoidCoercingStrings2() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAge()", int.class);
        final TypedExpression right = expr("\"50\"", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("50", int.class), coerce.getCoercedRight());
    }

    @Test
    public void avoidCoercingBinaryExpressions () {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAddress().getCity() == \"Brno\" && _this.getAddress().getStreet() == \"Technology Park\"", String.class);
        final TypedExpression right = expr(THIS_PLACEHOLDER + ".getAddress().getNumber() == 1", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr(THIS_PLACEHOLDER + ".getAddress().getCity() == \"Brno\" && _this.getAddress().getStreet() == \"Technology Park\"", String.class), coerce.getCoercedLeft());
        assertEquals(expr(THIS_PLACEHOLDER + ".getAddress().getNumber() == 1", int.class), coerce.getCoercedRight());
    }

    @Test
    public void castToShort() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAgeAsShort()", java.lang.Short.class);
        final TypedExpression right = expr("40", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("(short)40", int.class), coerce.getCoercedRight());
    }

    @Test
    public void castMaps() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAge()", Integer.class);
        final TypedExpression right = expr("$m.get(\"age\")", java.util.Map.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("(java.lang.Integer)$m.get(\"age\")", Map.class), coerce.getCoercedRight());
    }

    @Test
    public void doNotCastNumberLiteralInt() {
        final TypedExpression left = expr("getValue()", java.lang.Object.class);
        final TypedExpression right = expr("20", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("20", int.class), coerce.getCoercedRight());
    }

    @Test
    public void doNotCastNumberLiteralShort() {
        final TypedExpression left = expr("getValue()", java.lang.Object.class);
        final TypedExpression right = expr("20", short.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("20", short.class), coerce.getCoercedRight());
    }

    @Test
    public void doNotCastNumberLiteralDouble() {
        final TypedExpression left = expr("getValue()", java.lang.Object.class);
        final TypedExpression right = expr("20", double.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("20", double.class), coerce.getCoercedRight());
    }

    @Test
    public void doNotCastNameExprLiterals() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAgeAsShort()", java.lang.Short.class);
        final TypedExpression right = expr("$age", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("$age", int.class), coerce.getCoercedRight());
    }

    @Test
    public void doNotCastNameExprLiterals2() {
        final TypedExpression left = expr("exprDouble", java.lang.Double.class);
        final TypedExpression right = expr("$age", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("$age", int.class), coerce.getCoercedRight());
    }

    @Test
    public void doNotCast() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".intValue()", int.class);
        final TypedExpression right = expr("$one << $shift", long.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("$one << $shift", long.class), coerce.getCoercedRight());
    }

    @Test
    public void doNotCastNullLiteral() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".isApproved()", java.lang.Boolean.class);
        final TypedExpression right = expr("null", MethodUtils.NullType.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("null", MethodUtils.NullType.class), coerce.getCoercedRight());
    }

    @Test(expected = CoercedExpression.CoercedExpressionException.class)
    public void testException() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getAge()", int.class);
        final TypedExpression right = expr("rage", java.lang.Object.class);
        new CoercedExpression(left, right, false).coerce();
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
        assertEquals(expr("java.math.BigDecimal.valueOf(0.0)", double.class), coerce.getCoercedRight());
    }

    @Test
    public void testStringToBooleanTrue() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getBooleanValue", Boolean.class);
        final TypedExpression right = expr("\"true\"", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("true", Boolean.class), coerce.getCoercedRight());
    }

    @Test
    public void testStringToBooleanFalse() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getBooleanValue", Boolean.class);
        final TypedExpression right = expr("\"false\"", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right, false).coerce();
        assertEquals(expr("false", Boolean.class), coerce.getCoercedRight());
    }

    @Test
    public void testStringToBooleanRandomStringError() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getBooleanValue", Boolean.class);
        final TypedExpression right = expr("\"randomString\"", String.class);
        Assertions.assertThatThrownBy(() -> new CoercedExpression(left, right, false).coerce())
                .isInstanceOf(CoercedExpression.CoercedExpressionException.class);
    }

    @Test
    public void testIntegerToBooleanError() {
        final TypedExpression left = expr(THIS_PLACEHOLDER + ".getBooleanValue", Boolean.class);
        final TypedExpression right = expr("1", Integer.class);
        Assertions.assertThatThrownBy(() -> new CoercedExpression(left, right, false).coerce())
                .isInstanceOf(CoercedExpression.CoercedExpressionException.class);
    }
}