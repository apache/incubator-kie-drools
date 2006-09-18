/**
 * 
 */
package org.drools.util;

public class ObjectHashMap extends AbstractHashTable {
    public ObjectHashMap() {
        this( 16,
              0.75f );
    }

    public ObjectHashMap(int capacity,
                         float loadFactor) {
        super( capacity,
               loadFactor );
    }

    public Object put(Object key,
                      Object value) {
        int hashCode = hash( key );
        int index = indexOf( hashCode,
                             table.length );

        // scan the linked entries to see if it exists
        if ( checkExists ) {
            ObjectEntry current = (ObjectEntry) this.table[index];
            while ( current != null ) {
                if ( hashCode == current.hashCode && comparator.equal( key,
                                                                       current.key ) ) {
                    Object oldValue = current.value;
                    current.value = value;
                    return oldValue;
                }
            }
        }

        // We aren't checking the key exists, or it didn't find the key
        ObjectEntry entry = new ObjectEntry( key,
                                             value,
                                             hashCode );
        entry.next = this.table[index];
        this.table[index] = entry;

        if ( this.size++ >= this.threshold ) {
            resize( 2 * this.table.length );
        }
        return null;
    }

    public Object get(Object key) {
        int hashCode = hash( key );
        int index = indexOf( hashCode,
                             table.length );

        ObjectEntry current = (ObjectEntry) this.table[index];
        while ( current != null ) {
            if ( hashCode == current.hashCode && comparator.equal( key,
                                                                   current.key ) ) {
                return current.value;
            }
            current = (ObjectEntry) current.getNext();
        }
        return null;
    }

    public Object remove(Object key) {
        int hashCode = hash( key );
        int index = indexOf( hashCode,
                             table.length );

        ObjectEntry previous = (ObjectEntry) this.table[index];
        ObjectEntry current = previous;
        while ( current != null ) {
            ObjectEntry next = (ObjectEntry) current.getNext();
            if ( hashCode == current.hashCode && comparator.equal( key,
                                                                   current.key ) ) {
                if ( previous == current ) {
                    this.table[index] = next;
                } else {
                    previous.setNext( next );
                }
                current.setNext( null );
                this.size--;
                return current;
            }
            previous = current;
            current = next;
        }
        return current;
    }
    
    public Entry getBucket(int hashCode) {
        int h = hashCode;
        h += ~(h << 9);
        h ^= (h >>> 14);
        h += (h << 4);
        h ^= (h >>> 10);
        
        return this.table[ indexOf( h, table.length ) ];
    }    

    public int hash(Object key) {
        int h = key.hashCode();
        h += ~(h << 9);
        h ^= (h >>> 14);
        h += (h << 4);
        h ^= (h >>> 10);
        return h;
    }

    protected int indexOf(int hashCode,
                          int dataSize) {
        return hashCode & (dataSize - 1);
    }

    private static class ObjectEntry
        implements
        Entry {
        private Object key;

        private Object value;

        private int    hashCode;

        private Entry next;

        public ObjectEntry(Object key,
                           Object value,
                           int hashCode) {
            this.key = key;
            this.value = value;
            this.hashCode = hashCode;
        }

        public Object getValue() {
            return value;
        }


        public Object getKey() {
            return key;
        }

        public Entry getNext() {
            return this.next;
        }

        public void setNext(Entry next) {
            this.next = next;
        }

        public int hashCode() {
            return this.key.hashCode() ^ this.value.hashCode();
        }

        public boolean equals(Object object) {
            if ( object == this ) {
                return true;
            }

            // assumes we never have null or wrong class

            ObjectEntry other = (ObjectEntry) object;
            return this.key.equals( other.key ) && this.value.equals( other.value );
        }
    }
}