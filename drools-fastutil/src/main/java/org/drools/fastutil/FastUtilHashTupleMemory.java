package org.drools.fastutil;

import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.util.AbstractHashTable.DoubleCompositeIndex;
import org.drools.base.util.FieldIndex;
import org.drools.core.util.AbstractHashTable.HashEntry;
import org.drools.core.util.AbstractHashTable.Index;
import org.drools.core.util.AbstractHashTable.SingleIndex;
import org.drools.core.util.AbstractHashTable.TripleCompositeIndex;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.LinkedList;
import org.drools.core.util.index.TupleList;

public class FastUtilHashTupleMemory implements TupleMemory {

    public static final int                    PRIME            = 31;

    private int                                startResult;

    private int                                factSize;

    private FastUtilMergableHashSet<HashEntry> set;

    private transient FullFastIterator         fullFastIterator;

    private boolean                            left;

    private Index                              index;

    public static class HashStrategy implements Strategy<HashEntry> {
        public static HashStrategy INSTANCE = new HashStrategy();

        public static HashStrategy get() {
            return INSTANCE;
        }

        @Override
        public int hashCode(HashEntry a) {
            return a.hashCode();
        }

        @Override
        public boolean equals(HashEntry a, HashEntry b) {
            return a.equals(b != null ? ((IndexTupleList)b).hashEntry : null);
        }
    }

    public FastUtilHashTupleMemory(FieldIndex[] index,
                                   boolean left) {
        this.set = new FastUtilMergableHashSet<>(HashStrategy.get());

        this.left = left;

        fullFastIterator = new FullFastIterator(set);

        this.startResult = PRIME;
        for ( FieldIndex i : index ) {
            this.startResult += PRIME * this.startResult + i.getRightExtractor().getIndex();
        }

        switch ( index.length ) {
            case 0 :
                throw new IllegalArgumentException( "FieldIndexHashTable cannot use an index[] of length  0" );
            case 1 :
                this.index = new SingleIndex(index,
                                             this.startResult );
                break;
            case 2 :
                this.index = new DoubleCompositeIndex(index,
                                                      this.startResult );
                break;
            case 3 :
                this.index = new TripleCompositeIndex(index,
                                                      this.startResult );
                break;
            default :
                throw new IllegalArgumentException( "FieldIndexHashTable cannot use an index[] of length  great than 3" );
        }
    }

    @Override
    public Tuple getFirst(Tuple tuple) {
        TupleList bucket = get( tuple, !left );
        return bucket != null ? bucket.getFirst() : null;
    }

    private TupleList get(final Tuple tuple, boolean left) {
        HashEntry hashEntry = this.index.hashCodeOf(tuple, left);

        TupleList tupleList = (TupleList) set.get(hashEntry);

        return tupleList;
    }

    @Override
    public void removeAdd(Tuple tuple) {
        IndexTupleList tupleList = (IndexTupleList) tuple.getMemory();
        tupleList.remove( tuple );

        HashEntry hashEntry = this.index.hashCodeOf( tuple, left );
        if ( hashEntry.hashCode() == tupleList.hashCode() ) {
            // it's the same bucket, so re-use and return
            tupleList.add( tuple );
            return;
        }

        // bucket is empty so remove.
        if ( tupleList.getFirst() == null ) {
            set.remove(tupleList.hashEntry);
        }

        tupleList = getOrCreate(hashEntry);
        tupleList.add(tuple);
    }

    @Override
    public void add(Tuple tuple) {
        HashEntry hashEntry = this.index.hashCodeOf( tuple, left );

        final TupleList entry = getOrCreate(hashEntry);

        entry.add( tuple );
        this.factSize++;
    }

    private IndexTupleList getOrCreate(HashEntry hashEntry) {
        final IndexTupleList returned = (IndexTupleList) set.compute(hashEntry, (key, value) -> {
            if (value == null) {
                value = new IndexTupleList(key.clone());
            }
            return value;
        });
        return returned;
    }

    @Override
    public void remove(Tuple tuple) {
        IndexTupleList memory = (IndexTupleList) tuple.getMemory();

        memory.remove( tuple );
        this.factSize--;

        if ( memory.getFirst() == null ) {
            set.remove(memory.hashEntry());
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
    public Iterator iterator() {
        return new FullIterator( new FullFastIterator(set));
    }

    @Override
    public FastIterator<Tuple> fastIterator() {
        return LinkedList.fastIterator;
    }

    @Override
    public FastIterator<Tuple> fullFastIterator() {
        fullFastIterator.reset();
        return fullFastIterator;
    }

    @Override
    public FastIterator<Tuple> fullFastIterator(Tuple tuple) {
        fullFastIterator.resume(tuple);
        return fullFastIterator;
    }

    @Override
    public Tuple[] toArray() {
        Tuple[] result = new Tuple[size()];
        int index = 0;

        for ( HashEntry hash : set) {
            Tuple tuple = ((TupleList)hash).getFirst();
            while (tuple != null) {
                result[index++] = tuple;
                tuple = tuple.getNext();
            }
        }

        return result;
    }

    @Override
    public Index getIndex() {
        return this.index;
    }

    @Override
    public IndexType getIndexType() {
        return IndexType.EQUAL;
    }

    @Override
    public void clear() {
        set.clear();
        this.startResult = PRIME;
        this.factSize = 0;
    }

    public static class IndexTupleList extends TupleList implements HashEntry {
        private HashEntry hashEntry;

        public IndexTupleList(HashEntry hashEntry) {
            this.hashEntry = hashEntry;
        }

        public HashEntry hashEntry() {
            return hashEntry;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            IndexTupleList that = (IndexTupleList) o;
            return hashEntry.equals(that.hashEntry);
        }

        @Override
        public int hashCode() {
            return hashEntry.hashCode();
        }

        @Override
        public HashEntry clone() {
            throw new UnsupportedOperationException();
        }
    }


    public static class FullIterator implements Iterator<Tuple> {
        private FullFastIterator fullFastIterator;
        private Tuple tuple;

        public FullIterator(FullFastIterator fullFastIterator) {
            this.fullFastIterator = fullFastIterator;
        }

        @Override
        public Tuple next() {
            this.tuple = fullFastIterator.next(tuple);
            return this.tuple;
        }
    }

    public static class FullFastIterator implements FastIterator<Tuple> {

        private FastUtilMergableHashSet<HashEntry> set;

        private ObjectIterator<HashEntry>                     it;

        public FullFastIterator(FastUtilMergableHashSet<HashEntry> set) {
            this.set = set;
        }

        /**
         * This only seems to be used in tests, so is not performance sensitive
         * @param target
         */
        public void resume(Tuple target) {
            reset();

            TupleList targetMemory = target.getMemory(); // not ideal as this is a linear search to find the resume point
            while (it.hasNext()) {
                TupleList c = (TupleList) it.next();
                if (c == targetMemory) {
                    return;
                }
            }
        }

        public Tuple next(Tuple tuple) {
            Tuple next = null;
            if (tuple != null) {
                next = tuple.getNext();
                if (next != null) {
                    return next;
                }
            }

            if (it.hasNext()) {
                next = ((TupleList)it.next()).getFirst();
            }
            return next;
        }

        public boolean isFullIterator() {
            return true;
        }

        public void reset() {
            it = set.iterator();
        }
    }
}
