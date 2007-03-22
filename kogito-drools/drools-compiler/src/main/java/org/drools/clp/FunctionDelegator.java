package org.drools.clp;

public class FunctionDelegator implements Function {    
    private Function function;
    private String name;
    
    public FunctionDelegator( String name ) {
        this.name = name;
    }

    public FunctionDelegator(Function function) {
        this.function = function;
    }
    
    public void setFunction(Function function) {
        this.function = function;
    }

    public Object execute(ValueHandler[] args,
                          ExecutionContext context) {
        return function.execute( args,
                                 context );
    }

    public String getName() {
        return this.function == null ? this.name : function.getName();
    }

}
