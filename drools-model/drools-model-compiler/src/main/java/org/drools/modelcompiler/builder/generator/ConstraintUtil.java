package org.drools.modelcompiler.builder.generator;

import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.UnaryExpr;
import org.drools.model.Index.ConstraintType;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.modelcompiler.util.EvaluationUtil;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.THIS_PLACEHOLDER;

public class ConstraintUtil {

    private static final String CLASS_NAME = EvaluationUtil.class.getCanonicalName() + ".";

    // This is required to detect BigDecimal property from generated MethodCallExpr
    private static final String TO_BIG_DECIMAL = EvaluationUtil.class.getCanonicalName() + ".toBigDecimal";

    private static final String GREATER_THAN_PREFIX = "greaterThan";
    private static final String GREATER_OR_EQUAL_PREFIX = "greaterOrEqual";
    private static final String LESS_THAN_PREFIX = "lessThan";
    private static final String LESS_OR_EQUAL_PREFIX = "lessOrEqual";

    private static final boolean ENABLE_NORMALIZE = Boolean.parseBoolean(System.getProperty(org.drools.compiler.rule.builder.util.ConstraintUtil.DROOLS_NORMALIZE_CONSTRAINT, "true"));

    private ConstraintUtil() {}

    /**
     * Swap left and right operands in a constraint when a fact property is located on the right side.
     * 
     * e.g. Person(20 < age) should be normalized to Person(age > 20)
     * 
     * @param drlxParseResult
     * @return Normalized <code>DrlxParseResult</code>
     */
    public static DrlxParseResult normalizeConstraint(DrlxParseResult drlxParseResult) {
        if (!ENABLE_NORMALIZE) {
            return drlxParseResult;
        }

        if (drlxParseResult instanceof SingleDrlxParseSuccess) {
            // Create a copy
            SingleDrlxParseSuccess drlx = new SingleDrlxParseSuccess((SingleDrlxParseSuccess) drlxParseResult);

            Expression expr = drlx.getExpr();
            if (expr == null) {
                return drlxParseResult;
            }

            if (expr instanceof MethodCallExpr) {
                processTopLevelExpression(drlx, (MethodCallExpr) expr);
            } else if (expr instanceof EnclosedExpr) {
                Expression inner = stripEnclosedExpr((EnclosedExpr) expr);
                if (inner instanceof MethodCallExpr) {
                    processTopLevelExpression(drlx, (MethodCallExpr) inner);
                } else {
                    processExpression(expr);
                }
            } else {
                processExpression(expr);
            }

            return drlx;
        }
        return drlxParseResult;
    }

    private static Expression stripEnclosedExpr(EnclosedExpr eExpr) {
        Expression inner = eExpr.getInner();
        if (inner instanceof EnclosedExpr) {
            return stripEnclosedExpr((EnclosedExpr) inner);
        } else {
            return inner;
        }
    }

    private static void processTopLevelExpression(SingleDrlxParseSuccess drlx, MethodCallExpr mcExpr) {
        // Modify SingleDrlxParseSuccess when a top level constraint is modified
        if (canInverse(drlx) && canInverse(mcExpr)) {
            inverseSingleDrlxParseSuccess(drlx);
            inverseMethodCallExpr(mcExpr);
        }
    }

    private static void processExpression(Expression expr) {
        if (expr instanceof MethodCallExpr) {
            MethodCallExpr mcExpr = (MethodCallExpr) expr;
            if (canInverse(mcExpr)) {
                inverseMethodCallExpr(mcExpr);
            }
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr bExpr = (BinaryExpr) expr;
            if (bExpr.getOperator() == BinaryExpr.Operator.AND || bExpr.getOperator() == BinaryExpr.Operator.OR) {
                Expression left = bExpr.getLeft();
                processExpression(left);
                Expression right = bExpr.getRight();
                processExpression(right);
            }
        } else if (expr instanceof UnaryExpr) {
            Expression expression = ((UnaryExpr) expr).getExpression();
            processExpression(expression);
        } else if (expr instanceof EnclosedExpr) {
            Expression inner = ((EnclosedExpr) expr).getInner();
            processExpression(inner);
        }
    }

