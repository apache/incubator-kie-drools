/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.checks.gaps;

import java.util.List;
import java.util.function.Consumer;

import org.drools.verifier.core.cache.inspectors.condition.ConditionInspector;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.relations.Operator;

import static org.drools.verifier.core.relations.Operator.resolve;

public class NumericRange extends Range<Double> implements Comparable<Range<Double>> {

    public NumericRange(final List<ConditionInspector> conditionInspectors) {
        super(conditionInspectors);
    }

    @Override
    protected Consumer<ConditionInspector> getConditionParser() {
        return c -> {
            final FieldCondition cond = (FieldCondition) c.getCondition();
            final Operator op = resolve(cond.getOperator());
            switch (op) {
                case LESS_OR_EQUAL:
                case LESS_THAN:
                    upperBound = ((Number) cond.getValues().iterator().next()).doubleValue();
                    break;
                case GREATER_THAN:
                case GREATER_OR_EQUAL:
                    lowerBound = ((Number) cond.getValues().iterator().next()).doubleValue();
                    break;
                case EQUALS:
                    lowerBound = ((Number) cond.getValues().iterator().next()).doubleValue();
                    upperBound = ((Number) cond.getValues().iterator().next()).doubleValue();
                    break;
            }
        };
    }

    @Override
    protected Double minValue() {
        return Double.MIN_VALUE;
    }

    @Override
    protected Double maxValue() {
        return Double.MAX_VALUE;
    }
}