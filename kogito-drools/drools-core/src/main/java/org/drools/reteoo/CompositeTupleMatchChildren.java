package org.drools.reteoo;

import java.util.LinkedList;
import java.util.Iterator;

import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public class CompositeTupleMatchChildren
    implements
    TupleMatchChildren {
    private LinkedList list;

    public CompositeTupleMatchChildren() {
        this.list = new LinkedList();
    }

    public void add(ReteTuple tuple) {
        list.add( tuple );
    }

    public void propagateRetractTuple(final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        for ( Iterator it = this.list.iterator(); it.hasNext(); ) {
            ReteTuple joined = (ReteTuple) it.next();
            joined.retractTuple( context,
                                 workingMemory );
        }
    }

    public void propagateModifyTuple(final PropagationContext context,
                                     final InternalWorkingMemory workingMemory) {
        for ( Iterator it = this.list.iterator(); it.hasNext(); ) {
            ReteTuple joined = (ReteTuple) it.next();
            joined.modifyTuple( context,
                                workingMemory );
        }
    }

    public ReteTuple getTupleForSink(TupleSink sink) {
        for ( Iterator it = this.list.iterator(); it.hasNext(); ) {
            ReteTuple joined = (ReteTuple) it.next();
            if ( sink.equals(  joined.getTupleSink() ) ) {
                return joined;
            }
        }    
        return null;
    }
    
    

}