    private static boolean canInverse(MethodCallExpr mcExpr) {
        String mcExprName = mcExpr.getName().asString();
        if (!mcExprName.startsWith(CLASS_NAME)) {
            return false;
        }
        NodeList<Expression> arguments = mcExpr.getArguments();
        if (arguments == null || arguments.size() != 2) {
            return false;
        }
        Expression left = arguments.get(0);
        Expression right = arguments.get(1);

        return isPropertyOnRight(left, right);
    }

    private static boolean canInverse(SingleDrlxParseSuccess drlx) {
        ConstraintType type = drlx.getDecodeConstraintType();
        TypedExpression left = drlx.getLeft();
        TypedExpression right = drlx.getRight();
        if (type != null && left != null && right != null && type.canInverse()) {
            return isPropertyOnRight(left.getExpression(), right.getExpression());
        } else {
            return false;
        }
    }

    private static boolean isPropertyOnRight(Expression left, Expression right) {
        return !isProperty(left) && isProperty(right);
    }

    private static boolean isProperty(Expression expr) {
        if (expr instanceof CastExpr) {
            expr = ((CastExpr) expr).getExpression();
        }
        if (expr instanceof MethodCallExpr) {
            MethodCallExpr mcExpr = (MethodCallExpr) expr;
            if (mcExpr.getName().asString().equals(TO_BIG_DECIMAL) && mcExpr.getArgument(0) instanceof MethodCallExpr) {
                mcExpr = (MethodCallExpr) mcExpr.getArgument(0);
            }
            Optional<Expression> thisScope = getRootScope(mcExpr).filter(scope -> scope.equals(new NameExpr(THIS_PLACEHOLDER)));
            if (thisScope.isPresent()) {
                return true;
            }
        }
        return false;
    }

    private static Optional<Expression> getRootScope(MethodCallExpr mcExpr) {
        // to get "_this" from nested property like "_this.getAdress().getCity()"
        return mcExpr.getScope().flatMap(scope -> {
            if (scope instanceof NameExpr) {
                return Optional.of(scope);
            } else if (scope instanceof MethodCallExpr) {
                return getRootScope((MethodCallExpr) scope);
            } else {
                return Optional.empty();
            }
        });
    }

    private static void inverseSingleDrlxParseSuccess(SingleDrlxParseSuccess drlx) {
        ConstraintType inversedOperator = drlx.getDecodeConstraintType().inverse();

        TypedExpression left = drlx.getLeft();
        drlx.setLeft(drlx.getRight());
        drlx.setRight(left);
        drlx.setDecodeConstraintType(inversedOperator);
    }

    private static void inverseMethodCallExpr(MethodCallExpr mcExpr) {
        String mcExprName = mcExpr.getName().asString();

        String methodName = mcExprName.substring(CLASS_NAME.length(), mcExprName.length());
        NodeList<Expression> arguments = mcExpr.getArguments();

        if (methodName.startsWith(GREATER_THAN_PREFIX)) {
            methodName = methodName.replaceFirst(GREATER_THAN_PREFIX, LESS_THAN_PREFIX);
        } else if (methodName.startsWith(GREATER_OR_EQUAL_PREFIX)) {
            methodName = methodName.replaceFirst(GREATER_OR_EQUAL_PREFIX, LESS_OR_EQUAL_PREFIX);
        } else if (methodName.startsWith(LESS_THAN_PREFIX)) {
            methodName = methodName.replaceFirst(LESS_THAN_PREFIX, GREATER_THAN_PREFIX);
        } else if (methodName.startsWith(LESS_OR_EQUAL_PREFIX)) {
            methodName = methodName.replaceFirst(LESS_OR_EQUAL_PREFIX, GREATER_OR_EQUAL_PREFIX);
        }

        mcExpr.setName(new SimpleName(CLASS_NAME + methodName));
        Expression firstArg = arguments.get(0);
        mcExpr.setArgument(0, arguments.get(1));
        mcExpr.setArgument(1, firstArg);
    }
}
