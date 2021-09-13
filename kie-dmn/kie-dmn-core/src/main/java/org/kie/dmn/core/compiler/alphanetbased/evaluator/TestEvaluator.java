package org.kie.dmn.core.compiler.alphanetbased.evaluator;

import org.kie.dmn.core.compiler.alphanetbased.PropertyEvaluator;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELUnaryTests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestEvaluator {
    static Logger logger = LoggerFactory.getLogger(TestEvaluator.class);

    public static boolean evaluateAllTests(PropertyEvaluator propertyEvaluator,
                                           CompiledFEELUnaryTests instance,
                                           int index,
                                           String traceString) {
        return instance.getUnaryTests().stream().anyMatch(t -> {
            Object value = propertyEvaluator.getValue(index);
            Boolean result = t.apply(propertyEvaluator.getEvaluationContext(), value);
            if (logger.isTraceEnabled()) {
                logger.trace(traceString);
            }
            return result != null && result;
        });
    }
}
