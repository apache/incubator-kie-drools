package org.drools.compiler.rule.builder.util;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.OperatorDescr;
import org.drools.compiler.lang.descr.RelationalExprDescr;
import org.drools.core.base.ClassObjectType;
import org.drools.core.rule.Pattern;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.index.IndexUtil;

public class ConstraintUtil {

    public static final String DROOLS_NORMALIZE_CONSTRAINT = "drools.normalize.constraint";

    private static final boolean ENABLE_NORMALIZE = Boolean.parseBoolean(System.getProperty(DROOLS_NORMALIZE_CONSTRAINT, "true"));

    private ConstraintUtil() {}

    /**
     * Swap left and right operands in a constraint when a fact property is located on the right side.
     * 
     * e.g. Person(20 < age) should be normalized to Person(age > 20)
     * 
     * @param expression
     * @param operator 
     * @param rightValue 
     * @param leftValue 
     * @return Normalized <code>expression</code>
     */
    public static String inverseExpression(RelationalExprDescr relDescr, String expression, String leftValue, String rightValue, String operator, Pattern pattern) {
        if (!ENABLE_NORMALIZE) {
            return expression;
        }
        Class<?> clazz = pattern.getObjectType().getClassType();

        String leftProp = getFirstProp(leftValue);
        String rightProp = getFirstProp(rightValue);

        OperatorDescr operatorDescr = relDescr.getOperatorDescr();

        if (canInverse(pattern, operator, operatorDescr, leftProp, rightProp) && isPropertyOnRight(clazz, leftProp, rightProp)) {
            boolean negate = false;
            if ( isNegatedExpression(expression, leftValue, rightValue, operator)) {
                if (relDescr.getOperatorDescr().isNegated()) {
                    negate = true;
                } else {
                    // do not inverse
                    return expression;
                }
            }

            BaseDescr left = relDescr.getLeft();
            relDescr.setLeft(relDescr.getRight());
            relDescr.setRight(left);

            String inversedOperator = IndexUtil.ConstraintType.decode(operator).inverse().getOperator();

            operatorDescr.setOperator(inversedOperator);

            StringBuilder sb = new StringBuilder();
            String inversedExpression = sb.append(rightValue).append(" ").append(inversedOperator).append(" ").append(leftValue).toString();
            if (negate) {
                inversedExpression = "!( " + inversedExpression + " )";
            }

            return inversedExpression;
        }

        // do not inverse
        return expression;
    }

    private static boolean isPropertyOnRight(Class<?> clazz, String leftProp, String rightProp) {
        return (ClassUtils.getFieldOrAccessor(clazz, leftProp) == null) && ((ClassUtils.getFieldOrAccessor(clazz, rightProp) != null) || (rightProp.equals("this")));
    }

    private static boolean canInverse(Pattern pattern, String operator, OperatorDescr operatorDescr, String leftProp, String rightProp) {
        if (!(pattern.getObjectType() instanceof ClassObjectType)) {
            return false;
        }
        if (!operator.equals(operatorDescr.getOperator())) {
            return false;
        }
        if (leftProp.isEmpty() || rightProp.isEmpty()) {
            return false;
        }
        return IndexUtil.ConstraintType.decode(operator).canInverse();
    }

    private static String getFirstProp(String str) {
        int idxDot = str.indexOf('.');
        int idxBracket = str.indexOf('[');
        if (idxDot == -1 && idxBracket == -1) {
            return str;
        } else if (idxDot != -1 && idxBracket != -1) {
            return str.substring(0, Math.min(idxDot, idxBracket)); // pick smaller
        } else {
            return str.substring(0, Math.max(idxDot, idxBracket)); // pick not -1
        }
    }

    private static boolean isNegatedExpression( String expression, String leftValue, String rightValue, String operator ) {
        return expression.matches("^!\\s*\\(\\s*\\Q" + leftValue + "\\E\\s*" + operator + "\\s*\\Q" + rightValue + "\\E\\s*\\)$");
    }
}
