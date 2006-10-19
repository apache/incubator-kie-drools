/**
 * 
 */
package org.drools.base.resolvers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.spi.Tuple;

public class MapValue
    implements
    ValueHandler {

    private static final long    serialVersionUID = 320L;

    private final KeyValuePair[] pairs;

    private Object               cachedValue      = ValueHandler.EMPTY;

    public MapValue(final KeyValuePair[] pairs) {
        this.pairs = pairs;
    }

    public Object getValue(final Tuple tuple,
                           final WorkingMemory wm) {
        if ( this.cachedValue == ValueHandler.EMPTY ) {
            final Map map = new HashMap();

            for ( int i = 0, length = this.pairs.length; i < length; i++ ) {
                final ValueHandler key = this.pairs[i].getKey();
                final ValueHandler value = this.pairs[i].getValue();
                map.put( key.getValue( tuple,
                                       wm ),
                         value.getValue( tuple,
                                         wm ) );
            }
            this.cachedValue = map;
        }

        return this.cachedValue;
    }

    public Class getExtractToClass() {
        return Map.class;
    }

    public void reset() {
        this.cachedValue = ValueHandler.EMPTY;
    }

    public static class KeyValuePair
        implements
        Serializable {
        private static final long serialVersionUID = 320L;

        private ValueHandler      key;
        private ValueHandler      value;

        public KeyValuePair(final ValueHandler key,
                            final ValueHandler value) {
            this.key = key;
            this.value = value;
        }

        /**
         * 
         * @return The ValueHandler for the key
         */
        public ValueHandler getKey() {
            return this.key;
        }

        /**
         * 
         * @return The ValueHandler for the value
         */
        public ValueHandler getValue() {
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

        public boolean equals(final Object object) {
            if ( this == object ) {
                return true;
            }

            if ( object == null || getClass() != object.getClass() ) {
                return false;
            }
            final KeyValuePair other = (KeyValuePair) object;
            return this.key.equals( other.key ) && this.value.equals( other.value );
        }
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append( "MapValue pairs=" );
        for ( int i = 0, length = this.pairs.length; i < length; i++ ) {
            buffer.append( this.pairs[i] );
        }
        buffer.append( "]" );

        return buffer.toString();
    }

}