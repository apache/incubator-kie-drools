package org.drools.mvelcompiler.util;

import java.math.BigDecimal;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static com.github.javaparser.ast.NodeList.nodeList;

public class BigDecimalArgumentCoercion {

    public Expression coercedArgument(Class<?> argumentType, Class<?> actualType, Expression argument) {
        boolean argumentTypeIsBigDecimal = BigDecimal.class.isAssignableFrom(argumentType);

        if (isInteger(actualType) && argumentTypeIsBigDecimal) {
            return bigDecimalToPrimitive(argument, "intValue");
        } else if (isLong(actualType) && argumentTypeIsBigDecimal) {
            return bigDecimalToPrimitive(argument, "longValue");
        } else if (isShort(actualType) && argumentTypeIsBigDecimal) {
            return bigDecimalToPrimitive(argument, "shortValue");
        } else if (isDouble(actualType) && argumentTypeIsBigDecimal) {
            return bigDecimalToPrimitive(argument, "doubleValue");
        } else if (isFloat(actualType) && argumentTypeIsBigDecimal) {
            return bigDecimalToPrimitive(argument, "floatValue");
        }

        return argument;
    }

    private MethodCallExpr bigDecimalToPrimitive(Expression argumentExpression, String intValue) {
        return new MethodCallExpr(argumentExpression, intValue, nodeList());
    }

    private boolean isInteger(Class<?> actualType) {
        return actualType == int.class || actualType == Integer.class;
    }

    private boolean isLong(Class<?> actualType) {
        return actualType == long.class || actualType == Long.class;
    }

    private boolean isFloat(Class<?> actualType) {
        return actualType == float.class || actualType == Float.class;
    }

    private boolean isDouble(Class<?> actualType) {
        return actualType == double.class || actualType == Double.class;
    }

    private boolean isShort(Class<?> actualType) {
        return actualType == short.class || actualType == Short.class;
    }
}
