/**
 * 
 */
package org.drools.util;

public class DroolsMap {
    static final int  MAX_CAPACITY = 1 << 30;

    protected int     size;
    protected int     threshold;
    protected float   loadFactor;
    
    protected boolean checkExists;

    protected Entry[] table;

    public DroolsMap() {
        this( 16,
              0.75f );
    }

    public DroolsMap(int capacity,
                     float loadFactor) {
        this.loadFactor = loadFactor;
        this.threshold = (int) (capacity * loadFactor);
        this.table = new Entry[capacity];
    }

    protected void resize(int newCapacity) {
        Entry[] oldTable = this.table;
        int oldCapacity = oldTable.length;
        if ( oldCapacity == MAX_CAPACITY ) {
            this.threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];

        for ( int i = 0; i < this.table.length; i++ ) {
            Entry entry = this.table[i];
            if ( entry == null ) {
                continue;
            }
            this.table[i] = null;
            Entry next = null;
            while ( entry != null ) {
                next = entry.getNext();

                int index = indexOf( entry.hashCode(), newTable.length  );
                entry.setNext( newTable[index] );
                newTable[index] = entry;

                entry = next;
            }
        }

        this.table = newTable;
        this.threshold = (int) (newCapacity * this.loadFactor);
    }

    public void add(Entry entry) {
        int index = indexOf( entry.hashCode(), table.length  );

        
        boolean exists = false;
        
        // scan the linked entries to see if it exists
        if ( !checkExists ) {
            Entry current = this.table[index];
            int hashCode = entry.hashCode();
            while ( current != null ) {                
                if  ( hashCode == current.hashCode() && entry.equals( current ) ) {
                    exists = true;
                }
            }                        
        }
        
        if( exists == false ) {
            entry.setNext( this.table[index] );
            this.table[index] = entry;
    
            if ( this.size++ >= this.threshold ) {
                resize( 2 * this.table.length );
            }
        }

    }

    public Entry get(Entry entry) {
        int index = indexOf( entry.hashCode(), table.length  );
        Entry current = this.table[index];
        while ( current != null ) {
            if ( entry.hashCode() == current.hashCode() && entry.equals( current ) ) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }

    public Entry remove(Entry entry) {
        int index = indexOf( entry.hashCode(), table.length  );
        Entry previous = this.table[index];        
        Entry current = previous;
        int hashCode = entry.hashCode();
        while ( current != null ) {
            Entry next = current.getNext();
            if ( hashCode == current.hashCode() && entry.equals( current ) ) {
                if( previous  == current ) {
                    this.table[index] = next;
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
        int index = indexOf( hashCode, table.length );
        return this.table[index];
    }

    public int size() {
        return this.size;
    }
    
    protected int indexOf(int hashCode, int length) {
        int index = hashCode % length;
        if ( index < 0 ) {
            index  = index * -1;
        }
        return index;
    }
}