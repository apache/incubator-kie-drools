package org.drools.metric.reteoo.builder;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.DefaultBetaConstraints;
import org.drools.core.common.DoubleBetaConstraints;
import org.drools.core.common.QuadroupleBetaConstraints;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.common.TripleBetaConstraints;
import org.drools.core.reteoo.builder.BetaNodeConstraintFactory;
import org.drools.base.rule.constraint.BetaNodeFieldConstraint;
import org.drools.metric.common.DefaultBetaConstraintsMetric;
import org.drools.metric.common.DoubleBetaConstraintsMetric;
import org.drools.metric.common.QuadroupleBetaConstraintsMetric;
import org.drools.metric.common.SingleBetaConstraintsMetric;
import org.drools.metric.common.TripleBetaConstraintsMetric;
import org.drools.metric.util.MetricLogUtils;

public class MetricBetaNodeConstraintFactoryImpl implements BetaNodeConstraintFactory {

    @Override
    public SingleBetaConstraints createSingleBetaConstraints(final BetaNodeFieldConstraint constraint,
                                                             final RuleBaseConfiguration conf,
                                                             final boolean disableIndex) {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new SingleBetaConstraintsMetric(constraint, conf, disableIndex);
        } else {
            return new SingleBetaConstraints(constraint, conf, disableIndex);
        }
    }

    @Override
    public DoubleBetaConstraints createDoubleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                             final RuleBaseConfiguration conf,
                                                             final boolean disableIndexing) {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new DoubleBetaConstraintsMetric(constraints, conf, disableIndexing);
        } else {
            return new DoubleBetaConstraints(constraints, conf, disableIndexing);
        }
    }

    @Override
    public TripleBetaConstraints createTripleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                             final RuleBaseConfiguration conf,
                                                             final boolean disableIndexing) {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new TripleBetaConstraintsMetric(constraints, conf, disableIndexing);
        } else {
            return new TripleBetaConstraints(constraints, conf, disableIndexing);
        }
    }

    @Override
    public QuadroupleBetaConstraints createQuadroupleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                                     final RuleBaseConfiguration conf,
                                                                     final boolean disableIndexing) {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new QuadroupleBetaConstraintsMetric(constraints, conf, disableIndexing);
        } else {
            return new QuadroupleBetaConstraints(constraints, conf, disableIndexing);
        }
    }

    @Override
    public DefaultBetaConstraints createDefaultBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                               final RuleBaseConfiguration conf,
                                                               final boolean disableIndexing) {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new DefaultBetaConstraintsMetric(constraints, conf, disableIndexing);
        } else {
            return new DefaultBetaConstraints(constraints, conf, disableIndexing);
        }
    }
}
