package org.drools.reteoo;

import java.util.ArrayList;
import java.util.List;

import org.drools.AssertionException;
import org.drools.FactException;
import org.drools.RetractionException;
import org.drools.spi.PropagationContext;

public class MockObjectSink
    implements
    ObjectSink {
    private List                asserted  = new ArrayList();
    private List                retracted = new ArrayList();
    private AssertionException  assertionException;
    private RetractionException retractionException;

    public void assertObject(Object object,
                             FactHandleImpl handle,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) throws FactException{
        if ( this.assertionException != null ) {
            throw this.assertionException;
        }

        this.asserted.add( new Object[]{object, handle, context, workingMemory} );
    }

    public void retractObject(FactHandleImpl handle,
                              PropagationContext context,
                              WorkingMemoryImpl workingMemory) throws FactException{
        if ( this.retractionException != null ) {
            throw this.retractionException;
        }

        this.retracted.add( new Object[]{handle, context, workingMemory} );
    }

    public List getAsserted(){
        return this.asserted;
    }

    public List getRetracted(){
        return this.retracted;
    }

    public void setAssertionException(AssertionException assertionException){
        this.assertionException = assertionException;
    }

    public void setRetractionException(RetractionException retractionException){
        this.retractionException = retractionException;
    }

}
