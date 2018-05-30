package org.drools.modelcompiler.builder.generator.drlxparse;

import org.drools.javaparser.ast.expr.BinaryExpr;
import org.drools.javaparser.ast.expr.DoubleLiteralExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.IntegerLiteralExpr;
import org.drools.javaparser.ast.expr.LiteralStringValueExpr;
import org.drools.javaparser.ast.expr.LongLiteralExpr;
import org.drools.modelcompiler.builder.generator.TypedExpression;

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

        if (right.getExpression() instanceof LiteralStringValueExpr ) {
            final Expression coercedLiteralNumberExprToType = coerceLiteralNumberExprToType((LiteralStringValueExpr) right.getExpression(), left.getType());
            coercedRight = right.cloneWithNewExpression(coercedLiteralNumberExprToType);
        } else {
            coercedRight = right;
        }

        System.out.println("XXX right = " + coercedRight);
        System.out.println("\n\n");
        final CoercedExpressionResult coercedExpressionResult = new CoercedExpressionResult(left, coercedRight, new BinaryExpr());
        return coercedExpressionResult;
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
