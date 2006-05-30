package org.drools.util;

/*
 * Javolution - Java(TM) Solution for Real-Time and Embedded Systems
 * Copyright (C) 2005 - Javolution (http://javolution.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <p> This class represents a hash map with real-time behavior; 
 *     smooth capacity increase and no rehashing ever performed.</p>
 *     <img src="doc-files/map-put.png"/>
 *     
 * <p> {@link FastMap} has a predictable iteration order, which is the order in
 *     which keys are inserted into the map (similar to 
 *     <code>java.util.LinkedHashMap</code> collection class).</p>
 *     
 * <p> {@link FastMap.Entry} can quickly be iterated over (forward or backward)
 *     without using iterators. For example:[code]
 *     FastMap<String, Thread> map = new FastMap<String, Thread>();
 *     for (FastMap.Entry<String, Thread> e = map.head(), end = map.tail(); (e = e.getNext()) != end;) {
 *          String key = e.getKey(); // No typecast necessary.
 *          Thread value = e.getValue(); // No typecast necessary.
 *     }[/code]</p>
 * 
 * <p> {@link FastMap} may use custom key comparators; the default comparator is
 *     either {@link FastComparator#DIRECT DIRECT} or 
 *     {@link FastComparator#REHASH REHASH} based upon the current <a href=
 *     "{@docRoot}/overview-summary.html#configuration">Javolution 
 *     Configuration</a>. Users may explicitly set the key comparator to 
 *     {@link FastComparator#DIRECT DIRECT} for optimum performance
 *     when the hash codes are well distributed for all run-time platforms
 *     (e.g. calculated hash codes).</p>
 *     
 * <p> Custom key comparators are extremely useful for value retrieval when
 *     map's keys and argument keys are not of the same class, such as 
 *     {@link String} and {@link javolution.lang.Text Text} 
 *     ({@link FastComparator#LEXICAL LEXICAL}) or for identity maps 
 *     ({@link FastComparator#IDENTITY IDENTITY}).
 *     For example:[code]
 *     FastMap identityMap = new FastMap().setKeyComparator(FastComparator.IDENTITY);
 *     [/code]</p>
 * 
 * <p> {@link FastMap} marked {@link #setShared(boolean) shared} are 
 *     thread-safe without external synchronization and are often good 
 *     substitutes for <code>ConcurrentHashMap</code>. For example:[code]
 *     // Holds the units multiplication lookup table (persistent).
 *     static final FastMap<Unit, FastMap<Unit, Unit>> MULT_LOOKUP 
 *          = new FastMap<Unit, FastMap<Unit, Unit>>("mult-unit-lookup").setShared(true);
 *     
 *     // Fast and non-blocking (no synchronization necessary).     
 *     static Unit productOf(Unit left, Unit right) {
 *          FastMap<Unit, Unit> leftTable = MULT_LOOKUP.get(left);
 *          if (leftTable == null) return calculateProductOf(left, right);
 *          Unit result = leftTable.get(right);
 *          if (result == null) return calculateProductOf(left, right);
 *          return result; // Returns cache result.
 *    }[/code]</p>
 *     
 * <p> Finally, {@link FastMap} are {@link Reusable reusable}; they maintain an 
 *     internal pool of <code>Map.Entry</code> objects. When an entry is removed
 *     from a map, it is automatically restored to its pool (unless the map
 *     is shared in which case the removed entry is candidate for garbage 
 *     collection as it cannot be safely recycled).</p>
 *     
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle </a>
 * @version 3.7, March 29, 2006
 */
