package org.drools.modelcompiler.builder.generator.drlxparse;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralStringValueExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.PrimitiveType;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.TypedExpression;

import static org.drools.modelcompiler.builder.PackageModel.STRING_TO_DATE_METHOD;
import static org.drools.modelcompiler.util.ClassUtil.toNonPrimitiveType;
import static org.drools.modelcompiler.util.JavaParserUtil.toJavaParserType;

public class CoercedExpression {

    private static final List<Class<?>> LITERAL_NUMBER_CLASSES = Arrays.asList(int.class, long.class, double.class);

    private TypedExpression left;
    private TypedExpression right;

    private static Map<Class, List<Class<?>>> narrowingTypes = new HashMap<>();

    static {
        // https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.3
        narrowingTypes.put(short.class, Arrays.asList(Byte.class, Character.class));
        narrowingTypes.put(char.class, Arrays.asList(Byte.class, Short.class));
        narrowingTypes.put(int.class, Arrays.asList(Byte.class, Short.class, Character.class));
        narrowingTypes.put(long.class, Arrays.asList(Byte.class, Short.class, Character.class, Integer.class));
        narrowingTypes.put(float.class, Arrays.asList(Byte.class, Short.class, Character.class, Integer.class, Long.class));
        narrowingTypes.put(double.class, Arrays.asList(Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class));
    }

    public CoercedExpression(TypedExpression left, TypedExpression right) {
        this.left = left;
        this.right = right;
    }

    public CoercedExpressionResult coerce() {
        final TypedExpression coercedRight;

        final Class<?> leftClass = left.getRawClass();
        final Class<?> rightClass = right.getRawClass();

        if (cannotCoerce()) {
            throw new CoercedExpressionException(new InvalidExpressionErrorResult("Comparison operation requires compatible types. Found " + leftClass + " and " + rightClass));
        }

        final Expression rightExpression = right.getExpression();

        final boolean leftIsPrimitive = leftClass.isPrimitive();
        final boolean canCoerceLiteralNumberExpr = canCoerceLiteralNumberExpr(leftClass);

        if (leftIsPrimitive && canCoerceLiteralNumberExpr && rightExpression instanceof LiteralStringValueExpr) {
            final Expression coercedLiteralNumberExprToType = coerceLiteralNumberExprToType((LiteralStringValueExpr) right.getExpression(), leftClass);
            coercedRight = right.cloneWithNewExpression(coercedLiteralNumberExprToType);
        } else if (shouldCoerceBToString(left, right)) {
            coercedRight = coerceToString(right);
        } else if (isNotBinaryExpression(right) && canBeNarrowed(leftClass, rightClass) && right.isNumberLiteral()) {
            coercedRight = castToClass(leftClass);
        } else if (leftClass == long.class && rightClass == int.class) {
            coercedRight = right.cloneWithNewExpression(new CastExpr(PrimitiveType.longType(), right.getExpression()));
        } else if (leftClass == Date.class && rightClass == String.class) {
            coercedRight = coerceToDate(right);
        } else if(shouldCoerceBToMap()) {
            coercedRight = castToClass(toNonPrimitiveType(leftClass));
        } else if (Boolean.class.isAssignableFrom(leftClass) && !Boolean.class.isAssignableFrom(rightClass)) {
             coercedRight = coerceBoolean(right);
        } else {
            coercedRight = right;
        }

        final TypedExpression coercedLeft;
        if (toNonPrimitiveType(leftClass) == Character.class && shouldCoerceBToString(right, left)) {
            coercedLeft = coerceToString(left);
        } else {
            coercedLeft = left;
        }

        return new CoercedExpressionResult(coercedLeft, coercedRight);
    }

    private boolean shouldCoerceBToMap() {
        return isNotBinaryExpression(right) && Map.class.isAssignableFrom(right.getRawClass());
    }

