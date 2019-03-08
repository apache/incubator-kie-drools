package org.drools.mvelcompiler.phase4;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.stream.Stream;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;

public class MethodTypedExpression implements TypedExpression {

    final Node expression;
    final TypedExpression scope;
    final Type type;
    final Method accessor;

    MethodTypedExpression(Node expression, TypedExpression scope, Type type, Method accessor) {
        this.expression = expression;
        this.scope = scope;
        this.type = type;
        this.accessor = accessor;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Expression toJavaExpression() {
        return new MethodCallExpr(scope.toJavaExpression(), accessor.getName());
    }

    @Override
    public Stream<Node> stream() {
        return null;
    }

    @Override
    public String toString() {
        return "MethodTypedExpression{" +
                "expression=" + printConstraint(expression) +
                ", type=" + type +
                ", accessor=" + accessor +
                '}';
    }
}