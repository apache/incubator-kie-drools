package org.drools.clp;

public interface Function { //extends ValueHandler {
    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context);

    public String getName();

    public void initCallback(ExecutionBuildContext context);

    public ValueHandler addParameterCallback(int index,
                                             ValueHandler valueHandler,
                                             ExecutionBuildContext context);

    public LispList createList(int index);
}
