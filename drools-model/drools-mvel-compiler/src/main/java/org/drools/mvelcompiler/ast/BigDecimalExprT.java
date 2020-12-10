package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static com.github.javaparser.ast.NodeList.nodeList;

public class BigDecimalExprT implements TypedExpression {

    private final String name;
    private final Optional<TypedExpression> scope;
    private final TypedExpression argument;
    private final Optional<Type> type;

    public BigDecimalExprT(String bigDecimalMethod,
                           TypedExpression scope,
                           TypedExpression argument) {
        this.name = bigDecimalMethod;
        this.scope = Optional.of(scope);
        this.argument = argument;
        this.type = Optional.of(BigDecimal.class);
    }

    @Override
    public Optional<Type> getType() {
        return type;
    }

    @Override
    public Node toJavaExpression() {
        Node scopeE = scope.map(TypedExpression::toJavaExpression).orElse(null);

        return new MethodCallExpr((Expression) scopeE, name, nodeList((Expression) argument.toJavaExpression()));
    }

    @Override
    public String toString() {
        return "BigDecimalExprT{" +
                "name='" + name + '\'' +
                ", scope=" + scope +
                ", arguments=" + argument +
                ", type=" + type +
                '}';
    }
}
