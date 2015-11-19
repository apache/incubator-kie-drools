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

import org.drools.core.reteoo.TupleMemory;
import org.drools.core.spi.Tuple;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.RightTupleRBTree;
import org.drools.core.util.RightTupleRBTree.Boundary;
import org.drools.core.util.RightTupleRBTree.Node;
import org.drools.core.util.index.IndexUtil.ConstraintType;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class RightTupleIndexRBTree implements TupleMemory, Externalizable {

    private RightTupleRBTree<Comparable<Comparable>> tree;

    private FieldIndex index;
    private ConstraintType constraintType;

    private int size;

    public RightTupleIndexRBTree() {
        // constructor for serialisation
    }

    public RightTupleIndexRBTree(ConstraintType constraintType, FieldIndex index) {
        this.index = index;
        this.constraintType = constraintType;
        tree = new RightTupleRBTree<Comparable<Comparable>>();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( tree );
        out.writeObject( index );
        out.writeObject( constraintType );
        out.writeInt(size);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        tree = (RightTupleRBTree<Comparable<Comparable>>) in.readObject();
        index = (FieldIndex) in.readObject();
        constraintType = (ConstraintType) in.readObject();
        size = in.readInt();
    }

    public void add(Tuple tuple) {
        Comparable key = getRightIndexedValue( tuple );
        TupleList list = tree.insert(key);
        list.add(tuple);
        size++;
    }

    public void remove(Tuple tuple) {
        TupleList list = tuple.getMemory();
        list.remove(tuple);
        if (list.getFirst() == null) {
            tree.delete(((RightTupleRBTree.Node<Comparable<Comparable>>)list).key);
        }
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

        TupleList list = null;
        while ( (list = (TupleList) it.next( list )) != null ) {
            Tuple entry = list.getFirst();
            while (entry != null) {
                result.add(entry);
                entry = (Tuple) entry.getNext();
            }
        }

        return result.toArray(new Tuple[result.size()]);
    }

    public Tuple getFirst(Tuple tuple) {
        Comparable key = getLeftIndexedValue( tuple );
        return getNext(key, true);
    }

    public Iterator iterator() {
        TupleList list = tree.first();
        Tuple firstTuple = list != null ? list.first : null;
        return new FastIterator.IteratorAdapter(fastIterator(), firstTuple);
    }

    public boolean contains(Tuple tuple) {
        Comparable key = getRightIndexedValue( tuple );
        return tree.lookup(key) != null;
    }

    public FastIterator fastIterator() {
        return new RightTupleFastIterator();
    }

    public FastIterator fullFastIterator() {
        return new RightTupleFastIterator();
    }

    public FastIterator fullFastIterator(Tuple tuple) {
        FastIterator fastIterator = fullFastIterator();
        Comparable key = getRightIndexedValue( tuple );
        fastIterator.next(getNext(key, true));
        return fastIterator;
    }

    public IndexType getIndexType() {
        return IndexType.COMPARISON;
    }

    private Comparable getLeftIndexedValue( Tuple leftTuple ) {
        return (Comparable) index.getDeclaration().getExtractor().getValue(leftTuple.get(index.getDeclaration()).getObject());
    }

    private Comparable getRightIndexedValue( Tuple rightTuple ) {
        return (Comparable) index.getExtractor().getValue( rightTuple.getFactHandle().getObject() );
    }

    private Tuple getNext(Comparable key, boolean first) {
        Node<Comparable<Comparable>> firstNode;
        switch (constraintType) {
            case LESS_THAN:
                firstNode = tree.findNearestNode(key, false, Boundary.UPPER);
                break;
            case LESS_OR_EQUAL:
                firstNode = tree.findNearestNode(key, first, Boundary.UPPER);
                break;
            case GREATER_THAN:
                firstNode = tree.findNearestNode(key, false, Boundary.LOWER);
                break;
            case GREATER_OR_EQUAL:
                firstNode = tree.findNearestNode(key, first, Boundary.LOWER);
                break;
            default:
                throw new UnsupportedOperationException("Cannot call remove constraint of type: " + constraintType);
        }
        return firstNode == null ? null : firstNode.getFirst();
    }

    public class RightTupleFastIterator implements FastIterator {
        public Entry next(Entry object) {
            if (object == null) {
                Node<Comparable<Comparable>> firstNode = tree.first();
                return firstNode == null ? null : firstNode.getFirst();
            }
            Tuple rightTuple = (Tuple) object;
            Tuple next = (Tuple) rightTuple.getNext();
            if (next != null) {
                return next;
            }
            Comparable key = getRightIndexedValue( rightTuple );
            return getNext(key, false);
        }

        public boolean isFullIterator() {
            return false;
        }
    }

    public void clear() {
        tree = new RightTupleRBTree<Comparable<Comparable>>();
    }
}
