package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

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
        Optional<Type> optInitType = initExpression.flatMap(TypedExpression::getType);
        com.github.javaparser.ast.type.Type jpType = StaticJavaParser.parseType(this.type.getTypeName());

        return initExpression.map(ie -> {

            Expression initializer = (Expression) ie.toJavaExpression();
            if(!optInitType.isPresent()) {
                initializer = new CastExpr(jpType, new EnclosedExpr(initializer));
            }
            return (Node) new VariableDeclarationExpr(new VariableDeclarator(jpType, name, initializer));
        }).orElse(new VariableDeclarationExpr(jpType, name));
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
