/**
 * 
 */
package org.drools.util;

import org.drools.common.InternalFactHandle;
import org.drools.spi.FieldExtractor;

public class FieldIndexEntry extends BaseEntry {
    FactEntry            first;

    private final int            hashCode;
    final int            fieldIndex;
    private final FieldExtractor extractor;

    public FieldIndexEntry(FieldExtractor extractor,
                          int fieldIndex,
                          int hashCode) {
        this.extractor = extractor;
        this.fieldIndex = fieldIndex;
        this.hashCode = hashCode;
    }
    
    public FactEntry getFirst() {
        return this.first;
    }

    public void add(InternalFactHandle handle) {
        FactEntry entry = new FactEntry( handle );            
        entry.setNext( this.first );
        this.first = entry;
    }

    public FactEntry get(InternalFactHandle handle) {
        long id = handle.getId();
        FactEntry current = first;
        while ( current != null ) {
            if ( current.getFactHandle().getId() == id ) {
                return current;
            }
            current = ( FactEntry ) current.getNext();
        }
        return null;
    }

    public FactEntry remove(InternalFactHandle handle) {
        long id = handle.getId();
        
        FactEntry previous = this.first; 
        FactEntry current = previous;
        while ( current != null ) {
            FactEntry next = ( FactEntry ) current.getNext();
            if ( current.getFactHandle().getId() == id ) {
                this.first = next;
                previous.setNext( next );
                current.setNext( null );
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
        FieldIndexEntry other = ( FieldIndexEntry ) object;
        return this.hashCode == other.hashCode && this.fieldIndex == other.fieldIndex;
    }
}