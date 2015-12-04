/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.util.index;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.kie.internal.conf.IndexPrecedenceOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.drools.core.util.ClassUtils.getter2property;

public class IndexUtil {

    private static final boolean USE_COMPARISON_INDEX = true;
    private static final boolean USE_RANGE_INDEX = USE_COMPARISON_INDEX && false;

    public static boolean compositeAllowed(BetaNodeFieldConstraint[] constraints, short betaNodeType) {
        // 1) If there is 1 or more unification restrictions it cannot be composite
        // 2) Ensures any non unification restrictions are first
        int firstUnification = -1;
        int firstNonUnification = -1;
        for ( int i = 0, length = constraints.length; i < length; i++ ) {
            if ( isIndexable(constraints[i], betaNodeType) ) {
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

        if (firstNonUnification != -1 && firstNonUnification > 0) {
            // Make sure a nonunification indexable constraint is first
            swap(constraints, 0, firstNonUnification);
        }

        return (firstUnification == -1);
    }

    public static boolean isIndexable(BetaNodeFieldConstraint constraint, short nodeType) {
        return constraint instanceof IndexableConstraint && ((IndexableConstraint)constraint).isIndexable(nodeType);
    }

    private static boolean canHaveRangeIndex(short nodeType) {
        return USE_COMPARISON_INDEX && ( nodeType == NodeTypeEnums.NotNode || nodeType == NodeTypeEnums.ExistsNode );
    }

    public static boolean isIndexableForNode(short nodeType, BetaNodeFieldConstraint constraint) {
        if ( !(constraint instanceof IndexableConstraint) ) {
            return false;
        }

        ConstraintType constraintType = ((IndexableConstraint)constraint).getConstraintType();
        return constraintType.isIndexableForNode(nodeType);
    }

    public static boolean[] isIndexableForNode(IndexPrecedenceOption indexPrecedenceOption, short nodeType, int keyDepth, BetaNodeFieldConstraint[] constraints) {
        if (keyDepth < 1) {
            return new boolean[constraints.length];
        }

        return indexPrecedenceOption == IndexPrecedenceOption.EQUALITY_PRIORITY ?
                findIndexableWithEqualityPriority(nodeType, keyDepth, constraints) :
                findIndexableWithPatternOrder(nodeType, keyDepth, constraints);
    }

    private static boolean[] findIndexableWithEqualityPriority(short nodeType, int keyDepth, BetaNodeFieldConstraint[] constraints) {
        boolean[] indexable = new boolean[constraints.length];
        if (hasEqualIndexable(keyDepth, indexable, constraints)) {
            return indexable;
        }

        if (!canHaveRangeIndex(nodeType)) {
            return indexable;
        }

        for (int i = 0; i < constraints.length; i++) {
            if (isIndexable(constraints[i], nodeType)) {
                sortRangeIndexable(constraints, indexable, i);
                break;
            }
        }

        return indexable;
    }

    private static boolean[] findIndexableWithPatternOrder(short nodeType, int keyDepth, BetaNodeFieldConstraint[] constraints) {
        boolean[] indexable = new boolean[constraints.length];
        for (int i = 0; i < constraints.length; i++) {
            if (isIndexable(constraints[i], nodeType)) {
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

    private static boolean hasEqualIndexable(int keyDepth, boolean[] indexable, BetaNodeFieldConstraint[] constraints) {
        return sortEqualIndexable(keyDepth, indexable, constraints, 0);
    }

    private static boolean sortEqualIndexable(int keyDepth, boolean[] indexable, BetaNodeFieldConstraint[] constraints, int start) {
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

    private static void sortRangeIndexable(BetaNodeFieldConstraint[] constraints, boolean[] indexable, int i) {
        int dualConstraintPosition = findDualConstraint(constraints, i);
        swap(constraints, i, 0);
        indexable[0] = true;
        if (dualConstraintPosition > 0) {
            swap(constraints, dualConstraintPosition, 1);
            indexable[1] = true;
        }
    }

    private static int findDualConstraint(BetaNodeFieldConstraint[] constraints, int comparisonPos) {
        if ( !(USE_RANGE_INDEX && constraints[comparisonPos] instanceof MvelConstraint) ) {
            return -1;
        }
        MvelConstraint firstConstraint = (MvelConstraint) constraints[comparisonPos];
        String leftValue = getLeftValueInExpression(firstConstraint.getExpression());
        for (int i = comparisonPos+1; i < constraints.length; i++) {
            if (constraints[i] instanceof MvelConstraint) {
                MvelConstraint dualConstraint = (MvelConstraint) constraints[i];
                if (isDual(firstConstraint, leftValue, dualConstraint)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static boolean isEqualIndexable(BetaNodeFieldConstraint constraint) {
        return constraint instanceof IndexableConstraint && ((IndexableConstraint)constraint).getConstraintType() == ConstraintType.EQUAL;
    }

    private static void swap(BetaNodeFieldConstraint[] constraints, int p1, int p2) {
        if (p1 != p2) {
            BetaNodeFieldConstraint temp = constraints[p2];
            constraints[p2] = constraints[p1];
            constraints[p1] = temp;
        }
    }

    public enum ConstraintType {
        EQUAL(true),
        NOT_EQUAL(false),
        GREATER_THAN(true),
        GREATER_OR_EQUAL(true),
        LESS_THAN(true),
        LESS_OR_EQUAL(true),
        RANGE(true),
        UNKNOWN(false);

        private final boolean indexable;

        private ConstraintType(boolean indexable) {
            this.indexable = indexable;
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

        public boolean isIndexableForNode(short nodeType) {
            switch (this) {
                case EQUAL:
                    return true;
                case NOT_EQUAL:
                case UNKNOWN:
                    return false;
                default:
                    return canHaveRangeIndex(nodeType);
            }
        }

        public static ConstraintType decode(String operator) {
            if (operator.equals("==")) {
                return EQUAL;
            }
            if (operator.equals("!=")) {
                return NOT_EQUAL;
            }
            if (operator.equals(">")) {
                return GREATER_THAN;
            }
            if (operator.equals(">=")) {
                return GREATER_OR_EQUAL;
            }
            if (operator.equals("<")) {
                return LESS_THAN;
            }
            if (operator.equals("<=")) {
                return LESS_OR_EQUAL;
            }
            return UNKNOWN;
        }

        public static ConstraintType getType(Constraint constraint) {
            return constraint instanceof IndexableConstraint ? ((IndexableConstraint)constraint).getConstraintType() : UNKNOWN;
        }
    }

    public static List<String> getIndexedProperties(BetaNode betaNode, RuleBaseConfiguration config) {
        int keyDepth = config.getCompositeKeyDepth();
        if (config.getCompositeKeyDepth() < 1) {
            return Collections.emptyList();
        }

        Factory.IndexSpec indexSpec = new Factory.IndexSpec(config.getIndexPrecedenceOption(), keyDepth, betaNode.getType(), betaNode.getConstraints());
        List<String> indexedProps = new ArrayList<String>();
        for (FieldIndex fieldIndex : indexSpec.indexes) {
            indexedProps.add( getter2property(fieldIndex.getExtractor().getNativeReadMethodName()) );
        }

        return indexedProps;
    }

    public static class Factory {
        public static BetaMemory createBetaMemory(RuleBaseConfiguration config, short nodeType, BetaNodeFieldConstraint... constraints) {
            int keyDepth = config.getCompositeKeyDepth();
            if (config.getCompositeKeyDepth() < 1) {
                return new BetaMemory( config.isSequential() ? null : new TupleList(),
                                       new TupleList(),
                                       createContext(constraints),
                                       nodeType );
            }

            IndexSpec indexSpec = new IndexSpec(config.getIndexPrecedenceOption(), keyDepth, nodeType, constraints);
            return new BetaMemory( createLeftMemory(config, indexSpec),
                                   createRightMemory(config, indexSpec),
                                   createContext(constraints),
                                   nodeType );
        }

        private static TupleMemory createRightMemory(RuleBaseConfiguration config, IndexSpec indexSpec) {
            if ( !config.isIndexRightBetaMemory() || !indexSpec.constraintType.isIndexable() ) {
                return new TupleList();
            }

            if (indexSpec.constraintType == ConstraintType.EQUAL) {
                return new TupleIndexHashTable( indexSpec.indexes, false );
            }

            if (indexSpec.constraintType.isComparison()) {
                return new TupleIndexRBTree( indexSpec.constraintType, indexSpec.indexes[0], false );
            }

            if (indexSpec.constraintType == ConstraintType.RANGE) {
                // missing TreeMap based implementation for range indexes
                return new RightTupleIndexRangeRBTree( indexSpec.ascendingConstraintType, indexSpec.indexes[0],
                                                       indexSpec.descendingConstraintType, indexSpec.indexes[1] );
            }

            return new TupleList();
        }

        private static TupleMemory createLeftMemory(RuleBaseConfiguration config, IndexSpec indexSpec) {
            if (config.isSequential()) {
                return null;
            }
            if ( !config.isIndexLeftBetaMemory() || !indexSpec.constraintType.isIndexable() ) {
                return new TupleList();
            }

            if (indexSpec.constraintType == ConstraintType.EQUAL) {
                return new TupleIndexHashTable( indexSpec.indexes, true );
            }

            if (indexSpec.constraintType.isComparison()) {
                return new TupleIndexRBTree( indexSpec.constraintType, indexSpec.indexes[0], true );
            }

            if (indexSpec.constraintType == ConstraintType.RANGE) {
                // missing TreeMap based implementation for range indexes
                return new LeftTupleIndexRangeRBTree( indexSpec.ascendingConstraintType, indexSpec.indexes[0],
                                                      indexSpec.descendingConstraintType, indexSpec.indexes[1] );
            }

            return new TupleList();
        }

        public static ContextEntry[] createContext(BetaNodeFieldConstraint... constraints) {
            ContextEntry[] entries = new ContextEntry[constraints.length];
            for (int i = 0; i < constraints.length; i++) {
                entries[i] = constraints[i].createContextEntry();
            }
            return entries;
        }

        private static class IndexSpec {
            private ConstraintType constraintType = ConstraintType.UNKNOWN;
            private FieldIndex[] indexes;

            private ConstraintType ascendingConstraintType = null;
            private ConstraintType descendingConstraintType = null;

            private IndexSpec(IndexPrecedenceOption indexPrecedenceOption, int keyDepth, short nodeType, BetaNodeFieldConstraint[] constraints) {
                init(indexPrecedenceOption, keyDepth, nodeType, constraints);
            }

            private void init(IndexPrecedenceOption indexPrecedenceOption, int keyDepth, short nodeType, BetaNodeFieldConstraint[] constraints) {
                int firstIndexableConstraint = indexPrecedenceOption == IndexPrecedenceOption.EQUALITY_PRIORITY ?
                        determineTypeWithEqualityPriority(nodeType, constraints) :
                        determineTypeWithPatternOrder(nodeType, constraints);

                if (constraintType == ConstraintType.EQUAL) {
                    List<FieldIndex> indexList = new ArrayList<FieldIndex>();
                    indexList.add(((IndexableConstraint)constraints[firstIndexableConstraint]).getFieldIndex());

                    // look for other EQUAL constraint to eventually add them to the index
                    for (int i = firstIndexableConstraint+1; i < constraints.length && indexList.size() < keyDepth; i++) {
                        if ( ConstraintType.getType(constraints[i]) == ConstraintType.EQUAL && ! ((IndexableConstraint) constraints[i]).isUnification() ) {
                            indexList.add(((IndexableConstraint)constraints[i]).getFieldIndex());
                        }
                    }
                    indexes = indexList.toArray(new FieldIndex[indexList.size()]);

                } else if (constraintType.isComparison()) {
                    // look for a dual constraint to create a range index
                    if (USE_RANGE_INDEX && constraints[firstIndexableConstraint] instanceof MvelConstraint) {
                        MvelConstraint firstConstraint = (MvelConstraint) constraints[firstIndexableConstraint];
                        String leftValue = getLeftValueInExpression(firstConstraint.getExpression());
                        for (int i = firstIndexableConstraint+1; i < constraints.length; i++) {
                            if (constraints[i] instanceof MvelConstraint) {
                                MvelConstraint dualConstraint = (MvelConstraint) constraints[i];
                                if (isDual(firstConstraint, leftValue, dualConstraint)) {
                                    constraintType = ConstraintType.RANGE;
                                    if (firstConstraint.getConstraintType().isAscending()) {
                                        ascendingConstraintType = firstConstraint.getConstraintType();
                                        descendingConstraintType = dualConstraint.getConstraintType();
                                        indexes = new FieldIndex[]{ firstConstraint.getFieldIndex(), dualConstraint.getFieldIndex() };
                                    } else {
                                        ascendingConstraintType = dualConstraint.getConstraintType();
                                        descendingConstraintType = firstConstraint.getConstraintType();
                                        indexes = new FieldIndex[]{ dualConstraint.getFieldIndex(), firstConstraint.getFieldIndex() };
                                    }
                                    return;
                                }
                            }
                        }
                    }

                    indexes = new FieldIndex[]{ ((IndexableConstraint)constraints[firstIndexableConstraint]).getFieldIndex() };
                }
            }

            private int determineTypeWithEqualityPriority(short nodeType, BetaNodeFieldConstraint[] constraints) {
                int indexedConstraintPos = 0;
                for (int i = 0; i < constraints.length; i++) {
                    if (constraints[i] instanceof IndexableConstraint) {
                        IndexableConstraint indexableConstraint = (IndexableConstraint) constraints[i];
                        ConstraintType type = indexableConstraint.getConstraintType();
                        if (type == ConstraintType.EQUAL) {
                            constraintType = type;
                            return i;
                        } else if (constraintType == ConstraintType.UNKNOWN && type.isIndexableForNode(nodeType)) {
                            constraintType = type;
                            indexedConstraintPos = i;
                        }
                    }
                }
                return indexedConstraintPos;
            }

            private int determineTypeWithPatternOrder(short nodeType, BetaNodeFieldConstraint[] constraints) {
                for (int i = 0; i < constraints.length; i++) {
                    ConstraintType type = ConstraintType.getType(constraints[i]);
                    if ( type.isIndexableForNode(nodeType) ) {
                        constraintType = type;
                        return i;
                    }
                }
                return constraints.length;
            }

        }
    }

    private static boolean isDual(MvelConstraint firstConstraint, String leftValue, MvelConstraint dualConstraint) {
        return dualConstraint.getConstraintType().isComparison() &&
                dualConstraint.getConstraintType().isAscending() != firstConstraint.getConstraintType().isAscending() &&
                leftValue.equals( getLeftValueInExpression(dualConstraint.getExpression()) );
    }

    private static String getLeftValueInExpression(String expression) {
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if ( !Character.isJavaIdentifierPart(ch) && ch != '.' ) {
                return expression.substring(0, i);
            }
        }
        return expression;
    }
}
