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

import org.drools.base.rule.Declaration;
import org.drools.base.rule.EvalCondition;
import org.drools.base.rule.EvalConditionFactory;
import org.drools.metric.util.MetricLogUtils;

public class MetricEvalConditionFactoryImpl implements EvalConditionFactory {

    @Override
    public EvalCondition createEvalCondition(final Declaration[] requiredDeclarations) {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new EvalConditionMetric(requiredDeclarations);
        } else {
            return new EvalCondition(requiredDeclarations);
        }
    }
}
