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

    private final List list;

    public ListValue(final List list) {
        this.list = list;
    }

    public Object getValue(final Tuple tuple,
                           final WorkingMemory wm) {
        final List resolvedList = new ArrayList( this.list.size() );

        for ( final Iterator it = this.list.iterator(); it.hasNext(); ) {
            resolvedList.add( ((ValueHandler) it.next()).getValue( tuple,
                                                                   wm ) );
        }

        return resolvedList;
    }
}