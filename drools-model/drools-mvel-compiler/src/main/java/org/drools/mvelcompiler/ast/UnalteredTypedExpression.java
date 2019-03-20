package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.SimpleName;

public class UnalteredTypedExpression extends TypedExpression {

    private Type type;

    public UnalteredTypedExpression(Node originalExpression) {
        super(originalExpression);
    }

    public UnalteredTypedExpression(Node originalExpression, Type type) {
        super(originalExpression);
        this.type = type;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.ofNullable(type);
    }

    @Override
    public Node toJavaExpression() {
        return (originalExpression instanceof SimpleName) ? originalExpression.getParentNode().get() : originalExpression;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UnalteredTypedExpression{");
        sb.append("originalExpression=").append(originalExpression);
        sb.append('}');
        return sb.toString();
    }
}
