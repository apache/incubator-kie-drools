package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.Statement;

public class ForEachStmtT implements TypedExpression {

    private TypedExpression variableDeclaratorExpr;
    private TypedExpression iterable;
    private TypedExpression body;

    public ForEachStmtT(TypedExpression variableDeclaratorExpr, TypedExpression iterable, TypedExpression body) {
        this.variableDeclaratorExpr = variableDeclaratorExpr;
        this.iterable = iterable;
        this.body = body;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        return new ForEachStmt((VariableDeclarationExpr) variableDeclaratorExpr.toJavaExpression(),
                               (Expression) iterable.toJavaExpression(),
                               (Statement) body.toJavaExpression());
    }
}
