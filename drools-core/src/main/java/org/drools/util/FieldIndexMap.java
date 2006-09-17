/**
 * 
 */
package org.drools.util;

import org.drools.common.InternalFactHandle;
import org.drools.spi.FieldExtractor;

public class FieldIndexMap extends DroolsMap {
    private int            fieldIndex;
    private FieldExtractor extractor;

    private final int      PRIME = 31;

    private final int      startResult;

    public FieldIndexMap(int fieldIndex,
                         FieldExtractor extractor) {
        this( 16,
              0.75f,
              fieldIndex,
              extractor );
    }

    public FieldIndexMap(int capacity,
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

        int index = indexOf( hashCode, table.length  );
        
        // search the table for  the Entry, we need to track previous  and next, so if the 
        // Entry is empty after  its had the FactEntry removed, we must remove  it from the table
        FieldIndexEntry previous = ( FieldIndexEntry ) this.table[index];        
        FieldIndexEntry current = previous;
        while ( current != null ) {
            FieldIndexEntry next = ( FieldIndexEntry ) current.getNext();
            if ( hashCode == current.hashCode() && value.equals( current.getValue() ) ) {
                current.remove( handle );
                // If the FactEntryIndex is empty, then remove it from the hash map
                if (  current.first ==  null  ) {
                    if( previous  == current ) {
                        this.table[index] = next;
                        previous.setNext( next );
                    }
                    current.setNext( null );
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

        int index = indexOf( hashCode, table.length  );
        FieldIndexEntry entry = (FieldIndexEntry) this.table[index];

        while ( entry != null ) {
            if ( hashCode == entry.hashCode() && value.equals( entry.getValue() ) ) {
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
    private FieldIndexEntry getOrCreate(Object value) {
        int hashCode = PRIME * startResult + ((value == null) ? 0 : value.hashCode());
        int index = indexOf( hashCode, table.length  );
        FieldIndexEntry entry = (FieldIndexEntry) this.table[index];

        while ( entry != null ) {
            if ( hashCode == entry.hashCode() && value.equals( entry.getValue() ) ) {
                return entry;
            }
            entry = (FieldIndexEntry) entry.getNext();
        }        

        if ( entry == null ) {
            entry = new FieldIndexEntry( this.extractor,
                                         fieldIndex,
                                         hashCode );
            entry.setNext( this.table[index] );
            this.table[index] = entry;

            if ( this.size++ >= this.threshold ) {
                resize( 2 * this.table.length );
            }
        }
        return entry;
    }
}