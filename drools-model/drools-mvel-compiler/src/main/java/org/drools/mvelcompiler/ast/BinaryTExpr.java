package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;

public class BinaryTExpr extends TypedExpression {

    private final TypedExpression left;
    private final TypedExpression right;
    private final BinaryExpr.Operator operator;

    public BinaryTExpr(BinaryExpr original, TypedExpression left, TypedExpression right, BinaryExpr.Operator operator) {
        super(original);
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        return new BinaryExpr((Expression) left.toJavaExpression(), (Expression) right.toJavaExpression(), operator);
    }
}
