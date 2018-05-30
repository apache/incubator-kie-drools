package org.drools.modelcompiler.builder.generator.drlxparse;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.expr.BinaryExpr;
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
import org.drools.modelcompiler.builder.generator.TypedExpression;

import static org.drools.modelcompiler.util.ClassUtil.toNonPrimitiveType;
import static org.drools.modelcompiler.util.JavaParserUtil.toJavaParserType;

public class CoercedExpression {

    private TypedExpression left;
    private TypedExpression right;

    public CoercedExpression(TypedExpression left, TypedExpression right) {
        this.left = left;
        this.right = right;
    }

    public CoercedExpressionResult coerce() {
        System.out.println("XXX left = " + left);
        System.out.println("XXX right = " + right);
        final TypedExpression coercedRight;
        final Expression rightExpression = right.getExpression();

        if (left.isPrimitive() && canCoerceLiteralNumberExpr(left.getType()) && rightExpression instanceof LiteralStringValueExpr ) {
            final Expression coercedLiteralNumberExprToType = coerceLiteralNumberExprToType((LiteralStringValueExpr) right.getExpression(), left.getType());
            coercedRight = right.cloneWithNewExpression(coercedLiteralNumberExprToType);
        } else if (shouldCoerceBToString(left, right)) {
            if (rightExpression instanceof CharLiteralExpr) {
                coercedRight = right.cloneWithNewExpression( new StringLiteralExpr((( CharLiteralExpr ) rightExpression).getValue() ) );
            } else if (right.isPrimitive() ){
                coercedRight = right.cloneWithNewExpression( new MethodCallExpr(new NameExpr("String"), "valueOf", NodeList.nodeList(rightExpression )) );
            } else if(right.getType() == Object.class) {
                coercedRight = right.cloneWithNewExpression( new MethodCallExpr(rightExpression, "toString" ) );
            } else {
                coercedRight = right.cloneWithNewExpression( new StringLiteralExpr( rightExpression.toString() ) );
            }
            coercedRight.setType(String.class);

        } else if (areCompatible(right.getType(), left.getType() ) ) {
            coercedRight = right.setExpression(new CastExpr(toJavaParserType(left.getType(), right.getType().isPrimitive()), right.getExpression()));
        } else {
            coercedRight = right;
        }

        System.out.println("XXX right = " + coercedRight);
        System.out.println("\n\n");
        final CoercedExpressionResult coercedExpressionResult = new CoercedExpressionResult(left, coercedRight, new BinaryExpr());
        return coercedExpressionResult;
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
        return bExpressionExists && aIsString && (bIsNotString &&  bIsNotNull && bIsNotSerializable);
    }

    private Expression coerceLiteralNumberExprToType(LiteralStringValueExpr expr, Class<?> type ) {
        if (type == int.class) {
            return new IntegerLiteralExpr(expr.getValue() );
        }
        if (type == long.class) {
            return new LongLiteralExpr(expr.getValue().endsWith("l" ) ? expr.getValue() : expr.getValue() + "l" );
        }
        if (type == double.class) {
            return new DoubleLiteralExpr(expr.getValue().endsWith("d" ) ? expr.getValue() : expr.getValue() + "d" );
        }
        throw new RuntimeException("Unknown literal: " + expr);
    }

    private boolean areCompatible( Class<?> leftType, Class<?> rightType ) {
        return toNonPrimitiveType( rightType ).isAssignableFrom( toNonPrimitiveType( leftType ) );
    }


    static class CoercedExpressionResult {
        private final TypedExpression left;
        private final TypedExpression coercedRight;

        CoercedExpressionResult(TypedExpression left, TypedExpression coercedRight, BinaryExpr binaryExpr) {
            this.left = left;
            this.coercedRight = coercedRight;
        }

        public TypedExpression getLeft() {
            return left;
        }

        public TypedExpression getCoercedRight() {
            return coercedRight;
        }

    }
}
