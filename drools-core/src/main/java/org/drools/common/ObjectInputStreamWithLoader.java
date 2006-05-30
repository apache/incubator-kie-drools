/**
 * 
 */
package org.drools.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ObjectInputStreamWithLoader extends ObjectInputStream {
    private final ClassLoader classLoader;

    public ObjectInputStreamWithLoader(final InputStream in,
                                       final ClassLoader classLoader) throws IOException {
        super( in );
        this.classLoader = classLoader;
        enableResolveObject( true );
    }

    protected Class resolveClass(final ObjectStreamClass desc) throws IOException,
                                                              ClassNotFoundException {
        if ( this.classLoader == null ) {
            return super.resolveClass( desc );
        } else {
            final String name = desc.getName();
            return this.classLoader.loadClass( name );
        }
    }
}