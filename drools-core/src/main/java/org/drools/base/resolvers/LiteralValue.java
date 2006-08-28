/**
 * 
 */
package org.drools.base.resolvers;

import org.drools.WorkingMemory;
import org.drools.spi.Tuple;

public class LiteralValue
    implements
    ValueHandler {
    
    private static final long serialVersionUID = 320L;

    private String value;    

    public LiteralValue(final String value) {
        this.value = value;
    }

    public Object getValue(final Tuple tuple,
                           final WorkingMemory wm) {
        return this.value;
    }
    
    public void reset() {
        // N/A
    }    
    
    public String toString() {
        return "LiteralValue value=" + this.value + "]";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * this.value.hashCode();
        return result;
    }

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }
        if ( object == null || getClass() != object.getClass()) {
            return false;
        }
        final LiteralValue other = (LiteralValue) object;
        return this.value.equals( other.value );
    }
    
    

}