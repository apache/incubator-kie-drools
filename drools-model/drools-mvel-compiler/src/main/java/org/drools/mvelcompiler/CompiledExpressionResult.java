package org.drools.mvelcompiler;

import com.github.javaparser.ast.expr.Expression;

public class CompiledExpressionResult {

    private Expression expression;

    public CompiledExpressionResult(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
