package org.drools.base.mvel;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.Tuple;
import org.mvel.CompileException;
import org.mvel.integration.VariableResolver;
import org.mvel.integration.VariableResolverFactory;
import org.mvel.integration.impl.BaseVariableResolverFactory;
import org.mvel.integration.impl.MapVariableResolver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DroolsMVELFactory extends BaseVariableResolverFactory {
    /**
     * Holds the instance of the variables.
     */
    private Map           variables;

    //    public DroolsMVELFactory(Map variables) {
    //        this.variables = variables;
    //    }

    private Tuple         tuple;
    private Object        object;
    private Map           localDeclarations;
    private Map           previousDeclarations;
    private Map           globals;

    //private Map           resolvers;
    //private
    private WorkingMemory workingMemory;

    public DroolsMVELFactory() {
        // this.resolvers = Collections.EMPTY_MAP;
    }

    public DroolsMVELFactory(final Map previousDeclarations,
                             final Map localDeclarations,
                             final Map globals) {
        this.previousDeclarations = previousDeclarations;
        this.localDeclarations = localDeclarations;
        this.globals = globals;
        this.variables = new HashMap();
    }
    
    public Object getObject() {
        return this.object;
    }

    public void setContext(final Tuple tuple,
                           final Object object,
                           final WorkingMemory workingMemory) {
        this.tuple = tuple;
        this.object = object;
        this.workingMemory = workingMemory;
    }

    public Object getValue(final Declaration declaration) {
        return this.tuple.get( declaration ).getObject();
    }

    public Object getValue(final String identifier) {
        return this.workingMemory.getGlobal( identifier );
    }    

    public VariableResolver createVariable(String name,
                                           Object value) {
        VariableResolver vr = getVariableResolver( name );
        if ( vr != null ) {
            vr.setValue( value );
            return vr;
        } else {
            addResolver( name,
                         vr = new MapVariableResolver( variables,
                                                       name ) );
            vr.setValue( value );
            return vr;
        }
    }

    public VariableResolver createVariable(String name,
                                           Object value,
                                           Class type) {
        VariableResolver vr = getVariableResolver( name );
        if ( vr != null && vr.getType() != null ) {
            throw new CompileException( "variable already defined within scope: " + vr.getType() + " " + name );
        } else {
            addResolver( name,
                         vr = new MapVariableResolver( variables,
                                                       name,
                                                       type ) );
            vr.setValue( value );
            return vr;
        }
    }

    public boolean isResolveable(String name) {
        if ( variableResolvers != null && variableResolvers.containsKey( name ) ) {
            return true;
        } else if ( this.previousDeclarations != null && this.previousDeclarations.containsKey( name ) ) {
            addResolver(name, new DroolsMVELPreviousDeclarationVariable( (Declaration) this.previousDeclarations.get( name ),
                                                                  this ) );
            return true;
        } else if ( this.localDeclarations != null && this.localDeclarations.containsKey( name ) ) {
            addResolver(name, new DroolsMVELLocalDeclarationVariable( (Declaration) this.localDeclarations.get( name ),
                                                               this ) );
            return true;
        } else if ( this.globals.containsKey( name ) ) {
            addResolver(name, new DroolsMVELGlobalVariable( name,
                                                     (Class) this.globals.get( name ),
                                                     this ) );
            return true;
        } else if ( variables != null && variables.containsKey( name ) ) {
            addResolver( name,
                         new MapVariableResolver( variables,
                                                  name ) );
            return true;
        } else if ( nextFactory != null ) {
            return nextFactory.isResolveable( name );
        }
        
        return false;
    }

    public void pack() {
        if ( variables != null ) {
            if ( variableResolvers == null ) variableResolvers = new HashMap();
            for ( Iterator it = variables.keySet().iterator(); it.hasNext(); ) {
                String s = (String) it.next();
                //for (String s : variables.keySet()) {
                variableResolvers.put( s,
                                       new MapVariableResolver( variables,
                                                                s ) );
            }
        }
    }

    private void addResolver(String name,
                             VariableResolver vr) {
        if (variableResolvers == null) variableResolvers = new HashMap();
        variableResolvers.put( name,
                               vr );
    }

    public boolean isTarget(String name) {
        return variableResolvers.containsKey( name );
    }
}
