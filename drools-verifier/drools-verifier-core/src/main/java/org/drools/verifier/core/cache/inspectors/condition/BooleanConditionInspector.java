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

public class BooleanConditionInspector
        extends ComparableConditionInspector {

    public BooleanConditionInspector(final FieldCondition<Boolean> fieldCondition,
                                     final AnalyzerConfiguration configuration) {
        super(fieldCondition,
              configuration);
    }

    @Override
    public boolean isRedundant(final Object other) {
        if (this.equals(other)) {
            return true;
        }
        if (other instanceof BooleanConditionInspector) {
            switch (operator) {
                case EQUALS:
                    switch (((BooleanConditionInspector) other).operator) {
                        case EQUALS:
                            return getValues().containsAll(((BooleanConditionInspector) other).getValues());
                        case NOT_EQUALS:
                            return !getValue().equals(((BooleanConditionInspector) other).getValue());
                        default:
                            return false;
                    }
                case NOT_EQUALS:
                    switch (((BooleanConditionInspector) other).operator) {
                        case EQUALS:
                            return !getValues().equals(((BooleanConditionInspector) other).getValues());
                        case NOT_EQUALS:
                            return getValues().containsAll(((BooleanConditionInspector) other).getValues());
                        default:
                            return false;
                    }
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean conflicts(final Object other) {
        return !isRedundant(other);
    }

    @Override
    public boolean overlaps(final Object other) {
        return isRedundant(other);
    }

    @Override
    public boolean subsumes(final Object other) {
        return isRedundant(other);
    }

    @Override
    public String toHumanReadableString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(field.getFactType());
        stringBuilder.append(".");
        stringBuilder.append(field.getName());
        stringBuilder.append(" ");
        stringBuilder.append(operator);
        stringBuilder.append(" ");
        stringBuilder.append(getValues());

        return stringBuilder.toString();
    }
}
