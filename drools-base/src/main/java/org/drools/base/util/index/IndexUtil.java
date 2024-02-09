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

import org.drools.base.base.ValueType;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.rule.IndexableConstraint;
import org.drools.base.rule.accessor.TupleValueExtractor;
import org.drools.base.rule.constraint.BetaConstraint;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.BetaRangeIndexOption;
import org.kie.internal.conf.IndexPrecedenceOption;

public class IndexUtil {

    // package private for test convenience
    static boolean USE_COMPARISON_INDEX = true;
    static boolean USE_COMPARISON_INDEX_JOIN = true;

    public static boolean compositeAllowed(BetaConstraint[] constraints, int betaNodeType, KieBaseConfiguration config) {
        // 1) If there is 1 or more unification restrictions it cannot be composite
        // 2) Ensures any non unification restrictions are first
        int firstUnification = -1;
        int firstNonUnification = -1;
        for ( int i = 0, length = constraints.length; i < length; i++ ) {
            if ( isIndexable(constraints[i], betaNodeType, config) ) {
                final boolean isUnification = ((IndexableConstraint) constraints[i]).isUnification();
                if ( isUnification && firstUnification == -1 ) {
                    firstUnification = i;
                } else if ( !isUnification &&firstNonUnification == -1 ) {
                    firstNonUnification = i;
                }
            }
            if ( firstUnification != -1 && firstNonUnification != -1) {
                break;
            }
        }

        if ( firstNonUnification > 0 ) {
            // Make sure a nonunification indexable constraint is first
            swap(constraints, 0, firstNonUnification);
        }

        return (firstUnification == -1);
    }

    public static boolean isIndexable(BetaConstraint constraint, int nodeType, KieBaseConfiguration config) {
        return constraint instanceof IndexableConstraint && ((IndexableConstraint)constraint).isIndexable(nodeType, config) && !isBigDecimalEqualityConstraint((IndexableConstraint)constraint);
    }

    public static boolean canHaveRangeIndex(int nodeType, IndexableConstraint constraint, KieBaseConfiguration config) {
        return canHaveRangeIndexForNodeType(nodeType, config) && areRangeIndexCompatibleOperands(constraint);
    }

    private static boolean canHaveRangeIndexForNodeType(int nodeType, KieBaseConfiguration config) {
        if (USE_COMPARISON_INDEX_JOIN && config.getOption(BetaRangeIndexOption.KEY).isBetaRangeIndexEnabled()) {
            boolean b = USE_COMPARISON_INDEX && (nodeType == NodeTypeEnums.NotNode || nodeType == NodeTypeEnums.ExistsNode || nodeType == NodeTypeEnums.JoinNode);
            return USE_COMPARISON_INDEX && (nodeType == NodeTypeEnums.NotNode || nodeType == NodeTypeEnums.ExistsNode || nodeType == NodeTypeEnums.JoinNode);
        } else {
            return USE_COMPARISON_INDEX && (nodeType == NodeTypeEnums.NotNode || nodeType == NodeTypeEnums.ExistsNode);
        }
    }

    private static boolean areRangeIndexCompatibleOperands(IndexableConstraint constraint) {
        TupleValueExtractor rightTupleExtractor;
        TupleValueExtractor leftTupleExtractor;
        try {
            rightTupleExtractor = constraint.getRightIndexExtractor();
            leftTupleExtractor = constraint.getLeftIndexExtractor();
        } catch (UnsupportedOperationException uoe) {
            return false;
        }
        if (rightTupleExtractor == null || leftTupleExtractor == null) {
            return false;
        }

        ValueType leftValueType = rightTupleExtractor.getValueType();
        ValueType rightValueType = leftTupleExtractor.getValueType();

        if (leftValueType != null && rightValueType != null) {
            if (leftValueType.isNumber() && rightValueType.isNumber()) {
                return true; // Number vs Number
            }
            Class<?> leftClass = leftValueType.getClassType();
            Class<?> rightClass = rightValueType.getClassType();
            return leftClass != null && rightClass != null && Comparable.class.isAssignableFrom(leftClass) && leftClass.equals(rightClass); // Same Comparable class
        }
        return false;
    }

    public static boolean isIndexableForNode(int nodeType, BetaConstraint constraint, KieBaseConfiguration config) {
        if ( !(constraint instanceof IndexableConstraint) ) {
            return false;
        }

        ConstraintTypeOperator constraintType = ((IndexableConstraint)constraint).getConstraintType();
        if (isBigDecimalEqualityConstraint((IndexableConstraint)constraint)) {
            return false;
        }
        return constraintType.isIndexableForNode(nodeType, (IndexableConstraint)constraint, config);
    }

