package org.drools.clp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExecutionBuildContext {
    private Map              vars       = new HashMap();
    private Map              properties = Collections.EMPTY_MAP;
    private ExecutionEngine  engine;
    private FunctionRegistry registry;

    public ExecutionBuildContext(ExecutionEngine engine,
                                 FunctionRegistry registry) {
        this.engine = engine;
        this.registry = registry;
    }

    public FunctionRegistry getFunctionRegistry() {
        return this.registry;
    }
    
    public void addFunction(FunctionCaller function) {
        this.engine.addFunction( function );
    }    

    public Object setProperty(Object key,
                              Object value) {
        if ( this.properties == Collections.EMPTY_MAP ) {
            this.properties = new HashMap();
        }
        return this.properties.put( key,
                                    value );
    }

    public Object getProperty(Object key) {
        return this.properties.get( key );
    }

    public ValueHandler createLocalVariable(String identifier) {
        ValueHandler var = (ValueHandler) this.vars.get( identifier );
        if ( var == null ) {
            var = new LocalVariableValue( identifier,
                                          this.engine.getNextIndex() );
            this.vars.put( identifier,
                           var );
        }
        return var;
    }

    public ValueHandler getVariableValueHandler(String identifier) {
        ValueHandler var = (ValueHandler) this.vars.get( identifier );
        if ( var == null ) {
            var = new TempTokenVariable( identifier );
        }
        return var;
    }

}
