package org.drools.modelcompiler.builder.generator.drlxparse;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.expr.CastExpr;
import org.drools.javaparser.ast.expr.CharLiteralExpr;
import org.drools.javaparser.ast.expr.DoubleLiteralExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.IntegerLiteralExpr;
import org.drools.javaparser.ast.expr.LiteralStringValueExpr;
import org.drools.javaparser.ast.expr.LongLiteralExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.NullLiteralExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.TypedExpression;

import static org.drools.modelcompiler.util.ClassUtil.toNonPrimitiveType;
import static org.drools.modelcompiler.util.JavaParserUtil.toJavaParserType;

public class CoercedExpression {

    private TypedExpression left;
    private TypedExpression right;

    private static Map<Class, List<Class<?>>> narrowingTypes = new HashMap<>();

    static {
        // https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.3
        narrowingTypes.put(Short.class, Arrays.asList(Byte.class, Character.class));
        narrowingTypes.put(Character.class, Arrays.asList(Byte.class, Short.class));
        narrowingTypes.put(Integer.class, Arrays.asList(Byte.class, Short.class, Character.class));
        narrowingTypes.put(Long.class, Arrays.asList(Byte.class, Short.class, Character.class, Integer.class));
        narrowingTypes.put(Float.class, Arrays.asList(Byte.class, Short.class, Character.class, Integer.class, Long.class));
        narrowingTypes.put(Double.class, Arrays.asList(Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class));
    }

    public CoercedExpression(TypedExpression left, TypedExpression right) {
        this.left = left;
        this.right = right;
    }

    public CoercedExpressionResult coerce() {
        final TypedExpression coercedRight;
        final Expression rightExpression = right.getExpression();

        final boolean leftIsPrimitive = left.isPrimitive();
        final boolean canCoerceLiteralNumberExpr = canCoerceLiteralNumberExpr(left.getType());

        if (leftIsPrimitive && canCoerceLiteralNumberExpr) {
            if (!right.getType().isPrimitive() && !Number.class.isAssignableFrom(right.getType()) &&
                    !Boolean.class.isAssignableFrom(right.getType()) && !String.class.isAssignableFrom(right.getType())) {
                throw new CoercedExpressionException(new InvalidExpressionErrorResult("Comparison operation requires compatible types. Found " + left.getType() + " and " + right.getType()));
            }
        }

        if (leftIsPrimitive && canCoerceLiteralNumberExpr && rightExpression instanceof LiteralStringValueExpr) {
            final Expression coercedLiteralNumberExprToType = coerceLiteralNumberExprToType((LiteralStringValueExpr) right.getExpression(), left.getType());
            coercedRight = right.cloneWithNewExpression(coercedLiteralNumberExprToType);
        } else if (shouldCoerceBToString(left, right)) {
            coercedRight = coerceToString(right);
        } else if (isNotBinaryExpression(right) && canBeNarrowed(left.getType(), right.getType())) {
            coercedRight = right.cloneWithNewExpression(new CastExpr(toJavaParserType(left.getType(), right.getType().isPrimitive()), right.getExpression()));
        } else if (isNotBinaryExpression(right) && left.getType().equals(Object.class) && right.getType() != Object.class) {
            coercedRight = right.cloneWithNewExpression(new CastExpr(toJavaParserType(Object.class, right.getType().isPrimitive()), right.getExpression()));
        } else {
            coercedRight = right;
        }

        final TypedExpression coercedLeft;
        if (toNonPrimitiveType(left.getType()) == Character.class && shouldCoerceBToString(right, left)) {
            coercedLeft = coerceToString(left);
        } else {
            coercedLeft = left;
        }

        return new CoercedExpressionResult(coercedLeft, coercedRight);
    }

    private static TypedExpression coerceToString(TypedExpression typedExpression) {
        final Expression expression = typedExpression.getExpression();
        TypedExpression coercedExpression;
        if (expression instanceof CharLiteralExpr) {
            coercedExpression = typedExpression.cloneWithNewExpression(new StringLiteralExpr(((CharLiteralExpr) expression).getValue()));
        } else if (typedExpression.isPrimitive()) {
            coercedExpression = typedExpression.cloneWithNewExpression(new MethodCallExpr(new NameExpr("String"), "valueOf", NodeList.nodeList(expression)));
        } else if (typedExpression.getType() == Object.class) {
            coercedExpression = typedExpression.cloneWithNewExpression(new MethodCallExpr(expression, "toString"));
        } else {
            coercedExpression = typedExpression.cloneWithNewExpression(new StringLiteralExpr(expression.toString()));
        }
        coercedExpression.setType(String.class);
        return coercedExpression;
    }

    public static boolean canCoerceLiteralNumberExpr(Class<?> type) {
        final List<? extends Class<?>> classes = Arrays.asList(int.class, long.class, double.class);
        return classes.contains(type);
    }

    private static boolean shouldCoerceBToString(TypedExpression a, TypedExpression b) {
        boolean aIsString = a.getType() == String.class;
        boolean bIsNotString = b.getType() != String.class;
        boolean bIsNotNull = !(b.getExpression() instanceof NullLiteralExpr);
        boolean bIsNotSerializable = !(b.getType() == Serializable.class);
        boolean bExpressionExists = b.getExpression() != null;
        return bExpressionExists && isNotBinaryExpression(b) && aIsString && (bIsNotString && bIsNotNull && bIsNotSerializable);
    }

    private static boolean isNotBinaryExpression(TypedExpression e) {
        return !(e.getExpression() != null && e.getExpression().isBinaryExpr());
    }

    private Expression coerceLiteralNumberExprToType(LiteralStringValueExpr expr, Class<?> type) {
        if (type == int.class) {
            return new IntegerLiteralExpr(expr.getValue());
        }
        if (type == long.class) {
            return new LongLiteralExpr(expr.getValue().endsWith("l") ? expr.getValue() : expr.getValue() + "l");
        }
        if (type == double.class) {
            return new DoubleLiteralExpr(expr.getValue().endsWith("d") ? expr.getValue() : expr.getValue() + "d");
        }
        throw new RuntimeException("Unknown literal: " + expr);
    }

    private boolean canBeNarrowed(Class<?> leftType, Class<?> rightType) {
        final Class<?> rightNonPrimitive = toNonPrimitiveType(rightType);
        final Class<?> leftNonPrimitive = toNonPrimitiveType(leftType);

        return Optional.ofNullable(narrowingTypes.get(rightNonPrimitive)).map(a -> a.contains(leftNonPrimitive)).orElse(false);
    }

    public static class CoercedExpressionResult {

        private final TypedExpression coercedLeft;
        private final TypedExpression coercedRight;

        CoercedExpressionResult(TypedExpression left, TypedExpression coercedRight) {
            this.coercedLeft = left;
            this.coercedRight = coercedRight;
        }

        public TypedExpression getCoercedLeft() {
            return coercedLeft;
        }

        public TypedExpression getCoercedRight() {
            return coercedRight;
        }
    }

    public static class CoercedExpressionException extends RuntimeException {

        private final InvalidExpressionErrorResult invalidExpressionErrorResult;

        public CoercedExpressionException(InvalidExpressionErrorResult invalidExpressionErrorResult) {
            this.invalidExpressionErrorResult = invalidExpressionErrorResult;
        }

        public InvalidExpressionErrorResult getInvalidExpressionErrorResult() {
            return invalidExpressionErrorResult;
        }
    }
}
