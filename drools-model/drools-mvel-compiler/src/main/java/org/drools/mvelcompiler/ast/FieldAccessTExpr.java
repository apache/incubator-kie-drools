package org.drools.mvelcompiler.ast;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;

public class FieldAccessTExpr implements TypedExpression {

    private final TypedExpression scope;
    private final Field field;

    public FieldAccessTExpr(TypedExpression scope, Field field) {
        this.scope = scope;
        this.field = field;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(field.getType());
    }

    @Override
    public Node toJavaExpression() {
        return new FieldAccessExpr((Expression) scope.toJavaExpression(), field.getName());
    }

    @Override
    public String toString() {
        return "FieldAccessTExpr{" +
                "scope=" + scope +
                ", field=" + field +
                '}';
    }
}
