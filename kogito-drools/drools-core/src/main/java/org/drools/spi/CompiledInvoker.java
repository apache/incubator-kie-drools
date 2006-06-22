package org.drools.spi;

import java.util.List;

import org.drools.spi.Invoker;

/**
 * This interface is used by semantic modules that are compiled
 * to bytecode. 
 * 
 * @author Michael Neale
 */
public interface CompiledInvoker
    extends
    Invoker {
    
    /**
     * Generated code should be able to return a List which represents the bytecode.
     * The elements in the list will be used to compare one semantic invoker
     * with another by making sure each item in the list is equivalent (equals()).
     * There are utilities in the ASM package to retrieve the bytecode for this.
     */
    public List getMethodBytecode();
}
