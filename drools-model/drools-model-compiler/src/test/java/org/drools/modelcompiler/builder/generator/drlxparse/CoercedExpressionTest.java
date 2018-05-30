package org.drools.modelcompiler.builder.generator.drlxparse;

import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.TypedExpression;
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
    public void test222x1() {
        /*


    222 left = TypedExpression{expression=_this.getName(), jpType=MethodCallExpr, type=class java.lang.String, fieldName='name', unificationVariable=Optional.empty, unificationName=Optional.empty}
    222 right = TypedExpression{expression=40, jpType=IntegerLiteralExpr, type=int, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}
    222 right = TypedExpression{expression=String.valueOf(40), jpType=MethodCallExpr, type=class java.lang.String, fieldName='null', unificationVariable=Optional.empty, unificationName=Optional.empty}

         */
        final TypedExpression left = expr("_this.getName()", String.class);
        final TypedExpression right = expr("40", int.class);
        final CoercedExpression.CoercedExpressionResult coerce = new CoercedExpression(left, right).coerce();
        assertEquals(expr("String.valueOf(40)", int.class), coerce.getCoercedRight() );
    }

    private TypedExpression expr(String leftStr, Class<?> leftClass) {
        return new TypedExpression(DrlxParseUtil.parseExpression(leftStr).getExpr(), leftClass);
    }
}