package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;

public class LongLiteralExpressionT implements TypedExpression {

    private final LongLiteralExpr longLiteralExpr;

    public LongLiteralExpressionT(LongLiteralExpr longLiteralExpr) {
        this.longLiteralExpr = longLiteralExpr;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(long.class);
    }

    @Override
    public Node toJavaExpression() {
        return longLiteralExpr;
    }

    @Override
    public String toString() {
        return "LongLiteralExpressionT{" +
                "longLiteralExpr=" + longLiteralExpr +
                '}';
    }
}
