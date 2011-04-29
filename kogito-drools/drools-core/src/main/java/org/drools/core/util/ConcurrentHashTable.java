/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.common.InternalFactHandle;
import org.drools.core.util.AbstractHashTable.DoubleCompositeIndex;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.AbstractHashTable.Index;
import org.drools.core.util.AbstractHashTable.SingleIndex;
import org.drools.core.util.AbstractHashTable.TripleCompositeIndex;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;

public class ConcurrentHashTable {
    private static final long serialVersionUID          = 510l;

    /*
     * The basic strategy is to subdivide the table among Segments,
     * each of which itself is a concurrently readable hash table.
     */

    /* ---------------- Constants -------------- */

    /**
     * The default initial capacity for this table,
     * used when not otherwise specified in a constructor.
     */
    static final int          DEFAULT_INITIAL_CAPACITY  = 16;

    /**
     * The default load factor for this table, used when not
     * otherwise specified in a constructor.
     */
    static final float        DEFAULT_LOAD_FACTOR       = 0.75f;

    /**
     * The default concurrency level for this table, used when not
     * otherwise specified in a constructor.
     */
    static final int          DEFAULT_CONCURRENCY_LEVEL = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly
     * specified by either of the constructors with arguments.  MUST
     * be a power of two <= 1<<30 to ensure that entries are indexable
     * using ints.
     */
    static final int          MAXIMUM_CAPACITY          = 1 << 30;

    /**
     * The maximum number of segments to allow; used to bound
     * constructor arguments.
     */
    static final int          MAX_SEGMENTS              = 1 << 16;             // slightly conservative

    /**
     * Number of unsynchronized retries in size and containsValue
     * methods before resorting to locking. This is used to avoid
     * unbounded retries if tables undergo continuous modification
     * which would make it impossible to obtain an accurate result.
     */
    static final int          RETRIES_BEFORE_LOCK       = 2;

    /* ---------------- Fields -------------- */

    /**
     * Mask value for indexing into segments. The upper bits of a
     * key's hash code are used to choose the segment.
     */
    final int                 segmentMask;

    /**
     * Shift value for indexing within segments.
     */
    final int                 segmentShift;

    /**
     * The segments, each of which is a specialized hash table
     */
    final Segment[]           segments;

    private Index             index;

    private int               startResult;

    /* ---------------- Small Utilities -------------- */

    /**
     * Applies a supplemental hash function to a given hashCode, which
     * defends against poor quality hash functions.  This is critical
     * because ConcurrentHashMap uses power-of-two length hash tables,
     * that otherwise encounter collisions for hashCodes that do not
     * differ in lower or upper bits.
     */
    private static int hash(int h) {
        // Spread bits to regularize both segment and index locations,
        // using variant of single-word Wang/Jenkins hash.
        h += (h << 15) ^ 0xffffcd7d;
        h ^= (h >>> 10);
        h += (h << 3);
        h ^= (h >>> 6);
        h += (h << 2) + (h << 14);
        return h ^ (h >>> 16);
    }

    /**
     * Returns the segment that should be used for key with given hash
     * @param hash the hash code for the key
     * @return the segment
     */
    final Segment segmentFor(int hash) {
        return segments[(hash >>> segmentShift) & segmentMask];
    }

    /* ---------------- Inner Classes -------------- */

