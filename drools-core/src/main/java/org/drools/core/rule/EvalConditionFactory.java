package org.drools.core.rule;

import org.drools.core.rule.metric.EvalConditionMetric;
import org.drools.core.util.PerfLogUtils;

public class EvalConditionFactory {

    private static final EvalConditionFactory INSTANCE = new EvalConditionFactory();

    public static EvalConditionFactory getInstance() {
        return INSTANCE;
    }

    private EvalConditionFactory() {}

    public EvalCondition createEvalCondition(final Declaration[] requiredDeclarations) {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new EvalConditionMetric(requiredDeclarations);
        } else {
            return new EvalCondition(requiredDeclarations);
        }
    }
}
