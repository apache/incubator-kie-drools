package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static com.github.javaparser.ast.NodeList.nodeList;

public class MethodCallExprT implements TypedExpression {

    private final String name;
    private final Optional<TypedExpression> scope;
    private final List<TypedExpression> arguments;
    private final Optional<Type> type;

    public MethodCallExprT(Node originalExpression, String name, Optional<TypedExpression> scope, List<TypedExpression> arguments, Optional<Type> type) {

        this.name = name;
        this.scope = scope;
        this.arguments = arguments;
        this.type = type;
    }

    @Override
    public Optional<Type> getType() {
        return type;
    }

    @Override
    public Node toJavaExpression() {
        Node scopeE = scope.map(TypedExpression::toJavaExpression).orElse(null);
        List<Expression> arguments = this.arguments
                .stream()
                .map(a -> (Expression) a.toJavaExpression())
                .collect(Collectors.toList());

        return new MethodCallExpr((Expression) scopeE, name, nodeList(arguments));
    }
}
