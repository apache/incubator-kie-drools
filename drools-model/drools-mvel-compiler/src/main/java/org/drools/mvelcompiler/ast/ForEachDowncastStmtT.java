package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.Statement;

/**
 * A ForEachStatement that downcast the iterable variable
 */
public class ForEachDowncastStmtT implements TypedExpression {

    ForEachStmt originalExpression;

    public ForEachDowncastStmtT(ForEachStmt originalExpression) {
        this.originalExpression = originalExpression;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        ForEachStmt clone = new ForEachStmt();

        VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
        Expression iterable = clone.getIterable();
        Statement body = new BlockStmt();


        ForEachStmt forEachStmt = new ForEachStmt(variableDeclarationExpr, iterable, body);

        return forEachStmt;
    }

    @Override
    public String toString() {
        return "ForEachStmtT{" +
                "originalExpression=" + originalExpression +
                '}';
    }
}
