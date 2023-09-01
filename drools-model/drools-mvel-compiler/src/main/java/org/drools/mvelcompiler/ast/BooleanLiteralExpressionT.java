package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;

public class BooleanLiteralExpressionT implements TypedExpression {

    private final BooleanLiteralExpr booleanLiteralExpr;

    public BooleanLiteralExpressionT(BooleanLiteralExpr booleanLiteralExpr) {
        this.booleanLiteralExpr = booleanLiteralExpr;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(boolean.class);
    }

    @Override
    public Node toJavaExpression() {
        return booleanLiteralExpr;
    }

    @Override
    public String toString() {
        return "BooleanLiteralExpressionT{" +
               "originalExpression=" + booleanLiteralExpr +
               '}';
    }
}
