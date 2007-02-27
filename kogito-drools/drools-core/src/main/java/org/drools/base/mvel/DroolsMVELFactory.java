package org.drools.base.mvel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.Tuple;
import org.mvel.integration.VariableResolver;
import org.mvel.integration.VariableResolverFactory;

public class DroolsMVELFactory implements VariableResolverFactory {    
    private Tuple tuple;
    private Map declarations;
    private Map globals;
    
    private Map resolvers;
    //private
    private WorkingMemory workingMemory;
    
    public DroolsMVELFactory() {
    	this.resolvers = Collections.EMPTY_MAP;
    }
    
    public void setDeclarationMap(Map declarations) {
        this.declarations = declarations;
    }
    
    public void setGlobalsMap(Map globals) {
        this.globals = globals;
    }
    
    public void setContext(Tuple tuple, WorkingMemory workingMemory) {
        this.tuple = tuple;
        this.workingMemory = workingMemory;
    }
    
    public Object getValue(Declaration declaration) {
        return tuple.get( declaration ).getObject();
    }
    
    public Object getValue(String identifier) {
        return this.workingMemory.getGlobal( identifier );
    }

	public VariableResolver createVariable(String name, Object value) {
		throw new UnsupportedOperationException( "Variables cannot be created here" );
	}

	public VariableResolverFactory getNextFactory() {
		return null;
	}

	public VariableResolverFactory setNextFactory(VariableResolverFactory resolverFactory) {
		throw new UnsupportedOperationException( "Chained factories are not support for DroolsMVELFactory" );
	}    	
	
	public VariableResolver getVariableResolver(String name) {	 	  
		return ( VariableResolver ) this.resolvers.get( name );
	}

	public boolean isResolveable(String name) {
		 //return this.declarations.containsKey( name ) || this.globals.containsKey( name );
		  if ( this.resolvers == Collections.EMPTY_MAP) {
			  this.resolvers = new HashMap();
		  }
		  
		  VariableResolver resolver = (VariableResolver) this.resolvers.get( name );
		  
		  if ( resolver != null )  {
			  return true;
		  }
		  
	      if ( this.declarations.containsKey( name )) {
	    	  resolver = new DroolsMVELDeclarationVariable( (Declaration) this.declarations.get( name ), this );
	      } else {
	    	  resolver = new DroolsMVELGlobalVariable( name, (Class) this.globals.get( name ), this );
	      }
	      
	      if ( resolver != null ) {
	    	  this.resolvers.put( name,  resolver );
	    	  return true;
	      } else {
	    	  return false;
	      }	      	
	}

	public boolean isTarget(String name) {
		return this.resolvers.containsKey( name  );
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
