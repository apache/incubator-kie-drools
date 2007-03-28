package org.drools.clp.functions;

import org.drools.clp.ExecutionBuildContext;
import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm;
import org.drools.clp.LispList;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.FunctionCaller;

public abstract class BaseFunction
    implements
    Function {

    public void initCallback(ExecutionBuildContext context) {

    }

    public ValueHandler addParameterCallback(int index,
                                             FunctionCaller caller,
                                             ValueHandler valueHandler,
                                             ExecutionBuildContext context) {
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
