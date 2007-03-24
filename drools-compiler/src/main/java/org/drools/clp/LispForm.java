package org.drools.clp;

public class LispForm implements LispList {    
    private FunctionRegistry registry;
    private ExecutionBuildContext context;
    private FunctionCaller caller;

    public LispForm(ExecutionBuildContext context, FunctionRegistry registry) {
        this.context = context;
        this.registry = registry;
    }
    
    public LispForm() {
    }
    
    public ValueHandler getValueHandler() {
        return this.caller;
    }

    public void add(ValueHandler valueHandler) {
        // we know this is a string literal, so can use null for the context
        if ( this.caller == null ) {
            this.caller = new FunctionCaller( this.registry.getFunction( valueHandler.getStringValue( null ) ) );
        } else {
            this.caller.addParameter( valueHandler );
        }
    }

    public LispList createList() {
        LispList list = this.caller.createList( this.caller.getParameters().length );
        
        list.setContext( this.context );
        list.setRegistry( this.registry );
        
        return list; 
    }

    public void setContext(ExecutionBuildContext context) {
        this.context = context;
    }


    public void setRegistry(FunctionRegistry registry) {
        this.registry = registry;
    }
        
}
