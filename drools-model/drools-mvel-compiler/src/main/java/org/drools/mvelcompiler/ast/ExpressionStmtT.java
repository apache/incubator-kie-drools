package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ExpressionStmt;

public class ExpressionStmtT extends TypedExpression {

    public ExpressionStmtT(ExpressionStmt originalExpression) {
        super(originalExpression);
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Node toJavaExpression() {
        return new ExpressionStmt((Expression) children.iterator().next().toJavaExpression());
    }
}
