/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.jsr223.jq;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicTest {
    private static final Logger LOG = LoggerFactory.getLogger( BasicTest.class );

    private static final ScriptEngineManager SEMANAGER = new ScriptEngineManager();
    
    private ScriptEngine engine;

    @BeforeEach
    public void init() {
        engine = SEMANAGER.getEngineByName("jq");        
    }
    
    @Test
    public void testEval() throws ScriptException {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("a", 1);
        ctx.put("b", 2);
        Object result = engine.eval(" .a + .b ", new SimpleBindings( ctx ));
        LOG.info("{}", result);
        assertThat(result).asString().isEqualTo("3");
    }
    
    @Test
    public void testEvalJSONKey() throws ScriptException {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("Age", 1);
        ctx.put("Previous incidents?", false);
        Object result = engine.eval(" .\"Previous incidents?\" ", new SimpleBindings( ctx ));
        LOG.info("{}", result);
        assertThat(result).isEqualTo(false);
    }
    
    @Test
    public void testTest() throws ScriptException {
        evalToStringEquals(testCtx(1), " . > 10 ", false);
        evalToStringEquals(testCtx(47), " . > 10 ", true);
        evalToStringEquals(testCtx(47), " . > $a ", true);
    }

    private void evalToStringEquals(Map<String, Object> ctx, String testScript, Object expected) throws ScriptException {
        Object evalResult = engine.eval(testScript, new SimpleBindings( ctx ));
        LOG.info("{}", evalResult);
        assertThat(evalResult).isEqualTo(expected);
    }

    private Map<String, Object> testCtx(Object dotValue) {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("a", 1);
        ctx.put("b", 2);
        ctx.put(JQScriptEngine.DMN_UNARYTEST_SYMBOL_VALUE, dotValue);
        return ctx;
    }
}
