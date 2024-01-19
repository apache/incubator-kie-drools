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
package org.drools.core.util.index;

import org.drools.base.rule.IndexableConstraint;
import org.drools.base.rule.constraint.BetaConstraint;
import org.drools.base.util.IndexedValueReader;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.util.AbstractHashTable.DoubleCompositeIndex;
import org.drools.core.util.AbstractHashTable.Index;
import org.drools.core.util.AbstractHashTable.SingleIndex;
import org.drools.core.util.AbstractHashTable.TripleCompositeIndex;
import org.kie.internal.conf.IndexPrecedenceOption;
import java.util.ArrayList;
import java.util.List;

import static org.drools.base.util.index.IndexUtil.isEqualIndexable;

public class IndexSpec {
    private ConstraintTypeOperator constraintType = ConstraintTypeOperator.UNKNOWN;
    private IndexedValueReader[]   indexes;

    public IndexSpec(int nodeType, BetaConstraint[] constraints, RuleBaseConfiguration config) {
        init(nodeType, constraints, config);
    }

    public IndexSpec(IndexedValueReader[] indexes, ConstraintTypeOperator constraintType) {
        this.indexes = indexes;
        this.constraintType = constraintType;
    }

    public IndexSpec(IndexedValueReader[] indexes) {
        this.indexes = indexes;
        this.constraintType = ConstraintTypeOperator.EQUAL;
    }

    public ConstraintTypeOperator getConstraintType() {
        return constraintType;
    }

    public IndexedValueReader[] getIndexes() {
        return indexes;
    }

    public Index getIndex() {
        Index index;
        int PRIME   = 31;
        int startResult = PRIME;
        int i = 1;
        for ( IndexedValueReader j : indexes ) {
            startResult += PRIME * startResult + i;
            i++;
        }

        switch ( indexes.length ) {
            case 0 :
                throw new IllegalArgumentException( "FieldIndexHashTable cannot use an index[] of length  0" );
            case 1 :
                index = new SingleIndex(indexes,
                                        startResult );
                break;
            case 2 :
                index = new DoubleCompositeIndex(indexes,
                                                 startResult );
                break;
            case 3 :
                index = new TripleCompositeIndex(indexes,
                                                 startResult );
                break;
            default :
                throw new IllegalArgumentException( "FieldIndexHashTable cannot use an index[] of length  great than 3" );
        }

        return index;
    }

    public IndexedValueReader getIndex(int pos) {
        return indexes[pos];
    }

    public void init(int nodeType, BetaConstraint[] constraints, RuleBaseConfiguration config) {
        int keyDepth = config.getCompositeKeyDepth();
        IndexPrecedenceOption indexPrecedenceOption = config.getIndexPrecedenceOption();
        int firstIndexableConstraint = indexPrecedenceOption == IndexPrecedenceOption.EQUALITY_PRIORITY ?
                determineTypeWithEqualityPriority(nodeType, constraints, config) :
                determineTypeWithPatternOrder(nodeType, constraints, config);

        if (constraintType == ConstraintTypeOperator.EQUAL) {
            List<IndexedValueReader> indexList = new ArrayList<>();
            if (isEqualIndexable(constraints[firstIndexableConstraint])) {
                indexList.add(((IndexableConstraint) constraints[firstIndexableConstraint]).getFieldIndex());
            }

            // look for other EQUAL constraint to eventually add them to the index
            for (int i = firstIndexableConstraint+1; i < constraints.length && indexList.size() < keyDepth; i++) {
                if ( isEqualIndexable(constraints[i]) && ! ((IndexableConstraint) constraints[i]).isUnification() ) {
                    indexList.add(((IndexableConstraint)constraints[i]).getFieldIndex());
                }
            }
            indexes = indexList.toArray(new IndexedValueReader[indexList.size()]);

        } else if (constraintType.isComparison()) {
            // look for a dual constraint to create a range index
            indexes = new IndexedValueReader[]{((IndexableConstraint)constraints[firstIndexableConstraint]).getFieldIndex() };
        }
    }

    public int determineTypeWithEqualityPriority(int nodeType, BetaConstraint[] constraints, RuleBaseConfiguration config) {
        int indexedConstraintPos = 0;
        for (int i = 0; i < constraints.length; i++) {
            if (constraints[i] instanceof IndexableConstraint) {
                IndexableConstraint indexableConstraint = (IndexableConstraint) constraints[i];
                ConstraintTypeOperator type = indexableConstraint.getConstraintType();
                if (type == ConstraintTypeOperator.EQUAL) {
                    constraintType = type;
                    return i;
                } else if (constraintType == ConstraintTypeOperator.UNKNOWN && type.isIndexableForNode(nodeType, indexableConstraint, config)) {
                    constraintType = type;
                    indexedConstraintPos = i;
                }
            }
        }
        return indexedConstraintPos;
    }

    public int determineTypeWithPatternOrder(int nodeType, BetaConstraint[] constraints, RuleBaseConfiguration config) {
        for (int i = 0; i < constraints.length; i++) {
            ConstraintTypeOperator type = ConstraintTypeOperator.getType(constraints[i]);
            if ( type.isIndexableForNode(nodeType, (IndexableConstraint) constraints[i], config) ) {
                constraintType = type;
                return i;
            }
        }
        return constraints.length;
    }
}