    private boolean cannotCoerce() {
        final Class<?> leftClass = left.getRawClass();
        final Class<?> rightClass = right.getRawClass();

        final boolean leftIsPrimitive = leftClass.isPrimitive();
        final boolean canCoerceLiteralNumberExpr = canCoerceLiteralNumberExpr(leftClass);

        return leftIsPrimitive
                && canCoerceLiteralNumberExpr
                && !rightClass.isPrimitive()
                && !Number.class.isAssignableFrom(rightClass)
                && !Boolean.class.isAssignableFrom(rightClass)
                && !String.class.isAssignableFrom(rightClass)
                && !(Map.class.isAssignableFrom(leftClass) || Map.class.isAssignableFrom(rightClass));

    }

    private TypedExpression castToClass(Class<?> clazz) {
        return right.cloneWithNewExpression(new CastExpr(toJavaParserType(clazz, right.isPrimitive()), right.getExpression()));
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
        return coercedExpression.setType(String.class);
    }

    private static TypedExpression coerceToDate(TypedExpression typedExpression) {
        MethodCallExpr methodCallExpr = new MethodCallExpr( null, STRING_TO_DATE_METHOD );
        methodCallExpr.addArgument( typedExpression.getExpression() );
        return new TypedExpression( methodCallExpr, Date.class );
    }

    private static TypedExpression coerceBoolean(TypedExpression typedExpression) {
        final Expression expression = typedExpression.getExpression();
        if (expression instanceof BooleanLiteralExpr) {
            return typedExpression;
        } else if (expression instanceof StringLiteralExpr) {
            final String expressionValue = ((StringLiteralExpr) expression).getValue();
            if (Boolean.TRUE.toString().equals(expressionValue) || Boolean.FALSE.toString().equals(expressionValue)) {
                final TypedExpression coercedExpression = typedExpression.cloneWithNewExpression(new BooleanLiteralExpr(Boolean.parseBoolean(expressionValue)));
                return coercedExpression.setType(Boolean.class);
            } else {
                throw new CoercedExpressionException(new InvalidExpressionErrorResult("Cannot coerce String " + expressionValue + " to boolean!"));
            }
        } else {
            throw new CoercedExpressionException(new InvalidExpressionErrorResult("Cannot coerce " + typedExpression.getType() + " to boolean!"));
        }
    }

    private static boolean canCoerceLiteralNumberExpr(Class<?> type) {
        return LITERAL_NUMBER_CLASSES.contains(type);
    }

    private static boolean shouldCoerceBToString(TypedExpression a, TypedExpression b) {
        boolean aIsString = a.getType() == String.class;
        boolean bIsNotString = b.getType() != String.class;
        boolean bIsNotNull = !(b.getExpression() instanceof NullLiteralExpr);
        boolean bIsNotSerializable = b.getType() != Serializable.class;
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
        throw new CoercedExpressionException(new InvalidExpressionErrorResult("Unknown literal: " + expr));
    }

    private boolean canBeNarrowed(Class<?> leftType, Class<?> rightType) {
        return Optional.ofNullable(narrowingTypes.get(rightType)).map(a -> a.contains(toNonPrimitiveType(leftType))).orElse(false);
    }

    public static class CoercedExpressionResult {

        private final TypedExpression coercedLeft;
        private final TypedExpression coercedRight;

        CoercedExpressionResult(TypedExpression left, TypedExpression coercedRight) {
            this.coercedLeft = left;
            this.coercedRight = coercedRight;
        }

        TypedExpression getCoercedLeft() {
            return coercedLeft;
        }

        public TypedExpression getCoercedRight() {
            return coercedRight;
        }
    }

    static class CoercedExpressionException extends RuntimeException {

        private final transient InvalidExpressionErrorResult invalidExpressionErrorResult;

        CoercedExpressionException(InvalidExpressionErrorResult invalidExpressionErrorResult) {
            this.invalidExpressionErrorResult = invalidExpressionErrorResult;
        }

        InvalidExpressionErrorResult getInvalidExpressionErrorResult() {
            return invalidExpressionErrorResult;
        }
    }
}
