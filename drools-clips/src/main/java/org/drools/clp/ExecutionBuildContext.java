package org.drools.clp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.clp.valuehandlers.FunctionCaller;
import org.drools.clp.valuehandlers.IndexedLocalVariableValue;
import org.drools.clp.valuehandlers.TempTokenVariable;

public class ExecutionBuildContext
    implements
    BuildContext {
    private Map              vars       = new HashMap();
    private Map              properties = Collections.EMPTY_MAP;
    private ExecutionEngine  engine;
    private FunctionRegistry registry;

    public ExecutionBuildContext(ExecutionEngine engine,
                                 FunctionRegistry registry) {
        this.engine = engine;
        this.registry = registry;
    }

    /* (non-Javadoc)
     * @see org.drools.clp.BuildContext#getFunctionRegistry()
     */
    public FunctionRegistry getFunctionRegistry() {
        return this.registry;
    }

    /* (non-Javadoc)
     * @see org.drools.clp.BuildContext#addFunction(org.drools.clp.valuehandlers.FunctionCaller)
     */
    public void addFunction(FunctionCaller function) {
        this.engine.addFunction( function );
    }

    /* (non-Javadoc)
     * @see org.drools.clp.BuildContext#setProperty(java.lang.Object, java.lang.Object)
     */
    public Object setProperty(Object key,
                              Object value) {
        if ( this.properties == Collections.EMPTY_MAP ) {
            this.properties = new HashMap();
        }
        return this.properties.put( key,
                                    value );
    }

    /* (non-Javadoc)
     * @see org.drools.clp.BuildContext#getProperty(java.lang.Object)
     */
    public Object getProperty(Object key) {
        return this.properties.get( key );
    }

    /* (non-Javadoc)
     * @see org.drools.clp.BuildContext#createLocalVariable(java.lang.String)
     */
    public ValueHandler createLocalVariable(String identifier) {
        ValueHandler var = (ValueHandler) this.vars.get( identifier );
        if ( var == null ) {
            var = this.engine.createLocalVariable( identifier );
            this.vars.put( identifier,
                           var );
        }
        return var;
    }
    
    public void addVariable(VariableValueHandler var) {
        this.vars.put( var.getIdentifier(), var);
    }

    /* (non-Javadoc)
     * @see org.drools.clp.BuildContext#getVariableValueHandler(java.lang.String)
     */
    public ValueHandler getVariableValueHandler(String identifier) {
        ValueHandler var = (ValueHandler) this.vars.get( identifier );
        if ( var == null ) {
            var = new TempTokenVariable( identifier );
        }
        return var;
    }

}
