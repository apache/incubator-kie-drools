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
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.MutableTypeConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Tuple;
import org.drools.metric.util.MetricLogUtils;

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
