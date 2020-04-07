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
package org.kie.pmml.models.drooled.tuples;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Tupla representing the name of a field and its <code>KiePMMLOperatorValue</code>
 */
public class KiePMMLFieldOperatorValue {

    public static final String NO_FIELD_CONSTRAINT_PATTERN = "(%s)";
    public static final String FIELD_CONSTRAINT_PATTERN = " %s " + NO_FIELD_CONSTRAINT_PATTERN;
    private final String name;
    private final String operator;
    private final List<KiePMMLOperatorValue> kiePMMLOperatorValues;
    private final List<KiePMMLFieldOperatorValue> nestedKiePMMLFieldOperatorValues;
    private final String constraintsString;
    private final String toString;

    /**
     * @param name The name of the type
     * @param operator the operator to use to join multiple <code>KiePMMLOperatorValue</code>s (if provided)
     * @param operatorValues the <code>LinkedHashMap&lt;String, Object&gt;</code> used to build the inner <code>List&lt;KiePMMLOperatorValue&lt;</code>
     */
    public KiePMMLFieldOperatorValue(final String name, final String operator, final Map<String, Object> operatorValues, final List<KiePMMLFieldOperatorValue> nestedKiePMMLFieldOperatorValues) {
        this.name = name;
        this.operator = operator != null ? operator : "";
        kiePMMLOperatorValues = operatorValues.entrySet().stream().map(entry -> new KiePMMLOperatorValue(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        this.nestedKiePMMLFieldOperatorValues = nestedKiePMMLFieldOperatorValues;
        constraintsString = buildConstraintsString();
        toString = buildToString();
    }

    public String getName() {
        return name;
    }

    public String getOperator() {
        return operator;
    }

    public String getConstraintsAsString() {
        return constraintsString;
    }

    public List<KiePMMLFieldOperatorValue> getNestedKiePMMLFieldOperatorValues() {
        return nestedKiePMMLFieldOperatorValues != null ? Collections.unmodifiableList(nestedKiePMMLFieldOperatorValues) : null;
    }

    @Override
    public String toString() {
        return toString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KiePMMLFieldOperatorValue that = (KiePMMLFieldOperatorValue) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(operator, that.operator) &&
                Objects.equals(kiePMMLOperatorValues, that.kiePMMLOperatorValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, operator, kiePMMLOperatorValues);
    }

    protected String buildConstraintsString() {
        return kiePMMLOperatorValues.stream().map(KiePMMLOperatorValue::toString).collect(Collectors.joining(" " + operator + " "));
    }

    protected String buildToString() {
        if (name != null) {
            return String.format(FIELD_CONSTRAINT_PATTERN, name, constraintsString);
        } else {
            return String.format(NO_FIELD_CONSTRAINT_PATTERN, constraintsString);
        }
    }
}
