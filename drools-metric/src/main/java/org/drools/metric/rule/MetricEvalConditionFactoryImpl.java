package org.drools.metric.rule;

import org.drools.base.rule.Declaration;
import org.drools.base.rule.EvalCondition;
import org.drools.base.rule.EvalConditionFactory;
import org.drools.metric.util.MetricLogUtils;

public class MetricEvalConditionFactoryImpl implements EvalConditionFactory {

    @Override
    public EvalCondition createEvalCondition(final Declaration[] requiredDeclarations) {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new EvalConditionMetric(requiredDeclarations);
        } else {
            return new EvalCondition(requiredDeclarations);
        }
    }
}
