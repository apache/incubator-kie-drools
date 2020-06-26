package org.drools.core.reteoo.builder;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.DefaultBetaConstraints;
import org.drools.core.common.DoubleBetaConstraints;
import org.drools.core.common.QuadroupleBetaConstraints;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.common.TripleBetaConstraints;
import org.drools.core.common.metric.DefaultBetaConstraintsMetric;
import org.drools.core.common.metric.DoubleBetaConstraintsMetric;
import org.drools.core.common.metric.QuadroupleBetaConstraintsMetric;
import org.drools.core.common.metric.SingleBetaConstraintsMetric;
import org.drools.core.common.metric.TripleBetaConstraintsMetric;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.util.PerfLogUtils;

public class BetaNodeConstraintFactory {

    private static final BetaNodeConstraintFactory INSTANCE = new BetaNodeConstraintFactory();

    public static BetaNodeConstraintFactory getInstance() {
        return INSTANCE;
    }

    private BetaNodeConstraintFactory() {}

    SingleBetaConstraints createSingleBetaConstraints(final BetaNodeFieldConstraint constraint,
                                                      final RuleBaseConfiguration conf,
                                                      final boolean disableIndex) {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new SingleBetaConstraintsMetric(constraint, conf, disableIndex);
        } else {
            return new SingleBetaConstraints(constraint, conf, disableIndex);
        }
    }

    DoubleBetaConstraints createDoubleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                      final RuleBaseConfiguration conf,
                                                      final boolean disableIndexing) {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new DoubleBetaConstraintsMetric(constraints, conf, disableIndexing);
        } else {
            return new DoubleBetaConstraints(constraints, conf, disableIndexing);
        }
    }

    TripleBetaConstraints createTripleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                      final RuleBaseConfiguration conf,
                                                      final boolean disableIndexing) {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new TripleBetaConstraintsMetric(constraints, conf, disableIndexing);
        } else {
            return new TripleBetaConstraints(constraints, conf, disableIndexing);
        }
    }

    QuadroupleBetaConstraints createQuadroupleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                              final RuleBaseConfiguration conf,
                                                              final boolean disableIndexing) {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new QuadroupleBetaConstraintsMetric(constraints, conf, disableIndexing);
        } else {
            return new QuadroupleBetaConstraints(constraints, conf, disableIndexing);
        }
    }

    DefaultBetaConstraints createDefaultBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                        final RuleBaseConfiguration conf,
                                                        final boolean disableIndexing) {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new DefaultBetaConstraintsMetric(constraints, conf, disableIndexing);
        } else {
            return new DefaultBetaConstraints(constraints, conf, disableIndexing);
        }
    }
}
