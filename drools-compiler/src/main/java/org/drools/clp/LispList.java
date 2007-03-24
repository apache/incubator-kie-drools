package org.drools.clp;

public interface LispList {        
    public void add(ValueHandler valueHandler);
    
    public LispList createList();
    
    public ValueHandler getValueHandler();
    
    public void setContext(ExecutionBuildContext context);
    
}
