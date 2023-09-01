package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.UnaryExpr;

public class UnalteredTypedExpression implements TypedExpression {

    private final Node originalExpression;
    private Type type;

    public UnalteredTypedExpression(Node originalExpression) {
        this(originalExpression, null);
    }

    public UnalteredTypedExpression(Node originalExpression, Type type) {
        this.originalExpression = originalExpression;
        this.type = type;

        if (originalExpression instanceof UnaryExpr) {
            Expression innerExpression = ((UnaryExpr) originalExpression).getExpression();
            if (innerExpression instanceof IntegerLiteralExpr) {
                this.type = int.class;
            } else if (innerExpression instanceof LongLiteralExpr) {
                this.type = long.class;
            }
        }
    }

    @Override
    public Optional<Type> getType() {
        return Optional.ofNullable(type);
    }

    @Override
    public Node toJavaExpression() {
        return (originalExpression instanceof SimpleName)
                ? originalExpression.getParentNode().orElseThrow(() -> new IllegalStateException("Expression has no parent node!"))
                : originalExpression;
    }

    @Override
    public String toString() {
        return "UnalteredTypedExpression{originalExpression=" + originalExpression + '}';
    }
}
