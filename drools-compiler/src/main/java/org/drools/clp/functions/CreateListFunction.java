package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.FunctionCaller;
import org.drools.clp.ListValueHandler;
import org.drools.clp.ValueHandler;

public class CreateListFunction extends BaseFunction implements Function {
    private static final String name = "create$";

    public CreateListFunction() {
        
    }
    
    public ValueHandler execute(ValueHandler[] args, ExecutionContext context) {
        ListValueHandler list = new ListValueHandler();
        addElements(list, args, context);
        return list;
    }  
    
    public void addElements(ListValueHandler list, ListValueHandler nested, ExecutionContext context) {
        addElements(list, nested.getList(), context);       
    }
    
    public void addElements(ListValueHandler list, ValueHandler[] args, ExecutionContext context) {
        for ( int i = 0, length = args.length; i < length; i++ ) {
            if ( args[i] instanceof ListValueHandler) {
                addElements(list, ( ListValueHandler ) args[i], context);
            } else if ( args[i] instanceof FunctionCaller && ((FunctionCaller)args[i]).getName().equals( "create$" ) ){
                addElements(list, (ListValueHandler)args[i].getValue( context ), context );
            } else {
                list.add( args[i] );
            }
        }        
    }    
    
    
    public String getName() {
        return name;
    }    
}
