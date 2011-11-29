package org.drools.rule.constraint;

import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableStatement;

import java.io.Serializable;

public class MvelConditionEvaluator implements ConditionEvaluator {

    private ExecutableStatement stmt;

    MvelConditionEvaluator(ParserConfiguration conf, String expression) {
        stmt = (ExecutableStatement)MVEL.compileExpression(expression, new ParserContext(conf));
    }

    public boolean evaluate(Object object) {
        return (Boolean)MVEL.executeExpression(stmt, object);
    }

    ExecutableStatement getExecutableStatement() {
        return stmt;
    }
}
