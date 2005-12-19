package org.drools.reteoo;

import java.util.HashSet;
import java.util.Set;

import org.drools.FactException;
import org.drools.spi.Constraint;
import org.drools.spi.PropagationContext;

public class AlphaNode extends ObjectSource
    implements
    ObjectSink,
    NodeMemory
{
    private final Constraint constraint;

    private final boolean           storeMemory;

    private final ObjectSource      objectSource;

    AlphaNode(int id,
              Constraint constraint,
              boolean storeMemory,
              ObjectSource objectSource)
    {
        super( id );
        this.constraint = constraint;
        this.storeMemory = storeMemory;
        this.objectSource = objectSource;
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
        if ( this.storeMemory )
        {
            Set memory = (Set) workingMemory.getNodeMemory( this );
            if ( !memory.contains( handle ) )
            {
                if ( constraint.isAllowed( object,
                                           handle,
                                           null) )
                {
                    memory.add( handle );
                    propagateAssertObject( object,
                                           handle,
                                           context,
                                           workingMemory );
                }
            }
        }
        else
        {
            if ( constraint.isAllowed( object,
                                       handle,
                                       null) )
            {
                propagateAssertObject( object,
                                       handle,
                                       context,
                                       workingMemory );
            }
        }
    }

    public void retractObject(FactHandleImpl handle,
                              PropagationContext context, 
                              WorkingMemoryImpl workingMemory) throws FactException
    {          
        if ( this.storeMemory )
        {
            Set memory = (Set) workingMemory.getNodeMemory( this );
            if ( memory.contains( handle ) )
            {
                memory.remove( handle );
                propagateRetractObject( handle,
                                        context,
                                        workingMemory );
            }
        }
        else
        {
            propagateRetractObject( handle,
                                    context,
                                    workingMemory );
        }
    }

    public Object createMemory()
    {
        return new HashSet( );
    }
        
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
     
        if ( object == null || getClass( ) != object.getClass( ) )
        {
            return false;
        }
        
        AlphaNode other = (AlphaNode) object;
        
        return this.objectSource.equals( other.objectSource ) && this.constraint.equals( other.constraint );
    }

    public void remove()
    {
        // TODO Auto-generated method stub
        
    }     
}
