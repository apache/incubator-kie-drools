package org.drools.clp;

import org.drools.clp.valuehandlers.FunctionCaller;

public interface BuildContext {

    public abstract FunctionRegistry getFunctionRegistry();

    public abstract void addFunction(FunctionCaller function);

    public abstract Object setProperty(Object key,
                                       Object value);

    public abstract Object getProperty(Object key);

    public abstract ValueHandler createLocalVariable(String identifier);

    public abstract ValueHandler getVariableValueHandler(String identifier);

}