package org.drools.reteoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.AssertionException;
import org.drools.FactException;
import org.drools.RetractionException;
import org.drools.spi.PropagationContext;

public class MockTupleSink extends TupleSource
    implements
    TupleSink,
    NodeMemory
{
    private List                asserted  = new ArrayList();
    private List                retracted = new ArrayList();
    private AssertionException  assertionException;
    private RetractionException retractionException;    
    
    public MockTupleSink()
    {
        super( 0 );
    }
    
    public MockTupleSink( int id )
    {
        super( id );
    }

    public void assertTuple(ReteTuple tuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) throws AssertionException
    {
        if ( assertionException != null )
        {
            throw this.assertionException;
        }

        if ( workingMemory != null )
        {
            Map map = ( Map ) workingMemory.getNodeMemory( this );
            map.put( tuple.getKey( ), tuple );
        }
        
        this.asserted.add( new Object[]{tuple, context, workingMemory} );

    }

    public void retractTuples(TupleKey key,
                              PropagationContext context,
                              WorkingMemoryImpl workingMemory) throws RetractionException
    {
        if ( retractionException != null )
        {
            throw retractionException;
        }
        
        if ( workingMemory != null )
        {
            Map map = ( Map ) workingMemory.getNodeMemory( this );
            map.remove( key );
        }        

        this.retracted.add( new Object[]{key, context, workingMemory} );

    }

    public List getAsserted()
    {
        return this.asserted;
    }

    public List getRetracted()
    {
        return this.retracted;
    }

    public void setAssertionException(AssertionException assertionException)
    {
        this.assertionException = assertionException;
    }

    public void setRetractionException(RetractionException retractionException)
    {
        this.retractionException = retractionException;
    }

    public void ruleAttached()
    {
        // TODO Auto-generated method stub        
    }
    
    public void setHasMemory(boolean hasMemory)
    {
        this.hasMemory = hasMemory;
    }

    public int getId()
    {
        return this.id;
    }

    public Object createMemory()
    {
        return new HashMap();
    }

    public void attach()
    {
        // TODO Auto-generated method stub
        
    }

    public void remove()
    {
        // TODO Auto-generated method stub
        
    }

    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) throws FactException
    {
        // TODO Auto-generated method stub
        
    }

}
