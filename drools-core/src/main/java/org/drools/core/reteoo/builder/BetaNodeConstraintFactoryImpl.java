/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.reteoo.builder;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.DefaultBetaConstraints;
import org.drools.core.common.DoubleBetaConstraints;
import org.drools.core.common.QuadroupleBetaConstraints;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.common.TripleBetaConstraints;
import org.drools.base.rule.constraint.BetaConstraint;

public class BetaNodeConstraintFactoryImpl implements BetaNodeConstraintFactory {

    @Override
    public SingleBetaConstraints createSingleBetaConstraints(final BetaConstraint constraint,
                                                             final RuleBaseConfiguration conf,
                                                             final boolean disableIndex) {
        return new SingleBetaConstraints(constraint, conf, disableIndex);
    }

    @Override
    public DoubleBetaConstraints createDoubleBetaConstraints(final BetaConstraint[] constraints,
                                                             final RuleBaseConfiguration conf,
                                                             final boolean disableIndexing) {
        return new DoubleBetaConstraints(constraints, conf, disableIndexing);
    }

    @Override
    public TripleBetaConstraints createTripleBetaConstraints(final BetaConstraint[] constraints,
                                                             final RuleBaseConfiguration conf,
                                                             final boolean disableIndexing) {
        return new TripleBetaConstraints(constraints, conf, disableIndexing);
    }

    @Override
    public QuadroupleBetaConstraints createQuadroupleBetaConstraints(final BetaConstraint[] constraints,
                                                                     final RuleBaseConfiguration conf,
                                                                     final boolean disableIndexing) {
        return new QuadroupleBetaConstraints(constraints, conf, disableIndexing);
    }

    @Override
    public DefaultBetaConstraints createDefaultBetaConstraints(final BetaConstraint[] constraints,
                                                               final RuleBaseConfiguration conf,
                                                               final boolean disableIndexing) {
        return new DefaultBetaConstraints(constraints, conf, disableIndexing);
    }
}
