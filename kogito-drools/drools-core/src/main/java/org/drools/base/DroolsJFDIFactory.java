package org.drools.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jfdi.interpreter.AbstractValueHandlerFactory;
import org.codehaus.jfdi.interpreter.TypeResolver;
import org.codehaus.jfdi.interpreter.ValueHandler;
import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;

public class DroolsJFDIFactory extends AbstractValueHandlerFactory {    
    private ReteTuple tuple;
    private Map declarations;
    private Map globals;
    //private
    private WorkingMemory workingMemory;
    
    public DroolsJFDIFactory(TypeResolver typeResolver) {
        super( typeResolver );
    }
    
    public void setDeclarationMap(Map declarations) {
        this.declarations = declarations;
    }
    
    public void setGlobalsMap(Map globals) {
        this.globals = globals;
    }
    
    public void setContext(ReteTuple tuple, WorkingMemory workingMemory) {
        this.tuple = tuple;
        this.workingMemory = workingMemory;
    }
    
    public Object getValue(Declaration declaration) {
        return tuple.get( declaration ).getObject();
    }
    
    public Object getValue(String identifier) {
        return this.workingMemory.getGlobal( identifier );
    }    

    public ValueHandler createExternalVariable(String identifier) {        
        registerExternalVariable( identifier );
        ValueHandler variable;
        if ( this.declarations.containsKey( identifier )) {
            variable = new DroolsJFDIDeclarationVariable( (Declaration) this.declarations.get( identifier ), this );
        } else {
            variable = new DroolsJFDIGlobalVariable( identifier, (Class) this.globals.get( identifier ), this );
        }
        return variable;
    }

    public boolean isValidVariable(String identifier) {        
        return this.declarations.containsKey( identifier );
    }   
    
    public Declaration[] getRequiredDeclarations()  {
        List list = new ArrayList();
        for (int i  = 0, length  = this.requiredVariables.length; i < length; i++) {
            list.add( this.declarations.get( this.requiredVariables[i] ) );
        }
        return (Declaration[]) list.toArray( new Declaration[list.size()  ]  );
    }
}
