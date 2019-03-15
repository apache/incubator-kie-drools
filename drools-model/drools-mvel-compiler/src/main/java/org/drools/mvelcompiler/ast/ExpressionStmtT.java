package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ExpressionStmt;

public class ExpressionStmtT extends TypedExpression {

    private final TypedExpression child;

    public ExpressionStmtT(ExpressionStmt originalExpression, TypedExpression child) {
        super(originalExpression);
        this.child = child;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        return new ExpressionStmt((Expression) child.toJavaExpression());
    }

    @Override
    public String toString() {
        return "ExpressionStmtT{\n" +
                "\tchild=" + child +
                '}';
    }
}
