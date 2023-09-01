package org.drools.core.reteoo.builder;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.DefaultBetaConstraints;
import org.drools.core.common.DoubleBetaConstraints;
import org.drools.core.common.QuadroupleBetaConstraints;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.common.TripleBetaConstraints;
import org.drools.base.rule.constraint.BetaNodeFieldConstraint;

public class BetaNodeConstraintFactoryImpl implements BetaNodeConstraintFactory {

    @Override
    public SingleBetaConstraints createSingleBetaConstraints(final BetaNodeFieldConstraint constraint,
                                                             final RuleBaseConfiguration conf,
                                                             final boolean disableIndex) {
        return new SingleBetaConstraints(constraint, conf, disableIndex);
    }

    @Override
    public DoubleBetaConstraints createDoubleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                             final RuleBaseConfiguration conf,
                                                             final boolean disableIndexing) {
        return new DoubleBetaConstraints(constraints, conf, disableIndexing);
    }

    @Override
    public TripleBetaConstraints createTripleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                             final RuleBaseConfiguration conf,
                                                             final boolean disableIndexing) {
        return new TripleBetaConstraints(constraints, conf, disableIndexing);
    }

    @Override
    public QuadroupleBetaConstraints createQuadroupleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                                     final RuleBaseConfiguration conf,
                                                                     final boolean disableIndexing) {
        return new QuadroupleBetaConstraints(constraints, conf, disableIndexing);
    }

    @Override
    public DefaultBetaConstraints createDefaultBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                               final RuleBaseConfiguration conf,
                                                               final boolean disableIndexing) {
        return new DefaultBetaConstraints(constraints, conf, disableIndexing);
    }
}
