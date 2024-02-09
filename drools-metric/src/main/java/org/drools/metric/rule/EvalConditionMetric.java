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
package org.drools.metric.rule;

import java.util.ArrayList;
import java.util.Collections;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.EvalCondition;
import org.drools.base.rule.accessor.EvalExpression;
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
    public boolean isAllowed(final BaseTuple tuple,
                             final ValueResolver valueResolver,
                             final Object context) {
        MetricLogUtils.getInstance().incrementEvalCount();
        return super.isAllowed(tuple, valueResolver, context);
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
