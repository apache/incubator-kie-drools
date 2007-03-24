package org.drools.clp;

public class LispForm implements LispList {    
    private ExecutionBuildContext context;
    private FunctionCaller caller;

    public LispForm(ExecutionBuildContext context) {
        this.context = context;
    }
    
    public LispForm() {
    }
    
    public ValueHandler getValueHandler() {
        return this.caller;
    }

    public void add(ValueHandler valueHandler) {
        // we know this is a string literal, so can use null for the context
        if ( this.caller == null ) {
            this.caller = new FunctionCaller( this.context.getFunctionRegistry().getFunction( valueHandler.getStringValue( null ) ) );
            this.caller.getFunction().initCallback( context );
        } else {
            int length;
            if ( this.caller == null || this.caller.getParameters() == null ) {
                length = 0;
            } else {
                length = ( this.caller == null ) ? 0 : this.caller.getParameters().length;
            }
            valueHandler = this.caller.getFunction().addParameterCallback( length, valueHandler, context );
            this.caller.addParameter( valueHandler );
            
        }
    }

    public LispList createList() {
        int length;
        if ( this.caller == null || this.caller.getParameters() == null ) {
            length = 0;
        } else {
            length = ( this.caller == null ) ? 0 : this.caller.getParameters().length;
        }
        
        LispList list = this.caller.createList( length );
        
        list.setContext( this.context );
        
        return list; 
    }

    public void setContext(ExecutionBuildContext context) {
        this.context = context;
    }
        
}
