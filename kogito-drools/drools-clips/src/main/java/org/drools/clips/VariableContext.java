package org.drools.clips;

public interface VariableContext {
    public void addVariable(String name, Object value);
    
    public void removeVariable(String name);
}
