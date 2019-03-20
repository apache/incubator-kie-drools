package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public class ObjectCreationExpressionT implements TypedExpression {

    private final Class<?> type;

    ObjectCreationExpr originalExpression;

    public ObjectCreationExpressionT(ObjectCreationExpr originalExpression, Class<?> type) {
        this.originalExpression = originalExpression;
        this.type = type;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {
        return originalExpression;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ObjectCreationExpressionT{");
        sb.append("originalExpression=").append(originalExpression);
        sb.append("type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
