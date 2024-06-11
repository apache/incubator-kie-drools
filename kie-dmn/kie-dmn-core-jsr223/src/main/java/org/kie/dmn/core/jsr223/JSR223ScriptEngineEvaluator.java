/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.jsr223;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.util.NumberEvalHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSR223ScriptEngineEvaluator {
    
    public static final String DMN_UNARYTEST_SYMBOL = "DMN_UNARYTEST_SYMBOL";
    public static final String DMN_SYMBOL_ESCAPE_BOOL = "DMN_SYMBOL_ESCAPE_BOOL";
    private static final Logger LOG = LoggerFactory.getLogger( JSR223ScriptEngineEvaluator.class );
    private final ScriptEngine scriptEngine;
    private final String expression;
    
    private final Function<String, String> keyEscapeFn;
    
    public JSR223ScriptEngineEvaluator(ScriptEngine scriptEngine, String expression) {
        this.scriptEngine = scriptEngine;
        this.expression = expression;
        boolean shouldEscape = Boolean.valueOf(Optional.ofNullable(this.scriptEngine.getFactory().getParameter(DMN_SYMBOL_ESCAPE_BOOL)).map(Object::toString).orElse("false"));
        if (shouldEscape) {
            keyEscapeFn = JSR223Utils::escapeIdentifierForBinding;
        } else {
            keyEscapeFn = Function.identity();
        }
    }
    
    /**
     * Opinionated evaluation for DMN scope.
     */
    public Object eval(Map<String, Object> ins) throws ScriptException {
        Bindings engineScope = createBindings(ins);
        Object result = scriptEngine.eval(expression, engineScope);
        LOG.debug("Script eval of '{}' result: {}", expression, result);
        return NumberEvalHelper.coerceNumber(result);
    }
    
    /**
     * Opinionated evaluation for DMN scope.
     */
    public boolean test(Object in, Map<String, Object> context) throws ScriptException {
        Bindings engineScope = createBindings(context);
        String keyForUnaryTest = Optional.ofNullable(scriptEngine.getFactory().getParameter(DMN_UNARYTEST_SYMBOL).toString()).orElse("_");
        engineScope.put(keyForUnaryTest, in);
        Object result = scriptEngine.eval(expression, engineScope);
        LOG.debug("Script test of '{}' result: {}", expression, result);
        return result == Boolean.TRUE ? true : false;
    }

    private Bindings createBindings(Map<String, Object> ins) {
        Bindings engineScope = scriptEngine.createBindings();
        Map<String, Object> _context = new HashMap<>();
        engineScope.put("_context", _context ); // an opinionated DMN choice.
        for (Entry<String, Object> kv : ins.entrySet()) { 
            String key = keyEscapeFn.apply(kv.getKey());
            Object value = kv.getValue();
            // TODO should this be substituted with Jackson here?
            if (value instanceof BigDecimal) {
                value = JSR223Utils.doubleValueExact((BigDecimal) value);
            }
            if (value instanceof FEELFunction) {
                LOG.trace("SKIP binding {} of {}", key, value);
            } else {
                LOG.trace("Setting binding {} to {}", key, value);
                engineScope.put(key, value);
                _context.put(kv.getKey(), value);
            }
        }
        return engineScope;
    }
}
