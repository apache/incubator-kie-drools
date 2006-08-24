/**
 * 
 */
package org.drools.base.resolvers;

import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.spi.Tuple;

public class MapValue
    implements
    ValueHandler {

    private final KeyValuePair[] pairs;

    public MapValue(final KeyValuePair[] pairs) {
        this.pairs = pairs;
    }

    public Object getValue(final Tuple tuple,
                           final WorkingMemory wm) {
        final Map map = new HashMap();

        for ( int i = 0, length = this.pairs.length; i < length; i++ ) {
            final ValueHandler key = this.pairs[i].getKey();
            final ValueHandler value = this.pairs[i].getValue();
            map.put( key.getValue( tuple,
                                   wm ),
                     value.getValue( tuple,
                                     wm ) );
        }

        return map;
    }

    static class KeyValuePair {
        private ValueHandler key;
        private ValueHandler value;

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

    }
}