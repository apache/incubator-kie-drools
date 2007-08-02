package org.drools.clp.functions;

import org.drools.clp.BuildContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm;
import org.drools.clp.LispList;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.FunctionCaller;

public abstract class BaseFunction
    implements
    Function {

    public void initCallback(BuildContext context) {

    }

    public ValueHandler addParameterCallback(int index,
                                             FunctionCaller caller,
                                             ValueHandler valueHandler,
                                             BuildContext context) {
        caller.addParameter( valueHandler );
        return valueHandler;
    }

    public LispList createList(int index) {
        return new LispForm();
    }

    public String toString() {
        return "[Function '" + getName() + "']";
    }
}
