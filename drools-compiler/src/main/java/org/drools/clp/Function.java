package org.drools.clp;


public interface Function { //extends ValueHandler {
    public Object execute(ValueHandler[] args, ExecutionContext context);    
    
    public String getName();    
    
    public LispList createList(int index);
    
    //public void addParameter(ValueHandler valueHandler);
    
    //public ValueHandler[] getParameters();                
}
