package org.drools.metric.common;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.DefaultBetaConstraints;
import org.drools.base.rule.ContextEntry;
import org.drools.base.rule.MutableTypeConstraint;
import org.drools.base.rule.constraint.BetaNodeFieldConstraint;
import org.drools.core.reteoo.Tuple;
import org.drools.metric.util.MetricLogUtils;
import org.kie.api.runtime.rule.FactHandle;

public class DefaultBetaConstraintsMetric extends DefaultBetaConstraints {

    private static final long serialVersionUID = 510l;

    public DefaultBetaConstraintsMetric() {}

    public DefaultBetaConstraintsMetric(final BetaNodeFieldConstraint[] constraints,
                                        final RuleBaseConfiguration conf) {
        super(constraints, conf);

    }

    public DefaultBetaConstraintsMetric(final BetaNodeFieldConstraint[] constraints,
                                        final RuleBaseConfiguration conf,
                                        final boolean disableIndexing) {
        super(constraints, conf, disableIndexing);
    }

    @Override
    public DefaultBetaConstraintsMetric cloneIfInUse() {
        if (constraints[0] instanceof MutableTypeConstraint && ((MutableTypeConstraint) constraints[0]).setInUse()) {
            BetaNodeFieldConstraint[] clonedConstraints = new BetaNodeFieldConstraint[constraints.length];
            for (int i = 0; i < constraints.length; i++) {
                clonedConstraints[i] = constraints[i].cloneIfInUse();
            }
            DefaultBetaConstraintsMetric clone = new DefaultBetaConstraintsMetric();
            clone.constraints = clonedConstraints;
            clone.disableIndexing = disableIndexing;
            clone.indexPrecedenceOption = indexPrecedenceOption;
            clone.indexed = indexed;
            return clone;
        }
        return this;
    }

    @Override
    public boolean isAllowedCachedLeft(final ContextEntry[] context,
                                       final FactHandle handle) {
        MetricLogUtils.getInstance().incrementEvalCount();
        return super.isAllowedCachedLeft(context, handle);
    }

    @Override
    public boolean isAllowedCachedRight(final ContextEntry[] context,
                                        final Tuple tuple) {
        MetricLogUtils.getInstance().incrementEvalCount();
        return super.isAllowedCachedRight(context, tuple);
    }
}
