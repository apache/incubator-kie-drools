package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

public class IfStmtT implements TypedExpression {

    private final TypedExpression typedCondition;
    private final TypedExpression typedThen;
    private final Optional<TypedExpression> typedElse;

    public IfStmtT(TypedExpression typedCondition, TypedExpression typedThen, Optional<TypedExpression> typedElse) {
        this.typedCondition = typedCondition;
        this.typedThen = typedThen;
        this.typedElse = typedElse;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        IfStmt stmt = new IfStmt();
        stmt.setCondition((Expression) typedCondition.toJavaExpression());
        stmt.setThenStmt((Statement) typedThen.toJavaExpression());
        typedElse.ifPresent(e -> stmt.setElseStmt((Statement) e.toJavaExpression()));
        return stmt;
    }
}
