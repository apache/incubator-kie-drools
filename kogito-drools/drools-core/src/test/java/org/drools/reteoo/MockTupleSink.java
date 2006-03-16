package org.drools.reteoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.drools.FactException;
import org.drools.spi.PropagationContext;

public class MockTupleSink extends TupleSource
    implements
    TupleSink,
    NodeMemory {
    private List asserted  = new ArrayList();
    private List retracted = new ArrayList();

    public MockTupleSink() {
        super( 0 );
    }

    public MockTupleSink(int id) {
        super( id );
    }

    public void assertTuple(ReteTuple tuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) {
        this.asserted.add( new Object[]{tuple, context, workingMemory} );

    }

    public void retractTuple(ReteTuple tuple,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        this.retracted.add( new Object[]{tuple, context, workingMemory} );

    }

    public List getAsserted() {
        return this.asserted;
    }

    public List getRetracted() {
        return this.retracted;
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

    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) throws FactException {
        // TODO Auto-generated method stub

    }

    public void modifyTuple(ReteTuple tuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) {
        // TODO Auto-generated method stub
        
    }

    public void remove(BaseNode node,
                       WorkingMemoryImpl workingMemory,
                       PropagationContext context) {
        // TODO Auto-generated method stub
        
    }

    public void attach(WorkingMemoryImpl[] workingMemories) {
        // TODO Auto-generated method stub
        
    }

}
