package org.drools.core.util.index;

import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.LeftTupleRBTree;
import org.drools.core.util.LeftTupleRBTree.Node;
import org.drools.core.util.LeftTupleRBTree.Boundary;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleMemory;
import org.drools.reteoo.RightTuple;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class LeftTupleIndexRBTree implements LeftTupleMemory, Externalizable {

    private LeftTupleRBTree<Comparable<Comparable>> tree;

    private AbstractHashTable.FieldIndex index;
    private IndexUtil.ConstraintType constraintType;

    private int size;

    public LeftTupleIndexRBTree() {
        // constructor for serialisation
    }

    public LeftTupleIndexRBTree(IndexUtil.ConstraintType constraintType, AbstractHashTable.FieldIndex index) {
        this.index = index;
        this.constraintType = constraintType;
        tree = new LeftTupleRBTree<Comparable<Comparable>>();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( tree );
        out.writeObject( index );
        out.writeObject( constraintType );
        out.writeInt(size);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        tree = (LeftTupleRBTree<Comparable<Comparable>>) in.readObject();
        index = (AbstractHashTable.FieldIndex) in.readObject();
        constraintType = (IndexUtil.ConstraintType) in.readObject();
        size = in.readInt();
    }

    public void add(LeftTuple tuple) {
        Comparable key = getIndexedValue(tuple);
        LeftTupleList list = tree.insert(key);
        list.add(tuple);
        size++;
    }

    public void remove(LeftTuple tuple) {
        LeftTupleList list = tuple.getMemory();
        list.remove(tuple);
        if (list.size() == 0) {
            tree.delete(((Node<Comparable<Comparable>>)list).key);
        }
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
        List<LeftTuple> result = new ArrayList<LeftTuple>();

        LeftTupleList list = null;
        while ( (list = (LeftTupleList) it.next( list )) != null ) {
            LeftTuple entry = list.getFirst();
            while (entry != null) {
                result.add(entry);
                entry = (LeftTuple) entry.getNext();
            }
        }

        return result.toArray(new LeftTuple[result.size()]);
    }

    public LeftTuple getFirst(RightTuple rightTuple) {
        Comparable key = getIndexedValue(rightTuple);
        return getNext(key, true);
    }

    public Iterator iterator() {
        return new FastIterator.IteratorAdapter(fastIterator());
    }

    public boolean contains(LeftTuple leftTuple) {
        Comparable key = getIndexedValue(leftTuple);
        return tree.lookup(key) != null;
    }

    public FastIterator fastIterator() {
        return new LeftTupleFastIterator();
    }

    public FastIterator fullFastIterator() {
        return new LeftTupleFastIterator();
    }

    public FastIterator fullFastIterator(LeftTuple leftTuple) {
        FastIterator fastIterator = fullFastIterator();
        Comparable key = getIndexedValue(leftTuple);
        fastIterator.next(getNext(key, true));
        return fastIterator;
    }

    private Comparable getIndexedValue(LeftTuple leftTuple) {
        return (Comparable) index.getDeclaration().getExtractor().getValue( leftTuple.get( index.getDeclaration() ).getObject() );
    }

    private Comparable getIndexedValue(RightTuple rightTuple) {
        return (Comparable) index.getExtractor().getValue( rightTuple.getFactHandle().getObject() );
    }

    private LeftTuple getNext(Comparable key, boolean first) {
        Node<Comparable<Comparable>> firstNode;
        switch (constraintType) {
            case LESS_THAN:
                firstNode = tree.findNearestNode(key, false, Boundary.LOWER);
                break;
            case LESS_OR_EQUAL:
                firstNode = tree.findNearestNode(key, first, Boundary.LOWER);
                break;
            case GREATER_THAN:
                firstNode = tree.findNearestNode(key, false, Boundary.UPPER);
                break;
            case GREATER_OR_EQUAL:
                firstNode = tree.findNearestNode(key, first, Boundary.UPPER);
                break;
            default:
                throw new UnsupportedOperationException("Cannot call getNext constraint of type: " + constraintType);
        }
        return firstNode == null ? null : firstNode.getFirst();
    }

    public class LeftTupleFastIterator implements FastIterator {
        public Entry next(Entry object) {
            LeftTuple leftTuple = (LeftTuple) object;
            LeftTuple next = (LeftTuple) leftTuple.getNext();
            if (next != null) {
                return next;
            }
            Comparable key = getIndexedValue(leftTuple);
            return getNext(key, false);
        }

        public boolean isFullIterator() {
            return false;
        }
    }
}
