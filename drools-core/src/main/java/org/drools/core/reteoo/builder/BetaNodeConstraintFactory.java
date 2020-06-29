/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public SingleBetaConstraints createSingleBetaConstraints(final BetaNodeFieldConstraint constraint,
                                                      final RuleBaseConfiguration conf,
                                                      final boolean disableIndex) {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new SingleBetaConstraintsMetric(constraint, conf, disableIndex);
        } else {
            return new SingleBetaConstraints(constraint, conf, disableIndex);
        }
    }

    public DoubleBetaConstraints createDoubleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                      final RuleBaseConfiguration conf,
                                                      final boolean disableIndexing) {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new DoubleBetaConstraintsMetric(constraints, conf, disableIndexing);
        } else {
            return new DoubleBetaConstraints(constraints, conf, disableIndexing);
        }
    }

    public TripleBetaConstraints createTripleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                      final RuleBaseConfiguration conf,
                                                      final boolean disableIndexing) {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new TripleBetaConstraintsMetric(constraints, conf, disableIndexing);
        } else {
            return new TripleBetaConstraints(constraints, conf, disableIndexing);
        }
    }

    public QuadroupleBetaConstraints createQuadroupleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                              final RuleBaseConfiguration conf,
                                                              final boolean disableIndexing) {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new QuadroupleBetaConstraintsMetric(constraints, conf, disableIndexing);
        } else {
            return new QuadroupleBetaConstraints(constraints, conf, disableIndexing);
        }
    }

    public DefaultBetaConstraints createDefaultBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                        final RuleBaseConfiguration conf,
                                                        final boolean disableIndexing) {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new DefaultBetaConstraintsMetric(constraints, conf, disableIndexing);
        } else {
            return new DefaultBetaConstraints(constraints, conf, disableIndexing);
        }
    }
}
