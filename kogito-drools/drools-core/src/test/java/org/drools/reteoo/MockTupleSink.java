package org.drools.reteoo;

import java.util.ArrayList;
import java.util.List;

import org.drools.AssertionException;
import org.drools.RetractionException;
import org.drools.spi.PropagationContext;

public class MockTupleSink
    implements
    TupleSink
{
    private List                asserted  = new ArrayList();
    private List                retracted = new ArrayList();
    private AssertionException  assertionException;
    private RetractionException retractionException;

    public void assertTuple(ReteTuple tuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) throws AssertionException
    {
        if ( assertionException != null )
        {
            throw this.assertionException;
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

}
