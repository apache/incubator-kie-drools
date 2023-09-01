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
package org.drools.verifier.core.cache.inspectors.condition;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.maps.InspectorFactory;

public class ConditionInspectorFactory
        extends InspectorFactory<ConditionInspector, Condition> {

    public ConditionInspectorFactory(final AnalyzerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public ConditionInspector make(final Condition condition) {

        if (condition instanceof FieldCondition) {
            return makeFieldCondition((FieldCondition) condition);
        } else {
            return null;
        }
    }

    private ConditionInspector makeFieldCondition(final FieldCondition condition) {
        if (!condition.getValues().isEmpty() && condition.getFirstValue() instanceof String) {
            return new StringConditionInspector(condition,
                                                configuration);
        } else if (!condition.getValues().isEmpty() && condition.getFirstValue() instanceof Boolean) {
            return new BooleanConditionInspector(condition,
                                                 configuration);
        } else if (!condition.getValues().isEmpty() && condition.getFirstValue() instanceof Integer) {
            return new NumericIntegerConditionInspector(condition,
                                                        configuration);
        } else {
            return new ComparableConditionInspector<>(condition,
                                                      configuration);
        }
    }
}
