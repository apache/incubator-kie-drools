/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.models.drools.tuples;

import java.util.Objects;

import org.kie.pmml.commons.model.enums.OPERATOR;

/**
 * Tupla representing the operator and the value to be applied to a given field
 */
public class KiePMMLOperatorValue {

    public static final String VALUE_CONSTRAINT_PATTERN = "value %s %s";
    private final OPERATOR operator;
    private final Object value;
    private final String constraintsString;

    public KiePMMLOperatorValue(OPERATOR operator, Object value) {
        this.operator = operator;
        this.value = value;
        constraintsString = buildConstraintsString();
    }

    public OPERATOR getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }

    public String getConstraintsAsString() {
        return constraintsString;
    }

    @Override
    public String toString() {
        return "KiePMMLOperatorValue{" +
                "operator='" + operator + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KiePMMLOperatorValue that = (KiePMMLOperatorValue) o;
        return Objects.equals(operator, that.operator) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, value);
    }

    protected String buildConstraintsString() {
        return String.format(VALUE_CONSTRAINT_PATTERN, operator.getOperator(), value);
    }
}
