package org.drools.reteoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.spi.PropagationContext;

public class MockTupleSink extends TupleSource
    implements
    TupleSink,
    NodeMemory {
    private List                asserted  = new ArrayList();

    public MockTupleSink() {
        super( 0 );
    }

    public MockTupleSink(int id) {
        super( id );
    }

    public void assertTuple(ReteTuple tuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) {

        if ( workingMemory != null ) {
            Map map = (Map) workingMemory.getNodeMemory( this );
            map.put( tuple.getKey(),
                     tuple );
        }

        this.asserted.add( new Object[]{tuple, context, workingMemory} );

    }

    public List getAsserted() {
        return this.asserted;
    }
    
    public void ruleAttached() {
        // TODO Auto-generated method stub
    }

    public void setHasMemory(boolean hasMemory) {
        this.hasMemory = hasMemory;
    }

    public int getId() {
        return this.id;
    }

    public Object createMemory() {
        return new HashMap();
    }

    public void attach() {
        // TODO Auto-generated method stub

    }

    public void remove() {
        // TODO Auto-generated method stub

    }

    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) {
        // TODO Auto-generated method stub

    }

}
