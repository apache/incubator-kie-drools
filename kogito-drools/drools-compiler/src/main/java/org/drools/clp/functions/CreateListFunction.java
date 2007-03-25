package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.ListValueHandler;

public class CreateListFunction extends BaseFunction
    implements
    Function {
    private static final String name = "create$";

    public CreateListFunction() {

    }

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {
        ListValueHandler list = new ListValueHandler();
        list.add( args,
                  context );
        return list;
    }

    public String getName() {
        return name;
    }
}
