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
 *
 */

package org.drools.core.util.index;

import org.drools.core.reteoo.TupleMemory;
import org.drools.core.spi.Tuple;
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.RBTree;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class LeftTupleIndexRangeRBTree implements Externalizable, TupleMemory {

    private RBTree<Comparable<Comparable>, RBTree<Comparable<Comparable>, TupleList>> tree;

    private AbstractHashTable.FieldIndex ascendingIndex;
    private IndexUtil.ConstraintType ascendingConstraintType;

    private AbstractHashTable.FieldIndex descendingIndex;
    private IndexUtil.ConstraintType descendingConstraintType;


    private transient TupleFastIterator tupleFastIterator;

    private int size;

    public LeftTupleIndexRangeRBTree() {
        // constructor for serialisation
    }

    public LeftTupleIndexRangeRBTree( IndexUtil.ConstraintType ascendingConstraintType, AbstractHashTable.FieldIndex ascendingIndex,
                                      IndexUtil.ConstraintType descendingConstraintType, AbstractHashTable.FieldIndex descendingIndex ) {
        this.ascendingIndex = ascendingIndex;
        this.ascendingConstraintType = ascendingConstraintType;
        this.descendingIndex = descendingIndex;
        this.descendingConstraintType = descendingConstraintType;
        tree = new RBTree<Comparable<Comparable>, RBTree<Comparable<Comparable>, TupleList>>();
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
        tree = (RBTree<Comparable<Comparable>, RBTree<Comparable<Comparable>, TupleList>>) in.readObject();
        ascendingIndex = (AbstractHashTable.FieldIndex) in.readObject();
        ascendingConstraintType = (IndexUtil.ConstraintType) in.readObject();
        descendingIndex = (AbstractHashTable.FieldIndex) in.readObject();
        descendingConstraintType = (IndexUtil.ConstraintType) in.readObject();
        size = in.readInt();
    }

    public void add(Tuple tuple) {
        Comparable lowerBound = getLeftAscendingIndexedValue(tuple);
        Comparable upperBound = getLeftDescendingIndexedValue(tuple);
        RBTree<Comparable<Comparable>, TupleList> nestedTree = tree.lookup(lowerBound);
        if (nestedTree == null) {
            nestedTree = new RBTree<Comparable<Comparable>, TupleList>();
            tree.insert(lowerBound, nestedTree);
        }
        TupleList list = nestedTree.lookup(upperBound);
        if (list == null) {
            list = new TupleList();
            nestedTree.insert(upperBound, list);
        }
        list.add(tuple);
        size++;
    }

    public void remove(Tuple tuple) {
        tuple.getMemory().remove(tuple);
        size--;
    }

    public void removeAdd(Tuple tuple) {
        remove( tuple );
        add( tuple );
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
        List<Comparable> nestedToBeRemoved = new ArrayList<Comparable>();
        List<Tuple> result = new ArrayList<Tuple>();

        RBTree.Node<Comparable<Comparable>, RBTree<Comparable<Comparable>, TupleList>> node = null;
        RBTree.Node<Comparable<Comparable>, TupleList> nestedNode = null;

        while ( (node = (RBTree.Node<Comparable<Comparable>, RBTree<Comparable<Comparable>, TupleList>>) it.next( node )) != null ) {
            nestedToBeRemoved.clear();
            RBTree<Comparable<Comparable>, TupleList> nestedTree = node.value;
            FastIterator nestedIt = nestedTree.fastIterator();

            while ( (nestedNode = (RBTree.Node<Comparable<Comparable>, TupleList>) nestedIt.next( nestedNode )) != null ) {
                TupleList list = nestedNode.value;
                int listSize = list.size();
                if (listSize == 0) {
                    nestedToBeRemoved.add(nestedNode.key);
                } else {
                    Tuple entry = list.getFirst();
                    while (entry != null) {
                        result.add(entry);
                        entry = (Tuple) entry.getNext();
                    }
                }
            }

            for (Comparable key : nestedToBeRemoved) {
                nestedTree.delete(key);
            }

            if (nestedTree.isEmpty()) {
                toBeRemoved.add(node.key);
            }
        }

        for (Comparable key : toBeRemoved) {
            tree.delete(key);
        }

        return result.toArray(new Tuple[result.size()]);
    }

    public Tuple getFirst(Tuple tuple) {
        Comparable key = getRightIndexedValue(tuple);
        return getFirst(key);
    }

    public Iterator<Tuple> iterator() {
        TupleList list = tree.first().value.first().value;
        Tuple firstTuple = list != null ? list.first : null;
        return new FastIterator.IteratorAdapter(fastIterator(), firstTuple);
    }

    public boolean contains(Tuple tuple) {
        Comparable lowerBound = getLeftAscendingIndexedValue(tuple);
        RBTree<Comparable<Comparable>, TupleList> nestedTree = tree.lookup(lowerBound);
        return nestedTree == null ? false : nestedTree.lookup(getLeftDescendingIndexedValue(tuple)) != null;
    }

    public FastIterator fastIterator() {
        if ( tupleFastIterator == null ) {
            tupleFastIterator = new TupleFastIterator();
        }
        return tupleFastIterator;
    }

    public FastIterator fullFastIterator() {
        if ( tupleFastIterator == null ) {
            tupleFastIterator = new TupleFastIterator();
        }
        return tupleFastIterator;
    }

    public FastIterator fullFastIterator(Tuple tuple) {
        FastIterator fastIterator = fullFastIterator();
        fastIterator.next(getNext(tuple));
        return fastIterator;
    }

    private Comparable getLeftAscendingIndexedValue(Tuple tuple) {
        return (Comparable) ascendingIndex.getDeclaration().getExtractor().getValue( tuple.get( ascendingIndex.getDeclaration() ).getObject() );
    }

    private Comparable getLeftDescendingIndexedValue(Tuple tuple) {
        return (Comparable) descendingIndex.getDeclaration().getExtractor().getValue( tuple.get( descendingIndex.getDeclaration() ).getObject() );
    }

    private Comparable getRightIndexedValue(Tuple tuple) {
        return (Comparable) ascendingIndex.getExtractor().getValue( tuple.getFactHandle().getObject() );
    }

    private Tuple getFirst(Comparable key) {
        RBTree.Node<Comparable<Comparable>, RBTree<Comparable<Comparable>, TupleList>> nestedNode = null;
        RBTree.Node<Comparable<Comparable>, TupleList> firstNode;

        boolean findNextNestedNode = true;
        while (true) {
            if (findNextNestedNode) {
                switch (ascendingConstraintType) {
                    case GREATER_THAN:
                        nestedNode = tree.findNearestNode(key, false, RBTree.Boundary.UPPER);
                        break;
                    case GREATER_OR_EQUAL:
                        nestedNode = tree.findNearestNode(key, true, RBTree.Boundary.UPPER);
                        break;
                    default:
                        throw new UnsupportedOperationException("Cannot call remove constraint of type: " + ascendingConstraintType);
                }
            }

            findNextNestedNode = true;
            if (nestedNode == null) {
                return null;
            }
            if (nestedNode.value.isEmpty()) {
                tree.delete(nestedNode.key);
                continue;
            }

            RBTree<Comparable<Comparable>, TupleList> nestedTree = nestedNode.value;
            while (true) {
                switch (descendingConstraintType) {
                    case LESS_THAN:
                        firstNode = nestedTree.findNearestNode(key, false, RBTree.Boundary.LOWER);
                        break;
                    case LESS_OR_EQUAL:
                        firstNode = nestedTree.findNearestNode(key, true, RBTree.Boundary.LOWER);
                        break;
                    default:
                        throw new UnsupportedOperationException("Cannot call remove constraint of type: " + descendingConstraintType);
                }

                if (firstNode != null && firstNode.value.size() == 0) {
                    nestedTree.delete(firstNode.key);
                    firstNode = null;
                }

                if (firstNode == null) {
                    nestedNode = tree.findNearestNode(nestedNode.key, false, RBTree.Boundary.UPPER);
                    if (nestedNode == null) {
                        return null;
                    }
                    findNextNestedNode = false;
                    break;
                }

                return firstNode.value.getFirst();
            }
        }
    }

    private Entry getNext(Tuple tuple) {
        Tuple next = (Tuple) tuple.getNext();
        if (next != null) {
            return next;
        }

        Comparable ascendingKey = getLeftAscendingIndexedValue(tuple);
        Comparable descendingKey = getLeftDescendingIndexedValue(tuple);

        RBTree<Comparable<Comparable>, TupleList> nestedTree = tree.lookup(ascendingKey);
        while (nestedTree != null) {
            while (true) {
                RBTree.Node<Comparable<Comparable>, TupleList> nextNode = nestedTree.findNearestNode(descendingKey, false, RBTree.Boundary.LOWER);
                if (nextNode == null) {
                    break;
                }
                if (nextNode.value.size() == 0) {
                    nestedTree.delete(nextNode.key);
                } else {
                    return nextNode.value.getFirst();
                }
            }

            RBTree.Node<Comparable<Comparable>, RBTree<Comparable<Comparable>, TupleList>> nextNode = tree.findNearestNode(ascendingKey, false, RBTree.Boundary.UPPER);
            nestedTree = nextNode == null ? null : nextNode.value;
        }

        return null;
    }

    public class TupleFastIterator implements FastIterator {
        public Entry next(Entry object) {
            return object == null ? null : getNext((Tuple) object);
        }

        public boolean isFullIterator() {
            return false;
        }
    }

    public void clear() {
        tree = new RBTree<Comparable<Comparable>, RBTree<Comparable<Comparable>, TupleList>>();
    }

    public TupleMemory.IndexType getIndexType() {
        return TupleMemory.IndexType.COMPARISON;
    }
}
