package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.Statement;

public class DoStmtT implements TypedExpression {

    private final TypedExpression condition;
    private final TypedExpression body;

    public DoStmtT(TypedExpression condition, TypedExpression body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        DoStmt stmt = new DoStmt();
        stmt.setCondition((Expression) condition.toJavaExpression());
        stmt.setBody((Statement) body.toJavaExpression());
        return stmt;
    }
}
