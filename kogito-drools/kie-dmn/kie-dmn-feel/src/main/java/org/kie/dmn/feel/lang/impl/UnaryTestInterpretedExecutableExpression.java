package org.kie.dmn.feel.lang.impl;

import java.util.Collections;
import java.util.List;

import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.runtime.UnaryTest;

public class UnaryTestInterpretedExecutableExpression implements CompiledFEELExpression {

    public static final UnaryTestInterpretedExecutableExpression EMPTY = new UnaryTestInterpretedExecutableExpression(null) {
        @Override
        public List<UnaryTest> apply(EvaluationContext evaluationContext) {
            return Collections.emptyList();
        }
    };
    private final CompiledExpressionImpl expr;

    public UnaryTestInterpretedExecutableExpression(CompiledExpressionImpl expr) {
        this.expr = expr;
    }

    public List<UnaryTest> apply(EvaluationContext evaluationContext) {
        return (List<UnaryTest>) expr.apply(evaluationContext);
    }

    /**
     * Internals helper methods.
     */
    public ASTNode getASTNode() {
        return expr.getExpression();
    }

}
