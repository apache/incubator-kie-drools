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

import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.RBTree;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleMemory;
import org.drools.core.reteoo.RightTuple;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class LeftTupleIndexRangeRBTree implements LeftTupleMemory, Externalizable {

    private RBTree<Comparable<Comparable>, RBTree<Comparable<Comparable>, LeftTupleList>> tree;

    private AbstractHashTable.FieldIndex ascendingIndex;
    private IndexUtil.ConstraintType ascendingConstraintType;

    private AbstractHashTable.FieldIndex descendingIndex;
    private IndexUtil.ConstraintType descendingConstraintType;
    
    
    private transient LeftTupleFastIterator leftTupleFastIterator;

    private int size;

    public LeftTupleIndexRangeRBTree() {
        // constructor for serialisation
    }

    public LeftTupleIndexRangeRBTree(IndexUtil.ConstraintType ascendingConstraintType, AbstractHashTable.FieldIndex ascendingIndex,
                                      IndexUtil.ConstraintType descendingConstraintType, AbstractHashTable.FieldIndex descendingIndex) {
        this.ascendingIndex = ascendingIndex;
        this.ascendingConstraintType = ascendingConstraintType;
        this.descendingIndex = descendingIndex;
        this.descendingConstraintType = descendingConstraintType;
        tree = new RBTree<Comparable<Comparable>, RBTree<Comparable<Comparable>, LeftTupleList>>();
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
        tree = (RBTree<Comparable<Comparable>, RBTree<Comparable<Comparable>, LeftTupleList>>) in.readObject();
        ascendingIndex = (AbstractHashTable.FieldIndex) in.readObject();
        ascendingConstraintType = (IndexUtil.ConstraintType) in.readObject();
        descendingIndex = (AbstractHashTable.FieldIndex) in.readObject();
        descendingConstraintType = (IndexUtil.ConstraintType) in.readObject();
        size = in.readInt();
    }

    public void add(LeftTuple leftTuple) {
        Comparable lowerBound = getLeftAscendingIndexedValue(leftTuple);
        Comparable upperBound = getLeftDescendingIndexedValue(leftTuple);
        RBTree<Comparable<Comparable>, LeftTupleList> nestedTree = tree.lookup(lowerBound);
        if (nestedTree == null) {
            nestedTree = new RBTree<Comparable<Comparable>, LeftTupleList>();
            tree.insert(lowerBound, nestedTree);
        }
        LeftTupleList list = nestedTree.lookup(upperBound);
        if (list == null) {
            list = new LeftTupleList();
            nestedTree.insert(upperBound, list);
        }
        list.add(leftTuple);
        size++;
    }

    public void remove(LeftTuple tuple) {
        tuple.getMemory().remove(tuple);
        size--;
    }

    public void removeAdd(LeftTuple tuple) {
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
        List<Comparable> nestedToBeRemoved = new ArrayList<Comparable>();
        List<LeftTuple> result = new ArrayList<LeftTuple>();

        RBTree.Node<Comparable<Comparable>, RBTree<Comparable<Comparable>, LeftTupleList>> node = null;
        RBTree.Node<Comparable<Comparable>, LeftTupleList> nestedNode = null;

        while ( (node = (RBTree.Node<Comparable<Comparable>, RBTree<Comparable<Comparable>, LeftTupleList>>) it.next( node )) != null ) {
            nestedToBeRemoved.clear();
            RBTree<Comparable<Comparable>, LeftTupleList> nestedTree = node.value;
            FastIterator nestedIt = nestedTree.fastIterator();

            while ( (nestedNode = (RBTree.Node<Comparable<Comparable>, LeftTupleList>) nestedIt.next( nestedNode )) != null ) {
                LeftTupleList list = nestedNode.value;
                int listSize = list.size();
                if (listSize == 0) {
                    nestedToBeRemoved.add(nestedNode.key);
                } else {
                    LeftTuple entry = list.getFirst();
                    while (entry != null) {
                        result.add(entry);
                        entry = (LeftTuple) entry.getNext();
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

        return result.toArray(new LeftTuple[result.size()]);
    }

    public LeftTuple getFirst(RightTuple rightTuple) {
        Comparable key = getRightIndexedValue(rightTuple);
        return getFirst(key);
    }

    public Iterator<LeftTuple> iterator() {
        LeftTupleList list = tree.first().value.first().value;
        LeftTuple firstTuple = list != null ? list.first : null;
        return new FastIterator.IteratorAdapter(fastIterator(), firstTuple);
    }

    public boolean contains(LeftTuple leftTuple) {
        Comparable lowerBound = getLeftAscendingIndexedValue(leftTuple);
        RBTree<Comparable<Comparable>, LeftTupleList> nestedTree = tree.lookup(lowerBound);
        return nestedTree == null ? false : nestedTree.lookup(getLeftDescendingIndexedValue(leftTuple)) != null;
    }

    public FastIterator fastIterator() {
        if ( leftTupleFastIterator == null ) {
            leftTupleFastIterator = new LeftTupleFastIterator();
        }
        return leftTupleFastIterator;
    }

    public FastIterator fullFastIterator() {
        if ( leftTupleFastIterator == null ) {
            leftTupleFastIterator = new LeftTupleFastIterator();
        }
        return leftTupleFastIterator;
    }

    public FastIterator fullFastIterator(LeftTuple leftTuple) {
        FastIterator fastIterator = fullFastIterator();
        fastIterator.next(getNext(leftTuple));
        return fastIterator;
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

    private LeftTuple getFirst(Comparable key) {
        RBTree.Node<Comparable<Comparable>, RBTree<Comparable<Comparable>, LeftTupleList>> nestedNode = null;
        RBTree.Node<Comparable<Comparable>, LeftTupleList> firstNode;

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

            RBTree<Comparable<Comparable>, LeftTupleList> nestedTree = nestedNode.value;
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

    private Entry getNext(LeftTuple leftTuple) {
        LeftTuple next = (LeftTuple) leftTuple.getNext();
        if (next != null) {
            return next;
        }

        Comparable ascendingKey = getLeftAscendingIndexedValue(leftTuple);
        Comparable descendingKey = getLeftDescendingIndexedValue(leftTuple);

        RBTree<Comparable<Comparable>, LeftTupleList> nestedTree = tree.lookup(ascendingKey);
        while (nestedTree != null) {
            while (true) {
                RBTree.Node<Comparable<Comparable>, LeftTupleList> nextNode = nestedTree.findNearestNode(descendingKey, false, RBTree.Boundary.LOWER);
                if (nextNode == null) {
                    break;
                }
                if (nextNode.value.size() == 0) {
                    nestedTree.delete(nextNode.key);
                } else {
                    return nextNode.value.getFirst();
                }
            }

            RBTree.Node<Comparable<Comparable>, RBTree<Comparable<Comparable>, LeftTupleList>> nextNode = tree.findNearestNode(ascendingKey, false, RBTree.Boundary.UPPER);
            nestedTree = nextNode == null ? null : nextNode.value;
        }

        return null;
    }

    public class LeftTupleFastIterator implements FastIterator {
        public Entry next(Entry object) {
            return object == null ? null : getNext((LeftTuple) object);
        }

        public boolean isFullIterator() {
            return false;
        }
    }

    public void clear() {
        tree = new RBTree<Comparable<Comparable>, RBTree<Comparable<Comparable>, LeftTupleList>>();
    }
}
