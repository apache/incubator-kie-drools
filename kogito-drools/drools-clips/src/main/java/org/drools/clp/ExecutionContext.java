package org.drools.clp;

import java.io.PrintStream;
import java.util.Map;

import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;

public interface ExecutionContext {

    public void setPrintoutRouters(Map printoutRouters);

    public void addPrintoutRouter(String identifier,
                                           PrintStream stream);

    public PrintStream getPrintoutRouters(String identifier);

    public InternalWorkingMemory getWorkingMemory();

    public Object getObject();
    
    public ReteTuple getTuple(); 

    public ValueHandler getLocalVariable(int index);

    public void setLocalVariable(int index,
                                 ValueHandler valueHandler);

}