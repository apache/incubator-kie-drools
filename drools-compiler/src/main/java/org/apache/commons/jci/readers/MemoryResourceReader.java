package org.apache.commons.jci.readers;

import java.util.Map;
import java.util.HashMap;

public class MemoryResourceReader implements ResourceReader {
    
    private Map resources;

    public boolean isAvailable( final String pResourceName ) {
        if (resources == null) {
            return false;
        }

        return resources.containsKey(pResourceName);
    }
    
    public void add( final String pResourceName, final byte[] pContent ) {
        if (resources == null) {
            resources = new HashMap();
        }
        
        resources.put(pResourceName, pContent);
    }
    
    public void remove( final String pResourceName ) {
        if (resources != null) {
            resources.remove(pResourceName);
        }    
    }    
    

    public byte[] getBytes( final String pResourceName ) {
        return (byte[]) resources.get(pResourceName);
    }

    /**
     * @deprecated
     */
    public String[] list() {
        if (resources == null) {
            return new String[0];
        }
        return (String[]) resources.keySet().toArray(new String[resources.size()]);
    }
}
