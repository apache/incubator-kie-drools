package org.drools.rule;

import java.io.Serializable;
import java.util.HashMap;

import org.mvel.ast.Function;
import org.mvel.integration.impl.MapVariableResolverFactory;

public class MVELDialectData implements DialectData, Serializable {
    private MapVariableResolverFactory functionFactory;
    
    public MVELDialectData(final DialectDatas datas) {
        this.functionFactory = new MapVariableResolverFactory( new HashMap() );
    }
    
    public void addFunction(String name, Function function) {
        this.functionFactory.createVariable( name, function );
    }

    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    public void removeFunction(Package pkg,
                               org.drools.rule.Function function) {
        // TODO Auto-generated method stub
        
    }

    public void removeRule(Package pkg,
                           Rule rule) {
        // TODO Auto-generated method stub
        
    }

    public void merge(DialectData newData) {
        // TODO Auto-generated method stub
        
    }

    public void reload() {
        // TODO Auto-generated method stub
        
    }
}
