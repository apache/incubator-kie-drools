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

package org.drools.metric.common;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.QuadroupleBetaConstraints;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.MutableTypeConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Tuple;
import org.drools.metric.util.MetricLogUtils;
import org.kie.internal.conf.IndexPrecedenceOption;

public class QuadroupleBetaConstraintsMetric extends QuadroupleBetaConstraints {

    private static final long serialVersionUID = 510l;

    public QuadroupleBetaConstraintsMetric() {}

    public QuadroupleBetaConstraintsMetric(final BetaNodeFieldConstraint[] constraints,
                                           final RuleBaseConfiguration conf) {
        super(constraints, conf);
    }

    public QuadroupleBetaConstraintsMetric(final BetaNodeFieldConstraint[] constraints,
                                           final RuleBaseConfiguration conf,
                                           final boolean disableIndexing) {
        super(constraints, conf, disableIndexing);
    }

    protected QuadroupleBetaConstraintsMetric(BetaNodeFieldConstraint[] constraints,
                                              IndexPrecedenceOption indexPrecedenceOption,
                                              boolean disableIndexing) {
        super(constraints, indexPrecedenceOption, disableIndexing);
    }

    @Override
    public QuadroupleBetaConstraintsMetric cloneIfInUse() {
        if (constraints[0] instanceof MutableTypeConstraint && ((MutableTypeConstraint) constraints[0]).setInUse()) {
            BetaNodeFieldConstraint[] clonedConstraints = new BetaNodeFieldConstraint[constraints.length];
            for (int i = 0; i < constraints.length; i++) {
                clonedConstraints[i] = constraints[i].cloneIfInUse();
            }
            QuadroupleBetaConstraintsMetric clone = new QuadroupleBetaConstraintsMetric(clonedConstraints, indexPrecedenceOption, disableIndexing);
            clone.indexed = indexed;
            return clone;
        }
        return this;
    }

    @Override
    public boolean isAllowedCachedLeft(final ContextEntry[] context,
                                       final InternalFactHandle handle) {
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
