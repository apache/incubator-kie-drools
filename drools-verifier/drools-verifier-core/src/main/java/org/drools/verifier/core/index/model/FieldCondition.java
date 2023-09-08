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
package org.drools.verifier.core.index.model;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.util.PortablePreconditions;

public class FieldCondition<T extends Comparable>
        extends Condition {

    private final Field field;
    private String operator;

    public FieldCondition(final Field field,
                          final Column column,
                          final String operator,
                          final Values<T> values,
                          final AnalyzerConfiguration configuration) {
        super(column,
              ConditionSuperType.FIELD_CONDITION,
              values,
              configuration);

        this.field = PortablePreconditions.checkNotNull("field",
                                                        field);
        this.operator = PortablePreconditions.checkNotNull("operator",
                                                           operator);
    }

    public Field getField() {
        return field;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(final String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return field + " " + operator + " " + getValues();
    }

    @Override
    public Key[] keys() {
        return super.keys();
    }

    public static KeyDefinition[] keyDefinitions() {
        return Condition.keyDefinitions();
    }
}
