package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.SimpleName;

public class UnalteredTypedExpression implements TypedExpression {

    private final Node originalExpression;
    private Type type;

    public UnalteredTypedExpression(Node originalExpression) {
        this(originalExpression, null);
    }

    public UnalteredTypedExpression(Node originalExpression, Type type) {
        this.originalExpression = originalExpression;
        this.type = type;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.ofNullable(type);
    }

    @Override
    public Node toJavaExpression() {
        return (originalExpression instanceof SimpleName)
                ? originalExpression.getParentNode().orElseThrow(() -> new IllegalStateException("Expression has no parent node!"))
                : originalExpression;
    }

    @Override
    public String toString() {
        return "UnalteredTypedExpression{originalExpression=" + originalExpression + '}';
    }
}
