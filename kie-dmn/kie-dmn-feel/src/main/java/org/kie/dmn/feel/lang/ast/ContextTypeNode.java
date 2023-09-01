package org.kie.dmn.feel.lang.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.MapBackedType;

public class ContextTypeNode extends TypeNode {

    private final Map<String, TypeNode> gen;

    public ContextTypeNode(ParserRuleContext ctx, Map<String, TypeNode> gen) {
        super( ctx );
        this.gen = new HashMap<>(gen);
    }

    @Override
    public Type evaluate(EvaluationContext ctx) {
        return new MapBackedType("[anonymous]", evalTypes(ctx, gen));
    }

    public static Map<String, Type> evalTypes(EvaluationContext ctx, Map<String, TypeNode> gen) {
        Map<String, Type> fields = new HashMap<>();
        for (Entry<String, TypeNode> kv : gen.entrySet()) {
            fields.put(kv.getKey(), kv.getValue().evaluate(ctx));
        }
        return fields;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    public Map<String, TypeNode> getGen() {
        return gen;
    }
}
