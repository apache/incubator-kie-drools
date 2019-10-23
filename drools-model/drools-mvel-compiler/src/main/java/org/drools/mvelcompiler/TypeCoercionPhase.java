package org.drools.mvelcompiler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.Statement;

public class TypeCoercionPhase {

    public Statement invoke(Statement statement) {

        List<MethodCallExpr> put =
                statement.findAll(MethodCallExpr.class, mc -> "put".equals(mc.getNameAsString()) && mc.getArguments().size() == 2);



        return statement;
    }

    private void coerce(Expression originalValue, Optional<Type> type) {
        // Move coercion in a different phase
        final Expression coercedValue = type.flatMap(t -> {
            if (t instanceof ParameterizedType) {
                ParameterizedType parametrizedType = (ParameterizedType) t;
                if (parametrizedType.getActualTypeArguments().length == 2) {
                    Type valueType = parametrizedType.getActualTypeArguments()[1];

                    if (valueType.equals(String.class)) {
                        return Optional.of((Expression) new MethodCallExpr(new NameExpr("java.lang.String"),
                                                                           "valueOf",
                                                                           NodeList.nodeList(originalValue)));
                    }
                }
            }
            return Optional.empty();
        }).orElse(originalValue);
    }
}
