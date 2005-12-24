package org.drools.reteoo;

import org.drools.FactException;
import org.drools.spi.PropagationContext;

public class MockTupleSource extends TupleSource
{

    private int attached;
    
    private int updated;    

    public MockTupleSource(int id)
    {
        super( id );
    }

    public void attach()
    {
        this.attached++;

    }

    public int getAttached()
    {
        return this.attached;
    }

    public void remove()
    {
        // TODO Auto-generated method stub
        
    }
    
    public int getUdated()
    {
        return this.updated;
    }    

    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) throws FactException
    {
        updated++;        
    }

}
