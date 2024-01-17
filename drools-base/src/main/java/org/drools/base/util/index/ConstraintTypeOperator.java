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
package org.drools.base.util.index;

import org.drools.base.rule.IndexableConstraint;
import org.drools.base.rule.constraint.Constraint;
import org.kie.api.KieBaseConfiguration;

public enum ConstraintTypeOperator {
    EQUAL(true, "=="),
    NOT_EQUAL(false, "!="),
    GREATER_THAN(true, ">"),
    GREATER_OR_EQUAL(true, ">="),
    LESS_THAN(true, "<"),
    LESS_OR_EQUAL(true, "<="),
    RANGE(true, null),
    UNKNOWN(false, null);

    private final boolean indexable;
    private final String operator;

    ConstraintTypeOperator(boolean indexable, String operator) {
        this.indexable = indexable;
        this.operator = operator;
    }

    public boolean isComparison() {
        return isAscending() || isDescending();
    }

    public boolean isEquality() {
        return this == EQUAL || this == NOT_EQUAL;
    }

    public boolean isAscending() {
        return this == GREATER_THAN || this == GREATER_OR_EQUAL;
    }

    public boolean isDescending() {
        return this == LESS_THAN || this == LESS_OR_EQUAL;
    }

    public boolean isIndexable() {
        return indexable;
    }

    /**
     * May be null.
     *
     * @return the operator string representation if does exists, null otherwise.
     */
    public String getOperator() {
        return this.operator;
    }

    public boolean isIndexableForNode(int nodeType, IndexableConstraint constraint, KieBaseConfiguration config) {
        switch (this) {
            case EQUAL:
                return true;
            case NOT_EQUAL:
            case UNKNOWN:
                return false;
            default:
                return IndexUtil.canHaveRangeIndex(nodeType, constraint, config);
        }
    }

    public ConstraintTypeOperator negate() {
        switch (this) {
            case EQUAL:
                return NOT_EQUAL;
            case NOT_EQUAL:
                return EQUAL;
            case GREATER_THAN:
                return LESS_OR_EQUAL;
            case GREATER_OR_EQUAL:
                return LESS_THAN;
            case LESS_OR_EQUAL:
                return GREATER_THAN;
            case LESS_THAN:
                return GREATER_OR_EQUAL;
        }
        return UNKNOWN;
    }

    public boolean canInverse() {
        switch (this) {
            case EQUAL:
            case NOT_EQUAL:
            case GREATER_THAN:
            case GREATER_OR_EQUAL:
            case LESS_THAN:
            case LESS_OR_EQUAL:
                return true;
            default:
                return false;
        }
    }

    public ConstraintTypeOperator inverse() {
        switch (this) {
            case GREATER_THAN:
                return LESS_THAN;
            case GREATER_OR_EQUAL:
                return LESS_OR_EQUAL;
            case LESS_THAN:
                return GREATER_THAN;
            case LESS_OR_EQUAL:
                return GREATER_OR_EQUAL;
            default:
                return this;
        }
    }

    public static ConstraintTypeOperator decode(String operator) {
        return decode(operator, false);
    }

    public static ConstraintTypeOperator decode(String operator, boolean negated) {
        for (ConstraintTypeOperator c : ConstraintTypeOperator.values()) {
            if (c.getOperator() != null && c.getOperator().equals(operator)) {
                return negated ? c.negate() : c;
            }
        }
        return UNKNOWN;
    }

    public static ConstraintTypeOperator getType(Constraint constraint) {
        return constraint instanceof IndexableConstraint ic ? ic.getConstraintType() : UNKNOWN;
    }
}
