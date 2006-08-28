/**
 * 
 */
package org.drools.base.resolvers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.WorkingMemory;
import org.drools.spi.Tuple;

public class ListValue
    implements
    ValueHandler {
    
    private static final long serialVersionUID = 320L;

    private final List list;
    
    private Object cachedValue = ValueHandler.EMPTY;    

    public ListValue(final List list) {
        this.list = list;
    }

    public Object getValue(final Tuple tuple,
                           final WorkingMemory wm) {
        if ( this.cachedValue == ValueHandler.EMPTY ) {        
            final List resolvedList = new ArrayList( this.list.size() );
    
            for ( final Iterator it = this.list.iterator(); it.hasNext(); ) {
                resolvedList.add( ((ValueHandler) it.next()).getValue( tuple,
                                                                       wm ) );
            }
            
            this.cachedValue = resolvedList;
        }
        return this.cachedValue;
    }
    
    public void reset() {
        this.cachedValue = ValueHandler.EMPTY;
    }
    
    public String toString() {
        return "[ListValue list=" + this.list + "]";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.list.hashCode();
        return result;
    }

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }
        
        if ( object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final ListValue other = (ListValue) object;
        return this.list.equals( other.list );
    }
    
    
}