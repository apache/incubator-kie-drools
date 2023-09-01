package org.kie.dmn.core.compiler.alphanetbased;

import java.util.Arrays;

import org.kie.dmn.feel.lang.EvaluationContext;

// A class used to evaluate a property name against a FEEL evaluation context
public class PropertyEvaluator {

    private final EvaluationContext evaluationContext;
    private final Object[] values;

    public PropertyEvaluator(EvaluationContext evaluationContext, String... propertyNames) {
        this.evaluationContext = evaluationContext;
        this.values = new Object[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++) {
            values[i] = evaluationContext.getValue(propertyNames[i]);
        }
    }

    public Object getValue(int i) {
        return values[i];
    }

    public EvaluationContext getEvaluationContext() {
        return evaluationContext;
    }

    @Override
    public String toString() {
        return "PropertyEvaluator{" +
                "values=" + Arrays.toString(values) +
                '}';
    }
}
