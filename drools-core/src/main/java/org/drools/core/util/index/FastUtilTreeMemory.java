package org.drools.core.util.index;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.TupleMemory;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.index.IndexUtil.ConstraintType;

public class FastUtilTreeMemory extends AbstractTupleIndexTree implements TupleMemory {

    private Object2ObjectRBTreeMap<Comparable, TupleList> tree;

    private TreeFastIterator fastIterator;

    private TreeFastFullIterator fullFastIterator;

    private TupleList nullTupleList;

    public static class HolderEntry implements Object2ObjectMap.Entry<Comparable, TupleList> {
        static HolderEntry INSTANCE = new HolderEntry();

        public static HolderEntry getInstance() {
            return INSTANCE;
        }

        private Comparable key;

        @Override
        public Comparable getKey() {
            return key;
        }

        public HolderEntry setKey(Comparable key) {
            this.key = key;
            return this;
        }

        @Override
        public TupleList getValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TupleList setValue(TupleList value) {
            throw new UnsupportedOperationException();
        }
    }

    public FastUtilTreeMemory(IndexUtil.ConstraintType constraintType, AbstractHashTable.FieldIndex index, boolean left) {
        this.index = index;
        this.constraintType = constraintType;
        this.left = left;
        this.tree = new Object2ObjectRBTreeMap<>();

        fastIterator = new TreeFastIterator(this);
        fullFastIterator = new TreeFastFullIterator(this);
        nullTupleList = new TupleList();
    }

    @Override
    public Tuple getFirst(Tuple tuple) {
        return fastIterator.getFirst(tuple);
    }

    @Override
    public void removeAdd(Tuple tuple) {
        IndexTupleList tupleList = (IndexTupleList) tuple.getMemory();
        tupleList.remove( tuple );

        // bucket is empty so remove.
        if ( tupleList.getFirst() == null && tupleList != nullTupleList) {
            tree.remove(tupleList.key());
        }

        TupleList entry = getOrCreate(tuple);
        entry.add(tuple);
    }

    @Override
    public void add(Tuple tuple) {
        TupleList entry = getOrCreate(tuple);
        entry.add(tuple);

        this.factSize++;
    }

    private TupleList getOrCreate(Tuple tuple) {
        Comparable key = TupleIndexRBTree.coerceType(index, !tree.isEmpty() ? tree.firstKey() : null, getIndexedValue(tuple, left));
        if (key == null) {
            return nullTupleList;
        }
        TupleList entry = tree.compute(key, (newKey, tupleList) -> {
           if (tupleList == null) {
               tupleList = new IndexTupleList(key);
           }

           return tupleList;
        });

        return entry;
    }

    @Override
    public void remove(Tuple tuple) {
        IndexTupleList memory = (IndexTupleList) tuple.getMemory();

        memory.remove( tuple );
        this.factSize--;

        if ( memory.getFirst() == null && memory != nullTupleList ) {
            tree.remove(memory.key());
        }
        tuple.clear();
    }

    @Override
    public boolean isIndexed() {
        return true;
    }

    @Override
    public int size() {
        return factSize;
    }

    @Override
    public Iterator<Tuple> iterator() {
        return null;
    }

    @Override
    public FastIterator<Tuple> fastIterator() {
        return this.fastIterator;
    }

    @Override
    public FastIterator fullFastIterator() {
        this.fullFastIterator.reset();
        return this.fullFastIterator;
    }

    @Override
    public FastIterator fullFastIterator(Tuple tuple) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Tuple[] toArray() {
        Tuple[] result = new Tuple[size()];
        int index = 0;

        for ( Object2ObjectMap.Entry<Comparable, TupleList> i : Object2ObjectMaps.fastIterable(tree)) {
            TupleList tupleList  = i.getValue();
            Tuple entry = tupleList.getFirst();
            while (entry != null) {
                result[index++] = entry;
                entry = entry.getNext();
            }
        }

        return result;
    }

    @Override
    public IndexType getIndexType() {
        return IndexType.COMPARISON;
    }

    @Override
    public void clear() {
        tree.clear();
        this.factSize = 0;
    }

    public static class TreeFastFullIterator implements FastIterator<Tuple> {
        FastUtilTreeMemory treeMemory;

        ObjectIterator<Object2ObjectMap.Entry<Comparable, TupleList>> it;

        Tuple current;

        Comparable key;

        public TreeFastFullIterator(FastUtilTreeMemory treeMemory) {
            this.treeMemory = treeMemory;
        }

        public void reset() {
            it =  treeMemory.tree.object2ObjectEntrySet().iterator();
        }

