package org.drools.core.util.index;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.rule.constraint.BetaNodeFieldConstraint;
import org.drools.core.util.FieldIndex;
import org.kie.internal.conf.IndexPrecedenceOption;

import static org.drools.core.util.index.IndexUtil.isEqualIndexable;

public class IndexSpec {
    private ConstraintTypeOperator constraintType = ConstraintTypeOperator.UNKNOWN;
    private FieldIndex[] indexes;

    IndexSpec(short nodeType, BetaNodeFieldConstraint[] constraints, RuleBaseConfiguration config) {
        init(nodeType, constraints, config);
    }

    public ConstraintTypeOperator getConstraintType() {
        return constraintType;
    }

    public FieldIndex[] getIndexes() {
        return indexes;
    }

    public FieldIndex getIndex(int pos) {
        return indexes[pos];
    }

    private void init(short nodeType, BetaNodeFieldConstraint[] constraints, RuleBaseConfiguration config) {
        int keyDepth = config.getCompositeKeyDepth();
        IndexPrecedenceOption indexPrecedenceOption = config.getIndexPrecedenceOption();
        int firstIndexableConstraint = indexPrecedenceOption == IndexPrecedenceOption.EQUALITY_PRIORITY ?
                determineTypeWithEqualityPriority(nodeType, constraints, config) :
                determineTypeWithPatternOrder(nodeType, constraints, config);

        if (constraintType == ConstraintTypeOperator.EQUAL) {
            List<FieldIndex> indexList = new ArrayList<>();
            if (isEqualIndexable(constraints[firstIndexableConstraint])) {
                indexList.add(((IndexableConstraint) constraints[firstIndexableConstraint]).getFieldIndex());
            }

            // look for other EQUAL constraint to eventually add them to the index
            for (int i = firstIndexableConstraint+1; i < constraints.length && indexList.size() < keyDepth; i++) {
                if ( isEqualIndexable(constraints[i]) && ! ((IndexableConstraint) constraints[i]).isUnification() ) {
                    indexList.add(((IndexableConstraint)constraints[i]).getFieldIndex());
                }
            }
            indexes = indexList.toArray(new FieldIndex[indexList.size()]);

        } else if (constraintType.isComparison()) {
            // look for a dual constraint to create a range index
            indexes = new FieldIndex[]{((IndexableConstraint)constraints[firstIndexableConstraint]).getFieldIndex() };
        }
    }

    private int determineTypeWithEqualityPriority(short nodeType, BetaNodeFieldConstraint[] constraints, RuleBaseConfiguration config) {
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

    private int determineTypeWithPatternOrder(short nodeType, BetaNodeFieldConstraint[] constraints, RuleBaseConfiguration config) {
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