/**
 * 
 */
package org.drools.util;

import org.drools.common.InternalFactHandle;
import org.drools.spi.FieldExtractor;

public class FieldIndexHashTable extends AbstractHashTable {
    private int            fieldIndex;
    private FieldExtractor extractor;

    private final int      PRIME = 31;

    private final int      startResult;

    public FieldIndexHashTable(int fieldIndex,
                               FieldExtractor extractor) {
        this( 16,
              0.75f,
              fieldIndex,
              extractor );
    }

    public FieldIndexHashTable(int capacity,
                               float loadFactor,
                               int fieldIndex,
                               FieldExtractor extractor) {
        super( capacity,
               loadFactor );
        this.fieldIndex = fieldIndex;
        this.extractor = extractor;

        this.startResult = PRIME + this.fieldIndex;
    }

    public void add(InternalFactHandle handle) {
        FieldIndexEntry entry = getOrCreate( this.extractor.getValue( handle.getObject() ) );
        entry.add( handle );
    }

    public void remove(InternalFactHandle handle) {
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
                return;
            }
            previous = current;
            current = next;
        }
    }

    public FieldIndexEntry get(Object value) {
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

    private static class FactEntry
        implements
        Entry {
        private InternalFactHandle handle;

        private Entry              next;

        public FactEntry(InternalFactHandle handle) {
            this.handle = handle;
        }

        public InternalFactHandle getFactHandle() {
            return handle;
        }

        public Entry getNext() {
            return this.next;
        }

        public void setNext(Entry next) {
            this.next = next;
        }

        public int hashCode() {
            return this.handle.hashCode();
        }

        public boolean equals(Object object) {
            if ( object == this ) {
                return true;
            }

            // assumes we never have null or wrong class
            FactEntry other = (FactEntry) object;
            return this.handle.equals( other.handle );
        }
    }

    public static class FieldIndexEntry
        implements
        Entry {
        private Entry                next;
        FactEntry                    first;
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
            entry.next =  this.first;
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
                        previous.next =   next;
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