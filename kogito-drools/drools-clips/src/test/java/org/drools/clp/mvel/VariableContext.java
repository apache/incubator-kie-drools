package org.drools.clp.mvel;

public interface VariableContext {
    public void addVariable(String name, Object value);
    
    public void removeVariable(String name);
}
