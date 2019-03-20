package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import static org.drools.mvelcompiler.util.OptionalUtils.map2;

public class VariableDeclaratorTExpr implements TypedExpression {

    private final Node originalNode;
    private final String name;
    private final Type type;
    private final Optional<TypedExpression> initExpression;

    public VariableDeclaratorTExpr(Node originalNode, String name, Type type, Optional<TypedExpression> initExpression) {
        this.originalNode = originalNode;
        this.name = name;
        this.type = type;
        this.initExpression = initExpression;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {
        final Optional<Type> ieType = initExpression.flatMap(TypedExpression::getType);

        return map2(initExpression, ieType, (ie, type) -> {
            com.github.javaparser.ast.type.Type initializationExpressionType = JavaParser.parseType(type.getTypeName());
            return (Node) new VariableDeclarationExpr(new VariableDeclarator(initializationExpressionType, name, (Expression) ie.toJavaExpression()));
        }).orElse(originalNode);
    }

    @Override
    public String toString() {
        return "VariableDeclaratorTExpr{" +
                "originalNode=" + originalNode +
                ", name=" + name +
                ", initExpression=" + initExpression +
                '}';
    }
}
