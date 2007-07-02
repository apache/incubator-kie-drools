/**
 * 
 */
package org.drools.util;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.FactHandleMemory;
import org.drools.reteoo.ReteTuple;

public class FactHashTable extends AbstractHashTable
    implements
    FactHandleMemory {
    private static final long serialVersionUID = 320L;

    public FactHashTable() {
        this( 16,
              0.75f );
    }

    public FactHashTable(final int capacity,
                         final float loadFactor) {
        super( capacity,
               loadFactor );
    }

    public Iterator iterator(final ReteTuple tuple) {
        return iterator();
    }

    public boolean add(final InternalFactHandle handle) {
        return add( handle,
                    true );
    }

    public boolean add(final InternalFactHandle handle,
                       final boolean checkExists) {
        final int hashCode = this.comparator.hashCodeOf( handle );
        final int index = indexOf( hashCode,
                                   this.table.length );

        // scan the linked entries to see if it exists
        if ( checkExists ) {
            FactEntryImpl current = (FactEntryImpl) this.table[index];
            while ( current != null ) {
                if ( hashCode == current.hashCode && handle.getId() == current.handle.getId() ) {
                    return false;
                }
                current = (FactHashTable.FactEntryImpl) current.getNext();
            }
        }

        // We aren't checking the key exists, or it didn't find the key
        final FactEntryImpl entry = new FactEntryImpl( handle,
                                               hashCode );
        entry.next = this.table[index];
        this.table[index] = entry;

        if ( this.size++ >= this.threshold ) {
            resize( 2 * this.table.length );
        }
        return true;
    }

    public boolean contains(final InternalFactHandle handle) {
        final int hashCode = this.comparator.hashCodeOf( handle );
        final int index = indexOf( hashCode,
                                   this.table.length );

        FactEntryImpl current = (FactEntryImpl) this.table[index];
        while ( current != null ) {
            if ( hashCode == current.hashCode && handle.getId() == current.handle.getId() ) {
                return true;
            }
            current = (FactEntryImpl) current.getNext();
        }
        return false;
    }

    public boolean remove(final InternalFactHandle handle) {
        final int hashCode = this.comparator.hashCodeOf( handle );
        final int index = indexOf( hashCode,
                                   this.table.length );

        FactEntryImpl previous = (FactEntryImpl) this.table[index];
        FactEntryImpl current = previous;
        while ( current != null ) {
            final FactEntryImpl next = (FactEntryImpl) current.getNext();
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

    public Entry getBucket(final Object object) {
        final int hashCode = this.comparator.hashCodeOf( object );
        final int index = indexOf( hashCode,
                                   this.table.length );

        return this.table[index];
    }

    public boolean isIndexed() {
        return false;
    }
}