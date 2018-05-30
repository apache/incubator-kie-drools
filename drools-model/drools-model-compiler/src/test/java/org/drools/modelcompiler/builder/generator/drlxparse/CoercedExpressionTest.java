package org.drools.modelcompiler.builder.generator.drlxparse;

import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CoercedExpressionTest {

    @Test
    public void test444() {

        /*
        444 left = TypedExpression{expression=_this.getAge(), jpType=MethodCallExpr, type=int, fieldName='age', unificationVariable=Optional.empty, unificationName=Optional.empty}
        444 right = TypedExpression{expression=10, jpType=IntegerLiteralExpr, type=int, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}
        444 right = TypedExpression{expression=10, jpType=IntegerLiteralExpr, type=int, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}

         */
        final TypedExpression left = expr("_this.getAge()", int.class);
        final TypedExpression right = expr("10", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right).coerce();
        assertEquals(expr("10", int.class), coerce.getCoercedRight() );
    }

    @Test
    public void test444x2() {

        /*

        444 left = TypedExpression{expression=_this.doubleValue(), jpType=MethodCallExpr, type=double, fieldName='doubleValue', unificationVariable=Optional.empty, unificationName=Optional.empty}
        444 right = TypedExpression{expression=0, jpType=IntegerLiteralExpr, type=int, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}
        444 right = TypedExpression{expression=0d, jpType=DoubleLiteralExpr, type=int, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}

         */
        final TypedExpression left = expr("_this.doubleValue()", double.class);
        final TypedExpression right = expr("0", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right).coerce();
        assertEquals(expr("0d", int.class), coerce.getCoercedRight() );
    }


    @Test
    public void test444x4() {

        /*


222 left = TypedExpression{expression=_this, jpType=NameExpr, type=class java.lang.String, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}
222 right = TypedExpression{expression='x', jpType=CharLiteralExpr, type=char, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}
222 right = TypedExpression{expression="x", jpType=StringLiteralExpr, type=class java.lang.String, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}


         */
        final TypedExpression left = expr("_this", java.lang.String.class);
        final TypedExpression right = expr("\'x'", char.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right).coerce();
        final TypedExpression expected = new TypedExpression(new StringLiteralExpr("x"), String.class);
        assertEquals(expected, coerce.getCoercedRight() );
    }


    @Test
    public void test444x3() {

        /*

444 left = TypedExpression{expression=$pr.compareTo(new BigDecimal("0.0")), jpType=MethodCallExpr, type=int, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}
444 right = TypedExpression{expression=0, jpType=IntegerLiteralExpr, type=int, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}
444 right = TypedExpression{expression=0, jpType=IntegerLiteralExpr, type=int, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}

         */
        final TypedExpression left = expr("$pr.compareTo(new BigDecimal(\"0.0\"))", int.class);
        final TypedExpression right = expr("0", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right).coerce();
        assertEquals(expr("0", int.class), coerce.getCoercedRight() );
    }

    @Test
    @Ignore("should support bigDecimal coercion also?")
    public void test444x6() {

        /*

ZZZ left = TypedExpression{expression=_this.getRate(), jpType=MethodCallExpr, type=class java.math.BigDecimal, fieldName='rate', unificationVariable=Optional.empty, unificationName=Optional.empty}
ZZZ right = TypedExpression{expression=0.0, jpType=DoubleLiteralExpr, type=double, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}
ZZZ right = TypedExpression{expression=java.math.BigDecimal.valueOf(0.0), jpType=MethodCallExpr, type=double, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}



         */
        final TypedExpression left = expr("_this.getRate()", java.math.BigDecimal.class);
        final TypedExpression right = expr("0.0d", Double.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right).coerce();
        assertEquals(expr("java.math.BigDecimal.valueOf(0.0)", double.class), coerce.getCoercedRight() );
    }

    @Test
    public void test222x1() {
        /*


    222 left = TypedExpression{expression=_this.getName(), jpType=MethodCallExpr, type=class java.lang.String, fieldName='name', unificationVariable=Optional.empty, unificationName=Optional.empty}
    222 right = TypedExpression{expression=40, jpType=IntegerLiteralExpr, type=int, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}
    222 right = TypedExpression{expression=String.valueOf(40), jpType=MethodCallExpr, type=class java.lang.String, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}

         */
        final TypedExpression left = expr("_this.getName()", String.class);
        final TypedExpression right = expr("40", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right).coerce();
        assertEquals(expr("String.valueOf(40)", String.class), coerce.getCoercedRight() );
    }

    @Test
    public void test333() {
        /*


333 left = TypedExpression{expression=_this.getAge(), jpType=MethodCallExpr, type=int, fieldName='age', unificationVariable=Optional.empty, unificationName=Optional.empty}
333 right = TypedExpression{expression="50", jpType=StringLiteralExpr, type=class java.lang.String, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}
333 right = TypedExpression{expression=50, jpType=IntegerLiteralExpr, type=class java.lang.String, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}

         */
        final TypedExpression left = expr("_this.getAge()", int.class);
        final TypedExpression right = expr("\"50\"", String.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right).coerce();
        assertEquals(expr("50", String.class), coerce.getCoercedRight() );
    }

    @Test
    public void test555() {
        /*


555 left = TypedExpression{expression=_this.getItems().get((Integer) 1), jpType=MethodCallExpr, type=class java.lang.Object, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}
555 right = TypedExpression{expression=2000, jpType=IntegerLiteralExpr, type=int, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}
555 right = TypedExpression{expression=(java.lang.Object) 2000, jpType=CastExpr, type=int, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}



         */
        final TypedExpression left = expr("_this.getItems().get((Integer) 1)", java.lang.Object.class);
        final TypedExpression right = expr("2000", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right).coerce();
        assertEquals(expr("(java.lang.Object)2000", int.class), coerce.getCoercedRight() );
    }

    private TypedExpression expr(String leftStr, Class<?> leftClass) {
        return new TypedExpression(DrlxParseUtil.parseExpression(leftStr).getExpr(), leftClass);
    }
}