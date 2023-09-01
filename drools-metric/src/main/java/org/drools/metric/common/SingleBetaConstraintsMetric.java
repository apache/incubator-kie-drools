package org.drools.metric.common;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.base.rule.ContextEntry;
import org.drools.base.rule.MutableTypeConstraint;
import org.drools.base.rule.constraint.BetaNodeFieldConstraint;
import org.drools.core.reteoo.Tuple;
import org.drools.metric.util.MetricLogUtils;
import org.kie.api.runtime.rule.FactHandle;

public class SingleBetaConstraintsMetric extends SingleBetaConstraints {

    private static final long serialVersionUID = 510l;

    public SingleBetaConstraintsMetric() {}

    public SingleBetaConstraintsMetric(final BetaNodeFieldConstraint[] constraint,
                                       final RuleBaseConfiguration conf) {
        super(constraint[0], conf);
    }

    public SingleBetaConstraintsMetric(final BetaNodeFieldConstraint constraint,
                                       final RuleBaseConfiguration conf) {
        super(constraint, conf);
    }

    public SingleBetaConstraintsMetric(final BetaNodeFieldConstraint constraint,
                                       final RuleBaseConfiguration conf,
                                       final boolean disableIndex) {
        super(constraint, conf, disableIndex);
    }

    @Override
    public SingleBetaConstraintsMetric cloneIfInUse() {
        if (constraint instanceof MutableTypeConstraint && ((MutableTypeConstraint) constraint).setInUse()) {
            SingleBetaConstraintsMetric clone = new SingleBetaConstraintsMetric(constraint.cloneIfInUse(), null, disableIndex);
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
