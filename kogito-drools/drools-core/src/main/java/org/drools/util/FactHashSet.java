/**
 * 
 */
package org.drools.util;

import org.drools.common.InternalFactHandle;

public class FactHashSet extends AbstractHashTable {
    public FactHashSet() {
        this( 16,
              0.75f );
    }

    public FactHashSet(int capacity,
                         float loadFactor) {
        super( capacity,
               loadFactor );
    }    

    public boolean add(InternalFactHandle handle, boolean checkExists) {
        int hashCode =  handle.hashCode();
        int index = indexOf( hashCode,
                             table.length );

        // scan the linked entries to see if it exists
        if ( checkExists ) {
            FactEntry current = (FactEntry) this.table[index];
            while ( current != null ) {
                if ( hashCode == current.hashCode && handle.getId() == current.handle.getId() )  {
                    return false;
                }
            }
        }

        // We aren't checking the key exists, or it didn't find the key
        FactEntry entry = new FactEntry( handle,
                                             hashCode );
        entry.next = this.table[index];
        this.table[index] = entry;

        if ( this.size++ >= this.threshold ) {
            resize( 2 * this.table.length );
        }
        return true;
    }

    public boolean contains(InternalFactHandle handle) {
        int hashCode = handle.hashCode();
        int index = indexOf( hashCode,
                             table.length );

        FactEntry current = (FactEntry) this.table[index];
        while ( current != null ) {
            if ( hashCode == current.hashCode && handle.getId() == current.handle.getId() ) {
                return false;
            }
            current = (FactEntry) current.getNext();
        }
        return true;
    }

    public boolean remove(InternalFactHandle handle) {
        int hashCode = handle.hashCode();
        int index = indexOf( hashCode,
                             table.length );

        FactEntry previous = (FactEntry) this.table[index];
        FactEntry current = previous;
        while ( current != null ) {
            FactEntry next = (FactEntry) current.getNext();
            if ( hashCode == current.hashCode && handle.getId() == current.handle.getId() ) {
                if ( previous == current ) {
                    this.table[index] = next;
                } else {
                    previous.setNext( next );
                }
                current.setNext( null );
                this.size--;
                return true;
            }
            previous = current;
            current = next;
        }
        return false;
    }
    
    public Entry getBucket(int hashCode) {
        int h = hashCode;
        h += ~(h << 9);
        h ^= (h >>> 14);
        h += (h << 4);
        h ^= (h >>> 10);
        
        return this.table[ indexOf( h, table.length ) ];
    }    
    
    public FactEntry[] getTable()  {
        return (FactEntry[]) this.table;
    }

    public int hash(Object key) {
        return key.hashCode();
    }

    protected int indexOf(int hashCode,
                          int dataSize) {
        return hashCode & (dataSize - 1);
    }    

    public static class FactEntry
        implements
        Entry {
        private InternalFactHandle handle;

        private int    hashCode;

        private Entry next;

        public FactEntry(InternalFactHandle handle,
                           int hashCode) {
            this.handle = handle;
            this.hashCode = hashCode;
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
            return this.hashCode;
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
}