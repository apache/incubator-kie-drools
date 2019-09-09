package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ExpressionStmt;

public class ExpressionStmtT implements TypedExpression {

    private final TypedExpression child;

    public ExpressionStmtT(TypedExpression child) {
        this.child = child;
    }

    @Override
    public Optional<Type> getType() {
        return child.getType();
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
