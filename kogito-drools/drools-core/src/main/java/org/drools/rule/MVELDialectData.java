package org.drools.rule;

import java.io.Serializable;
import java.util.HashMap;

import org.mvel.ast.Function;
import org.mvel.integration.VariableResolver;
import org.mvel.integration.impl.MapVariableResolverFactory;

public class MVELDialectData implements DialectData, Serializable {
    private MapFunctionResolverFactory functionFactory;
    
    public MVELDialectData(final DialectDatas datas) {
        this.functionFactory = new MapFunctionResolverFactory( );
    }
    
    public void addFunction(String name, Function function) {
        this.functionFactory.createVariable( name, function );
    }

    public boolean isDirty() {
        return false;
    }

    public void removeFunction(Package pkg,
                               org.drools.rule.Function function) {
        // TODO Auto-generated method stub
        
    }

    public void removeRule(Package pkg,
                           Rule rule) {        
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
