package org.drools.mvelcompiler.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

public class CoercionUtils {

    public static final String PUT_CALL = "put";

    public static Expression coerceMapValueToString(Optional<Type> type, Expression originalValue) {
        return type.flatMap(t -> {
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
    }

}
