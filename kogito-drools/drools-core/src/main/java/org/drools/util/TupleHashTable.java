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

    public TupleHashTable(final int capacity,
                          final float loadFactor) {
        super( capacity,
               loadFactor );
    }

    public Object add(final ReteTuple tuple) {
        final int hashCode = tuple.hashCode();
        final int index = indexOf( hashCode,
                             this.table.length );

        tuple.setNext( this.table[index] );
        this.table[index] = tuple;

        if ( this.size++ >= this.threshold ) {
            resize( 2 * this.table.length );
        }
        return null;
    }

    public Object get(final ReteTuple tuple) {
        final int hashCode = tuple.hashCode();
        final int index = indexOf( hashCode,
                             this.table.length );

        ReteTuple current = (ReteTuple) this.table[index];
        while ( current != null ) {
            if ( hashCode == current.hashCode() && tuple.equals( current ) ) {
                return current;
            }
            current = (ReteTuple) current.getNext();
        }
        return null;
    }

    public Object remove(final ReteTuple tuple) {
        final int hashCode = tuple.hashCode();
        final int index = indexOf( hashCode,
                             this.table.length );

        ReteTuple previous = (ReteTuple) this.table[index];
        ReteTuple current = previous;
        while ( current != null ) {
            final ReteTuple next = (ReteTuple) current.getNext();
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

    public Entry getBucket(final Object object) {
        final int hashCode = object.hashCode();
        final int index = indexOf( hashCode,
                             this.table.length );

        return this.table[index];
    }

    public boolean contains(final ReteTuple tuple) {
        return (get( tuple ) != null);
    }
}