    /**
     * Segments are specialized versions of hash tables.  This
     * subclasses from ReentrantLock opportunistically, just to
     * simplify some locking and avoid separate construction.
     */
    static final class Segment extends ReentrantLock
        implements
        Serializable {
        /*
         * Segments maintain a table of entry lists that are ALWAYS
         * kept in a consistent state, so can be read without locking.
         * Next fields of nodes are immutable (final).  All list
         * additions are performed at the front of each bin. This
         * makes it easy to check changes, and also fast to traverse.
         * When nodes would otherwise be changed, new nodes are
         * created to replace them. This works well for hash tables
         * since the bin lists tend to be short. (The average length
         * is less than two for the default load factor threshold.)
         *
         * Read operations can thus proceed without locking, but rely
         * on selected uses of volatiles to ensure that completed
         * write operations performed by other threads are
         * noticed. For most purposes, the "count" field, tracking the
         * number of elements, serves as that volatile variable
         * ensuring visibility.  This is convenient because this field
         * needs to be read in many read operations anyway:
         *
         *   - All (unsynchronized) read operations must first read the
         *     "count" field, and should not look at table entries if
         *     it is 0.
         *
         *   - All (synchronized) write operations should write to
         *     the "count" field after structurally changing any bin.
         *     The operations must not take any action that could even
         *     momentarily cause a concurrent read operation to see
         *     inconsistent data. This is made easier by the nature of
         *     the read operations in Map. For example, no operation
         *     can reveal that the table has grown but the threshold
         *     has not yet been updated, so there are no atomicity
         *     requirements for this with respect to reads.
         *
         * As a guide, all critical volatile reads and writes to the
         * count field are marked in code comments.
         */

        private static final long           serialVersionUID = 510l;

        /**
         * The number of elements in this segment's region.
         */
        transient volatile int              tupleCount;
        
        transient volatile int              keyCount;

        /**
         * Number of updates that alter the size of the table. This is
         * used during bulk-read methods to make sure they see a
         * consistent snapshot: If modCounts change during a traversal
         * of segments computing size or checking containsValue, then
         * we might have an inconsistent view of state so (usually)
         * must retry.
         */
        transient int                       modCount;

        /**
         * The table is rehashed when its size exceeds this threshold.
         * (The value of this field is always <tt>(int)(capacity *
         * loadFactor)</tt>.)
         */
        transient int                       threshold;

        /**
         * The per-segment table.
         */
        transient volatile RightTupleList[] table;

        /**
         * The load factor for the hash table.  Even though this value
         * is same for all segments, it is replicated to avoid needing
         * links to outer object.
         * @serial
         */
        final float                         loadFactor;

        private Index                       index;

        Segment(Index index,
                int initialCapacity,
                float lf) {
            loadFactor = lf;
            setTable( new RightTupleList[initialCapacity] );
            this.index = index;
        }

        static final Segment[] newArray(int i) {
            return new Segment[i];
        }

        /**
         * Sets table to new HashEntry array.
         * Call only while holding lock or in constructor.
         */
        void setTable(RightTupleList[] newTable) {
            threshold = (int) (newTable.length * loadFactor);
            table = newTable;
        }

        /**
         * Returns properly casted first entry of bin for given hash.
         */
        RightTupleList getFirst(int hash) {
            RightTupleList[] tab = table;
            return tab[hash & (tab.length - 1)];
        }

        /* Specialized implementations of map methods */

        //        Object get(Object key, int hash) {
        //            if (count != 0) { // read-volatile
        //                RightTuple e = getFirst(hash);
        //                while (e != null) {
        //                    if (e.hash == hash && key.equals(e.key)) {
        //                        Object v = e.value;
        //                        if (v != null)
        //                            return v;
        //                        return readValueUnderLock(e); // recheck
        //                    }
        //                    e = e.next;
        //                }
        //            }
        //            return null;
        //        }
        //
        //        boolean containsKey(Object key, int hash) {
        //            if (count != 0) { // read-volatile
        //                HashEntry e = getFirst(hash);
        //                while (e != null) {
        //                    if (e.hash == hash && key.equals(e.key))
        //                        return true;
        //                    e = e.next;
        //                }
        //            }
        //            return false;
        //        }
        void add(final RightTuple rightTuple,
                 int hashCode,
                 Object object) {
            lock();
            try {
                final RightTupleList entry = getOrCreate( hashCode,
                                                          object );
                entry.add( rightTuple );
                this.tupleCount++;
            } finally {
                unlock();
            }
        }

        /**
         * Remove; match on key only if value null, else match both.\
         */
        void remove(RightTuple rightTuple,
                    int hashCode,
                    Object object) {
            lock();
            try {
                int c = keyCount - 1;

                RightTupleList[] tab = table;
                int index = hashCode & (tab.length - 1);
                RightTupleList first = tab[index];
                RightTupleList e = first;
                while ( e != null ) {
                    if ( e.matches( object,
                                    hashCode ) ) {
                        break;
                    }
                    e = (RightTupleList) e.next;
                }

                e.remove( rightTuple );
                tupleCount--;

                if ( e.getFirst( ) == null ) {
                    // list is empty, so remove it
                    RightTupleList newFirst = (RightTupleList) e.getNext();
                    for ( RightTupleList p = first; p != e; p = (RightTupleList) p.getNext() ) {
                        newFirst = new RightTupleList( p.getIndex(),
                                                       hashCode,
                                                       newFirst );
                    }
                    keyCount = c; // write-volatile
                }

            } finally {
                unlock();
            }
        }

        RightTupleList get(final int hashCode,
                                  final LeftTuple tuple,
                                  final InternalFactHandle factHandle) {
            //this.index.setCachedValue( tuple );
            lock();
            try {
                RightTupleList[] tab = table;
                int index = hashCode & (tab.length - 1);
                RightTupleList first = tab[index];
                RightTupleList entry = first;

                while ( entry != null ) {
                    if ( entry.matches( tuple,
                                        hashCode ,
                                        factHandle ) ) {
                        return entry;
                    }
                    entry = (RightTupleList) entry.getNext();
                }

                return entry;
            } finally {
                unlock();
            }
        }

        private RightTupleList getOrCreate(int hashCode,
                                           final Object object) {
            int c = keyCount;

            RightTupleList[] tab = table;
            int index = hashCode & (tab.length - 1);
            RightTupleList first = tab[index];
            RightTupleList e = first;
            while ( e != null ) {
                if ( e.matches( object,
                                hashCode ) ) {
                    return e;
                }
                e = (RightTupleList) e.next;
            }

            if ( e == null ) {
                if ( c++ > threshold ) // ensure capacity
                    rehash();
                ++modCount;
                e = new RightTupleList( this.index,
                                        hashCode,
                                        first );
                tab[index] = e;
                keyCount = c; // write-volatile
            }

            return e;
        }

        void rehash() {
            RightTupleList[] oldTable = table;
            int oldCapacity = oldTable.length;
            if ( oldCapacity >= MAXIMUM_CAPACITY ) return;

            /*
             * Reclassify nodes in each list to new Map.  Because we are
             * using power-of-two expansion, the elements from each bin
             * must either stay at same index, or move with a power of two
             * offset. We eliminate unnecessary node creation by catching
             * cases where old nodes can be reused because their next
             * fields won't change. Statistically, at the default
             * threshold, only about one-sixth of them need cloning when
             * a table doubles. The nodes they replace will be garbage
             * collectable as soon as they are no longer referenced by any
             * reader thread that may be in the midst of traversing table
             * right now.
             */

            RightTupleList[] newTable = new RightTupleList[oldCapacity << 1];
            threshold = (int) (newTable.length * loadFactor);
            int sizeMask = newTable.length - 1;
            for ( int i = 0; i < oldCapacity; i++ ) {
                // We need to guarantee that any existing reads of old Map can
                //  proceed. So we cannot yet null out each bin.
                RightTupleList e = oldTable[i];

                if ( e != null ) {
                    RightTupleList next = (RightTupleList) e.getNext();
                    int idx = e.hashCode() & sizeMask;

                    //  Single node on list
                    if ( next == null ) newTable[idx] = e;

                    else {
                        // Reuse trailing consecutive sequence at same slot
                        RightTupleList lastRun = e;
                        int lastIdx = idx;
                        for ( RightTupleList last = next; last != null; last = (RightTupleList) last.getNext() ) {
                            int k = last.hashCode() & sizeMask;
                            if ( k != lastIdx ) {
                                lastIdx = k;
                                lastRun = last;
                            }
                        }
                        newTable[lastIdx] = lastRun;

                        // Clone all remaining nodes
                        for ( RightTupleList p = e; p != lastRun; p = (RightTupleList) p.getNext() ) {
                            int k = p.hashCode() & sizeMask;
                            RightTupleList n = newTable[k];
                            newTable[k] = new RightTupleList( p, n );
                        }
                    }
                }
            }
            table = newTable;
        }

        void clear() {
            if ( tupleCount != 0 ) {
                lock();
                try {
                    RightTupleList[] tab = table;
                    for ( int i = 0; i < tab.length; i++ )
                        tab[i] = null;
                    ++modCount;
                    tupleCount = 0; // write-volatile
                    keyCount = 0;
                } finally {
                    unlock();
                }
            }
        }
    }

