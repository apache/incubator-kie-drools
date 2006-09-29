/**
 * 
 */
package org.drools.util;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ObjectHashTable;
import org.drools.reteoo.ReteTuple;
import org.drools.spi.Tuple;

public class FactHashTable extends AbstractHashTable implements ObjectHashTable {
    private static final long serialVersionUID = 320L;    

    public FactHashTable() {
        this( 16,
              0.75f );
    }

    public FactHashTable(int capacity,
                         float loadFactor) {
        super( capacity,
               loadFactor );
    }        
    
    public Iterator iterator(int hashCode) {
        throw new UnsupportedOperationException("FactHashTable does not support the method iterator(int hashCode");
    }      
    
    public Iterator iterator(ReteTuple tuple) {
        return iterator();
    }    

    public boolean add(InternalFactHandle handle) {
        return add( handle, true);
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
                current = (FactHashTable.FactEntry) current.getNext();
            }
        }

        // We aren't checking the key exists, or it didn't find the key
        FactEntry entry = new FactEntry( handle,
                                             hashCode );
        entry.next = (FactEntry) this.table[index];
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

    public int hash(Object key) {
        return key.hashCode();
    }

    protected int indexOf(int hashCode,
                          int dataSize) {
        return hashCode & (dataSize - 1);
    }    
}