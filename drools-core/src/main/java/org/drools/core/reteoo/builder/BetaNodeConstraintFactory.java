package org.drools.core.reteoo.builder;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.DefaultBetaConstraints;
import org.drools.core.common.DoubleBetaConstraints;
import org.drools.core.common.QuadroupleBetaConstraints;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.common.TripleBetaConstraints;
import org.drools.base.rule.constraint.BetaNodeFieldConstraint;
import org.kie.api.internal.utils.KieService;

public interface BetaNodeConstraintFactory extends KieService {

    SingleBetaConstraints createSingleBetaConstraints(final BetaNodeFieldConstraint constraint,
                                                      final RuleBaseConfiguration conf,
                                                      final boolean disableIndex);

    DoubleBetaConstraints createDoubleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                      final RuleBaseConfiguration conf,
                                                      final boolean disableIndexing);

    TripleBetaConstraints createTripleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                      final RuleBaseConfiguration conf,
                                                      final boolean disableIndexing);

    QuadroupleBetaConstraints createQuadroupleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                              final RuleBaseConfiguration conf,
                                                              final boolean disableIndexing);

    DefaultBetaConstraints createDefaultBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                        final RuleBaseConfiguration conf,
                                                        final boolean disableIndexing);

    class Factory {

        private static class LazyHolder {

            private static final BetaNodeConstraintFactory INSTANCE = createInstance();

            private static BetaNodeConstraintFactory createInstance() {
                BetaNodeConstraintFactory factory = KieService.load(BetaNodeConstraintFactory.class);
                return factory != null ? factory : new BetaNodeConstraintFactoryImpl();
            }
        }

        public static BetaNodeConstraintFactory get() {
            return LazyHolder.INSTANCE;
        }

        private Factory() {}
    }
}