        public void resume(Tuple tuple) {
            it =  treeMemory.tree.object2ObjectEntrySet().iterator();
        }

        @Override
        public Tuple next(Tuple tuple) {
            Tuple next = null;
            if (tuple != null) {
                next = tuple.getNext();
            }

            if (next == null && it.hasNext()) {
                Object2ObjectMap.Entry<Comparable, TupleList> entry = it.next();
                TupleList list = entry.getValue();
                next = list.getFirst();
            }
            current = next;
            return next;
        }

        @Override
        public boolean isFullIterator() {
            return true;
        }
    }
    public static class TreeFastIterator implements FastIterator<Tuple> {
        FastUtilTreeMemory treeMemory;

        ObjectBidirectionalIterator<Object2ObjectMap.Entry<Comparable, TupleList>> it;

        Tuple current;

        Comparable key;

        ConstraintType constraintType;

        public TreeFastIterator(FastUtilTreeMemory treeMemory) {
            this.treeMemory = treeMemory;
            constraintType = !treeMemory.left ? treeMemory.constraintType : treeMemory.constraintType.negate();
        }

        public Tuple getFirst(Tuple tuple) {
            key = TupleIndexRBTree.coerceType(treeMemory.index, !treeMemory.tree.isEmpty() ? treeMemory.tree.firstKey() : null, treeMemory.getIndexedValue(tuple, !treeMemory.left));

            if (key == null) {
                switch (constraintType) {
                    case EQUAL:
                    case GREATER_OR_EQUAL:
                    case LESS_OR_EQUAL:
                        return treeMemory.nullTupleList.getFirst();
                    default:
                        return null;
                }
            }

            HolderEntry from = HolderEntry.getInstance().setKey(key);

            if (constraintType == ConstraintType.LESS_THAN ||
                constraintType == ConstraintType.LESS_OR_EQUAL) {
                it =  treeMemory.tree.object2ObjectEntrySet().iterator();
            } else {
                it = treeMemory.tree.object2ObjectEntrySet().iterator(from);
            }

            Tuple first = null;

            // adjust for >=, as fastutils is it.next() is always returning the first that is >
            if (it.hasNext()) {
                switch (constraintType) {
                    case LESS_THAN: {
                        Object2ObjectMap.Entry<Comparable, TupleList> tempKey = it.next();
                        if (tempKey.getKey().compareTo(key) < 0) {
                            first = tempKey.getValue().getFirst();
                        } else {
                            key = null;
                            it = null;
                        }
                        break;
                    } case LESS_OR_EQUAL: {
                        Object2ObjectMap.Entry<Comparable, TupleList> tempKey = it.next();
                        if (tempKey.getKey().compareTo(key) <= 0) {
                            first = tempKey.getValue().getFirst();
                        } else {
                            key = null;
                            it = null;
                        }
                        break;
                    } case GREATER_OR_EQUAL: {
                        if (it.hasPrevious()) {
                            Object2ObjectMap.Entry<Comparable, TupleList> prev = it.previous();

                            if (prev.getKey().compareTo(key) >= 0) {
                                Object2ObjectMap.Entry<Comparable, TupleList> entry = prev; // skip the == bucket
                                first = entry.getValue().getFirst();
                            }
                            break; // deliberaly inside the if, as if there is no previous it should continue as normal
                        }
                    } default: {
                        Object2ObjectMap.Entry<Comparable, TupleList> entry = it.next(); // skip the == bucket
                        first = entry.getValue().getFirst();
                        break;
                    }
                }
            }

            current = first;

            return first;
        }

        @Override
        public Tuple next(Tuple tuple) {
            Tuple next = tuple.getNext();

            if (next == null && it.hasNext()) {
                Object2ObjectMap.Entry<Comparable, TupleList> entry = it.next();
                TupleList list = entry.getValue();

                // check if we need to finish now, of so return null;
                switch (constraintType) {
                    case LESS_THAN: {
                        if (entry.getKey().compareTo(key) < 0) {
                            next = list.getFirst();
                        } else {
                            it = null;
                            key = null;
                        }
                        break;
                    } case LESS_OR_EQUAL: {
                        if (entry.getKey().compareTo(key) <= 0) {
                            next = list.getFirst();
                        } else {
                            it = null;
                            key = null;
                        }
                        break;
                    } default:
                        next = list.getFirst();
                }
            } else if (next == null){
                it = null;
                key = null;
            }
            current = next;
            return next;
        }

        @Override
        public boolean isFullIterator() {
            return false;
        }
    }
}
