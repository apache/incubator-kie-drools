package org.drools.clp.mvel;

import java.util.HashMap;
import java.util.Map;

import org.mvel.ast.Function;

public class MVELClipsContext {
    private Map<String, Function> map;

    public MVELClipsContext() {
        this.map = new HashMap<String, Function>();
    }
    public void addFunction(Function function) {
        this.map.put( function.getAbsoluteName(), function );
    }
    
    public boolean removeFunction(String functionName) {
        return ( this.map.remove( functionName ) != null );
    }
    
    public Map<String, Function> getFunctions() {
        return this.map;
    }
}
