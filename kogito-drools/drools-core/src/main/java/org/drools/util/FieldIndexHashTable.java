/**
 * 
 */
package org.drools.util;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ObjectHashTable;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.FieldExtractor;
import org.drools.spi.Tuple;

public class FieldIndexHashTable extends AbstractHashTable
    implements
    ObjectHashTable {
    private int                         fieldIndex;

    private FieldExtractor              extractor;

    private Declaration                 declaration;

    private final int                   PRIME = 31;

    private final int                   startResult;

    private FieldIndexHashTableIterator tupleValueIterator;

    private HashCodeHashTableIterator   hashCodeValueIterator;

    public FieldIndexHashTable(FieldExtractor extractor,
                               Declaration declaration) {
        this( 16,
              0.75f,
              extractor,
              declaration );
    }

    public FieldIndexHashTable(int capacity,
                               float loadFactor,
                               FieldExtractor extractor,
                               Declaration declaration) {
        super( capacity,
               loadFactor );
        this.fieldIndex = extractor.getIndex();

        this.extractor = extractor;

        this.declaration = declaration;

        this.startResult = PRIME + this.fieldIndex;
    }

    public Iterator iterator() {
        throw new UnsupportedOperationException( "FieldIndexHashTable does not support  iterator()" );
    }

    public Iterator iterator(ReteTuple tuple) {
        if ( this.tupleValueIterator == null ) {
            this.tupleValueIterator = new FieldIndexHashTableIterator();
        }
        this.tupleValueIterator.reset( get( tuple ) );
        return this.iterator();
    }

    public Iterator iterator(int hashCode) {
        if ( this.hashCodeValueIterator == null ) {
            this.hashCodeValueIterator = new HashCodeHashTableIterator( this );
        }

        this.hashCodeValueIterator.reset( hashCode );
        return this.iterator();
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
            this.entry = this.entry.getNext();
            return this.entry;
        }

        /* (non-Javadoc)
         * @see org.drools.util.Iterator#reset()
         */
        public void reset(Entry entry) {
            this.entry = entry;
        }

        public void reset(int hashCode) {
            throw new UnsupportedOperationException( "FieldIndexHashTableIterator does not support reset(int hashCode)" );
        }
    }

    /**
     * Fast re-usable iterator
     *
     */
    public static class HashCodeHashTableIterator
        implements
        Iterator {
        private AbstractHashTable hashTable;
        private FactEntry         current;
        private FactEntry         next;
        private FieldIndexEntry   index;

        public HashCodeHashTableIterator(AbstractHashTable hashTable) {
            this.hashTable = hashTable;
        }

        /* (non-Javadoc)
         * @see org.drools.util.Iterator#next()
         */
        public Entry next() {
            this.current = (FactEntry) this.next;
            if ( current == null ) {
                this.index = (FieldIndexEntry) this.index.getNext();
                if ( this.index == null ) {
                    return null;
                }
                this.current = this.index.first;
            }
            this.next = (FactEntry) current.getNext();
            return current;
        }

        /* (non-Javadoc)
         * @see org.drools.util.Iterator#reset()
         */
        public void reset(int hashCode) {
            this.index = (FieldIndexEntry) this.hashTable.getBucket( hashCode );
            this.current = null;
            this.next = (FactEntry) index.getFirst();
        }

        public void reset(Entry entry) {
            throw new UnsupportedOperationException( "FieldIndexHashTableIterator does not support reset(Entry entry)" );
        }
    }

    public boolean add(InternalFactHandle handle) {
        FieldIndexEntry entry = getOrCreate( this.extractor.getValue( handle.getObject() ) );
        entry.add( handle );
        return true;
    }

    public boolean add(InternalFactHandle handle,
                       boolean checkExists) {
        throw new UnsupportedOperationException( "FieldIndexHashTable does not support add(InternalFactHandle handle, boolean checkExists)" );
    }

    public boolean remove(InternalFactHandle handle) {
        Object value = this.extractor.getValue( handle.getObject() );
        int hashCode = PRIME * startResult + ((value == null) ? 0 : value.hashCode());

        int index = indexOf( hashCode,
                             table.length );

        // search the table for  the Entry, we need to track previous  and next, so if the 
        // Entry is empty after  its had the FactEntry removed, we must remove  it from the table
        FieldIndexEntry previous = (FieldIndexEntry) this.table[index];
        FieldIndexEntry current = previous;
        while ( current != null ) {
            FieldIndexEntry next = (FieldIndexEntry) current.next;
            if ( hashCode == current.hashCode && value.equals( current.getValue() ) ) {
                current.remove( handle );
                // If the FactEntryIndex is empty, then remove it from the hash map
                if ( current.first == null ) {
                    if ( previous == current ) {
                        this.table[index] = next;
                        previous.next = next;
                    }
                    current.next = null;
                    this.size--;
                }
                return true;
            }
            previous = current;
            current = next;
        }
        return false;
    }

    public boolean contains(InternalFactHandle handle) {
        Object value = this.extractor.getValue( handle.getObject() );
        int hashCode = PRIME * startResult + ((value == null) ? 0 : value.hashCode());

        int index = indexOf( hashCode,
                             table.length );

        FieldIndexEntry current = (FieldIndexEntry) this.table[index];
        while ( current != null ) {
            if ( hashCode == current.hashCode && value.equals( current.getValue() ) ) {
                return  true;
            }
            current = (FieldIndexEntry) current.next;
        }
        return false;
    }
    
    public FieldIndexEntry get(ReteTuple tuple) {
        Object value = this.declaration.getValue( tuple.get( this.declaration ).getObject() );
        int hashCode = PRIME * startResult + ((value == null) ? 0 : value.hashCode());

        int index = indexOf( hashCode,
                             table.length );
        FieldIndexEntry entry = (FieldIndexEntry) this.table[index];

        while ( entry != null ) {
            if ( hashCode == entry.hashCode && value.equals( entry.getValue() ) ) {
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
    public FieldIndexEntry getOrCreate(Object value) {
        int hashCode = PRIME * startResult + ((value == null) ? 0 : value.hashCode());
        int index = indexOf( hashCode,
                             table.length );
        FieldIndexEntry entry = (FieldIndexEntry) this.table[index];

        while ( entry != null ) {
            if ( hashCode == entry.hashCode && value.equals( entry.getValue() ) ) {
                return entry;
            }
            entry = (FieldIndexEntry) entry.next;
        }

        if ( entry == null ) {
            entry = new FieldIndexEntry( this.extractor,
                                         fieldIndex,
                                         hashCode );
            entry.next = this.table[index];
            this.table[index] = entry;

            if ( this.size++ >= this.threshold ) {
                resize( 2 * this.table.length );
            }
        }
        return entry;
    }

    public static class FieldIndexEntry
        implements
        Entry {
        private Entry                next;
        private FactEntry            first;
        private final int            hashCode;
        private final int            fieldIndex;
        private final FieldExtractor extractor;

        public FieldIndexEntry(FieldExtractor extractor,
                               int fieldIndex,
                               int hashCode) {
            this.extractor = extractor;
            this.fieldIndex = fieldIndex;
            this.hashCode = hashCode;
        }

        public Entry getNext() {
            return next;
        }

        public void setNext(Entry next) {
            this.next = next;
        }

        public FactEntry getFirst() {
            return this.first;
        }

        public void add(InternalFactHandle handle) {
            FactEntry entry = new FactEntry( handle );
            entry.next = this.first;
            this.first = entry;
        }

        public FactEntry get(InternalFactHandle handle) {
            long id = handle.getId();
            FactEntry current = first;
            while ( current != null ) {
                if ( current.handle.getId() == id ) {
                    return current;
                }
                current = (FactEntry) current.next;
            }
            return null;
        }

        public FactEntry remove(InternalFactHandle handle) {
            long id = handle.getId();

            FactEntry previous = this.first;
            FactEntry current = previous;
            while ( current != null ) {
                FactEntry next = (FactEntry) current.next;
                if ( current.handle.getId() == id ) {
                    if ( this.first == current ) {
                        this.first = next;
                    } else {
                        previous.next = next;
                    }
                    current.next = null;
                    return current;
                }
                previous = current;
                current = next;
            }
            return current;
        }

        public String toString() {
            return this.extractor.toString();
        }

        public int fieldIndex() {
            return this.fieldIndex;
        }

        public Object getValue() {
            if ( this.first == null ) {
                return null;
            }
            return this.extractor.getValue( this.first.getFactHandle().getObject() );
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object object) {
            FieldIndexEntry other = (FieldIndexEntry) object;
            return this.hashCode == other.hashCode && this.fieldIndex == other.fieldIndex;
        }
    }
}