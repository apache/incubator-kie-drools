package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;

public class VariableDeclaratorTExpr extends TypedExpression {

    private final VariableDeclarator originalNode;
    private final SimpleName name;
    private final Optional<TypedExpression> initExpression;

    public VariableDeclaratorTExpr(VariableDeclarator originalNode, SimpleName name, Optional<TypedExpression> initExpression) {
        super(originalNode);
        this.originalNode = originalNode;
        this.name = name;
        this.initExpression = initExpression;
    }

    @Override
    public Type getType() {
        return initExpression.map(typedExpression -> typedExpression.children.get(0).getType()).orElse(null);
    }

    @Override
    public Node toJavaExpression() {
        return initExpression.map(ie -> {
            com.github.javaparser.ast.type.Type initializationExpressionType = JavaParser.parseType(ie.getType().getTypeName());
            return new VariableDeclarator(initializationExpressionType, name.asString(), (Expression) ie.toJavaExpression());
        }).orElse(originalNode);
    }
}
