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
package org.drools.verifier.core.relations;

public enum Operator {

    NONE(""),
    EQUALS("=="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_OR_EQUAL(">="),
    LESS_OR_EQUAL("<="),
    NOT_EQUALS("!="),

    IN("in"),
    NOT_IN("not in"),

    MATCHES("matches"),
    SOUNDSLIKE("soundslike"),

    AFTER("after"),
    BEFORE("before"),
    COINCIDES("coincides"),

    STR_STARTS_WITH("str[startsWith]"),
    STR_ENDS_WITH("str[endsWith]"),
    STR_LENGHT("str[length]"),
    NOT_MATCHES("not matches"),

    CONTAINS("contains"),
    NOT_CONTAINS("not contains"),

    DURING("during"),
    FINISHES("finishes"),
    FINISHED_BY("finishedby"),
    INCLUDES("includes"),
    MEETS("meets"),
    MET_BY("met by"),
    OVERLAPS("overlaps"),
    OVERLAPPED_BY("overlappedby"),
    STARTS("starts"),
    STARTED_BY("startedby"),
    CUSTOM("not sure what is in here"),
    MEMBER_OF("memberOf"),
    NOT_MEMBER_OF("not memberOf"),
    EXCLUDES("excludes");

    private final String operator;

    Operator(final String operator) {
        this.operator = operator;
    }

    public boolean isRangeOperator() {
        return this == GREATER_THAN || this == GREATER_OR_EQUAL || this == LESS_THAN || this == LESS_OR_EQUAL;
    }

    public static Operator resolve(final String operator) {
        if (operator.equals("== null")) {
            return EQUALS;
        } else if (operator.equals("!= null")) {
            return NOT_EQUALS;
        } else {
            for (Operator enumOperator : Operator.values()) {
                if (enumOperator.operator.equals(operator)) {
                    return enumOperator;
                }
            }
        }

        return NONE;
    }

    @Override
    public String toString() {
        return operator;
    }
}