public class FastMap
    /*<K,V>*/implements
    Map/*<K,V>*/,
    Serializable {

    /**
     * 
     */
    private static final long             serialVersionUID        = -4281546938715716829L;

    /**
     * Holds table higher index rotation. 
     */
    private static final int              R0                      = 5;

    /**
     * Holds the table lower index mask. 
     */
    private static final int              M0                      = (1 << FastMap.R0) - 1;

    /**
     * Holds the map's hash table.
     * Use two dimensional arrays to avoid large arrays allocations. 
     */
    private transient Entry/*<K,V>*/[][] _entries;

    /**
     * Holds the head entry to which the first entry attaches.
     * The head entry never changes (entries always added last).
     */
    private transient Entry              /*<K,V>*/_head         = new Entry();

    /**
     * Holds the tail entry to which the last entry attaches.
     * The tail entry changes as entries are added/removed.
     */
    private transient Entry              /*<K,V>*/_tail         = new Entry();

    /**
     * Holds the current size.
     */
    private transient int                 _size;

    /**
     * Holds the values view.
     */
    private transient Values              _values                 = new Values();

    /**
     * Holds the key set view.
     */
    private transient KeySet              _keySet                 = new KeySet();

    /**
     * Holds the entry set view.
     */
    private transient EntrySet            _entrySet               = new EntrySet();

    /**
     * Holds the unmodifiable view.
     */
    private transient Map                /*<K,V>*/_unmodifiable = new Unmodifiable();

    /**
     * Holds a reference to a map having the old entries when resizing.
     */
    private transient FastMap            /*<K,V>*/_oldEntries;

    /**
     * Holds the key comparator.
     */
    private transient FastComparator      _keyComparator          = FastComparator.DEFAULT;

    /**
     * Holds comparator set to <code>null</code> when equivalent to direct.
     */
    private transient FastComparator      _keyComp                = FastComparator.REHASH_SYSTEM_HASHCODE ? FastComparator.REHASH : null;

    /**
     * Indicates if this map is shared (thread-safe).
     */
    private transient boolean             _isShared;

    /**
     * Creates a fast map of small initial capacity.
     */
    public FastMap() {
        this( 4 );
    }

    /**
     * Creates a map of specified initial capacity; unless the map size 
     * reaches the specified capacity, operations on this map will not allocate
     * memory (no lazy object creation).
     * 
     * @param capacity the initial capacity.
     */
    public FastMap(final int capacity) {
        int tableLength = 1 << FastMap.R0;
        while ( tableLength < capacity ) {
            tableLength <<= 1;
        }
        this._entries = new Entry[tableLength >> FastMap.R0][];
        for ( int i = 0; i < this._entries.length; ) {
            this._entries[i++] = new Entry[1 << FastMap.R0];
        }
        this._head._next = this._tail;
        this._tail._previous = this._head;
        Entry/*<K,V>*/previous = this._tail;
        for ( int i = 0; i++ < capacity; ) {
            final Entry/*<K,V>*/newEntry = new Entry/*<K,V>*/();
            newEntry._previous = previous;
            previous._next = newEntry;
            previous = newEntry;
        }
    }

    /**
     * Creates a map containing the specified entries, in the order they
     * are returned by the map iterator.
     *
     * @param map the map whose entries are to be placed into this map.
     */
    public FastMap(final Map/*<? extends K, ? extends V>*/map) {
        this( map.size() );
        putAll( map );
    }

    /**
     * Creates a fast map having the specified entry table.
     * 
     * @param entries the entry table.
     */
    private FastMap(final Entry/*<K,V>*/[][] entries) {
        this._entries = entries;
        this._head._next = this._tail;
        this._tail._previous = this._head;
    }

    /**
     * Returns the head entry of this map.
     *
     * @return the entry such as <code>head().getNext()</code> holds 
     *         the first map entry.
     */
    public final Entry/*<K,V>*/head() {
        return this._head;
    }

    /**
     * Returns the tail entry of this map.
     *
     * @return the entry such as <code>tail().getPrevious()</code>
     *         holds the last map entry.
     */
    public final Entry/*<K,V>*/tail() {
        return this._tail;
    }

    /**
     * Returns the number of key-value mappings in this {@link FastMap}.
     * 
     * @return this map's size.
     */
    public final int size() {
        return this._size;
    }

    /**
     * Indicates if this map contains no key-value mappings.
     * 
     * @return <code>true</code> if this map contains no key-value mappings;
     *         <code>false</code> otherwise.
     */
    public final boolean isEmpty() {
        return this._head._next == this._tail;
    }

    /**
     * Indicates if this map contains a mapping for the specified key.
     * 
     * @param key the key whose presence in this map is to be tested.
     * @return <code>true</code> if this map contains a mapping for the
     *         specified key; <code>false</code> otherwise.
     * @throws NullPointerException if the key is <code>null</code>.
     */
    public final boolean containsKey(final Object key) {
        return getEntry( key ) != null;
    }

    /**
     * Indicates if this map associates one or more keys to the specified value.
     * 
     * @param value the value whose presence in this map is to be tested.
     * @return <code>true</code> if this map maps one or more keys to the
     *         specified value.
     * @throws NullPointerException if the key is <code>null</code>.
     */
    public final boolean containsValue(final Object value) {
        return this._values.contains( value );
    }

    /**
     * Returns the value to which this map associates the specified key.
     * 
     * @param key the key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or
     *         <code>null</code> if there is no mapping for the key.
     * @throws NullPointerException if key is <code>null</code>.
     */
    public final Object/*V*/get(final Object key) {
        final Entry/*<K,V>*/entry = getEntry( key,
                                               (this._keyComp == null) ? key.hashCode() : this._keyComp.hashCodeOf( key ) );
        return (entry != null) ? entry._value : null;
    }

    /**
     * Returns the entry with the specified key.
     * 
     * @param key the key whose associated entry is to be returned.
     * @return the entry for the specified key or <code>null</code> if none.
     */
    public final Entry/*<K,V>*/getEntry(final Object key) {
        return getEntry( key,
                         (this._keyComp == null) ? key.hashCode() : this._keyComp.hashCodeOf( key ) );
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If this map previously contained a mapping for this key, the old value
     * is replaced. For {@link #isShared() shared} map internal synchronization
     * is automatically performed.
     * 
     * @param key the key with which the specified value is to be associated.
     * @param value the value to be associated with the specified key.
     * @return the previous value associated with specified key, or
     *         <code>null</code> if there was no mapping for key. A
     *         <code>null</code> return can also indicate that the map
     *         previously associated <code>null</code> with the specified key.
     * @throws NullPointerException if the key is <code>null</code>.
     */
    public final Object/*V*/put(final Object/*K*/key,
                                 final Object/*V*/value) {
        final int keyHash = (this._keyComp == null) ? key.hashCode() : this._keyComp.hashCodeOf( key );
        if ( this._isShared ) {
            return putShared( key,
                              value,
                              keyHash );
        }
        final Entry/*<K,V>*/entry = getEntry( key,
                                               keyHash );
        if ( entry == null ) {
            addEntry( keyHash,
                      key,
                      value );
            return null;
        }
        final Object/*V*/prevValue = entry._value;
        entry._value = value;
        return prevValue;
    }

    private synchronized Object/*V*/putShared(final Object/*K*/key,
                                               final Object/*V*/value,
                                               final int keyHash) {
        final Entry/*<K,V>*/entry = getEntry( key,
                                               keyHash );
        if ( entry == null ) {
            addEntry( keyHash,
                      key,
                      value );
            return null;
        }
        final Object/*V*/prevValue = entry._value;
        entry._value = value;
        return prevValue;
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     * 
     * @param map the mappings to be stored in this map.
     * @throws NullPointerException the specified map is <code>null</code>,
     *         or the specified map contains <code>null</code> keys.
     */
    public final void putAll(final Map/*<? extends K, ? extends V>*/map) {
        if ( map instanceof FastMap ) { // Optimization.
            final FastMap/*<? extends K, ? extends V>*/fm = (FastMap/*<? extends K, ? extends V>*/) map;
            for ( Entry/*<? extends K, ? extends V>*/e = fm._head, end = fm._tail; (e = e._next) != end; ) {
                put( e._key,
                     e._value );
            }
        } else {
            for ( final Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
                final Map.Entry/*<? extends K, ? extends V>*/e = (Map.Entry/*<? extends K, ? extends V>*/) i.next();
                put( e.getKey(),
                     e.getValue() );
            }
        }
    }

    /**
     * Removes the entry for the specified key if present. The entry 
     * is recycled if the map is not marked as {@link #isShared shared};
     * otherwise the entry is candidate for garbage collection.
     * 
     * <p> Note: Shared maps in ImmortalMemory (e.g. static) should not remove
     *           their entries as it could cause a memory leak (ImmortalMemory
     *           is never garbage collected), instead they should set their 
     *           entry values to <code>null</code>.</p> 
     * 
     * @param key the key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or
     *         <code>null</code> if there was no mapping for key. A
     *         <code>null</code> return can also indicate that the map
     *         previously associated <code>null</code> with the specified key.
     * @throws NullPointerException if the key is <code>null</code>.
     */
    public final Object/*V*/remove(final Object key) {
        if ( this._isShared ) {
            return removeShared( key );
        }
        final Entry/*<K,V>*/entry = getEntry( key );
        if ( entry != null ) {
            final Object/*V*/prevValue = entry._value;
            removeEntry( entry );
            return prevValue;
        }
        return null;
    }

    private synchronized Object/*V*/removeShared(final Object key) {
        final Entry/*<K,V>*/entry = getEntry( key );
        if ( entry != null ) {
            this._size--;
            entry.detach();
            return entry._value;
        }
        return null;
    }

    /**
     * <p> Sets the shared status of this map (whether the map is thread-safe 
     *     or not). Shared maps are typically used for lookup table (e.g. static 
     *     instances in ImmortalMemory). They support concurrent access 
     *     (e.g. iterations) without synchronization, the maps updates 
     *     themselves are synchronized internally.</p>
     * <p> Unlike <code>ConcurrentHashMap</code> access to a shared map never 
     *     blocks. Retrieval reflects the map state not older than the last 
     *     time the accessing thread has been synchronized (for multi-processors
     *     systems synchronizing ensures that the CPU internal cache is not 
     *     stale).</p>
     * 
     * @param isShared <code>true</code> if this map is shared and thread-safe;
     *        <code>false</code> otherwise.
     * @return <code>this</code>
     */
    public FastMap/*<K,V>*/setShared(final boolean isShared) {
        this._isShared = isShared;
        return this;
    }

    /**
     * Indicates if this map supports concurrent operations without 
     * synchronization (default unshared).
     * 
     * @return <code>true</code> if this map is thread-safe; <code>false</code> 
     *         otherwise.
     */
    public boolean isShared() {
        return this._isShared;
    }

    /**
     * Sets the key comparator for this fast map.
     * 
     * @param keyComparator the key comparator.
     * @return <code>this</code>
     */
    public FastMap/*<K,V>*/setKeyComparator(final FastComparator keyComparator) {
        this._keyComparator = keyComparator;
        this._keyComp = (keyComparator instanceof FastComparator.Default) ? (FastComparator.REHASH_SYSTEM_HASHCODE ? FastComparator.REHASH : null) : (keyComparator instanceof FastComparator.Direct) ? null : keyComparator;
        return this;
    }

    /**
     * Returns the key comparator for this fast map.
     * 
     * @return the key comparator.
     */
    public FastComparator getKeyComparator() {
        return this._keyComparator;
    }

    /**
     * Sets the value comparator for this map.
     * 
     * @param valueComparator the value comparator.
     * @return <code>this</code>
     */
    public FastMap/*<K,V>*/setValueComparator(final FastComparator valueComparator) {
        this._values.setValueComparator( valueComparator );
        return this;
    }

    /**
     * Returns the value comparator for this fast map.
     * 
     * @return the value comparator.
     */
    public FastComparator getValueComparator() {
        return this._values.getValueComparator();
    }

    /**
     * Removes all map's entries. The entries are removed and recycled; 
     * unless this map is {@link #isShared shared} in which case the entries 
     * are candidate for garbage collection.
     * 
     * <p> Note: Shared maps in ImmortalMemory (e.g. static) should not remove
     *           their entries as it could cause a memory leak (ImmortalMemory
     *           is never garbage collected), instead they should set their 
     *           entry values to <code>null</code>.</p> 
     */
    public final void clear() {
        if ( this._isShared ) {
            clearShared();
            return;
        }
        // Clears all keys, values and buckets linked lists.
        for ( Entry/*<K,V>*/e = this._head, end = this._tail; (e = e._next) != end; ) {
            e._key = null;
            e._value = null;
            final Entry/*<K,V>*/[][] table = e._table;
            table[(e._keyHash >> FastMap.R0) & (table.length - 1)][e._keyHash & FastMap.M0] = null;
        }
        this._tail = this._head._next;
        this._size = 0;

        // Discards old entries.
        this._oldEntries = null;
    }

    private synchronized void clearShared() {
        for ( Entry/*<K,V>*/e = this._head, end = this._tail; (e = e._next) != end; ) {
            final Entry/*<K,V>*/[][] table = e._table;
            table[(e._keyHash >> FastMap.R0) & (table.length - 1)][e._keyHash & FastMap.M0] = null;
        }
        this._head._next = this._tail; // Does not modify current linked list.
        this._tail._previous = this._head; //
        this._oldEntries = null;
        this._size = 0;
    }

    /**
     * Compares the specified object with this map for equality.
     * Returns <code>true</code> if the given object is also a map and the two
     * maps represent the same mappings (regardless of collection iteration
     * order).
     * 
     * @param obj the object to be compared for equality with this map.
     * @return <code>true</code> if the specified object is equal to this map;
     *         <code>false</code> otherwise.
     */
    public boolean equals(final Object obj) {
        if ( obj == this ) {
            return true;
        } else if ( obj instanceof Map ) {
            final Map/*<?,?>*/that = (Map) obj;
            if ( this.size() == that.size() ) {
                final Set thatEntrySet = that.entrySet();
                for ( Entry e = this._head, end = this._tail; (e = e._next) != end; ) {
                    if ( !thatEntrySet.contains( e ) ) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns the hash code value for this map.
     * 
     * @return the hash code value for this map.
     */
    public int hashCode() {
        int code = 0;
        for ( Entry e = this._head, end = this._tail; (e = e._next) != end; ) {
            code += e.hashCode();
        }
        return code;
    }

    /**
     * Returns the textual representation of this map.
     * 
     * @return the textual representation of the entry set.
     */
    public String toString() {
        return this._entrySet.toString();
    }

    /**
     * Prints the current statistics on this map.
     * This method may help identify poorly defined hash functions.
     * An average collision of less than <code>50%</code> is typically 
     * acceptable.
     *  
     * @param out the stream to use for output (e.g. <code>System.out</code>)
     */
    public void printStatistics(final PrintStream out) {
        int maxOccupancy = 0;
        int totalCollisions = 0;
        int size = 0;
        for ( int i = 0; i < this._entries.length; i++ ) {
            for ( int j = 0; j < this._entries[i].length; j++ ) {
                Entry entry = this._entries[i][j];
                int occupancy = 0;
                while ( entry != null ) {
                    occupancy++;
                    if ( occupancy > maxOccupancy ) {
                        maxOccupancy = occupancy;
                    }
                    if ( occupancy > 1 ) {
                        totalCollisions++;
                    }
                    entry = entry._beside;
                    size++;
                }
            }
        }
        final StringBuffer percentCollisions = new StringBuffer();
        if ( size != 0 ) {
            percentCollisions.append( (100 * totalCollisions) / size );
            percentCollisions.append( '%' );
        } else {
            percentCollisions.append( "N/A" );
        }
        synchronized ( out ) {
            out.print( "SIZE: " + size );
            out.print( ", TABLE LENGTH: " + this._entries.length * this._entries[0].length );
            out.print( ", AVG COLLISIONS: " + percentCollisions );
            out.print( ", MAX SLOT OCCUPANCY: " + maxOccupancy );
            out.print( ", KEY COMPARATOR: " + ((this._keyComp == null) ? FastComparator.DIRECT : this._keyComp) );
            out.print( ", SHARED: " + this._isShared );
            out.println();
            if ( this._oldEntries != null ) {
                out.print( " + " );
                this._oldEntries.printStatistics( out );
            }
        }
    }

    /**
     * Returns a {@link FastCollection} view of the values contained in this
     * map. The collection is backed by the map, so changes to the
     * map are reflected in the collection, and vice-versa. The collection 
     * supports element removal, which removes the corresponding mapping from
     * this map, via the <code>Iterator.remove</code>, 
     * <code>Collection.remove</code>, <code>removeAll</code>,
     * <code>retainAll</code> and <code>clear</code> operations. 
     * It does not support the <code>add</code> or <code>addAll</code> 
     * operations.
     * 
     * @return a collection view of the values contained in this map 
     *         (instance of {@link FastCollection}).
     */
    public final Collection/*<V>*/values() {
        return this._values;
    }

    private final class Values extends FastCollection {

        /**
         * 
         */
        private static final long serialVersionUID = 8804295702684770940L;

        public int size() {
            return FastMap.this._size;
        }

        public void clear() {
            FastMap.this.clear();
        }

        public Record head() {
            return FastMap.this._head;
        }

        public Record tail() {
            return FastMap.this._tail;
        }

        public Object valueOf(final Record record) {
            return ((Entry) record)._value;
        }

        public void delete(final Record record) {
            FastMap.this.remove( ((Entry) record).getKey() );
        }
    }

    /**
     * Returns a {@link FastCollection} view of the mappings contained in this
     * map. Each element in the returned collection is a 
     * <code>FastMap.Entry</code>. The collection is backed by the map, so
     * changes to the map are reflected in the collection, and vice-versa. The
     * collection supports element removal, which removes the corresponding
     * mapping from this map, via the <code>Iterator.remove</code>,
     * <code>Collection.remove</code>,<code>removeAll</code>,
     * <code>retainAll</code>, and <code>clear</code> operations. It does
     * not support the <code>add</code> or <code>addAll</code> operations.
     * 
     * @return a collection view of the mappings contained in this map
     *         (instance of {@link FastCollection}).
     */
    public final Set/*<Map.Entry<K,V>>*/entrySet() {
        return this._entrySet;
    }

    private final class EntrySet extends FastCollection
        implements
        Set {

        /**
         * 
         */
        private static final long serialVersionUID = 8729117163337735415L;

        public int size() {
            return FastMap.this._size;
        }

        public void clear() {
            FastMap.this.clear();
        }

        public boolean contains(final Object obj) { // Optimization.
            if ( obj instanceof Map.Entry ) {
                final Map.Entry entry = (Entry) obj;
                final Entry mapEntry = getEntry( entry.getKey() );
                return entry.equals( mapEntry );
            } else {
                return false;
            }
        }

        public String toString() {
            StringBuffer text = new StringBuffer( "[" );
            final String equ = "=";
            final String sep = ", ";
            for ( Entry e = FastMap.this._head, end = FastMap.this._tail; (e = e._next) != end; ) {
                text = text.append( String.valueOf( e._key ) ).append( equ ).append( String.valueOf( e._value ) );
                if ( e._next != end ) {
                    text = text.append( sep );
                }
            }
            return text.append( ']' ).toString();
        }

        public Record head() {
            return FastMap.this._head;
        }

        public Record tail() {
            return FastMap.this._tail;
        }

        public Object valueOf(final Record record) {
            return record;
        }

        public void delete(final Record record) {
            FastMap.this.remove( ((Entry) record).getKey() );
        }
    }

    /**
     * Returns a {@link FastCollection} view of the keys contained in this 
     * map. The set is backed by the map, so changes to the map are reflected
     * in the set, and vice-versa. The set supports element removal, which 
     * removes the corresponding mapping from this map, via the 
     * <code>Iterator.remove</code>, <code>Collection.remove</code>,<code>removeAll<f/code>,
     * <code>retainAll</code>, and <code>clear</code> operations. It does
     * not support the <code>add</code> or <code>addAll</code> operations.
     * 
     * @return a set view of the keys contained in this map
     *         (instance of {@link FastCollection}).
     */
    public final Set/*<K>*/keySet() {
        return this._keySet;
    }

    private final class KeySet extends FastCollection
        implements
        Set {

        /**
         * 
         */
        private static final long serialVersionUID = -2629453921452583782L;

        public int size() {
            return FastMap.this._size;
        }

        public void clear() {
            FastMap.this.clear();
        }

        public boolean contains(final Object obj) { // Optimization.
            return FastMap.this.containsKey( obj );
        }

        public boolean remove(final Object obj) { // Optimization.
            return FastMap.this.remove( obj ) != null;
        }

        public Record head() {
            return FastMap.this._head;
        }

        public Record tail() {
            return FastMap.this._tail;
        }

        public Object valueOf(final Record record) {
            return ((Entry) record)._key;
        }

        public void delete(final Record record) {
            FastMap.this.remove( ((Entry) record).getKey() );
        }
    }

    /**
     * Returns the unmodifiable view associated to this map.
     * Attempts to modify the returned map or to directly access its  
     * (modifiable) map entries (e.g. <code>unmodifiable().entrySet()</code>)
     * result in an {@link UnsupportedOperationException} being thrown.
     * Unmodifiable {@link FastCollection} views of this map keys and values
     * are nonetheless obtainable (e.g. <code>unmodifiable().keySet(), 
     * <code>unmodifiable().values()</code>). 
     *  
     * @return an unmodifiable view of this map.
     */
    public final Map/*<K,V>*/unmodifiable() {
        return this._unmodifiable;
    }

    /**
     * Returns the entry with the specified key and hash code.
     * 
     * @param key the key whose associated entry is to be returned.
     * @param the associated hash code (need to be calculated only once).
     * @return the entry for the specified key or <code>null</code> if none.
     */
    private final Entry/*<K,V>*/getEntry(final Object key,
                                          final int keyHash) {
        Entry/*<K,V>*/entry = this._entries[(keyHash >> FastMap.R0) & (this._entries.length - 1)][keyHash & FastMap.M0];
        while ( entry != null ) {
            if ( (key == entry._key) || ((entry._keyHash == keyHash) && ((this._keyComp == null) ? key.equals( entry._key ) : this._keyComp.areEqual( key,
                                                                                                                                                      entry._key ))) ) {
                return entry;
            }
            entry = entry._beside;
        }
        return (this._oldEntries != null) ? this._oldEntries.getEntry( key,
                                                                       keyHash ) : null;
    }

    /**
     * Adds a new entry for the specified key and value.
     * 
     * @param hash the hash of the key, generated with {@link #keyHash}.
     * @param key the entry's key.
     * @param value the entry's value.
     */
    private void addEntry(final int hash,
                          final Object/*K*/key,
                          final Object/*V*/value) {
        // Updates size.
        if ( (this._size++ >> FastMap.R0) >= this._entries.length ) { // Check if entry table too small. 
            increaseEntryTable();
        }

        if ( this._tail._next == null ) {
            increaseCapacity();
        }
        final Entry newTail = this._tail._next;
        // Setups entry parameters.
        this._tail._key = key;
        this._tail._value = value;
        this._tail._keyHash = hash;
        this._tail._table = this._entries;

        // Connects to bucket.
        final int index = (hash >> FastMap.R0) & (this._entries.length - 1);
        Entry[] tmp = this._entries[index];
        if ( tmp == FastMap.NULL_BLOCK ) {
            newBlock( index );
            tmp = this._entries[index];
        }
        final Entry beside = tmp[hash & FastMap.M0];
        this._tail._beside = beside;
        tmp[hash & FastMap.M0] = this._tail;

        // Moves tail forward.
        this._tail = newTail;
    }

    /**
     * Removes the specified entry from the specified map.
     * The entry is added to the internal pool.
     * 
     * @param entry the entry to be removed.
     * @param the map from which the entry is removed.
     */
    private final void removeEntry(final Entry entry) {

        // Updates size.
        this._size--;

        // Clears value and key.
        entry._key = null;
        entry._value = null;

        // Detaches from list and bucket.
        entry.detach();

        // Re-inserts next tail.
        final Entry next = this._tail._next;
        entry._previous = this._tail;
        entry._next = next;
        this._tail._next = entry;
        if ( next != null ) {
            next._previous = entry;
        }
    }

    // Allocates a new block.
    private void newBlock(final int index) {
        this._entries[index] = new Entry[1 << FastMap.R0];
    }

    // Increases capacity (_tail._next == null)
    private void increaseCapacity() {
        final Entry/*<K,V>*/newEntry0 = new Entry/*<K,V>*/();
        this._tail._next = newEntry0;
        newEntry0._previous = this._tail;

        final Entry/*<K,V>*/newEntry1 = new Entry/*<K,V>*/();
        newEntry0._next = newEntry1;
        newEntry1._previous = newEntry0;

        final Entry/*<K,V>*/newEntry2 = new Entry/*<K,V>*/();
        newEntry1._next = newEntry2;
        newEntry2._previous = newEntry1;

        final Entry/*<K,V>*/newEntry3 = new Entry/*<K,V>*/();
        newEntry2._next = newEntry3;
        newEntry3._previous = newEntry2;

    }

    // Increases the table size, the table length is multiplied by 8.
    // It still ensures that no more half memory space is unused 
    // (most space is being taken by the entries objects themselves).
    private void increaseEntryTable() {
        final int newLength = this._entries.length << 3;
        FastMap/*<K,V>*/tmp;
        if ( newLength <= (1 << 3) ) { //                
            tmp = new FastMap/*<K,V>*/( new Entry[1 << 3][] ); // 256
        } else if ( newLength <= (1 << 6) ) {
            tmp = new FastMap/*<K,V>*/( new Entry[1 << 6][] ); // 2048
        } else if ( newLength <= (1 << 9) ) {
            tmp = new FastMap/*<K,V>*/( new Entry[1 << 9][] ); // 16,384
        } else if ( newLength <= (1 << 12) ) {
            tmp = new FastMap/*<K,V>*/( new Entry[1 << 12][] ); // 131,072
        } else if ( newLength <= (1 << 15) ) {
            tmp = new FastMap/*<K,V>*/( new Entry[1 << 15][] ); // 1,048,576
        } else if ( newLength <= (1 << 18) ) {
            tmp = new FastMap/*<K,V>*/( new Entry[1 << 18][] );
        } else if ( newLength <= (1 << 21) ) {
            tmp = new FastMap/*<K,V>*/( new Entry[1 << 21][] );
        } else if ( newLength <= (1 << 24) ) {
            tmp = new FastMap/*<K,V>*/( new Entry[1 << 24][] );
        } else if ( newLength <= (1 << 27) ) {
            tmp = new FastMap/*<K,V>*/( new Entry[1 << 27][] );
        } else { // Cannot increase.
            return;
        }
        for ( int i = 0; i < tmp._entries.length; ) {
            tmp._entries[i++] = FastMap.NULL_BLOCK;
        }

        // Takes the entry from the new map.
        final Entry[][] newEntries = tmp._entries;

        // Setups what is going to be the old entries.
        tmp._entries = this._entries;
        tmp._oldEntries = this._oldEntries;
        tmp._keyComp = this._keyComp;
        tmp._head = null;
        tmp._tail = null;
        tmp._size = -1;

        // Swaps entries.
        this._oldEntries = tmp;
        checkpoint(); // Both this and _oldEntries have the same entries.
        this._entries = newEntries; // Use new larger entry table now.

        // Done. We have now a much larger entry table. 
        // Still, we keep reference to the old entries through oldEntries
        // until the map is cleared.

    }

    private static final Entry[] NULL_BLOCK = new Entry[1 << FastMap.R0];

    // Implements Reusable.
    public void reset() {
        setShared( false ); // A shared map can only be reset if no thread use it.
        clear(); // In which case, it is safe to recycle the entries.
        setKeyComparator( FastComparator.DEFAULT );
        setValueComparator( FastComparator.DEFAULT );
    }

    /**
     * Requires special handling during de-serialization process.
     *
     * @param  stream the object input stream.
     * @throws IOException if an I/O error occurs.
     * @throws ClassNotFoundException if the class for the object de-serialized
     *         is not found.
     */
    private void readObject(final ObjectInputStream stream) throws IOException,
                                                           ClassNotFoundException {
        final int size = stream.readInt();
        final int entriesLength = stream.readInt();

        // Initializes transient fields.
        this._entries = new FastMap.Entry[entriesLength][];
        for ( int i = 0; i < this._entries.length; ) {
            this._entries[i++] = FastMap.NULL_BLOCK;
        }
        this._head = new Entry();
        this._tail = new Entry();
        this._head._next = this._tail;
        this._tail._previous = this._head;
        this._values = new Values();
        this._entrySet = new EntrySet();
        this._keySet = new KeySet();
        this._unmodifiable = new Unmodifiable();

        setShared( stream.readBoolean() );
        setKeyComparator( (FastComparator) stream.readObject() );
        setValueComparator( (FastComparator) stream.readObject() );

        // Reads data.
        for ( int i = 0; i < size; i++ ) {
            final Object/*K*/key = stream.readObject();
            final Object/*V*/value = stream.readObject();
            addEntry( this._keyComparator.hashCodeOf( key ),
                      key,
                      value );
        }
    }

    /**
     * Requires special handling during serialization process.
     *
     * @param  stream the object output stream.
     * @throws IOException if an I/O error occurs.
     */
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.writeInt( this._size );
        stream.writeInt( this._entries.length );

        stream.writeBoolean( this._isShared );
        stream.writeObject( this._keyComparator );
        stream.writeObject( this._values.getValueComparator() );

        for ( Entry e = this._head, end = this._tail; (e = e._next) != end; ) {
            stream.writeObject( e._key );
            stream.writeObject( e._value );
        }
    }

    /**
     * This class represents a {@link FastMap} entry.
     */
    public static final class Entry
        /*<K,V>*/implements
        Map.Entry/*<K,V>*/,
        FastCollection.Record {

        /**
         * Holds the next node.
         */
        private Entry              /*<K,V>*/_next;

        /**
         * Holds the previous node.
         */
        private Entry              /*<K,V>*/_previous;

        /**
         * Holds the entry key.
         */
        private Object             /*K*/_key;

        /**
         * Holds the entry value.
         */
        private Object             /*V*/_value;

        /**
         * Holds the next entry in the same bucket.
         */
        private Entry              /*<K,V>*/_beside;

        /**
         * Holds the hash table this entry belongs to.
         */
        private Entry/*<K,V>*/[][] _table;

        /**
         * Holds the key hash code.
         */
        private int                 _keyHash;

        /**
         * Default constructor.
         */
        private Entry() {
        }

        /**
         * Returns the entry after this one.
         * 
         * @return the next entry.
         */
        public final FastCollection.Record/*Entry<K,V>*/getNext() {
            return this._next;
        }

        /**
         * Returns the entry before this one.
         * 
         * @return the previous entry.
         */
        public final FastCollection.Record/*Entry<K,V>*/getPrevious() {
            return this._previous;
        }

        /**
         * Returns the key for this entry.
         * 
         * @return the entry key.
         */
        public final Object/*K*/getKey() {
            return this._key;
        }

        /**
         * Returns the value for this entry.
         * 
         * @return the entry value.
         */
        public final Object/*V*/getValue() {
            return this._value;
        }

        /**
         * Sets the value for this entry.
         * 
         * @param value the new value.
         * @return the previous value.
         */
        public final Object/*V*/setValue(final Object/*V*/value) {
            final Object/*V*/old = this._value;
            this._value = value;
            return old;
        }

        /**
         * Indicates if this entry is considered equals to the specified entry
         * (using default value and key equality comparator to ensure symetry).
         * 
         * @param that the object to test for equality.
         * @return <code>true<code> if both entry have equal keys and values.
         *         <code>false<code> otherwise.
         */
        public boolean equals(final Object that) {
            if ( that instanceof Map.Entry ) {
                final Map.Entry entry = (Map.Entry) that;
                return this._key.equals( entry.getKey() ) && ((this._value != null) ? this._value.equals( entry.getValue() ) : (entry.getValue() == null));
            } else {
                return false;
            }
        }

        /**
         * Returns the hash code for this entry.
         * 
         * @return this entry hash code.
         */
        public int hashCode() {
            return this._key.hashCode() ^ ((this._value != null) ? this._value.hashCode() : 0);
        }

        /**
         * Detaches this entry from the entry table and list.
         */
        private final void detach() {
            // Removes from list.
            this._previous._next = this._next;
            this._next._previous = this._previous;

            // Removes from bucket.
            final int index = (this._keyHash >> FastMap.R0) & (this._table.length - 1);
            final Entry/*<K,V>*/beside = this._beside;
            Entry/*<K,V>*/previous = this._table[index][this._keyHash & FastMap.M0];
            if ( previous == this ) { // First in bucket.
                this._table[index][this._keyHash & FastMap.M0] = beside;
            } else {
                while ( previous._beside != this ) {
                    previous = previous._beside;
                }
                previous._beside = beside;
            }
        }
    }

    /**
     * This class represents an read-only view over a {@link FastMap}.
     */
    private final class Unmodifiable
        implements
        Map,
        Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 2699246906507262549L;

        public boolean equals(final Object obj) {
            return FastMap.this.equals( obj );
        }

        public int hashCode() {
            return FastMap.this.hashCode();
        }

        public String toString() {
            return FastMap.this.toString();
        }

        public int size() {
            return FastMap.this.size();
        }

        public boolean isEmpty() {
            return FastMap.this.isEmpty();
        }

        public boolean containsKey(final Object key) {
            return FastMap.this.containsKey( key );
        }

        public boolean containsValue(final Object value) {
            return FastMap.this.containsValue( value );
        }

        public Object get(final Object key) {
            return FastMap.this.get( key );
        }

        public Object put(final Object key,
                          final Object value) {
            throw new UnsupportedOperationException( "Unmodifiable map" );
        }

        public Object remove(final Object key) {
            throw new UnsupportedOperationException( "Unmodifiable map" );
        }

        public void putAll(final Map map) {
            throw new UnsupportedOperationException( "Unmodifiable map" );
        }

        public void clear() {
            throw new UnsupportedOperationException( "Unmodifiable map" );
        }

        public Set keySet() {
            return (Set) FastMap.this._keySet.unmodifiable();
        }

        public Collection values() {
            return FastMap.this._values.unmodifiable();
        }

        public Set entrySet() {
            throw new UnsupportedOperationException( "Direct view over unmodifiable map entries is not supported " + " (to prevent access to Entry.setValue(Object) method). " + "To iterate over unmodifiable map entries, applications may "
                                                     + "use the keySet() and values() fast collection views " + "in conjonction." );
        }
    }

    /**
     * Ensures that the compiler will not reorder previous instructions below
     * this point.
     */
    private static void checkpoint() {
        if ( FastMap.CHECK_POINT ) {
            throw new Error(); // Reads volatile.
        }
    }

    static volatile boolean CHECK_POINT;
}