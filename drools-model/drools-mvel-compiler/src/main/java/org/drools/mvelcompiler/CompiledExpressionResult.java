package org.drools.mvelcompiler;

import com.github.javaparser.ast.expr.Expression;

import static org.drools.mvel.parser.printer.PrintUtil.printConstraint;

public class CompiledExpressionResult {

    private Expression expression;

    public CompiledExpressionResult(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public String resultAsString() {
        return printConstraint(expression);
    }
}
