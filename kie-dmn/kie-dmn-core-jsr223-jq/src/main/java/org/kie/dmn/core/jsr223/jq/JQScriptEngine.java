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
package org.kie.dmn.core.jsr223.jq;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.thisptr.jackson.jq.BuiltinFunctionLoader;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;

public class JQScriptEngine extends AbstractScriptEngine { // TODO evaluate implementing Compilable
    
    public static final String DMN_UNARYTEST_SYMBOL = "DMN_UNARYTEST_SYMBOL";
    public static final String DMN_UNARYTEST_SYMBOL_VALUE = ".";
    public static final String DMN_SYMBOL_ESCAPE_BOOL = "DMN_SYMBOL_ESCAPE_BOOL";
    
    private final ScriptEngineFactory factory;
    
    private final ObjectMapper MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build()
            //.configure(JsonWriteFeature.QUOTE_FIELD_NAMES.mappedFeature(), true)
            ;
    
    JQScriptEngine(JQScriptEngineFactory factory) {
        this.factory = factory;
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        try {
            Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
            Scope rootScope = Scope.newEmptyScope();
            BuiltinFunctionLoader.getInstance().loadFunctions(Versions.JQ_1_6, rootScope);
            Scope childScope = Scope.newChildScope(rootScope);
            JsonNode in;
            if (bindings.containsKey(DMN_UNARYTEST_SYMBOL_VALUE)) { // TODO or check if there is a way to pass multiple inputs to JQ, without resorting to fixed childScopes
                for (Entry<String, Object> kv : bindings.entrySet()) {
                    if (!kv.getKey().equals(DMN_UNARYTEST_SYMBOL_VALUE)) {
                        childScope.setValue(kv.getKey(),  MAPPER.valueToTree(kv.getValue()));
                    }
                }
                in = MAPPER.valueToTree(bindings.get(DMN_UNARYTEST_SYMBOL_VALUE));
            } else {
                in = MAPPER.valueToTree(bindings);
            }
            JsonQuery q = JsonQuery.compile(script, Versions.JQ_1_6);
            final List<JsonNode> out = new ArrayList<>();
            q.apply(childScope, in, out::add);
            JsonNode outNode = out.get(0);
            Object result;
            if (!outNode.isValueNode()) {
                result = MAPPER.treeToValue(outNode, Map.class);
            } else if (outNode.isArray()) {
                result = MAPPER.treeToValue(outNode, List.class);
            } else if (outNode.isValueNode()) {
                result = MAPPER.treeToValue(outNode, Object.class);
            } else {
                throw new UnsupportedOperationException("TODO");
            }
            return result;
        } catch (Exception e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        try (Scanner scanner = new Scanner(reader)) {
            String script = scanner.useDelimiter("\\Z").next();
            return eval(script, context);
        } catch (Exception e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return factory;
    }


}