    /* ---------------- Public operations -------------- */

    /**
     * Creates a new, empty map with the specified initial
     * capacity, load factor and concurrency level.
     *
     * @param initialCapacity the initial capacity. The implementation
     * performs internal sizing to accommodate this many elements.
     * @param loadFactor  the load factor threshold, used to control resizing.
     * Resizing may be performed when the average number of elements per
     * bin exceeds this threshold.
     * @param concurrencyLevel the estimated number of concurrently
     * updating threads. The implementation performs internal sizing
     * to try to accommodate this many threads.
     * @throws IllegalArgumentException if the initial capacity is
     * negative or the load factor or concurrencyLevel are
     * nonpositive.
     */
    public ConcurrentHashTable(final FieldIndex[] index,
                               int initialCapacity,
                               float loadFactor,
                               int concurrencyLevel) {
        this.startResult = RightTupleIndexHashTable.PRIME;
        for ( int i = 0, length = index.length; i < length; i++ ) {
            this.startResult = RightTupleIndexHashTable.PRIME * this.startResult + index[i].getExtractor().getIndex();
        }

        switch ( index.length ) {
            case 0 :
                throw new IllegalArgumentException( "FieldIndexHashTable cannot use an index[] of length  0" );
            case 1 :
                this.index = new SingleIndex( index,
                                              this.startResult );
                break;
            case 2 :
                this.index = new DoubleCompositeIndex( index,
                                                       this.startResult );
                break;
            case 3 :
                this.index = new TripleCompositeIndex( index,
                                                       this.startResult );
                break;
            default :
                throw new IllegalArgumentException( "FieldIndexHashTable cannot use an index[] of length  great than 3" );
        }
        
        if ( !(loadFactor > 0) || initialCapacity < 0 || concurrencyLevel <= 0 ) throw new IllegalArgumentException();

        if ( concurrencyLevel > MAX_SEGMENTS ) concurrencyLevel = MAX_SEGMENTS;

        // Find power-of-two sizes best matching arguments
        int sshift = 0;
        int ssize = 1;
        while ( ssize < concurrencyLevel ) {
            ++sshift;
            ssize <<= 1;
        }
        segmentShift = 32 - sshift;
        segmentMask = ssize - 1;
        this.segments = Segment.newArray( ssize );

        if ( initialCapacity > MAXIMUM_CAPACITY ) initialCapacity = MAXIMUM_CAPACITY;
        int c = initialCapacity / ssize;
        if ( c * ssize < initialCapacity ) ++c;
        int cap = 1;
        while ( cap < c )
            cap <<= 1;

        for ( int i = 0; i < this.segments.length; ++i )
            this.segments[i] = new Segment( this.index,
                                            cap,
                                            loadFactor );
    }

