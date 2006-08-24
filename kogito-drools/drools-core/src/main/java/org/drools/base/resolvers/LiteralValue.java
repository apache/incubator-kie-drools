/**
 * 
 */
package org.drools.base.resolvers;

import org.drools.WorkingMemory;
import org.drools.spi.Tuple;

public class LiteralValue
    implements
    ValueHandler {

    private String value;

    public LiteralValue(final String value) {
        this.value = value;
    }

    public Object getValue(final Tuple tuple,
                           final WorkingMemory wm) {
        return this.value;
    }

}