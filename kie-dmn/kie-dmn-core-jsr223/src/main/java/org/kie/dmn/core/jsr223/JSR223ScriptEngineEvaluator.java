package org.kie.dmn.core.jsr223;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.util.EvalHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSR223ScriptEngineEvaluator {
    
    private static final Logger LOG = LoggerFactory.getLogger( JSR223ScriptEngineEvaluator.class );
    private final ScriptEngine scriptEngine;
    private final String expression;
    
    public JSR223ScriptEngineEvaluator(ScriptEngine scriptEngine, String expression) {
        this.scriptEngine = scriptEngine;
        this.expression = expression;
    }
    
    /**
     * Opinionated evaluation for DMN scope.
     */
    public Object eval(Map<String, Object> ins) throws ScriptException {
        Bindings engineScope = scriptEngine.createBindings();
        for (Entry<String, Object> kv : ins.entrySet()) { 
            String key = JSR223Utils.escapeIdentifierForBinding(kv.getKey());
            Object value = kv.getValue();
            if (value instanceof BigDecimal) {
                value = JSR223Utils.doubleValueExact((BigDecimal) value);
            }
            if (value instanceof FEELFunction) {
                LOG.trace("SKIP binding {} of {}", key, value);
            } else {
                LOG.info("Setting binding {} to {}", key, value);
                engineScope.put(key, value);
            }
        }
        Object result = scriptEngine.eval(expression, engineScope);
        LOG.info("Script result: {}", result);
        return EvalHelper.coerceNumber(result);
    }
}
