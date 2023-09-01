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
package org.drools.verifier.api.reporting;

import java.util.EnumSet;
import java.util.Set;

public enum CheckType {

    CONFLICTING_ROWS,
    DEFICIENT_ROW,
    IMPOSSIBLE_MATCH,
    MISSING_ACTION,
    MISSING_RESTRICTION,
    MULTIPLE_VALUES_FOR_ONE_ACTION,
    VALUE_FOR_FACT_FIELD_IS_SET_TWICE,
    VALUE_FOR_ACTION_IS_SET_TWICE,
    REDUNDANT_CONDITIONS_TITLE,
    REDUNDANT_ROWS,
    SUBSUMPTANT_ROWS,
    MISSING_RANGE,
    SINGLE_HIT_LOST,
    EMPTY_RULE,
    ILLEGAL_VERIFIER_STATE;

    public static Set<CheckType> getRowLevelCheckTypes() {
        return EnumSet.of(
                CONFLICTING_ROWS,
                DEFICIENT_ROW,
                REDUNDANT_ROWS,
                SUBSUMPTANT_ROWS,
                SINGLE_HIT_LOST);
    }
}
