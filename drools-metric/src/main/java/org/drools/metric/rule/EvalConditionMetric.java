package org.drools.metric.rule;

import java.util.ArrayList;
import java.util.Collections;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.EvalCondition;
import org.drools.base.rule.accessor.EvalExpression;
import org.drools.metric.util.MetricLogUtils;

public class EvalConditionMetric extends EvalCondition {

    public EvalConditionMetric() {}

    public EvalConditionMetric(final Declaration[] requiredDeclarations) {
        super(requiredDeclarations);
    }

    public EvalConditionMetric(final EvalExpression eval,
                               final Declaration[] requiredDeclarations) {

        super(eval, requiredDeclarations);
    }

    @Override
    public boolean isAllowed(final BaseTuple tuple,
                             final ValueResolver valueResolver,
                             final Object context) {
        MetricLogUtils.getInstance().incrementEvalCount();
        return super.isAllowed(tuple, valueResolver, context);
    }

    @Override
    public EvalCondition clone() {
        // cannot rely on super.clone() because it enlists an EvalCondition instance to "cloned"
        final EvalConditionMetric clone = new EvalConditionMetric(this.expression.clone(),
                                                                  this.requiredDeclarations.clone());

        if (this.getCloned() == Collections.<EvalCondition> emptyList()) {
            this.setCloned(new ArrayList<>(1));
        }

        this.getCloned().add(clone);

        return clone;
    }
}
