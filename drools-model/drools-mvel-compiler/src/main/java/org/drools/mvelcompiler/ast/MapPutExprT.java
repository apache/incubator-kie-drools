package org.drools.mvelcompiler.ast;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

public class MapPutExprT implements TypedExpression {

    private final TypedExpression name;
    private final Expression key;
    private final TypedExpression value;
    private final Optional<Type> type;

    public MapPutExprT(TypedExpression name, Expression key, TypedExpression value, Optional<Type> type) {
        this.name = name;
        this.key = key;
        this.value = value;
        this.type = type;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        final Expression originalValue = (Expression) this.value.toJavaExpression();

        // Move coercion in a different phase
        final Expression coercedValue = type.flatMap(t -> {
            if (t instanceof ParameterizedType) {
                ParameterizedType parametrizedType = (ParameterizedType) t;
                if (parametrizedType.getActualTypeArguments().length == 2) {
                    Type valueType = parametrizedType.getActualTypeArguments()[1];

                    if (valueType.equals(String.class)) {
                        return Optional.of((Expression)new MethodCallExpr(new NameExpr("java.lang.String"),
                                                              "valueOf",
                                                              NodeList.nodeList(originalValue)));
                    }
                }
            }
            return Optional.empty();
        }).orElse(originalValue);

        return new MethodCallExpr((Expression) name.toJavaExpression(),
                                  "put",
                                  NodeList.nodeList(key, coercedValue));
    }
}
