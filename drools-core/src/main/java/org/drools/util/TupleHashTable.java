/**
 * 
 */
package org.drools.util;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleMemory;

public class TupleHashTable extends AbstractHashTable
    implements
    LeftTupleMemory {
    public TupleHashTable() {
        this( 16,
              0.75f );
    }

    public TupleHashTable(final int capacity,
                          final float loadFactor) {
        super( capacity,
               loadFactor );
    }

    public Iterator iterator(final InternalFactHandle handle) {
        return iterator();
    }

    public void add(final LeftTuple tuple) {
        final int hashCode = tuple.hashCode();
        final int index = indexOf( hashCode,
                                   this.table.length );

        tuple.setNext( this.table[index] );
        this.table[index] = tuple;

        if ( this.size++ >= this.threshold ) {
            resize( 2 * this.table.length );
        }
    }

    public LeftTuple get(final LeftTuple tuple) {
        final int hashCode = tuple.hashCode();
        final int index = indexOf( hashCode,
                                   this.table.length );

        LeftTuple current = (LeftTuple) this.table[index];
        while ( current != null ) {
            if ( hashCode == current.hashCode() && tuple.equals( current ) ) {
                return current;
            }
            current = (LeftTuple) current.getNext();
        }
        return null;
    }

    public LeftTuple remove(final LeftTuple tuple) {
        final int hashCode = tuple.hashCode();
        final int index = indexOf( hashCode,
                                   this.table.length );

        LeftTuple previous = (LeftTuple) this.table[index];
        LeftTuple current = previous;
        while ( current != null ) {
            final LeftTuple next = (LeftTuple) current.getNext();
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

    public boolean contains(final LeftTuple tuple) {
        return (get( tuple ) != null);
    }

    public boolean isIndexed() {
        return false;
    }
}