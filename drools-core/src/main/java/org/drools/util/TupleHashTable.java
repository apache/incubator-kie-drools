/**
 * 
 */
package org.drools.util;

import org.drools.reteoo.ReteTuple;

public class TupleHashTable extends AbstractHashTable {
    public TupleHashTable() {
        this( 16,
              0.75f );
    }

    public TupleHashTable(int capacity,
                         float loadFactor) {
        super( capacity,
               loadFactor );
    }

    public Object add(ReteTuple tuple) {
        int hashCode = tuple.hashCode();
        int index = indexOf( hashCode,
                             table.length );

        this.table[index] = tuple;

        if ( this.size++ >= this.threshold ) {
            resize( 2 * this.table.length );
        }
        return null;
    }    

    public Object get(ReteTuple tuple) {
        int hashCode = tuple.hashCode();
        int index = indexOf( hashCode,
                             table.length );

        ReteTuple current = (ReteTuple) this.table[index];
        while ( current != null ) {
            if ( hashCode == current.hashCode() && tuple.equals( current ) ) {
                return current;
            }
            current = (ReteTuple) current.getNext();
        }
        return null;
    }
    
    public boolean contains(ReteTuple tuple) {
        return (get(tuple) != null);
    }

    public Object remove(ReteTuple tuple) {
        int hashCode = tuple.hashCode();
        int index = indexOf( hashCode,
                             table.length );

        ReteTuple previous = (ReteTuple) this.table[index];
        ReteTuple current = previous;
        while ( current != null ) {
            ReteTuple next = (ReteTuple) current.getNext();
            if ( hashCode == current.hashCode() && tuple.equals( current ) ) {
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
        return this.table[ indexOf( hashCode, table.length ) ];
    }    

    protected int indexOf(int hashCode,
                          int dataSize) {
        return hashCode & (dataSize - 1);
    }
}