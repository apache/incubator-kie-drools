package org.drools.clp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.base.ValueType;
import org.drools.rule.Declaration;
import org.drools.spi.Tuple;
import org.mvel.integration.VariableResolver;

public class CLPFactory {
    private Tuple         tuple;
    private Object        object;
    private Map           localDeclarations;
    private Map           previousDeclarations;
    private Map           globals;

    private Map           resolvers;
    //private
    private WorkingMemory workingMemory;

    public CLPFactory() {
        this.resolvers = Collections.EMPTY_MAP;
    }

    public CLPFactory(Map previousDeclarations,
                      Map localDeclarations,
                      Map globals) {

    }

    public void setPreviousDeclarationMap(Map declarations) {
        this.previousDeclarations = declarations;
    }

    public void setLocalDeclarationMap(Map declarations) {
        this.localDeclarations = declarations;
    }

    public void setGlobalsMap(Map globals) {
        this.globals = globals;
    }

    public Object getObject() {
        return this.object;
    }

    public void setContext(Tuple tuple,
                           Object object,
                           WorkingMemory workingMemory) {
        this.tuple = tuple;
        this.object = object;
        this.workingMemory = workingMemory;
    }

    public Object getValue(Declaration declaration) {
        return tuple.get( declaration ).getObject();
    }

    public Object getValue(String identifier) {
        return this.workingMemory.getGlobal( identifier );
    }

    public VariableResolver createVariable(String name,
                                           Object value) {
        throw new UnsupportedOperationException( "Variables cannot be created here" );
    }

    //    public ValueHandler getVariableResolver(String name) {
    //        return (ValueHandler) this.resolvers.get( name );
    //    }

    public ValueHandler getVariableResolver(String name) {
        //return this.declarations.containsKey( name ) || this.globals.containsKey( name );
        if ( this.resolvers == Collections.EMPTY_MAP ) {
            this.resolvers = new HashMap();
        }

        ValueHandler resolver = (ValueHandler) this.resolvers.get( name );

        if ( resolver != null ) {
            return null;
        }

        if ( this.previousDeclarations != null && this.previousDeclarations.containsKey( name ) ) {
            resolver = new CLPPreviousDeclarationVariable( (Declaration) this.previousDeclarations.get( name ) );
        } else if ( this.localDeclarations != null && this.localDeclarations.containsKey( name ) ) {
            resolver = new CLPLocalDeclarationVariable( (Declaration) this.localDeclarations.get( name ) );
        } else {
            Class clazz = (Class) this.globals.get( name );
            resolver = new CLPGlobalVariable( name,
                                              clazz,
                                              ValueType.determineValueType( clazz ).getSimpleType(),
                                              this );
        }

        if ( resolver != null ) {
            this.resolvers.put( name,
                                resolver );
            return resolver;
        } else {
            return null;
        }
    }

    //    public boolean isTarget(String name) {
    //        return this.resolvers.containsKey( name );
    //    }

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
