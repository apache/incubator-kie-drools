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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.relations.IsSubsuming;
import org.drools.verifier.core.relations.Operator;

public class StringConditionInspector
        extends ComparableConditionInspector<String> {

    private static final Set<Operator> LEGAL_OPERATORS = getLegalOperators();

    public StringConditionInspector(final FieldCondition<String> fieldInspector,
                                    final AnalyzerConfiguration configuration) {
        super(fieldInspector,
              configuration);
    }

    private static HashSet<Operator> getLegalOperators() {
        final HashSet<Operator> operators = new HashSet<>();
        operators.add(Operator.MATCHES);
        operators.add(Operator.NOT_MATCHES);
        operators.add(Operator.EQUALS);
        operators.add(Operator.NOT_EQUALS);
        operators.add(Operator.CONTAINS);
        operators.add(Operator.NOT_CONTAINS);
        operators.add(Operator.IN);
        operators.add(Operator.NOT_IN);
        operators.add(Operator.SOUNDSLIKE);
        return operators;
    }

    @Override
    public boolean isRedundant(Object other) {
        if (this.equals(other)) {
            return true;
        }
        if (other instanceof IsSubsuming) {
            boolean b = subsumes(other) && ((IsSubsuming) other).subsumes(this);
            return b;
        } else {
            return false;
        }
    }

    @Override
    public boolean conflicts(Object other) {
        if (this.equals(other)) {
            return false;
        }

        if (other instanceof StringConditionInspector) {

            if (!LEGAL_OPERATORS.contains(operator) || !LEGAL_OPERATORS.contains(((StringConditionInspector) other).operator)) {
                return false;
            }

            if (!hasValue() || !((StringConditionInspector) other).hasValue()) {
                return false;
            }

            if ((doesNotContainAll(((StringConditionInspector) other).getValues())
                    || ((StringConditionInspector) other).doesNotContainAll(getValues()))
                    &&
                    (eitherOperatorIs((StringConditionInspector) other,
                                      Operator.LESS_THAN)
                            || eitherOperatorIs((StringConditionInspector) other,
                                                Operator.LESS_OR_EQUAL)
                            || eitherOperatorIs((StringConditionInspector) other,
                                                Operator.GREATER_THAN)
                            || eitherOperatorIs((StringConditionInspector) other,
                                                Operator.GREATER_OR_EQUAL))) {
                return false;
            }

            if (operatorsAre(((StringConditionInspector) other),
                             Operator.NOT_EQUALS)) {
                return false;
            }
        }

        boolean conflicts = !overlaps(other);
        return conflicts;
    }

    private boolean eitherOperatorIs(StringConditionInspector other,
                                     Operator operator) {
        return other.getOperator()
                .equals(operator)
                || this.operator.equals(operator);
    }

    @Override
    public boolean overlaps(Object other) {
        if (other instanceof StringConditionInspector) {

            if (!LEGAL_OPERATORS.contains(operator) || !LEGAL_OPERATORS.contains(((StringConditionInspector) other).operator)) {
                return false;
            }

            final StringConditionInspector otherInspector = (StringConditionInspector) other;

            if (getValue() == null || getValue().isEmpty() || ((StringConditionInspector) other).getValue()
                    .isEmpty()) {
                return false;
            }

            if (operatorsAre(otherInspector,
                             Operator.LESS_THAN)
                    || (operatorsAre(otherInspector,
                                     Operator.GREATER_THAN))
                    || (operatorsAre(otherInspector,
                                     Operator.LESS_OR_EQUAL))
                    || (operatorsAre(otherInspector,
                                     Operator.GREATER_OR_EQUAL))
                    || operatorsAre(otherInspector,
                                    Operator.LESS_THAN,
                                    Operator.LESS_OR_EQUAL)
                    || operatorsAre(otherInspector,
                                    Operator.GREATER_THAN,
                                    Operator.GREATER_OR_EQUAL)) {
                return true;
            }

            if (getValue().equals(otherInspector.getValue())
                    && (operator.equals(otherInspector.getOperator()))) {
                return true;
            }

            if (((StringConditionInspector) other).getOperator()
                    .equals(Operator.LESS_THAN)
                    || operator.equals(Operator.LESS_THAN)
                    || ((StringConditionInspector) other).getOperator()
                    .equals(Operator.GREATER_THAN)
                    || operator.equals(Operator.GREATER_THAN)) {
                return false;
            }

            if (!otherInspector.hasValue()) {
                return false;
            } else {

                switch (operator) {
                    case MATCHES:
                    case SOUNDSLIKE:
                    case EQUALS:
                    case GREATER_OR_EQUAL:
                    case LESS_OR_EQUAL:
                        switch (otherInspector.getOperator()) {
                            case NOT_EQUALS:
                                return !otherInspector.containsAll(getValues());
                            case EQUALS:
                            case MATCHES:
                            case SOUNDSLIKE:
                                return otherInspector.containsAll(getValues());
                            case IN:
                                return otherInspector.valuesContains(getValue());
                            case NOT_IN:
                                return !otherInspector.valuesContains(getValue());
                            default:
                                return super.overlaps(other);
                        }
                    case NOT_IN:
                        switch (otherInspector.getOperator()) {
                            case EQUALS:
                            case MATCHES:
                            case SOUNDSLIKE:
                                return !valuesContains(otherInspector.getValue());
                            case IN:
                                return doesNotContainAll(otherInspector.getValues());
                            default:
                                return !otherInspector.containsAll(getValues());
                        }
                    case NOT_EQUALS:
                        switch (otherInspector.getOperator()) {
                            case IN:
                                return doesNotContainAll(((StringConditionInspector) other).getValues());
                            case NOT_EQUALS:
                                return !otherInspector.containsAll(getValues());
                            default:
                                return !otherInspector.valuesContains(getValue());
                        }
                    case IN:
                        switch (otherInspector.getOperator()) {
                            case EQUALS:
                            case MATCHES:
                            case SOUNDSLIKE:
                            case GREATER_OR_EQUAL:
                            case LESS_OR_EQUAL:
                                return valuesContains(otherInspector.getValue());
                            case NOT_EQUALS:
                                return otherInspector.doesNotContainAll(getValues());
                            case NOT_IN:
                                return doesNotContainAll(otherInspector.getValues());
                            case IN:
                                return containsAny(otherInspector.getValues());
                        }
                }
            }
        }

        return super.overlaps(other);
    }

    private boolean operatorsAre(final StringConditionInspector otherInspector,
                                 final Operator operator) {
        return this.operator.equals(operator) && otherInspector.getOperator()
                .equals(operator);
    }

    private boolean operatorsAre(final StringConditionInspector otherInspector,
                                 final Operator a,
                                 final Operator b) {
        return (this.operator.equals(a) && otherInspector.getOperator()
                .equals(b))
                || (this.operator.equals(b) && otherInspector.getOperator()
                .equals(a));
    }

    @Override
    public boolean covers(Comparable<String> otherValue) {

        switch (operator) {
            case STR_STARTS_WITH:
                return getValue() != null && getValue().startsWith(otherValue.toString());
            case STR_ENDS_WITH:
                return getValue() != null && getValue().endsWith(otherValue.toString());
            case MATCHES:
            case SOUNDSLIKE:
                return valueIsEqualTo(otherValue);
            case CONTAINS:
                return false;
            case NOT_MATCHES:
                return !valueIsEqualTo(otherValue);
            case IN:
                return valuesContains(otherValue.toString());
            case NOT_IN:
                return !valuesContains(otherValue.toString());
            default:
                return super.covers(otherValue);
        }
    }

    protected boolean valueIsGreaterThanOrEqualTo(final Comparable<String> otherValue) {
        return valueIsEqualTo(otherValue);
    }

    protected boolean valueIsLessThanOrEqualTo(final Comparable<String> otherValue) {
        return valueIsEqualTo(otherValue);
    }

    protected boolean valueIsGreaterThan(final Comparable<String> otherValue) {
        return false;
    }

    protected boolean valueIsLessThan(final Comparable<String> otherValue) {
        return false;
    }

    protected boolean valueIsEqualTo(final Comparable<String> otherValue) {
        return valuesContains(otherValue.toString());
    }

    private boolean valuesContains(final Object value) {
        return getValues().contains(value);
    }

    private boolean containsAll(final Values<Comparable> otherValues) {
        if (getValues().isEmpty() || otherValues.isEmpty()) {
            return false;
        } else {
            for (Object otherValue : otherValues) {
                if (!getValues().contains(otherValue)) {
                    return false;
                }
            }
            return true;
        }
    }

    private boolean doesNotContainAll(final Values<Comparable> otherValues) {
        for (final Object otherValue : otherValues) {
            if (!getValues().contains(otherValue)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAny(final Values<Comparable> otherValues) {
        for (final Object thisValue : getValues()) {
            for (final Object otherValue : otherValues) {
                if (thisValue.equals(otherValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean subsumes(Object other) {
        if (other instanceof StringConditionInspector) {

            if (((StringConditionInspector) other).getOperator()
                    .equals(operator)) {
                return getValues().containsAll(((StringConditionInspector) other).getValues());
            }

            switch (operator) {
                case EQUALS:
                case MATCHES:
                case SOUNDSLIKE:
                case LESS_OR_EQUAL:
                case GREATER_OR_EQUAL:

                    if (operatorsAre((StringConditionInspector) other,
                                     Operator.LESS_OR_EQUAL)
                            || operatorsAre((StringConditionInspector) other,
                                            Operator.GREATER_OR_EQUAL)
                            || operatorsAre((StringConditionInspector) other,
                                            Operator.LESS_OR_EQUAL,
                                            Operator.LESS_THAN)
                            || operatorsAre((StringConditionInspector) other,
                                            Operator.GREATER_OR_EQUAL,
                                            Operator.GREATER_THAN)) {
                        return Objects.equals(getValue(), ((StringConditionInspector) other).getValue());
                    } else {
                        switch (((StringConditionInspector) other).getOperator()) {
                            case IN:
                                return getValues().containsAll(((StringConditionInspector) other).getValues());
                            case EQUALS:
                            case MATCHES:
                            case SOUNDSLIKE:
                                return Objects.equals(getValue(), ((StringConditionInspector) other).getValue());
                        }
                    }
                    break;
                case IN:
                    switch (((StringConditionInspector) other).getOperator()) {
                        case EQUALS:
                        case MATCHES:
                        case SOUNDSLIKE:
                            return getValues().contains(((StringConditionInspector) other).getValue());
                    }

                    break;
                case NOT_IN:
                    switch (((StringConditionInspector) other).getOperator()) {
                        case IN:
                        case EQUALS:
                        case MATCHES:
                        case SOUNDSLIKE:
                            return !containsAll(((StringConditionInspector) other).getValues());
                        case NOT_EQUALS:
                            return getValues().contains(((StringConditionInspector) other).getValue());
                    }
                    break;
                case NOT_EQUALS:
                    switch (((StringConditionInspector) other).getOperator()) {
                        case NOT_IN:
                            return getValues().containsAll(((StringConditionInspector) other).getValues());
                        case IN:
                            return !((StringConditionInspector) other).getValues()
                                    .contains(getValue());
                        case EQUALS:
                        case MATCHES:
                        case SOUNDSLIKE:
                            return !getValue().equals(((StringConditionInspector) other).getValue());
                    }
                    break;
            }
        }

        return false;
    }

    @Override
    public boolean hasValue() {
        return getValues() != null && !getValues().isEmpty() && hasAValueSetInList();
    }

    @Override
    public String toHumanReadableString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(field.getName());
        stringBuilder.append(" ");
        stringBuilder.append(operator);
        stringBuilder.append(" ");
        final Iterator<Comparable> iterator = getValues().iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next());
            if (iterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }

    private boolean hasAValueSetInList() {
        for (final Object value : getValues()) {
            if (value != null && !((String) value).trim()
                    .isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
