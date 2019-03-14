package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;

import com.github.javaparser.ast.Node;

public class StringLiteralExpressionT extends TypedExpression {

    public StringLiteralExpressionT(Node originalExpression) {
        super(originalExpression);
    }

    @Override
    public Type getType() {
        return String.class;
    }

    @Override
    public Node toJavaExpression() {
        return originalExpression;
    }

    @Override
    public String toString() {
        return "StringLiteralExpressionT{" +
                "originalExpression=" + originalExpression +
                '}';
    }
}
