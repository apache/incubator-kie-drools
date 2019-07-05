package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;

public class IntegerLiteralExpressionT implements TypedExpression {

    private final IntegerLiteralExpr integerLiteralExpr;

    public IntegerLiteralExpressionT(IntegerLiteralExpr integerLiteralExpr) {

        this.integerLiteralExpr = integerLiteralExpr;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(int.class);
    }

    @Override
    public Node toJavaExpression() {
        return integerLiteralExpr;
    }

    @Override
    public String toString() {
        return "IntegerLiteralExpressionT{" +
                "originalExpression=" + integerLiteralExpr +
                '}';
    }
}
