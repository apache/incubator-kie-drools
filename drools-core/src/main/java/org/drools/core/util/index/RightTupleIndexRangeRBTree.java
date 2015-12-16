/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.reteoo.TupleMemory;
import org.drools.core.spi.Tuple;
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

public class RightTupleIndexRangeRBTree implements TupleMemory, Externalizable {

    private RBTree<Comparable<Comparable>, TupleList> tree;

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
        tree = new RBTree<Comparable<Comparable>, TupleList>();
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
        tree = (RBTree<Comparable<Comparable>, TupleList>) in.readObject();
        ascendingIndex = (FieldIndex) in.readObject();
        ascendingConstraintType = (ConstraintType) in.readObject();
        descendingIndex = (FieldIndex) in.readObject();
        descendingConstraintType = (ConstraintType) in.readObject();
        size = in.readInt();
    }

    public void add(Tuple tuple) {
        Comparable key = getRightIndexedValue(tuple);
        TupleList list = tree.lookup(key);
        if (list == null) {
            list = new TupleList();
            tree.insert(key, list);
        }
        list.add(tuple);
        size++;
    }

    public void remove(Tuple tuple) {
        tuple.getMemory().remove(tuple);
        size--;
    }

    public void removeAdd(Tuple tuple) {
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
        List<Tuple> result = new ArrayList<Tuple>();

        RBTree.Node<Comparable<Comparable>, TupleList> node;
        while ( (node = (RBTree.Node<Comparable<Comparable>, TupleList>) it.next( null )) != null ) {
            TupleList bucket = node.value;
            if (bucket.size() == 0) {
                toBeRemoved.add(node.key);
            } else {
                Tuple entry = bucket.getFirst();
                while (entry != null) {
                    result.add(entry);
                    entry = (Tuple) entry.getNext();
                }
            }
        }

        for (Comparable key : toBeRemoved) {
            tree.delete(key);
        }

        return result.toArray(new Tuple[result.size()]);
    }

    public Tuple getFirst(Tuple leftTuple) {
        Comparable lowerBound = getLeftAscendingIndexedValue(leftTuple);
        Tuple rightTuple = getNext(lowerBound, true);
        return rightTuple == null ? null : checkUpperBound(rightTuple, getLeftDescendingIndexedValue(leftTuple));
    }

    private Tuple checkUpperBound(Tuple rightTuple, Comparable upperBound) {
        int compResult = getRightIndexedValue(rightTuple).compareTo(upperBound);
        return compResult < 0 || (compResult == 0 && descendingConstraintType == ConstraintType.LESS_OR_EQUAL) ? rightTuple : null;
    }

    public Iterator iterator() {
        TupleList list = tree.first().value;
        Tuple firstTuple = list != null ? list.getFirst() : null;
        return new FastIterator.IteratorAdapter(fastIterator(), firstTuple);
    }

    public boolean contains(Tuple tuple) {
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

    public FastIterator fullFastIterator(Tuple tuple) {
        FastIterator fastIterator = fullFastIterator();
        Comparable key = getRightIndexedValue(tuple);
        fastIterator.next(getNext(key, true));
        return fastIterator;
    }

    public IndexType getIndexType() {
        return IndexType.COMPARISON;
    }

    private Tuple getNext(Comparable lowerBound, boolean first) {
        RBTree.Node<Comparable<Comparable>, TupleList> firstNode;
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

    private Comparable getLeftAscendingIndexedValue(Tuple leftTuple) {
        return (Comparable) ascendingIndex.getDeclaration().getExtractor().getValue(leftTuple.get(ascendingIndex.getDeclaration()).getObject());
    }

    private Comparable getLeftDescendingIndexedValue(Tuple leftTuple) {
        return (Comparable) descendingIndex.getDeclaration().getExtractor().getValue(leftTuple.get(descendingIndex.getDeclaration()).getObject());
    }

    private Comparable getRightIndexedValue(Tuple rightTuple) {
        return (Comparable) ascendingIndex.getExtractor().getValue( rightTuple.getFactHandle().getObject() );
    }

    public class RightTupleBoundedFastIterator implements FastIterator {

        private Comparable upperBound;

        public void setUpperBound(Tuple leftTuple) {
            upperBound = getLeftDescendingIndexedValue(leftTuple);
        }

        public Entry next(Entry object) {
            if (object == null) {
                return null;
            }
            Tuple rightTuple = (Tuple) object;
            Tuple next = (Tuple) rightTuple.getNext();
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
        tree = new RBTree<Comparable<Comparable>, TupleList>();
    }
}
