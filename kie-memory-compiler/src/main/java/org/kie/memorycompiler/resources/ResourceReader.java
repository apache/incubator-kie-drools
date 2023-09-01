package org.kie.memorycompiler.resources;

import java.util.Collection;

import org.drools.util.PortablePath;

/**
 * A ResourceReader provide access to resource like e.g. source code
 */
public interface ResourceReader {

    boolean isAvailable( PortablePath resourcePath );
    default boolean isAvailable( String resourceName ) {
        return isAvailable(PortablePath.of(resourceName));
    }

    byte[] getBytes( final PortablePath resourcePath );
    default byte[] getBytes( String resourceName ) {
        return getBytes(PortablePath.of(resourceName));
    }

    Collection<PortablePath> getFilePaths();

    void mark();
    Collection<String> getModifiedResourcesSinceLastMark();
}
