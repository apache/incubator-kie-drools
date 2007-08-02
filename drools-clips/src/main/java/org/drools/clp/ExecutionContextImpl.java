package org.drools.clp;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;

public class ExecutionContextImpl implements ExecutionContext {
    private InternalWorkingMemory workingMemory;
    private ReteTuple             tuple;
    private Object                object;
    private ValueHandler[]        localVariables;
    private Map                   printoutRouters;   

    
    public ExecutionContextImpl(InternalWorkingMemory workingMemory,
                            ReteTuple tuple,
                            int localVariableSize) {
        this( workingMemory, tuple, null, localVariableSize);
    }    

    
    public ExecutionContextImpl(InternalWorkingMemory workingMemory,
                            ReteTuple tuple,
                            Object object,
                            int localVariableSize) {
        this.workingMemory = workingMemory;
        this.tuple = tuple;
        this.object = object;
        this.localVariables = new ValueHandler[localVariableSize];
        this.printoutRouters = new HashMap();
        this.printoutRouters.put( "t", System.out );
    }
    
    /* (non-Javadoc)
     * @see org.drools.clp.ExecutionContext#setPrintoutRouters(java.util.Map)
     */
    public void setPrintoutRouters(Map printoutRouters) {
        this.printoutRouters = printoutRouters;
    }
    
    /* (non-Javadoc)
     * @see org.drools.clp.ExecutionContext#addPrintoutRouter(java.lang.String, java.io.PrintStream)
     */
    public void addPrintoutRouter(String identifier, PrintStream stream) {
        this.printoutRouters.put( identifier, stream );
    }
    
    /* (non-Javadoc)
     * @see org.drools.clp.ExecutionContext#getPrintoutRouters(java.lang.String)
     */
    public PrintStream getPrintoutRouters(String identifier) {
        return ( PrintStream ) this.printoutRouters.get( identifier );
    }

    /* (non-Javadoc)
     * @see org.drools.clp.ExecutionContext#getTuple()
     */
    public ReteTuple getTuple() {
        return tuple;
    }

    /* (non-Javadoc)
     * @see org.drools.clp.ExecutionContext#getWorkingMemory()
     */
    public InternalWorkingMemory getWorkingMemory() {
        return workingMemory;
    }

    /* (non-Javadoc)
     * @see org.drools.clp.ExecutionContext#getObject()
     */
    public Object getObject() {
        return this.object;
    }

    /* (non-Javadoc)
     * @see org.drools.clp.ExecutionContext#getLocalVariable(int)
     */
    public ValueHandler getLocalVariable(int index) {
        return localVariables[index];
    }

    /* (non-Javadoc)
     * @see org.drools.clp.ExecutionContext#setLocalVariable(int, org.drools.clp.ValueHandler)
     */
    public void setLocalVariable(int index,
                                 ValueHandler valueHandler) {
        this.localVariables[index] = valueHandler;
    }
}
