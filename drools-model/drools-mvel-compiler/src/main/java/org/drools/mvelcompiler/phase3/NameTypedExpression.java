package org.drools.mvelcompiler.phase3;

import java.lang.reflect.Type;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;

import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;

public class NameTypedExpression implements TypedExpression {

    final Node expression;
    final Type type;

    public NameTypedExpression(Node expression, Type type) {
        this.expression = expression;
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Expression toJavaExpression() {
        return new NameExpr(printConstraint(expression));
    }

    @Override
    public String toString() {
        return "NameTypedExpression{" +
                "expression=" + printConstraint(expression) +
                ", type=" + type +
                '}';
    }
}
