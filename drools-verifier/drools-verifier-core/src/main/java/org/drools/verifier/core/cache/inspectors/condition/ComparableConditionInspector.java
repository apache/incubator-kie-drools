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
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.relations.IsConflicting;
import org.drools.verifier.core.relations.Operator;

public class ComparableConditionInspector<T extends Comparable<T>>
        extends ConditionInspector<T>
        implements IsConflicting.Covers<T> {

    protected final Field field;
    protected final Operator operator;

    public ComparableConditionInspector(final FieldCondition condition,
                                        final AnalyzerConfiguration configuration) {
        super(condition,
              configuration);

        this.field = condition.getField();
        this.operator = Operator.resolve(condition.getOperator());
    }

    public Field getField() {
        return field;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public boolean conflicts(final Object other) {
        if (other instanceof ComparableConditionInspector) {
            if (!field.equals(((ComparableConditionInspector) other).field)) {
                return false;
            } else {
                switch (((ComparableConditionInspector) other).getOperator()) {
                    case NOT_EQUALS:
                        switch (operator) {
                            case NOT_EQUALS:
                                return false;
                        }
                    default:
                        final boolean overlaps = overlaps(other);
                        final boolean overlapsNOT = !overlaps;
                        return overlapsNOT;
                }
            }
        }
        return false;
    }

    @Override
    public boolean overlaps(final Object other) {
        if (other instanceof ComparableConditionInspector) {

            final ComparableConditionInspector<T> anotherPoint = (ComparableConditionInspector) other;

            if (!field.equals(anotherPoint.field)) {
                return false;
            } else {
                if (anotherPoint != null) {
                    switch (anotherPoint.getOperator()) {
                        case NOT_EQUALS:
                            switch (operator) {
                                case EQUALS:
                                    return !valueIsEqualTo(anotherPoint.getValue());
                                default:
                                    return true;
                            }
                        case EQUALS:
                            switch (operator) {
                                case NOT_EQUALS:
                                    return !valueIsEqualTo(anotherPoint.getValue());
                                default:
                                    return covers(anotherPoint.getValue());
                            }
                        case GREATER_OR_EQUAL:
                            switch (operator) {
                                case NOT_EQUALS:
                                    return true;
                                case LESS_THAN:
                                case BEFORE:
                                    return anotherPoint.covers(getValue())
                                            && covers(anotherPoint.getValue());
                                default:
                                    return covers(anotherPoint.getValue())
                                            || anotherPoint.covers(getValue());
                            }
                        case LESS_OR_EQUAL:
                            switch (operator) {
                                case NOT_EQUALS:
                                    return true;
                                case GREATER_THAN:
                                case AFTER:
                                    return anotherPoint.covers(getValue())
                                            && covers(anotherPoint.getValue());
                                default:
                                    return covers(anotherPoint.getValue())
                                            || anotherPoint.covers(getValue());
                            }
                        case LESS_THAN:
                        case BEFORE:
                            switch (operator) {
                                case EQUALS:
                                    return anotherPoint.covers(getValue());
                                case NOT_EQUALS:
                                    return true;
                                case LESS_THAN:
                                case BEFORE:
                                    return valueIsEqualTo(anotherPoint.getValue())
                                            || anotherPoint.covers(getValue())
                                            || covers(anotherPoint.getValue());
                                case GREATER_OR_EQUAL:
                                    return anotherPoint.covers(getValue())
                                            && covers(anotherPoint.getValue());
                                default:
                                    return covers(anotherPoint.getValue())
                                            || anotherPoint.covers(getValue());
                            }
                        case GREATER_THAN:
                        case AFTER:
                            switch (operator) {
                                case EQUALS:
                                    return anotherPoint.covers(getValue());
                                case NOT_EQUALS:
                                    return true;
                                case GREATER_THAN:
                                case AFTER:
                                    return valueIsEqualTo(anotherPoint.getValue())
                                            || anotherPoint.covers(getValue())
                                            || covers(anotherPoint.getValue());
                                case LESS_OR_EQUAL:
                                    return anotherPoint.covers(getValue())
                                            && covers(anotherPoint.getValue());
                                default:
                                    return covers(anotherPoint.getValue())
                                            || anotherPoint.covers(getValue());
                            }
                        case IN:
                            return anotherPoint.covers(getValue());
                        case NOT_IN:
                            boolean b = anotherPoint.covers(getValue());
                            return b;
                        default:
                            return false;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean subsumes(final Object other) {
        if (other instanceof ComparableConditionInspector) {

            final ComparableConditionInspector anotherPoint = (ComparableConditionInspector) other;

            if (!field.equals(anotherPoint.field)) {
                return false;
            } else {
                if (anotherPoint != null) {
                    switch (anotherPoint.getOperator()) {
                        case NOT_EQUALS:
                            switch (operator) {
                                case NOT_EQUALS:
                                    return valueIsEqualTo(anotherPoint.getValue());
                                case EQUALS:
                                    boolean valueIsEqualTo = valueIsEqualTo(anotherPoint.getValue());
                                    boolean covers = covers(anotherPoint.getValue());
                                    return !valueIsEqualTo && !covers;
                                default:
                                    return false;
                            }
                        case EQUALS:
                            switch (operator) {
                                case NOT_EQUALS:
                                    boolean valueIsEqualTo = valueIsEqualTo(anotherPoint.getValue());
                                    boolean covers = covers(anotherPoint.getValue());
                                    return !valueIsEqualTo && !covers;
                                default:
                                    return covers(anotherPoint.getValue());
                            }
                        case GREATER_OR_EQUAL:
                            switch (operator) {
                                case GREATER_OR_EQUAL:
                                case GREATER_THAN:
                                    return covers(anotherPoint.getValue());
                                case NOT_EQUALS:
                                    return valueIsGreaterThan(anotherPoint.getValue());
                                default:
                                    return false;
                            }

                        case LESS_OR_EQUAL:
                            switch (operator) {
                                case LESS_OR_EQUAL:
                                case LESS_THAN:
                                    return covers(anotherPoint.getValue());
                                case NOT_EQUALS:
                                    return valueIsLessThan(anotherPoint.getValue());
                                default:
                                    return false;
                            }
                        case LESS_THAN:
                            switch (operator) {
                                case LESS_OR_EQUAL:
                                    return covers(anotherPoint.getValue());
                                case LESS_THAN:
                                case NOT_EQUALS:
                                    return valueIsLessThanOrEqualTo(anotherPoint.getValue());
                                default:
                                    return false;
                            }
                        case GREATER_THAN:
                            switch (operator) {
                                case GREATER_OR_EQUAL:
                                    return covers(anotherPoint.getValue());
                                case GREATER_THAN:
                                case NOT_EQUALS:
                                    return valueIsGreaterThanOrEqualTo(anotherPoint.getValue());
                                default:
                                    return false;
                            }
                        default:
                            return false;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean covers(final Comparable<T> otherValue) {
        if (otherValue instanceof Comparable) {
            switch (operator) {
                case EQUALS:
                    return valueIsEqualTo(otherValue);
                case NOT_EQUALS:
                    return !valueIsEqualTo(otherValue);
                case GREATER_OR_EQUAL:
                    return valueIsGreaterThanOrEqualTo(otherValue);
                case LESS_OR_EQUAL:
                    return valueIsLessThanOrEqualTo(otherValue);
                case LESS_THAN:
                case BEFORE:
                    return valueIsLessThan(otherValue);
                case GREATER_THAN:
                case AFTER:
                    return valueIsGreaterThan(otherValue);
                default:
                    return false;
            }
        } else {
            return false;
        }
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
        stringBuilder.append(getValue());

        return stringBuilder.toString();
    }
}
