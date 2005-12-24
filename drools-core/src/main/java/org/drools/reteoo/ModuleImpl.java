package org.drools.reteoo;

import org.drools.util.PriorityQueue;

import org.drools.spi.ConflictResolver;
import org.drools.spi.Module;

public class ModuleImpl implements Module
{   
    private final String name;

    /** Items in the agenda. */
    private final PriorityQueue     activationQueue;    
        
    public ModuleImpl(String name,
                      ConflictResolver conflictResolver)
    {
        this.name = name;
        this.activationQueue = new PriorityQueue( conflictResolver );    
    }    
    
    public String getName()
    {
        return this.name;
    }
    
    public boolean equal(Object object)
    {
        if ( ( object == null ) || ! ( object instanceof ModuleImpl ) )
        {
            return false;
        }
        
        if ( ( (ModuleImpl) object).name.equals( this.name ) ) 
        {
            return true;
        }
        
        return false;
    }
    
    public int hashcode()
    {
        return this.name.hashCode();
    }

    public PriorityQueue getActivationQueue()
    {
        return this.activationQueue;
    }
    
    public String toString()
    {
        return "Module '" + this.name + "'";
    }

}