    /**
     * Creates a new, empty map with the specified initial capacity
     * and load factor and with the default concurrencyLevel (16).
     *
     * @param initialCapacity The implementation performs internal
     * sizing to accommodate this many elements.
     * @param loadFactor  the load factor threshold, used to control resizing.
     * Resizing may be performed when the average number of elements per
     * bin exceeds this threshold.
     * @throws IllegalArgumentException if the initial capacity of
     * elements is negative or the load factor is nonpositive
     *
     * @since 1.6
     */
    public ConcurrentHashTable(final FieldIndex[] index,
                               int initialCapacity,
                               float loadFactor) {
        this( index,
              initialCapacity,
              loadFactor,
              DEFAULT_CONCURRENCY_LEVEL );
    }

    /**
     * Creates a new, empty map with the specified initial capacity,
     * and with default load factor (0.75) and concurrencyLevel (16).
     *
     * @param initialCapacity the initial capacity. The implementation
     * performs internal sizing to accommodate this many elements.
     * @throws IllegalArgumentException if the initial capacity of
     * elements is negative.
     */
    public ConcurrentHashTable(final FieldIndex[] index,
                               int initialCapacity) {
        this( index,
              initialCapacity,
              DEFAULT_LOAD_FACTOR,
              DEFAULT_CONCURRENCY_LEVEL );
    }

