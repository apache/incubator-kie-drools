package org.drools.rule;

import java.io.Serializable;
import java.util.HashMap;

import org.mvel.ast.Function;
import org.mvel.integration.VariableResolver;
import org.mvel.integration.impl.MapVariableResolverFactory;

public class MVELDialectData implements DialectData, Serializable {
    private MapFunctionResolverFactory functionFactory;
    
    /**
     * Default constructor - for Externalizable. This should never be used by a user, as it
     * will result in an invalid state for the instance.
     */
    public MVELDialectData() {

    }
    
    public MVELDialectData(final DialectDatas datas) {
        this.functionFactory = new MapFunctionResolverFactory( );
    }
    
    public MapFunctionResolverFactory getFunctionFactory() {
        return this.functionFactory;
    }
    
    public void removeRule(Package pkg,
                           Rule rule) {
    }
    
    public void addFunction(Function function) {
        this.functionFactory.addFunction( function );
    }
    
    public void removeFunction(Package pkg,
                               org.drools.rule.Function function) {
        this.functionFactory.removeFunction( function.getName() );
        
    }    

    public boolean isDirty() {
        return false;
    }

    public void merge(DialectData newData) {        
    }

    public void reload() {
    }
    
    public static class MapFunctionResolverFactory extends MapVariableResolverFactory {
        
        public MapFunctionResolverFactory() {
            super(new HashMap<String, Object>() );
        }
        
        
        public void addFunction(Function function) {
            this.variables.put( function.getName(), function );
        }
        
        public void removeFunction(String functionName) {
            this.variables.remove( functionName );
            this.variableResolvers.remove( functionName );
        }
        
        public VariableResolver createVariable(String name,
                                               Object value) {
            throw new RuntimeException( "variable is a read-only function pointer" );
        }
        
        public VariableResolver createIndexedVariable(int index,
                                                      String name,
                                                      Object value,
                                                      Class< ? > type) {
            throw new RuntimeException( "variable is a read-only function pointer" );
        }
    }
}
