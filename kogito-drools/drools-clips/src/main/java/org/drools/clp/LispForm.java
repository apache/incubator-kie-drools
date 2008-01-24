package org.drools.clp;

import org.drools.clp.valuehandlers.FunctionCaller;

public class LispForm
    implements
    LispList {
    private BuildContext context;
    private FunctionCaller        caller;

    public LispForm(BuildContext context) {
        this.context = context;
    }

    public LispForm() {
    }

    public ValueHandler getValueHandler() {
        return this.caller;
    }

    public void add(ValueHandler valueHandler) {
        if ( this.caller == null ) {
            // we know this is a string literal, so can use null for the context
            this.caller = new FunctionCaller( this.context.getFunctionRegistry().getFunction( valueHandler.getStringValue( null ) ) );

            //We can only call initCallback on known functions
            FunctionDelegator delegator = (FunctionDelegator) this.caller.getFunction();
            if ( delegator.getFunction() != null ) {
                delegator.getFunction().initCallback( context );
            }

        } else {
            // we can only execute callbacks on known functions            
            FunctionDelegator delegator = (FunctionDelegator) this.caller.getFunction();
            if ( delegator.getFunction() != null ) {
                int length;
                if ( this.caller == null || this.caller.getParameters() == null ) {
                    length = 0;
                } else {
                    length = (this.caller == null) ? 0 : this.caller.getParameters().length;
                }
                delegator.getFunction().addParameterCallback( length,
                                                              this.caller,
                                                              valueHandler,
                                                              context );
            }
            //this.caller.addParameter( valueHandler );

        }
    }

    public LispList createList() {
        int length;
        if ( this.caller == null || this.caller.getParameters() == null ) {
            length = 0;
        } else {
            length = (this.caller == null) ? 0 : this.caller.getParameters().length;
        }

        LispList list = this.caller.createList( length );

        list.setContext( this.context );

        return list;
    }

    public void setContext(BuildContext context) {
        this.context = context;
    }

}
