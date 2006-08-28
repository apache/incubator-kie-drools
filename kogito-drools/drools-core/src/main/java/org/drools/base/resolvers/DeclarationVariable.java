/**
 * 
 */
package org.drools.base.resolvers;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.Tuple;

public class DeclarationVariable
    implements
    ValueHandler {

    private static final long serialVersionUID = 320L;
    
    private Declaration declaration;
    
    private Object cachedValue = ValueHandler.EMPTY;

    public DeclarationVariable(final Declaration dec) {
        this.declaration = dec;
    }

    public Object getValue(final Tuple tuple,
                           final WorkingMemory wm) {
        if ( cachedValue == ValueHandler.EMPTY ) {
            this.cachedValue = tuple.get( this.declaration ).getObject(); 
        }
        return  this.cachedValue;
    }
    
    public void reset() {
        this.cachedValue = ValueHandler.EMPTY;
    }
    
    public String toString() {
        return "[DeclarationVariable " + this.declaration + "]";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.declaration.hashCode();
        return result;
    }

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }
        
        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }
        
        final DeclarationVariable other = (DeclarationVariable) object;
        
        return this.declaration.equals( other.declaration );
    }
    
    
    
    

}