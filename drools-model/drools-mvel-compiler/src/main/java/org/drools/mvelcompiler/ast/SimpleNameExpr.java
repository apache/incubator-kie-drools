package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;

import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;

public class SimpleNameExpr extends TypedExpression {

    public SimpleNameExpr(Node originalExpression) {
        super(originalExpression);
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Expression toJavaExpression() {
        return new NameExpr(printConstraint(originalExpression));
    }
}
