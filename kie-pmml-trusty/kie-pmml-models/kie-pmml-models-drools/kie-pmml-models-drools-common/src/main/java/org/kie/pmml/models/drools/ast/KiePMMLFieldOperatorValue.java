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
package org.kie.pmml.models.drools.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue;

/**
 * Class representing a <code>Pattern</code> with the referred field, its possible <code>KiePMMLOperatorValue</code>s and, eventually, nested <code>Pattern</code>s
 */
public class KiePMMLFieldOperatorValue {

    public static final String NO_FIELD_CONSTRAINT_PATTERN = "(%s)";
    public static final String FIELD_CONSTRAINT_PATTERN = " %s " + NO_FIELD_CONSTRAINT_PATTERN;
    private final String name;
    private final BOOLEAN_OPERATOR operator;
    private final List<KiePMMLOperatorValue> kiePMMLOperatorValues;
    private final List<KiePMMLFieldOperatorValue> nestedKiePMMLFieldOperatorValues;
    private final String constraintsString;

    /**
     * @param name The name of the type
     * @param operator the operator to use to join multiple <code>KiePMMLOperatorValue</code>s (if provided)
     * @param kiePMMLOperatorValues the inner <code>List&lt;KiePMMLOperatorValue&gt;</code>
     * @param nestedKiePMMLFieldOperatorValues the nested <code>List&lt;KiePMMLFieldOperatorValue&gt;</code>s
     */
    public KiePMMLFieldOperatorValue(final String name, final BOOLEAN_OPERATOR operator, final List<KiePMMLOperatorValue> kiePMMLOperatorValues, final List<KiePMMLFieldOperatorValue> nestedKiePMMLFieldOperatorValues) {
        this.name = name;
        this.operator = operator;
        this.kiePMMLOperatorValues = kiePMMLOperatorValues;
        this.nestedKiePMMLFieldOperatorValues = nestedKiePMMLFieldOperatorValues;
        constraintsString = buildConstraintsString();
    }

    public String getName() {
        return name;
    }

    public BOOLEAN_OPERATOR getOperator() {
        return operator;
    }

    public String getConstraintsAsString() {
        return constraintsString;
    }

    public List<KiePMMLFieldOperatorValue> getNestedKiePMMLFieldOperatorValues() {
        return nestedKiePMMLFieldOperatorValues != null ? Collections.unmodifiableList(nestedKiePMMLFieldOperatorValues) : null;
    }

    public List<KiePMMLOperatorValue> getKiePMMLOperatorValues() {
        return kiePMMLOperatorValues;
    }

    @Override
    public String toString() {
        return "KiePMMLFieldOperatorValue{" +
                "name='" + name + '\'' +
                ", operator='" + operator + '\'' +
                ", kiePMMLOperatorValues=" + kiePMMLOperatorValues +
                ", nestedKiePMMLFieldOperatorValues=" + nestedKiePMMLFieldOperatorValues +
                ", constraintsString='" + constraintsString + '\'' +
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
        String operatorString = operator != null ? operator.getCustomOperator() : "";
        return kiePMMLOperatorValues.stream().map(KiePMMLOperatorValue::getConstraintsAsString).collect(Collectors.joining(" " + operatorString + " "));
    }
}
