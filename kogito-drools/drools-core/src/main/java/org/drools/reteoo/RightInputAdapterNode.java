package org.drools.reteoo;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.spi.PropagationContext;

public class RightInputAdapterNode extends ObjectSource
    implements
    TupleSink
{
    private final TupleSource tupleSource;
    
    private final int          column;

    public RightInputAdapterNode(int id,
                                 int column,
                                 TupleSource source)
    {

        super( id  );
        this.column = column;
        this.tupleSource = source;
    }    

    public void assertTuple(ReteTuple tuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) throws FactException
    {        
        Object object = ( Object ) tuple.get( this.column );
        FactHandleImpl handle = ( FactHandleImpl ) tuple.getKey().get( this.column );
                        
        propagateAssertObject(object, handle, context, workingMemory)  ;  
    }

    public void retractTuples(TupleKey key,
                              PropagationContext context,
                              WorkingMemoryImpl workingMemory) throws FactException
    {
        FactHandleImpl handle = ( FactHandleImpl ) key.get( this.column );
        
        propagateRetractObject(handle, context, workingMemory);        
    }    

    public void attach()
    {
        this.tupleSource.addTupleSink( this );
    }
    
    public void updateNewNode( WorkingMemoryImpl workingMemory,
                               PropagationContext context ) throws FactException
    {
        this.attachingNewNode = true;
        // We need to detach and re-attach to make sure the node is at the top
        // for the propagation
        this.tupleSource.removeTupleSink( this );
        this.tupleSource.addTupleSink( this );            
        this.tupleSource.updateNewNode( workingMemory, context );          
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
