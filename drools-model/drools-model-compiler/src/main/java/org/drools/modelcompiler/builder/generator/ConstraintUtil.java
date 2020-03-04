package org.drools.modelcompiler.builder.generator;

import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.UnaryExpr;
import org.drools.model.Index;
import org.drools.model.Index.ConstraintType;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.visitor.pattern.PatternConstraintParseResult;
import org.drools.modelcompiler.util.EvaluationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.THIS_PLACEHOLDER;

public class ConstraintUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConstraintUtil.class);

    private static final String CLASS_NAME = EvaluationUtil.class.getCanonicalName() + ".";
    private static final String GREATER_THAN_PREFIX = "greaterThan";
    private static final String GREATER_OR_EQUAL_PREFIX = "greaterOrEqual";
    private static final String LESS_THAN_PREFIX = "lessThan";
    private static final String LESS_OR_EQUAL_PREFIX = "lessOrEqual";

    public static final String DROOLS_NORMALIZE_CONSTRAINT = "drools.normalize.constraint";

    private static final boolean ENABLE_NORMALIZE = Boolean.parseBoolean(System.getProperty(DROOLS_NORMALIZE_CONSTRAINT, "true"));

    private ConstraintUtil() {}

    public static DrlxParseResult normalizeConstraint(DrlxParseResult drlxParseResult) {
        if (!ENABLE_NORMALIZE) {
            return drlxParseResult;
        }

        if (drlxParseResult instanceof SingleDrlxParseSuccess) {
            // Create copy
            SingleDrlxParseSuccess s = new SingleDrlxParseSuccess((SingleDrlxParseSuccess) drlxParseResult);

            Expression expr = s.getExpr();
            if (expr == null) {
                return drlxParseResult;
            }

            if (expr instanceof MethodCallExpr) {
                processTopLevelExpression(s, (MethodCallExpr) expr);
            } else if (expr instanceof EnclosedExpr) {
                Expression inner = stripEnclosedExpr((EnclosedExpr) expr);
                if (inner instanceof MethodCallExpr) {
                    processTopLevelExpression(s, (MethodCallExpr) inner);
                } else {
                    processExpression(expr);
                }
            } else {
                processExpression(expr);
            }

            return s;
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

    private static void processTopLevelExpression(SingleDrlxParseSuccess s, MethodCallExpr mExpr) {
        // Modify SingleDrlxParseSuccess when a top level constraint is modified
        if (canInverse(s) && canInverse(mExpr)) {
            inverseSingleDrlxParseSuccess(s);
            inverseMethodCallExpr(mExpr);
        }
    }

    private static void processExpression(Expression expr) {
        if (expr instanceof MethodCallExpr) {
            MethodCallExpr mExpr = (MethodCallExpr) expr;
            if (canInverse(mExpr)) {
                inverseMethodCallExpr(mExpr);
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

    private static boolean canInverse(MethodCallExpr mExpr) {
        String mExprName = mExpr.getName().asString();
        if (!mExprName.startsWith(CLASS_NAME)) {
            return false;
        }
        NodeList<Expression> arguments = mExpr.getArguments();
        if (arguments == null || arguments.size() != 2) {
            return false;
        }
        Expression left = arguments.get(0);
        Expression right = arguments.get(1);

        return isPropertyOnRight(left, right);
    }

    private static boolean canInverse(SingleDrlxParseSuccess s) {
        ConstraintType type = s.getDecodeConstraintType();
        TypedExpression left = s.getLeft();
        TypedExpression right = s.getRight();
        if (type != null && left != null && right != null && (type == ConstraintType.EQUAL || type == ConstraintType.NOT_EQUAL || type == ConstraintType.GREATER_THAN || type == ConstraintType.GREATER_OR_EQUAL ||
                                                              type == ConstraintType.LESS_THAN || type == ConstraintType.LESS_OR_EQUAL)) {
            return isPropertyOnRight(left.getExpression(), right.getExpression());
        } else {
            return false;
        }
    }

    private static boolean isPropertyOnRight(Expression left, Expression right) {
        return !isProperty(left) && isProperty(right);
    }

    private static boolean isProperty(Expression expr) {
        if (expr instanceof MethodCallExpr) {
            Optional<Expression> thisScope = getRootScope((MethodCallExpr) expr).filter(scope -> scope.equals(new NameExpr(THIS_PLACEHOLDER)));
            if (thisScope.isPresent()) {
                return true;
            }
        }
        return false;
    }

    private static Optional<Expression> getRootScope(MethodCallExpr mcExpr) {
        // to get "_this" from nested property like "_this.getAdress().getCity()"
        return mcExpr.getScope().flatMap(s -> {
            if (s instanceof NameExpr) {
                return Optional.of(s);
            } else if (s instanceof MethodCallExpr) {
                return getRootScope((MethodCallExpr) s);
            } else {
                return Optional.empty();
            }
        });
    }

    private static void inverseSingleDrlxParseSuccess(SingleDrlxParseSuccess s) {
        ConstraintType inversedOperator = null;

        switch (s.getDecodeConstraintType()) {
            case EQUAL:
                inversedOperator = ConstraintType.EQUAL;
                break;
            case NOT_EQUAL:
                inversedOperator = ConstraintType.NOT_EQUAL;
                break;
            case GREATER_THAN:
                inversedOperator = ConstraintType.LESS_THAN;
                break;
            case GREATER_OR_EQUAL:
                inversedOperator = ConstraintType.LESS_OR_EQUAL;
                break;
            case LESS_THAN:
                inversedOperator = ConstraintType.GREATER_THAN;
                break;
            case LESS_OR_EQUAL:
                inversedOperator = ConstraintType.GREATER_OR_EQUAL;
                break;
            default:
                throw new IllegalArgumentException(s.getDecodeConstraintType() + " should not be inversed");
        }

        TypedExpression left = s.getLeft();
        s.setLeft(s.getRight());
        s.setRight(left);
        s.setDecodeConstraintType(inversedOperator);
    }

    private static void inverseMethodCallExpr(MethodCallExpr mExpr) {
        String mExprName = mExpr.getName().asString();

        String methodName = mExprName.substring(CLASS_NAME.length(), mExprName.length());
        NodeList<Expression> arguments = mExpr.getArguments();

        if (methodName.startsWith(GREATER_THAN_PREFIX)) {
            methodName = methodName.replaceFirst(GREATER_THAN_PREFIX, LESS_THAN_PREFIX);
        } else if (methodName.startsWith(GREATER_OR_EQUAL_PREFIX)) {
            methodName = methodName.replaceFirst(GREATER_OR_EQUAL_PREFIX, LESS_OR_EQUAL_PREFIX);
        } else if (methodName.startsWith(LESS_THAN_PREFIX)) {
            methodName = methodName.replaceFirst(LESS_THAN_PREFIX, GREATER_THAN_PREFIX);
        } else if (methodName.startsWith(LESS_OR_EQUAL_PREFIX)) {
            methodName = methodName.replaceFirst(LESS_OR_EQUAL_PREFIX, GREATER_OR_EQUAL_PREFIX);
        }

        mExpr.setName(new SimpleName(CLASS_NAME + methodName));
        Expression firstArg = arguments.get(0);
        mExpr.setArgument(0, arguments.get(1));
        mExpr.setArgument(1, firstArg);
    }
}
