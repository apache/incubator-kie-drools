package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;

import static com.github.javaparser.ast.NodeList.nodeList;

/**
 * A ForEachStatement downcasting the iterable variable
 */
public class ForEachDowncastStmtT implements TypedExpression {

    private VariableDeclarationExpr variableDeclarationExpr;
    private String iterable;
    private TypedExpression child;

    public ForEachDowncastStmtT(VariableDeclarationExpr variableDeclarationExpr, String iterable, TypedExpression child) {
        this.variableDeclarationExpr = variableDeclarationExpr;
        this.iterable = iterable;
        this.child = child;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        ForEachStmt newForEachStmt = new ForEachStmt();

        BlockStmt body = new BlockStmt();

        NodeList<VariableDeclarator> variables = nodeList();

        for (VariableDeclarator v : variableDeclarationExpr.getVariables()) {
            VariableDeclarator newVariable = v.clone();

            String newIteratorVariable = "_" + v.getNameAsString();

            VariableDeclarationExpr castAssign = new VariableDeclarationExpr(
                    new VariableDeclarator(v.getType(), v.getName(),
                                           new CastExpr(v.getType(), new NameExpr(newIteratorVariable))));

            body.addStatement(0, castAssign);

            newVariable.setType(Object.class);
            newVariable.setName(newIteratorVariable);

            variables.add(newVariable);
        }

        body.addStatement((BlockStmt) child.toJavaExpression());
        newForEachStmt.setBody(body);

        VariableDeclarationExpr newVariables = new VariableDeclarationExpr(variables);
        newForEachStmt.setVariable(newVariables);

        return new ForEachStmt(newVariables, new NameExpr(iterable), body);
    }

    @Override
    public String toString() {
        return "ForEachDowncastStmtT{" +
                "variableDeclarationExpr=" + variableDeclarationExpr +
                ", child=" + child +
                '}';
    }
}
