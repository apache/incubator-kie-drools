package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;

public class SimpleNameTExpr implements TypedExpression {

    private final String constraintName;
    private final Class<?> clazz;

    public SimpleNameTExpr(String constraintName, Class<?> clazz) {
        this.constraintName = constraintName;
        this.clazz = clazz;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.ofNullable(clazz);
    }

    @Override
    public Expression toJavaExpression() {
        return new NameExpr(constraintName);
    }

    @Override
    public String toString() {
        return "SimpleNameTExpr{" +
                "name=" + constraintName +
                ",clazz=" + clazz +
                '}';
    }
}
