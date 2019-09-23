package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class MapPutExprT implements TypedExpression {

    private final TypedExpression name;
    private final Expression key;
    private final TypedExpression value;

    public MapPutExprT(TypedExpression name, Expression key, TypedExpression value) {
        this.name = name;
        this.key = key;
        this.value = value;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        return new MethodCallExpr((Expression) name.toJavaExpression(),
                                  "put",
                                  NodeList.nodeList(key, (Expression) value.toJavaExpression()));
    }
}
