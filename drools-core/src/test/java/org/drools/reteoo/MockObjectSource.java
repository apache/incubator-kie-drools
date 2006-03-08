package org.drools.reteoo;

import org.drools.spi.PropagationContext;

public class MockObjectSource extends ObjectSource {
    private int attached;

    private int updated;

    public MockObjectSource(int id) {
        super( id );
    }

    public void attach() {
        this.attached++;

    }

    public int getAttached() {
        return this.attached;
    }

    public int getUdated() {
        return this.updated;
    }


    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) {
        this.updated++;
    }

    public void remove(BaseNode node,
                       WorkingMemoryImpl workingMemory,
                       PropagationContext context) {
        // TODO Auto-generated method stub
        
    }

}
