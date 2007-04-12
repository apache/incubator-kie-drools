package org.apache.commons.jci.readers;

import java.util.Map;
import java.util.HashMap;

public class MemoryResourceReader
    implements
    ResourceReader {

    private Map resources;

    public boolean isAvailable(final String pResourceName) {
        if ( this.resources == null ) {
            return false;
        }

        return this.resources.containsKey( pResourceName );
    }

    public void add(final String pResourceName,
                    final byte[] pContent) {
        if ( this.resources == null ) {
            this.resources = new HashMap();
        }

        this.resources.put( pResourceName,
                       pContent );
    }

    public void remove(final String pResourceName) {
        if ( this.resources != null ) {
            this.resources.remove( pResourceName );
        }
    }

    public byte[] getBytes(final String pResourceName) {
        return (byte[]) this.resources.get( pResourceName );
    }

    /**
     * @deprecated
     */
    public String[] list() {
        if ( this.resources == null ) {
            return new String[0];
        }
        return (String[]) this.resources.keySet().toArray( new String[this.resources.size()] );
    }
}
