package org.kie.dmn.feel.lang.impl;

import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.FunctionDefNode;

public class InterpretedExecutableExpression implements CompiledFEELExpression {

    private final CompiledExpressionImpl expr;

    public InterpretedExecutableExpression(CompiledExpressionImpl expr) {
        this.expr = expr;
    }

    public boolean isFunctionDef() {
        return expr.getExpression() instanceof FunctionDefNode;
    }

    @Override
    public Object apply(EvaluationContext evaluationContext) {
        return expr.apply(evaluationContext);
    }
}
