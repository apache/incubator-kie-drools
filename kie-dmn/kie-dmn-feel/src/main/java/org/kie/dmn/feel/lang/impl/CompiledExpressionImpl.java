package org.kie.dmn.feel.lang.impl;

import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.FunctionDefNode;

public class CompiledExpressionImpl implements CompiledFEELExpression {
    private ASTNode     expression;

    public CompiledExpressionImpl(ASTNode expression) {
        this.expression = expression;
    }

    public ASTNode getExpression() {
        return expression;
    }

    public boolean isFunctionDef() {
        return expression instanceof FunctionDefNode;
    }

    public void setExpression( ASTNode expression ) {
        this.expression = expression;
    }

    public Object apply(EvaluationContext evaluationContext) {
        if (expression == null) {
            return null;
        }
        return expression.evaluate(evaluationContext);
    }

    @Override
    public String toString() {
        return "CompiledExpressionImpl{" +
               "expression=" + expression +
               '}';
    }
}
