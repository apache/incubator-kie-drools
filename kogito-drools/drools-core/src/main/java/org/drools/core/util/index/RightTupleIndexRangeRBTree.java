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

import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleMemory;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.RBTree;
import org.drools.core.util.index.IndexUtil.ConstraintType;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class RightTupleIndexRangeRBTree implements RightTupleMemory, Externalizable {

    private RBTree<Comparable<Comparable>, RightTupleList> tree;

    private FieldIndex ascendingIndex;
    private ConstraintType ascendingConstraintType;

    private FieldIndex descendingIndex;
    private ConstraintType descendingConstraintType;
    
    private RightTupleBoundedFastIterator rightTupleBoundedFastIterator;

    private int size;

    public RightTupleIndexRangeRBTree() {
        // constructor for serialisation
    }

    public RightTupleIndexRangeRBTree(ConstraintType ascendingConstraintType, FieldIndex ascendingIndex,
                                      ConstraintType descendingConstraintType, FieldIndex descendingIndex) {
        this.ascendingIndex = ascendingIndex;
        this.ascendingConstraintType = ascendingConstraintType;
        this.descendingIndex = descendingIndex;
        this.descendingConstraintType = descendingConstraintType;
        tree = new RBTree<Comparable<Comparable>, RightTupleList>();
    }


    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( tree );
        out.writeObject( ascendingIndex );
        out.writeObject( ascendingConstraintType );
        out.writeObject( descendingIndex );
        out.writeObject( descendingConstraintType );
        out.writeInt(size);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        tree = (RBTree<Comparable<Comparable>, RightTupleList>) in.readObject();
        ascendingIndex = (FieldIndex) in.readObject();
        ascendingConstraintType = (ConstraintType) in.readObject();
        descendingIndex = (FieldIndex) in.readObject();
        descendingConstraintType = (ConstraintType) in.readObject();
        size = in.readInt();
    }

    public void add(RightTuple tuple) {
        Comparable key = getRightIndexedValue(tuple);
        RightTupleList list = tree.lookup(key);
        if (list == null) {
            list = new RightTupleList();
            tree.insert(key, list);
        }
        list.add(tuple);
        size++;
    }

    public void remove(RightTuple tuple) {
        tuple.getMemory().remove(tuple);
        size--;
    }

    public void removeAdd(RightTuple tuple) {
        remove(tuple);
        add(tuple);
    }

    public boolean isIndexed() {
        return true;
    }

    public int size() {
        return size;
    }

    public Entry[] toArray() {
        FastIterator it = tree.fastIterator();
        if (it == null) {
            return new Entry[0];
        }

        List<Comparable> toBeRemoved = new ArrayList<Comparable>();
        List<RightTuple> result = new ArrayList<RightTuple>();

        RBTree.Node<Comparable<Comparable>, RightTupleList> node;
        while ( (node = (RBTree.Node<Comparable<Comparable>, RightTupleList>) it.next( null )) != null ) {
            RightTupleList bucket = node.value;
            if (bucket.size() == 0) {
                toBeRemoved.add(node.key);
            } else {
                RightTuple entry = bucket.getFirst();
                while (entry != null) {
                    result.add(entry);
                    entry = (RightTuple) entry.getNext();
                }
            }
        }

        for (Comparable key : toBeRemoved) {
            tree.delete(key);
        }

        return result.toArray(new LeftTuple[result.size()]);
    }

    public RightTuple getFirst(LeftTuple leftTuple, InternalFactHandle factHandle, FastIterator rightTupleIterator) {
        if ( rightTupleIterator instanceof RightTupleBoundedFastIterator ) {
            ((RightTupleBoundedFastIterator) rightTupleIterator).setUpperBound(leftTuple);
        }

        Comparable lowerBound = getLeftAscendingIndexedValue(leftTuple);
        RightTuple rightTuple = getNext(lowerBound, true);
        return rightTuple == null ? null : checkUpperBound(rightTuple, getLeftDescendingIndexedValue(leftTuple));
    }

    private RightTuple checkUpperBound(RightTuple rightTuple, Comparable upperBound) {
        int compResult = getRightIndexedValue(rightTuple).compareTo(upperBound);
        return compResult < 0 || (compResult == 0 && descendingConstraintType == ConstraintType.LESS_OR_EQUAL) ? rightTuple : null;
    }

    public Iterator iterator() {
        RightTupleList list = tree.first().value;
        RightTuple firstTuple = list != null ? list.first : null;
        return new FastIterator.IteratorAdapter(fastIterator(), firstTuple);
    }

    public boolean contains(RightTuple tuple) {
        Comparable key = getRightIndexedValue(tuple);
        return tree.lookup(key) != null;
    }

    public FastIterator fastIterator() {
        if ( rightTupleBoundedFastIterator != null ) {
            rightTupleBoundedFastIterator = new RightTupleBoundedFastIterator();
        }
        return rightTupleBoundedFastIterator;
    }

    public FastIterator fullFastIterator() {
        if ( rightTupleBoundedFastIterator != null ) {
            rightTupleBoundedFastIterator = new RightTupleBoundedFastIterator();
        }
        return rightTupleBoundedFastIterator;
    }

    public FastIterator fullFastIterator(RightTuple tuple) {
        FastIterator fastIterator = fullFastIterator();
        Comparable key = getRightIndexedValue(tuple);
        fastIterator.next(getNext(key, true));
        return fastIterator;
    }

    public IndexType getIndexType() {
        return IndexType.COMPARISON;
    }

    private RightTuple getNext(Comparable lowerBound, boolean first) {
        RBTree.Node<Comparable<Comparable>, RightTupleList> firstNode;
        while (true) {
            switch (ascendingConstraintType) {
                case GREATER_THAN:
                    firstNode = tree.findNearestNode(lowerBound, false, RBTree.Boundary.LOWER);
                    break;
                case GREATER_OR_EQUAL:
                    firstNode = tree.findNearestNode(lowerBound, first, RBTree.Boundary.LOWER);
                    break;
                default:
                    throw new UnsupportedOperationException("Cannot call remove constraint of type: " + ascendingConstraintType);
            }
            if (firstNode != null && firstNode.value.size() == 0) {
                tree.delete(firstNode.key);
            } else {
                break;
            }
        }
        return firstNode == null ? null : firstNode.value.getFirst();
    }

    private Comparable getLeftAscendingIndexedValue(LeftTuple leftTuple) {
        return (Comparable) ascendingIndex.getDeclaration().getExtractor().getValue(leftTuple.get(ascendingIndex.getDeclaration()).getObject());
    }

    private Comparable getLeftDescendingIndexedValue(LeftTuple leftTuple) {
        return (Comparable) descendingIndex.getDeclaration().getExtractor().getValue(leftTuple.get(descendingIndex.getDeclaration()).getObject());
    }

    private Comparable getRightIndexedValue(RightTuple rightTuple) {
        return (Comparable) ascendingIndex.getExtractor().getValue( rightTuple.getFactHandle().getObject() );
    }

    public class RightTupleBoundedFastIterator implements FastIterator {

        private Comparable upperBound;

        public void setUpperBound(LeftTuple leftTuple) {
            upperBound = getLeftDescendingIndexedValue(leftTuple);
        }

        public Entry next(Entry object) {
            if (object == null) {
                return null;
            }
            RightTuple rightTuple = (RightTuple) object;
            RightTuple next = (RightTuple) rightTuple.getNext();
            if (next != null) {
                return next;
            }
            Comparable key = getRightIndexedValue(rightTuple);
            next = getNext(key, false);
            return next == null ? null : checkUpperBound(next, upperBound);
        }

        public boolean isFullIterator() {
            return false;
        }
    }

    public void clear() {
        tree = new RBTree<Comparable<Comparable>, RightTupleList>();
    }
}
