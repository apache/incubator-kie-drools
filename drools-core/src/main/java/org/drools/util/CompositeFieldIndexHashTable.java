/**
 * 
 */
package org.drools.util;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ObjectHashTable;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.FieldExtractor;
import org.drools.util.AbstractHashTable.ObjectComparator;
import org.drools.util.ObjectHashMap.ObjectEntry;

public class CompositeFieldIndexHashTable extends AbstractHashTable
    implements
    ObjectHashTable {
    public static final int                   PRIME = 31;

    private int                         startResult;

    private FieldIndexHashTableIterator tupleValueIterator;

    private int                         factSize;

    private DefaultCompositeIndex       index;

    public CompositeFieldIndexHashTable(FieldIndex[] index) {
        this( 16,
              0.75f,
              index );
    }

    public CompositeFieldIndexHashTable(int capacity,
                                        float loadFactor,
                                        FieldIndex[] index) {
        super( capacity,
               loadFactor );

        this.startResult = PRIME;
        for ( int i = 0, length = index.length; i < length; i++ ) {
            this.startResult += index[i].getExtractor().getIndex();
        }
        this.index = new DefaultCompositeIndex( index,
                                                this.startResult,
                                                this.comparator );
    }

    public Iterator iterator() {
        throw new UnsupportedOperationException( "FieldIndexHashTable does not support  iterator()" );
    }

    public Iterator iterator(ReteTuple tuple) {
        if ( this.tupleValueIterator == null ) {
            this.tupleValueIterator = new FieldIndexHashTableIterator();
        }
        FieldIndexEntry entry = (FieldIndexEntry) get( tuple );
        this.tupleValueIterator.reset( (entry != null) ? entry.first : null );
        return this.tupleValueIterator;
    }

    public boolean isIndexed() {
        return true;
    }

    public Entry getBucket(Object object) {
        int hashCode = this.index.hashCodeOf( object );
        int index = indexOf( hashCode,
                             table.length );

        return (ObjectEntry) this.table[index];
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
            Entry current = this.entry;
            this.entry = (this.entry != null) ? this.entry.getNext() : null;
            return current;
        }

        /* (non-Javadoc)
         * @see org.drools.util.Iterator#reset()
         */
        public void reset(Entry entry) {
            this.entry = entry;
        }
    }

    public boolean add(InternalFactHandle handle) {
        FieldIndexEntry entry = getOrCreate( handle.getObject() );
        entry.add( handle );
        this.factSize++;
        return true;
    }

    public boolean add(InternalFactHandle handle,
                       boolean checkExists) {
        throw new UnsupportedOperationException( "FieldIndexHashTable does not support add(InternalFactHandle handle, boolean checkExists)" );
    }

    public boolean remove(InternalFactHandle handle) {
        Object object = handle.getObject();
        int hashCode = this.index.hashCodeOf( object );

        int index = indexOf( hashCode,
                             table.length );

        // search the table for  the Entry, we need to track previous  and next, so if the 
        // Entry is empty after  its had the FactEntry removed, we must remove  it from the table
        FieldIndexEntry previous = (FieldIndexEntry) this.table[index];
        FieldIndexEntry current = previous;
        while ( current != null ) {
            FieldIndexEntry next = (FieldIndexEntry) current.next;
            if ( current.matches( object,
                                  hashCode ) ) {
                current.remove( handle );
                this.factSize--;
                // If the FactEntryIndex is empty, then remove it from the hash map
                if ( current.first == null ) {
                    if ( previous == current ) {
                        this.table[index] = next;
                    } else {
                        previous.next  = next;
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
        Object object = handle.getObject();
        int hashCode = this.index.hashCodeOf( object );

        int index = indexOf( hashCode,
                             table.length );

        FieldIndexEntry current = (FieldIndexEntry) this.table[index];
        while ( current != null ) {
            if ( current.matches( object,
                                  hashCode ) ) {
                return true;
            }
            current = (FieldIndexEntry) current.next;
        }
        return false;
    }

    public FieldIndexEntry get(ReteTuple tuple) {
        int hashCode = this.index.hashCodeOf( tuple );

        int index = indexOf( hashCode,
                             table.length );
        FieldIndexEntry entry = (FieldIndexEntry) this.table[index];

        while ( entry != null ) {
            if ( entry.matches( tuple,
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
    private FieldIndexEntry getOrCreate(Object object) {
        int hashCode = this.index.hashCodeOf( object );
        int index = indexOf( hashCode,
                             table.length );
        FieldIndexEntry entry = (FieldIndexEntry) this.table[index];

        while ( entry != null ) {
            if ( entry.matches( object,
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
        private Entry          next;
        private FactEntry      first;
        private final int      hashCode;
        private CompositeIndex index;

        public FieldIndexEntry(CompositeIndex index,
                               int hashCode) {
            this.index = index;
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

        public boolean matches(Object object,
                               int objectHashCode) {
            return this.hashCode == objectHashCode && this.index.equal( this.first.getFactHandle().getObject(),
                                                                        object );
        }

        public boolean matches(ReteTuple tuple,
                               int tupleHashCode) {
            return this.hashCode == tupleHashCode && this.index.equal( this.first.getFactHandle().getObject(),
                                                                       tuple );
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object object) {
            FieldIndexEntry other = (FieldIndexEntry) object;
            return this.hashCode == other.hashCode && this.index == other.index;
        }
    }

    private static interface CompositeIndex {
        public int hashCodeOf(ReteTuple tuple);

        public int hashCodeOf(Object object);

        public boolean equal(Object object1,
                             Object object2);

        public boolean equal(Object object1,
                             ReteTuple tuple);
    }

    private static class DefaultCompositeIndex
        implements
        CompositeIndex {
        private FieldIndex[]          indexes;
        private int              startResult;
        private ObjectComparator comparator;

        public DefaultCompositeIndex(FieldIndex[] indexes,
                                     int startResult,
                                     ObjectComparator comparator) {
            this.startResult = startResult;
            this.indexes = indexes;
            this.comparator = comparator;
        }

        public int hashCodeOf(Object object) {
            int hashCode = startResult;
            for ( int i = 0, length = this.indexes.length; i < length; i++ ) {
                Object value = indexes[i].getExtractor().getValue( object );
                hashCode += CompositeFieldIndexHashTable.PRIME * hashCode + ((value == null) ? 0 : value.hashCode() );
            }
            return this.comparator.rehash( hashCode );
        }

        public int hashCodeOf(ReteTuple tuple) {
            int hashCode = startResult;
            for ( int i = 0, length = this.indexes.length; i < length; i++ ) {
                Object value = indexes[i].declaration.getValue( tuple.get( indexes[i].declaration ).getObject() );
                hashCode += CompositeFieldIndexHashTable.PRIME * hashCode + ((value == null) ? 0 : value.hashCode() );
            }
            return this.comparator.rehash( hashCode );
        }

        public boolean equal(Object object1,
                             ReteTuple tuple) {
            for ( int i = 0, length = this.indexes.length; i < length; i++ ) {
                Object value1 = indexes[i].extractor.getValue( object1 );
                Object value2 = indexes[i].declaration.getValue( tuple.get( indexes[i].declaration ).getObject() );
                if ( !this.comparator.equal( value1,
                                             value2 ) ) {
                    return false;
                }
            }
            return true;
        }

        public boolean equal(Object object1,
                             Object object2) {
            for ( int i = 0, length = this.indexes.length; i < length; i++ ) {
                Object value1 = indexes[i].getExtractor().getValue( object1 );
                Object value2 = indexes[i].getExtractor().getValue( object2 );
                if ( !this.comparator.equal( value1,
                                             value2 ) ) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class FieldIndex {
        private FieldExtractor extractor;
        private Declaration    declaration;

        public FieldIndex(FieldExtractor extractor,
                     Declaration declaration) {
            super();
            this.extractor = extractor;
            this.declaration = declaration;
        }

        public Declaration getDeclaration() {
            return declaration;
        }

        public void setDeclaration(Declaration declaration) {
            this.declaration = declaration;
        }

        public FieldExtractor getExtractor() {
            return extractor;
        }

        public void setExtractor(FieldExtractor extractor) {
            this.extractor = extractor;
        }
    }
}