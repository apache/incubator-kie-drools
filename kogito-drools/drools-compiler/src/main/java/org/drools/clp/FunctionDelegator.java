package org.drools.clp;

public class FunctionDelegator
    implements
    Function {
    private Function function;
    private String   name;

    public FunctionDelegator(String name) {
        this.name = name;
    }

    public FunctionDelegator(Function function) {
        this.function = function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public ValueHandler addParameterCallback(int index,
                                             ValueHandler valueHandler,
                                             ExecutionBuildContext context) {
        return this.function.addParameterCallback( 0,
                                                   valueHandler,
                                                   context );
    }

    public void initCallback(ExecutionBuildContext context) {
        this.function.initCallback( context );
    }

    public Object execute(ValueHandler[] args,
                          ExecutionContext context) {
        return function.execute( args,
                                 context );
    }

    public String getName() {
        return this.function == null ? this.name : function.getName();
    }

    public LispList createList(int index) {
        return this.function.createList( index );
    }

    public String toString() {
        return "[FunctionDelegate " + this.function + "]";
    }

}
