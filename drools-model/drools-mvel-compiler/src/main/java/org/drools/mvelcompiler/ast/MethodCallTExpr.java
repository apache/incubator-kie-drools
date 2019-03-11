package org.drools.mvelcompiler.ast;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;

import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;

public class MethodCallTExpr extends TypedExpression {

    final TypedExpression scope;
    final Type type;
    final Method accessor;

    public MethodCallTExpr(SimpleName expression, TypedExpression scope, Type type, Method accessor) {
        super(expression);
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
    public String toString() {
        return "MethodCallTExpr{" +
                "expression=" + printConstraint(originalExpression) +
                ", type=" + type +
                ", accessor=" + accessor +
                '}';
    }
}