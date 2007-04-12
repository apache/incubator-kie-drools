package org.drools.base.mvel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.Tuple;
import org.mvel.integration.VariableResolver;
import org.mvel.integration.VariableResolverFactory;

public class DroolsMVELFactory
    implements
    VariableResolverFactory {
    private Tuple         tuple;
    private Object        object;
    private Map           localDeclarations;
    private Map           previousDeclarations;
    private Map           globals;

    private Map           resolvers;
    //private
    private WorkingMemory workingMemory;

    public DroolsMVELFactory() {
        this.resolvers = Collections.EMPTY_MAP;
    }

    public DroolsMVELFactory(final Map previousDeclarations,
                             final Map localDeclarations,
                             final Map globals) {

    }

    public void setPreviousDeclarationMap(final Map declarations) {
        this.previousDeclarations = declarations;
    }

    public void setLocalDeclarationMap(final Map declarations) {
        this.localDeclarations = declarations;
    }

    public void setGlobalsMap(final Map globals) {
        this.globals = globals;
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

    public VariableResolver createVariable(final String name,
                                           final Object value) {
        throw new UnsupportedOperationException( "Variables cannot be created here" );
    }

    public VariableResolverFactory getNextFactory() {
        return null;
    }

    public VariableResolverFactory setNextFactory(final VariableResolverFactory resolverFactory) {
        throw new UnsupportedOperationException( "Chained factories are not support for DroolsMVELFactory" );
    }

    public VariableResolver getVariableResolver(final String name) {
        return (VariableResolver) this.resolvers.get( name );
    }

    public boolean isResolveable(final String name) {
        //return this.declarations.containsKey( name ) || this.globals.containsKey( name );
        if ( this.resolvers == Collections.EMPTY_MAP ) {
            this.resolvers = new HashMap();
        }

        VariableResolver resolver = (VariableResolver) this.resolvers.get( name );

        if ( resolver != null ) {
            return true;
        }

        if ( this.previousDeclarations != null && this.previousDeclarations.containsKey( name ) ) {
            resolver = new DroolsMVELPreviousDeclarationVariable( (Declaration) this.previousDeclarations.get( name ),
                                                                  this );
        } else if ( this.localDeclarations != null && this.localDeclarations.containsKey( name ) ) {
            resolver = new DroolsMVELLocalDeclarationVariable( (Declaration) this.localDeclarations.get( name ),
                                                               this );
        } else {
            resolver = new DroolsMVELGlobalVariable( name,
                                                     (Class) this.globals.get( name ),
                                                     this );
        }

        if ( resolver != null ) {
            this.resolvers.put( name,
                                resolver );
            return true;
        } else {
            return false;
        }
    }

    public boolean isTarget(final String name) {
        return this.resolvers.containsKey( name );
    }

    //    public ValueHandler createExternalVariable(String identifier) {        
    //        registerExternalVariable( identifier );
    //        ValueHandler variable;
    //        if ( this.declarations.containsKey( identifier )) {
    //            variable = new DroolsMVELDeclarationVariable( (Declaration) this.declarations.get( identifier ), this );
    //        } else {
    //            variable = new DroolsMVELGlobalVariable( identifier, (Class) this.globals.get( identifier ), this );
    //        }
    //        return variable;
    //    	return null;
    //    }
    //
    //    public boolean isValidVariable(String identifier) {        
    //        return this.declarations.containsKey( identifier );
    //    }   
    //    
    //    public Declaration[] getRequiredDeclarations()  {
    //        List list = new ArrayList();
    //        for (int i  = 0, length  = this.requiredVariables.length; i < length; i++) {
    //            list.add( this.declarations.get( this.requiredVariables[i] ) );
    //        }
    //        return (Declaration[]) list.toArray( new Declaration[list.size()  ]  );
    //    }
}
