package org.drools.clp;

import org.drools.clp.valuehandlers.FunctionCaller;

public interface BuildContext {

    public FunctionRegistry getFunctionRegistry();

    public void addFunction(FunctionCaller function);

    public Object setProperty(Object key,
                              Object value);

    public Object getProperty(Object key);

    public ValueHandler createLocalVariable(String identifier);

    public void addVariable(VariableValueHandler var);

    public ValueHandler getVariableValueHandler(String identifier);

}