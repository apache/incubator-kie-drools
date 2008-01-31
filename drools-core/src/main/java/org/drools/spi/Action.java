package org.drools.spi;

import org.drools.WorkingMemory;

public interface Action {
    
    public Object createContext();
    
    public void execute(final WorkingMemory workingMemory, final Object actionContext ) throws Exception ;
}
