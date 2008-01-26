package org.drools.clp.functions;

import org.drools.clp.BuildContext;
import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm2;
import org.drools.clp.LispList;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.BooleanValueHandler;
import org.drools.clp.valuehandlers.FunctionCaller;
import org.drools.clp.valuehandlers.ListValueHandler;
import org.drools.clp.valuehandlers.LongValueHandler;
import org.drools.clp.valuehandlers.ObjectValueHandler;
import org.drools.clp.valuehandlers.TempTokenVariable;

public class IfFunction extends BaseFunction
    implements
    Function {
    private static final String name = "if";

    public IfFunction() {

    }
    
    public ValueHandler addParameterCallback(int index,
                                             FunctionCaller caller,
                                             ValueHandler valueHandler,
                                             BuildContext context) {
        // We need to determine and store the 'else' location so that we don't have to "seach" for it at runtime
        // we rewrite the conditional function into a list, the first element stores the original
        
        if ( index == 0 ) {
            // we are at the conditional element, rewrite it so it can hold the 'else' position
            ListValueHandler list = new ListValueHandler();
            list.add( valueHandler );
            valueHandler = list;
        } else if ( valueHandler instanceof ObjectValueHandler ) {
            String token = valueHandler.getStringValue( null );
            if ( token.equals( "else" ) ) {
                ((ListValueHandler) caller.getParameters()[0]).add( new LongValueHandler( index ) );
            }
        }
        
        caller.addParameter( valueHandler );

        return valueHandler;
    }    

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {
        ValueHandler[] list = ((ListValueHandler)args[0]).getList();
        
         ValueHandler result = null;      
        
        int elseIndex = -1;
        if ( list.length == 2 ) {
            elseIndex = list[1].getIntValue( context );
        }
        
        if ( list[0].getBooleanValue( context ) ) {
            for ( int i = 2; i < elseIndex; i++ ) {
                result = args[i].getValue( context );
            }
        } else if ( elseIndex != -1) {
            for ( int i = elseIndex+1; i < args.length; i++ ) {
                result = args[i].getValue( context );
            }            
        } else {
            result = new BooleanValueHandler( false );
        }
        
        return result;
    }

    public String getName() {
        return name;
    }
}
