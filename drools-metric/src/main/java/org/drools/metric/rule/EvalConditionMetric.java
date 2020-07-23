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

package org.drools.metric.rule;

import java.util.ArrayList;
import java.util.Collections;

import org.drools.core.WorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EvalCondition;
import org.drools.core.spi.EvalExpression;
import org.drools.core.spi.Tuple;
import org.drools.metric.util.MetricLogUtils;

public class EvalConditionMetric extends EvalCondition {

    public EvalConditionMetric() {}

    public EvalConditionMetric(final Declaration[] requiredDeclarations) {
        super(requiredDeclarations);
    }

    public EvalConditionMetric(final EvalExpression eval,
                               final Declaration[] requiredDeclarations) {

        super(eval, requiredDeclarations);
    }

    @Override
    public boolean isAllowed(final Tuple tuple,
                             final WorkingMemory workingMemory,
                             final Object context) {
        MetricLogUtils.getInstance().incrementEvalCount();
        return super.isAllowed(tuple, workingMemory, context);
    }

    @Override
    public EvalCondition clone() {
        // cannot rely on super.clone() because it enlists an EvalCondition instance to "cloned"
        final EvalConditionMetric clone = new EvalConditionMetric(this.expression.clone(),
                                                                  this.requiredDeclarations.clone());

        if (this.getCloned() == Collections.<EvalCondition> emptyList()) {
            this.setCloned(new ArrayList<>(1));
        }

        this.getCloned().add(clone);

        return clone;
    }
}
