package org.drools.compiler.rule.builder.util;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.OperatorDescr;
import org.drools.compiler.lang.descr.RelationalExprDescr;
import org.drools.core.base.ClassObjectType;
import org.drools.core.rule.Pattern;
import org.mvel2.util.PropertyTools;

public class ConstraintUtil {

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

        Class<?> clazz = pattern.getObjectType().getClassType();

        String leftProp = getFirstProp(leftValue);
        String rightProp = getFirstProp(rightValue);

        OperatorDescr operatorDescr = relDescr.getOperatorDescr();

        if (!(pattern.getObjectType() instanceof ClassObjectType)) {
            // do not inverse
            return expression;
        }

        if (!operator.equals(operatorDescr.getOperator())) {
            // do not inverse
            return expression;
        }
        if (canInverse(operator) && (PropertyTools.getFieldOrAccessor(clazz, leftProp) == null) && ((PropertyTools.getFieldOrAccessor(clazz, rightProp) != null) || (rightProp.equals("this")))) {

            boolean negate = false;
            if (isNagatedExpression(expression, leftValue, rightValue, operator)) {
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

            String inversedOperator = inverseOperator(operator);

            operatorDescr.setOperator(inversedOperator);

            StringBuilder sb = new StringBuilder();
            String inversedExpression = sb.append(rightValue).append(" ").append(inversedOperator).append(" ").append(leftValue).toString();
            if (negate) {
                inversedExpression = "!( " + inversedExpression + " )";
            }

            return inversedExpression;
        }

        return expression;
    }

    private static String getFirstProp(String str) {
        int i = str.indexOf(".");
        return (i != -1) ? str.substring(0, i) : str;
    }

    private static boolean isNagatedExpression(String expression, String leftValue, String rightValue, String operator) {
        return expression.matches("^!\\s*\\(\\s*" + leftValue + "\\s*" + operator + "\\s*" + rightValue + "\\s*\\)$");
    }

    private static boolean canInverse(String operator) {
        return (operator.equals("==") || operator.equals("!=") || operator.equals(">") || operator.equals("<") || operator.equals(">=") || operator.equals("<="));
    }

    private static String inverseOperator(String operator) {
        switch (operator) {
            case ">":
                return "<";
            case "<":
                return ">";
            case ">=":
                return "<=";
            case "<=":
                return ">=";
            default:
                return operator;
        }
    }
}
