package org.drools.reteoo;

import java.util.Iterator;
import java.util.Set;

import org.drools.FactException;
import org.drools.spi.PropagationContext;

public class LeftInputAdapterNode extends TupleSource
    implements
    ObjectSink
{
    private final ObjectSource objectSource;
    
    private final int          column;

    public LeftInputAdapterNode(int id,
                                int column,
                                ObjectSource source)
    {
        super( id  );
        this.column = column;
        this.objectSource = source;
    }

    public void attach()
    {
        this.objectSource.addObjectSink( this );
    }

    public void assertObject(Object object,
                             FactHandleImpl handle,
                             PropagationContext context, 
                             WorkingMemoryImpl workingMemory) throws FactException
    {
        ReteTuple tuple = new ReteTuple( this.column ,
                                         handle,
                                         workingMemory );
        propagateAssertTuple( tuple,
                              context,
                              workingMemory );
    }

    public void retractObject(FactHandleImpl handle,
                              PropagationContext context, 
                              WorkingMemoryImpl workingMemory) throws FactException
    {
        TupleKey key = new TupleKey( this.column ,
                                     handle );
        propagateRetractTuples( key,
                                context,
                                workingMemory );
    }
    
    public void updateNewNode( WorkingMemoryImpl workingMemory,
                               PropagationContext context ) throws FactException
    {
        this.attachingNewNode = true;
        // We need to detach and re-attach to make sure the node is at the top
        // for the propagation
        this.objectSource.removeObjectSink( this );
        this.objectSource.addObjectSink( this );            
        this.objectSource.updateNewNode( workingMemory, context );          
        this.attachingNewNode = false;
    }     

    public int getId()
    {
        return id;
    }

    public void remove()
    {
        // TODO Auto-generated method stub
        
    }
}
