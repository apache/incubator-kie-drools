package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;

import com.github.javaparser.ast.Node;

public class IntegerLiteralExpressionT extends TypedExpression {

    public IntegerLiteralExpressionT(Node originalExpression) {
        super(originalExpression);
    }

    @Override
    public Type getType() {
        return int.class;
    }

    @Override
    public Node toJavaExpression() {
        return originalExpression;
    }

    @Override
    public String toString() {
        return "IntegerLiteralExpressionT{" +
                "originalExpression=" + originalExpression +
                '}';
    }
}