    /**
     * Creates a new, empty map with a default initial capacity (16),
     * load factor (0.75) and concurrencyLevel (16).
     */
    public ConcurrentHashTable(final FieldIndex[] index) {
        this( index,
              DEFAULT_INITIAL_CAPACITY,
              DEFAULT_LOAD_FACTOR,
              DEFAULT_CONCURRENCY_LEVEL );
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        final Segment[] segments = this.segments;
        /*
         * We keep track of per-segment modCounts to avoid ABA
         * problems in which an element in one segment was added and
         * in another removed during traversal, in which case the
         * table was never actually empty at any point. Note the
         * similar use of modCounts in the size() and containsValue()
         * methods, which are the only other methods also susceptible
         * to ABA problems.
         */
        int[] mc = new int[segments.length];
        int mcsum = 0;
        for ( int i = 0; i < segments.length; ++i ) {
            if ( segments[i].tupleCount != 0 ) return false;
            else mcsum += mc[i] = segments[i].modCount;
        }
        // If mcsum happens to be zero, then we know we got a snapshot
        // before any modifications at all were made.  This is
        // probably common enough to bother tracking.
        if ( mcsum != 0 ) {
            for ( int i = 0; i < segments.length; ++i ) {
                if ( segments[i].tupleCount != 0 || mc[i] != segments[i].modCount ) return false;
            }
        }
        return true;
    }

    /**
     * Returns the number of key-value mappings in this map.  If the
     * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        final Segment[] segments = this.segments;
        long sum = 0;
        long check = 0;
        int[] mc = new int[segments.length];
        // Try a few times to get accurate count. On failure due to
        // continuous async changes in table, resort to locking.
        for ( int k = 0; k < RETRIES_BEFORE_LOCK; ++k ) {
            check = 0;
            sum = 0;
            int mcsum = 0;
            for ( int i = 0; i < segments.length; ++i ) {
                sum += segments[i].tupleCount;
                mcsum += mc[i] = segments[i].modCount;
            }
            if ( mcsum != 0 ) {
                for ( int i = 0; i < segments.length; ++i ) {
                    check += segments[i].tupleCount;
                    if ( mc[i] != segments[i].modCount ) {
                        check = -1; // force retry
                        break;
                    }
                }
            }
            if ( check == sum ) break;
        }
        if ( check != sum ) { // Resort to locking all segments
            sum = 0;
            for ( int i = 0; i < segments.length; ++i )
                segments[i].lock();
            for ( int i = 0; i < segments.length; ++i )
                sum += segments[i].tupleCount;
            for ( int i = 0; i < segments.length; ++i )
                segments[i].unlock();
        }
        if ( sum > Integer.MAX_VALUE ) return Integer.MAX_VALUE;
        else return (int) sum;
    }

    public void add(final RightTuple rightTuple) {
        Object object = rightTuple.getFactHandle().getObject();
        final int hashCode = this.index.hashCodeOf( object );
        segmentFor( hashCode ).add( rightTuple,
                                    hashCode,
                                    object );
    }

    /**
     * Removes the key (and its corresponding value) from this map.
     * This method does nothing if the key is not in the map.
     *
     * @param  key the key that needs to be removed
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>
     * @throws NullPointerException if the specified key is null
     */
    public void remove(final RightTuple rightTuple) {
        Object object = rightTuple.getFactHandle().getObject();
        final int hashCode = this.index.hashCodeOf( object );
        segmentFor( hashCode ).remove( rightTuple,
                                       hashCode,
                                       object );
    }

    public RightTupleList get(final LeftTuple tuple, InternalFactHandle factHandle) {
        final int hashCode = this.index.hashCodeOf( tuple );
        return segmentFor( hashCode ).get( hashCode,
                                    tuple, 
                                    factHandle);
    }

    /**
     * Removes all of the mappings from this map.
     */
    public void clear() {
        for ( int i = 0; i < segments.length; ++i )
            segments[i].clear();
    }

}
