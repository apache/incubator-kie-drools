package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;

import static org.drools.constraint.parser.printer.PrintUtil.printConstraint;

public class SimpleNameTExpr extends TypedExpression {

    private final Class<?> clazz;

    public SimpleNameTExpr(Node originalExpression, Class<?> clazz) {
        super(originalExpression);
        this.clazz = clazz;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.ofNullable(clazz);
    }

    @Override
    public Expression toJavaExpression() {
        return new NameExpr(printConstraint(originalExpression));
    }
}
