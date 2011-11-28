package org.drools.rule.constraint;

import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;

public class MvelConditionEvaluator implements ConditionEvaluator {

    private String expression;
    private CompiledExpression compiledExpression;

    MvelConditionEvaluator(String expression) {
        this.expression = expression;
        compiledExpression = (CompiledExpression)MVEL.compileExpression(expression);
    }

    public boolean evaluate(Object object) {
        return (Boolean)MVEL.executeExpression(compiledExpression, object);
    }

    CompiledExpression getCompiledExpression() {
        return compiledExpression;
    }
}
