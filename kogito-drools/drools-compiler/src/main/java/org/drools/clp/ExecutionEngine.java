package org.drools.clp;

public interface ExecutionEngine {
    public void addFunction(Function function);
    
    public int getNextIndex();
}
