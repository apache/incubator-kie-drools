package org.drools.mvelcompiler.ast;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class ListAccessExprT implements TypedExpression {

    private final TypedExpression name;
    private final Expression index;
    private final Type type;

    public ListAccessExprT(TypedExpression name, Expression index, Type type) {
        this.name = name;
        this.index = index;
        this.type = type;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    public Optional<Type> getElementType() {
        return getType().filter(ParameterizedType.class::isInstance)
                        .map(ParameterizedType.class::cast)
                        .map(parameterizedType -> {
                            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                            if (List.class.isAssignableFrom(rawType)) {
                                return parameterizedType.getActualTypeArguments()[0];
                            } else if (Map.class.isAssignableFrom(rawType)) {
                                return parameterizedType.getActualTypeArguments()[1];
                            } else {
                                throw new IllegalStateException("ListAccessExprT is not applicable to " + rawType);
                            }
                        });
    }

    @Override
    public Node toJavaExpression() {
        return new MethodCallExpr((Expression) name.toJavaExpression(), "get", NodeList.nodeList(index));
    }
}
