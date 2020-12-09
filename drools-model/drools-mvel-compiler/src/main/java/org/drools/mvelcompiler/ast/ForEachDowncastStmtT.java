package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import org.drools.mvel.parser.ast.visitor.DrlCloneVisitor;

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
        ForEachStmt clone = (ForEachStmt) originalExpression.accept(new DrlCloneVisitor(), null);

        BlockStmt body = (BlockStmt) clone.getBody();
        VariableDeclarationExpr variableDeclarationExpr = clone.getVariable();
        for (VariableDeclarator v : variableDeclarationExpr.getVariables()) {
            String newIteratorVariable = "_" + v.getNameAsString();

            VariableDeclarationExpr castAssign = new VariableDeclarationExpr(
                    new VariableDeclarator(v.getType(), v.getName(),
                                           new CastExpr(v.getType(), new NameExpr(newIteratorVariable))));

            body.addStatement(0, castAssign);

            v.setType(Object.class);
            v.setName(newIteratorVariable);
        }

        return clone;
    }

    @Override
    public String toString() {
        return "ForEachStmtT{" +
                "originalExpression=" + originalExpression +
                '}';
    }
}
