package org.drools.modelcompiler.builder.generator.drlxparse;

import org.drools.javaparser.ast.expr.BinaryExpr;
import org.drools.javaparser.ast.expr.LiteralStringValueExpr;
import org.drools.modelcompiler.builder.generator.TypedExpression;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.coerceLiteralNumberExprToType;

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
        final TypedExpression coercedRight = right;
        System.out.println("XXX right = " + coercedRight);
        System.out.println("\n\n");
        final CoercedExpressionResult coercedExpressionResult = new CoercedExpressionResult(left, right, new BinaryExpr());
        return coercedExpressionResult;
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
