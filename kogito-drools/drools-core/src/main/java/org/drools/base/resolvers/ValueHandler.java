/**
 * 
 */
package org.drools.base.resolvers;

import org.drools.WorkingMemory;
import org.drools.spi.Tuple;

public interface ValueHandler {

    Object getValue(Tuple tuple,
                    WorkingMemory wm);
}