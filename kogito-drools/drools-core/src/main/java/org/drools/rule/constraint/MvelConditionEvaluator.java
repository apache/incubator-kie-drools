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
    private String operator;
    private boolean needMvelVars;

    MvelConditionEvaluator(ParserConfiguration conf, String expression, String operator) {
        this.operator = operator;
        this.needMvelVars = operator.equals("soundslike");
        stmt = (ExecutableStatement)MVEL.compileExpression(expression, getParserContext(conf));
    }

    private ParserContext getParserContext(ParserConfiguration conf) {
        ParserContext context = new ParserContext(conf);
        if (operator.equals("soundslike")) {
            context.addImport("soundex", (MVEL.getStaticMethod(Soundex.class, "soundex", new Class[] { String.class })));
        }
        return context;
    }

    // TODO: remove this method
    public boolean evaluate(Object object) {
        return evaluate(object, null);
    }

    // TODO: do the vars check outside this method
    public boolean evaluate(Object object, Map<String, Object> vars) {
        if (vars == null && needMvelVars) vars = new HashMap<String, Object>();
        return vars == null ? (Boolean)MVEL.executeExpression(stmt, object) : (Boolean)MVEL.executeExpression(stmt, object, vars);
    }

    ExecutableStatement getExecutableStatement() {
        return stmt;
    }
}
