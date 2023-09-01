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
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.relations.Operator;

public class NumericIntegerConditionInspector
        extends ComparableConditionInspector<Integer> {

    public NumericIntegerConditionInspector(final FieldCondition<Integer> fieldCondition,
                                            final AnalyzerConfiguration configuration) {
        super(fieldCondition,
              configuration);
    }

    @Override
    public boolean subsumes(Object other) {
        if (other instanceof NumericIntegerConditionInspector) {
            final NumericIntegerConditionInspector anotherPoint = (NumericIntegerConditionInspector) other;
            if (anotherPoint != null && anotherPoint.getValue() != null) {
                if ((anotherPoint.getOperator().equals(Operator.LESS_THAN) && operator.equals(Operator.LESS_OR_EQUAL))) {
                    return covers(anotherPoint.getValue() - 1);
                } else if ((anotherPoint.getOperator().equals(Operator.GREATER_OR_EQUAL) && operator.equals(Operator.GREATER_THAN))) {
                    if (getValue().equals(anotherPoint.getValue() - 1)) {
                        return true;
                    }
                } else if ((anotherPoint.getOperator().equals(Operator.GREATER_THAN) && operator.equals(Operator.GREATER_OR_EQUAL))) {
                    return covers(anotherPoint.getValue() + 1);
                } else if ((anotherPoint.getOperator().equals(Operator.LESS_OR_EQUAL) && operator.equals(Operator.LESS_THAN))) {
                    if (getValue().equals(anotherPoint.getValue() + 1)) {
                        return true;
                    }
                }
            }
        }

        return super.subsumes(other);
    }
}
