package org.drools.clp;

import java.util.HashMap;
import java.util.Map;

public class ExecutionBuildContext {
    private Map vars = new HashMap();
    private ExecutionEngine engine;
    
    public ExecutionBuildContext(ExecutionEngine engine) {
        this.engine = engine;
    }
    
    public void addFunction(FunctionCaller function) {
        this.engine.addFunction( function );
    }
    
    public ValueHandler createLocalVariable(String identifier) {
        ValueHandler var = ( ValueHandler ) this.vars.get( identifier ) ;
        if (  var == null ) {
            var = new LocalVariableValue(identifier, this.engine.getNextIndex() );
            this.vars.put( identifier, var );
        }
        return var;
    }
    
    public ValueHandler getVariableValueHandler(String identifier) {
        ValueHandler var = ( ValueHandler ) this.vars.get( identifier ) ;
        if (  var == null ) {
            var = new TempTokenVariable(identifier);
        }
        return var;
    }
    
}
