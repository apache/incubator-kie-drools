package org.kie.memorycompiler.resources;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.util.PortablePath;

/**
 * A memory based reader to compile from memory
 */
public class MemoryResourceReader implements ResourceReader {
    
    private final Map<PortablePath, byte[]> resources = new ConcurrentHashMap<>();

    private Set<String> modifiedResourcesSinceLastMark;

    public boolean isAvailable( PortablePath path ) {
        return resources.containsKey(path);
    }
    
    public void add( final String resourceName, final byte[] pContent ) {
        PortablePath normalizedName = PortablePath.of(resourceName);
        resources.put(normalizedName, pContent);
        if (modifiedResourcesSinceLastMark != null) {
            modifiedResourcesSinceLastMark.add(normalizedName.asString());
        }
    }
    
    public void remove( PortablePath path ) {
        resources.remove(path);
    }

    public void mark() {
        modifiedResourcesSinceLastMark = new HashSet<>();
    }

    public Collection<String> getModifiedResourcesSinceLastMark() {
        return modifiedResourcesSinceLastMark;
    }

    public byte[] getBytes( PortablePath path ) {
        return resources.get(path);
    }

    public Collection<PortablePath> getFilePaths() {
        return resources.keySet();
    }
}
