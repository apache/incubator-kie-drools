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

public class IntegerRange
        extends Range<Integer>
        implements Comparable<Range<Integer>> {

    public IntegerRange(final List<ConditionInspector> conditionInspectors) {
        super(conditionInspectors);
    }

    @Override
    protected Consumer<ConditionInspector> getConditionParser() {
        return c -> {
            final FieldCondition cond = (FieldCondition) c.getCondition();
            final Operator op = resolve(cond.getOperator());
            switch (op) {
                case LESS_OR_EQUAL:
                    if (cond.getValues().iterator().next().equals(Integer.MAX_VALUE)) {
                        upperBound = Integer.MAX_VALUE;
                    } else {
                        upperBound = (Integer) cond.getValues().iterator().next() + 1;
                    }
                    break;
                case LESS_THAN:
                    upperBound = (Integer) cond.getValues().iterator().next();
                    break;
                case GREATER_OR_EQUAL:
                    if (cond.getValues().iterator().next().equals(Integer.MIN_VALUE)) {
                        lowerBound = Integer.MIN_VALUE;
                    } else {
                        lowerBound = (Integer) cond.getValues().iterator().next() - 1;
                    }
                    break;
                case GREATER_THAN:
                    lowerBound = (Integer) cond.getValues().iterator().next();
                    break;
                case EQUALS:
                    lowerBound = (Integer) cond.getValues().iterator().next();
                    upperBound = (Integer) cond.getValues().iterator().next();
                    break;
            }
        };
    }

    @Override
    protected Integer minValue() {
        return Integer.MIN_VALUE;
    }

    @Override
    protected Integer maxValue() {
        return Integer.MAX_VALUE;
    }
}