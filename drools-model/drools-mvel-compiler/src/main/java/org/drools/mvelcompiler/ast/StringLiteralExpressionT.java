package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;

public class StringLiteralExpressionT extends TypedExpression {

    public StringLiteralExpressionT(Node originalExpression) {
        super(originalExpression);
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(String.class);
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
