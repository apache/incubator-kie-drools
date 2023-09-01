package org.kie.memorycompiler.resources;

import org.drools.util.PortablePath;

/**
 * A Store is where the compilers are storing the results
 */
public interface ResourceStore {

    void write(PortablePath resourcePath, byte[] pResourceData );
    default void write( String resourceName, byte[] pResourceData ) {
        write( PortablePath.of(resourceName), pResourceData );
    }

    void write(PortablePath resourcePath, byte[] pResourceData, boolean createFolder );
    default void write( String resourceName, byte[] pResourceData, boolean createFolder ) {
        write( PortablePath.of(resourceName), pResourceData, createFolder );
    }

    byte[] read( PortablePath resourcePath );
    default byte[] read( String resourceName ) {
        return read( PortablePath.of(resourceName) );
    }

    void remove( PortablePath resourcePath );
    default void remove( String resourceName ) {
        remove( PortablePath.of(resourceName) );
    }
}
