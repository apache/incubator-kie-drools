package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

public class VariableDeclarationTExpr extends TypedExpression {

    private final TypedExpression variableDeclaratorTExpr;

    public VariableDeclarationTExpr(Node originalExpression, TypedExpression variableDeclaratorTExpr) {
        super(originalExpression);
        this.variableDeclaratorTExpr = variableDeclaratorTExpr;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        return new VariableDeclarationExpr((VariableDeclarator) variableDeclaratorTExpr.toJavaExpression());
    }

    @Override
    public String toString() {
        return "VariableDeclarationTExpr{}";
    }
}
