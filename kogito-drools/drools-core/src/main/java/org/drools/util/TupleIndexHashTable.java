/**
 * 
 */
package org.drools.util;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ReteTuple;
import org.drools.reteoo.TupleMemory;

public class TupleIndexHashTable extends AbstractHashTable
    implements
    TupleMemory {

    private static final long               serialVersionUID = -6214772340195061306L;

    public static final int                 PRIME            = 31;

    private int                             startResult;

    private FieldIndexHashTableIterator     tupleValueIterator;
    private FieldIndexHashTableFullIterator tupleValueFullIterator;

    private int                             factSize;

    private Index                           index;

    public TupleIndexHashTable(final FieldIndex[] index) {
        this( 16,
              0.75f,
              index );
    }

    public TupleIndexHashTable(final int capacity,
                               final float loadFactor,
                               final FieldIndex[] index) {
        super( capacity,
               loadFactor );

        this.startResult = TupleIndexHashTable.PRIME;
        for ( int i = 0, length = index.length; i < length; i++ ) {
            this.startResult += TupleIndexHashTable.PRIME * this.startResult + index[i].getExtractor().getIndex();
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
    }

    public Iterator iterator() {
        if ( this.tupleValueFullIterator == null ) {
            this.tupleValueFullIterator = new FieldIndexHashTableFullIterator( this );
        }
        this.tupleValueFullIterator.reset();
        return this.tupleValueFullIterator;
    }

    public Iterator iterator(final InternalFactHandle handle) {
        if ( this.tupleValueIterator == null ) {
            this.tupleValueIterator = new FieldIndexHashTableIterator();
        }
        final FieldIndexEntry entry = get( handle );
        this.tupleValueIterator.reset( (entry != null) ? entry.first : null );
        return this.tupleValueIterator;
    }

    public boolean isIndexed() {
        return true;
    }

    public Entry getBucket(final Object object) {
        final int hashCode = this.index.hashCodeOf( object );
        final int index = indexOf( hashCode,
                                   this.table.length );

        return this.table[index];
    }

    /**
     * Fast re-usable iterator
     *
     */
    public static class FieldIndexHashTableIterator
        implements
        Iterator {
        private Entry entry;

        public FieldIndexHashTableIterator() {

        }

        /* (non-Javadoc)
         * @see org.drools.util.Iterator#next()
         */
        public Entry next() {
            final Entry current = this.entry;
            this.entry = (this.entry != null) ? this.entry.getNext() : null;
            return current;
        }

        /* (non-Javadoc)
         * @see org.drools.util.Iterator#reset()
         */
        public void reset(final Entry entry) {
            this.entry = entry;
        }
    }

    public static class FieldIndexHashTableFullIterator
        implements
        Iterator {
        private AbstractHashTable hashTable;
        private Entry[]           table;
        private int               row;
        private int               length;
        private Entry             entry;

        public FieldIndexHashTableFullIterator(final AbstractHashTable hashTable) {
            this.hashTable = hashTable;
        }

        /* (non-Javadoc)
         * @see org.drools.util.Iterator#next()
         */
        public Entry next() {
            if ( this.entry == null ) {
                // keep skipping rows until we come to the end, or find one that is populated
                while ( this.entry == null ) {
                    this.row++;
                    if ( this.row == this.length ) {
                        return null;
                    }
                    this.entry = (this.table[this.row] != null) ? ((FieldIndexEntry) this.table[this.row]).first : null;
                }
            } else {
                this.entry = this.entry.getNext();
                if ( this.entry == null ) {
                    this.entry = next();
                }
            }

            return this.entry;
        }

        /* (non-Javadoc)
         * @see org.drools.util.Iterator#reset()
         */
        public void reset() {
            this.table = this.hashTable.getTable();
            this.length = this.table.length;
            this.row = -1;
            this.entry = null;
        }
    }

    public void add(final ReteTuple tuple) {
        final FieldIndexEntry entry = getOrCreate( tuple );
        entry.add( tuple );
        this.factSize++;
    }

    public boolean add(final ReteTuple tuple,
                       final boolean checkExists) {
        throw new UnsupportedOperationException( "FieldIndexHashTable does not support add(ReteTuple tuple, boolean checkExists)" );
    }

    public ReteTuple remove(final ReteTuple tuple) {
        final int hashCode = this.index.hashCodeOf( tuple );

        final int index = indexOf( hashCode,
                                   this.table.length );

        // search the table for  the Entry, we need to track previous  and next, so if the 
        // Entry is empty after  its had the FactEntry removed, we must remove  it from the table
        FieldIndexEntry previous = (FieldIndexEntry) this.table[index];
        FieldIndexEntry current = previous;
        while ( current != null ) {
            final FieldIndexEntry next = (FieldIndexEntry) current.next;
            if ( current.matches( tuple,
                                  hashCode ) ) {
                final ReteTuple old = current.remove( tuple );
                this.factSize--;
                // If the FactEntryIndex is empty, then remove it from the hash table
                if ( current.first == null ) {
                    if ( previous == current ) {
                        this.table[index] = next;
                    } else {
                        previous.next = next;
                    }
                    current.next = null;
                    this.size--;
                }
                return old;
            }
            previous = current;
            current = next;
        }
        return null;
    }

    public boolean contains(final ReteTuple tuple) {
        final int hashCode = this.index.hashCodeOf( tuple );

        final int index = indexOf( hashCode,
                                   this.table.length );

        FieldIndexEntry current = (FieldIndexEntry) this.table[index];
        while ( current != null ) {
            if ( current.matches( tuple,
                                  hashCode ) ) {
                return true;
            }
            current = (FieldIndexEntry) current.next;
        }
        return false;
    }

    public FieldIndexEntry get(final InternalFactHandle handle) {
        final Object object = handle.getObject();
        final int hashCode = this.index.hashCodeOf( handle.getObject() );

        final int index = indexOf( hashCode,
                                   this.table.length );
        FieldIndexEntry entry = (FieldIndexEntry) this.table[index];

        while ( entry != null ) {
            if ( entry.matches( object,
                                hashCode ) ) {
                return entry;
            }
            entry = (FieldIndexEntry) entry.getNext();
        }

        return entry;
    }

    /**
     * We use this method to aviod to table lookups for the same hashcode; which is what we would have to do if we did
     * a get and then a create if the value is null.
     * 
     * @param value
     * @return
     */
    private FieldIndexEntry getOrCreate(final ReteTuple tuple) {
        final int hashCode = this.index.hashCodeOf( tuple );

        final int index = indexOf( hashCode,
                                   this.table.length );
        FieldIndexEntry entry = (FieldIndexEntry) this.table[index];

        while ( entry != null ) {
            if ( entry.matches( tuple,
                                hashCode ) ) {
                return entry;
            }
            entry = (FieldIndexEntry) entry.next;
        }

        if ( entry == null ) {
            entry = new FieldIndexEntry( this.index,
                                         hashCode );
            entry.next = this.table[index];
            this.table[index] = entry;

            if ( this.size++ >= this.threshold ) {
                resize( 2 * this.table.length );
            }
        }
        return entry;
    }

    public int size() {
        return this.factSize;
    }

    public static class FieldIndexEntry
        implements
        Entry {

        private static final long serialVersionUID = 8160842495541574574L;
        private Entry             next;
        private ReteTuple         first;
        private final int         hashCode;
        private Index             index;

        public FieldIndexEntry(final Index index,
                               final int hashCode) {
            this.index = index;
            this.hashCode = hashCode;
        }

        public Entry getNext() {
            return this.next;
        }

        public void setNext(final Entry next) {
            this.next = next;
        }

        public ReteTuple getFirst() {
            return this.first;
        }

        public void add(final ReteTuple tuple) {
            tuple.setNext( this.first );
            this.first = tuple;
        }

        public ReteTuple get(final ReteTuple tuple) {
            ReteTuple current = this.first;
            while ( current != null ) {
                if ( tuple.equals( current ) ) {
                    return current;
                }
                current = (ReteTuple) current.getNext();
            }
            return null;
        }

        public ReteTuple remove(final ReteTuple tuple) {
            ReteTuple previous = this.first;
            ReteTuple current = previous;
            while ( current != null ) {
                final ReteTuple next = (ReteTuple) current.getNext();
                if ( tuple.equals( current ) ) {
                    if ( this.first == current ) {
                        this.first = next;
                    } else {
                        previous.setNext( next );
                    }
                    current.setNext( null );
                    return current;
                }
                previous = current;
                current = next;
            }
            return current;
        }

        public boolean matches(final Object object,
                               final int objectHashCode) {
            return this.hashCode == objectHashCode && this.index.equal( object,
                                                                        this.first );
        }

        public boolean matches(final ReteTuple tuple,
                               final int tupleHashCode) {
            return this.hashCode == tupleHashCode && this.index.equal( this.first,
                                                                       tuple );
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(final Object object) {
            final FieldIndexEntry other = (FieldIndexEntry) object;
            return this.hashCode == other.hashCode && this.index == other.index;
        }
    }

}