package org.drools.clp.functions;

import java.io.PrintStream;

import org.drools.base.SimpleValueType;
import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.ListValueHandler;

public class PrintoutFunction extends BaseFunction
    implements
    Function {
    private static final String name = "printout";

    public PrintoutFunction() {

    }

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {
        PrintStream route = context.getPrintoutRouters( args[0].getStringValue( context ) );        
        
        if ( route == null ) {
            throw new RuntimeException("printout route '" + args[0].getStringValue( context ) + "' does not exists" );
        }        
        
        for ( int i = 1; i < args.length; i++ ) {
            ValueHandler value = args[i].getValue( context );
            if ( value != null && value.getValueType( context ) == SimpleValueType.LIST ) {
                ValueHandler[] list = ((ListValueHandler)value).getList();
                for ( int j = 0; j < list.length; j++ ) {
                    route.print( list[j].getStringValue( context ) );
                }
            } else {
                route.print( args[i].getStringValue( context ) );
            }
        }
        
        return null;
    }

    public String getName() {
        return name;
    }
}
