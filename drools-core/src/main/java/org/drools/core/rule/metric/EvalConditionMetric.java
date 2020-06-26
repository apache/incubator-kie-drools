package org.drools.core.rule.metric;

import java.util.ArrayList;
import java.util.Collections;

import org.drools.core.WorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EvalCondition;
import org.drools.core.spi.EvalExpression;
import org.drools.core.spi.Tuple;
import org.drools.core.util.PerfLogUtils;

public class EvalConditionMetric extends EvalCondition {

    public EvalConditionMetric() {
        super();
    }

    public EvalConditionMetric(final Declaration[] requiredDeclarations) {
        super(requiredDeclarations);
    }

    public EvalConditionMetric(final EvalExpression eval,
                               final Declaration[] requiredDeclarations) {

        super(eval, requiredDeclarations);
    }

    @Override
    public boolean isAllowed(final Tuple tuple,
                             final WorkingMemory workingMemory,
                             final Object context) {
        PerfLogUtils.getInstance().incrementEvalCount();
        return super.isAllowed(tuple, workingMemory, context);
    }

    @Override
    public EvalCondition clone() {
        // cannot rely on super.clone() because it enlists an EvalCondition instance to "cloned"
        final EvalConditionMetric clone = new EvalConditionMetric(this.expression.clone(),
                                                                  (Declaration[]) this.requiredDeclarations.clone());

        if (this.cloned == Collections.EMPTY_LIST) {
            this.cloned = new ArrayList<EvalCondition>(1);
        }

        this.cloned.add(clone);

        return clone;
    }
}
