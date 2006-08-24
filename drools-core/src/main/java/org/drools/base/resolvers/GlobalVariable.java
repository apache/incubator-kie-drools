/**
 * 
 */
package org.drools.base.resolvers;

import org.drools.WorkingMemory;
import org.drools.spi.Tuple;

public class GlobalVariable
    implements
    ValueHandler {
    public String globalName;

    public GlobalVariable(final String name) {
        this.globalName = name;
    }

    public Object getValue(final Tuple tuple,
                           final WorkingMemory wm) {
        return wm.getGlobal( this.globalName );

    }
}