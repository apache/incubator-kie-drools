package org.drools.rule.constraint;

import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.util.Soundex;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MvelConditionEvaluator implements ConditionEvaluator {

    private ExecutableStatement stmt;
    private String expression;
    private String operator;

    MvelConditionEvaluator(ParserConfiguration conf, String expression, String operator) {
        this.expression = expression;
        this.operator = operator;
        stmt = (ExecutableStatement)MVEL.compileExpression(expression, new ParserContext(conf));
    }

    public boolean evaluate(Object object, Map<String, Object> vars) {
        return vars == null ? (Boolean)MVEL.executeExpression(stmt, object) : (Boolean)MVEL.executeExpression(stmt, object, vars);
    }

    ExecutableStatement getExecutableStatement() {
        return stmt;
    }
}
