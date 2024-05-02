package org.kie.yard.core;

import org.drools.base.util.MVELExecutor;
import org.mvel2.MVEL;

import javax.script.Bindings;
import java.util.HashMap;
import java.util.Map;

public class MVELLiteralExpressionInterpreter implements Firable {
    private final String name;
    private final QuotedExprParsed expr;

    public MVELLiteralExpressionInterpreter(final String name,
                                            final QuotedExprParsed expr) {
        this.name = name;
        this.expr = expr;
    }

    @Override
    public int fire(final Map<String, Object> context,
                    final YaRDDefinitions units) {
        final Map<String, Object> internalContext = new HashMap<>();
        internalContext.putAll(context);

        for (Map.Entry<String, StoreHandle<Object>> outKV : units.outputs().entrySet()) {
            if (!outKV.getValue().isValuePresent()) {
                continue;
            }
            internalContext.put(QuotedExprParsed.escapeIdentifier(outKV.getKey()), outKV.getValue().get());
        }

        try {
            String rewrittenExpression = expr.getRewrittenExpression();
            final Object result = MVEL.eval(rewrittenExpression, internalContext);
            units.outputs().get(name).set(result);
            return 1;
        } catch (Exception e) {
            throw new RuntimeException("interpretation failed at runtime", e);
            // TODO why throw and not return 0?
        }
    }
}
