package org.drools.core.util.index;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.RightTupleRBTree;
import org.drools.core.util.RightTupleRBTree.Boundary;
import org.drools.core.util.RightTupleRBTree.Node;
import org.drools.core.util.index.IndexUtil.ConstraintType;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleMemory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class RightTupleIndexRBTree implements RightTupleMemory, Externalizable {

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

    public void add(RightTuple tuple) {
        Comparable key = getIndexedValue(tuple);
        RightTupleList list = tree.insert(key);
        list.add(tuple);
        size++;
    }

    public void remove(RightTuple tuple) {
        RightTupleList list = tuple.getMemory();
        list.remove(tuple);
        if (list.getFirst() == null) {
            tree.delete(((RightTupleRBTree.Node<Comparable<Comparable>>)list).key);
        }
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

        RightTupleList list = null;
        while ( (list = (RightTupleList) it.next( list )) != null ) {
            RightTuple entry = list.getFirst();
            while (entry != null) {
                result.add(entry);
                entry = (RightTuple) entry.getNext();
            }
        }

        return result.toArray(new LeftTuple[result.size()]);
    }

    public RightTuple getFirst(LeftTuple tuple, InternalFactHandle factHandle, FastIterator rightTupleIterator) {
        Comparable key = getIndexedValue(tuple);
        return getNext(key, true);
    }

    public Iterator iterator() {
        RightTupleList list = tree.first();
        RightTuple firstTuple = list != null ? list.first : null;
        return new FastIterator.IteratorAdapter(fastIterator(), firstTuple);
    }

    public boolean contains(RightTuple tuple) {
        Comparable key = getIndexedValue(tuple);
        return tree.lookup(key) != null;
    }

    public FastIterator fastIterator() {
        return new RightTupleFastIterator();
    }

    public FastIterator fullFastIterator() {
        return new RightTupleFastIterator();
    }

    public FastIterator fullFastIterator(RightTuple tuple) {
        FastIterator fastIterator = fullFastIterator();
        Comparable key = getIndexedValue(tuple);
        fastIterator.next(getNext(key, true));
        return fastIterator;
    }

    public IndexType getIndexType() {
        return IndexType.COMPARISON;
    }

    private Comparable getIndexedValue(LeftTuple leftTuple) {
        return (Comparable) index.getDeclaration().getExtractor().getValue(leftTuple.get(index.getDeclaration()).getObject());
    }

    private Comparable getIndexedValue(RightTuple rightTuple) {
        return (Comparable) index.getExtractor().getValue( rightTuple.getFactHandle().getObject() );
    }

    private RightTuple getNext(Comparable key, boolean first) {
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
            RightTuple rightTuple = (RightTuple) object;
            RightTuple next = (RightTuple) rightTuple.getNext();
            if (next != null) {
                return next;
            }
            Comparable key = getIndexedValue(rightTuple);
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
