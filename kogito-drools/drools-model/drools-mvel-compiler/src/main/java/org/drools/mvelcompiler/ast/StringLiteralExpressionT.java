package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class StringLiteralExpressionT implements TypedExpression {

    private final StringLiteralExpr stringLiteralExpr;

    public StringLiteralExpressionT(StringLiteralExpr stringLiteralExpr) {

        this.stringLiteralExpr = stringLiteralExpr;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(String.class);
    }

    @Override
    public Node toJavaExpression() {
        return stringLiteralExpr;
    }

    @Override
    public String toString() {
        return "StringLiteralExpressionT{" +
                "originalExpression=" + stringLiteralExpr +
                '}';
    }
}
