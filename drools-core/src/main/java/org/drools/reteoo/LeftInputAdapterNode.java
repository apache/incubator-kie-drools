package org.drools.reteoo;

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

    public int getId()
    {
        return id;
    }
}
