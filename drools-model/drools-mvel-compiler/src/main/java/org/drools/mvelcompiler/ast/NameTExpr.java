package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;

import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;

public class NameTExpr extends TypedExpression {
    final Type type;

    public NameTExpr(Node expression, Type type) {
        super(expression);
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "NameTExpr{" +
                "expression=" + printConstraint(originalExpression) +
                ", type=" + type +
                '}';
    }
}
