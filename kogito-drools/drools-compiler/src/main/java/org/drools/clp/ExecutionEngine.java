package org.drools.clp;

public interface ExecutionEngine {
    public void addFunction(FunctionCaller function);
    
    public int getNextIndex();
    
    public FunctionCaller[] getFunctions();
}
