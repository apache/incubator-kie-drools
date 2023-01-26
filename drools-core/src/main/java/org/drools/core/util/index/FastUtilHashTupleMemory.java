package org.drools.core.util.index;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.util.AbstractHashTable.DoubleCompositeIndex;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.AbstractHashTable.HashEntry;
import org.drools.core.util.AbstractHashTable.Index;
import org.drools.core.util.AbstractHashTable.SingleIndex;
import org.drools.core.util.AbstractHashTable.TripleCompositeIndex;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.LinkedList;

public class FastUtilHashTupleMemory implements TupleMemory {

    public static final int                           PRIME            = 31;

    private int                                       startResult;

    private int                                       factSize;

    private Object2ObjectOpenHashMap<HashEntry, IndexTupleList> map;

    private transient FullFastIterator  fullFastIterator;

    private boolean                        left;

    private Index index;

    public FastUtilHashTupleMemory(FieldIndex[] index,
                                   boolean left) {
        this.map = new Object2ObjectOpenHashMap<>();

        this.left = left;

        fullFastIterator = new FullFastIterator(map);

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

        TupleList tupleList = map.get(hashEntry);

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
            map.remove(tupleList.hashEntry);
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
        final IndexTupleList entry = map.compute(hashEntry, (key, value) -> {
            if (value == null) {
                value = new IndexTupleList(key.clone());
            }
            return value;
        });
        return entry;
    }

    @Override
    public void remove(Tuple tuple) {
        IndexTupleList memory = (IndexTupleList) tuple.getMemory();

        memory.remove( tuple );
        this.factSize--;

        if ( memory.getFirst() == null ) {
            map.remove(memory.hashEntry());
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
        return new FullIterator( new FullFastIterator(map));
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

        for ( Object2ObjectMap.Entry<HashEntry, IndexTupleList> i : Object2ObjectMaps.fastIterable(map)) {
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
    public Index getIndex() {
        return this.index;
    }

    @Override
    public IndexType getIndexType() {
        return IndexType.EQUAL;
    }

    @Override
    public void clear() {
        map.clear();
        this.startResult = PRIME;
        this.factSize = 0;
    }

    public static class IndexTupleList extends TupleList {
        private HashEntry hashEntry;

        public IndexTupleList(HashEntry hashEntry) {
            this.hashEntry = hashEntry;
        }

        public HashEntry hashEntry() {
            return hashEntry;
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
            this.tuple = (Tuple) fullFastIterator.next(tuple);
            return this.tuple;
        }
    }

    public static class FullFastIterator implements FastIterator<Tuple> {

        private Object2ObjectOpenHashMap<HashEntry, IndexTupleList> map;

        private ObjectIterator<Object2ObjectMap.Entry<HashEntry, IndexTupleList>> it;

        public FullFastIterator(Object2ObjectOpenHashMap<HashEntry, IndexTupleList> map) {
            this.map = map;;
        }

        /**
         * This only seems to be used in tests, so is not performance sensitive
         * @param target
         */
        public void resume(Tuple target) {
            reset();

            TupleList targetMemory = target.getMemory(); // not ideal as this is a linear search to find the resume point
            while (it.hasNext()) {
                TupleList c = it.next().getValue();
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
                next = it.next().getValue().getFirst();
            }
            return next;
        }

        public boolean isFullIterator() {
            return true;
        }

        public void reset() {
            it = Object2ObjectMaps.fastIterable(map).iterator();
        }
    }
}
