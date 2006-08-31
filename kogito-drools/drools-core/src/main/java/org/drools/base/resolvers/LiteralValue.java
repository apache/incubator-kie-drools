/**
 * 
 */
package org.drools.base.resolvers;

import java.util.List;

import org.drools.WorkingMemory;
import org.drools.spi.Tuple;

public class LiteralValue
    implements
    ValueHandler {
    
    private static final long serialVersionUID = 320L;

    private final String value;   
    
    private final Class clazz;

    public LiteralValue(final String value, final Class clazz) {
        this.value = value;
        this.clazz = clazz;
    }

    public Object getValue(final Tuple tuple,
                           final WorkingMemory wm) {
        return this.value;
    }
    
    public Class getExtractToClass() {
        return this.clazz;
    }     
    
    public void reset() {
        // N/A
    }    
    
    public String toString() {
        return "LiteralValue value=[" + this.value + "]";
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