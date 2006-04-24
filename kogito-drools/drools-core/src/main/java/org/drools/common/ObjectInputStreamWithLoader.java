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

    public ObjectInputStreamWithLoader(InputStream in,
                                       ClassLoader classLoader) throws IOException {
        super( in );
        this.classLoader = classLoader;
        enableResolveObject( true );
    }

    protected Class resolveClass(ObjectStreamClass desc) throws IOException,
                                                        ClassNotFoundException {
        if ( this.classLoader == null ) {
            return super.resolveClass( desc );
        } else {
            String name = desc.getName();
            return this.classLoader.loadClass( name );
        }
    }
}