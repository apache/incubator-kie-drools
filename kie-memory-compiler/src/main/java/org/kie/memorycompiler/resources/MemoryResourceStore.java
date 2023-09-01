package org.kie.memorycompiler.resources;

import java.util.HashMap;
import java.util.Map;

import org.drools.util.PortablePath;

public class MemoryResourceStore implements ResourceStore {

    private final Map<PortablePath, byte[]> resources = new HashMap<>();

    @Override
    public void write(PortablePath resourcePath, byte[] pResourceData ) {
        resources.put( resourcePath, pResourceData );
    }

    @Override
    public void write(PortablePath resourcePath, byte[] pResourceData, boolean createFolder ) {
        resources.put( resourcePath, pResourceData );
    }

    @Override
    public byte[] read( PortablePath resourcePath ) {
        return resources.get( resourcePath );
    }

    @Override
    public void remove( PortablePath resourcePath ) {
        resources.remove( resourcePath );
    }

    public Map<PortablePath, byte[]> getResources() {
        return resources;
    }
}
