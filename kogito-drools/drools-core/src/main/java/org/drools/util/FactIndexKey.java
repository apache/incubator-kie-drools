/**
 * 
 */
package org.drools.util;

public class FactIndexKey {
    public int    hashCode;
    public int    fieldIndex;
    public Object value;

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(Object object) {
        // Inside the rete network we know that it will always be a FactIndexEntry
        // We know it will never be null
        FieldIndexEntry entry = (FieldIndexEntry) object;
        return this.fieldIndex == entry.fieldIndex && this.value.equals( entry.getValue() );
    }
}