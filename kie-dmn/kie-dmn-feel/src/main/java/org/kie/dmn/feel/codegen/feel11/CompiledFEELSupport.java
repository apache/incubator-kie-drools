package org.kie.dmn.feel.codegen.feel11;

import java.util.HashMap;
import java.util.Map;

import org.kie.dmn.feel.lang.EvaluationContext;

public class CompiledFEELSupport {
    
    public static Object conditionWasNotBoolean(EvaluationContext ctx) {
        // TODO insert here some "exception" raise by notify ctx that the IF condition was not a boolean.
        return null;
    }

    public static ContextBuilder openContext(EvaluationContext ctx) {
        return new ContextBuilder(ctx);
    }

    public static class ContextBuilder {
        private Map<String, Object> resultContext = new HashMap<>();
        private EvaluationContext evaluationContext;

        public ContextBuilder(EvaluationContext evaluationContext) {
            this.evaluationContext = evaluationContext;
            evaluationContext.enterFrame();
        }

        public ContextBuilder setEntry(String key, Object value) {
            resultContext.put(key, value);
            evaluationContext.setValue(key, value);
            return this;
        }

        public Map<String, Object> closeContext() {
            evaluationContext.exitFrame();
            return resultContext;
        }
    }
}
