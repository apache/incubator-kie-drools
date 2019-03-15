package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Function;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;

import static org.drools.mvelcompiler.util.OptionalUtils.map2;

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
    public Optional<Type> getType() {
        return initExpression.flatMap(typedExpression -> typedExpression.children.get(0).getType());
    }

    @Override
    public Node toJavaExpression() {
        final Optional<Type> ieType = initExpression.flatMap(TypedExpression::getType);

        return map2(initExpression, ieType, (ie, type) -> {
            com.github.javaparser.ast.type.Type initializationExpressionType = JavaParser.parseType(type.getTypeName());
            return new VariableDeclarator(initializationExpressionType, name.asString(), (Expression) ie.toJavaExpression());
        }).orElse(originalNode);
    }
}
