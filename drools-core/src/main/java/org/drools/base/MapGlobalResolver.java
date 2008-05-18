package org.drools.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

import org.drools.spi.GlobalResolver;

public class MapGlobalResolver
    implements
    GlobalResolver {

    private static final long serialVersionUID = 400L;

    private Map map;

    public MapGlobalResolver() {
        this.map = new HashMap();
    }

    public MapGlobalResolver(Map map) {
        this.map = map;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        map = (Map)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(map);
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
    
    public GlobalResolver clone() {
        Map clone = new HashMap();
        
        for ( Entry entry : getGlobals() ) {
            clone.put( entry.getKey(), entry.getValue() );
        }
        return new MapGlobalResolver( clone );
    }

}
