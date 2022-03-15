package org.kie.dmn.core.jsr223.jq;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            JsonQuery q = JsonQuery.compile(script, Versions.JQ_1_6); // TODO see above, this should be Compilable
            JsonNode in = MAPPER.valueToTree(bindings);
            // TODO try : if UnaryTest, move bindings into childScope, and in becomes the "column"
            final List<JsonNode> out = new ArrayList<>();
            q.apply(childScope, in, out::add);
            JsonNode outNode = out.get(0);
            Object result = null;
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
