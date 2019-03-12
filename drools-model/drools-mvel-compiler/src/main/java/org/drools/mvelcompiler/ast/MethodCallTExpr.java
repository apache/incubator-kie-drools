package org.drools.mvelcompiler.ast;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;

public class MethodCallTExpr extends TypedExpression {

    private final TypedExpression scope;
    private final Type type;
    private final Method accessor;

    public MethodCallTExpr(Node expression, TypedExpression scope, Method accessor) {
        super(expression);
        this.scope = scope;
        this.accessor = accessor;
        this.type = accessor.getGenericReturnType();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Node toJavaExpression() {
        return new MethodCallExpr((Expression) scope.toJavaExpression(), accessor.getName());
    }

    @Override
    public String toString() {
        return "MethodCallTExpr{" +
                "expression=" + printConstraint(originalExpression) +
                ", scope=" + scope.toString() +
                ", type=" + type +
                ", accessor=" + accessor +
                '}';
    }
}