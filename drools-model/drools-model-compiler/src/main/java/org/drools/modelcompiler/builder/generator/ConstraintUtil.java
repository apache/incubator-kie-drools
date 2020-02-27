package org.drools.modelcompiler.builder.generator;

import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
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

    private static final String METHOD_PREFIX = EvaluationUtil.class.getCanonicalName() + ".";

    public static final String DROOLS_NORMALIZE_CONSTRAINT = "drools.normalize.constraint";

    private static final boolean ENABLE_NORMALIZE = Boolean.parseBoolean(System.getProperty(DROOLS_NORMALIZE_CONSTRAINT, "true"));

    private ConstraintUtil() {}

    public static PatternConstraintParseResult normalizeConstraint(PatternConstraintParseResult pConstraint) {
        if (!ENABLE_NORMALIZE) {
            return pConstraint;
        }

        DrlxParseResult drlxParseResult = pConstraint.getDrlxParseResult();

        if (drlxParseResult instanceof SingleDrlxParseSuccess) {
            SingleDrlxParseSuccess s = (SingleDrlxParseSuccess) drlxParseResult;

            // TODO: Add logic based on s.getExpr() class

            ConstraintType type = s.getDecodeConstraintType();
            TypedExpression left = s.getLeft();
            TypedExpression right = s.getRight();
            if (type != null && (type == ConstraintType.EQUAL || type == ConstraintType.NOT_EQUAL || type == ConstraintType.GREATER_THAN || type == ConstraintType.GREATER_OR_EQUAL || type == ConstraintType.LESS_THAN ||
                                 type == ConstraintType.LESS_OR_EQUAL) && (isPropertyOnRight(left, right))) {
                inverseExpression(s);
            }
        }

        return pConstraint;
    }

    private static boolean isPropertyOnRight(TypedExpression left, TypedExpression right) {
        return !isProperty(left) && isProperty(right);
    }

    private static boolean isProperty(TypedExpression tExpr) {
        if (tExpr == null) {
            return false;
        }
        Expression expr = tExpr.getExpression();
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

    private static void inverseExpression(SingleDrlxParseSuccess s) {
        Expression expr = s.getExpr();
        if (!(expr instanceof MethodCallExpr)) {
            return;
        }
        MethodCallExpr mExpr = (MethodCallExpr) expr;
        String mExprName = mExpr.getName().asString();
        String methodName = mExprName.substring(METHOD_PREFIX.length(), mExprName.length());
        NodeList<Expression> arguments = mExpr.getArguments();
        if (arguments.size() != 2) {
            return;
        }
        ConstraintType inversedOperator = null;
        try {
            switch (s.getDecodeConstraintType()) {
                case EQUAL:
                    inversedOperator = ConstraintType.EQUAL;
                    break;
                case NOT_EQUAL:
                    inversedOperator = ConstraintType.NOT_EQUAL;
                    break;
                case GREATER_THAN:
                    inversedOperator = ConstraintType.LESS_THAN;
                    methodName = replaceMethodName(methodName, "greaterThan", "lessThan");
                    break;
                case GREATER_OR_EQUAL:
                    inversedOperator = ConstraintType.LESS_OR_EQUAL;
                    methodName = replaceMethodName(methodName, "greaterOrEqual", "lessOrEqual");
                    break;
                case LESS_THAN:
                    inversedOperator = ConstraintType.GREATER_THAN;
                    methodName = replaceMethodName(methodName, "lessThan", "greaterThan");
                    break;
                case LESS_OR_EQUAL:
                    inversedOperator = ConstraintType.GREATER_OR_EQUAL;
                    methodName = replaceMethodName(methodName, "lessOrEqual", "greaterOrEqual");
                    break;
                default:
                    throw new IllegalArgumentException(s.getDecodeConstraintType() + " should not be inversed");
            }
        } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage());
            return;
        }
        TypedExpression left = s.getLeft();
        s.setLeft(s.getRight());
        s.setRight(left);
        s.setDecodeConstraintType(inversedOperator);

        mExpr.setName(new SimpleName(METHOD_PREFIX + methodName));
        Expression firstArg = arguments.get(0);
        mExpr.setArgument(0, arguments.get(1));
        mExpr.setArgument(1, firstArg);
    }

    private static String replaceMethodName(String methodName, String original, String replacement) {
        if (!methodName.contains(original)) {
            throw new IllegalArgumentException("methodName \"" + methodName + "\" should contain \"" + original + "\"");
        }
        return methodName.replaceFirst(original, replacement);
    }
}
