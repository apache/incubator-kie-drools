package org.drools.mvelcompiler.ast;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static com.github.javaparser.ast.NodeList.nodeList;

public class FieldToAccessorTExpr implements TypedExpression {

    private final TypedExpression scope;
    private final Type type;
    private final Method accessor;

    private final List<TypedExpression> arguments;

    public FieldToAccessorTExpr(TypedExpression scope, Method accessor, List<TypedExpression> arguments) {
        this.scope = scope;
        this.accessor = accessor;
        this.type = accessor.getGenericReturnType();
        this.arguments = arguments;
    }

    public FieldToAccessorTExpr(TypedExpression scope, Method accessor) {
        this(scope, accessor, Collections.emptyList());
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {
        List<Expression> expressionArguments = this.arguments.stream()
                .map(a -> (Expression) (a.toJavaExpression()))
                .collect(Collectors.toList());

        return new MethodCallExpr((Expression) scope.toJavaExpression(), accessor.getName(), nodeList(expressionArguments));
    }

    @Override
    public String toString() {
        return "FieldToAccessorTExpr{" +
                " scope=" + scope.toString() +
                ", type=" + type +
                ", accessor=" + accessor +
                '}';
    }
}