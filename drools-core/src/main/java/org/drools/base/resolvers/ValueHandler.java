/**
 * 
 */
package org.drools.base.resolvers;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.spi.Tuple;

public interface ValueHandler
    extends
    Serializable {

    public static final Object EMPTY = new Serializable() {
                                    };

    /**
     * Returns a value resolved. Declarations are resolved from the tuple, globals from the working memory and literals are just returned as is.
     * The returned value is cached until reset() is called.
     * 
     * @param tuple
     * @param wm
     * @return
     */
    public Object getValue(Tuple tuple,
                           WorkingMemory wm);

    /**
     * NULL the cached value
     *
     */
    public void reset();
}