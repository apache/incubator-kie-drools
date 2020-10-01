/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.util;

import org.drools.core.reteoo.AlphaNode;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.util.index.IndexUtil.ConstraintType;

public class AlphaNodeRBTree extends RBTree<Comparable<Comparable>, AlphaNode> {

    private static final long serialVersionUID = 510L;

    public FastIterator collectLeft(Comparable upperBound) {
        Node<Comparable<Comparable>, AlphaNode> first = first();
        Node<Comparable<Comparable>, AlphaNode> upperNearest = findNearestNode( upperBound, Boundary.UPPER );

        if ( first == null || upperNearest == null ) {
            return FastIterator.EMPTY;
        }

        return new RBTreeFastIterator( first , upperNearest );
    }

    public FastIterator collectRight(Comparable lowerBound) {
        Node<Comparable<Comparable>, AlphaNode> lowerNearest = findNearestNode( lowerBound, Boundary.LOWER );
        Node<Comparable<Comparable>, AlphaNode> last = last();

        if ( lowerNearest == null || last == null ) {
            return FastIterator.EMPTY;
        }

        return new RBTreeFastIterator( lowerNearest , last );
    }

    /**
     * Find the nearest node. If keys are equal, return it only when AlphaNode constraint is inclusive (GREATER_OR_EQUAL/LESS_OR_EQUAL)
     */
    public Node<Comparable<Comparable>, AlphaNode> findNearestNode(Comparable key, Boundary boundary) {
        Node<Comparable<Comparable>, AlphaNode> nearest = null;
        Node<Comparable<Comparable>, AlphaNode> n = root;

        while (n != null) {
            int compResult = key.compareTo(n.key);
            if (compResult == 0 && isInclusive(n.value)) {
                return n;
            }

            boolean accepted = acceptNode(compResult, boundary);
            if (accepted && (nearest == null || acceptNode(n.key.compareTo(nearest.key), boundary))) {
                nearest = n;
            }

            if (compResult == 0) {
                n = boundary == Boundary.LOWER ? n.right : n.left;
            } else {
                n = accepted ^ boundary == Boundary.LOWER ? n.right : n.left;
            }
        }

        return nearest;
    }

    private boolean isInclusive(AlphaNode value) {
        ConstraintType constraintType = ((IndexableConstraint) value.getConstraint()).getConstraintType();
        return (constraintType == ConstraintType.GREATER_OR_EQUAL || constraintType == ConstraintType.LESS_OR_EQUAL);
    }
}
