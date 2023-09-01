package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public class ObjectCreationExpressionT implements TypedExpression {

    private final Class<?> type;
    private List<TypedExpression> constructorArguments;

    public ObjectCreationExpressionT(List<TypedExpression> constructorArguments, Class<?> type) {
        this.constructorArguments = constructorArguments;
        this.type = type;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(type.getCanonicalName());
        List<Expression> arguments = this.constructorArguments.stream()
                .map(typedExpression -> (Expression)typedExpression.toJavaExpression())
                .collect(Collectors.toList());
        objectCreationExpr.setArguments(NodeList.nodeList(arguments));
        return objectCreationExpr;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ObjectCreationExpressionT{");
        sb.append("arguments=").append(constructorArguments);
        sb.append("type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
