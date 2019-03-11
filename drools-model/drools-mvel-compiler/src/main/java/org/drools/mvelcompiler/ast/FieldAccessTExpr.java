package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;

import com.github.javaparser.ast.expr.FieldAccessExpr;

public class FieldAccessTExpr extends TypedExpression {
    public FieldAccessTExpr(FieldAccessExpr originalExpression) {
        super(originalExpression);
    }

    @Override
    public Type getType() {
        return null;
    }
}
