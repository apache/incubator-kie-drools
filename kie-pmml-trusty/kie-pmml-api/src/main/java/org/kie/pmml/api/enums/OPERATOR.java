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
package org.kie.pmml.api.enums;

import java.util.Arrays;
import java.util.List;

import org.kie.pmml.api.exceptions.KieEnumException;

public enum OPERATOR implements Named {

    EQUAL("equal", "=="),
    NOT_EQUAL("notEqual", "!="),
    LESS_THAN("lessThan", "<"),
    LESS_OR_EQUAL("lessOrEqual", "<="),
    GREATER_THAN("greaterThan", ">"),
    GREATER_OR_EQUAL("greaterOrEqual", ">="),
    IS_MISSING("isMissing", ""),
    IS_NOT_MISSING("isNotMissing", "");

    /**
     * <code>OPERATOR</code>s that operates with <code>Number</code>s
     */
    static final List<OPERATOR> NUMBER_OPERATORS = Arrays.asList(EQUAL,
                                                                 NOT_EQUAL,
                                                                 LESS_THAN,
                                                                 LESS_OR_EQUAL,
                                                                 GREATER_THAN,
                                                                 GREATER_OR_EQUAL);

    /**
     * <code>OPERATOR</code>s that operates <b>ONLY</b >with <code>Number</code>s
     */
    static final List<OPERATOR> ONLY_NUMBER_OPERATORS = Arrays.asList(LESS_THAN,
                                                                 LESS_OR_EQUAL,
                                                                 GREATER_THAN,
                                                                 GREATER_OR_EQUAL);
    /**
     * <code>OPERATOR</code>s that operates with a <b>value</b>
     */
    static final List<OPERATOR> VALUE_OPERATORS = Arrays.asList(EQUAL,
                                                                NOT_EQUAL,
                                                                LESS_THAN,
                                                                LESS_OR_EQUAL,
                                                                GREATER_THAN,
                                                                GREATER_OR_EQUAL);
    private final String name;
    private final String operator;

    OPERATOR(String name, String operator) {
        this.name = name;
        this.operator = operator;
    }

    public static OPERATOR byName(String name) {
        return Arrays.stream(OPERATOR.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find OPERATOR with name: " + name));
    }

    /**
     * Returns <code>true</code> if the <code>OPERATOR</code> is applicable only for <code>NUMBER</code>s
     * @return
     */
    public boolean isOnlyNumberOperator() {
        return ONLY_NUMBER_OPERATORS.contains(this);
    }

    public boolean isValueOperator() {
        return VALUE_OPERATORS.contains(this);
    }

    public String getName() {
        return name;
    }

    public String getOperator() {
        return operator;
    }
}
