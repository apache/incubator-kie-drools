package org.drools.smf;


public interface SemanticInvokeable extends SemanticRule
{    
    public void setInvoker(Invoker invoker);
    
    public boolean isExceptionThrown();
    
    public String getThrownException();
}