    public static boolean isBigDecimalEqualityConstraint(IndexableConstraint indexableConstraint) {
        // Note : BigDecimal is not indexable for equality, because new BigDecimal("10").equals(new BigDecimal("10.00")) returns false
        if (indexableConstraint.getConstraintType() == ConstraintTypeOperator.EQUAL) {
            if (indexableConstraint.getFieldExtractor() != null) {
                return indexableConstraint.getFieldExtractor() != null && indexableConstraint.getFieldExtractor().getValueType() == ValueType.BIG_DECIMAL_TYPE;
            } else {
                return isBigDecimalEqualityConstraint(indexableConstraint.getLeftIndexExtractor()) || isBigDecimalEqualityConstraint(indexableConstraint.getRightIndexExtractor());
            }
        }
        return false;
    }

    private static boolean isBigDecimalEqualityConstraint(TupleValueExtractor tupleValueExtractor) {
        return tupleValueExtractor != null && tupleValueExtractor.getValueType()  == ValueType.BIG_DECIMAL_TYPE;
    }

    public static boolean[] isIndexableForNode(IndexPrecedenceOption indexPrecedenceOption, int nodeType, int keyDepth, BetaConstraint[] constraints, KieBaseConfiguration config) {
        if (keyDepth < 1) {
            return new boolean[constraints.length];
        }

        return indexPrecedenceOption == IndexPrecedenceOption.EQUALITY_PRIORITY ?
                findIndexableWithEqualityPriority(nodeType, keyDepth, constraints, config) :
                findIndexableWithPatternOrder(nodeType, keyDepth, constraints, config);
    }

    private static boolean[] findIndexableWithEqualityPriority(int nodeType, int keyDepth, BetaConstraint[] constraints, KieBaseConfiguration config) {
        boolean[] indexable = new boolean[constraints.length];
        if (hasEqualIndexable(keyDepth, indexable, constraints)) {
            return indexable;
        }

        if (!canHaveRangeIndexForNodeType(nodeType, config)) {
            return indexable;
        }

        for (int i = 0; i < constraints.length; i++) {
            if (isIndexable(constraints[i], nodeType, config)) {
                sortRangeIndexable(constraints, indexable, i);
                break;
            }
        }

        return indexable;
    }

    private static boolean[] findIndexableWithPatternOrder(int nodeType, int keyDepth, BetaConstraint[] constraints, KieBaseConfiguration config) {
        boolean[] indexable = new boolean[constraints.length];
        for (int i = 0; i < constraints.length; i++) {
            if (isIndexable(constraints[i], nodeType, config)) {
                if (isEqualIndexable(constraints[i])) {
                    sortEqualIndexable(keyDepth, indexable, constraints, i);
                } else {
                    sortRangeIndexable(constraints, indexable, i);
                }
                break;
            }
        }

        return indexable;
    }

    private static boolean hasEqualIndexable(int keyDepth, boolean[] indexable, BetaConstraint[] constraints) {
        return sortEqualIndexable(keyDepth, indexable, constraints, 0);
    }

    private static boolean sortEqualIndexable(int keyDepth, boolean[] indexable, BetaConstraint[] constraints, int start) {
        boolean hasEqualIndexable = false;
        int indexableCouter = 0;
        for (int i = start; i < constraints.length; i++) {
            if (isEqualIndexable(constraints[i])) {
                hasEqualIndexable = true;
                if (keyDepth > indexableCouter) {
                    swap(constraints, i, indexableCouter);
                    indexable[indexableCouter++] = true;
                }
            }
        }
        return hasEqualIndexable;
    }

    private static void sortRangeIndexable(BetaConstraint[] constraints, boolean[] indexable, int i) {
        swap(constraints, i, 0);
        indexable[0] = true;
    }

    public static boolean isEqualIndexable(BetaConstraint constraint) {
        return constraint instanceof IndexableConstraint && ((IndexableConstraint)constraint).getConstraintType() == ConstraintTypeOperator.EQUAL && !isBigDecimalEqualityConstraint((IndexableConstraint)constraint);
    }

    private static void swap(BetaConstraint[] constraints, int p1, int p2) {
        if (p1 != p2) {
            BetaConstraint temp = constraints[p2];
            constraints[p2] = constraints[p1];
            constraints[p1] = temp;
        }
    }

}
