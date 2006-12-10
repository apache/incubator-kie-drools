/**
 * 
 */
package org.codehaus.jfdi.interpreter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jfdi.interpreter.operations.Expr;

public class AnonMapValue
    implements
    ValueHandler {
    
    private static final long serialVersionUID = 320L;

    private final KeyValuePair[] pairs;
    
    
    private Object cachedValue = null;    

    public AnonMapValue(final KeyValuePair[] pairs) {
        this.pairs = pairs;
    }

    public Object getValue() {
        if ( this.cachedValue == null ) {            
            final Map map = new HashMap();
    
            for ( int i = 0, length = this.pairs.length; i < length; i++ ) {
                final Expr key = this.pairs[i].getKey();
                final Expr value = this.pairs[i].getValue();
                map.put( key.getValue(  ),
                         value.getValue( ) );
            }
            this.cachedValue = map;
        }

        return this.cachedValue;
    }
    
    public Class getExtractToClass() {
        return Map.class;
    }     

    public void reset() {
        this.cachedValue = null;
    }    
    
    public static class KeyValuePair implements Serializable {
        private static final long serialVersionUID = 320L;
        
        private Expr key;
        private Expr value;

        public KeyValuePair(final Expr key,
                            final Expr value) {
            this.key = key;
            this.value = value;
        }

        /**
         * 
         * @return The ValueHandler for the key
         */
        public Expr getKey() {
            return this.key;
        }

        /**
         * 
         * @return The ValueHandler for the value
         */
        public Expr getValue() {
            return this.value;
        }
        
        public String toString() {
            return this.key + "/" + this.value;
        }

        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + this.key.hashCode();
            result = PRIME * result + this.value.hashCode();
            return result;
        }                

        public boolean equals(Object object) {
            if ( this == object ) {
                return true;
            }
            
            if ( object == null ||  getClass() != object.getClass() ) {
                return false;
            }
            final KeyValuePair other = (KeyValuePair) object;
            return this.key.equals( other.key ) && this.value.equals( other.value );
        }                        
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "MapValue pairs=" );
        for ( int i = 0, length = this.pairs.length; i < length; i++ ) {
            buffer.append( this.pairs[i] );            
        }
        buffer.append( "]" );
        
        return buffer.toString();
    }


    public Class getType() {
        return Map.class;
    }

    public boolean isFinal() {        
        return true;
    }

    public boolean isLiteral() {        
        return false;
    }

    public boolean isLocal() {
        return false;
    }

    public void setValue(Object variable) {
        throw new UnsupportedOperationException("You can't change the value of a map (only the contents).");        
    }
    
}