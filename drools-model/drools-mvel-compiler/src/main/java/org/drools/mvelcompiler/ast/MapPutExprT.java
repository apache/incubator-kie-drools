package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static org.drools.mvelcompiler.util.CoercionUtils.PUT_CALL;
import static org.drools.mvelcompiler.util.CoercionUtils.coerceMapValueToString;

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
        final Expression coercedValue = coerceMapValueToString(type, originalValue);

        return new MethodCallExpr((Expression) name.toJavaExpression(),
                                  PUT_CALL,
                                  NodeList.nodeList(key, coercedValue));
    }
}
