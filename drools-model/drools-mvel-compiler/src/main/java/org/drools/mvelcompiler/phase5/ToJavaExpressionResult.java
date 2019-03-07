package org.drools.mvelcompiler.phase5;

import com.github.javaparser.ast.expr.Expression;

public class ToJavaExpressionResult {

    final Expression expression;

    ToJavaExpressionResult(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
