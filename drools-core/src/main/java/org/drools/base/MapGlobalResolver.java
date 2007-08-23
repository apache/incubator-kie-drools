package org.drools.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.spi.GlobalResolver;

public class MapGlobalResolver
    implements
    GlobalResolver {
    
    private static final long serialVersionUID = 400L;
    
    private final Map map;
    
    public MapGlobalResolver() {
        this.map = new HashMap();
    }
    
    public MapGlobalResolver(Map map) {
        this.map = map;
    }    

    public Object resolveGlobal(String identifier) {
        return this.map.get( identifier );
    }
    
    public void setGlobal(String identifier, Object value) {
        this.map.put( identifier,
                      value );
    }
    
    public Entry[] getGlobals() {
    	return (Entry[]) this.map.entrySet().toArray(new Entry[this.map.size()]);
    }

}
